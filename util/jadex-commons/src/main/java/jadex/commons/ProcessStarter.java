package jadex.commons;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *  The process starter allows for starting another process in a completely
 *  detached way, i.e. the std.out and std.err streams are automatically read.
 *  
 *  The process that starts the process starter can be savely terminated.
 */
public class ProcessStarter
{
	protected static final Set<String> reserved;
	
	static
	{
		reserved	= new HashSet<String>();
		reserved.add("-external"); // false=other java proc
		reserved.add("-stdout");
		reserved.add("-stderr");
		reserved.add("-dir");
	}
	
	/**
	 *  Start a java or non-java process. 
	 */
	public static void main(String[] args)
	{
		if(args.length<2)
			throw new IllegalArgumentException("Syntax is -external -true|false [-stdout file] [-stderr file] cmds");
		
//		System.out.println("enter main");
		
		OutputStream out = null;
		OutputStream err = null;
		try
		{
			Map<String, Object> nargs = processArguments(args);
			
			out = nargs.get("-stdout")!=null? new FileOutputStream(new File((String)nargs.get("-stdout"))): new NullOutputStream();
			err = nargs.get("-stderr")!=null? new FileOutputStream(new File((String)nargs.get("-stderr"))): new NullOutputStream();
			
			out.write("starting using process starter\n".getBytes());
			
			if("true".equals(nargs.get("-external")))
			{
				String[] pargs = (String[])nargs.get("args");
				String dir = nargs.get("-dir")!=null? (String)nargs.get("-dir"): ".";
				startExternalProcess(dir, pargs[0], out, err);
			}
			else
			{
				startJavaProcess((String[])nargs.get("args"), out, err);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			close(out);
			close(err);
			throw new RuntimeException(e);
		}
	}
	
	/**
	 *  Process the cmd line args.
	 */
	protected static Map<String, Object> processArguments(String[] args)
	{
		Map<String, Object> ret = new HashMap<String, Object>();
		
		int i=0;
		for(; i<args.length && reserved.contains(args[i]); i++)
		{
			ret.put(args[i], args[++i]);
		}
		
		if(i<args.length)
		{
			String[] nargs = new String[args.length-i];
			System.arraycopy(args, i, nargs, 0, nargs.length);
			ret.put("args", nargs);
		}
		
		return ret;
	}
	
	/**
	 *  Start an external process.
	 */
	public static void startExternalProcess(String dir, String cmd, OutputStream out, OutputStream err)
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
//			new Thread(new StreamCopy(proc.getInputStream(), System.out)).start(); // the input is the output stream :-(
//			new Thread(new StreamCopy(proc.getErrorStream(), System.err)).start();
			new Thread(new StreamCopy(proc.getInputStream(), out)).start(); // the input is the output stream :-(
			new Thread(new StreamCopy(proc.getErrorStream(), err)).start();
			
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
	public static void startJavaProcess(String[] parts, OutputStream out, OutputStream err)
	{
//		System.out.println("starting java in procs: "+SUtil.arrayToString(parts));
		try
		{
			// empty streams of this process to dev null 
//			System.setOut(new PrintStream(System.out));
//			System.setErr(new PrintStream(System.err));
			System.setOut(new PrintStream(out));
			System.setErr(new PrintStream(err));
			new Thread(new StreamCopy(System.in, new NullOutputStream())).start();
			
//			String[] parts = SUtil.splitCommandline(cmd);
			String[] args = new String[parts.length-1];
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
	
	/**
	 *  Close a stream.
	 */
	protected static void close(OutputStream os)
	{	
		try
		{
			if(os!=null)
			{
				os.close();
			}
		}
		catch(Exception e)
		{
		}
	}
}
