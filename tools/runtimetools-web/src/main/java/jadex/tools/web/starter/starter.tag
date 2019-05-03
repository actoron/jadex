<starter>
	<div class="container-fluid">
		<div hide="{model==null}" class="sticky-top bgwhitealpha m-2 p-2">
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
			<div class="row m-1">
				<div class="col-4">
					<input type="checkbox" ref="suspended">Suspended</input>
				</div>
				<div class="col-4">
					<input type="checkbox" ref="synchronous">Synchronous</input>
				</div>
				<div class="col-4">
					<select ref="monitoring" class="w100">
   						<option value="OFF">OFF</option> 
   						<option value="COARSE">COARSE</option> 
   						<option value="MEDIUM">MEDIUM</option> 
   						<option value="FINE">FINE</option> 
 					</select>
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
			<div class="col-12 m-1">
				<input class="w100" type="text" list="models" onchange="{select}" ref="modelchooser"></input>
				<datalist id=models>
					<option class="w100" each="{model in getModelNames()}" value="{model.name+' ['+model.pck+']'}"></option>
				</datalist>
			</div>
			<div class="col-12 m-1">
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
		
		console.log(this.getLanguage());
		
		var self = this;

		self.cid = opts!=null? opts.cid: null;
		self.models = []; // available component models [filename, classname]
		self.model = null; // loaded model
		
		var treeid = "modeltree";
		
		var myservice = "jadex.tools.web.starter.IJCCStarterService";
		
		getMethodPrefix()
		{
			return 'webjcc/invokeServiceMethod?cid='+self.cid+'&servicetype='+myservice;
		}
		
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
		
		getModelNames()
		{
			var ret = [];
			if(self.models.length>0)
			{
				for(var i=0; i<self.models.length; i++)
				{
					var n = self.models[i][1].lastIndexOf(".");
					if(n>=0)
					{
						ret.push({name: self.models[i][1].substring(n+1), pck: self.models[i][1].substring(0,n)});
					}
					else
					{
						ret.push({name: self.models[i][1], pck: null});
					}
				}
			}
			return ret;
		}
		
		selectModel(filename)
		{
			self.refs.filename.value = filename;
			//self.update();
			
			axios.get(self.getMethodPrefix()+'&methodname=loadComponentModel&args_0='+filename+"&argtypes_0=java.lang.String", self.transform).then(function(resp)
			{
				console.log("model is: "+resp.data);
				self.model = resp.data;
				self.update();
			});
		}
		
		select(e)
		{
			// does not work :-(
			//var sel = self.refs.modelchooser.selectionStart;
			var sel = self.refs.modelchooser.value;
			
			var opts = self.refs.modelchooser.list.options;
			var idx = -1;
			for(var i=0; i<opts.length; i++)
			{
				if(opts[i].value==sel)
				{
					idx = i;
					break;
				}
			}
			console.log(idx);
			
			if(idx>-1)
			{
				var filename = self.models[idx][0];
				self.selectModel(filename);
			}
		}
		
		start(e)
		{
			if(self.model!=null)
			{
				var conf = self.refs.config.value;
				var sync = self.refs.synchronous.checked;
				var sus = self.refs.suspended.checked;
				var mon = self.refs.monitoring.value;
				
				var gen = self.refs.autogen.checked;
				var gencnt = self.refs.gencnt.value;
				var name = self.refs.name.value;

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
				
				var ci = {filename: self.model.filename};
				if(conf!=null && conf.length>0)
					ci.configuration = conf;
				ci.synchronous = sync;
				ci.suspended = sus;
				ci.monitoring = mon;
				if(name!=null && name.length>0)
					ci.name = name;
				
				//axios.get(self.getMethodPrefix()+'&methodname=createComponent&args_0='+selected+"&argtypes_0=java.lang.String", self.transform).then(function(resp)
				//console.log("starting: "+ci);
				axios.get(self.getMethodPrefix()+'&methodname=createComponent&args_0='+JSON.stringify(ci)+"&argtypes_0=jadex.bridge.service.types.cms.CreationInfo", self.transform).then(function(resp)
				{
					// todo: show running components?!
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
				createNodes(treeid, self.models[i][1]);
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
			var sep = ".";
			//var sep = "/";
			//if(model.indexOf("\\")!=-1)
			//	sep = "\\";
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
		
		var res1 ="jadex/tools/web/starter/libs/jstree_3.3.7.css";
		var res2 = "jadex/tools/web/starter/libs/jstree_3.3.7.js";
		var ures1 = self.getMethodPrefix()+'&methodname=loadResource&args_0='+res1+"&argtypes_0=java.lang.String";
		var ures2 = self.getMethodPrefix()+'&methodname=loadResource&args_0='+res2+"&argtypes_0=java.lang.String";

		//console.log(ures1);
		//console.log(ures2);
		
		// dynamically load jstree lib and style
		//self.loadFiles(["libs/jstree_3.2.1.min.css", "libs/jstree_3.2.1.min.js"], function()
		self.loadFiles([ures1], [ures2], function()
		{
			// init tree
			$(function() { $('#'+treeid).jstree(
			{
				"core" : {"check_callback" : true}//,
				//"plugins" : ["dnd","contextmenu"]
			})});
			
			// no args here
			axios.get(self.getMethodPrefix()+'&methodname=getComponentModels', self.transform).then(function(resp)
			{
				//console.log(resp.data);
				self.models = resp.data;
				createModelTree(treeid);
				$("#modeltree").jstree('open_all');
				self.update();
			});
			
			self.on('mount', function()
			{
			    //console.log("adding listener");
				$('#'+treeid).on('select_node.jstree', function (e, data) 
				{
					self.selectModel(data.instance.get_path(data.node,'/'));
				});
			});
			
			//self.update();
		})
	</script>
</starter>