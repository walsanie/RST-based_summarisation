/*
 * RelationsHypothesizer.java
 *
 * Created in 2005
 */

package nlp.rst;

import nlp.word_stemmer.ArabicWordStemmer;
import java.util.*;

/**
 *
 * @author  Waleed Alsanie
 * @version 0.2 beta
 */
public class RelationsHypothesizer {
    
    private nlp.text.CharCoding coding;  // charset of the text that this class is applied on
    private Collection relations;      // relations that are extracted by this class
    private Collection complexRelations;
    private Collection binaryRelations;
    private int threshold;    // number of repetition words between two units,
                              // that when satisfied, the relation is Elaboration,
                              // or Joint otherwise.
    // the relation that is used when repetition > threshold
    private String relationManyRepetition; 
    // the relation that is used when repetition < threshold
    private String relationFewRepetition;  
    
    /** Creates a new instance of RelationsHypothesizer */
    public RelationsHypothesizer(nlp.text.CharCoding charset) {
        relations = new LinkedList ();
        binaryRelations = new LinkedList ();
        complexRelations = new LinkedList ();
        coding = charset;
        threshold = 2;
    }
    
    public RelationsHypothesizer(nlp.text.CharCoding charset, int limit) {
        relations = new LinkedList ();
        binaryRelations = new LinkedList ();
        complexRelations = new LinkedList ();
        coding = charset;
        threshold = limit;
    }
    
    public void setBinaryRelations(LinkedList biRel) {
        binaryRelations = biRel;
    }
    
    public Collection getHypothesizedRelations() {
        return relations;
    }
    
    public void setComplexRelations(LinkedList compRel) {
        complexRelations = compRel;
    }
    
    public int getThreshold() {
        return threshold;
    }
    
    public void setThreshold(int limit) {
        threshold = limit;
    }
    
    public void setRelationsManyRepetition(String rel) {
        relationManyRepetition = rel;
    }
    
    public void setRelationsFewRepetition(String rel) {
        relationFewRepetition = rel;
    }
    
    public void hypothesizeRelationsBasedOnCues(LinkedList elemU, 
            Collection cues) {
        
        Iterator cuesIt = cues.iterator();       // iterate over cues in text
        while (cuesIt.hasNext()) {            
            CuePhrase cue = (CuePhrase) cuesIt.next();
            // if type of the relatio is complex
            if (complexRelation (cue.getRelation()))       
                handleComplexRelation(cue, elemU);       // handle it
            else                         // handle binary relations otherwise
                handleBinaryRelation(cue, elemU);   
        }
        hypothesizeRelationsBasedOnRepetition(elemU, relations);
    }
    
    public Collection hypothesizeRelationsBasedOnRepetition(LinkedList elemUnits, 
            Collection oldRel) {
           
        //iterator over elemUnits
        ListIterator elemUnitsIterator = elemUnits.listIterator();   
        ElementaryUnit unit1, unit2;
        if (elemUnitsIterator.hasNext())                 // get first unit
            unit1 = (ElementaryUnit) elemUnitsIterator.next();
        else
            return null;                      // return if empty.

        int unit1Number;
        int unit2Number;

        while (elemUnitsIterator.hasNext()) {
            String rel_name = relationFewRepetition;
            unit2 = (ElementaryUnit) elemUnitsIterator.next();       // unit2 == other unit
            unit1Number = unit1.getNumber(); 
            unit2Number = unit2.getNumber();
            Iterator relIterator = oldRel.iterator();        // relations iterator
            boolean relationExists = false;              // suppose there is no relation between
            while (relIterator.hasNext()) {              // unit1 and unit2
                RhetRel tempRel = (RhetRel) relIterator.next(); // get relation
                if (tempRel instanceof ComplexRhetRel)        // if complex relation then ignore it
                     continue;
                BinaryRhetRel relation = (BinaryRhetRel) tempRel;
                if ((relation.getFirstSpan() == unit1Number &&       // if there it is a relations
                    relation.getSecondSpan() == unit2Number) ||      // between unit1 and unit2
                    (relation.getFirstSpan() == unit2Number && 
                    relation.getSecondSpan() == unit1Number)) {
                       relationExists = true;
                       break;
                }
            }
            if (relationExists) {   // if there is a relation between unit1 and unit2
                unit1 = unit2;       // check succeeding units
                continue;
            }
            else    // otherwise, use word repetition to determine the relation
                if (wordRepetition(unit1.getContent(), unit2.getContent()) >= threshold)   
                    rel_name = relationManyRepetition;   // if redandunts words > threshold
                                                         // then apply the designated relation
            BinaryRhetRel new_rel = new BinaryRhetRel(rel_name, unit2Number, unit1Number);
            relations.add(new_rel);
            applyTransitivity(new LinkedList(relations), new_rel);

            unit1 = unit2;       // check succeeding units
        }
        return relations;
    }
    
