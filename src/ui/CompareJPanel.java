package ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import pos.brill.BrillTagger;
import reader.Review;
import tokenizer.SentenceSplitter;
import tokenizer.Tokenizer;

public class CompareJPanel extends JPanel{
	
	List<Review> reviews;
	JTextArea sentences;
    JComboBox<Integer> comboBox;
    SentenceSplitter sp;
    
	public CompareJPanel(Integer[] reviewsList, List<Review> pReviews ) {
		super();
		super.setLayout(new FlowLayout());
		
		reviews = pReviews;
		// Add select box
		comboBox = new JComboBox<Integer>();
		comboBox.setModel(new DefaultComboBoxModel(reviewsList));
		comboBox.setMaximumSize( comboBox.getPreferredSize() );
		super.add(comboBox);
		
		sentences = new JTextArea(" ");
		sentences.setEditable(false);
		sentences.setSize(super.getSize());
		JScrollPane scrollFrameTxt = new JScrollPane(sentences);
		scrollFrameTxt.setPreferredSize(new Dimension(700, 300));
		sentences.setCaretPosition(0);
		super.add(scrollFrameTxt);
		sp = new SentenceSplitter();
		comboBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Integer selectedReview = (Integer)comboBox.getSelectedItem();
				String[] str =sp.split(reviews.get(comboBox.getSelectedIndex()).getText());
				sentences.setText(String.join("\n", str));
			}
		});
		

	}
	

	public JComboBox<Integer> getComboBox() {
		return comboBox;
	}


	public void setComboBox(JComboBox<Integer> comboBox) {
		this.comboBox = comboBox;
	}




	


}

