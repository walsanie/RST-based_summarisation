/*
 * ArabicWordStemmer.java
 *
 * Created in 2005
 */

package nlp.word_stemmer;

import nlp.text.*;
/**
 *
 * @author  Waleed Alsanie
 * @version 0.2 beta
 */
public interface ArabicWordStemmer {
    
    /**
     * Stem the word encoding using the given encoding.
     * 
     * @param word: th word to be stemmed.
     * @param charset: the encoding.
     * @return the stem.
     */
    public String stem(String word, CharCoding charset);    
}