    public void hypothesizeRelationsOnChunkUnits(LinkedList units1, LinkedList units2) {
        
        String fstUnit = new String();         // create a string that holds first chunk
        String sndUnit = new String();         // create a string that holds second chunk
        
        ListIterator it = units1.listIterator();     // iterate over first chunk units.
        int fstUnitStart = ((ElementaryUnit)it.next()).getNumber();     // get start unit number
        it.previous();
        while (it.hasNext())                    // collect the units in one string
            fstUnit = fstUnit.concat(((ElementaryUnit) it.next()).getContent());
        int fstUnitEnd = ((ElementaryUnit)it.previous()).getNumber();  // get end unit number
        
        it = units2.listIterator();             // iterate over second chunk units.
        int sndUnitStart = ((ElementaryUnit)it.next()).getNumber();     // get start unit number
        it.previous();
        while (it.hasNext())                    // collect the units in one string
            sndUnit = sndUnit.concat(((ElementaryUnit) it.next()).getContent());
        int sndUnitEnd = ((ElementaryUnit)it.previous()).getNumber();       // get end unit number
        
        String relName = relationFewRepetition;                // check word cooccurence
        if (wordRepetition(fstUnit, sndUnit) >= threshold)   // and set the relation
            relName = relationManyRepetition;                 // accordingly   
                                                            // then add the relation
        relations.add(new ComplexRhetRel (relName, fstUnitStart, fstUnitEnd, 
                                                 sndUnitStart, sndUnitEnd));
    }
    
    private boolean complexRelation(String rel) {
        Iterator i = complexRelations.iterator();
        while (i.hasNext()) {
            String compRel = (String) i.next();
            if (compRel.equalsIgnoreCase(rel))
                return true;
        }
        return false;
    }
    
    private void handleComplexRelation(CuePhrase cue, Collection elemU) {
        
        if (cue.getWhereToLink() == 'B')    // if cue linkes with units before
            if (cue.getUnitNumber() != 1)  // and this unit is not the first one.
                relations.add(new ComplexRhetRel(cue.getRelation(),  1, 
                                                cue.getUnitNumber() - 1, 
                                                cue.getUnitNumber(), 
                                                cue.getUnitNumber()));
            else                            // if the unit is the first one in text
                relations.add(new ComplexRhetRel(cue.getRelation(), 2, 
                                                 elemU.size(), 1, 1));
        else      // if cue linkes with units after and the unit is not the last one
            if (cue.getUnitNumber() != (elemU.size() +          
                ((ElementaryUnit)elemU.iterator().next()).getNumber() -1))
                relations.add (new ComplexRhetRel (cue.getRelation(), 
                                                 cue.getUnitNumber() + 1, 
                                                 elemU.size(),
                                                 cue.getUnitNumber(), 
                                                 cue.getUnitNumber()));
            else                            // if the unit is the last one. 
                relations.add (new ComplexRhetRel (cue.getRelation(), 
                                                 1, 
                                                 cue.getUnitNumber() - 1,
                                                 cue.getUnitNumber(),
                                                 cue.getUnitNumber()));
    }
    
