/*
 * wordStemmerCreator.java
 *
 * Created in 2005
 */

package factory.word_stemmer;

/**
 *
 * @author  Waleed Alsanie
 * @version 0.2 beta
 */
public interface WordStemmerCreator {
    
    public nlp.word_stemmer.ArabicWordStemmer createArabicStemmer ();
}
