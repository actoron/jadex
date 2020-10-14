import {LitElement, html, css} from 'lit-element';
import {BaseElement} from 'base-element';

// Tag name 'jadex-platform'
class PlatformElement extends BaseElement 
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
		ret['loggedin'] = { type: Boolean };
		return ret;
	}
	
	constructor() 
	{
		super();
		console.log("platform");
		this.cid = null;
		this.plugin = null;
		this.plugins = [];
		this.loggedin = false;
		this.no = 0;
	}
	
	/*init() 
	{
		console.log("platform");
		
		this.cid = null;
		this.plugin = null;
		this.plugins = [];
		this.loggedin = false;
	}*/
	
	attributeChangedCallback(name, oldVal, newVal) 
	{
	    //console.log('attribute change: ', name, newVal, oldVal);
	    super.attributeChangedCallback(name, oldVal, newVal);
	    
	    if("cid"==name) 
		{
	    	let self = this;
	
			// Continously check platform availability
			this.checkPlatform(10000);

	    	this.loadPluginInfos().then(function()
	    	{
	    		self.loaded = true;
	    		if(self.plugin != null)
	    			self.showPlugin2({ "name" : self.plugin });
	    		else if(self.plugins.length > 0)
	    			self.showPlugin2(self.getPlugins()[0].name);
	    	}).catch(function(err) 
			{
				self.createErrorMessage("Could not load plugins", err);
				//console.log("err: "+err);
			});
	    }
	    
	    if("plugin"==name) 
		{
	    	this.plugin = newVal;
	    	if(this.loaded)
	    		this.showPlugin2(newVal);
	    }
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
		console.log("show plugin: "+name+" "+this.cid);

		let self = this;
		
		if(!this.plugins[name].unrestricted && !this.loggedin)
		{
			let html = "<jadex-restricted></jadex-restricted>";
			self.shadowRoot.getElementById("plugin").innerHTML = html;
			self.requestUpdate();
		}
		else
		{
			var pi = this.plugins[name];
		
			if(pi.component==null)
			{
				this.loadPlugin(pi.name).then(function()
				{
					let html = "<jadex-"+name+" cid='"+self.cid+"'></jadex-"+name+">";
					//console.log("Insert plugin element: " + p.name);
					self.shadowRoot.getElementById("plugin").innerHTML = html;
					//console.log("Req update: " + p.name);
					self.requestUpdate();
					//console.log("Updated1: " + name);
				}).catch(function(err)
				{
					console.log(err);
				});
			}
			else
			{
				let html = "<jadex-"+name+" cid='"+this.cid+"'></jadex-"+name+">";
				//console.log("Insert plugin element: " + p.name);
				this.shadowRoot.getElementById("plugin").innerHTML = html;
				//console.log("Req update: " + p.name);
				this.requestUpdate();
				//console.log("Updated2: " + name);
			}
		}
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
					importShim.topLevelLoad(importShim.getFakeUrl(), component).then(function()
					{
						resolve(component);
					}).catch(function(err){reject(err)})
				}
			}).catch(function(err) 
			{
				//console.log("err: "+err);	
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
		console.log("check platform: "+no+" "+this.no);
		if(no!=this.no)
		{
			console.log("terminate platform check");
			return;
		}
			
		var self = this;
		axios.get('webjcc/isPlatformAvailable?cid='+self.cid, self.transform).then(function(resp)
		{
			self.isLoggedIn().then(function()
			{
				setTimeout(function(){self.internalCheckPlatform(interval, no)}, interval!=undefined? interval: 10000);
			})
			.catch(function(err)
			{
				setTimeout(function(){self.internalCheckPlatform(interval, no)}, interval!=undefined? interval: 10000);
			});
		})
		.catch(function(err) 
		{
			console.log("platform cannot be reached: "+err);	
			//history.pushState(null, "", "/#/platforms");
			window.location.href = "/#/platforms";
		});
	}
	
	loadPluginInfos() 
	{
		var self = this;
		return new Promise(function(resolve, reject) 
		{
			axios.get('webjcc/getPluginInfos?cid='+self.cid, self.transform).then(function(resp)
			{
				//console.log("received: "+resp);	
				
				var pis = resp.data;
				//console.log(map);
				
				for(var i=0; i<pis.length; i++)
				{
					self.plugins[pis[i].name] = pis[i];
				}
				
				self.showPlugin2(self.getPlugins()[0].name);
				
			}).catch(function(err) 
			{
				//console.log("err: "+err);	
				reject(err);
			});
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
				background: rgba(0, 0, 0, 0.3); /* Black see-through */
			}
	    `;
	}
	
	getPlugins()
	{
		var self = this;
		var ret = Object.values(this.plugins).sort(function(p1, p2) 
		{
			var ret = 0;
			if(!self.loggedin)
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
	
	render() 
	{
		var self = this;
		return html`
			<h1>Platform ${this.cid}</h1>
			<div class="container-fluid">
				${this.getPlugins().map((p) => html`
					${!p.unrestricted && !this.loggedin? "": p.icon!=null? 
						html`<img class="${self.plugin===p.name? "overlay": ""}" src="data:image/png;base64,${p.icon.__base64}" alt="Red dot" @click="${(e) => {self.showPlugin2(p.name)}}" data-toggle="tooltip" data-placement="top" title="${p.name}"/>`:
						html`<span @click="${(e) => {self.showPlugin2(p.name)}}">${p.name}</span>`
					}
				`)}
			</div>
			
			<div id="plugin"></div>
		`;
	}
	
	isLoggedIn()
	{
		var self = this;
		return new Promise(function(resolve, reject) 
		{
			axios.get('webjcc/isLoggedIn', {headers: {'x-jadex-isloggedin': true}}, self.transform).then(function(resp)
			{
				console.log("is logged in: "+resp);
				self.loggedin = resp.data;
				resolve(self.loggedin);
			})
			.catch(function(err) 
			{
				console.log("check failed: "+err);	
				reject(err);
			});
		});
	}
}

if(customElements.get('jadex-platform') === undefined)
	customElements.define('jadex-platform', PlatformElement);
