package jadex.platform.cms;

import org.junit.Test;

import jadex.base.Starter;
import jadex.base.test.util.STest;
import jadex.bridge.IExternalAccess;
import jadex.commons.future.Future;

/**
 *  Test that components are started even when their init is interleaved with the init of their parents.
 */
public class InterleavedInitTest
{
	@Test
	public void	testInterleavedInit()
	{
		// Child init waits until parent was started
		Future<Void>	parentstarted	= new Future<Void>();
		Future<Void>	childstarted	= new Future<Void>();
		InitAgent	child	= new InitAgent(
			ia -> 
			{
				System.out.println("init "+ia);
				parentstarted.get();
				System.out.println("init done "+ia);
			},
			ia -> 
			{
				System.out.println("start "+ia);
				childstarted.setResult(null);
			});
		
		// Parent starts child in init
		InitAgent	parent	= new InitAgent(
			ia -> 
			{
				System.out.println("init "+ia);
				ia.addComponent(child);
				System.out.println("init done "+ia);
			},
			ia -> 
			{
				System.out.println("start "+ia);
				parentstarted.setResult(null);
			});
		
		// Start parent and wait for child.
		IExternalAccess	platform	= Starter.createPlatform(STest.getLocalTestConfig(getClass())).get();
		platform.addComponent(parent);
		childstarted.get(2000, true);
		platform.killComponent().get();
	}
}
