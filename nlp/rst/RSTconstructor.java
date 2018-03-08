/*
 * RST_Builder.java
 *
 * Created in 2005
 */
 


/** 
 *
 * @author  Waleed Alsanie
 * @version 
 */
package nlp.rst;

import java.util.*;
import java.sql.*;

/**
 * this class uses the algorithm developed by March in:
 * 
 * -Daniel March, (2000). The Rhetorical Parsing of Unrestricted Texts: 
 * A Surface-based Approach, Computational Linguistics.
 * 
 * to build all the valid rhetorical structure trees. 
 * 
 * The axioms can be found in:
 * 
 * Daniel Marcu (1997). The Rhetorical Parsing, Summarization, and Generation 
 * of Natural Language Texts. PhD Thesis, Department of Computer Science, 
 * University of Toronto, December 1997. Also published as Technical Report 
 * CSRG-371, Computer Systems Research Group, University of Toronto.
 * 
 * @author  Waleed Alsanie
 * @version 0.2 beta
 */
public class RSTconstructor extends Object {

  private int NumberOfTextUnits;            // number of elementry text units
  private Collection SetOfRelations;   // a set of given rhetorical relations
  private Collection SetOfTextualSpans;    // set of RST generated .. 
  private Collection hypotacticRels;
  private Collection paratacticRels;
  
  public RSTconstructor(Collection hypo, Collection para) {
      hypotacticRels = new LinkedList (hypo);
      paratacticRels = new LinkedList (para);
      SetOfTextualSpans = new HashSet();
      NumberOfTextUnits = 0;
  }
  
  public void setRelations (Collection Set) {  // take a set of relations to build 
                                        // RST builder object .. 
    SetOfRelations = new HashSet(Set);
    
    Iterator i = SetOfRelations.iterator(); // iterate over the relations to
    RhetRel R;                             // know the max number of text
    while (i.hasNext()) {                   // units ... 
      R = (RhetRel) i.next();
      int firstUnitNumber;
      int secondUnitNumber;
      if (R instanceof BinaryRhetRel) {
          BinaryRhetRel rel = (BinaryRhetRel) R;
          firstUnitNumber = rel.getFirstSpan();
          secondUnitNumber = rel.getSecondSpan();
      } 
      else {
          ComplexRhetRel rel = (ComplexRhetRel) R;
          firstUnitNumber = rel.getFirstSpanEnd();
          secondUnitNumber = rel.getSecondSpanEnd();
      }
      if (firstUnitNumber > NumberOfTextUnits)
        NumberOfTextUnits = firstUnitNumber;

      if (secondUnitNumber > NumberOfTextUnits) 
        NumberOfTextUnits = secondUnitNumber;
    }
    
  }

  public void buildRST() {   // Marcu's algo. of building the valid text 
                               // trees. 
    int text_unit, SizeOfSpan, l, b, h;
    Iterator IS1, IS2, Ir;
    TextualSpan S1, S2;
    Collection C;
    
    apply_axiom_schema_3_23 ();
    for (text_unit =1; text_unit <= NumberOfTextUnits; text_unit++) {
      apply_axiom_schema_3_24 ();
      apply_axiom_schema_3_25_and_3_26 (new Integer (text_unit));
    }
    
    for (SizeOfSpan = 1; SizeOfSpan < NumberOfTextUnits; SizeOfSpan ++)
      for (l = 1; l <= NumberOfTextUnits - SizeOfSpan; l++) {
        h = l + SizeOfSpan;
        for (b = l; b < h; b++){ 
          //for each theorem S(l, b, tree1, RR1) of span[l, b]
          C = new HashSet (SetOfTextualSpans);
          IS1 = C.iterator();
          while (IS1.hasNext()) {
            // for each theorem S(b+1, h, tree2, RR2) of span [b+1, h]
            S1 = (TextualSpan) IS1.next();
            if (isStartSpan(S1, l) && isEndSpan(S1, b)) {
              IS2 = C.iterator();
              while (IS2.hasNext()) {
                // for each relation r such that r belongs to RR1 and RR2
                S2 = (TextualSpan) IS2.next();
                if (isStartSpan(S2, b+1) && isEndSpan(S2, h)) {
                  Ir = SetOfRelations.iterator();
                  while (Ir.hasNext()) {
                    // apply all possible axioms (3.31)-(3.42)
                    apply_all_possible_axioms_3_31_3_42 (S1, S2,
                                                  (RhetRel) Ir.next());
                  } //(most inner while )
                } // (most inner if )
              }  // (second innner while)
            }   // (second inner if )
          }   // (outer while )
        }   // (inner for )
      }     // (outer for )
  }     // (method)
  
  private void apply_axiom_schema_3_23 () {   // might be implemented later 
  }                                            // when needed 
  
  private void apply_axiom_schema_3_24 () { // might be implemented later 
  }                                           // when needed 
  
