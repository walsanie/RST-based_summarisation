/*
 * DataLoader.java
 *
 * Created in 2005
 */

package nlp.rst;

import java.io.*;
import java.sql.*;
import java.util.*;
import factory.encoding.*;
import nlp.text.CharCoding;

/**
 * Loads the data defining the cue phrases and the relations from the files.
 * 
 * @author  Waleed Alsanie
 * @version 0.2 beta
 */
public class DataLoader {
    
    private final String filesDir;
    private final CharCoding encoding; 
    private LinkedList allCues;
    private LinkedList binaryRel;
    private LinkedList complexRel;
    private String manyRepRel;
    private String fewRepRel;
    private LinkedList hypotacticRel;
    private LinkedList paratacticRel;
    
    /** Creates a new instance of DataLoader */
    /** Creates a new instance of DataLoader */
    public DataLoader(String dir) {
        filesDir = dir;
        allCues = new LinkedList();
        binaryRel = new LinkedList();
        complexRel = new LinkedList();
        hypotacticRel = new LinkedList();
        paratacticRel = new LinkedList();
        // If no encoding is given, make the default encoding "UTF-8"
        encoding = DataLoader.createCharEncoding("UTF-8");
    }
    
    /** Creates a new instance of DataLoader */
    public DataLoader(String dir, String en) {
        filesDir = dir;
        allCues = new LinkedList();
        binaryRel = new LinkedList();
        complexRel = new LinkedList();
        hypotacticRel = new LinkedList();
        paratacticRel = new LinkedList();
        // If no encoding is given, make the default encoding "UTF-8"
        encoding = DataLoader.createCharEncoding(en);
    }
    
    private static CharCoding createCharEncoding(String encode) {
        try{
            // If no encoding is given, make the default encoding "UTF-8"
            return new ArabicEncodingCreator().createEncoding(encode);
        } catch(UnsupportedEncodingException e) {
            System.out.println("ERROR: The encoding given for the files " + 
                               "containing the cue phrases is not supported!");
            e.printStackTrace();
            return null;
        }
    }
    
    public LinkedList getCuesPhrases() {
        return allCues;
    }
    
    public LinkedList getBinaryRelations() {
        return binaryRel;
    }
    
    public LinkedList getComplexRelations() {
        return complexRel;
    }
    
    public String getFewRepetitionRelations() {
        return fewRepRel;
    }
    
    public String getManyRepetitionRelations() {
        return manyRepRel;
    }
    
    public LinkedList getHypotacticRelations() {
        return hypotacticRel;
    }
    
    public LinkedList getParatacticRelations() {
        return paratacticRel;
    }
    
