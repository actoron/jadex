package jadex.micro.examples.hunterprey.service.impl;

/**
 *  Object representing a perceived food.
 */
public class Food
{
	//-------- attributes --------
	
	/** The space object id of the food. */
	protected Object id;
	
	/** The x coordinate relative to perceiving creature. */
	protected int	x;
	
	/** The x coordinate relative to perceiving creature. */
	protected int	y;
	
	//-------- constructors --------
	
	/**
	 *  Create a food percept.
	 */
	public Food(Object id, int x, int y)
	{
		this.id	= id;
		this.x	= x;
		this.y	= y;
	}
	
	//-------- methods --------
	
	/**
	 *  Get the space object id of the food.
	 */
	public Object	getId()
	{
		return id;
	}
	
	/**
	 *  Set the space object id.
	 */
	public void	setId(Object id)
	{
		this.id	= id;
	}
	
	/**
	 *  Get the x coordinate relative to the perceiving creature.
	 */
	public int	getX()
	{
		return x;
	}
	
	/**
	 *  Set the x coordinate relative to the perceiving creature.
	 */
	public void	setX(int x)
	{
		this.x	= x;
	}
	
	/**
	 *  Get the y coordinate relative to the perceiving creature.
	 */
	public int	getY()
	{
		return y;
	}
	
	/**
	 *  Set the y coordinate relative to the perceiving creature.
	 */
	public void	setY(int y)
	{
		this.y	= y;
	}	
}