  private void apply_axiom_schema_3_25_and_3_26(Integer unit) {
                                 // to builde the leaf nodes of the tree
    RhetTree T;
    TextualSpan S;
    Collection p = new HashSet ();
    
    p.add(unit);
    T = new RhetTree("NULL", "LEAF", p, null, null);
    S = new TextualSpan(unit.intValue(), unit.intValue(), T, 
                          new HashSet(SetOfRelations));
    SetOfTextualSpans.add(S);
  }
                        // start joining the trees up to the root .. 
  private void apply_all_possible_axioms_3_31_3_42 (TextualSpan S1,
                                                    TextualSpan S2,
                                                    RhetRel rel) {
                           // take to spans and one relation, then check
                           // if they can boolean joined useing the R
     Collection finalPromotion;
     BinaryRhetRel R = null;
     if (rel instanceof BinaryRhetRel)
        R = (BinaryRhetRel) rel;
    
     if (! ((S1.getEndSpan ()+1 == S2.getStartSpan())  &&
          (belongsTO_RR (S1.getRR(), rel)) && (belongsTO_RR (S2.getRR(), rel))))
        return;
                 
     if (rel instanceof ComplexRhetRel) {
         ComplexRhetRel R_ext = (ComplexRhetRel) rel;
         if (hypotactic (rel.getRel ()))
             if (S1.getStartSpan () == R_ext.getFirstSpanStart() &&
                 S1.getEndSpan () == R_ext.getFirstSpanEnd() &&
                 S2.getStartSpan () == R_ext.getSecondSpanStart() &&
                 S2.getEndSpan () == R_ext.getSecondSpanEnd()) {
                 S1.getTree().setStatus("NLS"); 
                 S2.getTree().setStatus("SAT");
                 finalPromotion = S1.getTree().getPromotion();
             }
             else
                 if (S2.getStartSpan () == R_ext.getFirstSpanStart() &&
                     S2.getEndSpan () == R_ext.getFirstSpanEnd() &&
                     S1.getStartSpan () == R_ext.getSecondSpanStart() &&
                     S1.getEndSpan () == R_ext.getSecondSpanEnd()) {
                     S2.getTree().setStatus("NLS"); 
                     S1.getTree().setStatus("SAT");
                     finalPromotion = S2.getTree().getPromotion();
                 }
                 else
                     return;
         else
             if ((S1.getStartSpan () == R_ext.getFirstSpanStart() &&
                 S1.getEndSpan () == R_ext.getFirstSpanEnd() &&
                 S2.getStartSpan () == R_ext.getSecondSpanStart() &&
                 S2.getEndSpan () == R_ext.getSecondSpanEnd()) ||
                 (S2.getStartSpan () == R_ext.getFirstSpanStart() &&
                 S2.getEndSpan () == R_ext.getFirstSpanEnd() &&
                 S1.getStartSpan () == R_ext.getSecondSpanStart() &&
                 S1.getEndSpan () == R_ext.getSecondSpanEnd())) {
                     S1.getTree().setStatus("NLS");
                     S2.getTree().setStatus("NLS");
                     finalPromotion = new HashSet (S1.getTree().getPromotion());
                     finalPromotion.addAll(S2.getTree().getPromotion());
             }
             else
                 return;
     }
     else        
         if (hypotactic (rel.getRel()))
            if ((belongsTOpromotion(S1.getTree().getPromotion(),  // axiom 3.31
                            new Integer(R.getFirstSpan()))) &&    // or 3.32
                (belongsTOpromotion(S2.getTree().getPromotion(),  
                             new Integer(R.getSecondSpan())))) {  
              S1.getTree().setStatus("NLS");      
              S2.getTree().setStatus("SAT");
              finalPromotion = S1.getTree().getPromotion();
            }
            else
              if ((belongsTOpromotion(S2.getTree().getPromotion(), // axiom 3.35
                            new Integer(R.getFirstSpan()))) &&    // or 3.36
                  (belongsTOpromotion(S1.getTree().getPromotion(),
                             new Integer(R.getSecondSpan())))) {
                S2.getTree().setStatus("NLS");
                S1.getTree().setStatus("SAT");
                finalPromotion = S2.getTree().getPromotion();
              }
              else 
                return;
         else
           if ((belongsTOpromotion(S1.getTree().getPromotion(), //axiom 3.39
                            new Integer(R.getFirstSpan()))) &&   // or 3.40
                (belongsTOpromotion(S2.getTree().getPromotion(), 
                             new Integer(R.getSecondSpan()))) ||
                (belongsTOpromotion(S2.getTree().getPromotion(), //axiom 3.39
                            new Integer(R.getFirstSpan()))) &&   // or 3.40
                (belongsTOpromotion(S1.getTree().getPromotion(), 
                             new Integer(R.getSecondSpan())))) {
              S1.getTree().setStatus("NLS");
              S2.getTree().setStatus("NLS");
              finalPromotion = new HashSet (S1.getTree().getPromotion());
              finalPromotion.addAll(S2.getTree().getPromotion()); // p1 union p2
            }
            else
              return;
                        // generate new textual span S
     TextualSpan S = new TextualSpan(S1.getStartSpan(), S2.getEndSpan(),
                          new RhetTree("NULL", rel.getRel(), finalPromotion, 
                                         S1.getTree(), S2.getTree()), 
                          excRelIntersection (S1.getRR(), S2.getRR(), rel));
      
     SetOfTextualSpans.add(S);
  }
  
