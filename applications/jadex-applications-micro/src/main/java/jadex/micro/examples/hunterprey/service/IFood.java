package jadex.micro.examples.hunterprey.service;

/**
 *  Representation of a perceived food.
 */
public interface IFood	extends IPreyPerceivable
{
	/**
	 *  Get the x coordinate relative to the perceiving creature.
	 */
	public int	getX();
	
	/**
	 *  Get the y coordinate relative to the perceiving creature.
	 */
	public int	getY();
}