    private void handleBinaryRelation (CuePhrase cue, Collection elemU) {
        
        BinaryRhetRel rel = null;
        if (cue.getWhereToLink() == 'B')   // if cue linkes with unit before
            if (cue.getUnitNumber() != 1)  // and the unit is not the first one
                if (cue.getStatus().equalsIgnoreCase("N_S"))    // first unit is N, second is S
                    rel = new BinaryRhetRel(cue.getRelation(), 
                                            cue.getUnitNumber() - 1, 
                                            cue.getUnitNumber());
                else                               // first is S, second is N
                    rel = new BinaryRhetRel(cue.getRelation(),
                                            cue.getUnitNumber(), 
                                            cue.getUnitNumber() - 1);
            else                                  // unit is the first one
                if (cue.getStatus().equalsIgnoreCase("N_S"))
                    rel = new BinaryRhetRel(cue.getRelation(), 
                                            cue.getUnitNumber() + 1, 
                                            cue.getUnitNumber());
                else                            // first is S, second is N
                    rel = new BinaryRhetRel(cue.getRelation(),
                                            cue.getUnitNumber(), 
                                            cue.getUnitNumber() + 1);
        else   // if cue linkes with unit after and the unit is not the last one
            if (cue.getUnitNumber() != (elemU.size() +     // 
                ((ElementaryUnit) elemU.iterator().next()).getNumber() -1 ))  
                if (cue.getStatus().equalsIgnoreCase("N_S"))   // first unit is N, second is S
                    rel = new BinaryRhetRel(cue.getRelation(), 
                                            cue.getUnitNumber() ,
                                            cue.getUnitNumber() + 1);
                else                                            // first is S, second is N
                    rel = new BinaryRhetRel(cue.getRelation(),
                                            cue.getUnitNumber() + 1,
                                            cue.getUnitNumber());
            else    // the unit is last one first unit is N, second is S
                if (cue.getStatus().equalsIgnoreCase("N_S"))     
                    rel = new BinaryRhetRel(cue.getRelation(), 
                                            cue.getUnitNumber(),
                                            cue.getUnitNumber() - 1);
                else    // first is S, second is N
                    rel = new BinaryRhetRel(cue.getRelation(),
                                            cue.getUnitNumber() - 1,
                                            cue.getUnitNumber());
        
        applyTransitivity(new LinkedList(relations), rel);
        relations.add(rel);
    }
    
    private void applyTransitivity(Collection listOfRel, BinaryRhetRel rel) {
        
        int nucleus = rel.getFirstSpan();
        int satellite = rel.getSecondSpan();
        Iterator it = listOfRel.iterator();        // iterate over relations
        
        while (it.hasNext()) {
            RhetRel tempRel = (RhetRel) it.next();
            // transitivity is applied on binary rel only
            if (tempRel instanceof BinaryRhetRel) {    
		BinaryRhetRel currentRel = (BinaryRhetRel) tempRel;
                if (currentRel.getSecondSpan() == nucleus &&     // if rel (name, N, S)
                    currentRel.getFirstSpan() != satellite)     // and currentRel (name, N1, N)
                    relations.add (new BinaryRhetRel(rel.getRel(),     
                                                     currentRel.getFirstSpan(), 
                                                     satellite));
            }
        }
    }
    
    private int wordRepetition(String text1, String text2) {
                                                 
        // get words from text1 and text2
        String masterString[] = text1.trim().split("\\s+");
        String slaveString[] = text2.trim().split("\\s+");
        int repetition = 0;
        // take each word in text1 and check if it exists in text2
        for (int i =0; i < masterString.length; i++) {      
            String word1 = masterString[i];                 
            for (int j=0; j < slaveString.length; j++) {     
                String word2 = slaveString[j];
                if (similar(word1, word2))
                    repetition++;
            }
        }
        
        return repetition;
    }
    
    private boolean similar(String word1, String word2) {
        
        // create word stemmer
        ArabicWordStemmer arabicStem = (new factory.word_stemmer.            
                                        SimpleArabicWordStemmerCreator ()).
                                        createArabicStemmer();
        word1 = arabicStem.stem(word1, coding);                 // stem both words
        word2 = arabicStem.stem(word2, coding);
        
        if (word1.length() < 3 || word2.length() < 3)  // if length < 3 consider
            return false;                             // one of them -or both
                                                     // as characters not words
        if (word1.equals(word2))              // if they are equal return true
            return true;
        
        if (word1.length()- word2.length() > 1 || // if the difference in lengths
            word2.length()- word1.length() > 1)   // is more than one letter
            return false;                         // consider them as unequal
        
                // if the two stemmed words are equal in first letter
                // and last letter consider them as equal 
        if (word1.charAt(0) == word2.charAt(0) &&
            word1.charAt(word1.length()-1) == word2.charAt(word2.length()-1))
            return true;
        else
            return false;       // otherwise consider them inequal ..
        
    }   
}
