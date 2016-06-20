package ui;

import java.awt.Color;
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

import pos.brill.BrillTagger;
import reader.Review;
import tokenizer.SentenceSplitter;
import tokenizer.Tokenizer;

public class CompareJPanel extends JPanel{
	
	List<Review> reviews;
	
    JComboBox<Integer> comboBox;
    
	public CompareJPanel(Integer[] reviewsList, List<Review> pReviews ) {
		super();
		reviews = pReviews;
		// Add select box
		comboBox = new JComboBox<Integer>();
		comboBox.setModel(new DefaultComboBoxModel(reviewsList));
		comboBox.setMaximumSize( comboBox.getPreferredSize() );
		super.add(comboBox);
		comboBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Integer selectedReview = (Integer)comboBox.getSelectedItem();
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

