package jadex.platform.service.daemon;

import java.util.Date;

/**
 *  Test starting separate VMs.
 */
public class DaemonTest
{
	/**
	 *  Test starting separate VMs.
	 */
	public static void main(String[] args)	throws Exception
	{
		if(args.length==0)
		{
//			for(Object key: System.getProperties().keySet())
//			{
//				System.out.println(key+": "+System.getProperty((String)key));
//			}
//			ProcessBuilder	pb	= new ProcessBuilder("cmd", "/C", "start", "/B", "javaw", "-classpath", System.getProperty("java.class.path"), System.getProperty("sun.java.command"), "huhu");
//			ProcessBuilder	pb	= new ProcessBuilder("javaw", "-classpath", System.getProperty("java.class.path"), System.getProperty("sun.java.command"), "huhu");
//			ProcessBuilder	pb	= new ProcessBuilder("java", "-classpath", System.getProperty("java.class.path"), System.getProperty("sun.java.command"), "huhu");
//			pb.redirectOutput(new File("./out.txt"));
//			pb.redirectError(new File("./err.txt"));
//			pb.redirectInput(new File("./in.txt"));
//			pb.start();
//			System.exit(0);
			
			Runtime.getRuntime().exec(new String[]{"cmd", "/C", "start", "/B", "java", "-classpath", System.getProperty("java.class.path"), System.getProperty("sun.java.command"), "huhu", ">out.txt"});
			Runtime.getRuntime().exec(new String[]{"cmd", "/C", "java", "-classpath", System.getProperty("java.class.path"), System.getProperty("sun.java.command"), "huhu", ">out.txt"});
		}
		else
		{
			while(true)
			{
				Thread.sleep(1000);
				System.out.println(new Date()+": "+args[0]);
			}
		}
	}
}