/*
 * ExtRhetRel.java
 *
 * Created in 2005
 */

package nlp.rst;

/**
 * This class defines the extended rhetorical relations.  
 * 
 * @author  Waleed Alsanie
 * @version 0.2 beta
 */
public class ExtRhetRel extends BinaryRhetRel{
    
    private int first_span_end;
    private int second_span_end;
    
    /** Creates a new instance of ExtRhetRel */
    public ExtRhetRel() {
        first_span_end = 0;
        second_span_end = 0;
    }
    
    public ExtRhetRel(String name, int fS, int sS) {
        super (name, fS, sS);
    }
    
    public ExtRhetRel(String name, int fS, int fSe, int sS, int sSe) {
        super (name, fS, sS);
        first_span_end = fSe;
        second_span_end = sSe;
    }
    
    public int getFirstSpanEnd () {
        return first_span_end;
    }
  
    public int getSecondSpanEnd () {
        return second_span_end;
    }   
    
    public void SetFirstSpanEnd (int fS) {
        first_span_end = fS;
    }
  
    public void SetSecondSpanEnd (int sS) {
        second_span_end = sS;
    }
}
