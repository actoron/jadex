package jadex.gpmn.editor.gui;

import jadex.gpmn.editor.gui.controllers.EdgeCreationController;
import jadex.gpmn.editor.gui.controllers.EdgeReconnectController;
import jadex.gpmn.editor.gui.controllers.FoldController;
import jadex.gpmn.editor.gui.controllers.SelectionController;
import jadex.gpmn.editor.gui.controllers.ValueChangeController;

public interface IControllerAccess
{
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
	 *  Returns the controller for handling value changes.
	 *   
	 *  @return The controller.
	 */
	public ValueChangeController getValueChangeController();
	
	/**
	 *  Returns the controller for handling selections.
	 *   
	 *  @return The controller.
	 */
	public SelectionController getSelectionController();
	
	/**
	 *  Returns the controller for handling edge reconnects.
	 *   
	 *  @return The controller.
	 */
	public EdgeReconnectController getEdgeReconnectController();
	
	/**
	 *  Returns the controller for handling edge creation.
	 *   
	 *  @return The controller.
	 */
	public EdgeCreationController getEdgeCreationController();
	
	/**
	 *  Returns the controller for folding.
	 *   
	 *  @return Fold controller.
	 */
	public FoldController getFoldController();
}
