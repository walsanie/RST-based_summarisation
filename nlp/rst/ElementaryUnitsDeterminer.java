/*
 * ElementaryUnitsDeterminer.java
 *
 * Created in 2005
 */

package nlp.rst;

import nlp.text.*;
import java.util.*;
import java.util.regex.*;
import java.io.*;
import java.nio.charset.*;

/**
 * This class is responsible for determining the elementary units in the given 
 * text. It determines these units based on the actions associated with the 
 * cue phrases.
 * 
 * @author  Waleed Alsanie
 * @version 0.2 beta
 */
public class ElementaryUnitsDeterminer {
    
    private Collection elementaryUnits;     // saves the extracted elem. units
    private Collection functioningCues;     // cue phrase that are extracted from the text, and
                                            // signal rehtorical relations.
    private Collection allCues;             // Arabic text markers.
    private int numberOfUnits;              // private counter of units.
    
    /** Creates a new instance of ElementaryUnitsDeterminer */
    public ElementaryUnitsDeterminer(Collection cues) {
        elementaryUnits = new LinkedList();
        functioningCues = new LinkedList();
        allCues = new LinkedList(cues);
        numberOfUnits = 0;
    }
    
    private void finlizeElementaryUnitsDeterminer(Text text) {
        
        Iterator elemIt = elementaryUnits.iterator();
        int startIndex = 0;
        // iterate over the prev. element to
        // determine the index of the cue phrase
        while (elemIt.hasNext()) {       
            String str = ((ElementaryUnit)elemIt.next()).getContent();  
            startIndex += str.length();
        }
        ElementaryUnit eu;
        if (startIndex < text.getText().length()) {
            eu = new ElementaryUnit(
                    text.getText().substring(startIndex, text.getText().length()), 
                    ++numberOfUnits);
            elementaryUnits.add(eu);
        }
    }
    
    private LinkedList extractCuePhrases(Text text) {
        
        Collection extractedCuesTreeSet = new TreeSet(new MarkerComparator());
        
        CuePhrase cue;                 
        if (allCues.isEmpty()) 
            return null;
                                                        
        Iterator i = allCues.iterator();
        if (text != null)  
            while (i.hasNext())  {     // Go over cues
                cue = (CuePhrase) i.next();   // get regex of cue
                Pattern p = Pattern.compile(cue.getRegex());
                                                     
                Matcher m = p.matcher(text.getText()); // match para to scan it.
                while (m.find()) {                             
                    int start = m.start();
                    // In case the cue regex has spaces in the beginning
                    // Move the index of the start of the match over those 
                    // spaces to reach the beginning of the first letter of the 
                    // cue.
                    for (int ii=0; String.valueOf(m.group(1).charAt(ii)).matches("\\s") ; ii++)
                        start ++;      
                    CuePhrase cueP = (CuePhrase) cue.clone ();
                    cueP.setIndex(start);
                    cueP.setMatched(m.group(1).trim());
                    extractedCuesTreeSet.add(cueP);
                }
            } 
        
        LinkedList extractedCues = new LinkedList (extractedCuesTreeSet);
        return extractedCues;
    }
    
    private boolean isPrecededByAndOr(Text tex, ListIterator i) {
        
        CuePhrase c = (CuePhrase) i.next();
        i.previous();                 // bypass the same element accessed above
        if (! i.hasPrevious())
            return false;
        CuePhrase c1 = (CuePhrase) i.previous();
        if (! c1.getName().equalsIgnoreCase(tex.getEncoding().getAND())
            || ! c1.getName().equalsIgnoreCase(tex.getEncoding().getOR()))
            return false;
        String sub = tex.getText().substring(
                c1.getIndex(), c.getIndex() + c.getMatched().length()
        );
        String[] words = sub.trim().split("\\s+");
        if (! words[0].isEmpty())  // if seprated by another word
            return false;
        return true;
    }
    
    private boolean isPrecededByAnotherCue(Text tex, ListIterator i) {
        
        CuePhrase c = (CuePhrase) i.next ();
        i.previous();                 // bypass the same element accessed above
        if (! i.hasPrevious())
            return false;
        CuePhrase c1 = (CuePhrase) i.previous();
        String sub = tex.getText().substring(
                c1.getIndex(), c.getIndex() + c.getMatched().length()
        );
        String[] words = sub.trim().split("\\s+");
        if (! words[0].isEmpty())  // if seprated by another word
            return false;
        return true;
    }
    
