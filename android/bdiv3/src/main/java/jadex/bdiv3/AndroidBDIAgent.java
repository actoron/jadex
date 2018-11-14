package jadex.bdiv3;

import android.content.Context;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.types.context.IContextService;
import jadex.micro.annotation.AgentArgument;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;

@RequiredServices({
	@RequiredService(name="context", type=IContextService.class)
})
public abstract class AndroidBDIAgent implements IBDIAgent
{
	@AgentArgument
	public Context androidContext;
}
