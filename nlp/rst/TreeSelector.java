/*
 * TreeSelector.java
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
public interface TreeSelector {
    
    public TextualSpan selectTree (Collection trees);   
}
