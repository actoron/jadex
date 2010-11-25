package jadex.micro.examples.mandelbrot;

import jadex.commons.IFuture;
import jadex.commons.concurrent.SwingDefaultResultListener;
import jadex.commons.service.IServiceProvider;
import jadex.commons.service.SServiceProvider;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Rectangle2D;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

/**
 *  Panel for displaying calculated results.
 */
public class DisplayPanel extends JComponent
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
	
	/** Flag indicating that a calculation is in progress. */
	protected boolean	calculating;
	
	/** Set of progress data objects (if calculating). */
	protected Set	progressset;
	
	//-------- constructors --------
	
	/**
	 *  Create a new display panel.
	 */
	public DisplayPanel(final IServiceProvider provider)
	{
		addMouseWheelListener(new MouseAdapter()
		{
			public void mouseWheelMoved(MouseWheelEvent e)
			{
				int sa = e.getScrollAmount();
				int dir = e.getWheelRotation();
				double factor = dir==1? (1+sa/10.0): (1-sa/10.0);
				
				final double xs = data.getXStart()*factor;
				final double xe = data.getXEnd()*factor;
				final double ys = data.getYStart()*factor;
				final double ye = data.getYEnd()*factor;
				
				SServiceProvider.getService(provider, IGenerateService.class)
					.addResultListener(new SwingDefaultResultListener()
				{
					public void customResultAvailable(Object source, Object result)
					{
						final Rectangle	bounds	= getInnerBounds();
						
						IGenerateService gs	= (IGenerateService)result;
						
						AreaData ad = new AreaData(xs, xe, ys, ye, bounds.width, bounds.height,
							data!=null ? data.getMax() : 256, data!=null ? data.getParallel() : 10, data!=null ? data.getTaskSize() : 160000);
						IFuture	fut	= gs.generateArea(ad);
						fut.addResultListener(new SwingDefaultResultListener(DisplayPanel.this)
						{
							public void customResultAvailable(Object source, Object result)
							{
								DisplayPanel.this.setResults((AreaData)result);
							}
							public void customExceptionOccurred(Object source, Exception exception)
							{
								calculating	= false;
								DisplayPanel.this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
								super.customExceptionOccurred(source, exception);
							}
						});
					}
				});
			}
		});
		addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent e)
			{
				if(SwingUtilities.isRightMouseButton(e))
				{
					DisplayPanel.this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
					calculating	= true;
					repaint();
					final Rectangle	bounds	= getInnerBounds();
					double	rratio	= 1;
					double	bratio	= (double)bounds.width/bounds.height;
					// Calculate pixel width/height of area.
					if(rratio<bratio)
					{
						int	width	= (int)(bounds.height*rratio);
						bounds.width	= width;
					}
					else if(rratio>bratio)
					{
						int	height	= (int)(bounds.width/rratio);
						bounds.height	= height;
					}
					
					SServiceProvider.getService(provider, IGenerateService.class)
						.addResultListener(new SwingDefaultResultListener()
					{
						public void customResultAvailable(Object source, Object result)
						{
							IGenerateService	gs	= (IGenerateService)result;
							AreaData ad = new AreaData(-2, 1, -1.5, 1.5, bounds.width, bounds.height,
								data!=null ? data.getMax() : 256, data!=null ? data.getParallel() : 10, data!=null ? data.getTaskSize() : 160000);
							IFuture	fut	= gs.generateArea(ad);
							fut.addResultListener(new SwingDefaultResultListener(DisplayPanel.this)
							{
								public void customResultAvailable(Object source, Object result)
								{
									DisplayPanel.this.setResults((AreaData)result);
								}
								public void customExceptionOccurred(Object source, Exception exception)
								{
									calculating	= false;
									DisplayPanel.this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
									super.customExceptionOccurred(source, exception);
								}
							});
						}
					});
				}
				
				else if(!calculating && range!=null)
				{
					if(e.getX()>=range.x && e.getX()<=range.x+range.width
						&& e.getY()>=range.y && e.getY()<=range.y+range.height)
					{
						// Calculate bounds relative to original image.
						final Rectangle	bounds	= getInnerBounds();
						Rectangle	drawarea	= scaleToFit(bounds, image.getWidth(DisplayPanel.this), image.getHeight(DisplayPanel.this));
						final double	x	= (double)(range.x-bounds.x-drawarea.x)/drawarea.width;
						final double	y	= (double)(range.y-bounds.y-drawarea.y)/drawarea.height;
						final double	x2	= x + (double)range.width/drawarea.width;
						final double	y2	= y + (double)range.height/drawarea.height;
						
						// Original bounds
						final double	ox	= data.getXStart();
						final double	oy	= data.getYStart();
						final double	owidth	= data.getXEnd()-data.getXStart();
						final double	oheight	= data.getYEnd()-data.getYStart();
						
						// Calculate pixel width/height of area.
						double	rratio	= (double)range.width/range.height;
						double	bratio	= (double)bounds.width/bounds.height;
						if(rratio<bratio)
						{
							int	width	= (int)(bounds.height*rratio);
							bounds.width	= width;
						}
						else if(rratio>bratio)
						{
							int	height	= (int)(bounds.width/rratio);
							bounds.height	= height;
						}
					
						DisplayPanel.this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
						calculating	= true;
						repaint();
						SServiceProvider.getService(provider, IGenerateService.class)
							.addResultListener(new SwingDefaultResultListener()
						{
							public void customResultAvailable(Object source, Object result)
							{
								IGenerateService	gs	= (IGenerateService)result;
								AreaData ad = new AreaData(ox+owidth*x, ox+owidth*x2, oy+oheight*y, oy+oheight*y2,
									bounds.width, bounds.height, data.getMax(), data.getParallel(), data.getTaskSize());
								IFuture	fut	= gs.generateArea(ad);
								fut.addResultListener(new SwingDefaultResultListener(DisplayPanel.this)
								{
									public void customResultAvailable(Object source, Object result)
									{
										DisplayPanel.this.setResults((AreaData)result);
									}
									public void customExceptionOccurred(Object source, Exception exception)
									{
										calculating	= false;
										DisplayPanel.this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
										super.customExceptionOccurred(source, exception);
									}
								});
							}
						});						
					}
				}
			}
			
			public void mousePressed(MouseEvent e)
			{
				if(!calculating)
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
			}
			
			public void mouseReleased(MouseEvent e)
			{
				if(!calculating)
				{
					DisplayPanel.this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				}
			}
		});
		addMouseMotionListener(new MouseAdapter()
		{
			public void mouseMoved(MouseEvent e)
			{
				if(!calculating && range!=null)
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
				if(!calculating && point!=null)
				{
					range	= new Rectangle(
						point.x<e.getX() ? point.x : e.getX(),
						point.y<e.getY() ? point.y : e.getY(),
						Math.abs(point.x-e.getX()), Math.abs(point.y-e.getY()));
					
					repaint();
				}
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
				
				point	= null;
				range	= null;
				progressset	= null;
				calculating	= false;
				DisplayPanel.this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

				getParent().invalidate();
				getParent().doLayout();
				getParent().repaint();
			}
		});
	}
	
	/**
	 *  Display intermediate calculation results.
	 */
	public void addProgress(final ProgressData progress)
	{		
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				if(progressset==null)
					progressset	= new HashSet();
				
				progressset.remove(progress);
				progressset.add(progress);
				repaint();
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
		
		// Draw image.
		if(image!=null)
		{
			Rectangle bounds = getInnerBounds();
			int	ix	= 0;
			int iy	= 0;
			int	iwidth	= image.getWidth(this);
			int iheight	= image.getHeight(this);
			Rectangle drawarea = scaleToFit(bounds, iwidth, iheight);

			// Zoom into original image while calculating
			if(calculating && range!=null)
			{
				ix	= (range.x-drawarea.x-bounds.x)*iwidth/drawarea.width;
				iy	= (range.y-drawarea.y-bounds.y)*iheight/drawarea.height;
				iwidth	= range.width*iwidth/drawarea.width;
				iheight	= range.height*iheight/drawarea.height;
				
				// Scale again to fit new image size.
				drawarea = scaleToFit(bounds, iwidth, iheight);
				
				g.drawImage(image, bounds.x+drawarea.x, bounds.y+drawarea.y,
						bounds.x+drawarea.x+drawarea.width, bounds.y+drawarea.y+drawarea.height,
						ix, iy, ix+iwidth, iy+iheight, this);

				// Draw progress boxes.
				if(progressset!=null)
				{
					for(Iterator it=progressset.iterator(); it.hasNext(); )
					{
						ProgressData	progress	= (ProgressData)it.next();
						if(!progress.isFinished())
						{
							g.setColor(new Color(0,0,0,160));
							g.fillRect(bounds.x+drawarea.x+progress.getArea().x+1, bounds.y+drawarea.y+progress.getArea().y+1,
								progress.getArea().width-1, progress.getArea().height-1);							
						}
						g.setColor(Color.white);
						g.drawRect(bounds.x+drawarea.x+progress.getArea().x, bounds.y+drawarea.y+progress.getArea().y,
							progress.getArea().width, progress.getArea().height);
						
						// Print provider name.
						if(progress.getProviderId()!=null)
						{
							String	name	= progress.getProviderId().toString();
							FontMetrics	fm	= g.getFontMetrics();
							Rectangle2D	sb	= fm.getStringBounds(name, g);
							if(sb.getWidth()<progress.getArea().getWidth() && sb.getHeight()<progress.getArea().getHeight())
							{
								int	x	= bounds.x+drawarea.x+progress.getArea().x+2 + (progress.getArea().width-(int)sb.getWidth())/2;
								int	y	= bounds.y+drawarea.y+progress.getArea().y+2+(int)sb.getHeight()  + (progress.getArea().height-(int)sb.getHeight())/2;
								g.drawString(name, x, y);
							}
						}
					}
				}
			}
			else
			{
				g.drawImage(image, bounds.x+drawarea.x, bounds.y+drawarea.y,
					bounds.x+drawarea.x+drawarea.width, bounds.y+drawarea.y+drawarea.height,
					ix, iy, ix+iwidth, iy+iheight, this);
			}
		}
		
		// Draw range area.
		if(!calculating && range!=null)
		{
			Rectangle bounds = getInnerBounds();
			double	rratio	= (double)range.width/range.height;
			double	bratio	= (double)bounds.width/bounds.height;
			
			// Draw left and right boxes to show unused space
			if(rratio<bratio)
			{
				int	drawwidth	= range.height*bounds.width/bounds.height;
				int offset = (range.width-drawwidth)/2;
				g.setColor(new Color(128,128,128,64));
				g.fillRect(range.x+offset, range.y, -offset, range.height+1);
				g.fillRect(range.x+range.width, range.y, -offset, range.height+1);
			}
			// Draw upper and lower boxes to show unused space
			else if(rratio>bratio)
			{
				int	drawheight	= range.width*bounds.height/bounds.width;
				int offset = (range.height-drawheight)/2;
				g.setColor(new Color(128,128,128,64));
				g.fillRect(range.x, range.y+offset, range.width+1, -offset);
				g.fillRect(range.x, range.y+range.height, range.width+1, -offset);
			}
		
			g.setColor(Color.white);
			g.drawRect(range.x, range.y, range.width, range.height);
		}
	}
	
	/**
	 *  Calculate draw area for image.
	 */
	protected Rectangle scaleToFit(Rectangle bounds, int iwidth, int iheight)
	{
		double	iratio	= (double)iwidth/iheight;
		double	bratio	= (double)bounds.width/bounds.height;
		Rectangle	drawarea	= new Rectangle(0, 0, bounds.width, bounds.height);
		
		// Scale to fit height
		if(iratio<bratio)
		{
			 double	hratio	= (double)bounds.height/iheight;
			 drawarea.width	= (int)(iwidth*hratio);
			 drawarea.x	= (bounds.width-drawarea.width)/2;
		}
		// Scale to fit width
		else if(iratio>bratio)
		{
			 double	wratio	= (double)bounds.width/iwidth;
			 drawarea.height	= (int)(iheight*wratio);
			 drawarea.y	= (bounds.height-drawarea.height)/2;
		}
		return drawarea;
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
	public Dimension getMinimumSize()
	{
		Insets	ins	= getInsets();
		Dimension	ret	= new Dimension(ins!=null ? ins.left+ins.right : 0, ins!=null ? ins.top+ins.bottom : 0);
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
	public Dimension getPreferredSize()
	{
		return getMinimumSize();
	}
}
