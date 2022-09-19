import {LitElement, html, css} from './libs/lit-3.2.0/lit-element.js';

export class MandelbrotElement extends LitElement 
{
	constructor() 
	{
		super();
		console.log("init of mandelbrot elem");
	}
	
	connectedCallback() 
	{
		super.connectedCallback();
		console.log('connected');
		
		var self = this;
		var data = null;
		var colors = null; // the color scheme
		
		this.setColorScheme([this.createColor[50, 100, 0], this.createColor[255, 0, 0]], true);
						
		var terminate = jadex.getIntermediate('/mandelbrotwebapi/subscribeToDisplayUpdates?args_0=webdisplay&returntype=jadex.commons.future.ISubscriptionIntermediateFuture',
		function(response)
		{
			console.log("recceived display update: "+response.data);
			var data = response.data;
			//System.out.println("rec: "+result.getClass());
			/*if(result instanceof AreaData)
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
			}*/		
		},
		function(err)
		{
			console.log("display subscribe err: "+err);
		});
		
		
		/*axios.get('/mandelbrotwebapi/subscribeToDisplayUpdates?args_0=webdisplay&returntype=jadex.commons.future.ISubscriptionIntermediateFuture', this.transform)
		.then(function(resp)
		{
			console.log("recceived display update: "+resp.data);
		})
		.catch(ex => console.log(ex))*/
	
		/*axios.get(this.getMethodPrefix()+'&methodname=subscribeToDisplayUpdates&args_0=webdisplay&returntype=jadex.commons.future.ISubscriptionIntermediateFuture', this.transform)
		.then(function(resp)
		{
			console.log("recceived display update: "+resp.data);
		})
		.catch(ex => console.log(ex))*/
	}
	
	disconnectedCallback()
	{
		super.disconnectedCallback();
		console.log('disconnected')
	}
	
	getMethodPrefix() 
	{
		//return '/webjcc/invokeServiceMethod?cid=null'+'&servicetype='+'jadex.micro.examples.mandelbrot_new.IDisplayService'; cid=null leads to CID(null) :-()
		return '/webjcc/invokeServiceMethod?servicetype='+'jadex.micro.examples.mandelbrot_new.IDisplayService';
	}

	render() 
	{
		return html`<h1>Mandelbrot</h1>
		<canvas id="canvas"></canvas>`;
	}
	
	setResults(data)
	{
		if(data.fetchData()==null)
			data.setData(new short[data.getSizeX()][data.getSizeY()]);
		this.data = data;
					
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
	public void addProgress(part)
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
	}
	
	/**
	 *  Set new results.
	 */
	public void	addDataChunk(final PartDataChunk data)
	{
		// first chunk is empty and only delivers name of worker
		if(data.getData()==null)
			return; 
		
		short[] chunk = data.getData();
		short[][] results;
		results = DisplayPanel.this.data.fetchData();
		
		int xi = ((int)data.getArea().getX())+data.getXStart();
		int yi = ((int)data.getArea().getY())+data.getYStart();
		int xmax = (int)(data.getArea().getX()+data.getArea().getWidth());
				
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
	
	paint()
	{
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
	scaleToFit(bwidth, bheight, iwidth, iheight)
	{
		var iratio = iwidth/iheight;
		var bratio = bwidth/bheight;
		
		var drawstartx = 0;
		var drawstarty = 0;
		var drawendx = bwidth;
		var drawendy = bheight;
		
		// Scale to fit height
		if(iratio<=bratio)
		{
			 var hratio	= bheight/iheight;
			 drawendy = iwidth*hratio;
			 drawstartx	= (bwidth-drawendx)/2;
		}
		// Scale to fit width
		else if(iratio>bratio)
		{
			 var wratio = bwidth/iwidth;
			 drawendy = iheight*wratio;
			 drawstarty	= (bheight-drawendy)/2;
		}
		return [Math.trunc(drawstartx), Math.truc(drawendx), Math.trunc(drawstarty), Math.trunc(drawendy)];
	}
	
	/**
	 *  Get the desired size of the panel.
	 * /
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
	}*/

	createColor(r, g, b, a)
	{
		const color = a << 24 | r << 16 | g << 8 | b; 
		return color;
	}
	
	getAlpha(color)
	{
		return color >>> 24;
	}
	
	getRed(color)
	{
		return color >>> 16 & 0xff;
	}

	getGreen(color)
	{
		return color >>> 8 & 0xff;
	}

	getBlue(color)
	{
		return color & 0xff;
	}

	createColorBetween(start, end, i)
	{
		return this.createColor(
			this.getRed(start)+(i%16)/16.0*(this.getRed(end)-this.getRed(start)),
			this.getGreen(start)+(i%16)/16.0*(this.getGreen(end)-this.getGreen(start)),
			this.getBlue(start)+(i%16)/16.0*(this.getBlue(end)-this.getBlue(start));
	}

	setColorScheme(scheme, cycle)
	{
		if(scheme==null || scheme.length==0)
		{
			this.colors	= [this.createColor[0xFF, 0xFF, 0xFF]];
		}
		else if(scheme.length==1)
		{
			this.colors	= scheme;
		}
		else if(cycle)
		{
			this.colors	= [scheme.length*16];
			for(var i=0; i<colors.length; i++)
			{
				var index = i/16;
				var start = scheme[index];
				var end	= index+1<scheme.length? scheme[index+1] : scheme[0];
				this.colors[i] = this.createColorBetween(start, end, i);
			}
		}
		else
		{
			//var max = data!=null ? data.getMax() : GenerateService.ALGORITHMS[0].getDefaultSettings().getMax();
			var max = 256; // todo
			this.colors = new Color[max];
			for(var i=0; i<colors.length; i++)
			{
				var index = i*(scheme.length-1)/max;
				var diff = i*(scheme.length-1)/max - index;
				var start = scheme[index];
				var end	= scheme[index+1];
				this.colors[i] = this.createColorBetween(start, end, i);
			}
		}
	}
}

if(customElements.get('jadex-mandelbrot') === undefined)
	customElements.define('jadex-mandelbrot', MandelbrotElement);

	
	/**
	 *  Subscribe for updates when display service is available.
	 *  @param ds The display service.
	 * /
	// Annotation only possible on agent
	//@OnService(requiredservice = @RequiredService(min = 1, max = 1))
	public void	displayServiceAvailable(IDisplayService ds)
	{
		ISubscriptionIntermediateFuture<Object> sub = ds.subscribeToDisplayUpdates(displayid);
		sub.addResultListener(new IntermediateEmptyResultListener<Object>()
		{
			public void resultAvailable(Collection<Object> result)
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
	}*/