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
			self.handleDisplayUpdate(response);
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
		let self = this;
		
		this.addMouseListener(this.shadowRoot.getElementById("canvas"));
		this.makeElementDragable(this.shadowRoot.getElementById("settings"));
		this.makeElementDragable(this.shadowRoot.getElementById("image"), true);
		
		let ro = new ResizeObserver(entries => 
		{
			for(let entry of entries) 
			{
				let adaptsize = self.shadowRoot.getElementById("adaptsize2").checked;
				if(adaptsize)
					self.adaptSizeToCanvas(entry, self);				
  			}
		});		
		ro.observe(this.shadowRoot.getElementById("canvas"));
  	}
	
	disconnectedCallback()
	{
		super.disconnectedCallback();
		console.log('disconnected')
	}
	
	handleDisplayUpdate(response)
	{
		console.log("recceived display update: "+response.data);
		let self = this;
		
		let data = response.data;
		//System.out.println("rec: "+result.getClass());
		
		if(data.algorithmClass!=null) // result instanceof AreaData
		{
			self.calculating = true;
			self.initResults(data);
			self.setSettings(data);
			
			let adaptsize = this.shadowRoot.getElementById("adaptsize").checked;
			if(adaptsize)
			{
				let results = this.data.data;
				let swidth = results.length;
				  let sheight = results[0].length;
				let container = this.shadowRoot.getElementById("image");
				container.style.width = swidth+"px";
				container.style.height = sheight+"px";
	  		}
		}
		else if(data.data!=null) // result instanceof PartDataChunk
		{
			self.calculating = true;
			self.addDataChunk(data);
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
	}
	
	makeElementDragable(element, leftborder) 
	{
		var x1 = 0;
		var y1 = 0;
		var x2 = 0; 
		var y2 = 0;

		var moved = e =>
		{
			e = e || window.event;
	    	e.preventDefault();
	    	x1 = x2 - e.clientX;
	    	y1 = y2 - e.clientY;
	    	x2 = e.clientX;
	    	y2 = e.clientY;
			//console.log("to: "+x2+" "+y2);
	    	// set the element's new position:
			//var y = parseInt(element.style.top) || 0;
	    	//var x = parseInt(element.style.left) || 0;
			element.style.top = element.offsetTop-y1+"px";
	    	element.style.left = element.offsetLeft-x1+"px";
		}

		var md = e => 
		{
			//console.log("offsetx: "+e.offsetX+" "+e.clientX+" "+element.offsetWidth);
			
			// only dragable when at left border (in case leftborder true)
			if(leftborder && e.offsetX>0)
			{
				console.log("not at border");
				return;
			}
			
			if(element.offsetWidth-e.offsetX < 20 && element.offsetHeight-e.offsetY < 20)
			{
				//console.log("at resize border");
				return;
	    	}

			e = e || window.event;
	    	//e.preventDefault(); // if used will hinder input fields from working
			x2 = e.clientX;
	    	y2 = e.clientY;
			//console.log("from: "+x2+" "+y2);
			
			// clean up document mouse listeners after mouse released
			var mu = e =>
			{
				document.removeEventListener("mouseup", mu);
				document.removeEventListener("mousemove", moved);
			};
			
			document.addEventListener("mouseup", mu);
			
			// watch now for movements
			document.addEventListener("mousemove", moved);
	  	}

		// listen on mouse clicks on that element
		element.addEventListener("mousedown", md);
	}
	
	getMethodPrefix() 
	{
		//return '/webjcc/invokeServiceMethod?cid=null'+'&servicetype='+'jadex.micro.examples.mandelbrot_new.IDisplayService'; cid=null leads to CID(null) :-()
		return '/webjcc/invokeServiceMethod?servicetype='+'jadex.micro.examples.mandelbrot_new.IDisplayService';
	}
	
	initResults(data)
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
		if(data.data===null || this.data===null)
		{
			console.log("Cannot add data chunk, no initial AreaData received");
			return; 
		}
		
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
		let image = new Uint8ClampedArray(oimage); // clone the image data
		let adaptsize = this.shadowRoot.getElementById("adaptsize").checked;
		let container = this.shadowRoot.getElementById("image");
		let canvas = this.shadowRoot.getElementById("canvas");
		let ctx = canvas.getContext("2d");
		let sx = 0;
		let sy = 0;
		let swidth = results.length;
		let sheight = results[0].length;
		
		ctx.canvas.width  = swidth;
		ctx.canvas.height = sheight;
  			
		// Zoom into original image while calculating
		if(this.calculating && this.range!=null)
		{
			console.log("draw range");
			
			let canvas2 = this.createCanvas(swidth, sheight);
			let ctx2 = canvas2.getContext('2d');
			var imgdata = new ImageData(image, swidth, sheight);
			ctx2.putImageData(imgdata, 0, 0);
			
			ctx.drawImage(canvas2, this.range.x, this.range.y, this.range.width, this.range.height, 0, 0, swidth, sheight);
		}
		
		// Offset and clip image and show border while dragging.
		else if(this.startdrag!=null && this.enddrag!=null)
		{
			console.log("draw dragged");
			// Draw original image in background
			/*g.drawImage(image, bounds.x+drawarea.x, bounds.y+drawarea.y,
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
			g.setClip(clip);*/
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
			
		//var imgdata = new ImageData(image, swidth, sheight);
		//ctx.putImageData(imgdata, 0, 0);
			
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
			canvas2 = this.createCanvas(swidth, sheight);
			ctx2 = canvas2.getContext('2d');
			
			for(let key of Object.keys(this.progressdata)) 
			{
				let progress = this.progressdata[key];
				
				//console.log("progress is: "+progress);
					
				let xf = swidth/progress.imageWidth;
				let yf = sheight/progress.imageHeight;
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
				canvas2 = this.createCanvas(swidth, sheight);
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
			ctx.drawImage(canvas2, 0, 0, swidth, sheight, 0, 0, swidth, sheight);
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
	/*scaleToFit(swidth, sheight, iwidth, iheight)
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
		return {x: Math.trunc(drawstartx), y: Math.trunc(drawstarty), width: Math.trunc(drawendx), height: Math.trunc(drawendy)};
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
	
	addMouseListener(element)
	{
		let self = this;
		let drag = false;
		
		let downlis = e => 
		{
			drag = false;
			
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
			//console.log("mouse move: "+e.button);
			drag = true;
			
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
				self.dragImage();
			}
			
			self.startdrag = null;
			self.enddrag = null;
			
			self.requestUpdate();
		}
		element.addEventListener('mouseup', uplis);
		
		let wheellis = e =>
		{
			let pos = self.getMousePosition(element, e);
			let delta = Math.max(-1, Math.min(1, (e.wheelDelta || -e.detail)))*-1;
			console.log("wheel: "+delta);
			
			let percent = Math.abs(10*delta)*2;
			let factor;
			if(delta>0)
			{
				factor = (100+percent)/100;
			}
			else
			{
				factor = 100/(100+percent);
			}
			self.zoomImage(pos.x, pos.y, factor);
		}
		element.addEventListener("wheel", wheellis);
		
		let clicklis = e =>
		{
			console.log("drag: "+drag);
			if(drag)
				return;
	
			let pos = self.getMousePosition(element, e); 
			
			if(e.button===2)
			{
				//self.calcDefaultImage();
			}
			else if(e.button===0 && !self.calculating && self.range!=null)
			{
				// Zoom when user clicked into range
				if(pos.x>=self.range.x && pos.x<=self.range.x+self.range.width
					&& pos.y>=self.range.y && pos.y<=self.range.y+self.range.height)
				{
					self.zoomIntoRange();
				}
			}
		}
		
		element.addEventListener("click", clicklis);
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
	
	getCanvasWidth()
	{
		let canvas = this.shadowRoot.getElementById("canvas");
		let ctx = canvas.getContext("2d");
		return ctx.canvas.width;
	}
	
	getCanvasHeight()
	{
		let canvas = this.shadowRoot.getElementById("canvas");
		let ctx = canvas.getContext("2d");
		return ctx.canvas.height;
	}
	
	dragImage()
	{
		console.log("dragImage: "+self.startdrag+" "+self.enddrag);
		let sw = this.getImageWidth();
		let sh = this.getImageHeight();
		let iw = this.getImageWidth();
		let ih = this.getImageHeight();
		//let drawarea = this.scaleToFit(sw, sh, iw, ih);
		
		let xdiff = this.startdrag.x-this.enddrag.x;
		let ydiff = this.startdrag.y-this.enddrag.y;
		let xp = xdiff/sw;
		let yp = ydiff/sh;
		
		let xm = (this.data.XEnd-this.data.XStart)*xp;
		let ym = (this.data.YEnd-this.data.YStart)*yp;
	 	let xs = this.data.XStart+xm;
		let xe = this.data.XEnd+xm;
		let ys = this.data.YStart+ym;
		let ye = this.data.YEnd+ym;
		
		this.startdrag = null;
		this.enddrag = null;
		this.range = {x: xdiff, y: ydiff, width: sw, height: sh};

		this.calcArea(xs, xe, ys, ye, this.data.sizeX, this.data.sizeY);
	}

	// Zoom into the given location by the given factor.
	zoomImage(x, y, factor)
	{
		console.log("zoomImage "+factor);
		let sw = this.getImageWidth();
		let sh = this.getImageHeight();
		let iw = this.getImageWidth();
		let ih = this.getImageHeight();
		//let drawarea = this.scaleToFit(sw, sh, iw, ih);
		
		let mx = Math.min(sw, Math.max(0, x));
		let my = Math.min(sh, Math.max(0, y));
		let xrel = mx/sw;
		let yrel = my/sh;

		let wold = this.data.xEnd-this.data.xStart;
		let hold = this.data.yEnd-this.data.yStart;
		let wnew = wold*factor;
		let hnew = hold*factor;
		let wd = wold-wnew;
		let hd = hold-hnew;
		
		let xs = this.data.xStart+wd*xrel;
		let xe = xs+wnew;
		let ys = this.data.yStart+hd*yrel;
		let ye = ys+hnew;
		
		// Set range for drawing preview of zoom area.
		let xdiff = sw - sw*factor;
		let ydiff = sh - sh*factor;
		this.range = {x: Math.trunc(xdiff*xrel), y: Math.trunc(ydiff*yrel),
			width: Math.trunc(sw*factor), height: Math.trunc(sh*factor)};
		
//		zoomIntoRange();
		this.calcArea(xs, xe, ys, ye, this.data.sizeX, this.data.sizeY);
	}
	
	zoomIntoRange()
	{
		console.log("zoomIntoRange: "+this.range);
		let sw = this.range.width;
		let sh = this.range.height;
		let iw = this.getImageWidth();
		let ih = this.getImageHeight();
		
		let x = this.range.x/iw;
		let y = this.range.y/ih;
		let x2 = x + this.range.width/iw;
		let y2 = y + this.range.height/ih;
		
		// Original bounds
		let ox = this.data.xStart;
		let oy = this.data.yStart;
		let owidth = this.data.xEnd-this.data.xStart;
		let oheight	= this.data.yEnd-this.data.yStart;
		
		let neww;
		let newh;
		let rw = this.range.width/this.range.height;
		let rh = this.range.height/this.range.width;
		if(rh<1)
		{
			neww = iw;
			newh = Math.trunc(rh*ih);
		}
		else
		{
			neww = Math.trunc(rw*iw);
			newh = ih;
		}
		
		this.calcArea(ox+owidth*x, ox+owidth*x2, oy+oheight*y, oy+oheight*y2, neww, newh);
	}
	
	getAlgorithmDefaultSettings(name)
	{
		return new Promise((resolve, reject) => 
		{
			axios.get('/mandelbrotdisplay/getAlgorithmDefaultSettings?a='+name, this.transform)
			.then(function(response)
			{
				console.log("fetching default settings finished: "+response.data);
				resolve(response.data);
			})
			.catch(ex => 
			{
				console.log(ex);
				reject(err);
			});
		});
		
	}
		
	calcArea(x1, x2, y1, y2, sizex, sizey, algo, max, chunks, tasksize)
	{
		let data = {};
		data.xStart = x1;
		data.xEnd = x2;
		data.yStart = y1;
		data.yEnd = y2; 
		data.sizeX = sizex;
		data.sizeY = sizey;
		
		if(algo)
			data.algorithmClass = algo;
		else if(this.data!=null)
			data.algorithmClass = this.data.algorithmClass;
		
		if(max)
			data.max = max;
		else if(this.data!=null)
			data.max = this.data.max;
		
		if(chunks)
			data.chunkCount = chunks;
		else if(this.data!=null)
			data.chunkCount = this.data.chunkCount;
			
		if(tasksize)
			data.taskSize = tasksize;
		else if(this.data!=null)
			data.taskSize = this.data.taskSize;
		
		if(this.data!=null && this.data.displayId)
			data.displayId = this.data.displayId;
		
		//DisplayPanel.this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		this.calculating = true;
		
		this.requestUpdate();
		
		console.log("request data: "+JSON.stringify(data));
		axios.get('/mandelbrotgenerate/generateArea?a='+JSON.stringify(data), this.transform)
		.then(function(response)
		{
			console.log("generateArea finished: "+response.data);
			self.calculating = false;
			//self.handleDisplayUpdate(response);
			//DisplayPanel.this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		})
		.catch(ex => 
		{
			console.log(ex);
			self.calculating = false;
		});
	}
	
	generateArea(e)
	{
		var algo = this.shadowRoot.getElementById("algorithm").value;
		if(algo)
			algo = {value: algo};
		
		var xmin = this.shadowRoot.getElementById("xmin").value;
		var xmax = this.shadowRoot.getElementById("xmax").value;
		var ymin = this.shadowRoot.getElementById("ymin").value;
		var ymax = this.shadowRoot.getElementById("ymax").value;
		var sizex = this.shadowRoot.getElementById("sizex").value;
		var sizey = this.shadowRoot.getElementById("sizey").value;
		var max = this.shadowRoot.getElementById("max").value;
		var chunks = this.shadowRoot.getElementById("chunks").value;
		var tasksize = this.shadowRoot.getElementById("tasksize").value;
		
		this.calcArea(xmin, xmax, ymin, ymax, sizex, sizey, algo, max, chunks, tasksize);
	}
	
	resetSettings(e)
	{
		this.shadowRoot.getElementById("algorithm").value = "jadex.micro.examples.mandelbrot_new.MandelbrotAlgorithm";
		this.shadowRoot.getElementById("xmin").value = -2.0;
		this.shadowRoot.getElementById("xmax").value = 1.0;
		this.shadowRoot.getElementById("ymin").value = -1.5;
		this.shadowRoot.getElementById("ymax").value = 1.5;
		this.shadowRoot.getElementById("sizex").value = 300;
		this.shadowRoot.getElementById("sizey").value = 300;
		this.shadowRoot.getElementById("max").value = 256;
		this.shadowRoot.getElementById("chunks").value = 4;
		this.shadowRoot.getElementById("tasksize").value = 300;
		this.range = null;
		
		this.generateArea();
	}
	
	setSettings(data)
	{
		if(data.algorithmClass!=null)
			this.shadowRoot.getElementById("algorithm").value = data.algorithmClass.value;
		this.shadowRoot.getElementById("xmin").value = data.xStart;
		this.shadowRoot.getElementById("xmax").value = data.xEnd;
		this.shadowRoot.getElementById("ymin").value = data.yStart;
		this.shadowRoot.getElementById("ymax").value = data.yEnd;
		this.shadowRoot.getElementById("sizex").value = data.sizeX;
		this.shadowRoot.getElementById("sizey").value = data.sizeY;
		this.shadowRoot.getElementById("max").value = data.max;
		this.shadowRoot.getElementById("chunks").value = data.chunkCount;
		this.shadowRoot.getElementById("tasksize").value = data.taskSize;
	}
	
	setDefaultSettings(e)
	{
		let self = this;
		let elem = this.shadowRoot.getElementById('algorithm');
		let value = elem.options[elem.selectedIndex].value;
		this.getAlgorithmDefaultSettings(value).then(data =>
		{
			self.setSettings(data);
		})
		.catch(err =>
		{
			console.log(err);
		});
		
	}
	
	adaptSizeToCanvas(e, self)
	{
		if(self==null)
			self = this;
		
		let canvas = this.shadowRoot.getElementById("canvas");
		self.shadowRoot.getElementById("sizex").value = canvas.offsetWidth;
		self.shadowRoot.getElementById("sizey").value = canvas.offsetHeight;
	}

	update()
	{
		super.update();
		this.paint();
	}
	
	static get styles() 
	{
		var ret = [];
		if(super.styles!=null)
			ret.push(super.styles);
		ret.push(
		    css`
			.dragable {
				padding: 10px;
				position: fixed;
				left: 50%;
    			top: 50%;
    			transform: translate(-50%, -50%);
				width: 30%;
			  	background-color: #00000066;
			  	border: 1px solid #d3d3d3;
				z-index: 1;
		 		resize: both;
    			overflow: hidden;
    			color: white;
			}
			.dragable2 {
				position: fixed;
				width: fit-content;
				height: fit-content;
				resize: both;
				overflow: hidden;
				background-color: yellow;
			}
			.grid {
				display: grid;
				grid-template-columns: max-content auto;
				grid-gap: 0.5em;
			}
			.grid2 {
				display: grid;
				grid-template-columns: auto auto;
			}
			.fitcontent {
				width: fit-content;
			}
			.fitcontentheight {
				height: fit-content;
			}
			.floatright {
				float: right;
			}
			.margintop {
				margin-top: 0.5em; 
			}
			.jadexbtn {
				background-color:#2a6699;
				border-radius:6px 6px 6px 6px;
				font-weight:bold;
				font-weight:600;
				padding-top:10px;
				padding-bottom:10px;
				border: 0px;
				color: #fff;
			}
			.jadexbtn:disabled,
			.jadexbtn.disabled {
				border: 1px solid #999999;
				background-color: #cccccc;
		  		color: #666666;
			}
			.leftborder {
				border-left: 10px solid red; 
			}
			.w100 {
				width: 100%;
			}
			.h100 {
				height: 100%;
			}
			.checkboxleft {
				width: fit-content;
				margin-left: 0px;
			}
		    `);
		return ret;
	}
	
	render() 
	{
		return html`
			<h1>Factals</h1>
			
			<div id="settings" class="dragable fitcontent fitcontentheight">
				<div class="grid">
					<label for="alogrithm">Algorithm</label> 
					<select name="algorithm" id="algorithm" @change="${e => this.setDefaultSettings(e)}">
						<option value="jadex.micro.examples.mandelbrot_new.MandelbrotAlgorithm">Mandelbrot</option>
	  					<option value="jadex.micro.examples.mandelbrot_new.LyapunovAlgorithm">Lyapunov</option>
					</select> 
					
					<label for="xmin">Min x</label> 
					<input name="xmin" id="xmin" placeholder="-2.0" value="-2" type="number" min="-5" value="5" step="0.1">
					
					<label for="xmax">Max x</label> 
					<input name="xmax" id="xmax" placeholder="1.0" value="1" type="number" min="-5" value="5" step="0.1">
					
					<label for="ymin">Min y</label> 
					<input name="ymin" id="ymin" placeholder="-1.5" value="-1.5" type="number" min="-5" value="5" step="0.1">
					
					<label for="ymax">Max y</label> 
					<input name="ymax" id="ymax"placeholder="1.5" value="1.5" type="number" min="-5" value="5" step="0.1">
					
					<label for="sizex">Size x</label> 
					<input name="sizex" id="sizex" value="300" type="number" min="10" max="10000" step="100">
					
					<label for="sizey">Size y</label> 
					<input name="sizey" id="sizey" value="300" type="number" min="10" max="10000" step="100">
					
					<label for="max">Max</label> 
					<input name="max" id="max" value="256" type="number" min="2" max="10000">
					
					<label for="chunks">Chunks</label> 
					<input name="chunks" id="chunks" value="4" type="number" min="1" max="10000">
					
					<label for="tasksize">Task size</label> 
					<input name="tasksize" id="tasksize" value="300" type="number" min="1" max="100000">
					
					<label for="adaptsize">Adapt screen size to result</label> 
					<input name="adaptsize" id="adaptsize" class="checkboxleft" checked type="checkbox">				

					<label for="adaptsize2">Adapt calc size to screen</label> 
					<input name="adaptsize2" id="adaptsize2" class="checkboxleft" checked type="checkbox">
				</div>
			
				<div class="floatright margintop">
					<button class="fitcontent jadexbtn" @click="${e => this.resetSettings(e)}">Reset</button>
					<button class="fitcontent jadexbtn" @click="${e => this.generateArea(e)}">Generate</button>
				</div>
			</div>
			
			<div id="image" class="leftborder dragable2">
				<canvas id="canvas" class="w100 h100"></canvas>
			</div>
		`;
	}
}

if(customElements.get('jadex-mandelbrot') === undefined)
	customElements.define('jadex-mandelbrot', MandelbrotElement);
