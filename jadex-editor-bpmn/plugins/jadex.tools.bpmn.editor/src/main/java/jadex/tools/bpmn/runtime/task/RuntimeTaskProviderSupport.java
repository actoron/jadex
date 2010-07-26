/**
 * 
 */
package jadex.tools.bpmn.runtime.task;

import jadex.tools.bpmn.editor.JadexBpmnEditor;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.common.util.UniqueEList;

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


	// ---- helper methods ----
	
	/**
	 * Loads a class from the workspace and call its getTaskMetaInfo method
	 * to retrieve the TaskMetaInfo.
	 * @param className
	 * @return TaskMetaInfo for class if provided, else null
	 */
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
			JadexBpmnEditor.log(e, IStatus.WARNING);
		} catch (InstantiationException e) {
			JadexBpmnEditor.log(e, IStatus.WARNING);
		} catch (IllegalAccessException e) {
			JadexBpmnEditor.log(e, IStatus.WARNING);
		} catch (SecurityException e) {
			JadexBpmnEditor.log(e, IStatus.WARNING);
		} catch (NoSuchMethodException e) {
			JadexBpmnEditor.log(e, IStatus.WARNING);
		} catch (IllegalArgumentException e) {
			JadexBpmnEditor.log(e, IStatus.WARNING);
		} catch (InvocationTargetException e) {
			JadexBpmnEditor.log(e, IStatus.WARNING);
		}

		return null;
	}
	
	
    /**
     * 
     * @param pckgname
     * @return List of classes in package
     * @throws ClassNotFoundException
     */
	public static List<Class<?>> getClassesForPackage(String pckgname)
	throws ClassNotFoundException {
		return getClassesForPackage(pckgname, ".class");
	}
    
    /**
     * Search for classes within a package with given suffix.
     * @param pkgName The package to search
     * @param classSuffix The suffix of the class name e.g.: "*Task.class"
     * @return List of matching classes in package
     * @throws ClassNotFoundException
     */
	public static List<Class<?>> getClassesForPackage(String pkgName, String classSuffix)
			throws ClassNotFoundException {
		// List of directories matching the package name. 
		// Package may be split across multiple directories
		List<File> directories = new UniqueEList<File>();
		List<Class<?>> classes = new UniqueEList<Class<?>>();
		
		try {
			ClassLoader classLoader = WorkspaceClassLoaderHelper
				.getWorkspaceClassLoader(false);

			if (classLoader == null) {
				// return Collections.emptyList();
				throw new ClassNotFoundException("Can't get class loader.");
			}
			// Retrieve all resources for the path
			Enumeration<URL> resources = classLoader.getResources(pkgName.replace('.',
					'/'));
			while (resources.hasMoreElements()) {
				URL urlRessource = resources.nextElement();
				if ("jar".equalsIgnoreCase(urlRessource.getProtocol())) {
					JarURLConnection connection = (JarURLConnection) urlRessource
							.openConnection();
					JarFile jar = connection.getJarFile();
					for (JarEntry entry : Collections.list(jar.entries())) {

						if (entry.getName().startsWith(pkgName.replace('.', '/'))
								&& entry.getName().endsWith(classSuffix)
								&& !entry.getName().contains("$")) {
							String className = entry.getName().replace("/", ".")
									.substring(0, entry.getName().length() - 6);
							classes.add(Class.forName(className));
						}
					}
				} else
					directories.add(new File(URLDecoder.decode(urlRessource.getPath(),
							"UTF-8")));
			}
		} catch (NullPointerException x) {
			throw new ClassNotFoundException(pkgName
					+ " does not appear to be "
					+ "a valid package (Null pointer exception)");
		} catch (UnsupportedEncodingException encex) {
			throw new ClassNotFoundException(pkgName
					+ " does not appear to be "
					+ "a valid package (Unsupported encoding)");
		} catch (IOException ioex) {
			throw new ClassNotFoundException(
					"IOException was thrown when trying "
							+ "to get all resources for " + pkgName);
		}

		for (File directory : directories) {
			if (directory.exists()) {
				String[] files = directory.list();
				for (String file : files) {
					// we are only interested in .class files with specific suffix
					if (file.endsWith(classSuffix)) {
						// remove .class extension
						classes.add(Class.forName(pkgName + '.'
								+ file.substring(0, file.length() - 6)));
					}
				}
			} else {
				throw new ClassNotFoundException(pkgName + " ("
						+ directory.getPath()
						+ ") does not appear to be a valid package");
			}
		}
		return classes;
	}

	/**
	 * Get all classes in a package implementing a specific interface
	 * @param pkgName
	 * @param classInterface
	 * @return List of classes
	 */
	public static List<Class<?>> getClassessOfInterface(String pkgName,
			Class<?> classInterface) {
		List<Class<?>> classList = new UniqueEList<Class<?>>();
		try {
			for (Class<?> discoveredClasses : getClassesForPackage(pkgName)) {
				if (Arrays.asList(discoveredClasses.getInterfaces()).contains(
						classInterface)) {
					classList.add(discoveredClasses);
				}
			}
		} catch (ClassNotFoundException ex) {
			JadexBpmnEditor.log(ex, IStatus.WARNING);
		}

		return classList;
	}


}
