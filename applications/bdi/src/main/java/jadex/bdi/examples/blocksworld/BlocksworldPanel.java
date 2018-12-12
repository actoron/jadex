package jadex.bdi.examples.blocksworld;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.util.Set;

import javax.swing.JPanel;

import jadex.commons.beans.PropertyChangeEvent;
import jadex.commons.beans.PropertyChangeListener;
import jadex.commons.collection.WeakSet;


/**
 *  Shows the blocksworld.
 */
public class BlocksworldPanel	extends JPanel
{
	//-------- constants --------

	/** The block placement x variance as fraction of the total available space (0-1). */
	protected static final double	XVARIANCE	= 0.2;

	/** The block placement y variance as fraction of the total available space (0-XVARIANCE). */
	protected static final double	YVARIANCE	= 0.04;

	//-------- attributes --------

	/** The table. */
	protected Table	table;

	/** The change listener to update the gui. */
	protected PropertyChangeListener	pcl;

	/** The known blocks. */
	protected Set<Block>	blocks;

	/** The block size (in pixels). */
	protected int	blocksize;

	/** The imaginary flag. */
	protected boolean	imaginary;

	//-------- constructors --------

	/**
	 *  Create a blocksworld panel.
	 *  @param table	The table.
	 *  @param imaginary	Flag indicating that its not the real world.
	 */
	public BlocksworldPanel(Table table, boolean imaginary)
	{
		this.table	= table;
		this.imaginary	= imaginary;
		this.blocksize	= 100;
		this.blocks	= new WeakSet<Block>();

		// Update gui when table changes.
		this.pcl	= new PropertyChangeListener()
		{
			public void	propertyChange(PropertyChangeEvent pce)
			{
				// Update gui.
				BlocksworldPanel.this.invalidate();
				BlocksworldPanel.this.repaint();

				// Add listener for new blocks.
				BlocksworldPanel.this.observeNewBlocks();
			}
		};
		table.addPropertyChangeListener(pcl);

		// Add listener to blocks.
		observeNewBlocks();
	}

	//-------- methods --------

	/**
	 *  Set the size of the blocks.
	 */
	public void	setBlockSize(int blocksize)
	{
		this.blocksize	= blocksize;
		this.revalidate();
		this.repaint();
	}

	/**
	 *  Get the size of the blocks.
	 */
	public int	getBlockSize()
	{
		return this.blocksize;
	}

	/**
	 *  Get the preferred size of the panel.
	 */
	public Dimension	getPreferredSize()
	{
		Dimension	grid	= getGridDimension();
		Insets	insets	= getInsets();
		return new Dimension(
			(int)Math.ceil(grid.width*blocksize*(1+XVARIANCE))
				+ insets.left + insets.right + 2*blocksize/5,
			grid.height*blocksize + insets.top	+ insets.bottom + blocksize/2);
	}

	/**
	 *  Determine grid dimension (numx, numy).
	 */
	public Dimension	getGridDimension()
	{
		Dimension	dim	= new Dimension(table.blocks.size(), 0);
		Block[]	baseblocks	= (Block[])table.blocks.toArray(new Block[dim.width]);
		for(int x=0; x<baseblocks.length; x++)
		{
			int y	= 0;
			Block	b	= baseblocks[x];
			while(b!=null)
			{
				y++;
				b	= b.upper;
			}
			dim.height	= Math.max(dim.height, y);
		}
		return dim;
	}

	/**
	 *  Add listener to new blocks.
	 */
	protected void	observeNewBlocks()
	{
		// Remove old listeners.
		for(Block b: blocks)
		{
			b.removePropertyChangeListener(pcl);
		}
		blocks.clear();
		
		// Traverse all blocks.
		Block[]	baseblocks	= (Block[])table.blocks.toArray(new Block[table.blocks.size()]);
		for(int i=0; i<baseblocks.length; i++)
		{
			Block	b	= baseblocks[i];
			while(b!=null)
			{
				b.addPropertyChangeListener(pcl);
				blocks.add(b);
				b	= b.upper;
			}
		}
	}

