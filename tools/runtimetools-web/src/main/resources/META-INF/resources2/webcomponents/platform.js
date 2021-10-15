let { LitElement, html, css } = modLoad('lit-element');
let { BaseElement } = modLoad('base-element');
let { CidElement } = modLoad('cid-element');

// Tag name 'jadex-platform'
class PlatformElement extends CidElement 
{
	loaded = false;

	static get properties() 
	{
		var ret = {};
		if(super.properties!=null)
		{
			for (let key in super.properties)
				ret[key]=super.properties[key];
		}
		ret['plugin'] = { type: String };
		//ret['loggedin'] = { type: Boolean };
		return ret;
	}
	
	/*constructor()
	{
		super();

	}*/
	
	init()
	{
		this.app.login.listeners.add(this);
		
		console.log("platform");
		this.plugin = null;
		//console.log("setting plugins to {}");
		this.plugins = {};
		//console.log("set plugins to {}");
		this.no = 0;
		
		var self = this;
		this.listener = function refresh()
		{
			var w = self.getScaledWidth();
			for(var i=0; ; i++)
			{
				var elem = self.shadowRoot.getElementById("plugin"+i);
				if(elem!=null)
				{
					elem.style.width= w+"px";
					elem.style.height= w+"px"
				}
				else
				{
					break;
				}
			}	
			self.requestUpdate();
		};
		this.checkPlatform(10000);
		
		
		//"../webcomponents/login.js"
	}
	
	postInit()
	{
		let self = this;
		this.loadPluginInfos().then(function()
    	{
    		self.loaded = true;

			self.plugin = self.getUrlHashParam(3);

			let lastplugin = localStorage.getItem("lastplugin")
    		if (self.plugin != null) {
				localStorage.setItem("lastplugin", self.plugin);
			}
    		if (lastplugin != null) {
				history.pushState(null, "", "/#/platform/"+self.cid+"/"+lastplugin);
				self.plugin = lastplugin;
			}
    		else if(self.plugin == null || self.plugins.length > 0) {
				let name = self.getPlugins()[0].name
				history.pushState(null, "", "/#/platform/"+self.cid+"/"+name);
				self.plugin = name;
			}
			//self.showPlugin2({ "name" : self.plugin });
			self.showPlugin2(self.plugin);
			//resolve();
    	}).catch(function(err) 
		{
			//self.createErrorMessage("Could not load plugins", err);
			console.log("err: "+err);
			//throw err;
			//reject(err);
		});
	}
	
	/*init() 
	{
		console.log("platform");
		
		this.cid = null;
		this.plugin = null;
		this.plugins = [];
		this.loggedin = false;
	}*/
	
	connectedCallback()
	{
		super.connectedCallback();
		window.addEventListener("resize", this.listener);
	}
	
	disconnectedCallback()
	{
		super.disconnectedCallback();
		window.removeEventListener("resize", this.listener);
	}
	
	showPlugin(event)
	{
		// Logic for setting the active link in the navbar
		var el = $('#plugins .navbar-nav').find('li.active');
		var sel = event.target.parentElement.parentElement;
		for(var i=0; i<el.length; i++)
			el[i].classList.remove('active');
		sel.classList.add("active");
		
		this.showPlugin2(event.item.name);
		history.pushState(null, "", "/#/platform/"+this.cid+"/"+event.item.name);
	}
	
