package main;

import java.io.File;

import de.tudarmstadt.ukp.jwktl.JWKTL;

public class WiktionaryDBCreator {
	
	/**
	 * Parsing the data
	 * Before JWKTL is ready to use, you need to parse the obtaining Wiktionary dump file. 
	 * The rationale behind this is to get in a position to efficiently access the Wiktionary 
	 * data within a productive application environment by separating out all preparatory 
	 * matters in a parsing step. In this step, the wiki syntax is being parsed by JWKTL and 
	 * stored in a Berkeley DB. The parsing methods are based on text mining methods, which 
	 * obviously require some computation time. This is, however, a one-time effort. The 
	 * resulting database can then be repeatedly and quickly accessed, as discussed in the 
	 * next section.
	 * To achieve that, create a new Java project and add JWKTL to your classpath as 
	 * described in the first section. Create a new class and run the parser using the 
	 * following sample code.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		  File dumpFile = new File("dictionary/enwiktionary-20160407-pages-articles.xml");
		  File outputDirectory = new File("dictionary");
		    
		  JWKTL.parseWiktionaryDump(dumpFile, outputDirectory, true);
	}

}