  private boolean belongsTOpromotion(Collection p, Integer u) {
               // checks of a given unit belongs to a given set of promotion
    Iterator i = p.iterator();
    Integer unit;
    
    while (i.hasNext()) {
      unit = (Integer) i.next();
      if (unit.intValue() == u.intValue())
        return true;
    }    
    return false;
  }
  
  private boolean belongsTO_RR(Collection c, RhetRel r) {
             // checks of a given relation belongs to a given set of relations
    Iterator i = c.iterator();
    RhetRel TempR;
    if (r instanceof BinaryRhetRel)
        while (i.hasNext()) {
          TempR = (RhetRel) i.next();
          if (TempR instanceof BinaryRhetRel) {
              BinaryRhetRel rel = (BinaryRhetRel) TempR;
              if (rel.equals (r))
                return true;
          }
        }
    else
        while (i.hasNext()) {
          TempR = (RhetRel) i.next();
          if (TempR instanceof ComplexRhetRel) {
              ComplexRhetRel rel = (ComplexRhetRel) TempR;
              if (rel.equals (r))
                return true;
          }
        }
    return false;
  }
  
  private boolean hypotactic(String rel) {
                   // checks if a given relations is hypotactic or otherwise
    Iterator it = hypotacticRels.iterator();
    while (it.hasNext())
        if (((String)it.next()).equalsIgnoreCase(rel))
            return true;
    return false;
    
  }
  
  private Collection excRelIntersection(Collection rel1, Collection rel2,
                                        RhetRel R) {
           // this method takes two sets of relations and returns back their
           //  intersection excluding the given relation R .. 
    Iterator i2, i1 = rel1.iterator();
    RhetRel r1;
    Collection result = new HashSet();
    
    while (i1.hasNext()) {
      r1 = (RhetRel) i1.next();
      i2 = rel2.iterator();
      while (i2.hasNext()) {
          RhetRel r2 = (RhetRel) i2.next();
          if (r1.equals(r2) && ! (r1.equals (R)))
              result.add(r1);
      }
    } 
    return result;
  }
  
  public Collection getAllRST() { // returns back the whole trees built
    return SetOfTextualSpans;           // during the process 
  }                                     // even the sub tress .. 
  
  public Collection getBuiltRST() { // returns back 
                                           // only the final trees (completed)
    Iterator i;
    TextualSpan T;
    Collection C = new HashSet();
    
    if (SetOfTextualSpans == null)
      return null;
    
    i = SetOfTextualSpans.iterator();
    while (i.hasNext ()){
      T = (TextualSpan) i.next();
      if ((T.getStartSpan () == 1) && (T.getEndSpan() == NumberOfTextUnits))
        if (! added (C, T)) {               // if the tree is complete .. 
          T.getTree().setStatus("NLS"); // mark its status as nucleus
          C.add(T);
        }
    }      
    return C;
  }
  
  public int deepestTree() {
    
    Collection C = getBuiltRST();
    Iterator i = getBuiltRST().iterator();
    TextualSpan T;
    int Max =0;
    
    while (i.hasNext()) {
      T = (TextualSpan) i.next();
      if (Max < T.getTree().depth())
        Max = T.getTree().depth();
    }
    
    return Max;
  }
  
  private boolean added(Collection C, TextualSpan T) { // check if a given
                                                // tree is already added
    Iterator i;                                 // to the given collection
    TextualSpan S;
    
    if (C.isEmpty())
      return false;
    else {
      i = C.iterator();
      while (i.hasNext()) {
        S = (TextualSpan) i.next();
        if ((S.getStartSpan() == T.getStartSpan()) &&
            (S.getEndSpan() == T.getEndSpan()) &&
            (S.getTree().equals(T.getTree ())))
            return true; 
      }
    }
    return false;
  }
  
  private boolean isStartSpan(TextualSpan S, int b) { // checks if the given 
                                              // unit is a start unit of 
    return S.getStartSpan () == b;            // a given textuat span (tree)
  }
  
  private boolean isEndSpan(TextualSpan S, int b) { // checks if the given 
                                           // unit is an end unit of  
    return S.getEndSpan () == b;         // a given textuat span (tree)
  }
}