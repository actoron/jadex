package jadex.platform.service.chat;


import jadex.bridge.service.types.chat.IChatGuiService;
import jadex.bridge.service.types.chat.IChatService;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.Configuration;
import jadex.micro.annotation.Configurations;
import jadex.micro.annotation.Description;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;

/**
 *  Chat micro agent. 
 */
@Description("This agent offers a chat service.")
@ProvidedServices({
	@ProvidedService(name="chat", type=IChatService.class, implementation=@Implementation(ChatService.class)),
	@ProvidedService(name="chatgui", scope=Binding.SCOPE_PLATFORM, type=IChatGuiService.class, implementation=@Implementation(expression="$component.getComponentFeature(jadex.bridge.service.component.IProvidedServicesFeature.class).getProvidedServiceRawImpl(\"chat\")"))
})
@RequiredServices(
	@RequiredService(name="chatservices", type=IChatService.class, multiple=true,
		binding=@Binding(dynamic=true, scope=Binding.SCOPE_GLOBAL))
)
@Arguments(@Argument(name="nosave", clazz=boolean.class, description="Don't save settings."))
@Agent
//@Properties(@NameValue(name="system", value="\"system\".equals($config)"))
@Configurations({@Configuration(name="system"), @Configuration(name="user")})
public class ChatAgent
{
//	@Agent
//	protected IInternalAccess agent;
//	
//	@AgentCreated
//	public void body()
//	{
//		System.out.println("config: "+agent.getConfiguration());
//	}
	
}
