package jadex.micro.examples.mandelbrot_new;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Rectangle2D;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

import javax.swing.JComponent;
import javax.swing.JProgressBar;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import jadex.bridge.IExternalAccess;
import jadex.bridge.service.search.ServiceQuery;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.IntermediateEmptyResultListener;
import jadex.commons.gui.future.SwingDefaultResultListener;

/**
 *  Panel for displaying calculated results.
 */
public class DisplayPanel extends JComponent
{
	//-------- constants --------
	
	/** The help text. */
	public static final String	HELPTEXT	=
		"Use mouse to navigate:\n" +
		"[wheel] zoom in/out\n" +
		"[left button] choose and click into area\n" +
		"[rigth button] drag to move, click for original area.\n";
	
	//-------- attributes --------
	
	/** The service provider. */
	protected IExternalAccess agent;
	
	/** The colors for drawing. */
	protected Color[] colors;
	
	/** The latest area data used for determining original coordinates of painted regions. */
	protected AreaData	data;
	
	/** The current image derived from the results. */
	protected Image	image;
	
	/** The current selection start point (if any). */
	protected Point	point;
	
	/** The current selection range (if any). */
	protected Rectangle	range;
	
	/** Flag indicating that a calculation is in progress. */
	protected boolean calculating;
	
	/** Progress data objects, available only when calculating (progress data -> percent finished). */
	protected Set<ProgressData> progressdata;
	
	/** Start point for dragging (if any). */
	protected Point	startdrag;
	
	/** End point for dragging (if any). */
	protected Point	enddrag;
	
	/** The display id. */
	protected String displayid;
	
	/** The generate service. */
	protected IGenerateService genservice;
	
	protected boolean dirty;

	//-------- constructors --------
	
	/**
	 *  Create a new display panel.
	 */
	public DisplayPanel(final IExternalAccess agent)
	{
		this.agent	= agent;
		this.displayid = ""+UUID.randomUUID();
		
		boolean[] init = new boolean[2];
		agent.addQuery(new ServiceQuery<IDisplayService>(IDisplayService.class)).next(ds ->
		{
			init[0] = true;
			displayServiceAvailable(ds);
			//if(init[0] && init[1])
			//	calcDefaultImage();
		});
		
		agent.addQuery(new ServiceQuery<IGenerateService>(IGenerateService.class)).next(gs ->
		{
			init[1] = true;
			this.genservice = gs;
			//if(init[0] && init[1])
			//	calcDefaultImage();
		});
		
		Timer timer = new Timer(10, a ->
		{
			if(dirty)
			{
				getParent().invalidate();
				getParent().doLayout();
				getParent().repaint();
			}
			dirty = false;
		});
		timer.start(); 
	}
	
	//-------- methods --------
	