    private int getBoundary(Text tex, ListIterator i) {
        
        CuePhrase c = (CuePhrase) i.next(); 
        i.previous();             // return the iterator one step to point to c
        if (! i.hasPrevious())
            return c.getIndex();
        
        CuePhrase c1 = (CuePhrase) i.previous();
        while (true) {                              
            // get substring from beginning of the cue and the 
            // beginning of the previouse cue
            String sub = tex.getText().substring(
                    c1.getIndex(), c.getIndex() + c.getMatched().length()
            );
            // get words in the substring
            String[] words = sub.trim().split("\\s+");   
            if (words.length > 2)             //check if there is 
                    return c.getIndex();      // a word between the two cues
            c = c1;                           // then return the cue index as boundary
            if (! i.hasPrevious())            // otherwise,
                return c1.getIndex();          // skip the preceding cue
            
            c1 = (CuePhrase) i.previous();    // get the next cue and iterate again
        }
    }
    
    private boolean isFirstWordInText(Text text, Iterator cueRef) {
        
        CuePhrase cue = (CuePhrase) cueRef.next();
        if (cue.getIndex() == 0)
            return true;
        else {
            // In case the cue's index is not 0 but it is the first word in 
            // the text because of some spaces before it.
            String[] words = text.getText().substring(0, cue.getIndex()).
                             trim().split("\\s+");       
            if (! words[0].isEmpty())            // if a word exists before cue
                    return false;                // return false
            return true;
        }
    }
    
    private void bypassFollowingCues(Text tex, ListIterator i) {
        
        CuePhrase c = (CuePhrase) i.next();             // get cue.
        if (! i.hasNext())
            return;                                 
        CuePhrase c1 = (CuePhrase) i.next ();           // get next cue
        while (true) {                          
            // get substring from cue and the next cue
            String sub = tex.getText().substring(
                    c.getIndex(), c1.getIndex() + c1.getMatched().length()
            );
            // get words in the substring
            String[] words = sub.trim().split("\\s+");  
            //check if there is a word between the two cues
            // then return
            if (! words[0].isEmpty())   // if only an empty string is resulting
                return;                 // from the split, then no words
                                       
            c = c1;                     // otherwise,
            i.remove();                 // skip the following cue
            if (! i.hasNext())                          
                return;
            c1 = (CuePhrase) i.next();                  // get the next cue and iterate again
        }
    }

    // add a unit boundary before the cue phrase.
    private void handleNormalAction(Text text, ListIterator i, LinkedList ll) {
                
        CuePhrase cue = (CuePhrase) i.next();   // back to the intended cue.
        ListIterator tempI = ll.listIterator(i.nextIndex()-1);
        if (elementaryUnits.size() == 0)        // No elementary unit has been added
            elementaryUnits.add(
                    new ElementaryUnit(
                            text.getText().substring(0, getBoundary(text, tempI)), 
                            ++numberOfUnits
                    )
            );
        else {
            Iterator elemIt = elementaryUnits.iterator();
            int startIndex = 0;
            while (elemIt.hasNext()) {       // iterate over the prev. element to
                // determine the index of the cue phrase
                String str = ((ElementaryUnit) elemIt.next()).getContent();  
                startIndex += str.length();
            }
            // get the boundary of the cue having Normal action
            int boundary = getBoundary(text, tempI);      
            if (startIndex < boundary)    // not to add empty string
                elementaryUnits.add(
                        new ElementaryUnit(
                                text.getText().substring(startIndex, boundary), 
                                ++numberOfUnits
                        )
                );
        }
        tempI = ll.listIterator(i.nextIndex()-1);        
        bypassFollowingCues(text, tempI);
    }
    
