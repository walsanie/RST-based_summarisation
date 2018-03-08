/*
 * RhetRel.java
 *
 * Created in 2005
 */

package nlp.rst;

/**
 *
 * @author  Waleed alsanie
 * @version 0.2 beta
 */
public abstract class RhetRel {
    
    private String rel_name;
    
    public String getRel () {
        return rel_name;
    }
    
    public void setRel (String rel) {
        rel_name = rel;
    }
    
    public abstract boolean equals (RhetRel rel);
    
}
