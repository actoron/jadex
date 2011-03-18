package jadex.wfms.bdi.interfaces.processdefinition;

import jadex.base.fipa.Done;
import jadex.bdi.runtime.IGoal;
import jadex.bpmn.BpmnModelLoader;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IModelInfo;
import jadex.bridge.service.SServiceProvider;
import jadex.bridge.service.library.ILibraryService;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.gpmn.GpmnModelLoader;
import jadex.wfms.bdi.client.cap.AbstractWfmsPlan;
import jadex.wfms.bdi.ontology.RequestModel;
import jadex.wfms.bdi.ontology.RequestProxy;
import jadex.wfms.client.IClient;
import jadex.wfms.service.IProcessDefinitionService;

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
			IFuture modelFuture = new Future();
			if (rqm.isModelNamePath())
			{
				try
				{
					if (rqm.getModelName().endsWith(".bpmn"))
					{
						BpmnModelLoader bpmnLoader = new BpmnModelLoader();
						ClassLoader cl = ((ILibraryService) SServiceProvider.getService(getScope().getServiceProvider(), ILibraryService.class).get(this)).getClassLoader();
						((Future) modelFuture).setResult(bpmnLoader.loadBpmnModel(rqm.getModelName(), new String[0], cl).getModelInfo());
					}
					else
					{
						GpmnModelLoader gpmnLoader = new GpmnModelLoader();
						ClassLoader cl = ((ILibraryService) SServiceProvider.getService(getScope().getServiceProvider(), ILibraryService.class).get(this)).getClassLoader();
						((Future) modelFuture).setResult(gpmnLoader.loadGpmnModel(rqm.getModelName(), new String[0], cl));
					}
				}
				catch (Exception e)
				{
					System.err.println(rqm.getModelName());
					throw new RuntimeException(e);
				}
			}
			else
				modelFuture = ((IProcessDefinitionService) SServiceProvider.getService(getScope().getServiceProvider(), IProcessDefinitionService.class).get(this)).getProcessModel(proxy, rqm.getModelName());
			
			IModelInfo model = (IModelInfo) modelFuture.get(this);
			/*File modelFile = new File(model.getFilename());
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
			}*/
			
			//rqm.encodeModelContent(content);
			//rqm.setFileName(modelFile.getName());
			rqm.setModelInfo(model);
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
