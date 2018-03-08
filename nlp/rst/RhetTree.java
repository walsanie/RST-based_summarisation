/*
 * RhetTree.java
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
public class RhetTree {
    
    private String status, type;        //rhet_tree has a status, type
    private Collection promotion;       // set of spans as a promotion set
    private RhetTree left, right;      // two sub-trees. 
    /** Creates a new instance of RhetTree */
    public RhetTree() {
        left= right = null;
    }
    
    public RhetTree(String s, String t, Collection p, 
                   RhetTree l, RhetTree r) {                    
        status = s; type = t; promotion = new HashSet (p);
        left = l; right = r;
    }
    
    public RhetTree(String t, Collection p, 
                    RhetTree l, RhetTree r) {
        type = t; promotion = new HashSet (p);
        left = l; right = r;
    }
    
    public String getType () {                   
        return type;
    }
  
    public String getStatus () {
        return status;
    }
    
    public RhetTree getLeft () {
        return left;
    }

    public RhetTree getRight () {
        return right;
    }

    public Collection getPromotion () {
        return promotion;
    }

    public void setStatus (String s) {    
        status = s;
    }  
    
    public int depth () {
    
    if (left == null && right == null)
      return 0;
    else
      if (left == null)
        return 1 + right.depth ();
      else
        if (right == null)
          return 1 + left.depth ();
        else
          return 1 + Max (left.depth(), right.depth ());
    }
    
    private int Max (int leftDepth, int rightDepth) {
        return (leftDepth > rightDepth ? leftDepth : rightDepth);
    }
    
    public boolean importantNode (Collection P) {
                                      // this moethod to check if a given node
          Iterator i = this.promotion.iterator(), //containg one of the
                   j ;                            // units that belongs
                                                 // to the root promotion      
          Integer iVal;                         // if so, it is an important unit         
          int impVal, PVal;                            

          while (i.hasNext()) {
            iVal = (Integer) i.next ();
            impVal = iVal.intValue();
            j = P.iterator();
            while (j.hasNext ()) {
              iVal = (Integer) j.next();
              PVal = iVal.intValue();
              if (PVal == impVal) {
                return true;
              }
            }
          }
          return false;
    }    
}

