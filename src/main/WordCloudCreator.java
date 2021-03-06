package main;

import java.awt.Color;
import java.awt.Dimension;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kennycason.kumo.CollisionMode;
import com.kennycason.kumo.WordCloud;
import com.kennycason.kumo.WordFrequency;
import com.kennycason.kumo.bg.PixelBoundryBackground;
import com.kennycason.kumo.bg.RectangleBackground;
import com.kennycason.kumo.font.scale.LinearFontScalar;
import com.kennycason.kumo.nlp.FrequencyAnalyzer;
import com.kennycason.kumo.palette.ColorPalette;

public class WordCloudCreator {
	private Logger logger = LogManager.getLogger(WordCloudCreator.class);

	private String outputPath;
	private String overlayPath;
	private String inputStreamPath;

	public WordCloudCreator(String outputPath, String overlayPath, String inputStreamPath) {
		super();
		this.outputPath = outputPath;
		this.overlayPath = overlayPath;
		this.inputStreamPath = inputStreamPath;
	}

	void createWordCloud() throws IOException {
		final FrequencyAnalyzer frequencyAnalyzer = new FrequencyAnalyzer();
		frequencyAnalyzer.setWordFrequenciesToReturn(300);
		frequencyAnalyzer.setMinWordLength(4);
		InputStream stream = new FileInputStream(inputStreamPath);
		final List<WordFrequency> wordFrequencies = frequencyAnalyzer.load(stream);
		final Dimension dimension = new Dimension(1000, 1000);
		final WordCloud wordCloud = new WordCloud(dimension, CollisionMode.PIXEL_PERFECT);
		wordCloud.setPadding(2);
		final InputStream imgStream = getInputStream(overlayPath);
		wordCloud.setBackground(new RectangleBackground(dimension));
		//wordCloud.setBackground(new PixelBoundryBackground(imgStream));
		wordCloud.setColorPalette(new ColorPalette(new Color(0x4055F1), new Color(0x408DF1), new Color(0x40AAF1),
				new Color(0x40C5F1), new Color(0x40D3F1), new Color(0xFFFFFF)));
		wordCloud.setFontScalar(new LinearFontScalar(10, 40));
		wordCloud.build(wordFrequencies);
		wordCloud.writeToFile(outputPath);
		stream.close();
		imgStream.close();
	}

	private static InputStream getInputStream(final String path) throws IOException{
		return new FileInputStream(path);
	}

}
