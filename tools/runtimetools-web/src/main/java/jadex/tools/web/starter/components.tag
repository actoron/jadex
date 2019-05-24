<components>
	<div class="container-fluid">
		<div class="row m-1" hide="{components.length==0}">
			<div class="col-12 m-1">
				<div id="componenttree"></div>
			</div>
		</div>
		<div class="row m-1" hide="{components.length==0}">
			<div class="col-10">
			</div>
			<div class="col-2">
				<button class="float-right" onclick="{refresh}">Refresh</button>
			</div>
		</div>
		<div class="row m-1" show="{components.length==0}">
			<div class="col-12">
		 		<div class="loader"></div> 
		 	</div>
		 </div>
	</div>
	
	<style>
		.w100 {
			width: 100%;
		}
		.loader {
			border: 8px solid #f3f3f3;
			border-top: 8px solid #070707; 
			border-radius: 50%;
			width: 60px;
			height: 60px;
			animation: spin 2s linear infinite;
		}
		@keyframes spin {
  			0% { transform: rotate(0deg); }
  			100% { transform: rotate(360deg); }
		}
	</style>
	
	<script>
		console.log("components: "+opts);
		
		//console.log(this.getLanguage());
		
		var self = this;

		self.cid = opts!=null? opts.cid: null;
		self.components = []; // component descriptions
		
		var treeid = "componenttree";
		
		var myservice = "jadex.tools.web.starter.IJCCStarterService";
		
		getMethodPrefix()
		{
			return 'webjcc/invokeServiceMethod?cid='+self.cid+'&servicetype='+myservice;
		}
		
		refresh()
		{
			axios.get(self.getMethodPrefix()+'&methodname=getComponentDescriptions', self.transform).then(function(resp)
			{
				//console.log("descs are: "+resp.data);
				self.components = resp.data;
				createTree(treeid);
				//$("#"+treeid).jstree('open_all');
				$("#"+treeid).jstree("open_node", $('#'+self.cid));
				self.update();
			});
		}
		
		function createTree(treeid)
		{
			empty(treeid);
			
			for(var i=0; i<self.components.length; i++)
			{
				//console.log(self.models[i]);
				createNodes(treeid, self.components[i]);
			}
		}
		
		function empty(treeid)
		{
			// $('#'+treeid).empty(); has problem when reading nodes :-(

			var roots = $('#'+treeid).jstree().get_node('#').children;
			for(var i=0; i<roots.length; i++)
			{
				$('#'+treeid).jstree('delete_node', roots[i]);
			}
		}
		
		function createNodes(treeid, component)
		{
			var cid = component.name.name; // todo: better json format?!
			var parts = cid.split("@");
			var name = parts[0];
			
			var names = [];
			
			names.unshift(cid);
			
			// if not only platform cid
			if(parts.length>1)
			{
				var rest = parts[1];
				var parents = rest.split(":");
				
				for(var i=0; i<parents.length; i++)
				{
					var name = parents[i];
					for(var j=i+1; j<parents.length; j++)
					{
						if(j==i+1)
							name += "@";
						else
							name += ":";
						name += parents[j];
					}
					if(name.indexOf("@")==-1)
					{
						if(component.systemComponent)
							names.unshift("System");
						else
							names.unshift("Applications");
					}
					
					names.unshift(name);
				}
			}
			
			var lastname = '';
			for(var i=0; i<names.length; i++)
			{
				var name = names[i].split("@")[0];
				
				if(!$('#'+treeid).jstree('get_node', names[i]))
					createNode(treeid, lastname, names[i], name, 'last');
				//else
				//	console.log("not creating: "+names[i]);
				
				lastname = names[i];
			}
		}
		
		// createNode(parent, id, text, position), position 'first' or 'last'
		function createNode(treeid, parent_node_id, new_node_id, new_node_text, position)//, donefunc) 
		{
			//console.log("parent="+parent_node_id+" child="+new_node_id+" childtext="+new_node_text);
			$('#'+treeid).jstree('create_node', '#'+parent_node_id, {"text": new_node_text, "id": new_node_id }, 'last');	
		}
		
		var res1 ="jadex/tools/web/starter/libs/jstree_3.3.7.css";
		var res2 = "jadex/tools/web/starter/libs/jstree_3.3.7.js";
		var ures1 = self.getMethodPrefix()+'&methodname=loadResource&args_0='+res1+"&argtypes_0=java.lang.String";
		var ures2 = self.getMethodPrefix()+'&methodname=loadResource&args_0='+res2+"&argtypes_0=java.lang.String";

		// dynamically load jstree lib and style
		//self.loadFiles(["libs/jstree_3.2.1.min.css", "libs/jstree_3.2.1.min.js"], function()
		self.loadFiles([ures1], [ures2], function()
		{
			self.refresh();
			
			// init tree
			$(function() { $('#'+treeid).jstree(
			{
				"plugins": ["sort"],
				"core": {"check_callback" : true},
				'sort': function(a, b) 
				{
			        a1 = this.get_node(a);
			        b1 = this.get_node(b);
			        if(a1.icon == b1.icon)
			        {
			            return (a1.text > b1.text) ? 1 : -1;
			        } 
			        else 
			        {
			            return (a1.icon > b1.icon) ? 1 : -1;
			        }
				}
			})});
			
			/*self.on('mount', function()
			{
			    //console.log("adding listener");
				$('#'+treeid).on('select_node.jstree', function (e, data) 
				{
					self.selectModel(data.instance.get_path(data.node,'/'));
				});
			});*/
			
			//self.update();
		})
	</script>
</components>