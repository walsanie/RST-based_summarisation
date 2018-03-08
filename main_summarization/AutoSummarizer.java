/* ArabicTextSummarizer.java
 *
 * Created in 2005
 */

package main_summarization;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import nlp.rst.*;
import arabic_text_summarization.rst_based.ArabicTextSummarizer;
import nlp.rst.RSTreePainter;

/**
 * This class implements the user interface of the program. It calls the 
 * summarization methods based on the settings that are given in this UI.
 * 
 * @author  Waleed Alsanie
 * @version 0.2 beta
 */
public class AutoSummarizer {
    
    private final JFrame textSumm;
    private final JRadioButton bundledButton;
    private final JRadioButton myDataButton;
    // this is the default data directory
    private final static String DEFAULT_DATA_DIR = "data";
    private final JTextField threshold;
    private final JTextField summLevel;
    private final JComboBox charset;
    private final JTextField dirLocation;
    private final JButton selectDir;
    private final JTextField inputFile;
    private final JButton selectInput;
    private final JTextField outputFile;
    private final JButton selectOutput;
    private final JButton run;
    private ArabicTextSummarizer ats;
    
    /** Creates a new instance of ArabicTextSummarizer */
    public AutoSummarizer() {
        
        textSumm = new JFrame("Arabic Text Summarization Using RST");
        Container content = textSumm.getContentPane();
        content.setLayout(new BoxLayout(content, BoxLayout.PAGE_AXIS));
        
        JPanel selectDataPanel = new JPanel();
        selectDataPanel.setMaximumSize(new Dimension (500, 30));
        selectDataPanel.setLayout(new FlowLayout());
        bundledButton = new JRadioButton("Use bundled data");
        //bundledButton.setMnemonic(KeyEvent.VK_B);
        //bundledButton.setActionCommand("Use bundled data");
        bundledButton.setSelected(true);
        myDataButton = new JRadioButton("Use my data");
        //catButton.setMnemonic(KeyEvent.VK_C);
        //bundledButton.setActionCommand("Use my data");
        ButtonGroup group = new ButtonGroup();
        group.add(bundledButton);
        group.add(myDataButton);
        selectDataPanel.add(bundledButton);
        selectDataPanel.add(myDataButton);
        
        JPanel dirPanel = new JPanel();
        dirPanel.setMaximumSize(new Dimension (500, 30));
        dirPanel.setLayout(new FlowLayout());
        JLabel dirLocLabel = new JLabel("Data directory");
        dirLocLabel.setPreferredSize(new Dimension (120, 20));
        dirLocation = new JTextField();
        dirLocation.setText(DEFAULT_DATA_DIR); // Set the default data directory
        dirLocation.setPreferredSize(new Dimension (200, 20));
        selectDir = new JButton ("Select");
        // Disable them as the defaul data will be used by default 
        // Will be enabled when the user chooses to select his own data directory
        dirLocation.setEnabled(false);
        selectDir.setEnabled(false);
        dirPanel.add(dirLocLabel);
        dirPanel.add(dirLocation);
        dirPanel.add(selectDir);
        
        JPanel thresholdPanel = new JPanel();
        thresholdPanel.setLayout(new FlowLayout());
        JLabel thresholdLabel = new JLabel("Word repetition threshold ");
        threshold = new JTextField("2");
        threshold.setForeground(Color.BLUE);
        threshold.setPreferredSize(new Dimension(30, 20));
        thresholdPanel.add(thresholdLabel);
        thresholdPanel.add(threshold);
        
        JPanel summLevelPanel = new JPanel();
        summLevelPanel.setLayout(new FlowLayout());
        JLabel summLevelLabel = new JLabel("Summarization level ");
        summLevel = new JTextField ("2");
        summLevel.setForeground(Color.BLUE);
        summLevel.setPreferredSize(new Dimension (30, 20));
        summLevelPanel.add(summLevelLabel);
        summLevelPanel.add(summLevel);
        
        JPanel charsetPanel = new JPanel();
        charsetPanel.setLayout(new FlowLayout());
        JLabel charsetLabel = new JLabel("Arabic encoding ");
        String charsetList [] = {"UTF-8"};
        charset = new JComboBox(charsetList);
        charset.setPreferredSize(new Dimension (120, 20));
        charsetPanel.add(charsetLabel);
        charsetPanel.add(charset);
        
        JPanel inputPanel = new JPanel();
        inputPanel.setMaximumSize(new Dimension (500, 30));
        inputPanel.setLayout(new FlowLayout());
        JLabel inputLabel = new JLabel("Input file ");
        inputLabel.setPreferredSize(new Dimension (80, 20));
        inputFile = new JTextField();
        inputFile.setPreferredSize(new Dimension (200, 20));
        selectInput = new JButton ("Select");
        inputPanel.add(inputLabel);
        inputPanel.add(inputFile);
        inputPanel.add(selectInput);
        
        JPanel outputPanel = new JPanel();
        outputPanel.setMaximumSize(new Dimension (500, 30));
        outputPanel.setLayout(new FlowLayout());
        JLabel outputLabel = new JLabel("Output file ");
        outputLabel.setPreferredSize(new Dimension (80, 20));
        outputFile = new JTextField ();
        outputFile.setPreferredSize(new Dimension (200, 20));
        selectOutput = new JButton ("Select");
        outputPanel.add(outputLabel);
        outputPanel.add(outputFile);
        outputPanel.add(selectOutput);
        
        run = new JButton ("Run");
        
        content.add(Box.createRigidArea(new Dimension(0 , 40)));
        content.add(selectDataPanel);
        content.add(dirPanel);
        content.add(thresholdPanel);
        content.add(summLevelPanel);
        content.add(charsetPanel);
        content.add(inputPanel);
        content.add(outputPanel);
        content.add(run);
        content.add(Box.createRigidArea(new Dimension(0 , 20)));
        
        textSumm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        textSumm.setVisible(true);
        textSumm.setResizable(true);
        textSumm.setLocation(700, 250);
        textSumm.setSize(600, 450);
        // JFrame.show has been deprecated since Java 1.5
        //textSumm.show();
        // So we will use JFrame.setVisible(true) instead
        textSumm.setVisible(true);
    }
    
