package jadex.bpmn.task.info;

import jadex.bpmn.model.task.annotation.Task;
import jadex.bpmn.model.task.annotation.TaskParameter;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *  Static helper for extracting task meta infos.
 */
public class STaskMetaInfoExtractor
{
	/**
	 *  Get the meta info for a task class.
	 */
	public static final TaskMetaInfo getMetaInfo(Class<?> taskclass)
	{
		TaskMetaInfo ret = null;
		
		Task taskanon = (Task)taskclass.getAnnotation(Task.class);
		if(taskanon == null)
		{
			try
			{
				Method getMetaInfo = taskclass.getMethod("getMetaInfo", (Class[]) null);
				ret = (TaskMetaInfo)getMetaInfo.invoke(null, (Object[]) null);
			}
			catch (NoSuchMethodException e)
			{
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		else
		{
			String desc = taskanon.description();
			TaskParameter[] parameters = taskanon.parameters();
			
			List<ParameterMetaInfo> pmis = new ArrayList<ParameterMetaInfo>();
//			ParameterMetaInfo[] pmis = new ParameterMetaInfo[parameters.length];
			for (int i = 0; i < parameters.length; ++i)
			{
				pmis.add(new ParameterMetaInfo(parameters[i].direction(), parameters[i].clazz(),
					parameters[i].name(), parameters[i].initialvalue(), parameters[i].description()));
			}
			
			ret = new TaskMetaInfo(desc, pmis);
		}
		
		return ret;
	}
}
