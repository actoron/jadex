package jadex.bdi.examples.antworld;

import jadex.adapter.base.envsupport.math.IVector2;

public interface PositionObject {

	/**
	 * Returns position of object.
	 * @param vector
	 * @return
	 */
	public IVector2 getPosition();
	
	/**
	 * Set position of object.
	 * @param position
	 */
	public void setPosition(IVector2 position);
}
