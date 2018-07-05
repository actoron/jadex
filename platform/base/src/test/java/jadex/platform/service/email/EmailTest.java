package jadex.platform.service.email;

import jadex.base.Starter;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.bridge.service.types.email.Email;
import jadex.bridge.service.types.email.IEmailService;

public class EmailTest
{
	public static void	main(String[] s)
	{
		String[]	args	= new String[]
		{
			"-gui", "false"
		};
		
//		ISuspendable.SUSPENDABLE.set(new ThreadSuspendable());
		IExternalAccess	exta	= Starter.createPlatform(args).get();
		IComponentManagementService	cms	= exta.searchService(
			new ServiceQuery<>(IComponentManagementService.class)).get();
		cms.createComponent(null, "jadex/platform/service/email/EmailAgent.class", null, null).get();
		
		IEmailService	ems	= exta.searchService(
			new ServiceQuery<>(IEmailService.class)).get();
		
		try
		{
			ems.sendEmail(new Email(null, "test", "email test", "pokahr@gmx.net"), null).get();
		}
		finally
		{
			exta.killComponent();
		}
	}
}
