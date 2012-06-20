package jadex.gpmn.editor.gui;

import jadex.gpmn.editor.gui.controllers.FoldController;

public interface IControllerAccess
{
	/** 
	 * Returns the current edit mode
	 * 
	 * @return the edit mode.
	 */
	public String getEditMode();
	
	/**
	 *  Sets the select tool as the current tool.
	 *  
	 */
	public void setSelectTool();
	
	/**
	 *  Disables deletion controller,
	 *  desynchronizing visual and business model.
	 */
	public void desynchModels();
	
	/**
	 *  Enables deletion controller,
	 *  synchronizing visual and business model.
	 */
	public void synchModels();
	
	/**
	 *  Returns the controller for folding.
	 *   
	 *  @return Fold controller.
	 */
	public FoldController getFoldController();
}
