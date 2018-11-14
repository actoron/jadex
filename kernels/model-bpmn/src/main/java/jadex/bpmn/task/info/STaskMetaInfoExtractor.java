package jadex.bpmn.task.info;

import java.util.ArrayList;
import java.util.List;

import jadex.bpmn.model.task.annotation.Task;
import jadex.bpmn.model.task.annotation.TaskParameter;
import jadex.bpmn.model.task.annotation.TaskProperty;
import jadex.bpmn.model.task.annotation.TaskPropertyGui;
import jadex.bridge.ClassInfo;

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
		if(taskanon != null)
		{
//			try
//			{
//				Method getMetaInfo = taskclass.getMethod("getMetaInfo", (Class[]) null);
//				ret = (TaskMetaInfo)getMetaInfo.invoke(null, (Object[]) null);
//			}
//			catch (NoSuchMethodException e)
//			{
//			}
//			catch (Exception e)
//			{
//				e.printStackTrace();
//			}
//		}
//		else
//		{
			String desc = taskanon.description();
			TaskParameter[] parameters = taskanon.parameters();
			List<ParameterMetaInfo> pmis = new ArrayList<ParameterMetaInfo>();
//			ParameterMetaInfo[] pmis = new ParameterMetaInfo[parameters.length];
			for(int i = 0; i < parameters.length; ++i)
			{
				pmis.add(new ParameterMetaInfo(parameters[i].direction(), parameters[i].clazz(),
					parameters[i].name(), parameters[i].initialvalue(), parameters[i].description()));
			}
			
			TaskProperty[] props = taskanon.properties();
			List<PropertyMetaInfo> prmis = new ArrayList<PropertyMetaInfo>();
//			ParameterMetaInfo[] pmis = new ParameterMetaInfo[parameters.length];
			for(int i = 0; i < props.length; ++i)
			{
				prmis.add(new PropertyMetaInfo(props[i].clazz(),
					props[i].name(), props[i].initialvalue(), props[i].description()));
			}
			
			TaskPropertyGui gui = taskanon.gui();
			ClassInfo guicl = null;
			if(!gui.value().equals(Object.class))
			{
				guicl = new ClassInfo(gui.value());
			}
			else if(gui.classname().length()>0)
			{
				guicl = new ClassInfo(gui.classname());
			}
			
			ret = new TaskMetaInfo(desc, pmis, prmis, guicl);
		}
		
		return ret;
	}
}
