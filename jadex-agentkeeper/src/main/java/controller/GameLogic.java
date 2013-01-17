package controller;

import java.awt.Component;

import jadex.commons.IPropertyObject;
import jadex.extension.envsupport.environment.IEnvironmentSpace;
import jadex.extension.envsupport.observer.gui.ObserverCenter;
import jadex.extension.envsupport.observer.gui.plugin.AbstractInteractionPlugin;

import agentkeeper.gui.GUIInformierer;
import agentkeeper.gui.Listener;
import agentkeeper.gui.UserEingabenManager;

import com.jme3.app.state.AbstractAppState;

public class GameLogic extends AbstractInteractionPlugin implements Listener, IPropertyObject {
	
	private UserEingabenManager _usermanager;
	private ObserverCenter _obsCenter;
	private IEnvironmentSpace _space;

	public String getName() {
		return null;
	}

	public Component getView() {
		return null;
	}

	@Override
	protected void initialize(ObserverCenter arg0) {
		_space = arg0.getSpace();
		_usermanager =  (UserEingabenManager) _space.getProperty("uem");
//		GUIInformierer.addListener(this);
		
	}

	public void aktualisierung() {
		// TODO Auto-generated method stub
		
	}

}
