package jadex.wfms.bdi.client.cap;

import jadex.base.fipa.Done;
import jadex.bdi.runtime.IGoal;
import jadex.bpmn.BpmnModelLoader;
import jadex.bridge.service.SServiceProvider;
import jadex.bridge.service.library.ILibraryService;
import jadex.gpmn.GpmnModelLoader;
import jadex.wfms.bdi.ontology.RequestModel;

public class ModelPlan extends AbstractWfmsPlan
{
	public void body()
	{
		RequestModel reqM = new RequestModel();
		reqM.setModelName((String) getParameter("model_name").getValue());
		if (hasParameter("model_name_path"))
			reqM.setModelNamePath(((Boolean) getParameter("model_name_path").getValue()).booleanValue());
		IGoal reqMGoal = createGoal("reqcap.rp_initiate");
		reqMGoal.getParameter("action").setValue(reqM);
		reqMGoal.getParameter("receiver").setValue(getPdInterface());
		dispatchSubgoalAndWait(reqMGoal);
		Done done = (Done) reqMGoal.getParameter("result").getValue();
		reqM = (RequestModel) done.getAction();
		Object model = null;
		try
		{
			
			if (reqM.getModelInfo().getFilename().endsWith(".bpmn"))
			{
				BpmnModelLoader ml = new BpmnModelLoader();
				ClassLoader cl = ((ILibraryService) SServiceProvider.getService(getScope().getServiceProvider(), ILibraryService.class).get(this)).getClassLoader();
				model = ml.loadBpmnModel(reqM.getModelInfo().getFilename(), new String[0], cl);
				//((MBpmnModel) model).setName(reqM.getModelInfo().getFilename().substring(0, reqM.getFileName().length() - 5));
			}
			else if (reqM.getModelInfo().getFilename().endsWith(".gpmn"))
			{
				GpmnModelLoader ml = new GpmnModelLoader();
				
				ClassLoader cl = ((ILibraryService) SServiceProvider.getService(getScope().getServiceProvider(), ILibraryService.class).get(this)).getClassLoader();
				model = ml.loadModel(reqM.getModelInfo().getFilename(), new String[0], cl);
			}
			else
				fail();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail(e);
		}
		
		getParameter("model").setValue(model);
	}
}
