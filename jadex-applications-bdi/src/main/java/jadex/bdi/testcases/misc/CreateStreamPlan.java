package jadex.bdi.testcases.misc;

import jadex.bdi.runtime.Plan;
import jadex.bridge.IOutputConnection;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.types.message.IMessageService;

/**
 * 
 */
public class CreateStreamPlan extends Plan
{
	/**
	 *  The body method is called on the
	 *  instantiated plan instance from the scheduler.
	 */
	public void	body()
	{
		IMessageService ms = getServiceContainer().searchService(IMessageService.class, RequiredServiceInfo.SCOPE_PLATFORM).get(this);
		IOutputConnection con = ms.createOutputConnection(getComponentIdentifier(), getComponentIdentifier()).get(this);
		
		for(int i=0; i<100; i++)
			con.write(new byte[]{(byte)i}).get(this);
	}
}
