/*
 * Utf8.java
 *
 * Created in 2005
 */

package nlp.text;

import java.io.UnsupportedEncodingException;

/**
 * This class defines the the encoding of the symbols in UTF8. 
 * 
 * @author  Waleed Alsanie
 * @version 0.2 beta
 */
public class Utf8 extends CharCoding {
    
    /** Creates a new instance of UTF-8 */
    public Utf8() {
        // Set the name of the encoding
        super("UTF-8");
        
        byte[] alef =           {(byte) 0xd8, (byte) 0xa3};    //alef 
        byte[] taaMarboota =    {(byte) 0xd8, (byte) 0xa9};    //taaMarboota
        byte[] taa =            {(byte) 0xd8, (byte) 0xaa};    //taa
        byte[] lam =            {(byte) 0xd9, (byte) 0x84};    //lam;
        byte[] meem =           {(byte) 0xd9, (byte) 0x85};    //meem
        byte[] noon =           {(byte) 0xd9, (byte) 0x86};    //noon;
        byte[] haa =            {(byte) 0xd9, (byte) 0x87};    //haa;
        byte[] waw =            {(byte) 0xd9, (byte) 0x88};    //waw;
        byte[] yaa =            {(byte) 0xd9, (byte) 0x8a};    //yaa;
        byte[] simiColon =      {(byte) 0xd8, (byte) 0x9b};    //simiColon;
        byte[] comma =          {(byte) 0xd8, (byte) 0x8c};    //comma;
        
        // convert the Arabic letter encodings from byte[] to char   
        try{
            setCharValues(
                new String(alef, "UTF-8").charAt(0),
                new String(taaMarboota, "UTF-8").charAt(0), 
                new String(taa, "UTF-8").charAt(0), 
                new String(lam, "UTF-8").charAt(0), 
                new String(meem, "UTF-8").charAt(0),  
                new String(noon, "UTF-8").charAt(0),  
                new String(haa, "UTF-8").charAt(0),  
                new String(waw, "UTF-8").charAt(0),  
                new String(yaa, "UTF-8").charAt(0),  
                new String(simiColon, "UTF-8").charAt(0),  
                new String(comma, "UTF-8").charAt(0)  
            );
        } catch (UnsupportedEncodingException e) {
            System.out.println("Internal Error! Cannot Encode " +
                               "Some UTF-8 Characters");
            e.printStackTrace();
        }
    }
}
