package jadex.platform.service.execution;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import jadex.base.IPlatformConfiguration;
import jadex.base.PlatformConfigurationHandler;
import jadex.base.Starter;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.ServiceScope;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.execution.IExecutionService;
import jadex.commons.concurrent.IExecutable;

public class ExecutionServiceTest
{
	@Test
	public void	testSimpleExecution()
	{
		IPlatformConfiguration minimal = PlatformConfigurationHandler.getMinimal();
		minimal.getExtendedPlatformConfiguration().setRelayTransport(false);
		minimal.getExtendedPlatformConfiguration().setWsTransport(false);
		minimal.setValue("settings.readonly", true);
		IExternalAccess	platform	= Starter.createPlatform(minimal).get();
		IExecutionService	exe	= platform.searchService( new ServiceQuery<>( IExecutionService.class, ServiceScope.PLATFORM)).get();
		
		final List<String>	list	= new ArrayList<String>();		
		exe.execute(new IExecutable()
		{
			@Override
			public boolean execute()
			{
				list.add("executed");
				return false;
			}
		});

		exe.getNextIdleFuture().get();

		Assert.assertEquals(list.toString(), 1, list.size());
		
		platform.killComponent().get();
	}
}