    /**
     * Load the cue phrases from a file called "cues.txt". The cue phrases in 
     * the file should be written as follows:
     *              Regex, Action, Name, Relation, Status, Position 
     * 
     */
    public void loadCuePhrases() {
        
        // File for cues with actions
        String actinofile = new File(filesDir, "cues.txt").toString();
        BufferedReader actfile = null;
        try{
            actfile = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(actinofile), encoding.getCharset()
                    )
            ); 
        } catch(FileNotFoundException e) {
            System.out.println("ERROR: Cannot find the file \"cues.txt\"!");
            e.printStackTrace();
        }
        
        // store the cue phrases
        String line;
        try{
            while ((line = actfile.readLine()) != null) 
                // If a line starts with '#' ignore it as it is a comment 
                if (! line.startsWith("#") && ! line.matches("\\s*")) {
                    String[] marker = line.split(",");
                    CuePhrase cue = new CuePhrase ();                                                     
                    cue.setRegex(marker[0].trim());
                    cue.setAction(marker[1].trim());
                    cue.setName(marker[2].trim());
                    // If Action is not "NOTHING" then get the other info
                    // Position, Relation and Status
                    if (! marker[1].matches("[Nn][Oo][Tt][Hh][Ii][Nn][Gg]")) {
                        cue.setRelation(marker[3].trim());
                        cue.setStatus(marker[4].trim());
                        cue.setPosition(marker[5].trim());
                    }
                    allCues.add (cue);
                }
            
            actfile.close();
        } catch(IOException e) {
            System.out.println("ERROR: In reading from \"cues.txt\"!\n" +
                               "It could be due to an ill formed line or a "+
                               "corrupted file!");
            e.printStackTrace();
        }
    }
    
    /**
     * Load the relations from a file called "relations.txt". The relations in 
     * the file should be written as follows:
     *              Relation, Type, Status
     * 
     * where Type is either "Binary" or "Complex" and Status is either 
     * "Paratactic" or "Hypotactic".
     * 
     */
    public void loadRelations() {
        
        // File for relations
        String relationsfile = new File(filesDir, "relations.txt").toString();
        // File with cues with no actions
        BufferedReader relFile = null;
        try{
            relFile = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(relationsfile), encoding.getCharset()
                    )
            ); 
        } catch(FileNotFoundException e) {
            System.out.println("ERROR: Cannot find the file \"relations.txt\"!");
            e.printStackTrace();
        }
        
        // store the relations
        String line;
        try{
            while ((line = relFile.readLine()) != null) 
                // If a line starts with '#' ignore it as it is a comment
                if (! line.startsWith("#") && ! line.matches("\\s*")) {
                    String[] marker = line.split(",");
                    if (marker[1].trim().matches("[Bb][Ii][Nn][Aa][Rr][Yy]"))
                        binaryRel.add(marker[0].trim());
                    else 
                        if (marker[1].trim().matches("[Cc][Oo][Mm][Pp][Ll][Ee][Xx]"))
                            complexRel.add(marker[0].trim());
                    //else do nothing for this conditional
                    if (marker[2].trim().matches("[Hh][Yy][Pp][Oo][Tt][Aa][Cc][Tt][Ii][Cc]"))
                        hypotacticRel.add(marker[0].trim());
                    else
                        if (marker[2].trim().matches("[Pp][Aa][Rr][Aa][Tt][Aa][Cc][Tt][Ii][Cc]"))
                            paratacticRel.add(marker[0].trim());
                }
            
            relFile.close();
        } catch(IOException e) {
            System.out.println("ERROR: In reading from \"relations.txt\"!\n" +
                               "It could be due to an ill formed line or a "+
                               "corrupted file!");
            e.printStackTrace();
        }
    }
    
    /**
     * Load the repetition types and he relations associated with them from 
     * a file called "repetition.txt". 
     * 
     */
    public void loadRepetitionRelations() {
        
        // File for relations
        String repfile = new File(filesDir, "repetition.txt").toString();
        // File with cues with no actions
        BufferedReader rFile = null;
        try{
            rFile = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(repfile), encoding.getCharset()
                    )
            ); 
        } catch(FileNotFoundException e) {
            System.out.println("ERROR: Cannot find the file \"repetition.txt\"!");
            e.printStackTrace();
        }
        
        // store the relations
        String line;
        try{
            while ((line = rFile.readLine()) != null) 
                // If a line starts with '#' ignore it as it is a comment
                if (! line.startsWith("#") && ! line.matches("\\s*")) {
                    String[] marker = line.split(",");
                    if (marker[1].trim().matches("[Mm][Aa][Nn][Yy][Rr][Ee][Pp][Ee][Tt][Ii][Tt][Ii][Oo][Nn]"))
                        manyRepRel = marker[0].trim();
                    else 
                        if (marker[1].trim().matches("[Ff][Ee][Ww][Rr][Ee][Pp][Ee][Tt][Ii][Tt][Ii][Oo][Nn]"))
                            fewRepRel = marker[0].trim();
                }
            
            rFile.close();
        } catch(IOException e) {
            System.out.println("ERROR: In reading from \"relations.txt\"!\n" +
                               "It could be due to an ill formed line or a "+
                               "corrupted file!");
            e.printStackTrace();
        }
    }
}
