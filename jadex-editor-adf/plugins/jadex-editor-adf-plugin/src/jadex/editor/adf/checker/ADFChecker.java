package jadex.editor.adf.checker;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.JavaCore;

/**
 *  Project builder that loads XML ADFs and adds the errors
 *  as problem markers in the XML file.
 */
public class ADFChecker extends IncrementalProjectBuilder
{
	//-------- constants --------
	
	/** The builder id used by the Jadex nature to add/remove the builder to/from projects. */
	public static final String	BUILDER_ID	= "jadex.editor.adf.plugin.adfChecker";

	/** The marker type determines how ADF errors appear in the file and the problems view. */
	protected static final String	MARKER_TYPE	= "jadex.editor.adf.plugin.jadexproblem";

	/** The factories for corresponding file suffixes [suffix1, factoryclass1, suffix2, factoryclass2, ...]. */
	// Supported factories require a public String constructor!
	// todo: make configurable for easy addition of new kernels?
	protected String[]	factories	= new String[]
	{
		".agent.xml", "jadex.bdi.BDIAgentFactory",
		".capability.xml", "jadex.bdi.BDIAgentFactory",
		".application.xml", "jadex.application.ApplicationComponentFactory",
		".component.xml", "jadex.component.ComponentComponentFactory",
		".bpmn", "jadex.bpmn.BpmnFactory",
		".gpmn", "jadex.gpmn.GpmnFactory"
		// todo: support .java / .class models (micro, bdiv3)
	};
	
	//-------- attributes --------
	
	/** The map holding the cache. */
	protected Map<Object, Object>	cache;

	
	//-------- IncrementalProjectBuilder methods --------
	
	/**
	 *  Called by eclipse, when a project needs building.
	 */
	protected IProject[] build(int kind, Map args, final IProgressMonitor monitor) throws CoreException
	{
//		final int[]	cnt	= new int[2];
//		long	start	= System.nanoTime();
		
		// Check if only some resources need to be rebuild (delta).
		IResourceDelta delta	= kind!=FULL_BUILD ? getDelta(getProject()) : null;
		if(delta==null)
		{
			// Full build: apply visitor to all resources 
			getProject().accept(new IResourceVisitor()
			{
				public boolean visit(IResource resource)	throws CoreException
				{
//					cnt[0]++;
//					boolean	checked	= 
						checkXML(resource);
//					if(checked)
//						cnt[1]++;
					// if not canceled, continue visiting children.
					return !monitor.isCanceled();
				}
			});
		}
		else
		{
			// Incremental build: apply visitor only to changed resources
			delta.accept(new IResourceDeltaVisitor()
			{
				public boolean visit(IResourceDelta delta) throws CoreException
				{
					IResource resource = delta.getResource();
					
					// handle added resource
					if(delta.getKind()==IResourceDelta.ADDED)
					{
//							cnt[0]++;
//							boolean	checked	= 
								checkXML(resource);
//							if(checked)
//								cnt[1]++;
					}
					// handle removed resource
					else if(delta.getKind()==IResourceDelta.REMOVED)
					{
						// Ignore
					}
					// handle changed resource
					else if(delta.getKind()==IResourceDelta.CHANGED)
					{
//							cnt[0]++;
//							boolean	checked	= 
								checkXML(resource);
//							if(checked)
//								cnt[1]++;
					}
					
					// if not canceled, continue visiting children.
					return !monitor.isCanceled();
				}
			});
		}
		
//		long	end	= System.nanoTime();
//		System.out.println("ADFChecker took "+((end-start)/100000000/10.0)+" seconds for checking "+cnt[1]+" of "+cnt[0]+" resources.");
		
		// Clear cache
		cache	= null;
		
		// No dependent projects.
		return null;
	}
	
	//-------- check methods -------- 

	/**
	 *  Check a resource.
	 *  If the resource is an ADF, the model is loaded and
	 *  the errors (if any) are added as problem markers.
	 *  @return false When the resource has been ignored.
	 */
	protected boolean	checkXML(IResource resource) throws CoreException
	{
		boolean	checked	= false;
		
//		if(resource.toString().indexOf(".bpmn")!=-1 && resource.toString().indexOf("src")!=-1)
//			System.out.println("sdfklj hsfopdgh : "+resource);
		
		// Only check resources in source folders.
		if(resource instanceof IFile)
			if(JavaCore.create(resource.getProject()).isOnClasspath(resource))
		{
			IFile file = (IFile)resource;
			for(int i=0; !checked && i<factories.length; i+=2)
			{
				if(resource.getName().endsWith(factories[i]))
				{
					checked	= true;
					
					// delete old markers from prior builds (only deletes own marker type).
					deleteMarkers(file);
					
					// Get the report for the model and parse the error messages (if any).
					String	report	= getReport(file, factories[i+1]);
					if(report!=null)
					{
//						System.out.println(report);
						StringTokenizer stok	= new StringTokenizer(report, "\n");
						while(stok.hasMoreTokens())
						{
							String	error	= stok.nextToken();
							if(error.startsWith("\t"))	// Error messages start with tab as they are printed below element name.
							{
								error	= error.trim();
								int iline	= error.indexOf("(line ");
								int icolumn	= error.indexOf(", column ", iline);
								// Use line number, if contained in error message as '(line x, column y)'.
								if(iline!=-1 && icolumn!=-1)
								{
									int line	= Integer.parseInt(error.substring(iline+6, icolumn));
									addMarker(file, error, line, IMarker.SEVERITY_ERROR);
								}
								// Add error at top of file, if line number not available.
								else
								{
									addMarker(file, error, -1, IMarker.SEVERITY_ERROR);							
								}
							}
						}
					}
					
					IMarker[]	markers	= file.findMarkers(MARKER_TYPE, false, IResource.DEPTH_ZERO);
					if(markers.length==0)
					{
						addMarker(file, "File validated by Jadex ADF Checker", -1, IMarker.SEVERITY_INFO);
					}
				}
			}
		}
		
		return checked;
	}
	
