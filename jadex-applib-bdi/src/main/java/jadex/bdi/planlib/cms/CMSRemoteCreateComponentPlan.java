package jadex.bdi.planlib.cms;

import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IResourceIdentifier;
import jadex.bridge.fipa.CMSCreateComponent;
import jadex.bridge.fipa.Done;
import jadex.bridge.fipa.SFipa;

import java.util.Map;

/**
 *  Create an component on a remote cms.
 */
public class CMSRemoteCreateComponentPlan extends Plan
{
	/**
	 * The body method is called on the
	 * instantiated plan instance from the scheduler.
	 */
	public void body()
	{
		CMSCreateComponent ca = new CMSCreateComponent();
		ca.setType((String)getParameter("type").getValue());
		ca.setName((String)getParameter("name").getValue());
		ca.setConfiguration((String)getParameter("configuration").getValue());
		ca.setArguments((Map)getParameter("arguments").getValue());
		ca.setSuspend(((Boolean)getParameter("suspend").getValue()).booleanValue());
		ca.setMaster(((Boolean)getParameter("master").getValue()).booleanValue());
		ca.setParent((IComponentIdentifier)getParameter("parent").getValue());
		ca.setResourceIdentifier((IResourceIdentifier)getParameter("rid").getValue());

//		System.out.println("cms remote create comp plan: "+getParameter("rid").getValue());
		
		IGoal req = createGoal("rp_initiate");
		req.getParameter("receiver").setValue(getParameter("cms").getValue());
		req.getParameter("action").setValue(ca);
		req.getParameter("ontology").setValue(SFipa.COMPONENT_MANAGEMENT_ONTOLOGY_NAME);
		dispatchSubgoalAndWait(req);

		getParameter("componentidentifier").setValue(((CMSCreateComponent)((Done)req.getParameter("result").getValue()).getAction()).getComponentIdentifier());
	}
}