	/**
	 *  Subscribe for updates when display service is available.
	 *  @param ds The display service.
	 */
	// Annotation only possible on agent
	//@OnService(requiredservice = @RequiredService(min = 1, max = 1))
	public void	displayServiceAvailable(IDisplayService ds)
	{
		ISubscriptionIntermediateFuture<Object> sub = ds.subscribeToDisplayUpdates(displayid);
		sub.addResultListener(new IntermediateEmptyResultListener<Object>()
		{
			public void resultAvailable(Collection<Object> result)
			{
			}
			
			public void intermediateResultAvailable(Object result)
			{
				//System.out.println("rec: "+result.getClass());
				if(result instanceof AreaData)
				{
					setResults((AreaData)result);
				}
				else if(result instanceof ProgressData)
				{
					addProgress(((ProgressData)result));
				}
				else if(result instanceof PartDataChunk)
				{
					PartDataChunk chunk = (PartDataChunk)result;
					//System.out.println("received: "+result);
					addProgress(new ProgressData(chunk.getWorker(), null, chunk.getArea(), chunk.getProgress(), chunk.getImageWidth(), chunk.getImageHeight(), chunk.getDisplayId()));
					addDataChunk((PartDataChunk)result);
				}
			}
			
			public void finished()
			{
				// todo: close
			}
			
			public void exceptionOccurred(Exception exception)
			{
				exception.printStackTrace();
			}
		});
		
		setColorScheme(new Color[]{new Color(50, 100, 0), Color.red}, true);
		
		// Dragging with right mouse button.
		MouseAdapter draghandler = new MouseAdapter()
		{
			public void mousePressed(MouseEvent e)
			{
				if(!calculating && e.getButton()==MouseEvent.BUTTON3 && e.getClickCount()==1 && image!=null)
				{
					startdrag = new Point(e.getX(), e.getY());
					range	= null;
					point	= null;
				}
				else
				{
					startdrag	= null;
				}
			}
			
			public void mouseDragged(MouseEvent e)
			{
				if(startdrag!=null)
				{
					enddrag = new Point(e.getX(), e.getY());
					repaint();
				}
			}
			
			public void mouseReleased(MouseEvent e)
			{
				if(startdrag!=null && enddrag!=null)
				{
//							System.out.println("dragged: "+startdrag+" "+enddrag);
					dragImage();
				}
			}
		};
		addMouseMotionListener(draghandler);
		addMouseListener(draghandler);
				
		// Zooming with mouse wheel.
		addMouseWheelListener(new MouseAdapter()
		{
			public void mouseWheelMoved(MouseWheelEvent e)
			{
				if(!calculating)
				{
					int sa = e.getScrollAmount();
					double	dir = Math.signum(e.getWheelRotation());
					double	percent	= 10*sa;
					double	factor;
					if(dir>0)
					{
						factor	= (100+percent)/100;
					}
					else
					{
						factor	= 100/(100+percent);
					}
					zoomImage(e.getX(), e.getY(), factor);
				}
			}
		});
		
		// Selecting range and default area.
		addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent e)
			{
				if(SwingUtilities.isRightMouseButton(e))
				{
					calcDefaultImage();
				}
				
				else if(!calculating && range!=null)
				{
					if(e.getX()>=range.x && e.getX()<=range.x+range.width
						&& e.getY()>=range.y && e.getY()<=range.y+range.height)
					{
						zoomIntoRange();
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
		
		// ESC stops dragging / range selection
		setFocusable(true);
		addKeyListener(new KeyAdapter()
		{
			public void keyPressed(KeyEvent e)
			{
				if(e.getKeyCode()==KeyEvent.VK_ESCAPE)
				{
					range	= null;
					point	= null;
					startdrag	= null;
					enddrag	= null;
					DisplayPanel.this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));						
					repaint();
				}
			}
		});
	}
	
	/**
	 *  Set new results.
	 */
	public void	setResults(final AreaData data)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				//short[][] results = data.fetchData();
				if(data.fetchData()==null)
					data.setData(new short[data.getSizeX()][data.getSizeY()]);
				DisplayPanel.this.data = data;
					
				dirty = true;
				
				/*DisplayPanel.this.image	= createImage(results.length, results[0].length);
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
							c	= colors[results[x][y]%colors.length];
						}
						g.setColor(c);
//						g.drawLine(x, results[x].length-y-1, x, results[x].length-y-1);	// Todo: use euclidean coordinates
						g.drawLine(x, y, x, y);
					}
				}
				
				point	= null;
				range	= null;
				progressdata = null;
				/*if(progressupdate!=null)
				{
					progressupdate.stop();
					progressupdate	= null;
				}*/
				calculating	= false;
				DisplayPanel.this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

