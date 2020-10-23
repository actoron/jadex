import {html} from 'lit-element';
import {css} from 'lit-element';
import {BaseElement} from '/webcomponents/baseelement.js'

//import { LitElement, html, css} from 'lit-element';

// Tag name 'jadex-componenttree'
class ComponentTree extends BaseElement 
{
	static get properties() 
	{ 
		return { cid: { type: String }};
	}
	
	constructor()
	{
		super();
		
		this.components = []; // component descriptions
		this.typemap = null;
		this.treedata = {};
		this.treeid = "componenttree";
	}
	
	init() 
	{
		super.init().then(()=>
		{
			// fixed types
			var cloud = this.getMethodPrefix()+'&methodname=loadResource&args_0=jadex/tools/web/starter/images/cloud.png';
			var applications = this.getMethodPrefix()+'&methodname=loadResource&args_0=jadex/tools/web/starter/images/applications.png';
			var platform = this.getMethodPrefix()+'&methodname=loadResource&args_0=jadex/tools/web/starter/images/platform.png';
			var system = this.getMethodPrefix()+'&methodname=loadResource&args_0=jadex/tools/web/starter/images/system.png';
			var services = this.getMethodPrefix()+'&methodname=loadResource&args_0=jadex/tools/web/starter/images/services.png';
			var provided = this.getMethodPrefix()+'&methodname=loadResource&args_0=jadex/tools/web/starter/images/provided.png';
			var required = this.getMethodPrefix()+'&methodname=loadResource&args_0=jadex/tools/web/starter/images/required.png';
			var required_mult = this.getMethodPrefix()+'&methodname=loadResource&args_0=jadex/tools/web/starter/images/required_multiple.png';
			var nonfunc = this.getMethodPrefix()+'&methodname=loadResource&args_0=jadex/tools/web/starter/images/nonfunc.png';
			var nfprop = this.getMethodPrefix()+'&methodname=loadResource&args_0=jadex/tools/web/starter/images/nfprop.png';
			var nfprop_dynamic = this.getMethodPrefix()+'&methodname=loadResource&args_0=jadex/tools/web/starter/images/nfprop_dynamic.png';
			
			this.types =
			{
				//"default" : {"icon": b},
			    "cloud" : {"icon": cloud},
			    "applications" : {"icon": applications},
			    "platform" : {"icon": platform},
			    "system" : {"icon": system},
			    "services" : {"icon": services},
			    "provided" : {"icon": provided},
			    "required" : {"icon": required},
			    "required_multiple" : {"icon": required_mult},
			    "nfproperties" : {"icon": nonfunc},
			    "nfproperty" : {"icon": nfprop},
			    "nfproperty_dynamic" : {"icon": nfprop_dynamic}
			}
			
			//console.log("components");
			
			var self = this;
			
			this.loadJSTree().then(function()
			{
				//console.log("jstree");
		
				//var myservice = "jadex.tools.web.starter.IJCCStarterService";
				//var extservice = "jadex.jadex.bridge.IExternalAccess";
				
				/*getMethodPrefix()
				{
					return 'webjcc/invokeServiceMethod?cid='+self.cid+'&servicetype='+myservice;
				}*/
				
				//var prefix = 'webjcc/invokeServiceMethod?cid='+self.cid+'&servicetype='+myservice;
				//self.prefix_ext = 'webjcc/invokeServiceMethod?cid='+self.cid+'&servicetype='+extservice;
				
				// init tree
				var types = self.types;
				$(function() { self.getTree(self.treeid).jstree(
				{
					"plugins": ["sort", "types", "contextmenu"],
					"core": 
					{
						"check_callback" : true,
						"data": function(node, cb) 
						{
							function getChildData(id)
							{
								console.log("loading node: "+id);
								
								var children = self.treedata[id+"_children"];
								var data = [];
								if(children!=null)
								{
									for(var i=0; i<children.length; i++)
									{
										var node = self.treedata[children[i]];
										data.push(node);
									}
								}
								return data;
							}
							
							// load initial state via component descriptions
							if("#"===node.id)
							{
								axios.get(self.getMethodPrefix()+'&methodname=getComponentDescriptions', self.transform).then(function(resp)
								{
									//console.log("descs are: "+resp.data);
									self.components = resp.data;
									
									self.typemap = {};
									for(var i=0; i<self.components.length; i++)
										self.typemap[self.components[i].name.name] = self.components[i].type;
	
									self.createTree(self.treeid);
									self.refreshCMSSubscription();
									self.requestUpdate();
									
									var data = getChildData(node.id);
									cb.call(this, data);
								});
							}
							// when loading other nodes, component desriptions are availble via treedata
							// nodeid = {nodevalues} for tree
							// nodeid_children = [ids] for children ids
							else
							{
								//console.log("loading node: "+node.id);
							
								// cont() fetches the child data and calls the jstree callback that fetching is finished for this node 
								function cont()
								{
									var data = getChildData(node.id);
									cb.call(this, data);
								}
								
								// create nfproperty container nodes with nfproperty children
								function createNFChildren(node, res, vals, refreshcmd)
								{
									if(res!=null && Object.keys(res).length>0)
									{
										var nfid = node.id+"_nfprops";
										var ch = [];
										
										for(var nfname in res)
										{
											var nfprop = res[nfname];
											var val = vals!=null? vals[nfname]: null;
											var txt = val!=null? nfprop.name+": "+val: nfprop.name;
											var nfnode = {"id": nfid+"_"+nfprop.name, "text": txt, 
												"type": nfprop.dynamic? "nfproperty_dynamic": "nfproperty", "children": false,
												"refreshcmd": nfprop.dynamic? refreshcmd: null, "propname": nfprop.name};
											ch.push(nfnode);
										}
										
										self.treedata[nfid] = {"id": nfid, "text": "Non-functional Properties", "type": "nfproperties", "children": ch};
										
										var key = node.id+"_children";
										var children = self.treedata[key];
										if(children==null)
										{
											children = [];
											self.treedata[key] = children;
										}
										children.push(nfid);
									}
								}
								
								// provided service node
								if(node.type==="provided")
								{
									// look for nf props (only possible children)
									// sid is saved in node data 
									
									// args IComponentIdentifier cid, IServiceIdentifier sid, MethodInfo mi, Boolean req
									var sid = encodeURIComponent(JSON.stringify(node.original.sid));
									//console.log("SID: "+sid);
									axios.get(self.getMethodPrefix()+'&methodname=getNFPropertyMetaInfos&args_0=null&args_1='+sid, self.transform)
									.then(function(resp)
									{
										//console.log("nf prov props:"+resp.data);		
									
										var res = resp.data;
										
										if(res!=null && Object.keys(res).length>0)
										{
											axios.get(self.getMethodPrefix()+'&methodname=getNFPropertyValues&args_0=null&args_1='+JSON.stringify(node.original.sid), self.transform)
											.then(function(resp)
											{
												function refresh(node) 
												{
													axios.get(self.getMethodPrefix()+'&methodname=getNFPropertyValues&args_0=null'
														+"&args_1="+JSON.stringify(node.original.sid)+"&args_2=null&args_3=null&args_4="+node.original.propname, self.transform)
													.then(function(resp)
													{
														//console.log("refresh nfnode: "+node);
														var res = resp.data;
														var val = res[node.original.propname];
														var txt = node.original.propname+": "+val;
														self.getTree(self.treeid).jstree('rename_node', node, txt);
													})
													.catch(function(e)
													{
														console.log("err in refresh nfnode: "+node);
													});
												}
												
												var vals = resp.data;
												createNFChildren(node, res, vals, refresh);	
												cont();
											})
											.catch(function(e)
											{
												createNFChildren(node, res);	
												cont();
											});
										}
										else
										{
											cont();
										}
									})
									.catch(function(e)
									{
										console.log("getNF exception: "+e);
										cont();
									});
								}
								// required service node
								else if(node.type==="required")
								{
									// look for nf props (only possible children)
									// cid is saved in node data 
									
									// args IComponentIdentifier cid, IServiceIdentifier sid, MethodInfo mi, Boolean req
									axios.get(self.getMethodPrefix()+'&methodname=getNFPropertyMetaInfos&args_0='+node.original.cid+'&args_1=null&args_2=null&args_3=true', self.transform)
									.then(function(resp)
									{
										//console.log("nf req props:"+resp.data);		
										
										var res = resp.data;
										
										if(res!=null && Object.keys(res).length>0)
										{
											axios.get(self.getMethodPrefix()+'&methodname=getNFPropertyValues&args_0='+node.original.cid+'&args_1=null&args_2=null&args_3=true', self.transform)
											.then(function(resp)
											{
												function refresh(node) 
												{
													axios.get(self.getMethodPrefix()+'&methodname=getNFPropertyValues&args_0='+node.original.cid
														+"&args_1=null&args_2=null&args_3=true&args_4="+node.original.propname, self.transform)
													.then(function(resp)
													{
														//console.log("refresh nfnode: "+node);
														var res = resp.data;
														var val = res[node.original.propname];
														var txt = node.original.propname+": "+val;
														self.getTree(self.treeid).jstree('rename_node', node, txt);
													})
													.catch(function(e)
													{
														console.log("err in refresh nfnode: "+node);
													});
												}
												
												var vals = resp.data;
												createNFChildren(node, res, vals, refresh);	
												cont();
											})
											.catch(function(e)
											{
												createNFChildren(node, res);	
												cont();
											});
										}
										else
										{
											cont();
										}
									})
									.catch(function(e)
									{
										console.log("getNF exception: "+e);
										cont();
									});
								}
								// component nodes 
								else if(node.type!=="system" && node.type!=="applications" && node.type!=="cloud")
								{
									// possible children are
									// a) nfprops
									// b) services
									// searches parallel and waits for barrier via Promise.all()
									
									// https://stackoverflow.com/questions/31069453/creating-a-es6-promise-without-starting-to-resolve-it
									var nfresolve = null;
									var promnf = new Promise(function(r, e)
									{
										nfresolve = r;
									});
									
									var serresolve = null;
									var promser = new Promise(function(r, e)
									{
										serresolve = r;
									});
									
									Promise.all([promnf, promser]).then(function(res)
									{
										cont();
									});
									
									axios.get(self.getMethodPrefix()+'&methodname=getNFPropertyMetaInfos&args_0='+node.id, self.transform)
									.then(function(resp)
									{
										//console.log("nf props:"+resp.data);	
	
										var res = resp.data;
										
										if(res!=null && Object.keys(res).length>0)
										{
											var cid = node.id;
											axios.get(self.getMethodPrefix()+'&methodname=getNFPropertyValues&args_0='+node.id, self.transform)
											.then(function(resp)
											{
												var vals = resp.data;
												
												function refresh(node) 
												{
													axios.get(self.getMethodPrefix()+'&methodname=getNFPropertyValues&args_0='+cid
														+"&args_1=null&args_2=null&args_3=null&args_4="+node.original.propname, self.transform)
													.then(function(resp)
													{
														//console.log("refresh nfnode: "+node);
														var res = resp.data;
														var val = res[node.original.propname];
														var txt = node.original.propname+": "+val;
														self.getTree(self.treeid).jstree('rename_node', node, txt);
													})
													.catch(function(e)
													{
														console.log("err in refresh nfnode: "+node);
													});
												}
												
												createNFChildren(node, res, vals, refresh);	
												nfresolve(null);
											})
											.catch(function(e)
											{
												createNFChildren(node, res);	
												nfresolve(null);
											});
										}
										else
										{
											nfresolve(null);
										}
									})
									.catch(function(e)
									{
										console.log("getNF exception: "+e);
										//promnf.resolve(null);
										nfresolve(null);
									});
									
									axios.get(self.getMethodPrefix()+'&methodname=getServiceInfos&args_0='+node.id, self.transform)
									.then(function(resp)
									{
										//console.log("service infos: "+resp.data);
										
										var res = resp.data;
										
										if(res[0].length>0 || res[1].length>0)
										{
											var serid = node.id+"_services";
			
											var ch = [];
											
											// provided
											for(var i=0; i<res[0].length; i++)
											{
												var psid = serid+"_"+res[0][i].name;
												var txt = res[0][i].type.value.split(".");
												txt = txt[txt.length-1];
												//console.log("sid: "+res[2][i]);
												var psnode = {"id": psid, "text": txt, "type": "provided", "children": true, "sid": res[2][i]};
												ch.push(psnode);
											}
											
											// required
											for(var i=0; i<res[1].length; i++)
											{
												var rsid = serid+"_"+res[1].name;
												var txt = res[1][i].type.value.split(".");
												var mult = res[1][i].multiple;
												txt = txt[txt.length-1];
												var rsnode = {"id": rsid, "text": txt, "type": mult? "required_multiple": "required", "children": true, "cid": node.id};
												ch.push(rsnode);
											}
											
											self.treedata[serid] = {"id": serid, "text": "Services", "type": "services", "children": ch};
											
											var key = node.id+"_children";
											var children = self.treedata[key];
											if(children==null)
											{
												children = [];
												self.treedata[key] = children;
											}
											children.push(serid);
										}
										
										serresolve(null);
										//promser.resolve(null);
									})
									.catch(function(e)
									{
										console.log("Err loading services for: "+node.id);
										serresolve(null);
										//promser.resolve(null);
									});
								}
								else
								{
									cont();									
								}
							}
					    }
					},
					'sort': function(a, b) 
					{
				        var a1 = this.get_node(a);
				        var b1 = this.get_node(b);
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
				        	if(node.original.refreshcmd!=null)
				        	{
				        		return { 'Refresh': 
				        			{
		                                'label': "Refresh",
		                                'action': function() 
		                                {
		                                	// todo: fix subscription refresh!
		                                	
		                                	// self.refreshCMSSubscription();
		                                	if(node.original.refreshcmd!=null)
		                                	{
		                                		//console.log("refresh cmd for: "+node.id);
		                                		node.original.refreshcmd(node);
		                                	}
		                                },
		                                'icon': self.getMethodPrefix()+'&methodname=loadResource&args_0=jadex/tools/web/starter/images/refresh.png'
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
					console.log("select: "+data.node.id);
					//self.selectModel(data.instance.get_path(data.node,'/'));
				});
				
				$('#'+treeid).on('open_node.jstree', function (e, data) 
				{
					console.log("open: "+data.node.id);
					//self.selectModel(data.instance.get_path(data.node,'/'));
				});*/
				
				/*$('#'+treeid).on('load_node.jstree', function(event, data) 
				{
					console.log("load node: "+data.node.id);
				    var treeNode = data.node;
				    if(treeNode.type === NODE_TYPE_FOLDER) 
				    {
				        domNode = fileTree.get_node(treeNode.id, true);
				        domNode.addClass('jstree-open').removeClass('jstree-leaf');
				        //data.node.state.loaded = false;
				    }
				});*/ 
				
				//self.refresh();
			});
		})
		.catch((err)=>console.log(err));
	}
		
	loadJSTree()
	{
		var self = this;
		
		return new Promise(function(resolve, reject) 
		{
			var res1 ="jadex/tools/web/starter/libs/jstree_3.3.7.css";
			var res2 = "jadex/tools/web/starter/libs/jstree_3.3.7.js";
			var ures1 = self.getMethodPrefix()+'&methodname=loadResource&args_0='+res1+"&argtypes_0=java.lang.String";
			var ures2 = self.getMethodPrefix()+'&methodname=loadResource&args_0='+res2+"&argtypes_0=java.lang.String";
	
			//console.log("jstree load files start");
			
			var p1 = self.loadStyle(ures1);
			var p2 = self.loadScript(ures2);
			
			Promise.all([p1, p2]).then((values) => 
			{
				//console.log("js tree load files ok");
				resolve();
			})
			.catch(err => 
			{
				console.log("js tree load files err: "+err);
				reject(err);
			});
		});
	}
	
	getMethodPrefix() 
	{
		return 'webjcc/invokeServiceMethod?cid='+this.cid+'&servicetype=jadex.tools.web.starter.IJCCStarterService';
	}
	
	getTree(treeid)
	{
		return $("#"+treeid, this.shadowRoot);
	}
	
	refreshCMSSubscription()
	{
		//console.log("refreshCMSSubscription");
		
		var self = this;
		
		var path = self.getMethodPrefix()+'&methodname=subscribeToComponentChanges&returntype=jadex.commons.future.ISubscriptionIntermediateFuture';

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
			self.termcom = jadex.getIntermediate(path,
				function(resp)
				{
					var event = resp.data;
					console.log("cms status event: "+event);
					
					if(event.type.toLowerCase().indexOf("created")!=-1)
					{
						self.typemap[event.componentDescription.name.name] = event.componentDescription.type;
						self.createNodes(self.treeid, event.componentDescription, false);
					}
					else if(event.type.toLowerCase().indexOf("terminated")!=-1)
					{
						try
						{
							self.deleteNode(self.treeid, event.componentDescription.name.name);
						}
						catch(ex)
						{
							console.log("Err: "+ex);
							console.log("Could not remove node: "+event.componentDescription.name.name);
						}
					}
					
					self.getTree(self.treeid).jstree("refresh");
				},
				function(err)
				{
					console.log("error occurred: "+err);
				}
			);
		}
	}
	
	createTree(treeid)
	{
		this.empty(treeid);
					
		for(var i=0; i<this.components.length; i++)
		{
			//console.log(self.models[i]);
			this.createNodes(treeid, this.components[i], false);
		}
	}
	
	empty(treeid)
	{
		// $('#'+treeid).empty(); has problem when reading nodes :-(

		var roots = this.getTree(treeid).jstree().get_node('#').children;
		for(var i=0; i<roots.length; i++)
		{
			this.getTree(self.treeid).jstree('delete_node', roots[i]);
		}
	}
	
	// create node(s) for a component description
	createNodes(treeid, component, createnode)
	{
		var self = this;
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
			
			//if(!$('#'+treeid).jstree('get_node', names[i]))
			if(self.treedata[names[i]]==null)
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
				
				if(self.types[type]==null)
					icon = self.getMethodPrefix()+'&methodname=loadComponentIcon&args_0='+type;
				//types[type] = self.getMethodPrefix()+'&methodname=loadResource&args_0=jadex/tools/web/starter/images/language_de.png';

				//console.log(cid+" "+type+" "+icon);
				
				if(createnode)
					self.createNode(treeid, lastname, names[i], name, 'last', type, icon);
				else
					self.createNodeData(names[i], name, type, icon, lastname);
			}
			//else
			//	console.log("not creating: "+names[i]);
			
			lastname = names[i];
		}
	}
	
	// createNode(parent, id, text, position), position 'first' or 'last'
	createNode(treeid, parent_node_id, new_node_id, new_node_text, position, type, icon)//, donefunc) 
	{
		console.log("parent="+parent_node_id+" child="+new_node_id+" childtext="+new_node_text);
		console.log("create node: "+new_node_id);
		var n = {"text": new_node_text, "id": new_node_id}; //"children": true  "state": {"opened": false}
		if(type!=null)
			n.type = type;
		if(icon!=null)
			n.icon = icon;
		this.getTree(self.treeid).jstree('create_node', '#'+parent_node_id, n, 'last');	
	}
		
	createNodeData(id, name, type, icon, parent)
	{
		this.treedata[id] = {"id": id, "text": name, "type": type, "icon": icon, "children": true};
		var key = parent==null || parent.length==0? "#_children": parent+"_children";
		var children = this.treedata[key];
		if(children==null)
		{
			children = [];
			this.treedata[key] = children;
		}
		children.push(id);
	}
		
	deleteNode(treeid, nodeid)
	{
		console.log("remove node: "+nodeid);
		this.getTree(treeid).jstree("delete_node", nodeid);
		//var apps4 = this.getTree(treeid).jstree().get_node(this.cid+'_Services');
		var apps = this.getTree(treeid).jstree().get_node(this.cid+'_Applications');
		if(apps!=false && apps.children.length==0)
			this.getTree(treeid).jstree("delete_node", this.cid+"_Applications");
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
	    `;
	}
	
	render() {
		return html`
			<div class="container-fluid m-0 p-0">
				<div class="row m-0 p-0">
					<div class="col-12 m-0 p-0">
						<div id="componenttree"></div>
					</div>
				</div>
				
				${this.components.length==0? html`
				<div class="row m-0 p-0">
					<div class="col-12 m-0 p-0">
				 		<div class="loader"></div> 
				 	</div>
				 </div>
				 ` : ''}
			</div>
		`;
	}
	
	switchLanguage() 
	{
	    language.switchLanguage(); 
	    this.requestUpdate(); // needs manual update as language.lang is not mapped to an attribute 
	}
}


if(customElements.get('jadex-componenttree') === undefined)
	customElements.define('jadex-componenttree', ComponentTree);

