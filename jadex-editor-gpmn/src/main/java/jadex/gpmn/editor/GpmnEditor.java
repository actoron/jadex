package jadex.gpmn.editor;

import jadex.gpmn.editor.gui.EditorWindow;

/**
 *  GPMN editor main class.
 *
 */
public class GpmnEditor
{
	/** Current version. */
	public static final int VERSION = 1;
	
	/**
	 *  Main method.
	 *  
	 *  @param args Arguments.
	 */
	public static void main(String[] args)
	{
		new EditorWindow();
	}
}
