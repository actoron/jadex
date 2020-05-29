import { LitElement, html, css } from 'lit-element';

// Defined as <jadex-platform> tag
class PlatformElement extends LitElement {

	constructor() {
		super();
		
		console.log("platform");
		
		var self = this;
		self.plugins = [];
		self.cid = opts!=null && opts.paths!=null && opts.paths.length>1? opts.paths[1]: "";
		
		var curplugin = null;
		
		showPlugin(event)
		{
			// Logic for setting the active link in the navbar
			var el = $('#plugins .navbar-nav').find('li.active');
			for(var i=0; i<el.length; i++)
				el[i].classList.remove('active');
			event.target.parentElement.parentElement.classList.add("active");
			
			self.showPlugin2(event.item.p);
		}
		
		showPlugin2(p)
		{
			if(curplugin!=null)
				curplugin.unmount(true);
			
			//console.log("tag and mount: "+p.name+" "+self.cid+" "+p.html);
			riot.compile(p.html);
			//riot.tag(p.name, p.html);
			
			var tags = riot.mount("div#plugin", p.name, {cid: self.cid});
			curplugin = tags[0];
			
			self.update();
		}
		
		axios.get('webjcc/getPluginFragments?cid='+self.cid, self.transform).then(function(resp)
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
			});
						
			if(i>0)
				self.showPlugin2(self.plugins[0]);
			else
				self.requestUpdate();
			
			return this.PROMISE_DONE;
			
		}).catch(function(err) 
		{
			console.log("err: "+err);	
			//return this.PROMISE_DONE;
		});
		
		this.requestUpdate();
		//console.log(opts.paths);
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
			<h1>Platform {cid}</h1>

			<nav id="plugins" class="navbar navbar-expand-sm navbar-light bg-light">
				<ul class="navbar-nav mr-auto">
					<li each="{p in plugins}" class="nav-item active">
			    		<div class="nav-link" onclick="{parent.showPlugin}"><h2>{p.name.toUpperCase()}</h2></div>
			  		</li>
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
