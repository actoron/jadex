package jadex.bdiv3.examples.blocksworld;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import jadex.commons.SUtil;


/**
 *  A table in the blocks-world.
 *  In contrast to a block, a table may have multiple blocks on top.
 */
public class Table	extends Block
{
	//-------- attributes --------

	/** The name of the table. */
	protected String	name;

	/** The blocks located on top of the table. */
	protected List	blocks;

//	/** The target configuration (if any). */
//	protected Table	target;

//	/** The block listener (if any). */
//	protected PropertyChangeListener	listener;

//	/** The old solution state. */
//	protected boolean	solution;

	//-------- constructors --------

	/**
	 *  Create a new table.
	 */
	public Table()
	{
		this("Table", new Color(64, 32, 32));
	}

	/**
	 *  Create a new table.
	 * /
	public Table(Table target)
	{
		this();
		this.target	= target;
		if(target!=null)
		{
			this.solution	= configurationEquals(target);
			listener	= new PropertyChangeListener()
			{
				public void propertyChange(PropertyChangeEvent evt)
				{
					boolean	oldsolution	= solution;
					solution	= configurationEquals(Table.this.target);
					pcs.firePropertyChange("solution", oldsolution, solution);
				}
			};
		}
	}*/

	/**
	 *  Create a new table.
	 *  @param name	The name of the table.
	 *  @param color	The color of the table.
	 */
	public Table(String name, Color color)
	{
		super(-1, color, null);
		this.name	= name;
		this.blocks	= new ArrayList();
	}
	
//	/**
//	 *  Create a new table.
//	 */
//	public Table(Table old)
//	{
//		this(old.name, old.color);
//		Map<Integer, Block> clones = new HashMap<Integer, Block>();
//		clones.put(this.number, this);
//		for(Block b: old.getAllBlocks())
//		{
//			blocks.add(new Block(b, clones));
//		}
//	}

	//-------- methods --------

	/**
	 *  The table is always clear.
	 */
	public boolean	isClear()
	{
		return true;
	}

	/**
	 *  Create a string representation of this block.
	 */
	public String	toString()
	{
		return name;
//		StringBuffer buf = new StringBuffer();
//
//		buf.append(name).append("\n");
//		
//		Block[][] stacks = getStacks();
//		for(Block[] stack: stacks)
//		{
//			buf.append(SUtil.arrayToString(stack));
//		}
//		
//		return buf.toString();
	}

	/**
	 *  Get all blocks on the table.
	 *  Also returns blocks which are located on other blocks on the table.
	 */
	public Block[]	getAllBlocks()
	{
		List	ret	= new ArrayList(blocks);
		for(int i=0; i<ret.size(); i++)
		{
			Block	b	= (Block)ret.get(i);
			if(b.upper!=null)
				ret.add(b.upper);
		}
		return (Block[])ret.toArray(new Block[ret.size()]);
	}

	/**
	 *  Get the stacks on the table.
	 */
	public Block[][]	getStacks()
	{
		Block[][]	stacks	= new Block[blocks.size()][];
		for(int i=0; i<stacks.length; i++)
		{
			List	ret	= new ArrayList();
			Block	b	= (Block)blocks.get(i);
			while(b!=null)
			{
				ret.add(b);
				b	= b.upper;
			}
			stacks[i]	= (Block[])ret.toArray(new Block[ret.size()]);
		}
		return stacks;
	}

	/**
	 *  clear all blocks from the table.
	 */
	public void	clear()
	{
		Block[]	all	= getAllBlocks();
		for(int i=all.length-1; i>=0; i--)
			all[i].stackOn(null);
	}

	/**
	 *  Check if two configurations are equal.
	 */
	public boolean	configurationEquals(Table table)
	{
		boolean	ret	= false;
		if(blocks.size()==table.blocks.size())
		{
			// Iterate through base blocks.
			ret	= true;
			for(Iterator i=blocks.iterator(); ret && i.hasNext(); )
			{
				Block	block	= (Block)i.next();
				int	index	= table.blocks.indexOf(block);
				if(index!=-1)
				{
					// Traverse and compare corresponding stacks.
					Block	block2	= (Block)table.blocks.get(index);
					while((block!=null || block2!=null)
						&& (ret=SUtil.equals(block, block2)))
					{
						block	= block.upper;
						block2	= block2.upper;
					}
				}
				else
				{
					ret	= false;
				}
			}
		}
		return ret;
	}

	/**
	 *  Add a block to this block.
	 */
	protected void	addBlock(Block block)
	{
		blocks.add(block);
//		if(listener!=null)
//			block.addPropertyChangeListener(listener);
		this.pcs.firePropertyChange("allBlocks", null, block);
	}

	/**
	 *  Remove a block from this block.
	 */
	protected void	removeBlock(Block block)
	{
		blocks.remove(block);
//		if(listener!=null)
//			block.removePropertyChangeListener(listener);
		this.pcs.firePropertyChange("allBlocks", block, null);
	}

	/**
	 *  Check for equality.
	 * /
	public boolean equals(Object o)
	{
		return o instanceof Table && configurationEquals((Table)o);
	}*/
	
	/**
	 *  Check for equality.
	 * /
	public boolean isSolution()
	{
		return solution;
	}*/
	
	/**
	 *  Get the hash code.
	 * /
	public int hashCode()
	{
		int	ret	= 1;
		List	allblocks	= new ArrayList(blocks);
		for(int i=0; i<allblocks.size(); i++)
		{
			Block	b	= (Block)allblocks.get(i);
			ret	= ret*31 + b.hashCode();
			if(b.upper!=null)
				allblocks.add(b.upper);
		}
		return ret;
	}*/
	
}

