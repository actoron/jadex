/**
 * 
 */
package jadex.tools.bpmn.runtime.task;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	 * Find classes in a package implementing a specific marker
	 * @param searchPackage
	 * @param marker
	 * @return
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	private static Class[] getClasses(String searchPackage, Class<?> marker)
			throws ClassNotFoundException, IOException {

		ClassLoader classLoader = WorkspaceClassLoaderHelper
				.getWorkspaceClassLoader(false);
		
		if (classLoader == null)
			return new Class[0];

		// retrieve all resources from the class loader path
		String path = searchPackage.replace('.', '/');
		Enumeration<URL> workspaceResources = classLoader.getResources(path);
		// iterate over resources and save directory
		List<File> directories = new UniqueEList<File>();
		while (workspaceResources.hasMoreElements()) {
			URL resource = workspaceResources.nextElement();
			directories.add(new File(resource.getFile()));
		}

		// find all class files in package directories
		ArrayList<Class<?>> packageClasses = new ArrayList<Class<?>>();
		for (File directory : directories) {
			packageClasses.addAll(findPackageClassesInDirectory(directory, searchPackage));
		}
		
		// don't filter with null argument
		if (marker == null)
		{
			return packageClasses.toArray(new Class[packageClasses.size()]); 
		}
		
		// filter for marker interface
		ArrayList<Class<?>> markerClasses = new ArrayList<Class<?>>();
		for (Class<?> clazz : markerClasses) 
		{
			if (marker.isAssignableFrom(clazz))
			{
				markerClasses.add(clazz);
			}
		}
		
		return markerClasses.toArray(new Class[markerClasses.size()]);
	}
    
	/**
	 * Find classes contained in a package in a specific directory
	 * @param directory
	 * @param packageName
	 * @return
	 * @throws ClassNotFoundException
	 */
    private static List<Class<?>> findPackageClassesInDirectory(File directory, String packageName) throws ClassNotFoundException {
        
    	List<Class<?>> directoryClasses = new ArrayList<Class<?>>();
        if (!directory.exists()) {
            return directoryClasses;
        }
        
        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                assert !file.getName().contains(".");
                directoryClasses.addAll(findPackageClassesInDirectory(file, packageName + "." + file.getName()));
            } else if (file.getName().endsWith(".class")) {
                directoryClasses.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
            }
        }
        
        return directoryClasses;
    }


}
