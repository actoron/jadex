package jadex.tools.bpmn.runtime.task;

import jadex.tools.bpmn.editor.JadexBpmnPlugin;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
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
	 * @return
	 */
	private static ClassLoader initialize()
	{
		URLClassLoader workspaceClassLoader = null;

		final IWorkspace workspace = ResourcesPlugin.getWorkspace();
	    //IJavaModel javaModel = JavaCore.create(ResourcesPlugin.getWorkspace().getRoot());
	    
	    // retrieve all (also closed, expect hidden) projects from this root
	    IProject[] workspaceProjects = workspace.getRoot().getProjects();

		List<URL> urls = new UniqueEList<URL>();
		// compute the URLs for where the classes for these projects will be located.
		for (int i = 0; i < workspaceProjects.length; i++)
		{
			IProject iProject = workspaceProjects[i];
			IJavaProject javaProject = JavaCore.create(iProject);
		    
			try
			{
				urls.add(new File(iProject.getLocation() + "/" + javaProject.getOutputLocation().removeFirstSegments(1) + "/").toURI().toURL());
				
				// Compute the URLs for all the output folder of all the project dependencies and all libraries needed by these projects.
				for (IClasspathEntry classpathEntry :  javaProject.getResolvedClasspath(true))
				{
				  if (classpathEntry.getEntryKind() == IClasspathEntry.CPE_PROJECT)
				  {
				    IPath projectPath = classpathEntry.getPath();
				    IProject otherProject = workspace.getRoot().getProject(projectPath.segment(0));
				    IJavaProject otherJavaProject = JavaCore.create(otherProject);

				    try
					{
						urls.add(new File(otherProject.getLocation() + "/" + otherJavaProject.getOutputLocation().removeFirstSegments(1) + "/").toURI().toURL());
					}
					catch (JavaModelException e) { continue; }
					catch (MalformedURLException e) { continue; }
				  }
				  
				  // add libraries to urls
				  else if (classpathEntry.getEntryKind() == IClasspathEntry.CPE_LIBRARY)
				  {
					  IPath libraryPath = classpathEntry.getPath();
					  urls.add(libraryPath.toFile().toURI().toURL());
				  }
				}
			}
			catch (JavaModelException e){ continue; }
			catch (MalformedURLException e) { continue; }

		}

		// add the plugin path to the search path for new classloader
		final Bundle bundle = JadexBpmnPlugin.getDefault().getBundle();
		
		workspaceClassLoader = new URLClassLoader(urls.toArray(new URL[urls.size()])/*, bundle.getClass().getClassLoader()*/)
		{
			/* (non-Javadoc)
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
	
	public static List<Class> getClassesForPackage(String pckgname)
			throws ClassNotFoundException {
		// This will hold a list of directories matching the pckgname.
		// There may be more than one if a package is split over multiple
		// jars/paths
		List<Class> classes = new ArrayList<Class>();
		ArrayList<File> directories = new ArrayList<File>();
		try {
			ClassLoader cld = getWorkspaceClassLoader(false);
			if (cld == null) {
				throw new ClassNotFoundException("Can't get class loader.");
			}
			// Ask for all resources for the path
			Enumeration<URL> resources = cld.getResources(pckgname.replace('.',
					'/'));
			while (resources.hasMoreElements()) {
				URL res = resources.nextElement();
				if (res.getProtocol().equalsIgnoreCase("jar")) {
					JarURLConnection conn = (JarURLConnection) res
							.openConnection();
					JarFile jar = conn.getJarFile();
					for (JarEntry e : Collections.list(jar.entries())) {

						if (e.getName().startsWith(pckgname.replace('.', '/'))
								&& e.getName().endsWith(".class")
								&& !e.getName().contains("$")) {
							String className = e.getName().replace("/", ".")
									.substring(0, e.getName().length() - 6);
							System.out.println(className);
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
					if (file.endsWith(".class")) {
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

	public static List<Class> getClassessOfInterface(String thePackage,
			Class theInterface) {
		List<Class> classList = new ArrayList<Class>();
		try {
			for (Class discovered : getClassesForPackage(thePackage)) {
				if (Arrays.asList(discovered.getInterfaces()).contains(
						theInterface)) {
					classList.add(discovered);
				}
			}
		} catch (ClassNotFoundException ex) {
			Logger.getLogger(WorkspaceClassLoaderHelper.class.getName()).log(Level.SEVERE,
					null, ex);
		}

		return classList;
	}

}