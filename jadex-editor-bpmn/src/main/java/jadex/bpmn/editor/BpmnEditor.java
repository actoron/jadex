package jadex.bpmn.editor;

import jadex.bpmn.editor.gui.BpmnEditorWindow;
import jadex.bpmn.editor.gui.stylesheets.BpmnStylesheetColor;
import jadex.bpmn.editor.gui.stylesheets.BpmnStylesheetColorGradient;
import jadex.bpmn.editor.gui.stylesheets.BpmnStylesheetComplexGrayscale;
import jadex.bpmn.editor.gui.stylesheets.BpmnStylesheetSimpleGrayscale;
import jadex.bpmn.task.info.TaskMetaInfo;
import jadex.commons.SUtil;
import jadex.commons.Tuple2;
import jadex.commons.transformation.binaryserializer.BinarySerializer;

import java.awt.EventQueue;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import com.mxgraph.view.mxStylesheet;

/**
 *  Class for starting the BPMN editor.
 *
 */
public class BpmnEditor
{
	/** Current version. */
	public static final int BUILD = 62;
	
	/** Console logging flag. */
	public static final boolean CONSOLE_LOGGING = false;
	
	/** The name of the application. */
	public static final String APP_NAME = "Jadex BPMN Editor";
	
	/** The settings directory name. */
	public static String HOME_DIR = System.getProperty("user.home") + File.separator + ".jadex-bpmn-editor";
	
	/** The settings directory name for windows. */
	public static final String WINDOWS_HOME_DIR = System.getProperty("user.dir") + File.separator + "config";
	
	/** The image cache file. */
//	public static final String IMAGE_CACHE = HOME_DIR + File.separator + "imagecache.dat";
	
	/** Log level for status area. */
	public static final Level STATUS_AREA_LOG_LEVEL = Level.INFO;
	
	/** Main Logger. */
	public static final Logger LOGGER = Logger.getLogger(APP_NAME);
	
	/** The style sheets. */
	@SuppressWarnings("unchecked")
	public static final Tuple2<String, mxStylesheet>[] STYLE_SHEETS = new Tuple2[] 
		{
			new Tuple2<String, mxStylesheet>("Color Gradient", new BpmnStylesheetColorGradient()),
			new Tuple2<String, mxStylesheet>("Color", new BpmnStylesheetColor()),
			new Tuple2<String, mxStylesheet>("Simple Grayscale", new BpmnStylesheetSimpleGrayscale()),
			new Tuple2<String, mxStylesheet>("Complex Grayscale", new BpmnStylesheetComplexGrayscale())
		};
	
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
	
	/** Available look and feels. */
	public static final Map<String, LookAndFeelInfo> LOOK_AND_FEELS = new HashMap<String, UIManager.LookAndFeelInfo>();
	
	/** Task informations. */
	public static Map<String, TaskMetaInfo> TASK_INFOS;
	
	/**
	 *  Starts the BPMN editor.
	 *  
	 *  @param args The arguments.
	 */
	public static void main(String[] args)
	{
		LOGGER.setUseParentHandlers(CONSOLE_LOGGING);
		LOGGER.setLevel(Level.ALL);
		
		String osname = System.getProperty("os.name");
		if (osname != null && osname.toLowerCase().contains("win"))
		{
			HOME_DIR = WINDOWS_HOME_DIR;
		}

		try
		{
			for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels())
			{
				LOOK_AND_FEELS.put(info.getName(), info);
//		        if ("Nimbus".equals(info.getName()))
//		        {
//		            UIManager.setLookAndFeel(info.getClassName());
//		            break;
//		        }
		    }
		}
		catch (Exception e)
		{
		}
		
		EventQueue.invokeLater(new Runnable()
		{
			public void run()
			{
//				Enumeration<Object> keys = UIManager.getDefaults().keys();
//				while (keys.hasMoreElements())
//				{
//					Object key = keys.nextElement();
//					if (key instanceof String && ((String) key).toLowerCase().matches(".*internalframe.*") &&
//						((String) key).toLowerCase().matches(".*\\.close?[(icon)(button].*"))
//						System.out.println(key);
//				}
//				LOGGER.setLevel(Level.FINEST);
//				try
//				{
//					ImageProvider.getInstance().loadCache(IMAGE_CACHE);
//				}
//				catch (IOException e)
//				{
//					e.printStackTrace();
//				}
				new BpmnEditorWindow();
			}
		});
	}
	
	/**
	 *  Initializes static information, after logging has been set up.
	 */
	public static final void initialize()
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
			Logger.getLogger(BpmnEditor.APP_NAME).log(Level.WARNING, "Could not load task information, using fallback...");
			TASK_INFOS = new HashMap<String, TaskMetaInfo>();
			for (int i = 0; i < FALLBACK_TASK_NAMES.length; ++i)
			{
				TASK_INFOS.put(FALLBACK_TASK_NAMES[i], null);
			}
		}
		
		TASK_INFOS.put("", null);
	}
}
