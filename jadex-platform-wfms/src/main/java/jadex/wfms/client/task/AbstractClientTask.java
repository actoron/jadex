package jadex.wfms.client.task;

import jadex.bpmn.model.MLane;
import jadex.bpmn.model.MParameter;
import jadex.bpmn.runtime.ITask;
import jadex.bpmn.runtime.ITaskContext;
import jadex.commons.concurrent.IResultListener;
import jadex.wfms.client.IWorkitem;
import jadex.wfms.client.Workitem;
import jadex.wfms.parametertypes.GuiProperty;
import jadex.wfms.service.IAAAService;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Abstract class for client interactions.
 */
public abstract class AbstractClientTask implements ITask
{
	
	/**
	 * Creates a workitem.
	 * @param type type of the workitem
	 * @param context The task context
	 * @param listener The result listener
	 * @return a new workitem
	 */
	protected static IWorkitem createWorkitem(int type, ITaskContext context)
	{
		Map parameterTypes = new HashMap();
		Map parameterValues = new HashMap();
		Map guiProperties = new HashMap();
		Set readOnlyParameters = new HashSet();
		Map parameters = context.getModelElement().getParameters();
		if (parameters != null)
		{
			for (Iterator it = context.getModelElement().getParameters().values().iterator(); it.hasNext(); )
			{
				MParameter param = (MParameter) it.next();
				if (param.getName().startsWith("GUI_"))
				{
					String paramName = param.getName().substring(4);
					String guiParamName = paramName.substring(paramName.indexOf(':') + 1);
					paramName = paramName.substring(0, paramName.indexOf(':'));
					if (!guiProperties.containsKey(paramName))
						guiProperties.put(paramName, new HashMap());
					Map propertyMap = (Map) guiProperties.get(paramName);
					//GuiProperty[] p = (GuiProperty[]) context.getParameterValue(param.getName());
					//for (int i = 0; i < p.length; ++i)
					//	propertyMap.put(p[i].getName(), p[i].getValue());
					propertyMap.put(guiParamName, context.getParameterValue(param.getName()));
				}
				else
				{
					parameterTypes.put(param.getName(), param.getClazz());
					if (context.getParameterValue(param.getName()) != null)
						parameterValues.put(param.getName(), context.getParameterValue(param.getName()));
					if (param.getDirection().equals(MParameter.DIRECTION_IN))
						readOnlyParameters.add(param.getName());
				}
			}
		}
		MLane lane = context.getModelElement().getLane();
		String role = null;
		if (lane != null)
			role = context.getModelElement().getLane().getName();
		if (role == null)
			role = IAAAService.ANY_ROLE;
		Workitem wi = new Workitem(context.getModelElement().getName(), type, role, parameterTypes, parameterValues, guiProperties, readOnlyParameters);
		wi.setId(context.getModelElement().getName() + "_" + String.valueOf(Integer.toHexString(System.identityHashCode(wi))));
		return wi;
	}
	
	protected static IResultListener createRedirListener(final ITaskContext context, final IResultListener listener)
	{
		IResultListener redirListener = new IResultListener()
		{
			
			public void resultAvailable(Object source, Object result)
			{
				Workitem wi = (Workitem) result;
				Map parameterValues = wi.getParameterValues();
				Set readOnlyParameters = wi.getReadOnlyParameters();
				for (Iterator it = parameterValues.entrySet().iterator(); it.hasNext(); )
				{
					Map.Entry paramEntry = (Map.Entry) it.next();
					if (!readOnlyParameters.contains(paramEntry.getKey()))
					{
						context.setParameterValue((String) paramEntry.getKey(), paramEntry.getValue());
					}
				}
				//System.out.println(listener.getClass().getName());
				listener.resultAvailable(source, result);
			}
			
			public void exceptionOccurred(Object source, Exception exception)
			{
				listener.exceptionOccurred(source, exception);
			}
		};
		return redirListener;
	}
}
