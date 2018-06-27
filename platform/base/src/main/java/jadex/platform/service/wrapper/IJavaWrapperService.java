package jadex.platform.service.wrapper;

import jadex.bridge.IResourceIdentifier;
import jadex.commons.future.IFuture;

/**
 *  Service to execute Java programs with a main method.
 */
public interface IJavaWrapperService
{
	/**
	 *  Execute a plain Java program as given by its main class.
	 *  
	 *  @param clazz	The class to be executed as Java program.
	 *  @param args	The arguments to the main method.
	 *  @return A future indication successful execution
	 *    (result: null) or failure (exception).
	 */
	public IFuture<Void>	executeJava(Class<?> clazz, String[] args);
	
	/**
	 *  Execute a plain Java program from a jar
	 *  as given by a file name.
	 *  Uses the main class name as specified in the manifest.
	 *  @param jarfile	File name of a jar file.
	 *  @param args	The arguments to the main method.
	 *  @return A future indication successful execution (result: null)
	 *    or failure (exception).
	 */
	public IFuture<Void>	executeJava(String jarfile, String[] args);
	
	/**
	 *  Execute a plain Java program from a jar
	 *  as given by a resource identifier.
	 *  Uses the main class name as specified in the manifest.
	 *  @param rid	The resource identifier for the jar
	 *    (global rid for maven artifact id, local rid for local file url).
	 *  @param args	The arguments to the main method.
	 *  @return A future indication successful execution (result: null)
	 *    or failure (exception).
	 */
	public IFuture<Void>	executeJava(IResourceIdentifier rid, String[] args);
}
