package main;

import java.io.File;

import de.tudarmstadt.ukp.jwktl.JWKTL;
import de.tudarmstadt.ukp.jwktl.api.IWiktionaryEdition;
import de.tudarmstadt.ukp.jwktl.api.IWiktionaryEntry;
import de.tudarmstadt.ukp.jwktl.api.IWiktionaryPage;
import de.tudarmstadt.ukp.jwktl.api.IWiktionarySense;

public class WiktionaryTest {
	
	public static void main(String[] args) {
		IWiktionaryEdition dict = JWKTL.openEdition(new File("dictionary"));
		IWiktionaryPage page = dict.getPageForWord("test");
		
		IWiktionaryEntry entry = page.getEntry(0);
		IWiktionarySense sense = entry.getSense(0);
		
		System.out.println(sense.getGloss().getText());
		
		dict.close();
	}

}
