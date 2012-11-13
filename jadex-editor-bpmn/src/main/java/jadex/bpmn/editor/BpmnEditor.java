package jadex.bpmn.editor;

import jadex.bpmn.editor.gui.BpmnEditorWindow;

/**
 *  Class for starting the BPMN editor.
 *
 */
public class BpmnEditor
{
	/** The name of the application. */
	public static final String APP_NAME = "Jadex BPMN Editor";
	
	/**
	 *  Starts the BPMN editor.
	 *  
	 *  @param args The arguments.
	 */
	public static void main(String[] args)
	{
		new BpmnEditorWindow();
	}
}
