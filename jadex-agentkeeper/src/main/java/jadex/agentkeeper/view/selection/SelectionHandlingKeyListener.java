package jadex.agentkeeper.view.selection;

import jadex.agentkeeper.game.userinput.UserEingabenManager;
import jadex.agentkeeper.view.GeneralAppState;

import com.jme3.input.controls.ActionListener;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;



public class SelectionHandlingKeyListener implements ActionListener
{

	private SelectionHandler	handler;
	public boolean	actionIsPressed	= false, cancelIsPressed = false;

	public SelectionHandlingKeyListener(SelectionHandler handler)
	{
		this.handler = handler;
	}

	public void onAction(String name, boolean keyPressed, float tpf)
	{
		if(name.equals("Lclick") && handler.isOnView())
		{ 
			
			if(keyPressed && !cancelIsPressed)
			{
				actionIsPressed = true;
				if(handler.getSelectionArea() != null)
				{
					handler.placeSelectionBox(handler.getRounded2dMousePos().x, handler.getRounded2dMousePos().y);
				}
				handler.updateSelectionBox();
			}
			else
			{
				actionIsPressed = false;
				if(handler.getSelectionArea() != null)
				{
					handler.userSubmit(handler.getSelectionArea());
				}
			}
		}


	}

}
