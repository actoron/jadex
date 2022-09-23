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
		this.colors = null; // the color scheme
		this.data = null; // the data to draw
		this.progressdata = null; // the progress infos
		
		this.setColorScheme([this.createColor(50, 100, 0), this.createColor(255, 0, 0)], true);
						
		var terminate = jadex.getIntermediate('/mandelbrotwebapi/subscribeToDisplayUpdates?args_0=webdisplay&returntype=jadex.commons.future.ISubscriptionIntermediateFuture',
		function(response)
		{
			console.log("recceived display update: "+response.data);
			var data = response.data;
			//System.out.println("rec: "+result.getClass());
			
			if(data.data!=null) // result instanceof PartDataChunk
			{
				self.addDataChunk(data);
			}
			
			if(data.algorithm!=null) // result instanceof AreaData
			{
				self.setResults(data);
			}
			
			if(data.progress!=null) // result instanceof ProgressData
			{
				let prog = {};
				prog.name = data.worker.name;
				prog.area = data.area;
				prog.progress = data.progress;
				prog.imageWidth = data.imageWidth;
				prog.imageHeight = data.imageHeight; 
				prog.finished = data.progress==100;
				self.addProgress(prog);
			}
			
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
		if(data.data==null)
			data.data = this.makeArray(data.sizeX, data.sizeY);
		this.data = data;
		this.data.image = new Uint8ClampedArray(data.sizeX*data.sizeY*4);
	}
	
	/**
	 *  Display intermediate calculation results.
	 */
	addProgress(part)
	{		
		if(this.progressdata==null)
			this.progressdata = {};//new HashSet<ProgressData>();
				
		let key = this.getProgressKey(part);
		
		delete this.progressdata[key];	
		
		let len = Object.keys(this.progressdata).length;

		if(!part.finished)
			this.progressdata[key] = part;
				
		if(len==1)
			this.range = null;
				
		if(len==0)
			this.calculating = false;
			
		console.log("progressdata len (before, after): "+len+" "+Object.keys(this.progressdata).length+" "+part.finished+" "+part.progress);
		
		this.requestUpdate();
	}
	
	getProgressKey(part)
	{
		let area = part.area;
		let key = 'x='+area.x+"y="+area.y+"w="+area.w+"h="+area.h;
		return key;
	}
	
	addDataChunk(data) //PartDataChunk 
	{
		// first chunk is empty and only delivers name of worker
		if(data.data==null)
			return; 
		
		var chunk = data.data;
		var results = this.data.data;
		var image = this.data.image;
		
		var xi = data.area.x+data.xStart;
		var yi = data.area.y+data.yStart;
		var xmax = data.area.x+data.area.w;
				
		//console.log("chunk: "+xi+" "+yi+" "+xmax);
				
		var cnt = 0;
		while(cnt<chunk.length)
		{
			results[xi][yi] = chunk[cnt++];
			this.drawPixel(image, this.data.sizeX, xi, yi, this.getColor(results[xi][yi]));
			if(++xi>=xmax)
			{
				xi=data.area.x;
				yi++;
			}
		}		
		
		this.requestUpdate();
	}
	
	makeArray(d1, d2) 
	{
    	var arr = [];
    	for(let i = 0; i < d1; i++) 
    	{
        	arr.push(new Array(d2));
    	}
    	return arr;
	}
	
	getColor(value)
	{
		let ret;
		if(value==-1)
		{
			ret = this.createColor(0, 0, 0);
		}
		else
		{
			ret = this.colors[value%this.colors.length];
		}
		return ret
	}
	
	drawPixel(data, width, x, y, color) 
	{
		this.drawPixel2(data, width, x, y, this.getRed(color), this.getGreen(color), this.getBlue(color), this.getAlpha(color));
	}
	
	drawPixel2(data, width, x, y, r, g, b, a) 
	{
    	var index = x*4 + y*width*4;
	    data[index + 0] = r;
	    data[index + 1] = g;
	    data[index + 2] = b;
	    if(a)
	    	data[index + 3] = a;
	    else
	    	data[index + 3] = 255; // make NOT transparent 
	}
	
	drawProgressBar(x, y, w, h, color, percentage, complete, ctx)
	{
    	if(complete)
    	{
	    	ctx.beginPath();
	    	ctx.arc(h / 2 + x, h / 2 + y, h / 2, Math.PI / 2, 3 / 2 * Math.PI);
	    	ctx.lineTo(w - h + x, 0 + y);
	    	ctx.arc(w - h / 2 + x, h / 2 + y, h / 2, 3 / 2 *Math.PI, Math.PI / 2);
	    	ctx.lineTo(h / 2 + x, h + y);
	    	ctx.strokeStyle = '#000000';
	    	ctx.stroke();
	    	ctx.closePath();
	    }
    	
		let p = percentage * w;
    	if(p <= h)
    	{
      		ctx.beginPath();
      		ctx.arc(h / 2 + x, h / 2 + y, h / 2, Math.PI - Math.acos((h - p) / h), Math.PI + Math.acos((h - p) / h));
      		ctx.save();
      		ctx.scale(-1, 1);
      		ctx.arc((h / 2) - p - x, h / 2 + y, h / 2, Math.PI - Math.acos((h - p) / h), Math.PI + Math.acos((h - p) / h));
      		ctx.restore();
      		ctx.closePath();
    	} 
    	else 
    	{
      		ctx.beginPath();
      		ctx.arc(h / 2 + x, h / 2 + y, h / 2, Math.PI / 2, 3 / 2 *Math.PI);
      		ctx.lineTo(p - h + x, 0 + y);
      		ctx.arc(p - (h / 2) + x, h / 2 + y, h / 2, 3 / 2 * Math.PI, Math.PI / 2);
      		ctx.lineTo(h / 2 + x, h + y);
      		ctx.closePath();
    	}
    	ctx.fillStyle = color;
    	ctx.fill();
  	}
	
	paint()
	{
		if(this.data==null || this.data.data==null || this.data.image==null)
			return;
			
		let results = this.data.data;
		let oimage = this.data.image;
		var image = new Uint8ClampedArray(oimage); // clone the image data
		let canvas = this.shadowRoot.getElementById("canvas");
		let ctx = canvas.getContext("2d");
		//let cwidth = canvas.width;
		//let cheight = canvas.height;
		//let cdata = ctx.getImageData(0, 0, cwidth, cheight);

		let sx = 0;
		let sy = 0;
		let swidth = results.length;
		let sheight = results[0].length;
		let tx = 0;
		let ty = 0;
		let twidth = swidth;		
		let theight = sheight;
		
		ctx.canvas.width  = swidth;
		ctx.canvas.height = sheight;
  			
		//console.log("background color: "+canvas.style.background);
		
		//ctx.drawImage(this.data, sx, sy, swidth, sheight, tx, ty, twidth, theight);

		// generate image data from results, i.e. assign colors for values according to the palette

		// creates a typed array of 8-bit unsigned integers clamped to 0-255
		/*let image = new Uint8ClampedArray(swidth*sheight*4);
		
		for(let x=0; x<results.length; x++)
		{
			for(let y=0; y<results[x].length; y++)
			{
				var c;
				if(results[x][y]==-1)
				{
					c = this.createColor(0, 0, 0);
				}
				else
				{
					c = this.colors[results[x][y]%this.colors.length];
				}
				
				this.drawPixel(image, twidth, x, y, c);
			}
		}*/
		
		//ctx.putImageData(cdata, 0, 0);
		
		/*img 	Specifies the image, canvas, or video element to use 	 
		sx 	Optional. The x coordinate where to start clipping 	
		sy 	Optional. The y coordinate where to start clipping 	
		swidth 	Optional. The width of the clipped image 	
		sheight 	Optional. The height of the clipped image 	
		tx 	The x coordinate where to place the image on the canvas 	
		ty 	The y coordinate where to place the image on the canvas 	
		twidth 	Optional. The width of the image to use (stretch or reduce the image) 	
		theight 	Optional. The height of the image to use (stretch or reduce the image)*/
			
		var imgdata = new ImageData(image, swidth, sheight);
		ctx.putImageData(imgdata, 0, 0);
			
		/*
		createImageBitmap(imgdata).then(imgbitmap => 
		{
			ctx.drawImage(imgbitmap, sx, sy, swidth, sheight, tx, ty, twidth, theight);
		})
		.catch(err =>
		{
			console.log(err);
		});*/
		
		// Draw progress boxes.
		if(this.progressdata!=null && Object.keys(this.progressdata).length>0)
		{
			let canvas2 = document.createElement('canvas');
			let ctx2 = canvas2.getContext('2d');
			//canvas2.style.background = "transparent";
			ctx2.canvas.width  = twidth;
			ctx2.canvas.height = theight;
			//ctx2.clearRect(0, 0, twidth, theight);
			
			for(let key of Object.keys(this.progressdata)) 
			{
				let progress = this.progressdata[key];
				
				console.log("progress is: "+progress);
					
				let xf = twidth/progress.imageWidth;
				let yf = theight/progress.imageHeight;
				let corx = Math.trunc(progress.area.x*xf);
				let cory = Math.trunc(progress.area.y*yf);
				let corw = Math.trunc(progress.area.w*xf);
				let corh = Math.trunc(progress.area.h*yf);
					
				if(!progress.finished)
				{
					//ctx2.fillStyle = this.createColor(20, 20, 150, 160); //160
					ctx2.fillStyle = "rgba(20, 20, 150, 0.3)";
					ctx2.fillRect(corx+1, cory+1, corw-1, corh-1);
				}
				ctx2.strokeStyle = "yellow";
				ctx2.rect(corx, cory, corw, corh);
				
				// Print worker name
				let name = progress.name
				let provider = "";
				let index =	name.indexOf('@');
				if(index!=-1)
				{
					provider = name.substring(index+1);
					name = name.substring(0, index);
				}

				let textwidth;
				let textheight;
				let fsize = 20;				
				while(true)
				{
					ctx2.font = fsize+'px sans-serif';
					let m1 = ctx2.measureText(name);
					let m2 = ctx2.measureText(provider);
					textwidth = Math.max(m1.width, m2.width);
					//textheight = (m1.fontBoundingBoxAscent + m1.fontBoundingBoxDescent)*3; // + barsize.height + 2;
					textheight = Math.max(m1.actualBoundingBoxAscent + m1.actualBoundingBoxDescent, m2.actualBoundingBoxAscent + m2.actualBoundingBoxDescent);
					//textheight = Math.max(m1.fontBoundingBoxAscent + m1.fontBoundingBoxDescent, m2.fontBoundingBoxAscent + m2.fontBoundingBoxDescent);
					
					if(textwidth<corw-4 && textheight*3<corh-4 || fsize<5)		
						break;
					else
						fsize = Math.trunc(fsize*0.9);
				}
				
				if(textwidth<corw-4 && textheight*3<corh-4)
				{
					console.log("a: "+textwidth+" "+textheight+" "+corw+" "+corh+" "+fsize);
					// Draw provider id.
					let x = Math.trunc(corx + (corw-textwidth)/2);
					let y = Math.trunc(cory + (corh-textheight*3)/2);// + fm.getLeading()/2;
					ctx2.fillStyle = "rgb(255, 255, 255)";
					ctx2.fillText(name , x, y);
					ctx2.fillText(provider, x, y+textheight);
					//ctx.fillRect(x,y,textwidth,textheight*2);
					this.drawProgressBar(x, y+textheight*2, textwidth, textheight, 'red', progress.progress/100, true, ctx2);
				}
				else if(!progress.finished && corw>8 && corh>8)
				{
					console.log("b: "+textwidth+" "+textheight+" "+corw+" "+corh+" "+fsize);
					let x = corx + 2;
					let y = cory + Math.max((corh-textheight)/2, 2);
					this.drawProgressBar(x, y, textwidth, textheight, 'red', progress.progress/100, true, ctx2);
				}
			}
			
			ctx.drawImage(canvas2, 0, 0, twidth, theight);
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

	createColorBetween(start, end, diff)
	{
		let ret = this.createColor(
			this.getRed(start)+diff*(this.getRed(end)-this.getRed(start)),
			this.getGreen(start)+diff*(this.getGreen(end)-this.getGreen(start)),
			this.getBlue(start)+diff*(this.getBlue(end)-this.getBlue(start))
		);
		
		//console.log("start: "+start+" end: "+end+" diff: "+diff+" created: "+ret);
		
		return ret;
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
			this.colors	= new Array(scheme.length*16);
			for(var i=0; i<this.colors.length; i++)
			{
				var index = Math.trunc(i/16);
				var diff = (i%16)/16;
				var start = scheme[index];
				var end	= index+1<scheme.length? scheme[index+1] : scheme[0];
				this.colors[i] = this.createColorBetween(start, end, diff);
			}
		}
		else
		{
			//var max = data!=null ? data.getMax() : GenerateService.ALGORITHMS[0].getDefaultSettings().getMax();
			var max = 256; // todo
			this.colors = new Color[max];
			for(var i=0; i<colors.length; i++)
			{
				var index =  Math.trunc(i*(scheme.length-1)/max);
				var diff = i*(scheme.length-1)/max - index;
				var start = scheme[index];
				var end	= scheme[index+1];
				this.colors[i] = this.createColorBetween(start, end, diff);
			}
		}
	}
	
	update()
	{
		super.update();
		this.paint();
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