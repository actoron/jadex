package jadex.editor.bpmn.runtime.task;

import jadex.editor.bpmn.editor.JadexBpmnEditor;
import jadex.editor.bpmn.editor.JadexBpmnEditorActivator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.common.util.UniqueEList;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.osgi.framework.Bundle;

class WorkspaceClassLoaderHelper
{

	private static ClassLoader workspaceClassLoader;

	/** Accessor to the workspace class loader */
	public static ClassLoader getWorkspaceClassLoader(boolean reInitalize)
	{
		if (workspaceClassLoader == null || reInitalize)
		{
			workspaceClassLoader = initialize();
		}
		return workspaceClassLoader;
	}

	/**
	 * Initialize the workspace class loader
	 * 
	 * @return
	 */
	private static ClassLoader initialize()
	{
		URLClassLoader workspaceClassLoader = null;

		final IWorkspace workspace = ResourcesPlugin.getWorkspace();
		final IPath workspacePath = workspace.getRoot().getLocation();
		//IJavaModel javaModel = JavaCore.create(ResourcesPlugin.getWorkspace().getRoot());

		// retrieve all (also closed, expect hidden) projects from this root
		IProject[] workspaceProjects = workspace.getRoot().getProjects();

		List<URL> urls = new UniqueEList<URL>();
		// compute the URLs for where the classes for 
		// these projects will be located.
		for (int i = 0; i < workspaceProjects.length; i++)
		{
			IProject iProject = workspaceProjects[i];
			IJavaProject javaProject = JavaCore.create(iProject);

			try
			{
				urls.add(new File(iProject.getLocation()
						+ "/"
						+ javaProject.getOutputLocation()
								.removeFirstSegments(1) + "/").toURI().toURL());

				// Compute the URLs for all the output folder of all the project
				// dependencies and all libraries needed by these projects.
				for (IClasspathEntry classpathEntry : javaProject
						.getResolvedClasspath(true))
				{
					if (classpathEntry.getEntryKind() == IClasspathEntry.CPE_PROJECT)
					{
						IPath projectPath = classpathEntry.getPath();
						IProject otherProject = workspace.getRoot().getProject(
								projectPath.segment(0));
						IJavaProject otherJavaProject = JavaCore
								.create(otherProject);

						try
						{
							urls.add(new File(otherProject.getLocation()
									+ "/"
									+ otherJavaProject.getOutputLocation()
											.removeFirstSegments(1) + "/")
									.toURI().toURL());
						}
						catch (JavaModelException e)
						{
							continue;
						}
						catch (MalformedURLException e)
						{
							continue;
						}
					}

					// add libraries to URLs
					else if (classpathEntry.getEntryKind() == IClasspathEntry.CPE_LIBRARY)
					{
						File jarFile = classpathEntry.getPath().toFile();
						if (!jarFile.exists())
						{
							jarFile = workspacePath.append(classpathEntry.getPath()).toFile();
						}
						
						if (jarFile.exists())
						{
							urls.add(jarFile.toURI().toURL());
						}
						else
						{
							JadexBpmnEditor.log("Can't determine jar path for library entry: " + classpathEntry.getPath(), new FileNotFoundException(jarFile.toURI().toURL().toString()), IStatus.ERROR);
						}
						
					}
				}
			}
			catch (JavaModelException e)
			{
				continue;
			}
			catch (MalformedURLException e)
			{
				continue;
			}

		}

		// add the plugin path to the search path for new classloader
		final Bundle bundle = JadexBpmnEditorActivator.getDefault().getBundle();

		workspaceClassLoader = new URLClassLoader(urls.toArray(new URL[urls
				.size()])/* , bundle.getClass().getClassLoader() */)
		{
			/*
			 * (non-Javadoc)
			 * 
			 * @see java.lang.ClassLoader#loadClass(java.lang.String)
			 */
			@Override
			public Class<?> loadClass(String className)
					throws ClassNotFoundException
			{
				try
				{
					// first look in the bundles classpath
					return bundle.loadClass(className);
				}
				catch (ClassNotFoundException classNotFoundException)
				{
					// second look in the URLs
					return super.loadClass(className);
				}
			}

		};

		return workspaceClassLoader;

	} // initialize()

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
		// Needed because the package may be split across multiple directories
		List<File> directories = new UniqueEList<File>();
		List<Class<?>> classes = new UniqueEList<Class<?>>();
		
		ClassLoader classLoader = WorkspaceClassLoaderHelper
			.getWorkspaceClassLoader(false);
		
