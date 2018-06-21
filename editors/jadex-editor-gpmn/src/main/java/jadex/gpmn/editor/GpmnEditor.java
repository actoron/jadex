package jadex.gpmn.editor;

import java.util.logging.Level;
import java.util.logging.Logger;

import jadex.gpmn.editor.gui.GpmnEditorWindow;

/**
 *  GPMN editor main class.
 *
 */
public class GpmnEditor
{
	/** Current version. */
	public static final int BUILD = 4;
	
	/** The name of the application. */
	public static final String APP_NAME = "Jadex GPMN Editor";
	
	/** Log level for status area. */
	public static final Level STATUS_AREA_LOG_LEVEL = Level.INFO;
	
	/** Main Logger. */
	public static final Logger LOGGER = Logger.getLogger(APP_NAME);
	
	/**
	 *  Main method.
	 *  
	 *  @param args Arguments.
	 */
	public static void main(String[] args)
	{
		new GpmnEditorWindow();
	}
}