    private void handleCommaAction(Text text, ListIterator i, LinkedList ll) {
       
        int startIndex;
        if (elementaryUnits.size() == 0) {           // if first element in text
            startIndex = 0;                         // start from the begining
            i.next ();                   // access the cue phrase, so that i.previous () will work
        }
        else                                        // else, start from cue phrase.
            startIndex = ((CuePhrase) i.next()).getIndex();
        
        String clauses[] = text.getText().
                                substring(startIndex).
                                split(text.getEncoding().getPuncRegex());
        //CuePhrase funcCue = (CuePhrase) ((CuePhrase) i.previous()).clone();
        if (clauses.length == 0)
            elementaryUnits.add(
                    new ElementaryUnit(
                            text.getText().substring(
                                    ((CuePhrase) i.next()).getIndex()
                            ), 
                            ++numberOfUnits
                    )
            );
        else {
            //if there is a punctuation
            if (clauses[0].length() < text.getText().substring(startIndex).length()) 
                // to add the punctuation to the end of the unit.
                clauses[0] = clauses[0].concat(
                        String.valueOf(
                                text.getText().substring(startIndex).charAt(
                                        clauses[0].length()
                                )
                        )
                );      
            // then add the unit
            elementaryUnits.add(new ElementaryUnit(clauses[0], ++numberOfUnits));  
        }                                                               
    }
    
    /*  add a unit boundary
        before the cue phrase and another unit boundary
        after the first occurrence of a comma, semicolon
        or end of sentence. If a comma is met and
        followed by the cue phrases "And" or "Or", 
        add a unit boundary after the next
        occurrence of the three markers.
    */
    private void handleNormaThenCommaAction(Text text, ListIterator i, 
                                            LinkedList ll) {
       
        int counter = i.nextIndex();
        ListIterator tempI = ll.listIterator(i.nextIndex());
        // if the cue is the first
        // word in the text, then pass normal action
        if (! isFirstWordInText(text, tempI))            
            handleNormalAction (text, i, ll);    
        tempI = ll.listIterator(counter);
        handleCommaAction (text, tempI, ll);
    }
    
    /*  Same as the action normal if the cue
        phrase is not preceded by another cue phrase,
        action normal then comma is applied otherwise.
    */
    private void handleDualAction(Text text, ListIterator i, LinkedList ll) {
        
        int counter = i.nextIndex();
        CuePhrase cue = (CuePhrase) i.next();               // get cue phrase
        i.previous();                          // return the iterator back to its previouse status
        if (isPrecededByAnotherCue (text, i)) {         // if preceded by another cue
            ListIterator tempI = ll.listIterator(counter);  // apply action normal then comma
            handleNormaThenCommaAction (text, tempI, ll);
            cue.setUnitNumber(numberOfUnits);      // add the cue phrase to the 
            cue.setWhereToLink('A');                        // functioning cue phrases
            cue.setPosition("B");                     // set cue position as begining
            cue.setStatus("S_N");                   // set status as S then N
            functioningCues.add(cue);               // indicating that it connects with later unit
        }
        else {
            ListIterator tempI = ll.listIterator(counter);
            if (isFirstWordInText(text, tempI)) {          // if first word in text
                ListIterator it = ll.listIterator(counter);
                handleCommaAction (text, it, ll);               // apply comma only
                cue.setUnitNumber(numberOfUnits);      // add the cue phrase to the 
                cue.setWhereToLink('A');                        // functioning cue phrases
                cue.setPosition("B");               // set cue position as begining
                cue.setStatus("S_N");               // set status as S then N
                functioningCues.add(cue);          // indicating that it connects with later unit
            }
            else {
                ListIterator it = ll.listIterator(counter);     // otherwise apply normal
                handleNormalAction (text, it, ll);
                cue.setUnitNumber(numberOfUnits + 1);    // add the cue phrase to the
                cue.setWhereToLink('B');                    // functioning cue phrases
                cue.setPosition("M");                   // set cue position as middle
                cue.setStatus("N_S");                   // set status as N then S
                functioningCues.add(cue);            // indicating that it connects with pre. unit
            }
        }
    }
    
    /* add a unit boundary after the cue phrase */
    private void handleEndAction(Text text, ListIterator i) {
        
        CuePhrase cue = (CuePhrase) i.next();   // back to the intended cue.
        if (elementaryUnits.size() == 0)        // first element in the text
            elementaryUnits.add(
                    new ElementaryUnit(
                            text.getText().substring(
                                    0, 
                                    cue.getIndex() + cue.getMatched().length()
                            ), ++numberOfUnits
                    )
            );
        else {            //cue.index + MatchedCue.length will give the index of the end
            Iterator elemIt = elementaryUnits.iterator();
            int startIndex = 0;
            while (elemIt.hasNext()) {           // iterate over the prev. element to
                // determine the index of the cue phrase
                String str = ((ElementaryUnit)elemIt.next()).getContent(); 
                startIndex += str.length();
            }
            if (startIndex < cue.getIndex())
                elementaryUnits.add(
                        new ElementaryUnit(
                                text.getText().substring(
                                        startIndex, 
                                        cue.getIndex() + cue.getMatched().length()
                                ), ++numberOfUnits
                        )
                );
        }
    }
    
