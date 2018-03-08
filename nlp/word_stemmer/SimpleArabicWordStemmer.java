/*
 * SimpleArabicWordStemmer.java
 *
 * Created in 2005
 */

package nlp.word_stemmer;

import nlp.text.*;

/**
 * This is a very simple Arabic word stemmer. There are many more advanced 
 * Arabic stemmers that can be used instead of this. This is just meant to 
 * experimenting. 
 * 
 * @author  Waleed Alsanie
 * @version 0.2 beta
 */

public class SimpleArabicWordStemmer implements ArabicWordStemmer{
           
    /** Creates a new instance of SimpleArabicWordStemmer */
    public SimpleArabicWordStemmer() {
    }
    
    public String stem(String word, CharCoding charset) {
        
        if (word.length() < 3)      // if word < 3 length, 
            return word;            //it cann't be stemmed, consider it as Harf
        
        char Al[] = {charset.getAleph(), charset.getLam()};  
        // ignore Haa Al-Mokatabah
        char Haa[] = {charset.getHaa(), charset.getAleph()};  
        // ignore Alef Al-Ethneen -with noon-
        char Aan[] = {charset.getAleph(), charset.getNoon()}; 
        // ignore Aat with Jamaa Muanath Salem
        char Aat[] = {charset.getAleph(), charset.getTaa()};  
        // ignore waw Al-Jamaa'ah -with noon-
        char Won[] = {charset.getWaw(), charset.getNoon()};  
        // ignore Jamaa Muthaker Salem Manssob bel-Yaa
        char Een[] = {charset.getYaa(), charset.getNoon()};  
        // ignore Haa Al-Qaaeb -with Yaa-
        //char Eeh[] = {0xed, 0xe5};  
        // ignore Taa Al-Taaneeth -with Yaa
        //char Eat[] = {0xed, 0xc9};   
        // ignore Waw Al-Jamma with Alef
        char Waa[] = {charset.getWaw(), charset.getAleph()}; 
        // ignore Haa Al-Mukatabeen -with meem
        char Hom[] = {charset.getHaa(), charset.getMeem()};  
        // ignore Haa Al-Qaaeb -alone
        char Ha = charset.getHaa();         
        // ignore Taa Al-Tanneth -alone
        char Taa_Marbuta= charset.getTaaMarboota();  
        // ignore Yaa Al-Mutaklem
        //char Yaa = 0xed;                                         
        
        // Arabic 'Al' to be ignored
        if (word.charAt(0) == Al[0] && word.charAt(1) == Al[1])
            word = word.substring(2, word.length());               
        // if word < 2 length after ignoring 'Al',
        if (word.length() < 2)       
            return word;            //return it without Al
        
        char doubleSuffixes [][] = {Haa, Aan, Aat, Won, Een, Waa};
        char singleSuffix [] = {Ha, Taa_Marbuta};
        
        for (int i=0; i < doubleSuffixes.length; i++) {
            if (word.charAt(word.length()-1) == doubleSuffixes[i][1] &&
                word.charAt(word.length()-2) == doubleSuffixes[i][0]) {
                    word = word.substring(0, word.length()-2);
                    return word;          // if exist return it and quit method
            }
        }                   // check if single suffixes exist or not
        for (int i=0; i < singleSuffix.length; i++) {
            if (word.charAt(word.length()-1) == singleSuffix[i]) {
                    word = word.substring(0, word.length()-1);
                    return word;       // if exist return it and quit method
            }
        }
                                    // if niether double suffixes nor a single   
        return word;               //exists return the oringinal word.
    }    
}
