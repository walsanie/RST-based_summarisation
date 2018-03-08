/*
 * ArabicEncodingCreator.java
 *
 * Created in 2005
 */

package factory.encoding;

import java.io.UnsupportedEncodingException;

/**
 * This class implements EncodingCreator to create an Arabic CharEncoding
 * 
 * @author  Waleed Alsanie
 * @version 0.2 beta
 */
public class ArabicEncodingCreator implements EncodingCreator {
    
    /** Creates a new instance of ArabicEncodingCreator */
    public ArabicEncodingCreator() {
    }
    
    /**
     * Creates an Arabic CharEncoding object based on the given name.
     * 
     * @param name: The name of the encoding, e.g. "UTF8".
     * @throws UnsupportedEncodingException in case the encoding name is unknown.
     */
    @Override
    public nlp.text.CharCoding createEncoding(String name) 
            throws UnsupportedEncodingException {
        if (name.matches("[Uu][Tt][Ff]-?8"))
            return new nlp.text.Utf8();
        else
            throw new UnsupportedEncodingException();
    }    
    
    
}