    public Collection getFunctioningCues() {
        return functioningCues;
    }
    
    public int getNumberOfUnits() {
        return numberOfUnits;
    }
    
    public void setNumberOfUnits(int num) {
        numberOfUnits = num;
    }
    
    public void resetElementaryUnits() {
        elementaryUnits = new LinkedList();
    }
    
    public void resetCuePhrases() {
        functioningCues = new LinkedList();
    }
    
    public Collection determineElementaryUnits(Text text) {
        
        LinkedList LL = extractCuePhrases(text);
        ListIterator i = LL.listIterator();
        while (i.hasNext()) {
            CuePhrase cue = (CuePhrase) i.next();
            int counter = i.nextIndex();        //save the next index ..
            ListIterator paramIt = LL.listIterator(i.nextIndex()-1);
            
            i = null;       // because LL might be modified, 
                            //so we don't get ConcurrentModificationException
            if (cue.getAction().equalsIgnoreCase("Normal")) {
                ListIterator tempI = LL.listIterator(paramIt.nextIndex());
                // if the cue is not the first
                // word in 'text', then handle normal action
                if (! isFirstWordInText(text, tempI)) {
                    handleNormalAction(text, paramIt, LL);
                    cue.setUnitNumber(numberOfUnits + 1); 
                    // add the cue phrase to the functioning cue phrases
                    // indicating that it connects with a pre. unit
                    cue.setWhereToLink('B');                    
                    functioningCues.add(cue);       
                }
            }
            else
                if (cue.getAction().equalsIgnoreCase("Normal_Then_Comma")) {
                    handleNormaThenCommaAction(text, paramIt, LL);
                    cue.setUnitNumber(numberOfUnits);
                    // indicating that it connects with a later unit
                    cue.setWhereToLink('A');        
                    functioningCues.add(cue);
                }
                else
                    if (cue.getAction().equalsIgnoreCase("Dual"))
                        handleDualAction(text, paramIt, LL);
                    else
                        if (cue.getAction().equalsIgnoreCase("End")) {
                            handleEndAction(text, paramIt);
                            cue.setUnitNumber(numberOfUnits);
                            // indicating that it connects with a pre. unit
                            cue.setWhereToLink('B');       
                            functioningCues.add(cue);
                        }
            i = LL.listIterator(counter);           // return iterator back .. 
        }
        finlizeElementaryUnitsDeterminer(text);
        return elementaryUnits;
    }
    
    
    private class MarkerComparator implements Comparator {
    
        /** Creates a new instance of MarkerComparator */
        public MarkerComparator() {
        }
        @Override
        public int compare(Object o1, Object o2) {

            CuePhrase m1 = (CuePhrase) o1;
            CuePhrase m2 = (CuePhrase) o2;

            if (m1.getIndex() < m2.getIndex())
                return -1;
            else
                if (m1.getIndex() == m2.getIndex())
                    return 0;
                else
                    return 1;
        }

    } 
    
    /*
    public static void main (String args[]) {
        
        try{
            FileReader reader = new FileReader ("somewhere");
            FileWriter writer = new FileWriter ("somewhere");           
            char[] buffer = new char [2000];
            int count = reader.read(buffer); 
            DataLoader dl = new DataLoader("/home/walsanie/Desktop/test_summarisation/new");
            dl.loadCuePhrases();
            String para = new String (buffer, 0, count);
            String state[] = para.split("\\.");
            writer.write("|");
            int unitNumber = 1;
            for (int i=0; i < state.length; i++) {
                Text text = new Text(state[i].concat("."), new Utf8());
                ElementaryUnitsDeterminer EUD = new ElementaryUnitsDeterminer(dl.getCuesPhrases());
                Collection ll = EUD.determineElementaryUnits(text);
                Iterator it = ll.iterator();
                while (it.hasNext()) { 
                    String cc = ((ElementaryUnit)it.next()).getContent();
                    writer.write(cc);
                    writer.write(String.valueOf(unitNumber++) + "\n");
                }
            }
            reader.close();
            writer.close();
        }
        catch (Exception e) {
            e.getMessage();
            e.printStackTrace();
        }
    }
    */
}
