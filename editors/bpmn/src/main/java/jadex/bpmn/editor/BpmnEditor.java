package jadex.bpmn.editor;

import java.awt.EventQueue;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import com.mxgraph.view.mxStylesheet;

import jadex.bpmn.editor.gui.BpmnEditorWindow;
import jadex.bpmn.editor.gui.stylesheets.BpmnStylesheetColor;
import jadex.bpmn.editor.gui.stylesheets.BpmnStylesheetColorGradient;
import jadex.bpmn.editor.gui.stylesheets.BpmnStylesheetComplexGrayscale;
import jadex.bpmn.editor.gui.stylesheets.BpmnStylesheetSimpleGrayscale;
import jadex.bpmn.task.info.TaskMetaInfo;
import jadex.commons.SUtil;
import jadex.commons.Tuple2;
import jadex.transformation.jsonserializer.JsonTraverser;

/**
 *  Class for starting the BPMN editor.
 */
public class BpmnEditor
{
	/** Current version. */
	public static final int BUILD = 86;
	
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
	
	static
	{
		// Avoids a GUI bug on modern Linux systems.
		System.setProperty("sun.java2d.xrender", "true");
	}
	
	/** The style sheets. */
	@SuppressWarnings("unchecked")
	// Hack!!! generics expression doesn't compile on build servers (JDK 1.6/1.7/1.8 ???)
//	public static List<Tuple2<String, mxStylesheet>> STYLE_SHEETS = new ArrayList<Tuple2<String, mxStylesheet>>((Collection<? extends Tuple2<String, mxStylesheet>>) Arrays.asList(new Tuple2[] 
	public static List<Tuple2<String, mxStylesheet>> STYLE_SHEETS = new ArrayList(Arrays.asList(new Tuple2[] 
		{
			new Tuple2<String, mxStylesheet>("Color Gradient", new BpmnStylesheetColorGradient()),
			new Tuple2<String, mxStylesheet>("Color", new BpmnStylesheetColor()),
			new Tuple2<String, mxStylesheet>("Simple Grayscale", new BpmnStylesheetSimpleGrayscale()),
			new Tuple2<String, mxStylesheet>("Complex Grayscale", new BpmnStylesheetComplexGrayscale())
		}));
	
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
	
	public static Map<String, String> STRINGS;
	static
	{
		try
		{
			String res = BpmnEditor.class.getPackage().getName().replaceAll("\\.", "/") + "/strings_USA_eng.properties";
			InputStream sis = BpmnEditor.class.getClassLoader().getResourceAsStream(res);
			Properties strings = new Properties();
			strings.load(sis);
			sis.close();
			STRINGS = propertiesToMap(strings);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
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
		
//		try
//		{
//			String country = Locale.getDefault().getISO3Country();
//			String lang = Locale.getDefault().getISO3Language();
//			InputStream sis = BpmnEditor.class.getClassLoader().getResourceAsStream(BpmnEditor.class.getPackage().getName().replaceAll("\\.", "/") + "/strings_" + country + "_" + lang + ".properties");
//			Properties strings = new Properties();
//			strings.load(sis);
//			sis.close();
//			STRINGS = propertiesToMap(strings);
//		}
//		catch (Exception e)
//		{
//			e.printStackTrace();
//		}

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
		InputStream is = SUtil.getResource0("/jadex/bpmn/editor/task_infos.json", BpmnEditor.class.getClassLoader());
		if (is != null)
		{
			try
			{
				byte[] bytes = SUtil.readStream(is);
				
				JsonTraverser.objectFromByteArray(bytes, BpmnEditor.class.getClassLoader(), "UTF-8");
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		
		synchronized(BpmnEditor.class)
		{
			if (TASK_INFOS == null)
			{
				Logger.getLogger(BpmnEditor.APP_NAME).log(Level.WARNING, "Could not load task information, using fallback...");
				TASK_INFOS = new HashMap<String, TaskMetaInfo>();
				for (int i = 0; i < FALLBACK_TASK_NAMES.length; ++i)
				{
					TASK_INFOS.put(FALLBACK_TASK_NAMES[i], null);
				}
			}
		}
		
		TASK_INFOS.put("", null);
	}
	
	/**
	 *  Gets a localized string.
	 * 
	 *  @param key String key.
	 *  @return The string.
	 */
	public static String getString(String key)
	{
		String ret = key;
		if (STRINGS != null)
		{
			String rkey = key.replaceAll("_", "__");
			rkey = rkey.replaceAll(" ", "_");
//			System.out.println("RKEY " + rkey);
			if (STRINGS.containsKey(rkey))
			{
				ret = STRINGS.get(rkey);
			}
			else
			{
				System.out.println("Key not found: " + key);
			}
		}
		else
		{
			System.out.println("STRINGS null.");
		}
		return ret;
	}
	
	/**
	 *  Converts properties to a map.
	 * 
	 *  @param props Properties.
	 *  @return The map.
	 */
	protected static Map<String, String> propertiesToMap(Properties props)
	{
		Map<String, String> ret = new HashMap<String, String>();
		for (Object okey : props.keySet())
		{
			String key = (String) okey;
			ret.put(key, props.getProperty(key));
		}
		return ret;
	}
}
