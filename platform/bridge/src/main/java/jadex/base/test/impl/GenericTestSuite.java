package jadex.base.test.impl;

import org.junit.runner.RunWith;
import org.junit.runners.AllTests;

import jadex.base.IPlatformConfiguration;
import jadex.base.Starter;
import jadex.base.test.IAbortableTestSuite;
import jadex.base.test.util.STest;
import jadex.bridge.IExternalAccess;
import junit.framework.TestResult;
import junit.framework.TestSuite;


/**
 * Suite that can run multiple test agents.
 */
@RunWith(AllTests.class)
public abstract class GenericTestSuite extends TestSuite implements IAbortableTestSuite
{

	private IExternalAccess				access;

	private IPlatformConfiguration		config;

	private String[]					components;

	public GenericTestSuite(Class... clazzes)
	{
		this(false, clazzes);
	}

	public GenericTestSuite(String... components)
	{
		this(false, components);
	}

	public GenericTestSuite(boolean justStartComponents, Class... clazzes)
	{
		this(justStartComponents, toName(clazzes));
	}


	public GenericTestSuite(boolean justStartComponents, String... components)
	{
		this.components = components;
		this.config = STest.getDefaultTestConfig(getClass());
		for(String component : components)
		{
			if(!component.endsWith(".class"))
			{
				component = component + ".class";
			}
			// System.out.println("adding test for: " + component);
			ComponentTestLazyPlatform ct;
			if(justStartComponents)
			{
				ct = new ComponentStartTestLazyPlatform(component, this);
			}
			else
			{
				ct = new ComponentTestLazyPlatform(component, this);
			}
			addTest(ct);
		}
	}

	public void setConfig(IPlatformConfiguration config)
	{
		this.config = config;
	}

	private static String[] toName(Class[] clazzes)
	{
		String[] components = new String[clazzes.length];
		for(int i = 0; i < clazzes.length; i++)
		{
			components[i] = clazzes[i].getName();
		}
		return components;
	}

	@Override
	public void run(TestResult result)
	{
		IExternalAccess sharedPlatform = Starter.createPlatform(config).get();
		access = sharedPlatform;
		super.run(result);
		access.killComponent();
		access = null;
	}

	@Override
	public void runTest(junit.framework.Test test, TestResult result)
	{
		((ComponentTestLazyPlatform)test).setPlatform(access);
		super.runTest(test, result);
	}

	@Override
	public boolean isAborted()
	{
		return false;
	}

	@Override
	public void addTest(junit.framework.Test test)
	{
		super.addTest(test);
	}
}
