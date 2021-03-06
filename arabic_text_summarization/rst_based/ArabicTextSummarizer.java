/*
 * ArabicTextSummarizer.java
 *
 * Created in 2005
 */

package arabic_text_summarization.rst_based;

import java.io.*;
import factory.encoding.*;
import nlp.rst.*;
import nlp.text.*;
import java.util.*;
import javax.swing.*;
/**
 * This class provides the summarization method:
 *          - summarizeText/1
 * It also provides the related methods that return the resulting structures 
 * from the summarization, these are:
 *          - getGenertatedTree/0
 *          - getTextualSpan/0
 * and a method to write the resulting summary to a file:
 *          - outputSummary/3
 * 
 * @author  Waleed Alsanie
 * @version 0.2 beta
 */
public class ArabicTextSummarizer {
    
    private RSTconstructor rstBuilder;
    private TreeSelector treeSelector;
    private int repetitionThreshold;
    private int treeLevel;
    private RelationsHypothesizer relHypo;
    private ElementaryUnitsDeterminer unitsDeter;
    private String dataDir;
    private final CharCoding encoding;
    private final String encodingName;
    private LinkedList paraUnits;
    private TextualSpan selectedTree;
    
    /** Creates a new instance of ArabicTextSummarizer with the default 
     * character encoding of the input file which is "UTF8" 
     */
    public ArabicTextSummarizer() {
        encodingName = "UTF8";
        encoding = this.makeCharCodingObject("UTF8");
        treeLevel = 2;
        paraUnits = new LinkedList();
        repetitionThreshold = 2;
        selectedTree = null;
    }
    
    /** Creates a new instance of ArabicTextSummarizer 
     *@param charset: the name of the character encoding in which the input file 
     * was written. 
     */
    public ArabicTextSummarizer(String charset) {
        encodingName = charset;
        encoding = this.makeCharCodingObject(charset);
        treeLevel = 2;
        paraUnits = new LinkedList();
        repetitionThreshold = 2;
        selectedTree = null;
    }
    
