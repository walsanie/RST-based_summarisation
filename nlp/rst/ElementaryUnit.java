/*
 * ElementaryUnit.java
 *
 * Created in 2005
 */

package nlp.rst;

/**
 * Defines the elementary units. These are the basic units on which the 
 * rhetorical relations are defined. 
 * 
 * @author  Waleed Alsanie
 * @version 0.2 beta
 */
public class ElementaryUnit {
    
    private String content;     // Its content.
    private int number;         // Its number in the text.
    
    /** Creates a new instance of ElementaryUnit */
    public ElementaryUnit() {
    }
    
    public ElementaryUnit (String con, int num) {
        content = con; number = num;
    }
    
    public String getContent () {
        return content;
    }
    
    public int getNumber () {
        return number;
    }
    
    public void setContent (String con) {
        content = con;
    }
    
    public void setNumber (int num) {
        number = num;
    }
    
}
