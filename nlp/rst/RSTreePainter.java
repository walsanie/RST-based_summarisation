/*
 * RSTreePainter.java
 *
 * Created in 2005
 */

package nlp.rst;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import factory.encoding.*;
import nlp.text.*;
import java.io.UnsupportedEncodingException;

/**
 * Draws the rhetorical tree.
 * 
 * @author  Waleed Alsanie
 * @version
 */
public class RSTreePainter extends JFrame{
    
    private RSTpainter drawingArea;             // the drawing panel
    private JScrollPane scroller;               // scroller over the panel
    private TextualSpan span;                   // text span to be drawn
    private int width;                          // frame width
    private int height;                         // frame hight
    
    /** Creates a new instance of RSTreePainter */
    public RSTreePainter(TextualSpan s) {
        span = s; 
        width = 600;
        height = 600;
        drawingArea = new RSTpainter (span.getTree());
    }
    
    public RSTreePainter(TextualSpan s, String enc) {
        span = s; 
        width = 600;
        height = 600;
        drawingArea = new RSTpainter (span.getTree(), enc);
    }
    
    public void drawRST () {
        this.setTitle("Rhetorical Structure Tree");
        scroller = new JScrollPane (drawingArea);       // create view scroller
        scroller.getViewport().setViewPosition(
                new Point(150,drawingArea.getHeight()/2 - height /2)
        );
        // make the drawing area while color
        drawingArea.setBackground(Color.WHITE);     
        this.setSize(width, height);
        getContentPane().add (scroller);     // add the panel -in the scroller.
        // JFrame.show has been deprecated since Java 1.5
        // this.show();
        // So we will use JFrame.setVisible(true) instead
        this.setVisible(true);
        this.setLocation(700, 250);
    }
    
                    /* class the draws the trees in the drawing panel */
    private class RSTpainter extends JPanel {
        private int diameter;               // diameter of the node circle
        private int panelWidth;             // drawing panel width
        private int panelHight;             // drawing panel hight
        private final int childY = 50;      // the distance between two levels in the tree
        private final int childX = 70;      // the distance between two leaf nodes
        private RhetTree tree;              // tree to be drawn
        private CharCoding encoding;
        
        public RSTpainter (RhetTree t) {
            panelHight = span.getTree().depth() * childY + 100 ;
		panelWidth = childX * span.getTree().depth() * 8;
            if (panelWidth < width)
                panelWidth = width;
            if (panelHight < height) 
                panelHight = height;
            tree = t;
            diameter= 10;
            // Create an encoding object
            try{
                encoding = new ArabicEncodingCreator().createEncoding("UTF8");
            } catch (UnsupportedEncodingException exp) {
                exp.printStackTrace();
                    JOptionPane.showMessageDialog(null, 
                    "Error in RST Painter!",
                    "Error", JOptionPane.INFORMATION_MESSAGE);
            }
        }
        
        public RSTpainter (RhetTree t, String enc) {
            panelHight = span.getTree().depth() * childY + 100 ;
		panelWidth = childX * span.getTree().depth() * 8;
            if (panelWidth < width)
                panelWidth = width;
            if (panelHight < height) 
                panelHight = height;
            tree = t;
            diameter= 10;
            // Create an encoding object
            try{
                encoding = new ArabicEncodingCreator().createEncoding(enc);
            } catch (UnsupportedEncodingException exp) {
                exp.printStackTrace();
                JOptionPane.showMessageDialog(null, 
                    "Unsupported encoding " + enc,
                    "Error", JOptionPane.INFORMATION_MESSAGE);
            }
        }
        
        @Override
        public Dimension getPreferredSize () {   
            return new Dimension (panelWidth, panelHight);
        }
        @Override
        public int getWidth () {    
            return panelWidth;
        }
        @Override
        public int getHeight () {
            return panelHight;
        }
        @Override
        public void paint(Graphics g) {
            super.paint(g);                 // prepare the drawing panel
            g.setColor(Color.GRAY);         // set the drawing color
            draw(g, tree, 50, 50, new int[1], new Point [1]);  //draw
        }
        
        public void drawRST () {
            repaint();           
        }
        
        /* x is the x coordinate of the first node to be drawn.
         * the drawing follow the postorder traverse 
         *y is the y coordinate of the tree root.
         *mostright returns the x coordinate of the most right node in the tree
         *root returns the root coordinates of the tree
         */
        private void draw(Graphics g, RhetTree t, int x, int y, int []mostRight, 
                           Point[] root) {
            
            if (t != null) {
                y +=childY;                 // go down one level
                               // create the points of the left and right branches roots
                Point []rootLeft = new Point [1], rootRight = new Point [1];
                draw (g, t.getLeft(), x, y, mostRight, rootLeft);       // draw left
                        // the right branch is drawn by shifting a distance of childX
                        // from the mostright node of the left branch.
                if (t.getRight() == null)       // if right branch is null
                    mostRight[0] = x;           // the most right is the same node
                else {                          // since there is no most right
                    mostRight[0] += childX;     // otherwise increment most right by offset
                    draw (g, t.getRight(), mostRight[0], y, mostRight, rootRight);
                }
                if (rootLeft[0] == null)    // if left branch is null
                    root[0] = new Point ((mostRight[0] - x)/2 + mostRight[0], 
                                      y -=childY);
                else {
                    root[0] = new Point ((rootRight[0].x - rootLeft[0].x)/2 + rootLeft[0].x, 
                                      y -=childY);
                    if (span.getTree().importantNode(t.getLeft().getPromotion()))
                        g.setColor(Color.RED);              // draw in red if important node
                    g.drawLine(root[0].x + diameter/2, root[0].y + diameter, 
                                rootLeft[0].x + diameter /2, rootLeft[0].y);
                    g.setColor(Color.GRAY);             // color back to gray
                }
                if (rootRight[0] != null) {
                    if (span.getTree().importantNode(t.getRight().getPromotion()))
                        g.setColor(Color.RED);
                    g.drawLine(root[0].x + diameter/2, root[0].y + diameter, 
                            rootRight[0].x + diameter /2, rootRight[0].y);
                    g.setColor(Color.GRAY);
                }
                
                drawNode (g, t, root[0].x, root[0].y);      // draw the node 
            }                                               // and its info
        }
        
        private void drawNode (Graphics g, RhetTree t, int x, int y) {
            
            if (span.getTree().importantNode(t.getPromotion()))
                g.setColor(Color.RED);                  // if important draw in red
            g.drawOval(x, y, diameter, diameter);
            g.setColor(Color.BLACK);
            g.drawString("S= " + t.getStatus(), x + diameter + 5, y);
            Font f = g.getFont();
            try {             // convert to a format the is suitable to display Arabic
                String str = new String("T=" + t.getType());
                byte b[] = str.getBytes();
                str = new String (b, encoding.getEncodingName()); 
                g.setFont(new Font("Tahoma", Font.PLAIN, 10));
                g.drawString(str, x + diameter + 5, y + 10);
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, 
                    "An internal error in drawing the RST has occured",
                    "Error", JOptionPane.INFORMATION_MESSAGE);
            }
            
            g.setFont(f);
            Iterator it = t.getPromotion().iterator();
            String promo = new String ();
            while (it.hasNext()) {
                promo = promo.concat(String.valueOf(((Integer) it.next()).intValue()));
                if (it.hasNext())
                    promo = promo.concat(", ");
            }
            g.drawString ("P={" + promo + "}", x + diameter + 5, y + 22);
            g.setColor(Color.GRAY);
        }
    
    }
    
}
