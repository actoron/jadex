package jadex.micro.examples.mandelbrot;

import jadex.commons.IFuture;
import jadex.commons.concurrent.SwingDefaultResultListener;
import jadex.commons.service.IServiceProvider;
import jadex.commons.service.SServiceProvider;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 *  Panel for displaying calculated results.
 */
public class DisplayPanel extends JPanel
{
	//-------- constants --------
	public static final Color[]	COLORS	= new Color[16];
	
	static
	{
		Color	start	= new Color(50, 100, 0);
		Color	end	= new Color(255, 0, 0);		
		for(int i=0; i<COLORS.length; i++)
		{
			COLORS[i]	= new Color(
				(int)(start.getRed()+(double)i/COLORS.length*(end.getRed()-start.getRed())),
				(int)(start.getGreen()+(double)i/COLORS.length*(end.getGreen()-start.getGreen())),
				(int)(start.getBlue()+(double)i/COLORS.length*(end.getBlue()-start.getBlue()))); 
		}
	}
	
	//-------- attributes --------
	
	/** The latest area data used for determining original coordinates of painted regions. */
	protected AreaData	data;
	
	/** The current image derived from the results. */
	protected Image	image;
	
	/** The current selection point (if any). */
	protected Point	point;
	
	/** The current selection range (if any). */
	protected Rectangle	range;
	
	//-------- constructors --------
	
	/**
	 *  Create a new display panel.
	 */
	public DisplayPanel(final IServiceProvider provider)
	{
		addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent e)
			{
				if(SwingUtilities.isRightMouseButton(e))
				{
					final Rectangle	bounds	= getInnerBounds();
					SServiceProvider.getService(provider, IGenerateService.class)
					.addResultListener(new SwingDefaultResultListener()
					{
						public void customResultAvailable(Object source, Object result)
						{
							IGenerateService	gs	= (IGenerateService)result;
							IFuture	fut	= gs.generateArea(-2, -1, 1, 1,
								bounds.width, bounds.height, data.getMax(), data.getParallel());
							fut.addResultListener(new SwingDefaultResultListener(DisplayPanel.this)
							{
								public void customResultAvailable(Object source, Object result)
								{
									DisplayPanel.this.setResults((AreaData)result);
								}
							});
						}
					});						

					if(range!=null)
					{
						range	= null;
						repaint();
					}
				}
				
				else if(range!=null)
				{
					if(e.getX()>=range.x && e.getX()<=range.x+range.width
						&& e.getY()>=range.y && e.getY()<=range.y+range.height)
					{
						// Calculate bounds relative to original image.
						final Rectangle	bounds	= getInnerBounds();
						final double	x	= (double)(range.x-bounds.x)/bounds.width;
						final double	y	= (double)(range.y-bounds.y)/bounds.height;
						final double	x2	= x + (double)range.width/bounds.width;
						final double	y2	= y + (double)range.height/bounds.height;
						
						// Original bounds
						final double	ox	= data.getXStart();
						final double	oy	= data.getYStart();
						final double	owidth	= data.getXEnd()-data.getXStart();
						final double	oheight	= data.getYEnd()-data.getYStart();
						
						SServiceProvider.getService(provider, IGenerateService.class)
							.addResultListener(new SwingDefaultResultListener()
						{
							public void customResultAvailable(Object source, Object result)
							{
								IGenerateService	gs	= (IGenerateService)result;
								IFuture	fut	= gs.generateArea(ox+owidth*x, oy+oheight*y, ox+owidth*x2, oy+oheight*y2,
									bounds.width, bounds.height, data.getMax(), data.getParallel());
								fut.addResultListener(new SwingDefaultResultListener(DisplayPanel.this)
								{
									public void customResultAvailable(Object source, Object result)
									{
										DisplayPanel.this.setResults((AreaData)result);
									}
								});
							}
						});						
					}
					
					range	= null;
					repaint();
				}
			}
			
			public void mousePressed(MouseEvent e)
			{
				if(SwingUtilities.isRightMouseButton(e))
				{
					DisplayPanel.this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
				}
				else
				{
					point	= e.getPoint();
					DisplayPanel.this.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
				}
			}
			
			public void mouseReleased(MouseEvent e)
			{
				DisplayPanel.this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}
		});
		addMouseMotionListener(new MouseAdapter()
		{
			public void mouseMoved(MouseEvent e)
			{
				if(range!=null)
				{
					if(e.getX()>=range.x && e.getX()<=range.x+range.width
						&& e.getY()>=range.y && e.getY()<=range.y+range.height)
					{
						DisplayPanel.this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
					}
					else
					{
						DisplayPanel.this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));						
					}
				}
			}
			
			public void mouseDragged(MouseEvent e)
			{
				if(point!=null)
				{
					range	= new Rectangle(
						point.x<e.getX() ? point.x : e.getX(),
						point.y<e.getY() ? point.y : e.getY(),
						Math.abs(point.x-e.getX()), Math.abs(point.y-e.getY()));
				}
				
				repaint();
			}
		});
	}
	
	//-------- methods --------
	
	/**
	 *  Set new results.
	 */
	public void	setResults(final AreaData data)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				int[][]	results	= data.getData();
				DisplayPanel.this.data	= data;
				DisplayPanel.this.image	= createImage(results.length, results[0].length);
				Graphics	g	= image.getGraphics();
				for(int x=0; x<results.length; x++)
				{
					for(int y=0; y<results[x].length; y++)
					{
						Color	c;
						if(results[x][y]==-1)
						{
							c	= Color.black;
						}
						else
						{
							c	= COLORS[results[x][y]%COLORS.length];
						}
						g.setColor(c);
						g.drawLine(x, y, x, y);
					}
				}
				getParent().invalidate();
				getParent().doLayout();
				getParent().repaint();
			}
		});
	}
	
	//-------- JPanel methods --------
	
	/**
	 *  Paint the results.
	 */
	protected void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		
		if(image!=null)
		{
			Rectangle bounds = getInnerBounds();

			g.drawImage(image, bounds.x, bounds.y, bounds.x+bounds.width, bounds.y+bounds.height,
				0, 0, image.getWidth(this), image.getHeight(this), this);
		}
		
		if(range!=null)
		{
			g.setXORMode(Color.white);
			g.drawRect(range.x, range.y, range.width, range.height);
			g.setPaintMode();
		}
	}

	/**
	 *  Get the bounds with respect to insets (if any).
	 */
	protected Rectangle getInnerBounds()
	{
		Rectangle	bounds	= getBounds();
		Insets	insets	= getInsets();
		if(insets!=null)
		{
			bounds.x	= insets.left;
			bounds.y	= insets.top;
			bounds.width	-= insets.left + insets.right;
			bounds.height	-= insets.top + insets.bottom;
		}
		else
		{
			bounds.x	= 0;
			bounds.y	= 0;
		}
		return bounds;
	}
	
	/**
	 *  Get the desired size of the panel.
	 */
	public Dimension getPreferredSize()
	{
		Dimension	ret	= super.getPreferredSize();
		if(image!=null)
		{
			ret.width	+= image.getWidth(this);
			ret.height	+= image.getHeight(this);
		}
		return ret;
	}
	
	/**
	 *  Get the desired size of the panel.
	 */
	public Dimension getMinimumSize()
	{
		Dimension	ret	= super.getMinimumSize();
		if(image!=null)
		{
			ret.width	+= image.getWidth(this);
			ret.height	+= image.getHeight(this);
		}
		return ret;
	}
}
