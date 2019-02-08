<platform>

	<h1>Platform {opts!=null && opts.paths!=null && opts.paths.length>1? opts.paths[1]: ""}</h1>

	<ul class="nav">
		<li each="{p in plugins}" class="nav-item">
    		<div class="nav-link" onclick="{parent.showPlugin}">{p.name}</div>
  		</li>
	</ul>
	
	<div id="plugin"></div>

	<script>
		var self = this;
		self.plugins = [];
		
		showPlugin(event)
		{
			var p = event.item.p;
			
			riot.tag(p.name, p.html);
			
			var tags = riot.mount("div#plugin", p.name);
			
			self.update();
		}
		
		axios.get('webjcc/getPluginFragments', self.transform).then(function(resp)
		{
			console.log("received: "+resp);	
			
			var map = resp.data;
			console.log(map);
			
			var i = 0;
			Object.keys(map).forEach(function(tagname) 
			{
			    var taghtml = map[tagname];
				
			    self.plugins[i] = {name: tagname, html: taghtml};
			    
			    i++;
			});
			
			self.update();
			
		}).catch(function(err) 
		{
			console.log("err: "+err);	
		});
		//console.log(opts.paths);
	</script>
</platform>

