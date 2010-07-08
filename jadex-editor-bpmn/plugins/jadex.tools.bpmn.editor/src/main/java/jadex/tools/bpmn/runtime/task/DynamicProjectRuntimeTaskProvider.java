/**
 * 
 */
package jadex.tools.bpmn.runtime.task;

import jadex.tools.bpmn.editor.JadexBpmnPlugin;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.common.util.UniqueEList;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaModel;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.osgi.framework.Bundle;

/**
 * @author Claas
 *
 */
public class DynamicProjectRuntimeTaskProvider implements IRuntimeTaskProvider
{


	/* (non-Javadoc)
	 * @see jadex.tools.bpmn.runtime.task.IRuntimeTaskProvider#getAvailableTaskImplementations()
	 */
	@Override
	public String[] getAvailableTaskImplementations()
	{
		WorkspaceClassLoaderHelper.initialize();
		// TODO Auto-generated method stub
		return new String[]{"useless"};
	}

	/* (non-Javadoc)
	 * @see jadex.tools.bpmn.runtime.task.IRuntimeTaskProvider#getTaskMetaInfoFor(java.lang.String)
	 */
	@Override
	public TaskMetaInfo getTaskMetaInfoFor(String implementationClass)
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	
	
	
	

}

class WorkspaceClassLoaderHelper
{
	public static void initialize()
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
		
		System.out.println("\n-----------------------------------\n");
		for (URL url : urls)
		{
			System.out.println(url);
		}
		System.out.println("\n-----------------------------------\n");
		
		try
		{
			IRuntimeTaskProvider provider = (IRuntimeTaskProvider) workspaceClassLoader.loadClass("some.pack.age.MyTestProvider.class").newInstance();
			System.out.println(provider.getAvailableTaskImplementations());
		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (InstantiationException e)
		{
			e.printStackTrace();
		}
		catch (IllegalAccessException e)
		{
			e.printStackTrace();
		}
		
		
	} // initialize()
}