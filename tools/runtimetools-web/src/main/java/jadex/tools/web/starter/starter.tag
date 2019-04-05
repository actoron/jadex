<starter>
	<div class="container-fluid">
		<div class="sticky-top bgwhitealpha m-2 p-2">
			<div class="row m-1">
				<div class="col-12">
					<h3>Settings</h3>
				</div>
			</div>
			<div class="row m-1">
				<div class="col-2">
					Filename
				</div>
				<div class="col-10">
					<input type="text" ref="filename" class="w100">
				</div>
			</div>
			<div class="row m-1">
				<div class="col-2">
					Configuration
				</div>
				<div class="col-10">
					<select ref="config" class="w100">
   						<option each="{c in getConfigurationNames()}" value="{c}"> {c}</option> <!--  selected="{config==c}" -->
 					</select>
				</div>
			</div>
			<div class="row m-1">
				<div class="col-2">
					Comp. name
				</div>
				<div class="col-5">
					<input type="text" class="w100" value="{model!=null? model.instancename: ''}" ref="name"></input>
				</div>
				<div class="col-3">
					<input type="checkbox" ref="autogen">Auto generate</input>
				</div>
				<div class="col-2">
					<input class="w100" type="number" value="1" ref="gencnt"></input>
				</div>
			</div>
			<div hide="getArguments().length>0" class="row m-1" each="{arg, i in getArguments()}">
				<div class="col-4"">
					{"["+arg.clazz.value+"] "+arg.name}
				</div>
				<div class="col-4 p-0">
					<input class="w100" type="text" value="{arg.value}" readonly></input>
				</div>
				<div class="col-4 pl-2"> <!-- ref="{'arg_'+i} -->
					<input class="w100" type="text" id="{'arg_'+i}">
				</div>
			</div>
			<div class="row m-1">
				<div class="col-10">
				</div>
				<div class="col-2">
					<button class=" float-right" onclick="{start}">Start</button> <!-- class="w100" -->
				</div>
			</div>
		</div>
		<div class="row m-1">
			<div class="col-12">
				<h3>Available Models</h3>
			</div>
		</div>
		<div class="row m-1" hide="{models.length==0}">
			<div class="col-12">
				<div id="modeltree"></div> <!-- class="scroll" -->
			</div>
		</div>
		<div class="row m-1" show="{models.length==0}">
			<div class="col-12">
		 		<div class="loader"></div> 
		 	</div>
		 </div>
	</div>
	
	<style>
	    #modellist {
			display: block;
			height : 300px;
			overflow-y : scroll;
		}
		.bgwhitealpha {
			background-color: rgba(255,248,208,0.8);
		}
		.w100 {
			width: 100%;
		}
		.scroll {
  			height:150px;
  			overflow-y: scroll;
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
	
	<!-- how to load external js/style for tag
	<script src="https://cdnjs.cloudflare.com/ajax/libs/jstree/3.2.1/jstree.min.js"></script>-->
	
	<script>
		//console.log("starter: "+opts);
	
		var self = this;
		self.cid = opts!=null? opts.cid: null;
		self.models = [];
		self.selected = null;
		self.model = null;
		
		var treeid = "modeltree";
		
		$(function() { $('#'+treeid).jstree(
		{
			"core" : {"check_callback" : true}//,
			//"plugins" : ["dnd","contextmenu"]
		})});
		
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
		    //console.log("adding listener");
			$('#'+treeid).on('select_node.jstree', function (e, data) 
			{
				selected = data.instance.get_path(data.node,'/');
				self.refs.filename.value = selected;
				
				axios.get(self.getMethodPrefix()+'&methodname=loadComponentModel&args_0='+selected+"&argtypes_0=java.lang.String", self.transform).then(function(resp)
				{
					console.log("model is: "+resp.data);
					self.model = resp.data;
					self.update();
				});

				self.update();
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
		
		getConfigurationNames()
		{
			var ret = [];
			if(self.model!=null)
			{
				if(self.model.configurations!=null)
				{
					for(var i=0; i<self.model.configurations.length; i++)
					{
						if(i==0)
							ret.push("");
						ret.push(self.model.configurations[i].name);
					}
				}
			}
			return ret;
		}
		
		getArguments()
		{
			return self.model!=null? self.model.arguments: [];
		}
		
		start(e)
		{
			console.log(e+" "+selected);
			if(selected!=null)
			{
				var conf = self.refs.config.options[e.selectedIndex]!=null? self.refs.config.options[e.selectedIndex].value: null;
				var gen = self.refs.autogen.checked;
				var name = self.refs.name.value;
				var gencnt = self.refs.gencnt.value;
				
				var args = {};
				if(self.model!=null && self.model.arguments!=null)
				{
					for(var i=0; i<self.model.arguments.length; i++)
					{
						var el = document.getElementById('arg_'+i);
						var argval = el.value;
						console.log('arg_'+i+": "+argval);
					}
				}
				
				var ci = {configname: conf, name: name, filename: self.model.filename};
				
				//axios.get(self.getMethodPrefix()+'&methodname=createComponent&args_0='+selected+"&argtypes_0=java.lang.String", self.transform).then(function(resp)
				axios.get(self.getMethodPrefix()+'&methodname=createComponent&args_0='+JSON.stringify(ci)+"&argtypes_0=jadex.bridge.service.types.cms.CreationInfo", self.transform).then(function(resp)
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