	//-------- helper methods --------

	/**
	 *  Add a marker at the given location.
	 */
	protected void addMarker(IFile file, String message, int lineNumber, int severity) throws CoreException
	{
		IMarker marker = file.createMarker(MARKER_TYPE);
		marker.setAttribute(IMarker.MESSAGE, message);
		marker.setAttribute(IMarker.SEVERITY, severity);
		if(lineNumber == -1)
		{
			lineNumber = 1;
		}
		marker.setAttribute(IMarker.LINE_NUMBER, lineNumber);
	}
	
	/**
	 *  Remove all Jadex ADF markers from the given file.
	 */
	protected void deleteMarkers(IFile file) throws CoreException
	{
		file.deleteMarkers(MARKER_TYPE, false, IResource.DEPTH_ZERO);
	}
		
	/**
	 *  Get the report for a file.
	 */
	protected String	getReport(IFile file, String factoryname)	throws CoreException
	{
		String	ret	= null;
		try
		{
			// Use the project class loader to
			// 1. instantiate the factory
			// 2. load the model and get the error report (if any).
			String	filename	= file.getRawLocation().toPortableString();
			IProject	project	= file.getProject();
			Object[]	tmp	= getFactory(project, factoryname);
			Object	factory	= tmp[0];
			ClassLoader	cl	= (ClassLoader)tmp[1];
			Method	loadmodel	= (Method)tmp[2];
			Object modelfut	= loadmodel.invoke(factory, new Object[]{filename, null,
				loadmodel.getParameterTypes()[2].getName().indexOf("ClassLoader")!=-1 ? cl : null});
			Object	modelinfo	= modelfut.getClass().getMethod("get", new Class[]{Class.forName("jadex.commons.future.ISuspendable", true, cl)})
				.invoke(modelfut, new Object[]{Class.forName("jadex.commons.future.ThreadSuspendable", true, cl).newInstance()});
			Object	report	= modelinfo.getClass().getMethod("getReport", new Class[0])
				.invoke(modelinfo, new Object[0]);
			if(report!=null)
			{
				ret	= (String)report.getClass().getMethod("getErrorText", new Class[0])
					.invoke(report, new Object[0]);
			}
		}
		catch(InvocationTargetException e)
		{
//			e.printStackTrace();
			addMarker(file, "Cannot validate ADF due to: "+e.getTargetException(), -1, IMarker.SEVERITY_WARNING);
//			addMarker(file, "Cannot validate ADF due to: "+e.getTargetException(), -1, IMarker.SEVERITY_ERROR);
		}
		catch(Exception e)
		{
//			e.printStackTrace();
			addMarker(file, "Cannot validate ADF due to: "+e, -1, IMarker.SEVERITY_WARNING);
//			addMarker(file, "Cannot validate ADF due to: "+e, -1, IMarker.SEVERITY_ERROR);
		}

		return ret;
	}
	
	//-------- caching for speed --------
	
	/**
	 *  Get the factory object for the given factory name.
	 *  @return An array containing [factory object, class loader, loadModel() method].
	 */
	protected Object[]	getFactory(IProject project, String factoryname) throws Exception
	{
		Object	key	= Arrays.asList(new Object[]{project, factoryname});
		Object[]	ret	= cache!=null ? (Object[])cache.get(key) : null;
		if(ret==null)
		{
			ClassLoader	cl	= getClassLoader(project);
			Class<?>	clazz	= Class.forName(factoryname, true, cl);
			Constructor<?>	con	= clazz.getConstructor(new Class[]{String.class});
			Object	factory	= con.newInstance(new Object[]{"dummy"});
			Method	load;
			try
			{
				// Jadex 2.0 (use classloader)
				load	= factory.getClass().getMethod("loadModel", new Class[]{String.class, String[].class, ClassLoader.class});
			}
			catch(NoSuchMethodException e)
			{
				// Jadex 2.1 (use resource identifier)
				load	= factory.getClass().getMethod("loadModel", new Class[]{String.class, String[].class, Class.forName("jadex.bridge.IResourceIdentifier", true, cl)});				
			}
			
			ret	= new Object[]{factory, cl, load};
			if(cache==null)
			{
				cache	= new HashMap<Object, Object>();
			}
			cache.put(key, ret);
		}
		return ret;
	}
	
	/**
	 *  Get the class loader for a project.
	 */
	protected ClassLoader	getClassLoader(IProject project)
	{
		ClassLoader	ret	= cache!=null ? (ClassLoader)cache.get(project) : null;
		if(ret==null)
		{
			ret	= SClassLoader.getProjectClassLoader(project);
			if(cache==null)
			{
				cache	= new HashMap<Object, Object>();
			}
			cache.put(project, ret);
		}
		return ret;
	}
}
