package jadex.editor.adf.checker;

import jadex.bdi.BDIAgentFactory;
import jadex.bridge.IModelInfo;
import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.commons.service.IResultSelector;
import jadex.commons.service.ISearchManager;
import jadex.commons.service.IServiceProvider;
import jadex.commons.service.IVisitDecider;

import java.util.Collection;
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
		if(!resource.isDerived() &&	resource instanceof IFile
			&& (resource.getName().endsWith(".agent.xml")
			|| resource.getName().endsWith(".capability.xml")))
		{
			IFile file = (IFile)resource;
			deleteMarkers(file);
			try
			{
				IProject	project	= file.getProject();
				IModelInfo	info	= new BDIAgentFactory(null, new IServiceProvider()
				{
					public IFuture getServices(ISearchManager arg0, IVisitDecider arg1, IResultSelector arg2, Collection arg3)
					{
						return new Future(null);
					}
					
					public IFuture getParent()
					{
						return new Future(null);
					}
					
					public Object getId()
					{
						return "id";
					}
					
					public IFuture getChildren()
					{
						return new Future(null);
					}
				})
					.loadModel(file.getRawLocation().toPortableString(), null, SClassLoader.getProjectClassLoader(project));
				if(info.getReport()!=null)
				{
					String	report	= info.getReport().getErrorText();
//					System.out.println(report);
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
			}
			catch(RuntimeException e1)
			{
				e1.printStackTrace();
//				throw e1;
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
}
