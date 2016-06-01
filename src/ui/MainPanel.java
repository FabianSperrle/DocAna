package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.hamcrest.core.IsEqual;

import pos.brill.BrillTagger;
import pos.hmm.ViterbiTagger;
import stemmer.KehlbeckSperrleStemmer;
import stemmer.Stemmer;
import tokenizer.SplitSentences;
import tokenizer.Tokenizer;
import ui.FileOpener;

public class MainPanel extends JFrame{
	private     JSplitPane  splitPaneV;
	private     JSplitPane  splitPaneH;
	private     JPanel      panel1;
	private     JPanel      panel2;
	private     JPanel      panel3;
	private File selectedFile;
	private StringBuilder textInput;
	private JTextArea txtArea;
	private String[] resultSentences;
	private String[] resultTokens;
	private String[] resultStems;
	private String[] resultVit;
	private String[] resultBrill;
	JTextArea sentences ;
	JTextArea tokens ;
	JTextArea stems;
	JTextArea posbrill;
	JTextArea posvit;
	
	
	public MainPanel(){
		
		setTitle( "Split Pane Application" );
	    setBackground(Color.gray );

	    JPanel topPanel = new JPanel();
	    topPanel.setLayout( new BorderLayout() );
	    getContentPane().add( topPanel );

	    // Create the panels
	    createPanel1();
	    createPanel2();
	    createPanel3();

	    // Create a splitter pane
	    splitPaneV = new JSplitPane( JSplitPane.VERTICAL_SPLIT );
	    topPanel.add( splitPaneV, BorderLayout.CENTER );

	    splitPaneH = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT );
	    splitPaneH.setLeftComponent( panel1 );
	    splitPaneH.setRightComponent( panel2 );

	    splitPaneV.setLeftComponent( splitPaneH );
	    splitPaneV.setRightComponent( panel3 );
	    
	    //for(Component component : getComponents(panel3)) {
	        //component.setEnabled(false);
	    //}
	
	    for(Component component : getComponents(panel2)) {
 	        component.setEnabled(false);
 	    }
	    sentences.setEditable(false);
	    tokens.setEditable(false);
	    stems.setEditable(false);
	    posbrill.setEditable(false);
	    posvit.setEditable(false);
	    textInput = null;
	    resultSentences = null;
	    resultTokens = null;
	    resultStems = null;
	    
