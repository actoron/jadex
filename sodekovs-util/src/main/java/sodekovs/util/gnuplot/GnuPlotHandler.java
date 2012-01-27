package sodekovs.util.gnuplot;

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import sodekovs.util.misc.GlobalConstants;

public class GnuPlotHandler {

	/**
	 * Call gnuplot.exe and execute comands within array
	 * @param comando
	 */
	 public static void exec(String[] comando) {  
	        try {  
	            Process p = Runtime.getRuntime().exec(GlobalConstants.GNUPLOT_EXE_FILEPATH);  
	            OutputStream outputStream = p.getOutputStream(); 
	            PrintWriter gp = new PrintWriter(new BufferedWriter(new OutputStreamWriter(outputStream)));  
	  
	            for(int i = 0;i<comando.length;i++){  
	                gp.println(comando[i]);  
	                gp.flush();  
	            }  
	            gp.close();  
	        } catch (Exception x) {  
	        	x.printStackTrace();
	            System.out.println(x.getMessage());  
	        }  
	    }  

}
