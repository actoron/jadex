package jadex.micro;

import android.content.Context;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.types.context.IContextService;
import jadex.bridge.service.types.context.JadexAndroidEvent;
import jadex.micro.annotation.AgentArgument;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;

@RequiredServices({
	@RequiredService(name="context", type=IContextService.class, binding=@Binding(scope=RequiredServiceInfo.SCOPE_PLATFORM))
})
public class AndroidMicroAgent extends MicroAgent
{
	@AgentArgument
	public Context androidContext;
	
	protected void dispatchEvent(final JadexAndroidEvent event) {
		final IContextService contextService = (IContextService) getRequiredService("context").get();
		contextService.dispatchEvent(event);
	}
	
}
