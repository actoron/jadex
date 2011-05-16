package jadex.simulation.analysis.common.component.workflow.tasks;

import jadex.bpmn.model.MParameter;
import jadex.bpmn.runtime.BpmnInterpreter;
import jadex.bpmn.runtime.ITaskContext;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.SServiceProvider;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.ThreadSuspendable;
import jadex.simulation.analysis.common.component.workflow.defaultView.BpmnComponentView;
import jadex.simulation.analysis.common.component.workflow.factories.ATaskViewFactory;
import jadex.simulation.analysis.common.component.workflow.tasks.general.ATask;
import jadex.simulation.analysis.common.component.workflow.tasks.general.IATask;
import jadex.simulation.analysis.common.component.workflow.tasks.general.IATaskView;
import jadex.simulation.analysis.common.services.IAnalysisService;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Task to "create" a model
 */
public class GenericServiceTask extends ATask implements IATask
{
	private final GenericServiceTask task = this;
	private Future ret = new Future();

	/**
	 * Execute the task until Future return
	 */
	public IFuture execute(final ITaskContext context, final BpmnInterpreter instance)
	{
		activity = context.getActivity();
		IATaskView view = ATaskViewFactory.createView(this);
		((BpmnComponentView) instance.getContextVariable("view")).registerTask(task, view);
		
		
		Class serviceClass = ((MParameter)context.getActivity().getParameters().get("service")).getClazz();
		System.out.println(context.getActivity().getParameters());
		IFuture serviceFut = SServiceProvider.getServices(instance.getServiceProvider(),serviceClass , RequiredServiceInfo.SCOPE_GLOBAL);
		Object serviceRes = serviceFut.get(new ThreadSuspendable(this));
		ArrayList<IAnalysisService> services = null;
		if (serviceRes instanceof ArrayList)
		{
			services = (ArrayList<IAnalysisService>) serviceRes;
		}
		else
		{
			new RuntimeException("No Service found!");
		}
		
		IAnalysisService service = null;
		if (context.getParameterValue("modus") != null)
		{
			String modus = (String) context.getParameterValue("modus");
			for (IAnalysisService iService : services)
			{
				if (iService.getSupportedModes().contains(modus) && service == null) service = iService;
			}
		} else
		{
			for (IAnalysisService iService : services)
			{
				if (service == null) service = iService;
			}
		}
		
		Boolean found = true;
		Map<Integer,Object> parameters = new HashMap<Integer,Object>();
		Integer id = 1;
		while (found)
		{
				Object para = context.getParameterValue("parameter" + id);
				if (para == null)
				{
					found = false;
				} else
				{
					parameters.put(id, para);
				}
				id++;
		}
		
		Class partypes[] = new Class[parameters.size()];
		Integer id2 = 0;
		for (Object class1 : parameters.values())
		{
			partypes[id2] = class1.getClass();
			id2++;
		}
		
		try
		{
			Method meth = service.getClass().getMethod(
			          (String)context.getParameterValue("method"), partypes);
			Object arglist[] = new Object[parameters.size()];
			Integer id3 = 0;
			for (Object class1 : parameters.values())
			{
				arglist[id3] = class1;
				id3++;
			}
			
			IFuture executeFut = (IFuture) meth.invoke(service, arglist);
			Object executeRes = executeFut.get(new ThreadSuspendable(this));
			context.setParameterValue("result", executeRes);
			System.out.println(executeRes);
		}
		catch (Exception e1)
		{
			e1.printStackTrace();
		}
		
		return ret;
		
		
	}

	public void resumeTask(Object resume)
	{
		ret.setResultIfUndone(resume);
	}

}
