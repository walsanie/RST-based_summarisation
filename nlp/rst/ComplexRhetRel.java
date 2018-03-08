/*
 * ComplexRhetRel.java
 *
 * Created 2005
 */

package nlp.rst;

/**
 * This class defines the complex rhetorical relations. The complex rhetorical 
 * relations are those which are defined on two sets of consecutive elementary 
 * units. 
 * 
 * @author  Waleed Alsanie
 * @version 0.2 beta
 */
public class ComplexRhetRel extends RhetRel{
    
    private int first_span_start;
    private int first_span_end;
    private int second_span_start;
    private int second_span_end;
    
    /** Creates a new instance of ExtRhetRel */
    public ComplexRhetRel() {
    }
    
    public ComplexRhetRel(String name, int fS, int fSe, int sS, int sSe) {
        setRel (name);
        first_span_start = fS;
        first_span_end = fSe;
        second_span_start = sS;
        second_span_end = sSe;
    }
    
    public int getFirstSpanStart () {
        return first_span_start;
    }
     
    public int getFirstSpanEnd () {
        return first_span_end;
    }
  
    public int getSecondSpanStart () {
        return second_span_start;
    }
    
    public int getSecondSpanEnd () {
        return second_span_end;
    }
    
    public void setFirstSpanStart (int fS) {
        first_span_start = fS;
    }
    
    public void setFirstSpanEnd (int fSe) {
        first_span_end = fSe;
    }
  
    public void setSecondSpanStart (int sS) {
        second_span_start = sS;
    }
    
    public void setSecondSpanEnd (int sSe) {
        second_span_end = sSe;
    }
    
    public boolean equals (RhetRel r) {
        if (! (r instanceof ComplexRhetRel))
            return false;
        ComplexRhetRel rel = (ComplexRhetRel) r;
        return (first_span_start == rel.getFirstSpanStart() && 
                first_span_end == rel.getFirstSpanEnd() && 
                second_span_start == rel.getSecondSpanStart() &&
                second_span_end == rel.getSecondSpanEnd() &&
                super.getRel().equalsIgnoreCase(rel.getRel()));
    }
}
