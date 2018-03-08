/*
 * CuePhrase.java
 *
 * Created in 2005
 */

package nlp.rst;

/**
 * A class defining the cue phrases.
 * 
 * @author  Waleed Alsanie
 * @version 0.2 beta
 */
public class CuePhrase {
    
    private String regex;           // the regular expression of the cue phrase
    private String name;            // its name
    private String matched;         // The matched form of the regex in the text
    private String action;          // the action is signals to determine the 
                                    // elementary untis.
    private String position;        // Its position in the text.
    private int index;              // Its index in the text.
    private String relation;        // the relation it is associated with.
    private String status;          // either N_S, S_N or N_N
    private char whereTolink;       // does it link with a preceding unit or
                                    // a succeeding unit.
    private int unitNumber;         // in which unit number it is found
    
    /** Creates a new instance of CuePhrase */
    public CuePhrase() {
    }
    
    public CuePhrase (String reg, String n, String m, String act, int i) {
        
        regex = reg;
        name = n;
        matched = m;
        action = act;
        index = i;
    }
    
    public String getRegex () {
        return regex;
    }
    
    public String getName () {
        return name; 
    }
    
    public String getMatched () {
        return matched;
    }
    
    public String getAction () {
        return action;
    }
    
    public int getIndex () {
        return index;
    }
    
    public String getPosition () {
        return position;
    }
    
    public String getStatus () {
        return status;
    }
    
    public String getRelation () {
        return relation;
    }
    
    public char getWhereToLink () {
        return whereTolink;
    }
    
    public int getUnitNumber () {
        return unitNumber;
    }
    
    public void setRegex (String reg) {
        regex = reg;
    }
    
    public void setName (String n) {
        name = n; 
    }
    
    public void setMatched (String m) {
        matched = m;
    }
    
    public void setAction (String act) {
        action = act;
    }
    
    public void setStatus (String S) {
        status = S ;
    }
    
    public void setRelation (String rel) {
        relation = rel;
    }
    
    public void setIndex (int indx) {
        index = indx;
    }
    
    public void setPosition (String pos) {
        position = pos;
    }
    
    public void setWhereToLink (char wtl) {
        whereTolink = wtl;
    }
    
    public void setUnitNumber (int un) {
        unitNumber = un;
    }
    
    public Object clone () {
        CuePhrase tempCue = new CuePhrase ();
        tempCue.setAction(action);
        tempCue.setIndex(index);
        tempCue.setMatched(matched);
        tempCue.setName(name);
        tempCue.setPosition(position);
        tempCue.setRegex(regex);
        tempCue.setRelation(relation);
        tempCue.setStatus(status);
        return tempCue;
    }
    
}
