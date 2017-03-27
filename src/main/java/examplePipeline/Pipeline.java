package examplePipeline;


import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;
import static org.apache.uima.fit.factory.CollectionReaderFactory.createReaderDescription;
import static org.apache.uima.fit.pipeline.SimplePipeline.runPipeline;
import de.tudarmstadt.ukp.dkpro.core.io.conll.Conll2006Writer;
import de.tudarmstadt.ukp.dkpro.core.io.xml.InlineXmlWriter;

import de.tudarmstadt.ukp.dkpro.core.languagetool.LanguageToolLemmatizer;
import de.tudarmstadt.ukp.dkpro.core.maltparser.MaltParser;
import de.tudarmstadt.ukp.dkpro.core.opennlp.OpenNlpPosTagger;
import de.tudarmstadt.ukp.dkpro.core.opennlp.OpenNlpSegmenter;
import de.tudarmstadt.ukp.dkpro.core.readability.ReadabilityAnnotator;

import java.nio.file.Paths;

/**
 * This class runs the entire dialogue pipeline.
 * @author jamison
 *
 */
public class Pipeline {
	
	/**
	 * Location of project on your system
	 */
	public static final String MY_PROJECT_LOCATON = Paths.get(".").toAbsolutePath().normalize().toString();

	/**
	 * Main method to run pipeline
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
	  
	    runPipeline(
	    		// First, we build a Reader for our dialogues...
	        createReaderDescription(DialogueTurnReader.class,
	        		// ... and we pass it the requested parameters (dialogue file location and language).
	        	DialogueTurnReader.PARAM_SOURCE_LOCATION, MY_PROJECT_LOCATON + "/src/main/resources/examplePipeline/dialogue-09.txt",
	        	DialogueTurnReader.PARAM_LANGUAGE, "en"
	        	),
	        // Then we list the NLP tools we want to use to annotate our dialogue turns.
	        // This is a random assortment of available tools.
	        // This tool breaks a turn string into sentences and words
	        createEngineDescription(OpenNlpSegmenter.class),
	        // This tool assigns POS-tags (noun, verb, etc)
	        createEngineDescription(OpenNlpPosTagger.class),
	        // This tool annotates each word with its lemma
	        createEngineDescription(LanguageToolLemmatizer.class),
	        // This tool returns a syntactic parse
	        createEngineDescription(MaltParser.class),
	        // This tool returns readability scores for the turn, e.g. grade level, Flesh, Kincaid, etc
	        createEngineDescription(ReadabilityAnnotator.class),
	        // Then we write some of the info into CoNLL-format files...
	        createEngineDescription(Conll2006Writer.class,
	            Conll2006Writer.PARAM_TARGET_LOCATION, "src/main/resources/examplePipeline/",
	            // overwrite allows you to overwrite existing files
	            Conll2006Writer.PARAM_OVERWRITE, true),
	        // ... and we write more of the info to xml-format files.
	        createEngineDescription(InlineXmlWriter.class,
	        InlineXmlWriter.PARAM_TARGET_LOCATION, "src/main/resources/examplePipeline/",
	        InlineXmlWriter.PARAM_OVERWRITE, true)
	        );
	}
}
