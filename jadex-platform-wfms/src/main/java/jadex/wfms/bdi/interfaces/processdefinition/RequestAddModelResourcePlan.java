package jadex.wfms.bdi.interfaces.processdefinition;

import jadex.base.fipa.Done;
import jadex.bdi.runtime.IGoal;
import jadex.bridge.IComponentIdentifier;
import jadex.wfms.bdi.client.cap.AbstractWfmsPlan;
import jadex.wfms.bdi.ontology.RequestAddModelResource;
import jadex.wfms.bdi.ontology.RequestProxy;
import jadex.wfms.client.IClient;
import jadex.wfms.service.IProcessDefinitionService;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel.MapMode;
import java.security.AccessControlException;

public class RequestAddModelResourcePlan extends AbstractWfmsPlan
{
	public void body()
	{
		IGoal acqProxy = createGoal("rp_initiate");
		acqProxy.getParameter("action").setValue(new RequestProxy((IComponentIdentifier) getParameter("initiator").getValue()));
		acqProxy.getParameter("receiver").setValue(getClientInterface());
		dispatchSubgoalAndWait(acqProxy);
		IClient proxy = ((RequestProxy) ((Done) acqProxy.getParameter("result").getValue()).getAction()).getClientProxy();
		try
		{
			IProcessDefinitionService pd = (IProcessDefinitionService) getScope().getServiceContainer().getService(IProcessDefinitionService.class);
			RequestAddModelResource ramr = (RequestAddModelResource) getParameter("action").getValue();
			File resourceFile = File.createTempFile(ramr.getResourceName().substring(0, ramr.getResourceName().length() - 4), ".jar");
			byte[] resource = ramr.decodeResource();
			MappedByteBuffer buffer = (new RandomAccessFile(resourceFile, "rws")).getChannel().map(MapMode.READ_WRITE, 0, resource.length);
			buffer.put(resource);
			buffer = null;
			resourceFile.deleteOnExit();
			
			pd.addProcessResource(proxy, resourceFile.toURI().toURL());
			
			Done done = new Done();
			done.setAction(ramr);
			getParameter("result").setValue(done);
		}
		catch (AccessControlException e)
		{
			fail("Unauthorized Access.", e);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail(e.getMessage(), e);
		}
	}

}
