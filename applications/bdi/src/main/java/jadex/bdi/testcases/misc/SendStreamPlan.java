package jadex.bdi.testcases.misc;

import jadex.bdiv3x.runtime.Plan;
import jadex.bridge.IOutputConnection;
import jadex.bridge.component.IMessageFeature;

/**
 * 
 */
public class SendStreamPlan extends Plan
{
	 /**
	  *  The plan body.
	  */
	public void body()
	{
		IMessageFeature mf = getAgent().getFeature(IMessageFeature.class);
		IOutputConnection con = mf.createOutputConnection(getComponentIdentifier(), getComponentIdentifier(), null).get();

		for(int i=0; i<5; i++)
		{
			con.write(new byte[]{(byte)i});
			waitFor(1000);
		}
		
		con.close();
	}
}

