/*
 * RhetRel.java
 *
 * Created in 2005, 
 */

package nlp.rst;

/**
 * This class defines the binary rhetorical relations. The binary rhetorical 
 * relations are those which are defined on two elementary units. 
 * 
 * @author  Waleed Alsanie
 * @version 0.2 beta
 */

public class BinaryRhetRel extends RhetRel {
    
    private int first_span;           // and two span numbers ... 
    private int second_span;
    
    /** Creates a new instance of RhetRel */
    public BinaryRhetRel() {
    }
    
    /** Create a new instance of RhetRel with the parameter */
    public BinaryRhetRel(String name, int fS, int sS) {
        setRel(name); first_span = fS; second_span = sS;
    }
  
    public int getFirstSpan () {
      return first_span;
    }
  
    public int getSecondSpan () {
      return second_span;
    } 
    
    public void setFirstSpan (int fS) {
      first_span = fS;
    }
  
    public void setSecondSpan (int sS) {
      second_span = sS;
    }
    
    public boolean equals (RhetRel rel) {        // overrides the equal method
                                          // of object to check the equality
        if (! (rel instanceof BinaryRhetRel))
            return false;
        BinaryRhetRel R = (BinaryRhetRel) rel;
        return  ((first_span == R.first_span) &&
                 (second_span == R.second_span) &&
                 (super.getRel().equalsIgnoreCase(R.getRel())));
    }
}

