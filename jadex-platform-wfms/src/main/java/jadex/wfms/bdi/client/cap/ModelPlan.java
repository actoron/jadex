package jadex.wfms.bdi.client.cap;

import jadex.base.fipa.Done;
import jadex.base.fipa.IDF;
import jadex.bdi.runtime.IGoal;
import jadex.bpmn.BpmnModelLoader;
import jadex.bpmn.model.MBpmnModel;
import jadex.bridge.ILoadableComponentModel;
import jadex.gpmn.GpmnModelLoader;
import jadex.service.SServiceProvider;
import jadex.service.library.ILibraryService;
import jadex.wfms.bdi.ontology.RequestModel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel.MapMode;

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
		ILoadableComponentModel model = null;
		try
		{
			if ((reqM.getFileName().endsWith(".bpmn")) || (reqM.getFileName().endsWith(".gpmn")))
			{
				byte[] modelContent = reqM.decodeModelContent();
				File tmpFile = File.createTempFile(reqM.getFileName(), reqM.getFileName().substring(reqM.getFileName().length() - 5));
				MappedByteBuffer buffer = (new RandomAccessFile(tmpFile, "rws")).getChannel().map(MapMode.READ_WRITE, 0, modelContent.length);
				buffer.put(modelContent);
				buffer = null;
				if (reqM.getFileName().endsWith(".bpmn"))
				{
					BpmnModelLoader ml = new BpmnModelLoader();
					ClassLoader cl = ((ILibraryService) SServiceProvider.getService(getScope().getServiceProvider(), ILibraryService.class).get(this)).getClassLoader();
					model = ml.loadBpmnModel(tmpFile.getAbsolutePath(), new String[0], cl);
					((MBpmnModel) model).setName(reqM.getFileName().substring(0, reqM.getFileName().length() - 5));
				}
				else
				{
					GpmnModelLoader ml = new GpmnModelLoader();
					
					ClassLoader cl = ((ILibraryService) SServiceProvider.getService(getScope().getServiceProvider(), ILibraryService.class).get(this)).getClassLoader();
					model = ml.loadGpmnModel(tmpFile.getAbsolutePath(), new String[0], cl);
				}
				tmpFile.delete();
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