	/**
	 *  The overridden paint method.
	 */
	protected void	paintComponent(Graphics g)
	{
		super.paintComponent(g);

		// Determine draw area.
		Rectangle	bounds	= getBounds();
		Insets	insets	= getInsets();
		bounds.x	= insets.left;
		bounds.y	= insets.top;
		bounds.width	-= insets.left + insets.right;
		bounds.height	-= insets.top + insets.bottom;

		// Calculate offsets.
		int	xvariance	= (int)(blocksize*XVARIANCE);
		int	yvariance	= (int)(blocksize*YVARIANCE);
		Dimension	grid	= getGridDimension();
		bounds.x	+= (bounds.width-grid.width*(blocksize+xvariance))/2 - blocksize/15;
//		bounds.y	-= (bounds.height-grid.height*blocksize)/2;	// Centered
		bounds.y	-= blocksize/5;	// Bottom

		// Paint table.
		Color	color	= table.getColor();
		if(imaginary)
		{
			color	= new Color(color.getRed(), color.getGreen(),
				color.getBlue(), 64);
		}
		g.setColor(color);
		g.fillRect(insets.left, bounds.y + bounds.height - blocksize/2,
			bounds.width, blocksize/2 - bounds.y + insets.top);
		g.setColor(color.darker());
		g.drawLine(insets.left, bounds.y + bounds.height - blocksize/2,
			insets.left+bounds.width-1, bounds.y + bounds.height - blocksize/2);

		// Now paint blocks.
		Block[]	baseblocks	= (Block[])table.blocks.toArray(new Block[table.blocks.size()]);
		for(int x=0; x<baseblocks.length; x++)
		{
			int y	= 0;
			Block	b	= baseblocks[x];
			while(b!=null)
			{
				y++;
				paintBlock(g, b,
					bounds.x + x*(blocksize+xvariance) + (int)(xvariance*b.dx  + (int)(yvariance*b.dy)),	// x
					bounds.y + bounds.height - y*blocksize + (int)(yvariance*b.dy));	// y
				b	= b.upper;
			}
		}
	}

	/**
	 *  Paint a block.
	 */
	protected void	paintBlock(Graphics g, Block b, int x, int y)
	{
		Color	block0	= b.getColor();
		Color	block1	= block0.brighter();
		Color	block2	= block0.darker();
		Color	border1	= block0.darker();
		Color	border2	= block0.darker();
		if(imaginary)
		{
			block0	= new Color(block0.getRed(), block0.getGreen(),
				block0.getBlue(), 32);
			block1	= new Color(block1.getRed(), block1.getGreen(),
				block1.getBlue(), 32);
			block2	= new Color(block2.getRed(), block2.getGreen(),
				block2.getBlue(), 32);
			border1	= new Color(border1.getRed(), border1.getGreen(),
				border1.getBlue(), 128);
			border2	= new Color(border2.getRed(), border2.getGreen(),
				border2.getBlue(), 48);

			// Draw bottom.
			int[]	xp	= new int[]{x+blocksize+blocksize/5-1, x+blocksize/5, x, x+blocksize-1};
			int[]	yp	= new int[]{y+blocksize-blocksize/5-1, y+blocksize-blocksize/5-1, y+blocksize-1, y+blocksize-1};
			g.setColor(block2);
			g.fillPolygon(xp, yp, 4);
			g.setColor(border2);
			g.drawPolyline(xp, yp, 3);
			// Draw left side.
			xp	= new int[]{x+blocksize/5, x+blocksize/5, x, x};
			yp	= new int[]{y+blocksize-blocksize/5-1, y-blocksize/5, y, y+blocksize-1};
			g.setColor(block1);
			g.fillPolygon(xp, yp, 4);
			g.setColor(border2);
			g.drawPolyline(xp, yp, 2);

			// Draw backside.
			g.setColor(block0);
			g.fillRect(x+blocksize/5, y-blocksize/5, blocksize, blocksize);
		}

		// Draw frontside.
		g.setColor(block0);
		g.fillRect(x, y, blocksize, blocksize);
		g.setColor(border1);
		g.drawRect(x, y, blocksize-1, blocksize-1);
/*		g.setColor(Color.black);
		g.drawString(b.toString(), x+2, y+g.getFont().getSize());
		g.drawString("u: "+b.upper, x+2, y+g.getFont().getSize()*2);
		g.drawString("l: "+b.lower, x+2, y+g.getFont().getSize()*3);
*/
		// Draw top.
		int[]	xp	= new int[]{x+blocksize-1, x+blocksize+blocksize/5-1, x+blocksize/5, x};
		int[]	yp	= new int[]{y, y-blocksize/5, y-blocksize/5, y};
		g.setColor(block1);
		g.fillPolygon(xp, yp, 4);
		g.setColor(border1);
		g.drawPolyline(xp, yp, 4);
		// Draw right side.
		xp	= new int[]{x+blocksize-1, x+blocksize+blocksize/5-1, x+blocksize+blocksize/5-1, x+blocksize-1};
		yp	= new int[]{y+blocksize-1, y+blocksize-blocksize/5-1, y-blocksize/5, y};
		g.setColor(block2);
		g.fillPolygon(xp, yp, 4);
		g.setColor(border1);
		g.drawPolyline(xp, yp, 3);
	}
}

