import java.util.HashMap;
import java.util.Map;

import jadex.base.PlatformConfiguration;
import jadex.base.Starter;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.extension.rs.publish.JettyRSPublishAgent;

public class Test
{
	public static void main(String[] args) throws ClassNotFoundException
	{
//		Class cl = Class.forName("jadex.tools.jcc.JCCAgent");
//		System.out.println(cl);
		System.out.println(Class.forName("jadex.extension.rs.publish.JettyRSPublishAgent"));
		
		PlatformConfiguration pc = PlatformConfiguration.getDefault();
//	    pc.getRootConfig().setGui(false);
//	    pc.getRootConfig().setLogging(true);
	    pc.getRootConfig().setRsPublish(true);
	    IExternalAccess platform = Starter.createPlatform(pc).get();
	    
		IComponentManagementService cms = SServiceProvider.getService(platform, IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM).get();

		// Hello world
//		cms.createComponent(HelloWorldAgent.class.getName()+".class", null).getFirstResult();
		
		// Chat
//		cms.createComponent(WebchatAgent.class.getName()+".class", null).getFirstResult();
		
		// Email
		Map<String, Object> eargs = new HashMap<String, Object>();
		eargs.put("receivers", new String[]{"info@actoron.com"});
//		cms.createComponent(EmailAgent.class.getName()+".class", new CreationInfo(eargs)).getFirstResult();
	}
}
