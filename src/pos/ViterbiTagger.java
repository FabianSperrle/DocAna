package pos;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.management.RuntimeErrorException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import tokenizer.Tokenizer;

public class ViterbiTagger {
	private Logger logger = LogManager.getLogger(ViterbiTagger.class);

	// <token, <tag, probability>>
	private Map<String, Map<String, Double>> emissionParameters = new HashMap<>();
	private Map<String, Map<String, Double>> trigramParameters = new HashMap<>();
	private String[] tags;

	private List<File> corpusFiles;

	public void learnCorpus(String pathString) throws IOException {
		Path path = Paths.get(pathString);
		this.corpusFiles = Files.walk(path).filter(Files::isRegularFile).map(Path::toFile)
				// .filter(file -> file.getName().startsWith("cc")) // only
				// reviews
				.collect(Collectors.toList());

		// Store how often each word occurs under each tag
		// <tag, <token, count>>
		Map<String, Map<String, Integer>> countTokensPerTag = new HashMap<>();
		// <<tag, tag>, <tag, count>>
		Map<String, Map<String, Integer>> ngramCount = new HashMap<>();

		for (File file : this.corpusFiles) {
			BufferedReader reader = new BufferedReader(new FileReader(file));

			String line = reader.readLine();
			while (line != null) {
				ArrayList<String> tokensWithTages = new ArrayList<String>(Arrays.asList(line.split("\\s")));
				List<String> tagList = new ArrayList<>();
				tagList.add("§");
				tagList.add("§");

				// Count the occurences
				for (String combination : tokensWithTages) {
					if (combination.equals("")) {
						continue;
					}
					String[] results = combination.split("/");
					String token = results[0];
					String tag = results[1];

					// Handle special case where token contains /
					if (results.length > 2) {
						for (int i = 1; i < results.length; i++) {
							token += results[i];
						}
						tag = results[results.length - 1];
					}

					// Simplify the tag to speed up the calculations
					if (tag.endsWith("-hl") || tag.endsWith("-tl") || tag.endsWith("-nc")) {
						tag = tag.substring(0, tag.length() - 3);
					}

					// Don't store ' '' and ` etc as they are not produced by our tokenizer
					if (tag.equals("\"") || tag.equals("'") || tag.equals("`") || tag.equals("``") || tag.equals("''")
							|| tag.equals("(") || tag.equals(")") || tag.equals(".") || tag.equals(":")
							|| tag.equals(";") || tag.equals(",") || tag.equals("--")) {
						tokensWithTages.remove(tag);
						continue;
					}

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
				tagList.add("STOP");

				// Entire line read; do something with the tag list!
				for (int i = 2; i < tagList.size(); i++) {
					String tag = tagList.get(i);
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
			for (Integer count : tokenCountMap.values()) {
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
			for (Integer count : tagCountMap.values()) {
				sumOfOccurences += count;
			}
			for (Map.Entry<String, Integer> tagCount : tagCountMap.entrySet()) {
				String tag = tagCount.getKey();
				int count = tagCount.getValue();

				if (this.trigramParameters.containsKey(predecessors)) {
					Map<String, Double> map = this.trigramParameters.get(predecessors);
					map.put(tag, count / sumOfOccurences);
				} else {
					Map<String, Double> map = new HashMap<>();
					map.put(tag, count / sumOfOccurences);

					this.trigramParameters.put(predecessors, map);
				}
			}
		}

		if (this.logger.isDebugEnabled()) {
			for (Map.Entry<String, Map<String, Double>> tokenMap : this.emissionParameters.entrySet()) {
				String token = tokenMap.getKey();
				for (Map.Entry<String, Double> tagMap : tokenMap.getValue().entrySet()) {
					this.logger.debug("Token {} appears as {} with probability of {}", token, tagMap.getKey(),
							tagMap.getValue());
				}
			}
		}

		if (this.logger.isDebugEnabled()) {
			for (Map.Entry<String, Map<String, Double>> tokenMap : this.trigramParameters.entrySet()) {
				String tag = tokenMap.getKey();
				for (Map.Entry<String, Double> tagMap : tokenMap.getValue().entrySet()) {
					this.logger.debug("Tag {} appears after {} with probability of {}", tag, tagMap.getKey(),
							tagMap.getValue());
				}
			}
		}

		this.tags = countTokensPerTag.keySet().toArray(new String[0]);
		Arrays.sort(this.tags);
		this.logger.debug(Arrays.toString(this.tags));
	}

	public void getTagList(String sentence) throws IOException {
		Tokenizer tok = new Tokenizer(sentence);
		String[] tokens = tok.tokenize();

		double[][][] probabilities = new double[tokens.length][this.tags.length][this.tags.length];
		int[][][] backpointer = new int[tokens.length][this.tags.length][this.tags.length];

		for (int k = 0; k < tokens.length; k++) {
			for (int u = 0; u < this.tags.length; u++) {
				for (int v = 0; v < this.tags.length; v++) {
					for (int w = 0; w < this.tags.length; w++) {
						double pik_1wu = k == 0 ? 1 : probabilities[k - 1][w][u];
						
						double e = 1;
						if (this.emissionParameters.containsKey(tokens[k])) {
							if (this.emissionParameters.get(tokens[k]).containsKey(tags[v])) {
								e = this.emissionParameters.get(tokens[k]).get(tags[v]);
							}
						}
						
						double q = 0;
						if (k == 0) {
							if (this.trigramParameters.get("§#§").containsKey(tags[v])) {
								q = this.trigramParameters.get("§#§").get(tags[v]);
							}
						} else if (k == 1) {
							if (this.trigramParameters.containsKey("§#" + tags[u])) {
								if (this.trigramParameters.get("§#" + tags[u]).containsKey(tags[v])) {
									q = this.trigramParameters.get("§#" + tags[u]).get(tags[v]);
								}
							}
						} else {
							if (this.trigramParameters.containsKey(tags[w] + "#" + tags[u])) {
								if (this.trigramParameters.get(tags[w] + "#" + tags[u]).containsKey(tags[v])) {
									q = this.trigramParameters.get(tags[w] + "#" + tags[u]).get(tags[v]);
								}
							}
						}

						double prob = pik_1wu * q * e;

						if (prob > probabilities[k][w][u]) {
							probabilities[k][w][u] = prob;
							backpointer[k][u][v] = w;
						}
					}
				}
			}
		}
		
		int[] resultIDs = new int[tokens.length];
		
		double maxStopProb = 0;
		int n = tokens.length - 1;
		for (int u = 0; u < this.tags.length; u++) {
			for (int v = 0; v < this.tags.length; v++) {
				double q = 0;
				if (this.trigramParameters.containsKey(tags[u] + "#" + tags[v])) {
					if (this.trigramParameters.get(tags[u] + "#" + tags[v]).containsKey("STOP")) {
						q = this.trigramParameters.get(tags[u] + "#" + tags[v]).get("STOP");
					}
				}
				double pi = probabilities[n][u][v] * q;
				if (pi > maxStopProb) {
					maxStopProb = pi;
					resultIDs[n] = v;
					resultIDs[n-1] = u;
				}
			}
		}
		
		for (int k = n - 2; k >= 0; k--) {
			resultIDs[k] = backpointer[k + 2][resultIDs[k + 1]][resultIDs[k+2]];
		}
		
		String[] result = new String[n];
		for (int i = 0; i < n; i++) {
			result[i] = this.tags[resultIDs[i]];
		}
		
		logger.debug(Arrays.toString(result));
		logger.debug("done");
	}

	public static void main(String[] args) throws IOException {
		ViterbiTagger v = new ViterbiTagger();
		v.learnCorpus("data/brown");

		v.getTagList("I went to see the movie");
	}
}