	    txtArea.getDocument().addDocumentListener(new DocumentListener() {
	    	  public void changedUpdate(DocumentEvent e) {
	    	    changed();
	    	  }
	    	  public void removeUpdate(DocumentEvent e) {
	    	    changed();
	    	  }
	    	  public void insertUpdate(DocumentEvent e) {
	    	    changed();
	    	  }
	    	  public void changed() {
	    		     if (txtArea.getText().equals("")){
	    		    	 for(Component component : getComponents(panel2)) {
		    		 	        component.setEnabled(false);
		    		 	    }
	    		     }
	    		     else {
	    		    	for(Component component : getComponents(panel2)) {
	    		 	        component.setEnabled(true);
	    		 	    }
	    		    }

	    		  }
	    		});
	}
	
	public void createPanel1(){
	    panel1 = new JPanel();
	    panel1.setLayout( new BorderLayout() );

	    // Add some buttons
	    JButton openButton = new JButton("Open File");
	    panel1.add(openButton, BorderLayout.CENTER );
	    openButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
            	FileOpener opener = new FileOpener();
            	selectedFile = opener.createFileUI(MainPanel.this);
            	// Read the input
        	    try {
        	    	textInput = new StringBuilder();
        			BufferedReader buffReader = new BufferedReader(new FileReader(selectedFile.getAbsolutePath()));
        			String str;
        	        while ((str = buffReader.readLine()) != null) {
        	        	textInput.append(str);
        	        	textInput.append("\n");
        	        }	
        	        txtArea.setText(textInput.toString());
        	        buffReader.close();
        		    for(Component component : getComponents(panel3)) {
        		        component.setEnabled(true);
        		    }
        		    for(Component component : getComponents(panel2)) {
        		        component.setEnabled(true);
        		    }
        		} catch (Exception e) {
        			// TODO Auto-generated catch block
        			e.printStackTrace();
        		}
            }
	    });
	}

	public void createPanel2(){
	    panel2 = new JPanel();
	    panel2.setLayout( new FlowLayout() );

	    JButton sentencesButton = new JButton( "Extract Sentences" );
	    sentencesButton.addActionListener(new ActionListener() {
	        @Override
	        public void actionPerformed( ActionEvent e ) {
	        	SplitSentences sentence = new SplitSentences( txtArea.getText() );
	    		try {
	    			resultSentences = sentence.tokenize();
	    			//System.out.println(Arrays.toString(resultSentences));
	    		} catch (IOException e2) {
	    			e2.printStackTrace();
	    		}
	    		 JOptionPane.showMessageDialog(MainPanel.this, "Extracted the sentences!",
                                               "Done!",
                                               JOptionPane.OK_OPTION);
	    		 sentences.setText(Arrays.toString(resultSentences));
	    		 panel3.repaint();
	    		 panel3.revalidate();
	        }
	    });
	    panel2.add( sentencesButton );
	    
	    JButton tokensButton =  new JButton( "Extract tokens" );
	    tokensButton.addActionListener(new ActionListener() {
	        @Override
	        public void actionPerformed( ActionEvent e ) {
	        	
	        	 Tokenizer tokenizer = new Tokenizer(txtArea.getText());
	        	 try {
					resultTokens = tokenizer.tokenize();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
	    		 JOptionPane.showMessageDialog(MainPanel.this, "Extracted the tokens!",
                                               "Done!",
                                               JOptionPane.OK_OPTION);
	    		 tokens.setText(Arrays.toString(resultTokens));
	    		 
	    		 panel3.repaint();
	    		 panel3.revalidate();
	        }
	    });
	    panel2.add( tokensButton );
	    
	    JButton stemsButton =  new JButton( "Extract stems" );
	    stemsButton.addActionListener(new ActionListener() {
	        @Override
	        public void actionPerformed( ActionEvent e ) {
	        	
	        	 Stemmer stemmer = new KehlbeckSperrleStemmer();
	        	 if (resultTokens == null) {
	        		 Tokenizer tokenizer = new Tokenizer(txtArea.getText());
		        	 try {
						resultTokens = tokenizer.tokenize();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
	        	 }
	        	 resultStems = new String[resultTokens.length];
	        	 String[] pos = new String[resultTokens.length];
					for (int i = 0; i < resultTokens.length; i++) {
						resultStems[i] = stemmer.stem(resultTokens[i]);
					}
	    		 JOptionPane.showMessageDialog(MainPanel.this, "Extracted the stems!",
                                               "Done!",
                                               JOptionPane.OK_OPTION);
	    		 stems.setText(Arrays.toString(resultStems));
	    		 panel3.repaint();
	    		 panel3.revalidate();
	        }
	    });
	    panel2.add( stemsButton );
	    
	    JButton posProb =  new JButton( "Extract POS probalistic" );
	    posProb.addActionListener(new ActionListener() {
	        @Override
	        public void actionPerformed( ActionEvent e ) {
	        	resultVit = new String[txtArea.getText().length()];
	        	ViterbiTagger vit;
				try {
					vit = new ViterbiTagger("data/brown");
					resultVit = vit.getTagList(txtArea.getText());
				} catch (IOException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
	    		 JOptionPane.showMessageDialog(MainPanel.this, "Extracted the Viterbi Tags!",
                                               "Done!",
                                               JOptionPane.OK_OPTION);
	    		 posvit.setText(Arrays.toString(resultVit));
	    		 panel3.repaint();
	    		 panel3.revalidate();
	        }
	    });
	    panel2.add( posProb );
	    
	    JButton posRule =  new JButton( "Extract POS rule-based" );
	    posRule.addActionListener(new ActionListener() {
	        @Override
	        public void actionPerformed( ActionEvent e ) {
	        	resultBrill = new String[txtArea.getText().length()];
	        	BrillTagger br;
				try {
					br = new BrillTagger();
					resultBrill = br.tag(txtArea.getText());
				} catch (IOException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
	    		 JOptionPane.showMessageDialog(MainPanel.this, "Extracted the Brill Tags!",
                                               "Done!",
                                               JOptionPane.OK_OPTION);
	    		 posbrill.setText(Arrays.toString(resultBrill));
	    		 panel3.repaint();
	    		 panel3.revalidate();
	        }
	    });
	    panel2.add( posRule );
	}

	public void createPanel3(){
	    panel3 = new JPanel();
	    //panel3.setLayout( new FlowLayout() );
	    panel3.setLayout( new BoxLayout(panel3,BoxLayout.PAGE_AXIS) );
	    panel3.setPreferredSize( new Dimension( 1500, 800 ) );
	    panel3.setMinimumSize( new Dimension( 1000, 500 ) );

	    panel3.add( new JLabel( "Input Text:" ));
	    txtArea = new JTextArea();
	    panel3.add( txtArea);
	    JScrollPane scrollFrameTxt = new JScrollPane(txtArea);
	    txtArea.setAutoscrolls(true);
	    scrollFrameTxt.setPreferredSize(new Dimension( 800,300));
	    panel3.add(scrollFrameTxt);
	    
		 panel3.add( new JLabel( "Sentence Splitter:" ));
		 sentences = new JTextArea();
		 panel3.add( sentences );
		 JScrollPane sentencesArea = new JScrollPane( sentences );
		 sentencesArea.setAutoscrolls( true );
		 sentencesArea.setPreferredSize( new Dimension( 800,300) );
		 panel3.add(sentencesArea);
	    
		 panel3.add( new JLabel( "Tokenizer:" ));
		 tokens = new JTextArea();
		 panel3.add( tokens );
		 JScrollPane tokensArea = new JScrollPane( tokens );
		 tokensArea.setAutoscrolls( true );
		 tokensArea.setPreferredSize( new Dimension( 800,300) );
		 panel3.add(tokensArea);
	    
		 panel3.add( new JLabel( "Stemmer:" ));
		 stems = new JTextArea();
		 panel3.add( stems );
		 JScrollPane stemsArea = new JScrollPane(stems);
		 stemsArea.setAutoscrolls(true);
		 stemsArea.setPreferredSize(new Dimension( 800,300));
		 panel3.add(stemsArea);
		 
		 panel3.add( new JLabel( "Viterbi Tagger:" ));
		 posvit = new JTextArea();
		 panel3.add( posvit );
		 JScrollPane vitArea = new JScrollPane(posvit);
		 vitArea.setAutoscrolls(true);
		 vitArea.setPreferredSize(new Dimension( 800,300));
		 panel3.add(vitArea);
		 
		 panel3.add( new JLabel( "Brill Tagger:" ));
		 posbrill = new JTextArea();
		 panel3.add( posbrill );
		 JScrollPane brillArea = new JScrollPane(posbrill);
		 brillArea.setAutoscrolls(true);
		 brillArea.setPreferredSize(new Dimension( 800,300));
		 panel3.add(brillArea);
	}
	
	private Component[] getComponents(Component container) {
        ArrayList<Component> list = null;

        try {
            list = new ArrayList<Component>(Arrays.asList(
                  ((Container) container).getComponents()));
            for (int index = 0; index < list.size(); index++) {
                for (Component currentComponent : getComponents(list.get(index))) {
                    list.add(currentComponent);
                }
            }
        } catch (ClassCastException e) {
            list = new ArrayList<Component>();
        }

        return list.toArray(new Component[list.size()]);
    }

	public static void main( String args[] ){
	    try {
	        UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
	    } catch (Exception evt) {}
	    // Create an instance of the test application
	    MainPanel mainFrame = new MainPanel();
	    mainFrame.pack();
	    mainFrame.setVisible( true );
	}
}
