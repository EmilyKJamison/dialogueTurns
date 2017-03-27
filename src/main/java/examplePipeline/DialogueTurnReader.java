package examplePipeline;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.uima.UimaContext;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.fit.component.JCasCollectionReader_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Progress;
import org.apache.uima.util.ProgressImpl;

import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;




/**
 * Reads dialogue turns from a dialogue file
 */
public class DialogueTurnReader
    extends JCasCollectionReader_ImplBase
{
	
    /**
     * File that holds the list of file pairs
     */
    public static final String PARAM_SOURCE_LOCATION = "inputDialogueFile";
    @ConfigurationParameter(name = PARAM_SOURCE_LOCATION, mandatory = true)
    protected String inputDialogueFileName;

    /**
     * The language of the turns
     */
    public static final String PARAM_LANGUAGE = "LanguageCode";
    @ConfigurationParameter(name = PARAM_LANGUAGE, mandatory = true)
    protected String language;
	
    /**
     * The file object of the input file
     */
	private File dialogueFile;
	
	/**
	 * A list of all the dialogue turns in the file
	 */
	private List<String> dialogueTurns;
	
	/**
	 * Counter for the dialogue turns, i.e. we're on 13 out of 42
	 */
	protected int turnCounter;
	
	/**
	 * Does everything necessary to set up the Reader before we start reading the input file
	 */
    @Override
    public void initialize(UimaContext aContext)
        throws ResourceInitializationException
    {
        super.initialize(aContext);
        
        dialogueFile = new File(inputDialogueFileName);
        
        // read the dialogue file into a list of turns
        try{
        	dialogueTurns = readFileToList(inputDialogueFileName);
        } catch (Exception e){
        	throw new ResourceInitializationException(e);
        }
        turnCounter = 0;
    }
	
    /**
     * Tells the Reader if we should keep making more jcas objects.
     * (Each jcas is a dialogue turn to be processed.)
     */
    @Override
    public boolean hasNext()
        throws IOException, CollectionException
    {
    	return turnCounter < dialogueTurns.size();
    }
    
    /**
     * Tells the system "we're on 8 out of 54 turns total"
     */
    @Override
    public Progress[] getProgress()
    {
        return new Progress[] { new ProgressImpl(turnCounter, dialogueTurns.size(),
                Progress.ENTITIES) }; 
    }

    /**
     * Gets name of the corpus. Can be accessed later if desired.
     * @return name
     */
	public String getCollectionId() {
		return "WadeCorpus";
	}
	
	/**
	 * Gets a unique id for the jcas (the dialogue turn)
	 * @return the id
	 */
	public String getDocumentId() {
		return dialogueFile.getName().replace(".txt",  "") + "-Turn" + turnCounter;
	}
	
	/**
	 * Gets a human-readable name for the jcas (the dialogue turn)
	 * @return the title
	 */
	public String getTitle() {
		return dialogueFile.getName().replace(".txt",  "") + "-Turn" + turnCounter;
	}
	
	/**
	 * Gets the language for the jcas (the dialogue turn)
	 * @return language
	 */
	public String getLanguage() {
		return language;
	}
	
	/**
	 * Gets the text of the dialogue turn
	 * @return turn text
	 */
	public String getText(){
		return dialogueTurns.get(turnCounter);
	}
	/**
	 * Creates a new jcas object to represent the next dialogue turn
	 */
    @Override
    public void getNext(JCas jcas)
        throws IOException, CollectionException
    {
        DocumentMetaData docMetaData = DocumentMetaData.create(jcas);
        docMetaData.setDocumentTitle(getTitle());
        docMetaData.setDocumentId(getDocumentId());
        docMetaData.setCollectionId(getCollectionId());
        jcas.setDocumentLanguage(getLanguage());
        jcas.setDocumentText(getText());

        turnCounter++;
    }
	
    /**
     * Util method to read a dialogue file into a list of dialogue turns.  
     * Each turn can be multiple sentences or carriage-returns (for the chat paradigm).
     * 
     * @param fileLocationString the dialogue file
     * @return list of dialogue turns
     * @throws IOException
     */
	private static List<String> readFileToList(String fileLocationString) throws IOException {
        
        File fileLocation = new File(fileLocationString);
        List<String> dialogueTurns = new ArrayList<String>();
        String aTurn = "";
        for(String line : FileUtils.readLines(fileLocation)) {
            line = line.replace("\n", "");
            if((line.startsWith("C: ") || line.startsWith("W: ")) && aTurn.length() > 0){
            	dialogueTurns.add(aTurn);
            	aTurn = line.replaceFirst("C: ", "").replaceFirst("C: ", "");
            }else{
            	aTurn = aTurn + " " + line.replaceFirst("C: ", "").replaceFirst("C: ", "");
            }
        }
        return dialogueTurns;
    }
}
