package controller;

import agentkeeper.gui.UserEingabenManager;

import com.jme3.input.controls.ActionListener;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;

public class CustomAppStateKeyListener implements ActionListener
{
	
	private CustomAppState state;
	private UserEingabenManager	usermanager;
	
	
	public CustomAppStateKeyListener(CustomAppState state)
	{
		this.state = state;
		this.usermanager = (UserEingabenManager) state.getSpaceController().getProperty("uem");
	}
	
	protected boolean actionIsPressed = false, cancelIsPressed = false;
	

	public void onAction(String name, boolean keyPressed, float tpf)
	{

		if(name.equals("Leftclick")&&state.isOnView()) { //Pressed
            if(keyPressed && !cancelIsPressed) {
            	System.out.println("leftklick!");
                actionIsPressed = true;
                if(state.getSelectionArea() != null) {
                	state.placeSelectionBox(state.getRounded2dMousePos().x, state.getRounded2dMousePos().y);
                }
                state.updateSelectionBox();
            }else {
                actionIsPressed = false;
                if(state.getSelectionArea() != null) {
                	usermanager.destoryWalls(state.getSelectionArea());
                }
            }
        }


	}

}
