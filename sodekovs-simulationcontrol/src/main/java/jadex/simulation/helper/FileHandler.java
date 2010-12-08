package jadex.simulation.helper;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
*
* @author Handle files
*/
public class FileHandler {
   
   /**
    * This method reads contents of a file and print it out
    */
   public static BufferedInputStream readFromFile(String filename) {
       
       BufferedInputStream bufferedInput = null;
       byte[] buffer = new byte[1024];
       
       try {
           
           //Construct the BufferedInputStream object
           bufferedInput = new BufferedInputStream(new FileInputStream(filename));
           
           int bytesRead = 0;
           
           //Keep reading from the file while there is any content
           //when the end of the stream has been reached, -1 is returned
           while ((bytesRead = bufferedInput.read(buffer)) != -1) {
               
               //Process the chunk of bytes read
               //in this case we just construct a String and print it out
               String chunk = new String(buffer, 0, bytesRead);
               System.out.print(chunk);
           }
           
       } catch (FileNotFoundException ex) {
           ex.printStackTrace();
       } catch (IOException ex) {
           ex.printStackTrace();
       } finally {
           //Close the BufferedInputStream
           try {
               if (bufferedInput != null)
                   bufferedInput.close();
           } catch (IOException ex) {
               ex.printStackTrace();
           }
       }
       return bufferedInput;
   }
}