    // Facilitates creating CharCoding Object 
    private CharCoding makeCharCodingObject(String charset){
        try{
            EncodingCreator encodingFactory = new ArabicEncodingCreator ();
            return encodingFactory.createEncoding(encodingName);
        } catch(UnsupportedEncodingException e) {
            System.out.println("Error: " + charset + 
                    " Encoding is not supported. Cannot run the program!");
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, 
                                    charset + " Encoding is not supported\n" + 
                                              "Cannot run the program!",
                                    "Error", JOptionPane.INFORMATION_MESSAGE
            );
            return null;
        }
    } 
    
    // The typical get and set methods
    public void setDataDir(String dir) {
        dataDir = dir;
    }
    
    public void setTreeLevel(int level) {
        treeLevel = level;
    }
    
    public void setRepetitionThreshold(int threshold) {
        repetitionThreshold = threshold;
    }
    
    public TextualSpan getGenertatedTree() {
        return selectedTree;
    }
    
    public LinkedList getTextualSpan() {
        return paraUnits;
    }
    //
    
    /**
     * This methods takes a file whose containing the text to be summarized. 
     * The summary generated by this method can be obtained by calling the 
     * "getGeneratedSummary/0 method.
     * 
     * @param inputF: The file containing the text to be summarized.
     * @throws IOException is thrown in case of there is an error in reading 
     *         from inputF.
     */
    public void summarizeText(String inputF) throws IOException {
        
        if (dataDir == null) 
            throw new IOException("Data directory including files for " +
                                  " cues and relations has not been set");
        
        load(dataDir);
        BufferedReader input = new BufferedReader(
                new InputStreamReader(
                        new FileInputStream(inputF), "UTF-8"
                )
        );            
        String para;
        while ((para = input.readLine()) != null) {
            if (para.length() < 300) {
                String tempStr;
                while ((tempStr = input.readLine()) != null) {
                    para = para.concat(tempStr);
                    if (para.length() >= 300)
                        break;
                }
            }
            String[] stat= para.split("\\.");
            LinkedList units2 = null;
            LinkedList units1 = new LinkedList();
            //int prevIndex = 1;
            for (int i = 0; i < stat.length; i++) {                   
                Text text = new Text(stat[i], encoding);
                units2 = (LinkedList) unitsDeter.determineElementaryUnits(text);
                relHypo.hypothesizeRelationsBasedOnCues(
                        units2, 
                        unitsDeter.getFunctioningCues()
                );

                unitsDeter.resetElementaryUnits();
                unitsDeter.resetCuePhrases();
                if (units1.size() != 0 && units2.size() != 0) 
                    relHypo.hypothesizeRelationsOnChunkUnits(units1, units2);
                
                units1.addAll(units2);
            }
            if (paraUnits.size() != 0)
                relHypo.hypothesizeRelationsOnChunkUnits(paraUnits, units1);

            paraUnits.addAll(units1);
        }

        rstBuilder.setRelations(relHypo.getHypothesizedRelations());
        rstBuilder.buildRST();
        treeSelector = new MostBalancedTreeSelector();
        Collection trees = rstBuilder.getBuiltRST();
        if (trees.size() == 0) {
            System.out.println("No tree was generated!");
            return;
        }
        else              
            selectedTree = treeSelector.selectTree(trees);
        
        input.close();
    }
    
    // Load the data related to cue phrases and relations.
    private void load(String dataDir) {
        
        DataLoader data = new DataLoader(dataDir);
        data.loadCuePhrases();
        data.loadRelations();
        data.loadRepetitionRelations();
        relHypo = new RelationsHypothesizer(encoding);
        relHypo.setBinaryRelations(data.getBinaryRelations());
        relHypo.setComplexRelations(data.getComplexRelations());
        relHypo.setRelationsFewRepetition(data.getFewRepetitionRelations());
        relHypo.setRelationsManyRepetition(data.getManyRepetitionRelations());
        relHypo.setThreshold(repetitionThreshold);
        unitsDeter = new ElementaryUnitsDeterminer(data.getCuesPhrases());
        rstBuilder = new RSTconstructor(
                data.getHypotacticRelations(), data.getParatacticRelations()
        );
    }
    
    /**
     * This methods takes a tree, a collection of text units and a path to 
     * the file to which the summary will be written. 
     * The summary generated by this method can be obtained by calling the 
     * "getGeneratedSummary/0 method.
     * 
     * @param tree: A rhetorical tree.
     * @param textUnits: The units based on which the tree was built.
     * @param outFileName: the path of the file to which the summary will be
     *                     written.
     * @throws IOException is thrown in case of there is an error in writing
     *         to the file.
     */
    public void outputSummary(RhetTree tree, Collection textUnits, 
            String outFileName) throws IOException {
        
        OutputStreamWriter outputF = new OutputStreamWriter(
                new FileOutputStream(outFileName), encodingName
        );       
        
        Iterator iter = relHypo.getHypothesizedRelations().iterator();
        outputF.write("##################### Hypothesized Relations " + 
                      "#####################\n");
        while (iter.hasNext()) {
            RhetRel rel = (RhetRel) iter.next();
            if (rel instanceof BinaryRhetRel) {
                BinaryRhetRel rel1 = (BinaryRhetRel) rel;
                outputF.write(rel1.getRel() + "-");
                outputF.write(rel1.getFirstSpan() + "-");
                outputF.write(rel1.getSecondSpan() + "\n");
            }
            else {
                ComplexRhetRel rel1 = (ComplexRhetRel) rel;
                outputF.write(rel1.getRel() + "-");
                outputF.write(rel1.getFirstSpanStart() + "-");
                outputF.write(rel1.getFirstSpanEnd() + ";");
                outputF.write(rel1.getSecondSpanStart() + "-");
                outputF.write(rel1.getSecondSpanEnd() + "\n");
            }
        }     
        
        outputF.write("#############################################" + 
                      "#####################\n");
        
        Collection units = getPromotions(tree, treeLevel);
        Iterator it = units.iterator();
        int summary[] = new int[units.size()];
        for (int i = 0; i < units.size(); i++)
            summary[i] = ((Integer) it.next()).intValue(); 
        
        outputF.write("\n\n");
        outputF.write("############################## Summary " + 
                      "###########################\n\n");
        
        summary = sortUnits(summary);
        for (int i = 0; i < summary.length; i++) {
            it = textUnits.iterator();
            while (it.hasNext()) {
                ElementaryUnit elem = (ElementaryUnit) it.next();
                if (elem.getNumber() == summary[i]) {
                    outputF.write("*");
                    outputF.write(elem.getContent().trim());
                    if (! String.valueOf(elem.getContent().
                          charAt(elem.getContent().length()-1)).
                          matches(encoding.getPuncRegex()))
                        outputF.write(". ");
                    outputF.write("\n");
                }
            }
        }
        
        outputF.close();
    }
    
    /**
     * This method can be called to write the text units obtained after the 
     * summarization has been run.
     * 
     * @param path: the path of the file to which the units will be
     *              written.
     * @throws IOException is thrown in case of there is an error in writing
     *         to the file.
     */
    public void writeUnits(String path) throws IOException{
        
        OutputStreamWriter outputF = new OutputStreamWriter(
                new FileOutputStream(path), encodingName
        ); 
        
        Iterator i = paraUnits.iterator();
        while (i.hasNext()) {
            ElementaryUnit elm = (ElementaryUnit) i.next();
            outputF.write(String.valueOf(elm.getNumber()) + "-" + 
                    elm.getContent() + "\n");
        }
        outputF.close();
    }
    
    private int[] sortUnits(int[] notSorted) {
        
        for (int i = 0; i < notSorted.length; i++)
            for (int j = i+1 ; j < notSorted.length; j++)
                if (notSorted[i] > notSorted[j]) {
                    int temp = notSorted[i];
                    notSorted[i] = notSorted[j];
                    notSorted[j] = temp;
                }
        // Cancel redundant units by marking them with -1
        for (int i=0; i < notSorted.length; i++)
            for (int j = i+1 ; j < notSorted.length; j++)
                if (notSorted[i] == notSorted[j])
                    notSorted[j] = -1;
        /*
        int count = 0;
        for (int i = 0; i < notSorted.length; i++)
            if (notSorted[i] != -1)
                count++;
        
        int result[] = new int[count];
        for (int i = 0, j = 0; i < notSorted.length; i++)
            if (notSorted[i] != -1)
                result[j++] = notSorted[i];
        */           
        return notSorted;
    }
    
    private Collection getPromotions(RhetTree t, int level) {
        
        Collection promo = new LinkedList();
        
        if (level == 0 || t == null)
            return promo;
        else {
            promo.addAll(t.getPromotion());
            promo.addAll(getPromotions (t.getLeft(), level -1));
            promo.addAll(getPromotions (t.getRight(), level -1));
            return promo;
        }
    }
}