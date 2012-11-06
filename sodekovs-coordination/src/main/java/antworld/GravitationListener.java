package antworld;

import jadex.extension.envsupport.environment.ObjectEvent;

import jadex.commons.beans.PropertyChangeEvent;
import jadex.commons.beans.PropertyChangeListener;

public class GravitationListener implements PropertyChangeListener {

	public final static String FEELS_GRAVITATION ="feels_gravitation"; 
	
	/**
	 * This event gets called when an environment object event is triggered.
	 */
	public void dispatchObjectEvent(ObjectEvent event) {


		if (event instanceof FeelGravitationEvent) {
			System.out.println("#GravitationListener# dispatched following ObjectEvent. Type: FeelGravitationEvent");
		}
		if (event instanceof ObjectEvent) {
			System.out.println("#GravitationListener# dispatched following ObjectEvent. Type: ObjectEvent");
		}
	}

	public void propertyChange(PropertyChangeEvent evt) {
		// TODO Auto-generated method stub		
		System.out.println("#GravitationListener# Received PropertyChangeEvent: " + evt.getSource().toString());
		if(evt.getPropertyName().equals(FEELS_GRAVITATION)){
			System.out.println("Gravitation changed?");
		}
//		System.out.println("#BurnerVisionGenerator# dispatched following EnvironmentEvent. Type: " + event.getSpaceObject().getType()  + ", " + event.getInfo().toString());
	}
}
