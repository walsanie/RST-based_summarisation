/*
 * Text.java
 *
 * Created in 2005
 */

package nlp.text;

/**
 * Class defining a text. Its properties are content (String) and encoding 
 * (CharEncoding).
 * 
 * @author  Waleed Alsanie
 * @version 0.2 beta
 */
public class Text {
    
    private String content;
    private CharCoding encoding;
    /** Creates a new instance of Text */
    public Text() {
    }
    
    public Text(String body, CharCoding enc) {
        content = body;
        encoding = enc;
    }
    
    public String getText() {
        return content;
    }
    
    public CharCoding getEncoding() {
        return encoding;
    }
    
    public void setText(String tex) {
        content = tex;
    }
    
    public void setEncoding(CharCoding chset) {
        encoding = chset;
    }
}
