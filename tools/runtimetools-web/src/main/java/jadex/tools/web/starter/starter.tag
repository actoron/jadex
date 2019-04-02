<starter>
	<div class="container-fluid">
		<div class="sticky-top" style="background-color: rgba(255,255,255,0.8)">
			<div class="row">
				<div class="col-12">
					<h3>Available Component Models</h3>
				</div>
			</div>
			<div class="row">
				<div class="col-11 p-1">
					<input type="text" ref="filename" style="width: 100%">
				</div>
				<div class="col-1 p-1">
					<button class="float-right" style="width: 100%" onclick="{start}">Start</button>
				</div>
			</div>
		</div>
		<div class="row">
			<div class="col-12">
				<div id="modeltree"></div>
			</div>
		</div>
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

		var treeid = "modeltree";
		
		$(function() { $('#'+treeid).jstree(
		{
			"core" : {"check_callback" : true}//,
			//"plugins" : ["dnd","contextmenu"]
		})});
		
		var self = this;
		self.cid = opts!=null? opts.cid: null;
		self.models = [];
		self.selected = null;
		
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
			createModelTree(treeid);
			$("#modeltree").jstree('open_all');
			self.update();
		});
		
		this.on('mount', function()
		{
		    console.log("adding listener");
			$('#'+treeid).on('select_node.jstree', function (e, data) 
			{
				selected = data.instance.get_path(data.node,'/');
				self.refs.filename.value = selected;
				//console.log('Selected: ' + selected); 
			});
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
		
		start(e)
		{
			console.log(e+" "+selected);
			if(selected!=null)
			{
				axios.get(self.getMethodPrefix()+'&methodname=createComponent&args_0='+selected+"&argtypes_0=java.lang.String", self.transform).then(function(resp)
				{
					console.log("started: "+resp.data);
				});
			}
		}
		
		function createModelTree(treeid)
		{
			empty(treeid);
			
			for(var i=0; i<self.models.length; i++)
			{
				//console.log(self.models[i]);
				createNodes(treeid, self.models[i]);
			}
		}
		
		function empty(treeid)
		{
			// $('#'+treeid).empty(); has problem when readding nodes :-(

			var roots = $('#'+treeid).jstree().get_node('#').children;
			for(var i=0; i<roots.length; i++)
			{
				$('#'+treeid).jstree('delete_node', roots[i]);
			}
		}
		
		function createNodes(treeid, model)
		{
			var sep = "/";
			if(model.indexOf("\\")!=-1)
				sep = "\\";
			var parts = model.split(sep);
			
			var lastprefix = '';
			var prefix = parts[0];
			
			for(var i=0; i<parts.length; i++)
			{
				prefix = !lastprefix? parts[i]: lastprefix+sep+parts[i];
				if(!$('#'+treeid).jstree('get_node', prefix))
					createNode(treeid, lastprefix, prefix, parts[i], 'last');
				//else
				//	console.log("not creating: "+prefix);
				lastprefix = prefix;
			}
		}
		
		// createNode(parent, id, text, position), position 'first' or 'last'
		function createNode(treeid, parent_node_id, new_node_id, new_node_text, position)//, donefunc) 
		{
			//console.log("parent="+parent_node_id+" child="+new_node_id+" childtext="+new_node_text);
			$('#'+treeid).jstree('create_node', '#'+parent_node_id, {"text": new_node_text, "id": new_node_id }, 'last');	
		}
	</script>
</starter>