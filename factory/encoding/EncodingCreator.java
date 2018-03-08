/*
 * encodingCreator.java
 *
 * Created in 2005
 */

package factory.encoding;

import java.io.UnsupportedEncodingException;
import nlp.text.CharCoding;

/**
 * The interface for char encoding.
 * 
 * @author  Waleed Alsanie
 * @version 0.2 beta
 */
public interface EncodingCreator {
    
    /**
     * An interface method to create an Arabic CharEncoding object based on 
     * the given name.
     * 
     * @param charset: The name of the encoding, e.g. "UTF8".
     * @throws UnsupportedEncodingException in case the encoding name is unknown.
     */
    public CharCoding createEncoding (String charset) 
            throws UnsupportedEncodingException;
    
}
