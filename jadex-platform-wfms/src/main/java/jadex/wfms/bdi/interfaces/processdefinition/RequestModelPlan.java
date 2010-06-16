package jadex.wfms.bdi.interfaces.processdefinition;

import jadex.base.fipa.Done;
import jadex.bdi.runtime.IGoal;
import jadex.bpmn.BpmnModelLoader;
import jadex.bpmn.model.MBpmnModel;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.ILoadableComponentModel;
import jadex.gpmn.GpmnModelLoader;
import jadex.service.library.ILibraryService;
import jadex.wfms.bdi.client.cap.AbstractWfmsPlan;
import jadex.wfms.bdi.ontology.RequestModel;
import jadex.wfms.bdi.ontology.RequestProxy;
import jadex.wfms.client.IClient;
import jadex.wfms.service.IProcessDefinitionService;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.AccessControlException;

public class RequestModelPlan extends AbstractWfmsPlan
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
			RequestModel rqm = (RequestModel) getParameter("action").getValue();
			ILoadableComponentModel model = null;
			if (rqm.isModelNamePath())
			{
				try
				{
					if (rqm.getModelName().endsWith(".bpmn"))
					{
						BpmnModelLoader bpmnLoader = new BpmnModelLoader();
						bpmnLoader.setClassLoader(((ILibraryService)getScope().getServiceContainer().getService(ILibraryService.class)).getClassLoader());
						model = bpmnLoader.loadBpmnModel(rqm.getModelName(), new String[0]);
					}
					else
					{
						GpmnModelLoader gpmnLoader = new GpmnModelLoader();
						gpmnLoader.setClassLoader(((ILibraryService)getScope().getServiceContainer().getService(ILibraryService.class)).getClassLoader());
						model = gpmnLoader.loadGpmnModel(rqm.getModelName(), new String[0]);
					}
				}
				catch (Exception e)
				{
					throw new RuntimeException(e);
				}
			}
			else
				model = ((IProcessDefinitionService) getScope().getServiceContainer().getService(IProcessDefinitionService.class)).getProcessModel(proxy, rqm.getModelName());
			File modelFile = new File(model.getFilename());
			byte[] content;
			try
			{
				MappedByteBuffer map = (new FileInputStream(modelFile)).getChannel().map(FileChannel.MapMode.READ_ONLY, 0, modelFile.length());
				content = new byte[map.capacity()];
				map.get(content, 0, map.capacity());
			}
			catch (IOException e)
			{
				throw new RuntimeException(e);
			}
			
			rqm.encodeModelContent(content);
			rqm.setFileName(modelFile.getName());
			Done done = new Done();
			done.setAction(rqm);
			getParameter("result").setValue(done);
		}
		catch (AccessControlException e)
		{
			fail("Unauthorized Access.", e);
		}
	}

}
