/*
 * TextualSpan.java
 *
 * Created in 2005
 */

package nlp.rst;

import java.util.*;

/**
 *
 * @author  Waleed Alsanie
 * @version 0.2 beta
 */
public class TextualSpan {
    
    private int StartSpan, EndSpan;
    private RhetTree tree;
    private Collection RR;
    /** Creates new TextualSpan */
    public TextualSpan() {
      StartSpan = EndSpan = 0;
      tree = null; RR = null;
    }
  
    public TextualSpan(int Start, int End, RhetTree Rtree, Collection R) {
      SetStartSpan (Start); SetEndSpan (End);
      SetTree (Rtree); SetRR (R);    
    }
  
    public int getStartSpan () {
      return StartSpan;
    }
  
    public int getEndSpan () {
      return EndSpan;
    }
  
    public RhetTree getTree () {
      return tree;
    }
  
    public Collection getRR () {
      return RR;
    }
  
    public void SetStartSpan (int S) {
      StartSpan = S;
    }
  
    public void SetEndSpan (int E) {
      EndSpan = E;
    }
  
    public void SetTree (RhetTree T) {
      tree = T;
    }
  
    public void SetRR (Collection R) {
      RR = R;
    }   
}