    /**
     * Returns the files and the directory set in the UI.
     * 
     * @param which: if set to 0, the files only selection mode is enabled.
     *               Otherwise the directories only selection mode is enabled.
     */
    private static String selectFileDir(int which) {
        
        JFileChooser selector = new JFileChooser();   // create a file chooser
        selector.setDialogTitle("Select input file");
        if (which == 0)
            selector.setFileSelectionMode(JFileChooser.FILES_ONLY);
        else
            selector.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int pressed = selector.showOpenDialog(null); // dialog to choose a file
        File fileName;
        if (pressed == JFileChooser.APPROVE_OPTION) {
            fileName = selector.getSelectedFile();
            return fileName.getAbsolutePath();
        }
        else
            return new String();
    }
    
    /**
     * Handles the radio buttons and enables and disables the data directory
     * selection accordingly. 
     * 
     * @param enable: if true, user is able to choose his own data directory.
     *               Otherwise the default directory will be used.
     */
    private void setSelectData(boolean enable) {
        if (enable) {
            dirLocation.setEnabled(true);
            selectDir.setEnabled(true);
        }
        else {
            dirLocation.setEnabled(false);
            selectDir.setEnabled(false);
            // Set the default data directory
            dirLocation.setText(DEFAULT_DATA_DIR); 
        }
    }
    
    public void run () {
        // When the user chooses to use the bundled data
        bundledButton.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        setSelectData(false);
                    }
                }
        );
        // When the user chooses to use his own data
        myDataButton.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        setSelectData(true);
                    }
                }
        );
        // Select data directory when the user chooses to do so
        selectDir.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        dirLocation.setText(selectFileDir(1));
                    }
                }
        );
        // Select the input file containing the text
        selectOutput.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        outputFile.setText(selectFileDir(0));
                    }
                }
        );
        // Select the output file to where the text will be written
        selectInput.addActionListener(  
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        inputFile.setText(selectFileDir(0));
                    }
                }
        ); 
        // Run the summarizer
        run.addActionListener(
                new ActionListener () {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        try {
                        // JFrame.show has been deprecated since Java 1.5
                        // textSumm.hide();
                        // So we will use JFrame.setVisible(false) instead
                            textSumm.setVisible(false);
                            ats = new ArabicTextSummarizer(
                                    (String) charset.getSelectedItem()
                            );
                            ats.setDataDir(dirLocation.getText());
                            ats.setRepetitionThreshold(
                                    Integer.parseInt(threshold.getText())
                            );
                            ats.setTreeLevel(
                                    Integer.parseInt(summLevel.getText())
                            );
                            ats.summarizeText(
                                    inputFile.getText()
                            );
                            TextualSpan resultingTree = ats.getGenertatedTree();
                            ats.outputSummary(
                                    resultingTree.getTree(), 
                                    ats.getTextualSpan(),
                                    outputFile.getText()
                            );
                            //ats.writeUnits("somewhere");
                            RSTreePainter drawer = new RSTreePainter(
                                    resultingTree, 
                                    (String) charset.getSelectedItem()
                            ); 
                            // Exit the program when the RSTreePainter frame is closed
                            drawer.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                            //generateSummary(selectedTree.getTree(), paraUnits);
                            drawer.drawRST();
                        } catch (Exception exp) {
                            exp.printStackTrace();
                            JOptionPane.showMessageDialog(null, 
                                    "An internal error has occured\n" + 
                                     exp.getMessage(),
                                    "Error", JOptionPane.INFORMATION_MESSAGE
                            );
                            System.exit(1);
                        }
                    }
                }
        ); 
    }
    
    public static void main(String[] args) {
        // Run the UI
        AutoSummarizer ar = new AutoSummarizer();
        ar.run();
    }    
}
