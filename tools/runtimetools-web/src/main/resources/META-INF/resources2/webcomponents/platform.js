import { LitElement, html, css} from 'lit-element';

// Tag name 'jadex-platform'
class PlatformElement extends LitElement {

	static get properties() { 
		return { cid: { type: String }};
	}
	
	attributeChangedCallback(name, oldVal, newVal) {
	    console.log('attribute change: ', name, newVal, oldVal);
	    super.attributeChangedCallback(name, oldVal, newVal);
	    
	    if("cid"==name) {
	    	this.loadPlugins();
	    }
	}
	
	constructor() {
		super();

		console.log("platform");
		
		this.cid = null;
		
		this.plugins = [];
	}
	
	showPlugin(event)
	{
		// Logic for setting the active link in the navbar
		var el = $('#plugins .navbar-nav').find('li.active');
		for(var i=0; i<el.length; i++)
			el[i].classList.remove('active');
		event.target.parentElement.parentElement.classList.add("active");
		
		this.showPlugin2(event.item.p);
	}
	
	showPlugin2(p)
	{
		console.log("plugin: "+p.name+" "+this.cid);
		
		var html = "<jadex-"+p.name+" cid='"+this.cid+"'></jadex-"+p.name+">";
		this.shadowRoot.getElementById("plugin").innerHTML = html;
		this.requestUpdate();
	}
	
	loadPlugins() {
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
	            
			    if(i==1)
			    {
			    	var script = "<script type='module'>"+taghtml+"</script>";
			    	//document.getElementsByTagName("head")[0].append(script);
			    	$('head').append(script);
			    }
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
	    	/* Navbar styling. */
	    	/* background color. */
	    	.navbar-custom {
	    		background-color: #aaaaaa;
	    	}
	    	/* brand and text color */
	    	.navbar-custom .navbar-brand,
	    	.navbar-custom .navbar-text {
	    		color: rgba(255,255,255,.8);
	    	}
	    	/* link color */
	    	.navbar-custom .navbar-nav .nav-link {
	    		color: rgba(255,255,255,.5);
	    	}
	    	/* color of active or hovered links */
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
						<div class="nav-link" @click="${(e) => showPlugin(e)}"><h2>${p.name.toUpperCase()}</h2></div>
					</li>
					`)}
				</ul>
			</nav>
			
			<div id="plugin"></div>
		`;
	}
	
	switchLanguage() {
	    language.switchLanguage(); 
	    this.requestUpdate(); // needs manual update as language.lang is not mapped to an attribute 
	}
}

customElements.define('jadex-platform', PlatformElement);
