package jadex.editor.adf.checker;

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

/**
 *  Project builder that loads XML ADFs and adds the errors
 *  as problem markers in the XML file.
 */
public class ADFChecker extends IncrementalProjectBuilder
{
	//-------- constants --------
	
	/** The factories for corresponding file suffixes. */
	protected String[]	factories	= new String[]
	{
		".agent.xml", "jadex.bdi.BDIAgentFactory",
		".capability.xml", "jadex.bdi.BDIAgentFactory",
		".application.xml", "jadex.application.ApplicationComponentFactory"
	};
	
	class SampleDeltaVisitor implements IResourceDeltaVisitor
	{
		/*
		 * (non-Javadoc)
		 * @see
		 * org.eclipse.core.resources.IResourceDeltaVisitor#visit(org.eclipse
		 * .core.resources.IResourceDelta)
		 */
		public boolean visit(IResourceDelta delta) throws CoreException
		{
			IResource resource = delta.getResource();
			switch(delta.getKind())
			{
				case IResourceDelta.ADDED:
					// handle added resource
					checkXML(resource);
					break;
				case IResourceDelta.REMOVED:
					// handle removed resource
					break;
				case IResourceDelta.CHANGED:
					// handle changed resource
					checkXML(resource);
					break;
			}
			// return true to continue visiting children.
			return true;
		}
	}

	class SampleResourceVisitor implements IResourceVisitor
	{
		public boolean visit(IResource resource)
		{
			checkXML(resource);
			// return true to continue visiting children.
			return true;
		}
	}

	public static final String	BUILDER_ID	= "jadex-editor-adf.adfChecker";

	private static final String	MARKER_TYPE	= IMarker.PROBLEM;

	private void addMarker(IFile file, String message, int lineNumber,
			int severity)
	{
		try
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
		catch(CoreException e)
		{
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.internal.events.InternalBuilder#build(int,
	 * java.util.Map, org.eclipse.core.runtime.IProgressMonitor)
	 */
	protected IProject[] build(int kind, Map args, IProgressMonitor monitor)
			throws CoreException
	{
		if(kind == FULL_BUILD)
		{
			fullBuild(monitor);
		}
		else
		{
			IResourceDelta delta = getDelta(getProject());
			if(delta == null)
			{
				fullBuild(monitor);
			}
			else
			{
				incrementalBuild(delta, monitor);
			}
		}
		return null;
	}

	void checkXML(IResource resource)
	{
		if(!resource.isDerived() &&	resource instanceof IFile)
		{
			IFile file = (IFile)resource;
			for(int i=0; i<factories.length; i+=2)
			{
				if(resource.getName().endsWith(factories[i]))
				{
					deleteMarkers(file);
					try
					{
						String	report	= getReport(file, factories[i+1]);
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
								if(iline!=-1 && icolumn!=-1)
								{
									int line	= Integer.parseInt(error.substring(iline+6, icolumn));
									addMarker(file, error, line, IMarker.SEVERITY_ERROR);
								}
								else
								{
									addMarker(file, error, -1, IMarker.SEVERITY_ERROR);							
								}
							}
						}
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
				}
			}			
		}
	}

	private void deleteMarkers(IFile file)
	{
		try
		{
			file.deleteMarkers(MARKER_TYPE, false, IResource.DEPTH_ZERO);
		}
		catch(CoreException ce)
		{
		}
	}

	protected void fullBuild(final IProgressMonitor monitor)
			throws CoreException
	{
		try
		{
			getProject().accept(new SampleResourceVisitor());
		}
		catch(CoreException e)
		{
		}
	}

	protected void incrementalBuild(IResourceDelta delta,
			IProgressMonitor monitor) throws CoreException
	{
		// the visitor does the work.
		delta.accept(new SampleDeltaVisitor());
	}
	
	//-------- helper methods --------
	
	/**
	 *  Get the report for a file.
	 */
	protected String	getReport(IFile file, String factoryname)	throws Exception
	{
		String	ret	= null;
		String	filename	= file.getRawLocation().toPortableString();
		IProject	project	= file.getProject();
		ClassLoader	cl	= SClassLoader.getProjectClassLoader(project);
		Class	clazz	= Class.forName(factoryname, true, cl);
		Object	factory	= clazz.getConstructor(new Class[]{String.class}).newInstance(new Object[]{"dummy"});
		Object modelinfo	= factory.getClass().getMethod("loadModel", new Class[]{String.class, String[].class, ClassLoader.class})
			.invoke(factory, new Object[]{filename, null, cl});
		Object	report	= modelinfo.getClass().getMethod("getReport", new Class[0])
			.invoke(modelinfo, new Object[0]);
		if(report!=null)
		{
			ret	= (String)report.getClass().getMethod("getErrorText", new Class[0])
			.invoke(report, new Object[0]);
		}
		return ret;
	}
}
