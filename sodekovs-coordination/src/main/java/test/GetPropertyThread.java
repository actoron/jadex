package test;

import jadex.application.space.envsupport.environment.IEnvironmentSpace;
import deco.lang.dynamics.MASDynamics;

/**
 * Responsible for starting all things necessary for deco4mas
 * 
 * @author Ante Vilenica
 * 
 */
public class GetPropertyThread extends Thread {

	private IEnvironmentSpace space = null;

	/** Reference to the used MAS-File. */
	private String masFileName;

	/**
	 * 
	 * @param space
	 */
	public GetPropertyThread(IEnvironmentSpace space) {
		this.space = space;
	}

	public void run() {

		/** The dynamics model. */
		MASDynamics masDyn = null;

		// Make sure that the space has been initialized... (Kind of Hack)
		
			
		synchronized (this) {
			
		
		while (space.getProperty("dynamics_configuration") == null) {
			try {
				wait(500);
				System.out.println("#GetPropertyThread# - Waiting");
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		}
		System.out.println("#GetPropertyThread# Got property: " + space.getProperty("dynamics_configuration"));
	}

}
