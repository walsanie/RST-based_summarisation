/*
 * FileCreator.java
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
public interface FileCreator {
    
    public InputStreamReader createFileReader (String fileName, String charset)
                             throws IOException; 
    public OutputStreamWriter createFileWriter (String fileName, String charset)
                              throws IOException;
}
