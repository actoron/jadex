package jadex.base.test.impl;


import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import jadex.base.IPlatformConfiguration;
import jadex.base.Starter;
import jadex.base.test.IAbortableTestSuite;
import jadex.base.test.Testcase;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IResourceIdentifier;
import jadex.bridge.modelinfo.IModelInfo;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.library.ILibraryService;

/**
 *  Test a component.
 */
public class ComponentTest extends ComponentTestBase
{
	//-------- attributes --------
	
	/** Additional config args. */
	protected String[]	args;
	
	/** The dirs for rids (e.g. classes and resources dirs). */
	protected File[][]	dirs;
	
	/** The component full name. */
	protected String	fullname;
	
	/** The component (kernel) type. */
	protected String	type;
	
	//-------- constructors --------
	
	/**
	 *  Create a new ComponentTest.
	 */
	public ComponentTest() 
	{
		Logger.getLogger("ComponentTest").log(Level.SEVERE, "Empty ComponentTest Constructor called");
	}
	
	/**
	 *  Create a component test.
	 *  Run on existing test suite platform.
	 *  @param cms	The CMS of the test suite platform.
	 */
	public ComponentTest(IExternalAccess platform, IModelInfo comp, IAbortableTestSuite suite)
	{
		super(comp.getFullName(), suite);
		this.platform = platform;
		this.filename	= comp.getFilename();
		this.rid	= comp.getResourceIdentifier();
		this.fullname	= comp.getFullName();
		this.type	= comp.getType();
		Object	to	= comp.getProperty(Testcase.PROPERTY_TEST_TIMEOUT, getClass().getClassLoader());
		if(to!=null)
		{
			this.timeout	= ((Number)to).longValue();
		}
		else
		{
			this.timeout	= Starter.getScaledDefaultTimeout(platform.getId(), 2);
		}
	}
	
	/**
	 *  Create a component test.
	 *  Run on separate platform.
	 *  @param conf	The config for the new platform.
	 */
	public ComponentTest(IPlatformConfiguration conf, String[] args,  File[][] dirs, IExternalAccess platform, IModelInfo comp, IAbortableTestSuite suite)
	{
		this(platform, comp, suite);
		this.platform	= null; // Don't store suite cms -> use for own cms later.
		this.conf	= conf;
		this.args	= args;
		this.dirs	= dirs;
	}
	
	//-------- methods --------
	
	/**
	 *  Create a new platform (only if conf is given and thus platform per test is enabled)
	 */
	protected IExternalAccess	createPlatform()
	{
		IExternalAccess	platform = Starter.createPlatform(conf, args).get(timeout, true);
		ILibraryService	libsrv	= platform.searchService( new ServiceQuery<>(ILibraryService.class)).get(timeout, true);
		
		for(int projectIndex=0; projectIndex < dirs.length; projectIndex++) 
		{
			File[] project = dirs[projectIndex];
			IResourceIdentifier	parentRid	= null;
			for(int rootIndex=0; rootIndex<project.length; rootIndex++)
			{
				try
				{
					if(parentRid==null && rid.getLocalIdentifier().getUri().equals(project[rootIndex].getCanonicalFile().toURI()))
					{
//							System.out.println(fullname+": choose "+project[rootIndex]+" as "+rid);
						parentRid	= rid;
						libsrv.addURL(null, project[rootIndex].toURI().toURL()).get(timeout, true);
					}
					else if(parentRid!=null)
					{
//							System.out.println(fullname+": add "+project[rootIndex]+" to "+rid);
						libsrv.addURL(parentRid, project[rootIndex].toURI().toURL()).get(timeout, true);
					}
					else
					{
//							System.out.println(fullname+": no match "+project[rootIndex]+" for "+rid);
					}
				}
				catch(Exception e)
				{
					throw new RuntimeException(e);
				}
			}
		}
		
		return platform;
	}

	public String getName()
	{
		return this.toString();
	}
	
	
	/**
	 *  Get a string representation of this test.
	 */
	public String toString()
	{
		return fullname + " (" + type + ")";
	}
}
