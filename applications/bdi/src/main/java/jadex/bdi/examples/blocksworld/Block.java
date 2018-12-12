package jadex.bdi.examples.blocksworld;

import java.awt.Color;

import jadex.commons.SimplePropertyChangeSupport;
import jadex.commons.beans.PropertyChangeListener;


/**
 *  A block in the blocks-world.
 */
public class Block
{
	//-------- static part --------

	/** The block counter. */
	static int counter	= 0;
	
	/**
	 *  Get an instance number.
	 */
	protected static synchronized int	getNumber()
	{
		return ++counter;
	}

	//-------- attributes --------

	/** The number of the block. */
	protected int	number;

	/** The color of the block. */
	protected Color	color;

	/** The block where this block is located on. */
	protected Block	lower;

	/** The block located on upper of this block. */
	protected Block	upper;

	/** The helper object for bean events. */
	public SimplePropertyChangeSupport pcs;

	/** The x translation for drawing (0-1). */
	protected double	dx;

	/** The y translation for drawing (0-1). */
	protected double	dy;

	//-------- constructors --------

	/**
	 *  Create a new block.
	 *  @param color	The color of the block.
	 *  @param lower	The block where this block is located on.
	 */
	public Block(Color color, Block lower)
	{
		this(getNumber(), color, lower);
	}

	/**
	 *  Create a new block.
	 *  @param number	The number of the block.
	 *  @param color	The color of the block.
	 *  @param lower	The block where this block is located on.
	 */
	public Block(int number, Color color, Block lower)
	{
		this.number	= number;
		this.color	= color;
		this.pcs = new SimplePropertyChangeSupport(this);
		stackOn(lower);
	}

	//-------- methods --------

	/**
	 *  Get the color of the block.
	 *  @return The color of the block.
	 */
	public Color	getColor()
	{
		return color;
	}

	/**
	 *  Get the block where this block is located on.
	 *  @return The block where this block is located on.
	 */
	public Block	getLower()
	{
		return lower;
	}

	/**
	 *  Check if this block is clear.
	 */
	public boolean	isClear()
	{
		return upper==null;
	}

	/**
	 *  Move this block on top of another block.
	 */
	public void	stackOn(Block lower)
	{
		// Check if block can be moved.
		if(!isClear())
		{
			throw new RuntimeException("Can only move clear blocks: "+this);
		}
		else if(lower==this)
		{
			throw new RuntimeException("Cannot move block on itself: "+this);
		}

		// Remove this block from old lower block.
		if(this.lower!=null)
			this.lower.removeBlock(this);

		// Move to new block.
		if(lower!=null)
		{
			// Check if there is space on block.
			if(!lower.isClear())
			{
				throw new RuntimeException("Can only stack on clear blocks: "+lower);
			}
			lower.addBlock(this);
			this.dx	= Math.random();
			this.dy	= Math.random();
		}
		setLower(lower);
	}

	/**
	 *  Set the lower block, where this block is located on.
	 *  @param lower	The lower block.
	 */
	protected void	setLower(Block lower)
	{
		Block	old	= this.lower;
		this.lower	= lower;
		this.pcs.firePropertyChange("lower", old, this.lower);
	}

	//-------- helper methods --------

	/**
	 *  Add a block to this block.
	 */
	protected void	addBlock(Block block)
	{
		boolean oldclear = isClear();
//		Block	old	= this.upper;
		this.upper	= block;
//		this.pcs.firePropertyChange("upper", old, this.upper);
		pcs.firePropertyChange("clear", Boolean.valueOf(oldclear), Boolean.valueOf(isClear()));
	}

	/**
	 *  Remove a block from this block.
	 */
	protected void	removeBlock(Block block)
	{
		boolean oldclear = isClear();
		this.upper	= null;
//		this.pcs.firePropertyChange("upper", block, null);
		pcs.firePropertyChange("clear", Boolean.valueOf(oldclear), Boolean.valueOf(isClear()));
	}

	/**
	 *  Create a string representation of this block.
	 */
	public String	toString()
	{
		return "Block "+number;
	}

	/**
	 *  Check for equality.
	 */
	public boolean	equals(Object o)
	{
		return o instanceof Block
			&& ((Block)o).number==number
			&& ((Block)o).getColor().equals(getColor());
	}
	
	/**
	 *  Calculate the hash code.
	 */
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((color == null) ? 0 : color.hashCode());
		result = prime * result + number;
		return result;
	}


	//-------- property methods --------

	/**
     *  Add a PropertyChangeListener to the listener list.
     *  The listener is registered for all properties.
     *  @param listener  The PropertyChangeListener to be added.
     */
    public void addPropertyChangeListener(PropertyChangeListener listener)
	{
		pcs.addPropertyChangeListener(listener);
    }

    /**
     *  Remove a PropertyChangeListener from the listener list.
     *  This removes a PropertyChangeListener that was registered
     *  for all properties.
     *  @param listener  The PropertyChangeListener to be removed.
     */
    public void removePropertyChangeListener(PropertyChangeListener listener)
	{
		pcs.removePropertyChangeListener(listener);
    }

}

