package jadex.bdi.examples.antworld;

import jadex.application.space.envsupport.environment.ObjectEvent;
import jadex.application.space.envsupport.math.IVector2;

public class FeelGravitationEvent extends ObjectEvent{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected IVector2 gravitationPos;

	public FeelGravitationEvent(Object type) {
		super(type);
		// TODO Auto-generated constructor stub
	}

	public void setGravitationPos(IVector2 gravitationPos){
		this.gravitationPos = gravitationPos;		
	}
}
