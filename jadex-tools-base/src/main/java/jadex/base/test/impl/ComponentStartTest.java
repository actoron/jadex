package jadex.base.test.impl;


import jadex.bridge.ComponentTerminatedException;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.modelinfo.IModelInfo;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.future.ThreadSuspendable;


import junit.framework.Test;
import junit.framework.TestResult;

/**
 *  Test if a component can be started.
 */
public class ComponentStartTest implements	Test
{
	//-------- attributes --------
	
	/** The component management system. */
	protected IComponentManagementService	cms;
	
	/** The component. */
	protected IModelInfo	comp;
	
	//-------- constructors --------
	
	/**
	 *  Create a component test.
	 */
	public ComponentStartTest(IComponentManagementService cms, IModelInfo comp)
	{
		this.cms	= cms;
		this.comp	= comp;
	}
	
	//-------- methods --------
	
	/**
	 *  The number of test cases.
	 */
	public int countTestCases()
	{
		return 1;
	}
	
	/**
	 *  Test the component.
	 */
	public void run(TestResult result)
	{
		result.startTest(this);
		
		// Start the component.
		try
		{
			IComponentIdentifier	cid	= cms.createComponent(null, comp.getFilename(), new CreationInfo(comp.getResourceIdentifier()), null).get(new ThreadSuspendable(), 30000);
			try
			{
				cms.destroyComponent(cid).get(new ThreadSuspendable(), 30000);
			}
			catch(ComponentTerminatedException cte)
			{				
				// Ignore if component already terminated.
			}
			catch(RuntimeException e)
			{
				// Ignore if component already terminated.
				if(!(e.getCause() instanceof ComponentTerminatedException))
				{
					throw e;
				}
			}
		}
		catch(Exception e)
		{
			result.addError(this, e);
		}

		result.endTest(this);
}
	
	/**
	 *  Get a string representation of this test.
	 */
	public String toString()
	{
		return comp.getFullName();
	}	
}