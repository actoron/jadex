package jadex.commons;

import java.io.File;
import java.io.PrintStream;
import java.lang.reflect.Method;

/**
 *  The process starter allows for starting another process in a completely
 *  detached way, i.e. the std.out and std.err streams are automatically read.
 *  
 *  The process that starts the process starter can be savely terminated.
 */
public class ProcessStarter
{
	/**
	 *  Start a java or non-java process. 
	 */
	public static void main(String[] args)
	{
		if(args.length<2)
			throw new IllegalArgumentException("Syntax is (-java cmds+)|(-external dir! cmd)");
		
		if("-java".equals(args[0]))
		{
			String[] nargs = new String[args.length-1];
			System.arraycopy(args, 1, nargs, 0, args.length-1);
			startJavaProcess(nargs);
		}
		else if("-external".equals(args[0]))
		{
			if(args.length==2)
			{
				startExternalProcess(null, args[1]);
			}
			else
			{
				startExternalProcess(args[1], args[2]);
			}
		}
		else
		{
			throw new IllegalArgumentException("Syntax is (-java cmds+)|(-external dir! cmd)");
		}
	}
	
	/**
	 *  Start an external process.
	 */
	public static void startExternalProcess(String dir, String cmd)
	{
		try
		{
			File curdir = new File(dir);
			if(!curdir.exists())
			{
				curdir.mkdirs();
			}
			
			final Process proc = Runtime.getRuntime().exec(cmd, null, curdir);

			// empty streams of subprocess to dev null 
			new Thread(new StreamCopy(proc.getInputStream(), new NullOutputStream())).start(); // the input is the output stream :-(
			new Thread(new StreamCopy(proc.getErrorStream(), new NullOutputStream())).start();
			
			proc.waitFor();
			System.exit(0);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw new RuntimeException("Could not start process. Reason: "+ ex.getMessage());
		}
	}
	
	/**
	 *  Start a java process.
	 */
	public static void startJavaProcess(String[] parts)
	{
		try
		{
			// empty streams of this process to dev null 
			System.setOut(new PrintStream(new NullOutputStream()));
			System.setErr(new PrintStream(new NullOutputStream()));
			new Thread(new StreamCopy(System.in, new NullOutputStream())).start();
			
//			String[] parts = SUtil.splitCommandline(cmd);
			String[] args = new String[parts.length];
			System.arraycopy(parts, 1, args, 0, parts.length-1);
			Class<?> mcl = SReflect.classForName(parts[0], null);
			Method m = mcl.getMethod("main", new Class[]{String[].class});
			m.invoke(null, new Object[]{args});
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw new RuntimeException("Could not start process. Reason: "+ ex.getMessage());
		}
	}
}
