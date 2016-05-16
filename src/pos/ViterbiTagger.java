package pos;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ViterbiTagger {
	private Logger logger = LogManager.getLogger(ViterbiTagger.class);
	
	// <token, <tag, probability>>
	private Map<String, Map<String, Double>> emissionParameters = new HashMap<>();
	private Map<String, Map<String, Double>>  trigramParameters = new HashMap<>();
	private List<File> corpusFiles;
	
	
	public void learnCorpus(String pathString) throws IOException {
		Path path = Paths.get(pathString);
		this.corpusFiles = Files.walk(path)
							.filter(Files::isRegularFile)
							.map(Path::toFile)
							//.filter(file -> file.getName().startsWith("cc")) // only reviews
							.collect(Collectors.toList());
		
		// Store how often each word occurs under each tag
		// <tag, <token, count>>
		Map<String, Map<String,  Integer>> countTokensPerTag = new HashMap<>();
		// <<tag, tag>, <tag, count>>
		Map<String, Map<String, Integer>> ngramCount = new HashMap<>();
		
		// Read all training files and count occurrences
		for (File file : this.corpusFiles) {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			
			String line = reader.readLine();
			while (line != null) {
				String[] tokensWithTages = line.split("\\s");
				List<String> tagList = new ArrayList<>();
				tagList.add("*");
				tagList.add("*");
				
				// Count the occurences
				for (String combination : tokensWithTages) {
					if (combination.equals("")) {
						continue;
					}
					String[] results = combination.split("/");
					String token = results[0];
					String tag = results[1];
				
// Handle emission parameter updates
					if (countTokensPerTag.containsKey(tag)) {
						Map<String, Integer> map = countTokensPerTag.get(tag);
						if (map.containsKey(token)) {
							map.put(token, map.get(token) + 1);
							
						} else {
							map.put(token, 1);
						}
					} else {
						Map<String, Integer> map = new HashMap<>();
						map.put(token, 1);
						countTokensPerTag.put(tag, map);
					}
					
					// Update taglist for the sentence
					tagList.add(tag);
				}
				
				// Entire line read; do something with the tag list!
				for (int i = 2; i < tagList.size(); i++) {
					String tag = tagList.get(i);
					//Pair<String, String> predecessors = new Pair<>(tagList.get(i - 1), tagList.get(i - 2));
					String predecessors = tagList.get(i - 2) + "#" + tagList.get(i - 1);
					if (ngramCount.containsKey(predecessors)) {
						Map<String, Integer> map = ngramCount.get(predecessors);
						if (map.containsKey(tag)) {
							map.put(tag, map.get(tag) + 1);
							
						} else {
							map.put(tag, 1);
						}
					} else {
						Map<String, Integer> map = new HashMap<>();
						map.put(tag, 1);
						ngramCount.put(predecessors, map);
					}
				}
				
				line = reader.readLine();
			}
			reader.close();
		}
		
		// Now that we have the occurrences, convert them to emission parameters
		for (Map.Entry<String, Map<String, Integer>> entry : countTokensPerTag.entrySet()) {
			String tag = entry.getKey();
			Map<String, Integer> tokenCountMap = entry.getValue();
			double sumOfOccurences = 0;
			for (Integer  count : tokenCountMap.values()) {
				sumOfOccurences += count;
			}
			for (Map.Entry<String, Integer> tokenCount : tokenCountMap.entrySet()) {
				String token = tokenCount.getKey();
				int count = tokenCount.getValue();
				
				if (this.emissionParameters.containsKey(token)) {
					Map<String, Double> map = this.emissionParameters.get(token);
					map.put(tag, count / sumOfOccurences);
				} else {
					Map<String, Double> map = new HashMap<>();
					map.put(tag, count / sumOfOccurences);
					
					this.emissionParameters.put(token, map);
				}
			}
		}
		
		// Now that we have the occurrences, convert them to trigram parameters
		for (Map.Entry<String, Map<String, Integer>> entry : ngramCount.entrySet()) {
			String predecessors = entry.getKey();
			Map<String, Integer> tagCountMap = entry.getValue();
			double sumOfOccurences = 0;
			for (Integer  count : tagCountMap.values()) {
				sumOfOccurences += count;
			}
			for (Map.Entry<String, Integer> tagCount : tagCountMap.entrySet()) {
				String tag = tagCount.getKey();
				int count = tagCount.getValue();
				
				if (this.trigramParameters.containsKey(tag)) {
					Map<String, Double> map = this.trigramParameters.get(tag);
					map.put(predecessors, count / sumOfOccurences);
				} else {
					Map<String, Double> map = new HashMap<>();
					map.put(predecessors, count / sumOfOccurences);
					
					this.trigramParameters.put(tag, map);
				}
			}
		}
		
		if (this.logger.isDebugEnabled()) {
			for (Map.Entry<String, Map<String, Double>> tokenMap : this.emissionParameters.entrySet()) {
				String token = tokenMap.getKey();
				for (Map.Entry<String, Double> tagMap : tokenMap.getValue().entrySet()) {
					this.logger.debug("Token {} appears as {} with probability of {}", token, tagMap.getKey(), tagMap.getValue());
				}
			}
		}

		if (this.logger.isDebugEnabled()) {
			for (Map.Entry<String, Map<String, Double>> tokenMap : this.trigramParameters.entrySet()) {
				String tag = tokenMap.getKey();
				for (Map.Entry<String, Double> tagMap : tokenMap.getValue().entrySet()) {
					this.logger.debug("Tag {} appears after {} with probability of {}", tag, tagMap.getKey(), tagMap.getValue());
				}
			}
		}
	}
	
	public static void main(String[] args) throws IOException {
		ViterbiTagger v = new ViterbiTagger();
		v.learnCorpus("data/brown");
	}
}
