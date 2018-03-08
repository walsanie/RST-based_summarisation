/*
 * TextFileCreator.java
 *
 * Created in 2005
 */

package factory.file;

import java.io.*;
/**
 *
 * @author  Waleed Alsanie
 * @version 0.2 beta
 */
public class TextFileCreator implements FileCreator{
    
    /** Creates a new instance of TextFileCreator */
    public TextFileCreator() {
    }
    @Override
    public InputStreamReader createFileReader(String fileName, String charset) 
                             throws IOException {      
        return new InputStreamReader (new FileInputStream (fileName), charset);
    }
    @Override
    public OutputStreamWriter createFileWriter(String fileName, String charset) 
                              throws IOException {
        return new OutputStreamWriter (new FileOutputStream (fileName), charset);
    }
    
}
