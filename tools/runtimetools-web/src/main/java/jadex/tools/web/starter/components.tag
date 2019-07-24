<components>
	<div class="container-fluid m-0 p-0">
		<div class="row m-0 p-0" hide="{components.length==0}">
			<div class="col-12 m-0 p-0">
				<div id="componenttree"></div>
			</div>
		</div>
		<!-- <div class="row m-0 p-0" hide="{components.length==0}">
			<div class="col-10 m-0 p-0">
			</div>
			<div class="col-2 m-0 p-0">
				<button class="float-right" onclick="{refresh}">Refresh</button>
			</div>
		</div> -->
		<div class="row m-0 p-0" show="{components.length==0}">
			<div class="col-12 m-0 p-0">
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
		//console.log(this.getLanguage());
		
		var self = this;

		// https://github.com/riot/riot/issues/874
		// riot seems to automount this tag without opts :-(
		//self.cid = opts!=null? opts.cid: null;
		if(opts!=null && opts.cid!=null)
			self.cid = opts.cid;
		if(self.cid==null && self.parent!=null && self.parent.opts!=null)
			self.cid = self.parent.opts.cid;
		
		self.components = []; // component descriptions
		self.typemap = null;
	
		console.log("components: "+self.cid);
		
		//if(self.cid==null)
		//	console.log("cid is null");
		
		var treeid = "componenttree";
		
		var myservice = "jadex.tools.web.starter.IJCCStarterService";
		
		/*getMethodPrefix()
		{
			return 'webjcc/invokeServiceMethod?cid='+self.cid+'&servicetype='+myservice;
		}*/
		
		self.prefix = 'webjcc/invokeServiceMethod?cid='+self.cid+'&servicetype='+myservice;
		
		refresh()
		{
			// Reload the component descriptions
			axios.get(self.prefix+'&methodname=getComponentDescriptions', self.transform).then(function(resp)
			{
				//console.log("descs are: "+resp.data);
				self.components = resp.data;
				
				self.typemap = {};
				for(var i=0; i<self.components.length; i++)
				{
					// Can only be resolved as long as modelName is available
					if("jadex.platform.service.remote.ProxyAgent"==self.components[i].modelName)
						self.typemap[self.components[i].name.name] = "platform";
					else 
						self.typemap[self.components[i].name.name] = self.components[i].type;
				}
				
				self.createTree(treeid);
				//$("#"+treeid).jstree('open_all');
				$("#"+treeid).jstree("open_node", $('#'+self.cid));
				self.update();
			});
			
			self.refreshCMSSubscription();
		}
		
		refreshCMSSubscription()
		{
			//console.log("refreshCMSSubscription");
			
			var path = self.prefix+'&methodname=subscribeToComponentChanges&returntype=jadex.commons.future.ISubscriptionIntermediateFuture';

			if(self.termcom!=null)
				self.termcom().then(done).catch(err);
				//self.termcom("refreshing").then(done).catch(err);
			else
				done();
			
			function err(err)
			{
				console.log(err);
				done();
			}
			
			function done()
			{
				self.termcom = self.getIntermediate(path,
					function(resp)
					{
						var event = resp.data;
						//console.log("cms status event: "+event);
						if(event.type.toLowerCase().indexOf("created")!=-1)
						{
							self.typemap[event.componentDescription.name.name] = event.componentDescription.type;
							self.createNodes(treeid, event.componentDescription);
						}
						else if(event.type.toLowerCase().indexOf("terminated")!=-1)
						{
							try
							{
								self.deleteNode(treeid, event.componentDescription.name.name);
							}
							catch(ex)
							{
								console.log("Could not remove node: "+event.componentDescription.name.name);
							}
						}
					},
					function(err)
					{
						console.log("error occurred: "+err);
					}
				);
			}
		}
		
		// fixed types
		var cloud = self.prefix+'&methodname=loadResource&args_0=jadex/tools/web/starter/images/cloud.png';
		var applications = self.prefix+'&methodname=loadResource&args_0=jadex/tools/web/starter/images/applications.png';
		var platform = self.prefix+'&methodname=loadResource&args_0=jadex/tools/web/starter/images/platform.png';
		var system = self.prefix+'&methodname=loadResource&args_0=jadex/tools/web/starter/images/system.png';
		
		var types =
		{
			//"default" : {"icon": b},
		    "cloud" : {"icon": cloud},
		    "applications" : {"icon": applications},
		    "platform" : {"icon": platform},
		    "system" : {"icon": system}
		}
		
		createTree(treeid)
		{
			self.empty(treeid);
						
			for(var i=0; i<self.components.length; i++)
			{
				//console.log(self.models[i]);
				self.createNodes(treeid, self.components[i]);
			}
		}
		
		empty(treeid)
		{
			// $('#'+treeid).empty(); has problem when reading nodes :-(

			var roots = $('#'+treeid).jstree().get_node('#').children;
			for(var i=0; i<roots.length; i++)
			{
				$('#'+treeid).jstree('delete_node', roots[i]);
			}
		}
		
		// create node(s) for a component description
		createNodes(treeid, component)
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
						{
							names.unshift("System");
						}
						else if("jadex.platform.service.remote.ProxyAgent"==component.modelName) // Hack?!
						{
							names.unshift("Cloud");
						}
						else
						{
							//console.log("creating App: "+name+" "+cid);
							names.unshift("Applications");
						}
					}
					
					names.unshift(name);
				}
			}
			
			var lastname = '';
			for(var i=0; i<names.length; i++)
			{
				var parts = names[i].split("@");
				var name = parts[0];
				
				if(!$('#'+treeid).jstree('get_node', names[i]))
				{
					var type = self.typemap[names[i]];
					var icon = null;
					
					if(type!=null && parts.length==1)
						type = "platform";
					
					if(type==null)
					{
						if("Cloud"==name)
							type = "cloud";
						else if("Applications"==name)
							type = "applications";
						else if("System"==name) 
							type = "system";
					}
					
					if(types[type]==null)
						icon = self.prefix+'&methodname=loadComponentIcon&args_0='+type;
					//types[type] = self.getMethodPrefix()+'&methodname=loadResource&args_0=jadex/tools/web/starter/images/language_de.png';

					//console.log(cid+" "+type+" "+icon);
					
					self.createNode(treeid, lastname, names[i], name, 'last', type, icon);
				}
				//else
				//	console.log("not creating: "+names[i]);
				
				lastname = names[i];
			}
		}
		
		// createNode(parent, id, text, position), position 'first' or 'last'
		createNode(treeid, parent_node_id, new_node_id, new_node_text, position, type, icon)//, donefunc) 
		{
			//console.log("parent="+parent_node_id+" child="+new_node_id+" childtext="+new_node_text);
			//console.log("create node: "+new_node_id);
			var n = {"text": new_node_text, "id": new_node_id};
			if(type!=null)
				n.type = type;
			if(icon!=null)
				n.icon = icon;
			$('#'+treeid).jstree('create_node', '#'+parent_node_id, n, 'last');	
		}
			
		deleteNode(treeid, nodeid)
		{
			//console.log("remove node: "+nodeid);
			$("#"+treeid).jstree("delete_node", nodeid);
			var apps = $('#'+treeid).jstree().get_node('Applications');
			if(apps!=null && apps.children.length==0)
				$("#"+treeid).jstree("delete_node", "Applications");
		}
		
		var res1 ="jadex/tools/web/starter/libs/jstree_3.3.7.css";
		var res2 = "jadex/tools/web/starter/libs/jstree_3.3.7.js";
		var ures1 = self.prefix+'&methodname=loadResource&args_0='+res1;
		var ures2 = self.prefix+'&methodname=loadResource&args_0='+res2;

		// dynamically load jstree lib and style
		//self.loadFiles(["libs/jstree_3.2.1.min.css", "libs/jstree_3.2.1.min.js"], function()
		self.loadFiles([ures1], [ures2], function()
		{
			self.refresh();
		});
		
		self.on('mount', function()
		{
			//console.log("mounted components");
			
			// init tree
			$(function() { $('#'+treeid).jstree(
			{
				"plugins": ["sort", "types", "contextmenu"],
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
				},
				types,
				'contextmenu' : 
				{
			        'items': function menu(node) 
					{
			        	if(node.id.indexOf("@")==-1 && "System"!=node.id && "Applications"!=node.id)
			        	{
			        		return { 'Refresh': 
			        			{
	                                'label': "Refresh",
	                                'action': function() {self.refreshCMSSubscription();},
	                                'icon': self.prefix+'&methodname=loadResource&args_0=jadex/tools/web/starter/images/refresh.png'
			        			} 
			        		};
			        	}
			        	else
			        	{
			        		return null;
			        	}
					}
			    }
			})});
			
		    //console.log("adding listener");
			/*$('#'+treeid).on('select_node.jstree', function (e, data) 
			{
				self.selectModel(data.instance.get_path(data.node,'/'));
			});*/
			
			//self.refresh();
		});
		
		self.on('unmount', function()
		{
			console.log('unmounted: '+self);
			if(self.termcom!=null)
				self.termcom("unmounted");
		});
	</script>
</components>