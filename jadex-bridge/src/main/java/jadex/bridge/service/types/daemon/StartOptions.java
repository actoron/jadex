package jadex.bridge.service.types.daemon;


/**
 *   java [ options ] class [ argument ... ]
 *   java [ options ] -jar file.jar [ argument ... ]
 */
public class StartOptions
{
	/** The path to the java command. */
	protected String javacmd;
	
	/** The classpath. */
	protected String classpath;
	
	/** The main class to start. */
	protected String main;
	
	/** The program arguments. */
	protected String prgmargs; 
	
	/** The vm arguments. */
	protected String vmargs;
	
	/** The start directory. */
	protected String startdir;

	/**
	 *  Get the java command.
	 *  @return The java command.
	 */
	public String getJavaCommand()
	{
		return javacmd==null? "java": javacmd;
	}

	/**
	 *  Set the java.
	 *  @param java The java to set.
	 */
	public void setJavaCommand(String javacmd)
	{
		this.javacmd = javacmd;
	}

	/**
	 *  Get the classpath.
	 *  @return the classpath.
	 */
	public String getClassPath()
	{
		return classpath;
	}

	/**
	 *  Set the classpath.
	 *  @param classpath The classpath to set.
	 */
	public void setClassPath(String classpath)
	{
		this.classpath = classpath;
	}

	/**
	 *  Get the main.
	 *  @return the main.
	 */
	public String getMain()
	{
		return main;
	}

	/**
	 *  Set the main.
	 *  @param main The main to set.
	 */
	public void setMain(String main)
	{
		this.main = main;
	}

	/**
	 *  Get the prgmargs.
	 *  @return the prgmargs.
	 */
	public String getProgramArguments()
	{
		return prgmargs;
	}

	/**
	 *  Set the prgmargs.
	 *  @param prgmargs The prgmargs to set.
	 */
	public void setProgramArguments(String prgmargs)
	{
		this.prgmargs = prgmargs;
	}

	/**
	 *  Get the vmargs.
	 *  @return the vmargs.
	 */
	public String getVMArguments()
	{
		return vmargs;
	}

	/**
	 *  Set the vmargs.
	 *  @param vmargs The vmargs to set.
	 */
	public void setVMArguments(String vmargs)
	{
		this.vmargs = vmargs;
	}
	
	/**
	 *  Get the startdir.
	 *  @return The startdir.
	 */
	public String getStartDirectory()
	{
		return startdir==null? ".": startdir;
	}

	/**
	 *  Set the startdir.
	 *  @param startdir The startdir to set.
	 */
	public void setStartDirectory(String startdir)
	{
		this.startdir = startdir;
	}

	/**
	 *  Get the complete start command.
	 */
	public String getStartCommand()
	{
		// [path]java 
		StringBuffer cmd = new StringBuffer().append(getJavaCommand());
		
		// -cp
		if(classpath!=null && classpath.length()>0)
		{
			cmd.append(" -classpath \"").append(classpath).append("\"");
		}
//		for(int i=0; i<classpath.length; i++)
//		{
//			if(i==0)
//				cmd.append(" -cp ");
//			else
//				cmd.append(File.pathSeparator);
//			cmd.append("\"").append(classpath[i]).append("\"");
//		}
		
		// additional arguments
		if(getVMArguments()!=null)
			cmd.append(" ").append(getVMArguments());
		
		// main class or jar
		if(getMain()==null)
			throw new RuntimeException("No main class or executable jar specified: "+this);
		cmd.append(" ").append(getMain());
		
		// program arguments
		if(getProgramArguments()!=null)
			cmd.append(" ").append(getProgramArguments());
		
//		System.out.println("starting with: "+cmd.toString());
		
		return cmd.toString();
	}

	/**
	 *  String representation.
	 */
	public String toString()
	{
		return "StartOptions(javacmd="+javacmd
			+", classpath="+classpath
			+", main="+main
			+", prgmargs="+prgmargs
			+", vmargs="+vmargs
			+", startdir="+startdir+")";
	}
}