	showPlugin2(name)
	{
		// Only show if available
		if(this.plugins[name]==null)
			return;
			
		let done = null;
		let fail = null;
		let ret = new Promise((resolve, reject) => {
			done = resolve;
			fail = reject;
		});
		
		//console.log("login is: "+LoginElement.loginhandler.isLoggedIn());
		//console.log("show plugin: "+name+" "+this.cid);

		let self = this;
		
		//console.log('Loading plugin ' + name + ' value is ' + this.plugins[name].unrestricted + ' i am loggedin ' + this.app.login.isLoggedIn());
		if(!this.plugins[name].unrestricted && !this.app.login.isLoggedIn())
		{
			let html = "<jadex-restricted></jadex-restricted>";
			self.shadowRoot.getElementById("plugin").innerHTML = html;
			self.requestUpdate().then(() => 
			{
				done();
			});
		}
		else
		{
			this.plugin = name;
			var pi = this.plugins[name];
		
			if(pi.component==null)
			{
				this.loadPlugin(pi.name).then(function()
				{
					let html = "<jadex-"+name+" cid='"+self.cid+"'></jadex-"+name+">";
					//console.log("Insert plugin element: " + p.name);
					self.shadowRoot.getElementById("plugin").innerHTML = html;
					//console.log("Req update: " + p.name);
					self.requestUpdate().then(() => 
					{
						done();
					});
					//console.log("Updated1: " + name);
				}).catch(function(err)
				{
					console.log(err);
					fail();
				});
			}
			else
			{
				let html = "<jadex-"+name+" cid='"+this.cid+"'></jadex-"+name+">";
				//console.log("Insert plugin element: " + p.name);
				this.shadowRoot.getElementById("plugin").innerHTML = html;
				//console.log("Req update: " + p.name);
				self.requestUpdate().then(() => 
				{
					done();
				});
				//console.log("Updated2: " + name);
			}
		}
		return ret;
	}

	/**
	 *  Changes URL and selects new plugin (GUI operation).
	 *  @param name Name of selected plugin.
	 */
	selectPlugin(name)
	{
		history.pushState(null, "", "/#/platform/"+this.cid+"/"+name);
		localStorage.setItem("lastplugin", name);
		this.showPlugin2(name);
	}
	
	loadPlugin(name)
	{
		var self = this;
		return new Promise(function(resolve, reject) 
		{
			axios.get('webjcc/getPluginFragment?sid='+JSON.stringify(self.plugins[name].sid)+'&contenttype=application/json', self.transform).then(function(resp)
			{
				//console.log("received: "+resp);	
				var component = resp.data;
				//console.log("plug: "+component);
				
				self.plugins[name].component = component;
				
				if(!component.startsWith('<'))
				{
					let funname = name + "pluginFragment";
					//console.log("Dynamically starting " + funname);
					let componentfunc = new Function(component + "\n//# sourceURL=" + funname + "\n");
					componentfunc();
					resolve(component);
					
					/*importShim.topLevelLoad(importShim.getFakeUrl(), component).then(function()
					{
						resolve(component);
					}).catch(function(err){reject(err)})*/
				}
			}).catch(function(err) 
			{
				//console.log("err: "+err);	
				reject(err);
			});
		});		
	}
	
	loadPluginInfos() 
	{
		var self = this;
		return new Promise(function(resolve, reject) 
		{
			let presolve = resolve;
			axios.get('webjcc/getPluginInfos?cid='+self.cid, self.transform).then(function(resp)
			{
				//console.log("received: "+resp);	
				
				var pis = resp.data;
				//console.log(map);
				
				var cnt = 0;
				for(var i=0; i<pis.length; i++)
				{
					self.plugins[pis[i].name] = pis[i];
					pis[i].image = new Image();
					pis[i].image.src = 'data:image/png;base64,'+pis[i].icon.__base64;
					pis[i].image.onload = function()
					{
						cnt++;
						if(cnt==pis.length)
						{
							//self.showPlugin2(self.getPlugins()[0].name);
							presolve();
							//console.log("loadPlugs show: "+self.getPlugins()[0].name);
						}
					}
				}

				
			}).catch(function(err) 
			{
				console.log("errpluginInfos: "+err);
				reject(err);
			});
		});
	}
	
	checkPlatform(interval)
	{
		this.internalCheckPlatform(interval, ++this.no);
	}
	
	internalCheckPlatform(interval, no) 
	{
		// terminate when another call to checkPlatform() has been performed
		//console.log("check platform: "+no+" "+this.no);
		if(no!=this.no)
		{
			console.log("terminate platform check");
			return;
		}
			
		var self = this;
		axios.get('webjcc/isPlatformAvailable?cid='+self.cid, self.transform).then(function(resp)
		{
			setTimeout(function(){self.internalCheckPlatform(interval, no)}, interval!=undefined? interval: 10000);

			/*self.isLoggedIn().then(function()
			{
				setTimeout(function(){self.internalCheckPlatform(interval, no)}, interval!=undefined? interval: 10000);
			})
			.catch(function(err)
			{
				setTimeout(function(){self.internalCheckPlatform(interval, no)}, interval!=undefined? interval: 10000);
			});*/
		})
		.catch(function(err) 
		{
			console.log("platform cannot be reached: "+err);	
			//history.pushState(null, "", "/#/platforms");
			window.location.href = "/#/platforms";
		});
	}
	
