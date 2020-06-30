package jadex.base.test.impl;


import java.util.logging.Level;
import java.util.logging.Logger;

import jadex.base.Starter;
import jadex.base.test.IAbortableTestSuite;
import jadex.base.test.Testcase;
import jadex.bridge.IExternalAccess;
import jadex.bridge.modelinfo.IModelInfo;
import jadex.bridge.service.types.factory.SComponentFactory;


/**
 * Test a component. This version does not need a Platform on instantiation, but
 * rather later, before tests are started.
 */
public class ComponentTestLazyPlatform extends ComponentTestBase
{
	// -------- attributes --------
	
	/** The component to test. **/
	protected String					comp;

	/** Indicates if this test has already failed. */
	private boolean						failed;

	/** Message that describes an early test failure. **/
	private String						message;

	// -------- constructors --------

	/**
	 * Create a new ComponentTest.
	 */
	protected ComponentTestLazyPlatform()
	{
		// Logger.getLogger("ComponentTest").log(Level.SEVERE, "Empty
		// ComponentTest Constructor called");
	}

	/**
	 * Create a component test. Run on existing test suite platform.
	 */
	public ComponentTestLazyPlatform(String comp, IAbortableTestSuite suite)
	{
		super(comp, suite);
		this.comp = comp;
	}

	// -------- methods --------

	public void setPlatform(IExternalAccess platform)
	{
		this.platform = platform;
		if((SComponentFactory.isLoadable(platform, comp, rid).get()).booleanValue())
		{
			boolean startable = SComponentFactory.isStartable(platform, comp, rid).get().booleanValue();
			IModelInfo model = null;
			model = SComponentFactory.loadModel(platform, comp, rid).get();
			if(model != null && model.getReport() == null && startable)
			{
				this.filename = model.getFilename();
				this.rid = model.getResourceIdentifier();
				Object to = model.getProperty(Testcase.PROPERTY_TEST_TIMEOUT, getClass().getClassLoader());
				if(to != null)
				{
					this.timeout = ((Number)to).longValue();
					Logger.getLogger("ComponentTest").log(Level.INFO, "using timeout: " + timeout);
				}
				else
				{
					this.timeout = Starter.getDefaultTimeout(platform.getId());
				}
			}
			else
			{
				failed = true;
				message = "not startable: " + comp;
			}
		}
		else
		{
			failed = true;
			message = "not loadable: " + comp;
		}
	}

	/**
	 * Test the component.
	 */
	public void runBare()
	{
		if(failed)
			fail("could not start testcase: " + comp + ", " + message);

		super.runBare();
	}

	public String getName()
	{
		return comp.endsWith(".class") ? comp.substring(0, comp.length() - 6) : comp;
	}
}
