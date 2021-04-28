package jadex.platform.service.simulation;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import jadex.base.IPlatformConfiguration;
import jadex.base.Starter;
import jadex.base.test.impl.SharedService;
import jadex.base.test.util.STest;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.IInternalService;
import jadex.bridge.service.types.execution.IExecutionService;
import jadex.commons.future.Future;

/**
 *  Test that services can be shared as required by multi-platform simulation.
 */
public class ServiceSharingTest
{
	/**
	 *  Create and shutdown some platforms to check if thread pool and execution service still work.
	 */
	@Test
	public void	testServiceSharing()
	{
		// Shared config contains the shared service factories.
		IPlatformConfiguration	conf	= STest.createDefaultTestConfig(getClass());
//		conf.setLogging(true);
		
		// Start two platforms -> second platform should reuse shared service.
		IExternalAccess	p1	= Starter.createPlatform(conf).get();
		IExternalAccess	p2	= Starter.createPlatform(conf).get();
		IExecutionService	exe1	= p1.scheduleStep(ia -> new Future<>(ia.getProvidedService(IExecutionService.class))).get();
		IExecutionService	exe2	= p2.scheduleStep(ia -> new Future<>(ia.getProvidedService(IExecutionService.class))).get();
		assertTrue(""+exe1.getClass(), exe1 instanceof SharedService);
		assertTrue(""+exe2.getClass(), exe2 instanceof SharedService);
		assertSame(((SharedService<?>)exe1).getInstance(), ((SharedService<?>)exe2).getInstance());
		
		// Stop first platform -> second platform should still work (i.e. shared services are not shut down)
		p1.killComponent().get();
		assertTrue("Service should be valid", ((IInternalService)((SharedService<?>)exe2).getInstance()).isValid().get());
		assertTrue("Platform step", p2.scheduleStep(ia -> new Future<>(true)).get());
		
		// Stop second platform -> shared service should be terminated.
		IInternalService	is	= (IInternalService)((SharedService<?>)exe2).getInstance();
		p2.killComponent().get();
		assertFalse("Service should be invalid", is.isValid().get());
		
		// Start third platform -> platform should create new shared service.
		IExternalAccess	p3	= Starter.createPlatform(conf).get();
		IExecutionService	exe3	= p3.scheduleStep(ia -> new Future<>(ia.getProvidedService(IExecutionService.class))).get();
		assertTrue(""+exe3.getClass(), exe3 instanceof SharedService);
		assertNotSame(is, ((SharedService<?>)exe3).getInstance());
		assertTrue("Service should be valid", ((IInternalService)((SharedService<?>)exe3).getInstance()).isValid().get());
		assertTrue("Platform step", p3.scheduleStep(ia -> new Future<>(true)).get());
		p3.killComponent().get();
	}
}
