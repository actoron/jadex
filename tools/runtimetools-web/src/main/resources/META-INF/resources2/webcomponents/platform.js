import {LitElement, html, css} from 'lit-element';
import {BaseElement} from 'base-element';

// Tag name 'jadex-platform'
class PlatformElement extends BaseElement 
{

	static get properties() 
	{ 
		return { cid: { type: String }};
	}
	
	attributeChangedCallback(name, oldVal, newVal) 
	{
	    console.log('attribute change: ', name, newVal, oldVal);
	    super.attributeChangedCallback(name, oldVal, newVal);
	    
	    if("cid"==name) {
	    	this.loadPlugins();
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
	}
	
	showPlugin2(p)
	{
		let lcname = p.name.toLowerCase(); 
		console.log("plugin: "+lcname+" "+this.cid);
		
		let html = "<jadex-"+lcname+" cid='"+this.cid+"'></jadex-"+lcname+">";
		console.log('tag is ' + html)
		this.shadowRoot.getElementById("plugin").innerHTML = html;
		this.requestUpdate();
	}
	
	loadPlugins() 
	{
		var self = this;
		axios.get('webjcc/getPluginFragments?cid='+this.cid, this.transform).then(function(resp)
		{
			//console.log("received: "+resp);	
			
			var map = resp.data;
			//console.log(map);
			
			var i = 0;
			Object.keys(map).forEach(function(tagname) 
			{
			    var taghtml = map[tagname];
				
			    self.plugins[i] = {name: tagname, html: taghtml};
			    
			    i++;
			    
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
		    	importShim.topLevelLoad(importShim.getFakeUrl(), taghtml).then(x => console.log(x));
			});
						
			if(i>0)
				self.showPlugin2(self.plugins[0]);
			else
				self.requestUpdate();
			
			//return PROMISE_DONE;
			
		}).catch(function(err) 
		{
			console.log("err: "+err);	
			//return this.PROMISE_DONE;
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
