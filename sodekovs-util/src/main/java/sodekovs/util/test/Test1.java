package sodekovs.util.test;

import java.io.IOException;
import java.sql.Connection;


public class Test1 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
//		for(int i : WalkingStrategyEnum.values()){
//			
//		}
//		WalkingStrategyEnum p = WalkingStrategyEnum.randomly;
//		System.out.println(WalkingStrategyEnum.valueOf("randomly").ordinal());
		
		// TODO Auto-generated method stub
		
		Connection con = sodekovs.util.bike.persistence.ConnectionMgr.getConnection();

//		String cmd = "cmd cd c:\\ C:\\Users\\vilenica\\GnuPlot\\gp443win32\\gnuplot\\binary\\wgnuplot_pipes.exe pause";
		String cmd = "cmd ";
		
		String[] args1 = {"C:\\Users\\vilenica\\Desktop\12-5-23.plt"};
				  
	    
//		String[] args1 = new String[1];
//		args[0] = "C:\\Users\\vilenica\\Desktop\12-5-23.plt";
//		
//				Use the Runtime.exec(String, String[]) form!!
//
//				You assemble a String[] array with your arguments (one to a string), and invoke it as
//																							    
				String path_to_exec = "C:\\Program Files\\GnuPlot\\gp443win32\\gnuplot\\binary\\gnuplot.exe";
//				String path_to_exec = "C:\\Program Files\\GnuPlot\\gp443win32\\gnuplot\\binary\\wgnuplot.exe";
////
////				String[ ]args = new String[] {"arg1", "arg2", /*... */};
////
				try {
					Runtime.getRuntime().exec(path_to_exec, args1);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
//
//				No need to escape anything, or stand on your head.
//
//				Learn to RTFJ, folks..
		
																							 
//		try {
//			Runtime rt = Runtime.getRuntime();
//	         Process pr = rt.exec("cmd.exe");
			
//			Runtime.getRuntime().exec("cmd.exe pause");
//			Runtime.getRuntime().exec( "C:\\Users\\vilenica\\Desktop\\WORKING SCRIPT-Kopie.bat" );
//			Process p = Runtime.getRuntime().exec("cmd.exe /c start acrord32.exe " + "test.pdf");
//			p.waitFor();
//		 System.out.println("Fehler beim Aufrufen von Gnuplot: " + ex.getMessage());
				
		
		
		
				
				
				
//				
//			 Process gnuplot;
//			try {
//				gnuplot = Runtime.getRuntime().exec("C:\\Program Files\\GnuPlot\\gp443win32\\gnuplot\\binary\\wgnuplot-pipes.exe C:\\Users\\vilenica\\Desktop\12-5-23.plt");
//				gnuplot.waitFor();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			
			
			
			
			//, null, fileName.getParentFile());
// catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		        
////		            return(false);
//		        }
//			Runtime.getRuntime().exec( new String[] {
//                    "cmd.exe",
//                    "/E:1900",
//                    "/C",
//                    "net",
//                    "start",
//                    "servicename"});
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

		
//		try {
//			String gnuplot_cmd = "plot sin(x)";
//			Process p = Runtime.getRuntime().exec("C:\\Program Files\\GnuPlot\\gp443win32\\gnuplot\\binary\\wgnuplot_pipes.exe");

//			OutputStream outputStream = p.getOutputStream(); //process p
//			PrintWriter gp=new PrintWriter(new BufferedWriter(new
//			OutputStreamWriter(outputStream)));
//			gp.println("plot sin(x)\n");
//			gp.close();
//			PrintWriter pw = new PrintWriter(p.getOutputStream());
//			pw.print(gnuplot_cmd);
//			pw.close();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
		
		
//		 try {
//		      String line;
//		      Process p = Runtime.getRuntime().exec
//		        (System.getenv("windir") +"\\system32\\"+"tree.com /A");
//		      BufferedReader input =
//		        new BufferedReader
//		          (new InputStreamReader(p.getInputStream()));
//		      while ((line = input.readLine()) != null) {
//		        System.out.println(line);
//		      }
//		      input.close();
//		    }
//		    catch (Exception err) {
//		      err.printStackTrace();
//		    }
		
	}
}
