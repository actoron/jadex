<platform>

	<h1>Platform {cid}</h1>

	<ul class="nav">
		<li each="{p in plugins}" class="nav-item">
    		<div class="nav-link" onclick="{parent.showPlugin}">{p.name}</div>
  		</li>
	</ul>
	
	<div id="plugin"></div>

	<script>
		//var test = "<cms><h1>Starter</h1><script>alert('hiiiiiii')<\/script><\/cms>";
	
		var self = this;
		self.plugins = [];
		self.cid = opts!=null && opts.paths!=null && opts.paths.length>1? opts.paths[1]: "";
		
		var curplugin = null;
		
		showPlugin(event)
		{
			if(curplugin!=null)
				curplugin.unmount(true);
			
			var p = event.item.p;
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
			
			self.update();
			return this.PROMISE_DONE;
			
		}).catch(function(err) 
		{
			console.log("err: "+err);	
			return this.PROMISE_DONE;
		});
		
		self.update();
		//console.log(opts.paths);
	</script>
</platform>

