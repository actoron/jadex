package jadex.bpmn.editor;

import jadex.bpmn.editor.gui.BpmnEditorWindow;
import jadex.bpmn.task.info.TaskMetaInfo;
import jadex.commons.SUtil;
import jadex.commons.transformation.binaryserializer.BinarySerializer;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *  Class for starting the BPMN editor.
 *
 */
public class BpmnEditor
{
	/** Current version. */
	public static final int BUILD = 8;
	
	/** The name of the application. */
	public static final String APP_NAME = "Jadex BPMN Editor";
	
	/** Log level for status area. */
	public static final Level STATUS_AREA_LOG_LEVEL = Level.INFO;
	
	/** Main Logger. */
	public static final Logger LOGGER = Logger.getLogger(APP_NAME);
	
	/** Standard task classes. */
	protected static final String[] FALLBACK_TASK_NAMES = { "jadex.bpmn.runtime.task.PrintTask",
															"jadex.bpmn.runtime.task.InvokeMethodTask",
															"jadex.bpmn.runtime.task.CreateComponentTask",
															"jadex.bpmn.runtime.task.DestroyComponentTask",
															"jadex.bpmn.runtime.task.StoreResultsTask",
															"jadex.bpmn.runtime.task.UserInteractionTask",
														 
															"jadex.bdibpmn.task.DispatchGoalTask",
															"jadex.bdibpmn.task.WaitForGoalTask",
															"jadex.bdibpmn.task.DispatchInternalEventTask",
															"jadex.bdibpmn.task.WriteBeliefTask",
															"jadex.bdibpmn.task.WriteParameterTask",
														 
															"jadex.bdibpmn.task.CreateSpaceObjectTaskTask",
															"jadex.bdibpmn.task.WaitForSpaceObjectTaskTask",
															"jadex.bdibpmn.task.RemoveSpaceObjectTaskTask",

															"jadex.wfms.client.task.WorkitemTask"
													   	  };
	
	/** Task informations. */
	public static Map<String, TaskMetaInfo> TASK_INFOS;
	static
	{
		InputStream is = SUtil.getResource0("/jadex/bpmn/editor/task_infos.bin", BpmnEditor.class.getClassLoader());
		if (is != null)
		{
			try
			{
				ByteArrayOutputStream bytes = new ByteArrayOutputStream();
				int r;
				do
				{
					r = is.read();
					if (r != -1)
					{
						bytes.write(r);
					}
				}
				while (r != -1);
				
				TASK_INFOS = (Map<String, TaskMetaInfo>) BinarySerializer.objectFromByteArray(bytes.toByteArray(), null, null, BpmnEditor.class.getClassLoader(), null);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		
		if (TASK_INFOS == null)
		{
			System.out.println("Could not load task information, using fallback...");
			TASK_INFOS = new HashMap<String, TaskMetaInfo>();
			for (int i = 0; i < FALLBACK_TASK_NAMES.length; ++i)
			{
				TASK_INFOS.put(FALLBACK_TASK_NAMES[i], null);
			}
		}
		
		TASK_INFOS.put("", null);
	}
	
	/**
	 *  Starts the BPMN editor.
	 *  
	 *  @param args The arguments.
	 */
	public static void main(String[] args)
	{
		LOGGER.setUseParentHandlers(false);
		new BpmnEditorWindow();
	}
}
