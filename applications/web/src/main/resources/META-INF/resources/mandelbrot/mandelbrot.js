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
		
		let self = this;
		this.colors = null; // the color scheme
		this.data = null; // the area data to draw
		this.progressdata = null; // the progress infos
		this.calculating = false;
		this.startdrag = null;
		this.enddrag  = null;
		this.point = null; // The current selection start point (if any).
		this.range = null;
		
		this.setColorScheme([this.createColor(50, 100, 0), this.createColor(255, 0, 0)], true);
					
		// shadow dom not available here :-(	
		// -> firstUpdated()
		//this.addMouseListener();				
						
		let displayid = "webgui"+jadex.generateUUID();
		// must not use args_0 as parameter name as this will be made to args list
		let terminate = jadex.getIntermediate('/mandelbrotdisplay/subscribeToDisplayUpdates?a='+displayid+'&returntype=jadex.commons.future.ISubscriptionIntermediateFuture',
		function(response)
		{
			console.log("recceived display update: "+response.data);
			let data = response.data;
			//System.out.println("rec: "+result.getClass());
			
			if(data.data!=null) // result instanceof PartDataChunk
			{
				self.calculating = true;
				self.addDataChunk(data);
			}
			
			if(data.algorithm!=null) // result instanceof AreaData
			{
				self.calculating = true;
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
		},
		function(err)
		{
			console.log("display subscribe err: "+err);
		});
		
		
		/*axios.get('/mandelbrotdisplay/subscribeToDisplayUpdates?args_0=webdisplay&returntype=jadex.commons.future.ISubscriptionIntermediateFuture', this.transform)
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
	
	firstUpdated() 
	{
		this.addMouseListener();
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
			
		//console.log("progressdata len (before, after): "+len+" "+Object.keys(this.progressdata).length+" "+part.finished+" "+part.progress);
		
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
			//g.drawImage(image, bounds.x+drawarea.x, bounds.y+drawarea.y,
			//	bounds.x+drawarea.x+drawarea.width, bounds.y+drawarea.y+drawarea.height,
			//	ix, iy, ix+iwidth, iy+iheight, this);
				
			var imgdata = new ImageData(image, swidth, sheight);
			ctx.putImageData(imgdata, 0, 0);
		}
  			
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
		
		let canvas2 = null;
		let ctx2 = null;
		// Draw progress boxes.
		if(this.progressdata!=null && Object.keys(this.progressdata).length>0)
		{
			canvas2 = this.createCanvas(twidth, theight);
			ctx2 = canvas2.getContext('2d');
			
			for(let key of Object.keys(this.progressdata)) 
			{
				let progress = this.progressdata[key];
				
				//console.log("progress is: "+progress);
					
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
				ctx2.strokeStyle = "white";
				ctx2.strokeRect(corx, cory, corw, corh);
				
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
					//console.log("a: "+textwidth+" "+textheight+" "+corw+" "+corh+" "+fsize);
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
					//console.log("b: "+textwidth+" "+textheight+" "+corw+" "+corh+" "+fsize);
					let x = corx + 2;
					let y = cory + Math.max((corh-textheight)/2, 2);
					this.drawProgressBar(x, y, textwidth, textheight, 'red', progress.progress/100, true, ctx2);
				}
			}
			
			
		}	
		
		// Draw range area.
		if(!this.calculating && this.range!=null)
		{
			if(canvas2==null)
			{
				canvas2 = this.createCanvas(twidth, theight);
				ctx2 = canvas2.getContext('2d');
			}
			
			let rratio = this.range.width/this.range.height;
			let bratio = swidth/sheight;
			
			// Draw left and right boxes to show unused space
			if(rratio<bratio)
			{
				let drawwidth = this.range.height*swidth/sheight;
				let offset = (this.range.width-drawwidth)/2;
				ctx2.fillStyle = "rgba(128,128,128,0.25)";
				ctx2.fillRect(this.range.x+offset, this.range.y, -offset, this.range.height+1);
				ctx2.fillRect(this.range.x+this.range.width, this.range.y, -offset, this.range.height+1);
			}
			// Draw upper and lower boxes to show unused space
			else if(rratio>bratio)
			{
				let	drawheight	= this.range.width*sheight/swidth;
				let offset = (this.range.height-drawheight)/2;
				ctx2.fillStyle = "rgba(128,128,128,0.25)";
				ctx2.fillRect(this.range.x, this.range.y+offset, this.range.width+1, -offset);
				ctx2.fillRect(this.range.x, this.range.y+this.range.height, this.range.width+1, -offset);
			}
		
			ctx2.strokeStyle = "white";
			ctx2.strokeRect(this.range.x, this.range.y, this.range.width, this.range.height);
		}
		
		if(canvas2!=null)
			ctx.drawImage(canvas2, 0, 0, twidth, theight);
	}
	
	createCanvas(width, height)
	{
		let canvas = document.createElement('canvas');
		let ctx = canvas.getContext('2d');
		//canvas2.style.background = "transparent";
		ctx.canvas.width  = width;
		ctx.canvas.height = height;
		return canvas;
	}
	
	// Determine how the image can be printed on screen
	// swidth/height: screen width/height (screen)
	// iwidth/height: image width/height (calculated)
	scaleToFit(swidth, sheight, iwidth, iheight)
	{
		var sratio = swidth/sheight;
		var iratio = iwidth/iheight;
		
		var drawstartx = 0;
		var drawstarty = 0;
		var drawendx = swidth;
		var drawendy = sheight;
		
		// Scale to fit height
		if(iratio<=sratio)
		{
			 var hratio	= sheight/iheight;
			 drawendy = iwidth*hratio;
			 drawstartx	= (swidth-drawendx)/2;
		}
		// Scale to fit width
		else if(iratio>sratio)
		{
			 var wratio = swidth/iwidth;
			 drawendy = iheight*wratio;
			 drawstarty	= (sheight-drawendy)/2;
		}
		return {x: Math.trunc(drawstartx), y: Math.trunc(drawstarty), width: Math.truc(drawendx), height: Math.trunc(drawendy)};
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
	
	addMouseListener()
	{
		let self = this;
		let element = this.shadowRoot.getElementById("canvas");
		
		let downlis = e => 
		{
			if(!self.calculating)
			{
				if(e.button===2)
				{
	            	self.startdrag = self.getMousePosition(element, e);
	            	self.range = null;
					self.point = null;
					console.dir("startdrag: "+self.startdrag);
            	}
            	else if(e.button===0)
            	{
					self.point = self.getMousePosition(element, e);
				}
           	}
            else
			{
				self.startdrag = null;
			}
		}
		element.addEventListener('mousedown', downlis);
		
		let movelis = e => 
		{
			console.log("mouse move: "+e.button);
			
			if(self.startdrag!=null && e.buttons===2)
			{
	            self.enddrag = self.getMousePosition(element, e);
				console.dir('new enddrag: '+self.enddrag);
				self.requestUpdate();
			}
			
			if(!self.calculating && self.point!=null && e.buttons===1)
			{
				let pos = self.getMousePosition(element, e); 
				self.range = {x: self.point.x<pos.x? self.point.x: pos.x,
					y: self.point.y<pos.y? self.point.y: pos.y,
					width: Math.abs(self.point.x-pos.x),
					height: Math.abs(self.point.y-pos.y)
				};
				
				self.requestUpdate();
			}
		}
		element.addEventListener('mousemove', movelis);
		
		let uplis = e => 
		{
			if(self.startdrag!=null && self.enddrag!=null)
			{
				console.log("dragged: "+self.startdrag+" "+self.enddrag);
				//dragImage();
			}
			
			self.startdrag = null;
			self.enddrag = null;
		}
		element.addEventListener('mouseup', uplis);
		
		let wheellis = e =>
		{
			let pos = self.getMousePosition(element, e);
			let delta = Math.max(-1, Math.min(1, (e.wheelDelta || -e.detail)));
			console.log("wheel: "+delta);
			
			let percent = Math.abs(10*delta);
			let factor;
			if(delta>0)
			{
				factor = (100+percent)/100;
			}
			else
			{
				factor = 100/(100+percent);
			}
			//this.zoomImage(e.getX(), e.getY(), factor);
			console.log("zoom: "+pos+" "+factor);
		}
		element.addEventListener("wheel", wheellis);
		
		let clicklis = e =>
		{
			let pos = this.getMousePosition(element, e);
			if(e.button===2)
			{
				//self.calcDefaultImage();
			}
			else if(!calculating && range!=null)
			{
				if(pos.x>=range.x && pos.x<=range.x+range.width
					&& pos.y>=range.y && pos.y<=range.y+range.height)
				{
					console.log("zoomIntoRange: "+pos+" "+range);
					//zoomIntoRange();
				}
			}
		}
	}
	
	getMousePosition(element, event)
	{
		let rect = element.getBoundingClientRect();
	    let x = event.clientX - rect.left;
	    let y = event.clientY - rect.top;
		return {x,y};
	}
	
	getImageWidth()
	{
		let results = this.data.data;
		let width = results.length;
		return width;
	}
	
	getImageHeight()
	{
		let results = this.data.data;
		let height = results[0].length;
		return height;
	}
	
	dragImage()
	{
		let sw = this.getImageWidth();
		let sh = this.getImageHeight();
		let iw = this.getImageWidth();
		let ih = this.getImageHeight();
		let drawarea = this.scaleToFit(sw, sh, iw, ih);
		
		let xdiff = this.startdrag.x-this.enddrag.x;
		let ydiff = this.startdrag.y-this.enddrag.y;
		let xp = xdiff/drawarea.width;
		let yp = ydiff/drawarea.height;
		
		let xm = (this.data.XEnd-this.data.XStart)*xp;
		let ym = (this.data.YEnd-this.data.YStart)*yp;
	 	let xs = this.data.XStart+xm;
		let xe = this.data.XEnd+xm;
		let ys = this.data.YStart+ym;
		let ye = this.data.YEnd+ym;
		
		this.startdrag = null;
		this.enddrag = null;
		this.range = {x: drawarea.x+xdiff, y: drawarea.y+ydiff, width: drawarea.width, height: drawarea.height};

		this.calcArea(xs, xe, ys, ye, this.data.SizeX, this.data.SizeY);
	}

	/**
	 *  Zoom into the given location by the given factor.
	 */
	protected void zoomImage(int x, int y, double factor)
	{
		let sw = this.getImageWidth();
		let sh = this.getImageHeight();
		let iw = this.getImageWidth();
		let ih = this.getImageHeight();
		let drawarea = this.scaleToFit(sw, sh, iw, ih);
		
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
		range = new Rectangle(bounds.x+drawarea.x+(int)Math.round(xdiff*xrel), bounds.y+drawarea.y+(int)Math.round(ydiff*yrel),
			(int)Math.round(drawarea.width*factor), (int)Math.round(drawarea.height*factor));
		
//		zoomIntoRange();
		this.calcArea(xs, xe, ys, ye, data.getSizeX(), data.getSizeY());
	}
	
	zoomIntoRange()
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

		this.calcArea(ox+owidth*x, ox+owidth*x2, oy+oheight*y, oy+oheight*y2, area.width, area.height);
	}
		
	calcArea(x1, x2, y1, y2, sizex, sizey)
	{
		let data = {};
		data.XStart = x1;
		data.XEnd = x2;
		data.YStart = y1;
		data.YEnd = y2; 
		data.SizeX = sizex;
		data.Sizey = sizey;
		
		if(this.data!=null)
		{
			data.Algorithm = this.data.algorithm;
			data.Max = this.data.max;
			data.TaskSize = this.data.TaskSize;
			data.ChunkCount = this.data.ChunkCount;
			data.DisplayId = this.data.DisplayId;
		}
		
		//DisplayPanel.this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		this.calculating = true;
		
		this.requestUpdate();
		
		axios.get('/mandelbrotgenerate/generateArea?a='+JSON.stringify(data), this.transform)
			.then(function(resp)
			{
				console.log("recceived display update: "+resp.data);
				this.calculating = false;
				//DisplayPanel.this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			})
			.catch(ex => 
			{
				console.log(ex);
				this.calculating = false;
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
