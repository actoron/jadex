import {LitElement, html, css} from 'lit-element';
import {BaseElement} from 'base-element';

// Tag name 'jadex-platform'
class PlatformElement extends BaseElement 
{
	loaded = false;
	plugin = null;

	static get properties() 
	{
		var ret = {};
		if(super.properties!=null)
		{
			for (let key in super.properties)
				ret[key]=super.properties[key];
		}
		ret['plugin'] = { type: String }
		return ret;
	}
	
	attributeChangedCallback(name, oldVal, newVal) 
	{
	    console.log('attribute change: ', name, newVal, oldVal);
	    super.attributeChangedCallback(name, oldVal, newVal);
	    
	    if("cid"==name) {
	    	let self = this;
	    	this.loadPlugins().then(function()
	    	{
	    		self.loaded = true;
	    		if (self.plugin != null)
	    			self.showPlugin2({ "name" : self.plugin });
	    		else if (self.plugins.length > 0)
	    			self.showPlugin2(self.plugins[0]);
	    	}).catch(function(err) 
			{
				console.log("err: "+err);
			});
	    }
	    
	    if ("plugin"==name) {
	    	this.plugin = newVal;
	    	if (this.loaded)
	    		this.showPlugin2({ "name" : newVal });
	    }
	}
	
	constructor() 
	{
		super();

		console.log("platform");
		
		this.cid = null;
		
		this.plugins = [];
	}
	
	showPlugin(event)
	{
		// Logic for setting the active link in the navbar
		var el = $('#plugins .navbar-nav').find('li.active');
		var sel = event.target.parentElement.parentElement;
		for(var i=0; i<el.length; i++)
			el[i].classList.remove('active');
		sel.classList.add("active");
		
		this.showPlugin2(event.item);
		history.pushState(null, "", "/#/platform/"+this.cid+"/"+event.item.name);
	}
	
	showPlugin2(p)
	{
		let lcname = p.name.toLowerCase(); 
		console.log("plugin: "+lcname+" "+this.cid);
		
		let html = "<jadex-"+lcname+" cid='"+this.cid+"'></jadex-"+lcname+">";
		console.log("Insert plugin element: " + p.name);
		this.shadowRoot.getElementById("plugin").innerHTML = html;
		console.log("Req update: " + p.name);
		this.requestUpdate();
		console.log("Updated: " + p.name);
	}
	
	loadPlugins() 
	{
		var self = this;
		return new Promise(function(resolve, reject) 
		{
			axios.get('webjcc/getPluginFragments?cid='+self.cid, self.transform).then(function(resp)
			{
				//console.log("received: "+resp);	
				
				var map = resp.data;
				//console.log(map);
				
				var i = 0;
				let promises = [];
				Object.keys(map).forEach(function(tagname) 
				{
				    var taghtml = map[tagname];
					
				    self.plugins[i] = {name: tagname, html: taghtml};
				    
				    //var script = document.createElement('script');
		            //script.type = 'text/javascript';
		            //script.src = files[i];
		            
			    	//var script = "<script type='module'>"+taghtml+"</script>";
			    	//document.getElementsByTagName("head")[0].append(script);
			    	//$('head').append(script);
			    	/* let script = document.createElement("script");
			    	script.type='module-shim';
			    	script.innerHTML=taghtml;
			    	alert(taghtml);
			    	document.head.append(script); */
				    if (!taghtml.startsWith('<'))
				    {
					    i++;
				    	let promise = importShim.topLevelLoad(importShim.getFakeUrl(), taghtml);
				    	promises.push(promise);
				    }
				});
				
				Promise.all(promises).then(function(x)
				{
					if(i>0)
						self.showPlugin2(self.plugins[0]);
					else
						self.requestUpdate();
					resolve();
				});
				
				//return PROMISE_DONE;
				
			}).catch(function(err) 
			{
				console.log("err: "+err);	
				reject(err);
			});
		});
	}
	
	static get styles() {
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
	    `;
	}
	
	render() {
		return html`
			<h1>Platform ${this.cid}</h1>

			<nav id="plugins" class="navbar navbar-expand-sm navbar-light bg-light">
				<ul class="navbar-nav mr-auto">
					${this.plugins.map((p) => html`
					<li class="nav-item active">
						<div class="nav-link" @click="${(e) => {e.item = p; this.showPlugin(e)}}"><h2>${p.name}</h2></div>
					</li>
					`)}
				</ul>
			</nav>
			
			<div id="plugin"></div>
		`;
	}
}

customElements.define('jadex-platform', PlatformElement);
