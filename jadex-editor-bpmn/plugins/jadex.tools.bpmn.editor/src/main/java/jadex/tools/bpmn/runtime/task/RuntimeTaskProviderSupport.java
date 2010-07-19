/**
 * 
 */
package jadex.tools.bpmn.runtime.task;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Claas
 *
 */
public abstract class RuntimeTaskProviderSupport implements IRuntimeTaskProvider
{

	// ---- constants ----
	
	/** The default meta info if class not found */
	static final TaskMetaInfo NO_TASK_META_INFO_PROVIDED = new TaskMetaInfo("No TaskMetaInfo provided", new ParameterMetaInfo[0]);

	/** The implementing method */
	public static final String GET_TASK_META_INFO_METHOD_NAME = "getTaskMetaInfo";

	
	// ---- attributes ----
	
	/**
	 * The provided task implementation classes for this {@link IRuntimeTaskProvider}
	 */
	protected String[] taskImplementations;
	
	/**
	 * Map for provided runtime classes<p>
	 * Map(ClassName, TaskMetaInfo)
	 */
	protected Map<String, TaskMetaInfo> metaInfoMap;


	// ---- constructor ----
	
	/**
	 * Empty default constructor
	 */
	public RuntimeTaskProviderSupport()
	{
		super();
		Package classPackage = this.getClass().getPackage();
		System.out.println(classPackage.toString());
		
		taskImplementations = new String[]{""};
		metaInfoMap = new HashMap<String, TaskMetaInfo>();
	}

	/**
	 * @param taskImplementations
	 * @param metaInfoMap
	 */
	public RuntimeTaskProviderSupport(String[] taskImplementations,
			HashMap<String, TaskMetaInfo> metaInfoMap)
	{
		super();
		this.taskImplementations = taskImplementations;
		this.metaInfoMap = metaInfoMap;
	}


	// ---- interface methods ----

	/**
	 * Get the provided task implementations
	 * Per default return an String[] with an empty String
	 * @see jadex.tools.bpmn.runtime.task.IRuntimeTaskProvider#getAvailableTaskImplementations()
	 */
	public String[] getAvailableTaskImplementations()
	{
		return taskImplementations;
	}

	
	/**
	 * Get {@link TaskMetaInfo} for provided task implementation.
	 * @see jadex.tools.bpmn.runtime.task.IRuntimeTaskProvider#getTaskMetaInfoFor(java.lang.String)
	 */
	public TaskMetaInfo getTaskMetaInfoFor(String className)
	{
		if (metaInfoMap != null)
		{
			TaskMetaInfo info;
			if (metaInfoMap.containsKey(className))
			{
				info = metaInfoMap.get(className);
				if (info != null) 
					return info;
			}
			
			info = loadMetaInfo(className);
			if (info != null)
			{
				metaInfoMap.put(className, info);
				return info;
			}
		}
		
		return NO_TASK_META_INFO_PROVIDED;
	}


	private TaskMetaInfo loadMetaInfo(String className) {
		
		ClassLoader classLoader = WorkspaceClassLoaderHelper
				.getWorkspaceClassLoader(false);

		if (classLoader == null)
			return null;

		try {
			Class<?> taskImpl = classLoader.loadClass(className);
			Object taskInstance = taskImpl.newInstance();
			Method myMethod = taskImpl
					.getMethod(GET_TASK_META_INFO_METHOD_NAME);
			TaskMetaInfo returnValue = (TaskMetaInfo) myMethod
					.invoke(taskInstance);

			return returnValue;

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}

		return null;
	}
	
	
    /**
     * 
     * @param pckgname
     * @return Luist of classes in package
     * @throws ClassNotFoundException
     */
	public static List<Class<?>> getClassesForPackage(String pckgname)
	throws ClassNotFoundException {
		return getClassesForPackage(pckgname, ".class");
	}
    
    /**
     * Search for classes within a package with given suffix.
     * @param pckgname The package to search
     * @param classSuffix The suffix of the class name e.g.: "*Task.class"
     * @return List of matching classes in package
     * @throws ClassNotFoundException
     */
	public static List<Class<?>> getClassesForPackage(String pckgname, String classSuffix)
			throws ClassNotFoundException {
		// A list of directories matching the package.
		// May be more than one if a package is split
		ArrayList<File> directories = new ArrayList<File>();
		List<Class<?>> classes = new ArrayList<Class<?>>();
		
		try {
			ClassLoader classLoader = WorkspaceClassLoaderHelper
				.getWorkspaceClassLoader(false);

			if (classLoader == null) {
				// return Collections.emptyList();
				throw new ClassNotFoundException("Can't get class loader.");
			}
			// Ask for all resources for the path
			Enumeration<URL> resources = classLoader.getResources(pckgname.replace('.',
					'/'));
			while (resources.hasMoreElements()) {
				URL res = resources.nextElement();
				if ("jar".equalsIgnoreCase(res.getProtocol())) {
					JarURLConnection conn = (JarURLConnection) res
							.openConnection();
					JarFile jar = conn.getJarFile();
					for (JarEntry entry : Collections.list(jar.entries())) {

						if (entry.getName().startsWith(pckgname.replace('.', '/'))
								&& entry.getName().endsWith(classSuffix)
								&& !entry.getName().contains("$")) {
							String className = entry.getName().replace("/", ".")
									.substring(0, entry.getName().length() - 6);
							classes.add(Class.forName(className));
						}
					}
				} else
					directories.add(new File(URLDecoder.decode(res.getPath(),
							"UTF-8")));
			}
		} catch (NullPointerException x) {
			throw new ClassNotFoundException(pckgname
					+ " does not appear to be "
					+ "a valid package (Null pointer exception)");
		} catch (UnsupportedEncodingException encex) {
			throw new ClassNotFoundException(pckgname
					+ " does not appear to be "
					+ "a valid package (Unsupported encoding)");
		} catch (IOException ioex) {
			throw new ClassNotFoundException(
					"IOException was thrown when trying "
							+ "to get all resources for " + pckgname);
		}

		// For every directory identified capture all the .class files
		for (File directory : directories) {
			if (directory.exists()) {
				// Get the list of the files contained in the package
				String[] files = directory.list();
				for (String file : files) {
					// we are only interested in .class files
					if (file.endsWith(classSuffix)) {
						// removes the .class extension
						classes.add(Class.forName(pckgname + '.'
								+ file.substring(0, file.length() - 6)));
					}
				}
			} else {
				throw new ClassNotFoundException(pckgname + " ("
						+ directory.getPath()
						+ ") does not appear to be a valid package");
			}
		}
		return classes;
	}

	public static List<Class<?>> getClassessOfInterface(String aPackage,
			Class<?> aInterface) {
		List<Class<?>> classList = new ArrayList<Class<?>>();
		try {
			for (Class<?> discovered : getClassesForPackage(aPackage)) {
				if (Arrays.asList(discovered.getInterfaces()).contains(
						aInterface)) {
					classList.add(discovered);
				}
			}
		} catch (ClassNotFoundException ex) {
			Logger.getLogger(RuntimeTaskProviderSupport.class.getName()).log(Level.SEVERE,
					null, ex);
		}

		return classList;
	}


}
