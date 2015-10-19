/** code copied from
 *  http://www.javaworld.com/javaworld/jw-12-2000/jw-1229-traps.html
 *  When Runtime.exec() won't: Navigate yourself around pitfalls
 *  related to the Runtime.exec() method
 *  @author Michael Daconta
 */
import java.util.*;
import java.io.*;

class StreamGobbler extends Thread
{
    InputStream is;
    String type;
    OutputStream os;
    boolean print;

    StreamGobbler(InputStream is, String type)
    {
        this(is, type, null);
    }

    StreamGobbler(InputStream is, String type, OutputStream redirect)
    {
        this(is, type, redirect, false);
    }

    StreamGobbler(InputStream is, String type, OutputStream redirect, boolean print)
    {
        this.is = is;
        this.type = type;
        this.os = redirect;
        this.print = print;
    }

    /** creates readers to handle the text created by the external program
     */
    public void run()
    {
        try
        {
            PrintWriter pw = null;
            if (os != null)
                pw = new PrintWriter(os);

            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line=null;
            while ( (line = br.readLine()) != null)
            {
                if (pw != null)
                    pw.println(line);
                if (print) {
                    System.out.println(type + ">" + line);
                }
            }
            if (pw != null)
                pw.flush();
        } catch (IOException ioe)
        {
            ioe.printStackTrace();
        }
    }
}
