/*
 * MostBalancedTreeSelector.java
 *
 * Created in 2005
 */

package nlp.rst;

import java.util.*;

/**
 * Selects the most balanced rhetorical tree.
 * 
 * @author  Waleed Alsanie
 * @version 0.2 beta
 */
public class MostBalancedTreeSelector implements TreeSelector {
    
    /** Creates a new instance of MostBalancedTreeSelector */
    public MostBalancedTreeSelector() {
    }
    
    private int getSkewedToRightWieght (RhetTree tree) {
        
        if (tree == null)
            return 0;
        else
            if (tree.getLeft() == null && tree.getRight() == null)
                return 0;
            else
                return tree.getRight().depth() - tree.getLeft().depth() + 
                       getSkewedToRightWieght (tree.getLeft()) + 
                       getSkewedToRightWieght (tree.getRight());
    }
    
    public TextualSpan selectTree(Collection spans) {
        
        int balanced = Integer.MAX_VALUE;               // balanced measure = ABS (left - right)
        LinkedList balancedTrees = new LinkedList ();   // holds the most balanced trees
        Iterator it = spans.iterator();
        /* get most balanced trees */
        while (it.hasNext()) {
            TextualSpan S = (TextualSpan) it.next();
            int diff = Math.abs(S.getTree().getLeft().depth() - S.getTree().getRight().depth());
            if (diff <= balanced) {
                balancedTrees.add (S);
                balanced = diff;
            }
        }
        
        it = balancedTrees.iterator();
        if (balancedTrees.size() == 1)   // if only one tree is the most balanced
            return (TextualSpan) it.next();
       // if more than one balanced, or have the same balance, select the most skewed to right
        TextualSpan mostSkewedToRight = (TextualSpan) it.next();     // let most skewed to right the 1st one
        int wieght = getSkewedToRightWieght (mostSkewedToRight.getTree());
        while (it.hasNext()) {
            TextualSpan S = (TextualSpan) it.next();
            int currentWight = getSkewedToRightWieght (S.getTree());
            if ( currentWight > wieght) {
                mostSkewedToRight = S;
                currentWight = wieght;
            }
        }
        return mostSkewedToRight;
            
    }
    
}
