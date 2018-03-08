/*
 * CharCoding.java
 *
 * Created in 2005
 */

package nlp.text;

import java.nio.charset.Charset;

/**
 * This class defines the the encodings of the symbols in some encoding system. 
 * 
 * @author  Waleed Alsanie
 * @version 0.2 beta
 */
public abstract class CharCoding {
    
    // Only these symbols are defined. They can be extended with all the symbols.
    private final String encodingName;
    private final Charset chset;
    private char alef;
    private char taaMarboota;
    private char taa;
    private char lam;
    private char meem;
    private char noon;
    private char haa;
    private char waw;
    private char yaa;
    
    private char simiColon;
    private char comma;
    
    protected CharCoding(String name){
        encodingName = name;
        chset = Charset.forName(encodingName);
    }
    protected void setCharValues(char a, char tm, char t, char l, char m,
                                 char n, char h, char w, char y, char sc,
                                 char c){
        alef = a; taaMarboota = tm; taa = t; lam = l; meem = m; noon = n;
        haa = h; waw = w; yaa = y; simiColon = sc; comma = c;
    }
    
    public String getEncodingName() { return encodingName; }
    public Charset getCharset()      { return chset; }
    
    // Get the values of the chars
    public char getAleph() { return alef; }
    public char getTaaMarboota() { return taaMarboota; }
    public char getTaa() { return taa; }
    public char getLam() { return lam; }
    public char getMeem() { return meem; }
    public char getNoon() { return noon; }
    public char getHaa() { return haa; }
    public char getWaw() { return waw; }
    public char getYaa() { return yaa; }
    public char getSimiColon() { return simiColon; }
    public char getComma() { return comma; }
    
    public String getPuncRegex () {
        char[] ch = {'[', '\\', '.', this.getSimiColon(), 
                     this.getComma(), ']'};
                         
        return new String (ch);
    }
    public String getAND () {
        return String.valueOf(this.getWaw());
    }
    public String getOR () {
        char or [] = {this.getAleph(), this.getWaw()};
        return String.valueOf(or);
    }
    
}