		try {
			
			if (classLoader == null) {
				// return Collections.emptyList();
				throw new ClassNotFoundException("Can't get class loader.");
			}
			
			// Retrieve all resources for the path
			Enumeration<URL> resources = classLoader.getResources(pkgName.replace('.', '/'));
			while (resources.hasMoreElements()) {
				URL urlRessource = resources.nextElement();
				if ("jar".equalsIgnoreCase(urlRessource.getProtocol())) {
					JarURLConnection connection = (JarURLConnection) urlRessource
							.openConnection();
					JarFile jar = connection.getJarFile();
					for (JarEntry entry : Collections.list(jar.entries())) {

						if (entry.getName().startsWith(pkgName.replace('.', '/'))
								// given extension found
								&& entry.getName().endsWith(classSuffix)
								// no inner classes allowed
								&& !entry.getName().contains("$")
								// no sub packages allowed
								&& entry.getName().indexOf('/', pkgName.length()+1) == -1) {
							
							// remove class extension
							String className = entry.getName().replace("/", ".")
									.substring(0, entry.getName().length() - 6);

							// don't add abstract classes or interfaces to this list
							Class<?> clazz = classLoader.loadClass(className);
							if (!Modifier.isAbstract(clazz.getModifiers())
									&& !Modifier.isInterface(clazz.getModifiers()))
							{
								
								System.err.println("Found: " + className + " in " + urlRessource);
								
								classes.add(clazz);
							}
							
						}
					}
				} else
				{
					directories.add(new File(URLDecoder.decode(urlRessource.getPath(),
							"UTF-8")));
				}
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
						Class<?> clazz = classLoader.loadClass(pkgName + '.'
								+ file.substring(0, file.length() - 6));
						// don't add abstract classes or interfaces to this list
						if (!Modifier.isAbstract(clazz.getModifiers())
								&& !Modifier.isInterface(clazz.getModifiers()))
						{
							classes.add(clazz);
						}
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
	 * 
	 * @param pkgName The package to look for classes
	 * @param classInterface The interface class
	 * @return List of matching classes
	 */
	public static List<Class<?>> getClassesOfInterface(String pkgName,
			Class<?> classInterface)
	{
		return getClassesOfInterface(pkgName, ".class", classInterface);
	}
	
	/**
	 * Get all classes with the suffix (e.g. <code>.class</code>) in a package implementing a specific interface
	 * 
	 * @param pkgName The package to look for classes
	 * @param classSuffix The suffix e.g. "Task.class"
	 * @param classInterface The interface class
	 * @return List of matching classes
	 */
	public static List<Class<?>> getClassesOfInterface(String pkgName,
			String classSuffix, Class<?> classInterface)
	{
		List<Class<?>> classList = new UniqueEList<Class<?>>();
		try
		{
			for (Class<?> discoveredClass : getClassesForPackage(pkgName, classSuffix))
			{
				if (Arrays.asList(discoveredClass.getInterfaces()).contains(
						classInterface))
				{
					classList.add(discoveredClass);
				}
			}
		}
		catch (ClassNotFoundException ex)
		{
			JadexBpmnEditor.log("ClassNotFoundException in getClassessOfInterface("+pkgName+", "+classSuffix+", "+classInterface+")" ,ex, IStatus.WARNING);
		}

		return classList;
	}

	
// ---- reflection helper methods ----
	
	protected static String getStringFromMethod(Object source, String methodName)
	{

		Object returnValue = callUnparametrizedReflectionMethod(source, methodName);
		
		// check the return value
		if (returnValue instanceof String)
		{
			return (String) returnValue;
		}
		else if (returnValue == null)
		{
			return null;
		}
		else
		{
			JadexBpmnEditor.log("Wrong return value from method", new UnsupportedOperationException(
					"No String as result from '" + methodName + "' in : '"
							+ source +"'"), IStatus.WARNING);
			return "";
		}
	}
	
	protected static Class<?> getClassFromMethod(Object source, String methodName)
	{

		Object returnValue = callUnparametrizedReflectionMethod(source, methodName);
		
		// check the return value
		if (returnValue instanceof Class<?>)
		{
			return (Class<?>) returnValue;
		}
		else if (returnValue == null)
		{
			return null;
		}
		else
		{
			JadexBpmnEditor.log("Wrong return value from method", new UnsupportedOperationException(
					"No Class<?> as result from '" + methodName + "' in : '"
							+ source +"'"), IStatus.WARNING);
			return null;
		}
	}
	
	/**
	 * Call an unparameterized method on an object with reflection
	 * 
	 * @param source the source object to call the method
	 * @param methodName the method identifier
	 * @return the return value from called method, may be null
	 */
	protected static Object callUnparametrizedReflectionMethod(Object source, String methodName)
	{
		try
		{
			// use reflection
			Method method = source.getClass()
					.getMethod(methodName);
			Object returnValue = method.invoke(source);
			
			return returnValue;
		}
		catch (Exception e)
		{
			JadexBpmnEditor.log("Exception in callUnparametrizedReflectionMethod("+source+", "+methodName+")", e, IStatus.WARNING);
		}
		
		return null;
	}
	
	/**
	 * Call an Parameterized method on an object with reflection
	 * 
	 * @param source the source object to call the method
	 * @param methodName the method identifier
	 * @param args the method arguments
	 * @return the return value from called method, may be null
	 */
	protected static Object callParametrizedReflectionMethod(Object source, String methodName, Object...  args)
	{
		try
		{
			// use reflection
			Method method = source.getClass()
					.getMethod(methodName);
			Object returnValue = method.invoke(source, args);
			
			return returnValue;
		}
		catch (Exception e)
		{
			JadexBpmnEditor.log("Exception in callUnparametrizedReflectionMethod("+source+", "+methodName+")", e, IStatus.WARNING);
		}
		
		return null;
	}
}
