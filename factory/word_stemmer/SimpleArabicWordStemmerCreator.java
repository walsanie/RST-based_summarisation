/*
 * SimpleArabicWordStemmerCreator.java
 *
 * Created 2005
 */

package factory.word_stemmer;

/**
 *
 * @author  Waleed Alsanie
 * @version 0.2 beta
 */
public class SimpleArabicWordStemmerCreator implements WordStemmerCreator {
    
    /** Creates a new instance of SimpleArabicWordStemmerCreator */
    public SimpleArabicWordStemmerCreator() {
    }
    @Override
    public nlp.word_stemmer.ArabicWordStemmer createArabicStemmer() {
        
        return new nlp.word_stemmer.SimpleArabicWordStemmer ();
    }
    
}