				//getParent().invalidate();
				//getParent().doLayout();
				//getParent().repaint();
			}
		});
	}
	
	/**
	 *  Display intermediate calculation results.
	 */
	public void addProgress(final ProgressData part)
	{		
		Runnable r = new Runnable()
		{
			public void run() 
			{
				if(progressdata==null)
					progressdata = new HashSet<ProgressData>();
				
				progressdata.remove(part);
				if(!part.isFinished())
					progressdata.add(part);
				
				if(progressdata.size()==1)
					range = null;
				
				if(progressdata.size()==0)
					calculating = false;
				
				dirty = true;
				
				//System.out.println("progressdata: "+progressdata.size());
			}
		};
		
		if(SwingUtilities.isEventDispatchThread())
		{
			r.run();
		}
		else
		{
			SwingUtilities.invokeLater(new Runnable()
			{
				public void run()
				{
					r.run();
				}
			});
		}
	}
	
	/**
	 *  Set new results.
	 */
	public void	addDataChunk(final PartDataChunk data)
	{
		// first chunk is empty and only delivers name of worker
		if(data.getData()==null)
			return; 
		
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				short[] chunk = data.getData();
				short[][] results;
				results = DisplayPanel.this.data.fetchData();
				
				int xi = ((int)data.getArea().getX())+data.getXStart();
				int yi = ((int)data.getArea().getY())+data.getYStart();
				int xmax = (int)(data.getArea().getX()+data.getArea().getWidth());
				
				//System.out.println("received: "+xi+" "+yi);
				
				//for(int i=0; i<chunk.length; i++)
				//	System.out.print(chunk[i]+"-");
				
				/*if(yi==99)
					for(int y=0; y<results.length; y++)
					{
						for(int x=0; x<results[y].length; x++)
						{
							System.out.print(results[x][y]+"-");
						}
						System.out.println();
					}
				*/
				
				try
				{
					int cnt = 0;
					while(cnt<chunk.length)
					{
						results[xi][yi] = chunk[cnt++];
						if(++xi>=xmax)
						{
							xi=((int)data.getArea().getX());
							yi++;
						}
					}
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
				
				/*if(DisplayPanel.this.image==null)
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
							c	= colors[results[x][y]%colors.length];
						}
						g.setColor(c);
//						g.drawLine(x, results[x].length-y-1, x, results[x].length-y-1);	// Todo: use euclidean coordinates
						g.drawLine(x, y, x, y);
					}
				}*/
				
				dirty = true;
				
				//System.out.println("display received: "+data);
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
		
		if(data!=null)
		{
			short[][] results = data.fetchData();
			
			if(results==null)
				return;
			
			//if(image==null || image.getWidth(this)!=results[0].length || image.getHeight(this)!=results.length)
			//image = createImage(data.getSizeX(), data.getSizeY());
			image = createImage(results.length, results[0].length);
			
			Graphics go = image.getGraphics();
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
						c	= colors[results[x][y]%colors.length];
					}
					go.setColor(c);
	//				g.drawLine(x, results[x].length-y-1, x, results[x].length-y-1);	// Todo: use euclidean coordinates
					go.drawLine(x, y, x, y);
				}
			}
		}
		
		// Draw image.
		if(image!=null)
		{
			Rectangle bounds = getInnerBounds(true);
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
			}
			
			// Offset and clip image and show border while dragging.
			else if(startdrag!=null && enddrag!=null)
			{
				// Draw original image in background
				g.drawImage(image, bounds.x+drawarea.x, bounds.y+drawarea.y,
					bounds.x+drawarea.x+drawarea.width, bounds.y+drawarea.y+drawarea.height,
					ix, iy, ix+iwidth, iy+iheight, this);
				g.setColor(new Color(32,32,32,160));
				g.fillRect(bounds.x+drawarea.x, bounds.y+drawarea.y, drawarea.width, drawarea.height);

				// Draw offsetted image in foreground
				Shape	clip	= g.getClip();
				g.setClip(bounds.x+drawarea.x, bounds.y+drawarea.y, drawarea.width, drawarea.height);
				int	xoff	= enddrag.x-startdrag.x;
				int	yoff	= enddrag.y-startdrag.y;
				
				g.drawImage(image, bounds.x+drawarea.x+xoff, bounds.y+drawarea.y+yoff,
					bounds.x+drawarea.x+xoff+drawarea.width, bounds.y+drawarea.y+yoff+drawarea.height,
					ix, iy, ix+iwidth, iy+iheight, this);
				g.setClip(clip);
			}
			else
			{
				g.drawImage(image, bounds.x+drawarea.x, bounds.y+drawarea.y,
					bounds.x+drawarea.x+drawarea.width, bounds.y+drawarea.y+drawarea.height,
					ix, iy, ix+iwidth, iy+iheight, this);
			}
			
			// Draw progress boxes.
			if(progressdata!=null)
			{
				JProgressBar bar = new JProgressBar(0, 100);
				bar.setStringPainted(true);
				Dimension barsize	= bar.getPreferredSize();
				for(Iterator<ProgressData> it=progressdata.iterator(); it.hasNext(); )
				{
					ProgressData progress = (ProgressData)it.next();
					//System.out.println("progress is: "+progress);
					
					double xf = drawarea.getWidth()/progress.getImageWidth();
					double yf = drawarea.getHeight()/progress.getImageHeight();
					int corx = (int)(progress.getArea().x*xf);
					int cory = (int)(progress.getArea().y*yf);
					int corw = (int)(progress.getArea().width*xf);
					int corh = (int)(progress.getArea().height*yf);
					
					if(!progress.isFinished())
					{
						g.setColor(new Color(32,32,32,160));
						g.fillRect(bounds.x+drawarea.x+corx+1, bounds.y+drawarea.y+cory+1, corw-1, corh-1);							
					}
					g.setColor(Color.white);
					g.drawRect(bounds.x+drawarea.x+corx, bounds.y+drawarea.y+cory, corw, corh);
					
					// Print provider name.
					String name = progress.getProviderId()!=null? progress.getProviderId().toString(): "";
					String provider	= "";
					int index =	name.indexOf('@');
					if(index!=-1)
					{
						provider = name.substring(index+1);
						name = name.substring(0, index);
					}
//					provider = progress.getTaskId().toString();
					
					int width;
					int	height;
					
					while(true)
					{
						FontMetrics fm = g.getFontMetrics();
						Rectangle2D	sb1	= fm.getStringBounds(name, g);
						Rectangle2D	sb2	= fm.getStringBounds(provider, g);
						width = (int)Math.max(sb1.getWidth(), sb2.getWidth());
						height = fm.getHeight()*2 + barsize.height + 2;
						Font f = g.getFont();
						
						if(width<corw-4 && height<corh-4 || f.getSize()<6)		
							break;

						Font nf = f.deriveFont(f.getSize()*0.9f);
						g.setFont(nf);
					}
					
					if(width<corw-4 && height<corh-4)
					{
						//System.out.println("a: "+width+" "+height+" "+corw+" "+corh+" "+g.getFont().getSize());
						// Draw provider id.
						FontMetrics fm = g.getFontMetrics();
						int	x = bounds.x+drawarea.x+corx + (corw-width)/2;
						int	y = bounds.y+drawarea.y+cory + (corh-height)/2 + fm.getLeading()/2;
						g.drawString(name, x, y + fm.getAscent());
						g.drawString(provider, x, y + fm.getAscent() + fm.getHeight());
					
						// Draw progress bar.
						if(!progress.isFinished())
						{
							bar.setStringPainted(true);
							bar.setValue(progress.getProgress());
							width = Math.min(corw-10, barsize.width);
							x = bounds.x+drawarea.x+corx + (corw-width)/2;
							y = y + fm.getHeight()*2 + 2;
							bar.setBounds(0, 0, width, barsize.height);
							Graphics	g2	= g.create();
							g2.translate(x, y);
							bar.paint(g2);
						}
					}
					else if(!progress.isFinished() && corw>8 && corh>8)
					{
						//System.out.println("b: "+width+" "+height+" "+corw+" "+corh);

						bar.setStringPainted(false);
						int	x = bounds.x+drawarea.x+corx + 2;
						int	y = bounds.y+drawarea.y+cory + Math.max((corh-barsize.height)/2, 2);
						bar.setValue(progress.getProgress());
						bar.setBounds(0, 0, corw-4, Math.min(barsize.height, corh-4));
						Graphics g2 = g.create();
						g2.translate(x, y);
						bar.paint(g2);
					}
				}
			}
		}
		
		// Draw range area.
		if(!calculating && range!=null)
		{
			Rectangle bounds = getInnerBounds(false);
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
	 *  @param scrollarea	True when inner bounds of scroll area instead of visible window space should be considered.
	 */
	protected Rectangle getInnerBounds(boolean scrollarea)
	{
		Rectangle	bounds	= getBounds();

		if(!scrollarea && getParent() instanceof JViewport)
		{
			// Get bounds of outer scroll panel
			Rectangle	pbounds	= getParent().getParent().getBounds();
			if(bounds.width>pbounds.width || bounds.height>pbounds.height)
			{
				bounds	= pbounds;
			}
		}
		
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

	/**
	 *  Set the color scheme.
	 */
	public void	setColorScheme(Color[] scheme, boolean cycle)
	{
		if(scheme==null || scheme.length==0)
		{
			colors	= new Color[]{Color.white};
		}
		else if(scheme.length==1)
		{
			colors	= scheme;
		}
		else if(cycle)
		{
			colors	= new Color[scheme.length*16];
			for(int i=0; i<colors.length; i++)
			{
				int index	= i/16;
				Color	start	= scheme[index];
				Color	end	= index+1<scheme.length ? scheme[index+1] : scheme[0];
				colors[i]	= new Color(
					(int)(start.getRed()+(double)(i%16)/16*(end.getRed()-start.getRed())),
					(int)(start.getGreen()+(double)(i%16)/16*(end.getGreen()-start.getGreen())),
					(int)(start.getBlue()+(double)(i%16)/16*(end.getBlue()-start.getBlue())));
			}
		}
		else
		{
			short	max	= data!=null ? data.getMax() : GenerateService.ALGORITHMS[0].getDefaultSettings().getMax();
			colors	= new Color[max];
			for(int i=0; i<colors.length; i++)
			{
				int index	= i*(scheme.length-1)/max;
				double	diff	= (double)i*(scheme.length-1)/max - index;
				Color	start	= scheme[index];
				Color	end	= scheme[index+1];
				colors[i]	= new Color(
					(int)(start.getRed()+(double)diff*(end.getRed()-start.getRed())),
					(int)(start.getGreen()+(double)diff*(end.getGreen()-start.getGreen())),
					(int)(start.getBlue()+(double)diff*(end.getBlue()-start.getBlue())));
			}
		}
		
		if(data!=null)
		{
			setResults(data);
		}
	}
	
	//-------- recalculation methods --------
	
	/**
	 *  Drag the image according to current drag settings.
	 */
	protected void dragImage()
	{
		Rectangle	bounds	= getInnerBounds(true);
		Rectangle	drawarea	= scaleToFit(bounds, image.getWidth(DisplayPanel.this), image.getHeight(DisplayPanel.this));
		int xdiff = startdrag.x-enddrag.x;
		int ydiff = startdrag.y-enddrag.y;
		double xp = ((double)xdiff)/drawarea.width;
		double yp = ((double)ydiff)/drawarea.height;
		
		double xm = (data.getXEnd()-data.getXStart())*xp;
		double ym = (data.getYEnd()-data.getYStart())*yp;
		final double xs = data.getXStart()+xm;
		final double xe = data.getXEnd()+xm;
		final double ys = data.getYStart()+ym;
		final double ye = data.getYEnd()+ym;
		
		startdrag = null;
		enddrag = null;
		range	= new Rectangle(bounds.x+drawarea.x+xdiff, bounds.y+drawarea.y+ydiff, drawarea.width, drawarea.height);

		calcArea(xs, xe, ys, ye, data.getSizeX(), data.getSizeY());
	}

	/**
	 *  Zoom into the given location by the given factor.
	 */
	protected void zoomImage(int x, int y, double factor)
	{
		final Rectangle	bounds	= getInnerBounds(true);
		
		int	iwidth	= image.getWidth(DisplayPanel.this);
		int iheight	= image.getHeight(DisplayPanel.this);
		final Rectangle drawarea = scaleToFit(bounds, iwidth, iheight);
		int mx = Math.min(bounds.x+drawarea.x+drawarea.width, Math.max(bounds.x+drawarea.x, x));
		int my = Math.min(bounds.y+drawarea.y+drawarea.height, Math.max(bounds.y+drawarea.y, y));
		double xrel = ((double)mx-(bounds.x+drawarea.x))/drawarea.width;
		double yrel = ((double)my-(bounds.y+drawarea.y))/drawarea.height;

		double wold = data.getXEnd()-data.getXStart();
		double hold = data.getYEnd()-data.getYStart();
		double wnew = wold*factor;
		double hnew = hold*factor;
		double wd = wold-wnew;
		double hd = hold-hnew;
		
		final double xs = data.getXStart()+wd*xrel;
		final double xe = xs+wnew;
		final double ys = data.getYStart()+hd*yrel;
		final double ye = ys+hnew;
		
		// Set range for drawing preview of zoom area.
		double	xdiff	= drawarea.width - drawarea.width*factor;
		double	ydiff	= drawarea.height - drawarea.height*factor;
		range	= new Rectangle(bounds.x+drawarea.x+(int)Math.round(xdiff*xrel), bounds.y+drawarea.y+(int)Math.round(ydiff*yrel),
			(int)Math.round(drawarea.width*factor), (int)Math.round(drawarea.height*factor));
		
//		zoomIntoRange();
		calcArea(xs, xe, ys, ye, data.getSizeX(), data.getSizeY());
	}

	/**
	 *  Set display coordinates to default values.
	 */
	protected void calcDefaultImage()
	{
		AreaData settings;
		if(data!=null)
		{
			settings = data.getAlgorithm().getDefaultSettings();
		}
		else
		{
			settings = GenerateService.ALGORITHMS[0].getDefaultSettings();
		}
		
		final Rectangle	bounds	= getInnerBounds(false);
		double	rratio	= (double)settings.getSizeX()/settings.getSizeY();
		double	bratio	= (double)bounds.width/bounds.height;
		
		// Calculate pixel width/height of area.
		if(bounds.width==0 || bounds.height==0)
		{
			bounds.width	= settings.getSizeX();
			bounds.height	= settings.getSizeY();
		}
		else if(rratio<bratio)
		{
			int	width	= (int)(bounds.height*rratio);
			bounds.width	= width;
		}
		else if(rratio>bratio)
		{
			int	height	= (int)(bounds.width/rratio);
			bounds.height	= height;
		}
		
		// Clear image for painting only background.
		image = createImage(bounds.width, bounds.height);
		
		calcArea(settings.getXStart(), settings.getXEnd(), settings.getYStart(), settings.getYEnd(), bounds.width, bounds.height);
	}
	
	/**
	 *  Zoom into the selected range.
	 */
	protected void zoomIntoRange()
	{
		// Calculate bounds relative to original image.
		Rectangle	bounds	= getInnerBounds(true);
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
		
		// Calculate pixel width/height of visible area.
		bounds	= getInnerBounds(false);
		double	rratio	= (double)range.width/range.height;
		double	bratio	= (double)bounds.width/bounds.height;
		if(rratio<bratio)
		{
			bounds.width	= (int)(bounds.height*rratio);
		}
		else if(rratio>bratio)
		{
			bounds.height	= (int)(bounds.width/rratio);
		}
		final Rectangle	area	= bounds;

		calcArea(ox+owidth*x, ox+owidth*x2, oy+oheight*y, oy+oheight*y2, area.width, area.height);
	}

	/**
	 *  Calculate the given area.
	 */
	protected void calcArea(double x1, double x2, double y1, double y2, int sizex, int sizey)
	{
		AreaData	settings;
		if(data==null)
			settings = GenerateService.ALGORITHMS[0].getDefaultSettings();
		else
			settings = data;
		
		final AreaData ad = new AreaData(x1, x2, y1, y2, sizex, sizey,
			settings.getMax(), settings.getTaskSize(), settings.getAlgorithm(), displayid, settings.getChunkCount());
			//settings.getMax(), settings.getParallel(), settings.getTaskSize(), settings.getAlgorithm(), displayid);
		
		DisplayPanel.this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		calculating	= true;
		repaint();
		
		if(genservice!=null)
		{
			genservice.generateArea(ad).addResultListener(new SwingDefaultResultListener<Void>()
			{
				public void customResultAvailable(Void result)
				{
					// already done with partials
					//DisplayPanel.this.setResults(result);
				}
				
				public void customExceptionOccurred(Exception exception)
				{
					calculating	= false;
					DisplayPanel.this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					super.customExceptionOccurred(exception);
				}
			});
		}
		else
		{
			System.out.println("No generate service found");
		}
	}
	
}