	static get styles() 
	{
	    return css`
	    	.navbar-custom {
	    		background-color: #aaaaaa;
	    	}
	    	.navbar-custom .navbar-brand,
	    	.navbar-custom .navbar-text {
	    		color: rgba(255,255,255,.8);
	    	}
	    	.navbar-custom .navbar-nav .nav-link {
	    		color: rgba(255,255,255,.5);
	    	}
	    	.navbar-custom .nav-item.active .nav-link,
	    	.navbar-custom .nav-item:focus .nav-link,
	    	.navbar-custom .nav-item:hover .nav-link {
	    		color: #ffffff;
	    	}
			.overlay {
				background: rgba(0, 0, 0, 0.1); /* Black see-through */
			}
	    `;
	}
	
	getPlugins()
	{
		let self = this;
		let ret = Object.values(this.plugins).sort(function(p1, p2) 
		{
			var ret = 0;
			if(!self.app.login.isLoggedIn())
				ret = p2.unrestricted - p1.unrestricted;
			
			if(ret===0)
			{
				ret = p2.priority-p1.priority;
    			if(ret===0)
					ret = p1.name.toLowerCase().localeCompare(p2.name.toLowerCase());
			}
			return ret
		});
		//console.log("plugins: "+ret);
		return ret;
	}
	
	requestUpdate()
	{
		if(this.plugin!=null && this.plugins!=null && !this.app.login.isLoggedIn() && !this.plugins[this.plugin].unrestricted)
			this.showPlugin2(this.getPlugins()[0].name);
		
		return super.requestUpdate();
	}
	
	/*getScaledImage(img, width, height)
	{
		if(height===undefined)
			var height = width; 
    	var canvas = document.createElement('canvas');
        var ctx = canvas.getContext('2d');
		canvas.width = width;
		canvas.height = height;
    	ctx.drawImage(img, 0, 0, width, height);
    	return canvas.toDataURL();
	}*/
	
	getScaledWidth()
	{
		var num = Object.values(this.plugins).length;
		var maxw = this.shadowRoot.getElementById('plugincont').offsetWidth; 
		var w = Math.max(Math.min(80, maxw/num), 24);
		//console.log("width: "+w);
		return w;
	}
	
	asyncRender() 
	{
		var self = this;
		return html`
			<h1 class="m-0 p-0">Platform ${this.cid}</h1>
			<div class="container-fluid m-0 p-0" id="plugincont">
				${this.getPlugins().map((p, index) => html`
					${!p.unrestricted && !this.app.login.isLoggedIn()? "": p.icon!=null? 
						html`<img id="${'plugin'+index}" class="${self.plugin===p.name? "overlay": ""}" src="data:image/png;base64,${p.icon.__base64}" alt="Red dot" @click="${(e) => {self.selectPlugin(p.name)}}" data-toggle="tooltip" data-placement="top" title="${p.name}"/>`:
						html`<span @click="${(e) => {self.selectPlugin(p.name)}}">${p.name}</span>`
					}
				`)}
			</div>
			
			<div id="plugin"></div>
		`;
	}
	
	/*isLoggedIn()
	{
		var self = this;
		return new Promise(function(resolve, reject) 
		{
			axios.get('webjcc/isLoggedIn', {headers: {'x-jadex-isloggedin': true}}, self.transform).then(function(resp)
			{
				//console.log("is logged in: "+resp);
				self.loggedin = resp.data;
				resolve(self.loggedin);
			})
			.catch(function(err) 
			{
				console.log("check failed: "+err);	
				reject(err);
			});
		});
	}*/
}

if(customElements.get('jadex-platform') === undefined)
	customElements.define('jadex-platform', PlatformElement);
