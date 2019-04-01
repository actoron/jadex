<starter>

	<!-- <h2>Starter {cid}</h2>  -->
	
	<div class="container">
		<h3>Available Component Models</h3>
		<table class="table table-bordered" class="modellist">
			<thead>
	    		<tr>
	    			<th>Model name</th>
	    		</tr>
	    	</thead>
			<tbody>
				<div id="modeltree">
					<ul>
						<li>Root node 1
				        	<ul>
				          		<li id="child_node_1">Child node 1</li>
				          		<li>Child node 2</li>
				        	</ul>
				      	</li>
				      	<li>Root node 2</li>
				    </ul>
				</div>
				
				<tr each="{x in orderBy(models)}">
					<td><a href="{getMethodPrefix()+'&methodname=createComponent&args_0='+x+'&argtypes_0=java.lang.String'}">{x}</a></td>
				</tr>
			</tbody>
		</table>
	</div>
	
	<style>
	    #modellist {
			display: block;
			height : 300px;
			overflow-y : scroll;
		}
	</style>
	
	<!-- how to load external js/style for tag
	<script src="https://cdnjs.cloudflare.com/ajax/libs/jstree/3.2.1/jstree.min.js"></script>-->
	
	<script>
		//console.log("starter: "+opts);
		// +'&contenttype=application/json'
		
		$(function() { $('#modeltree').jstree(
		{
			"core" : {"check_callback" : true}//,
			//"plugins" : ["dnd","contextmenu"]
		})});
		
		var self = this;
		self.cid = opts!=null? opts.cid: null;
		self.models = [];
		
		var myservice = "jadex.tools.web.starter.IJCCStarterService";
		
		getMethodPrefix()
		{
			return 'webjcc/invokeServiceMethod?cid='+self.cid+'&servicetype='+myservice;
		}
		
		// no args here
		axios.get(self.getMethodPrefix()+'&methodname=getComponentModels', self.transform).then(function(resp)
		{
			//console.log(resp.data);
			
			self.models = resp.data;
			createModelTree();
			self.update();
		});
			
		self.update();
		
		// todo: order by name
		orderBy(data) 
		{ 
			var order = self.reversed ? -1 : 1;
			
			var res = data.slice().sort(function(a, b) 
			{ 
				return a===b? 0: a > b? order: -order 
			});
			
			return res; 
		}
		
		function createModelTree()
		{
			$('#modeltree').empty();
			
			createNodes('modeltree', self.models[0]);
			
			/*for(i=0; i<self.models.length; i++)
			{
				if(self.models[i].indexOf("\\")!=-1)
					sep = "\\";
				var parts = self.models[i].split(sep);
				
				var lastprefix = 'modeltree';
				var prefix = '';
				for(j=0; j<parts.length; j++)
				{
					prefix = lastprefix+"_"+parts[j];
					if(document.getElementById(prefix)==null)
					{
						createNode('modeltree', lastprefix, prefix, parts[j], 'last');
					}
					else
					{
						console.log("exists: "+prefix);
					}
					lastprefix = prefix;
				}
			}*/
		}
		
		function createNodes(treeid, model)
		{
			alert("here");
			
			if(model.indexOf("\\")!=-1)
				sep = "\\";
			var parts = model.split(sep);
			
			var lastprefix = treeid;
			var prefix = '';
			
			var cnt = 0;
			
			var func = function()
			{
				console.log("donefunc");
				lastprefix = prefix;
				if(++cnt<parts.length)
				{
					prefix = lastprefix+"_"+parts[cnt];
					createNode(treeid, lastprefix, prefix, parts[cnt], 'last', this);
				}
			};
			
			createNode(treeid, lastprefix, prefix, parts[cnt], 'last', func);
		}
		
		// createNode(parent, id, text, position), position 'first' or 'last'
		function createNode(treeid, parent_node_id, new_node_id, new_node_text, position, donefunc) 
		{
			var pa = document.getElementById(parent_node_id);
			console.log('modeltree'+" "+pa+" "+parent_node_id+" "+new_node_id+" "+new_node_text);
			$('#'+treeid).jstree().create_node(pa, {"text": new_node_text, "id": new_node_id }, 'last', donefunc);	
		}
	</script>
</starter>