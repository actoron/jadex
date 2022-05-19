let { LitElement, html, css } = modLoad('lit-element');
let { BaseElement } = modLoad('base-element');

// Tag name 'jadex-componenttree'
class ComponentTree extends BaseElement 
{
	static get properties() 
	{ 
		return { cid: { type: String }};
	}
	
	postInit()
	{
		// make details dragable
		this.dragElement(this.shadowRoot.getElementById("details"));
	}
	
	init() 
	{
		//console.log("component tree");
		
		this.typemap = null;
		this.treedata = {};
		this.treedata["#"] = {};
		this.treeid = "componenttree";
		this.info = null;
		//this.commands = [];

		// fixed types
		var cloud = this.getMethodPrefix()+'&methodname=loadResource&args_0=jadex/tools/web/commons/images/cloud.png';
		var applications = this.getMethodPrefix()+'&methodname=loadResource&args_0=jadex/tools/web/commons/images/applications.png';
		var platform = this.getMethodPrefix()+'&methodname=loadResource&args_0=jadex/tools/web/commons/images/platform.png';
		var system = this.getMethodPrefix()+'&methodname=loadResource&args_0=jadex/tools/web/commons/images/system.png';
		var services = this.getMethodPrefix()+'&methodname=loadResource&args_0=jadex/tools/web/commons/images/services.png';
		var provided = this.getMethodPrefix()+'&methodname=loadResource&args_0=jadex/tools/web/commons/images/provided.png';
		var required = this.getMethodPrefix()+'&methodname=loadResource&args_0=jadex/tools/web/commons/images/required.png';
		var required_mult = this.getMethodPrefix()+'&methodname=loadResource&args_0=jadex/tools/web/commons/images/required_multiple.png';
		var nonfunc = this.getMethodPrefix()+'&methodname=loadResource&args_0=jadex/tools/web/commons/images/nonfunc.png';
		var nfprop = this.getMethodPrefix()+'&methodname=loadResource&args_0=jadex/tools/web/commons/images/nfprop.png';
		var nfprop_dynamic = this.getMethodPrefix()+'&methodname=loadResource&args_0=jadex/tools/web/commons/images/nfprop_dynamic.png';
		
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
						//console.log("in: "+node+" "+mycnt);
						
						function getChildData(id)
						{					
							var children = self.getChildData(id);
							// problem: js tree changes data structures :-( give jstree only a clone?
							//console.log("children of: "+id+" "+children.length);
							return JSON.parse(JSON.stringify(children));
						}
						
						function loadComponentChildData(cid)
						{
							return new Promise(function(r, e)
							{
								var url = self.getMethodPrefix()+'&methodname=getChildComponentDescriptions&args_0=null&args_1='+cid;
								//console.log("fetching: "+url);
								axios.get(url, self.transform).then(function(resp)
								{
									//console.log("descs are: "+resp.data);
									var components = resp.data;
									
									self.typemap = {};
									for(var i=0; i<components.length; i++)
										self.typemap[components[i].name.name] = components[i].type;
	
									self.createTree(self.treeid, components);
									r();
								}).catch(err => e(err));
							});
						}
						
						// cont() fetches the child data and calls the jstree callback that fetching is finished for this node 
						function cont()
						{
							var data = getChildData(node.id);
							//console.log("out3: "+node+" "+mycnt);
							cb.call(this, data);
						}
						
						//console.log("loading node: "+node.id);
						// create nfproperty container nodes with nfproperty children
						function createNFChildren(node, res, vals, refreshcmd)
						{
							//console.log("createNFChildren: "+node.id+" "+(refreshcmd!=null));
							
							if(res!=null && Object.keys(res).length>0)
							{
								var nfid = node.id+"_nfprops";
								//var ch = [];
								
								// parent node nfprops
								var nfpropsnode =  {"id": nfid, "text": "Non-functional Properties", "type": "nfproperties", "children": true};
								self.addChildData(node.id, nfpropsnode)
								
								for(var nfname in res)
								{
									var nfprop = res[nfname];
									var val = vals!=null? vals[nfname]: null;
									nfprop.val = val;
									if(nfprop.dynamic && refreshcmd)
										nfprop.refreshcmd = refreshcmd;
									var txt = val!=null? nfprop.name+": "+val: nfprop.name;
									var nfnode = {"id": nfid+"_"+nfprop.name, "text": txt, 
										"type": nfprop.dynamic? "nfproperty_dynamic": "nfproperty", "children": false,
										"refreshcmd": nfprop.dynamic? refreshcmd: null, "propname": nfprop.name, info: nfprop};
									//ch.push(nfnode);
									self.addChildData(nfid, nfnode);
									//console.log("nfprop: "+nfname+" "+nfprop.dynamic+" "+nfnode.refreshcmd);
								}
							}
						}
						
						// load initial state via component descriptions
						if("#"===node.id)
						{
							loadComponentChildData(self.cid).then(()=>
							{
								self.refreshCMSSubscription();
								self.requestUpdate();
								
								var data = getChildData(node.id);
								//console.log("out1: "+node+" "+mycnt);
								
								cb.call(this, data);
							}).catch(err=>
							{
								console.log(err);
								var data = getChildData(node.id);
								//console.log("out2: "+node+" "+mycnt);
								cb.call(this, data);
							});
						}
						// provided service node
						else if(node.type==="provided")
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
												
												//console.log("refresh nfnode: "+node+" "+val);
												
												// store new data also in node info
												self.treedata[node.id].info.val = res[node.original.propname];
												
												self.getTree(self.treeid).jstree('rename_node', node, txt);
												self.refreshDetails(node);
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
										console.log("getNFProps err: "+e);
										createNFChildren(node, res);	
										cont();
									});
								}
								else
								{
									console.log("nonfprops");
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
												
												console.log("refresh nfnode: "+node+" "+val);
												
												// store new data also in node info
												self.treedata[node.id].info.val = res[node.original.propname];

												self.getTree(self.treeid).jstree('rename_node', node, txt);
												self.refreshDetails(node);
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
						else if(node.type==="default" || node.type==="platform")
						//else if(node.type!=="system" && node.type!=="applications" && node.type!=="cloud")
						{
							//console.log("loading comp node: "+node.type+" "+node.id);
							// possible children are
							// a) nfprops
							// b) services
							// c) subcomponents
							// searches parallel and waits for barrier via Promise.all()
							
							// https://stackoverflow.com/questions/31069453/creating-a-es6-promise-without-starting-to-resolve-it
							// can fail
							var promc = loadComponentChildData(node.id);

							// nfresolve and serresolve are always called (also on err)
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
							
							Promise.all([promnf, promser, promc]).then(function(res)
							{
								cont();
							})
							.catch(e => 
							{
								console.log("err: "+e);
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
												var res = resp.data;
												var val = res[node.original.propname];
												var txt = node.original.propname+": "+val;
												
												console.log("refresh nfnode: "+node+" "+val);

												// store new data also in node info
												self.treedata[node.id].info.val = res[node.original.propname];

												self.getTree(self.treeid).jstree('rename_node', node, txt);
												self.refreshDetails(node);
											})
											.catch(function(e)
											{
												console.log("err in refresh nfnode: "+e);
											});
										}
										
										createNFChildren(node, res, vals, refresh);	
										nfresolve(null);
									})
									.catch(function(e)
									{
										console.log("err is: "+e);
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
	
									var sernode = {"id": serid, "text": "Services", "type": "services", "children": true};
									self.addChildData(node.id, sernode);
									
									// provided
									for(var i=0; i<res[0].length; i++)
									{
										var psid = serid+"_"+res[0][i].name;
										var txt = res[0][i].type.value.split(".");
										txt = txt[txt.length-1];
										//console.log("sid: "+res[2][i]);
										var psnode = {"id": psid, "text": txt, "type": "provided", "children": true, "sid": res[2][i], "info": res[0][i]};
										self.addChildData(serid, psnode);
									}
									
									// required
									for(var i=0; i<res[1].length; i++)
									{
										var rsid = serid+"_"+res[1].name;
										var txt = res[1][i].type.value.split(".");
										var mult = res[1][i].multiple;
										txt = txt[txt.length-1];
										var rsnode = {"id": rsid, "text": txt, "type": mult? "required_multiple": "required", "children": true, "cid": node.id, "info": res[1][i]};
										self.addChildData(serid, rsnode);
									}
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
				},
				'sort': function(a, b) 
				{
			        var a1 = this.get_node(a);
			        var b1 = this.get_node(b);
			  		var ret = 0;   
					if(a1.icon == b1.icon)
			        {
			            ret = (a1.text > b1.text) ? 1 : -1;
			        } 
			        else 
			        {
			            ret = (a1.icon > b1.icon) ? 1 : -1;
			        }
					//if((a.text!=null && a.text.indexOf("App")!=-1) || (b.text!=null && b.text.indexOf("App")!=-1))
					//	console.log("sort: "+a+" "+b+" "+ret);
					return ret;
				},
				types,
				'contextmenu' : 
				{
			        'items': function menu(node) 
					{
						var menu = null;
						
						if(self.treedata[node.id].refreshcmd!=null)
			        	//if(node.original.refreshcmd!=null)
			        	{
							if(menu==null)
								menu = {};
							
			        		menu.Refresh = 
		        			{
                                'label': "Refresh",
                                'action': function() 
                                {
                                	// todo: fix subscription refresh!
                                	
                                	// self.refreshCMSSubscription();
									self.treedata[node.id].refreshcmd(node);
                                	//if(node.original.refreshcmd!=null)
                                	//{
                                		//console.log("refresh cmd for: "+node.id);
                                	//	node.original.refreshcmd(node);
                                	//}
                                },
                                'icon': self.getMethodPrefix()+'&methodname=loadResource&args_0=jadex/tools/web/commons/images/refresh.png'
		        			};
			        	}

						if(self.isComponentNode(node.type))
						{
							if(menu==null)
								menu = {};
							
			        		menu.Kill = 
		        			{
                                'label': "Kill",
                                'action': function(obj) 
                                {
									//console.log(obj);
                                	//console.log("kill me: "+node.id);
                                	self.killComponent(node.id);
                                },
                                'icon': self.getMethodPrefix()+'&methodname=loadResource&args_0=jadex/tools/web/commons/images/kill.png'
		        			};

							if(self.commands!=null)
							{
								for(const command of self.commands) 
								{ 
									const cmd = Object.assign({}, command);
									cmd.node = node;
									menu[command.label] = cmd;
								}
							}
						}
						
						if(menu!=null)
						{
							menu.Details = 
		        			{
	                            'label': "Details",
	                            'action': function(obj) 
	                            {
									//console.log(obj);
	                            	//console.log("kill me: "+node.id);
	                            	//self.killComponent(node.id);
									self.refreshDetails(node);
	                            },
	                            'icon': self.getMethodPrefix()+'&methodname=loadResource&args_0=jadex/tools/web/commons/images/details.png'
		        			};
						}

			        	return menu;
					}
			    }
			})});
			
			/*self.getTree(self.treeid).on("select_node.jstree", function(evt, data)
			{
				self.refreshDetails(data.node);
			});*/
			
			self.getTree(self.treeid).bind("dblclick.jstree", function (event) 
			{
				//console.log("double");
				var node = self.getTree(self.treeid).jstree("get_selected", true)[0];
				self.refreshDetails(node);
			});
			
			/*self.getTree(self.treeid).bind("dblclick.jstree", function (event) 
			{
   				var node = $(event.target).closest("li");
  				var data = node.data("jstree");
				console.log("dclick: "+data);
			});*/
			
			/*self.getTree(self.treeid).on('refresh.jstree', function (event) 
			{
				console.log("refreshed tree: "+event);
			});*/
			
			// Open root node after node has been loaded asyncronously
			self.getTree(self.treeid).on('load_node.jstree', function (event, args) 
			{
				//console.log("loaded node: "+event+" "+args);
				
				if(args.node.id==="#")
				{
					var childs = self.getTree(self.treeid).jstree('get_node', '#').children;
					for(var i=0; i<childs.length; i++)
					{
						self.getTree(self.treeid).jstree("open_node", childs[i]);
					}
				}
			});
			
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
	}
	
	// when node is selected
	// creates a 'global' info object from node details as saved in treedata
	// jstree node data cannot contain user data
	// the info object is then read by the details/property panel
	refreshDetails(node)
	{
		var self = this;
		
		//console.log("selected: "+node+" "+node.type);
		if(self.treedata[node.id]==null)
		{
			self.info=null;
			self.requestUpdate();
			return;
		}	
		
		var type = node.type;
		var info = self.treedata[node.id].info;
		var created = false;
		
		if(type=="default") 
		{
			var cci = (info) =>
			{
				self.info = {type: type};
				self.info.heading = "Component Details";
				self.info.name = info.name.name;
				self.info.ctype = info.type;
				self.info.model = info.modelName;
				self.info.creator = info.creator?.name;
				self.info.system = info.systemComponent;
				self.info.state = info.state;
				self.info.monitoring = info.monitoring?.value;
			}
			cci(info);
			created = true;
			
			var infocopy = self.info;
			var load = n =>
			{
				return new Promise(function(r, e)
				{
					//console.log("refresh: "+info.name.name);
					axios.get(self.getMethodPrefix()+'&methodname=getComponentDescription&arg0='+info.name.name, self.transform).then(function(resp)
					{
						//console.log("desc is: "+resp.data);
						var oldcmd = self.info.refreshcmd;
						var info = resp.data;
						if(infocopy===self.info)
						{
							cci(info);
							self.info.refreshcmd = oldcmd;
						}
					}).catch(e => console.log(e));
				});
			}
			self.info.refreshcmd = load;
		}
		else if(type=="required")
		{
			self.info = {type: type};
			self.info.heading = "Required Service Details";
			self.info.name = info.name;
			self.info.type = info.type.value;
			self.info.min = info.min;
			self.info.max = info.max;
			self.info.tags = info.tags.toString();
			self.info.searchscope = info.defaultBinding?.scope?.value;
			self.info.proxytype = info.defaultBinding?.proxytype;
			if(info.refreshcmd)
				self.info.refreshcmd = info.refreshcmd;
			created = true;
		}
		else if(type=="provided")
		{
			self.info = {type: type};
			self.info.heading = "Provided Service Details";
			self.info.name = info.name;
			self.info.type = info.type.value;
			self.info.scope = info.scope.value;
			self.info.systemservice = info.systemService;
			if(info.refreshcmd)
				self.info.refreshcmd = info.refreshcmd;
			created = true;
		}
		else if(type.startsWith("nfproperty"))
		{
			self.info = {type: type};
			self.info.heading = "Non-Functional Property Details";
			self.info.name = info.name;
			self.info.type = info.type.value;
			self.info.dynamic = info.dynamic;
			if(info.unit)
				self.info.unit = info.unit.value;
			if(info.updateRate && info.updateRate!=-1)
				self.info.updateRate = info.updateRate;
			self.info.value = info.val;
			if(info.refreshcmd)
				self.info.refreshcmd = info.refreshcmd;
			created = true;
		}
		
		if(created)
		{
			if(info!=null)
				self.info.node = node;
		}
		
		self.requestUpdate();
	}
	
	killComponent(cid)
	{
		var self = this;
		
		var paid = self.treedata[cid]._parent;
		
		return new Promise(function(r, e)
		{
			axios.get(self.getMethodPrefix()+'&methodname=killComponent&arg0='+cid, self.transform).then(function(resp)
			{
				//console.log("killed: "+cid);
				self.createInfoMessage("Killed component "+cid); 
				if(paid!=null)
				{
					self.getTree(self.treeid).jstree().refresh_node(paid);
				}
				else
				{
					self.getTree(self.treeid).jstree("refresh");
				}
				r();
			}).catch(err => {console.log("err kill: "+cid); e(err);});
		});
	}
	
	loadJSTree()
	{
		var self = this;
		
		return new Promise(function(resolve, reject) 
		{
			var res1 ="jadex/tools/web/commons/libs/jstree_3.3.7.css";
			var res2 = "jadex/tools/web/commons/libs/jstree_3.3.7.js";
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
	
	createTree(treeid, components)
	{
		//this.empty(treeid);
		for(var i=0; i<components.length; i++)
		{
			//console.log(self.models[i]);
			this.createNodes(treeid, components[i]);//, false);
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
	createNodes(treeid, component)//, createnode)
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
				
				if(type==null)
				{
					if("Cloud"==name)
						type = "cloud";
					else if("Applications"==name)
						type = "applications";
					else if("System"==name) 
						type = "system";
					//else if(type!=null && parts.length==1)
					else if(name===parts[parts.length-1])
						type = "platform";
				}
				
				if(self.types[type]==null)
					icon = self.getMethodPrefix()+'&methodname=loadComponentIcon&args_0='+type;

				//console.log(cid+" "+type+" "+icon);
				
				self.createNodeData(names[i], name, type, icon, lastname, 
					i+1<names.length? null: component);
			}
			//else
			//	console.log("not creating: "+names[i]);
			
			lastname = names[i];
		}
	}
	
	isComponentNode(type)
	{
		return type!=="nfproperties" 
			&& type!=="provided"
			&& type!=="required"
			&& type!=="system"
			&& type!=="applications"
			&& type!=="cloud"
			&& type!=="services";
	}
	
	createNodeData(id, name, type, icon, parent, info)
	{
		//console.log("create node data: "+id+" "+name+" "+type+" "+parent);
		var paid = parent==null || parent.length==0? "#": parent;
		// must use _parent as parent is already used by jstree
		var node = {"id": id, "text": name, "type": type, "icon": icon, "children": true, "_parent": paid, "info": info}; // "original": {'hello': true}
		var key = parent==null || parent.length==0? "#": parent;
		
		this.addChildData(key, node);
	}
		
	deleteNodeData(nodeid)
	{
		// delete a component node:
		// a) delete the node data itself delete this.treedata[nodeid];
		// b) delete the child data of that node delete this.treedata[nodeid+"_children"];
		// c) delete the node from the children of its parent
		// d) delete Applications if it was the last child
		// e) delete Applications children
		// f) delete Applications from Applications parent 
		
		//console.log("remove node data: "+nodeid);
		this.removeChildDataFromParent(nodeid);
		delete this.treedata[nodeid];
		//delete this.treedata[nodeid].children;

		var ac = this.getChildData("Applications");
		if(ac==null || ac.length==0)
		{
			this.removeChildDataFromParent("Applications");
			delete this.treedata["Applications"];
			//delete this.treedata["Applications_children"]
		}
	}
	
	removeChildDataFromParent(nodeid)
	{
		var removed = false;
		var paid = this.treedata[nodeid]?._parent;
		//this.getTree(treeid).jstree().get_parent(nodeid);
		if(paid!=null)
		{
			var pachilds = this.treedata[paid]?._children
			if(pachilds!=null)
			{
				var i = pachilds.indexOf(nodeid);
  				if(i!=-1)
				{
    				pachilds.splice(i, 1);
					removed = true;
					console.log("removed: "+nodeid);
				}
			}
		}
		
		if(!removed)
			console.log("not removed from parent: "+nodeid);
	}
	
	refreshCMSSubscription()
	{
		//console.log("refreshCMSSubscription");
		
		var self = this;
		
		var path = self.getMethodPrefix()+'&methodname=subscribeToComponentChanges&returntype=jadex.commons.future.ISubscriptionIntermediateFuture';

		if(self.callid!=null)
		{
			jadex.terminateCall(self.callid)
				.then(() => {done();})
				.catch(err => {console.log("Could not terminate subscription: "+err+" "+self.callid)});
		}
		else
		{
			done();
		}
		
		function err(err)
		{
			console.log(err);
			done();
		}
		
		function done()
		{
			self.callid = jadex.getIntermediate(path,
				function(resp)
				{
					var event = resp.data;
					//console.log("cms status event: "+event);
					
					if(event.type.toLowerCase().indexOf("created")!=-1)
					{
						self.typemap[event.componentDescription.name.name] = event.componentDescription.type;
						self.createNodes(self.treeid, event.componentDescription);//, false);
					}
					else if(event.type.toLowerCase().indexOf("terminated")!=-1)
					{
						try
						{
							//console.log("delete a node");
							self.deleteNodeData(event.componentDescription.name.name);
							if(self.info?.node?.id===event.componentDescription.name.name)
							{
								self.info = null;
								self.requestUpdate();
							}
						}
						catch(ex)
						{
							console.log("Err: "+ex);
							console.log("Could not remove node: "+event.componentDescription.name.name);
						}
					}
					
					// determine what is to be refreshed
					// normally 'Applications' node
					// when last app has been deleted or first app has been created -> root refresh 
					
					var hasapp = self.getTree(self.treeid).jstree().get_node("Applications")!=false;
					var ac = self.getChildData("Applications");
					var shouldhaveapp = ac!=null && ac.length>0;
					
					if(hasapp!=shouldhaveapp)
					{
						//console.log("refresh all: "+hasapp+" "+shouldhaveapp);
						self.getTree(self.treeid).jstree("refresh");
					}
					else
					{
						//console.log("refresh app: "+hasapp+" "+shouldhaveapp);
						self.getTree(self.treeid).jstree().refresh_node("Applications");
						self.getTree(self.treeid).jstree().refresh_node("System");
					}
				},
				function(err)
				{
					console.log("error occurred: "+err);
				}
			);
		}
	}
	
	getProps()
	{
		var ret = [];
		if(this.info!=null)
		{
			ret = Object.keys(this.info).sort();
			// remove all commands
			for(var i=ret.length-1; i>=0; i--)
			{
				if(ret[i].toLowerCase().endsWith("cmd") 
					|| ret[i].toLowerCase()==="node"
					|| ret[i].toLowerCase()==="heading"
					|| ret[i].toLowerCase()==="hidden"
				)
				{
					ret.splice(i, 1);
				}
			}
		}
		return ret;
	}
	
	getChildIds(id)
	{
		if(this.treedata[id]==null)
			console.log("node not found: "+id);
			
		var cids = this.treedata[id]?._children;
		return cids;
	}
	
	getChildData(id)
	{
		if(this.treedata[id]==null)
			console.log("node not found: "+id);
			
		var cids = this.treedata[id]?._children;
		var ret = [];
		
		if(cids!=null)
		{
			ret = [];
			for(var cid of cids)
			{
				ret.push(this.treedata[cid]);
			}
		}
		
		return ret;
	}
	
	setChildData(id, data)
	{
		if(this.treedata[id]==null)
			console.log("node not found: "+id);
		
		var ids = [];
		for(var d of data)
		{
			c.push(d.id);
			this.treedata[d.id] = d;
		}
		this.treedata[id]._children = ids;
	}
	
	addChildData(id, data)
	{
		if(this.treedata[id]==null)
			console.log("node not found: "+id);
			
		var cids = this.treedata[id]._children;
		if(cids==null)
		{
			cids = [];
			this.treedata[id]._children = cids;
		}
		this.treedata[data.id] = data;
		if(cids.indexOf(data.id)==-1)
			cids.push(data.id);
	}
	
	setCommands(commands)
	{
		//console.log("commands set to: "+commands);
		this.commands = commands;
	}
	
	// methods for making dragable an element
	dragElement(element) 
	{
		var x1 = 0;
		var y1 = 0;
		var x2 = 0; 
		var y2 = 0;

		var moved = e =>
		{
			e = e || window.event;
	    	e.preventDefault();
	    	x1 = x2 - e.clientX;
	    	y1 = y2 - e.clientY;
	    	x2 = e.clientX;
	    	y2 = e.clientY;
			//console.log("to: "+x2+" "+y2);
	    	// set the element's new position:
			//var y = parseInt(element.style.top) || 0;
	    	//var x = parseInt(element.style.left) || 0;
			element.style.top = element.offsetTop-y1+"px";
	    	element.style.left = element.offsetLeft-x1+"px";
		}

		var md = e => 
		{
			//console.log("offsetx: "+e.offsetX+" "+e.clientX);
			
			if(element.offsetWidth-e.offsetX < 20 && element.offsetHeight-e.offsetY < 20)
			{
				//console.log("at resize border");
				return;
	    	}

			e = e || window.event;
	    	e.preventDefault();
			x2 = e.clientX;
	    	y2 = e.clientY;
			//console.log("from: "+x2+" "+y2);
			
			// clean up document mouse listeners after mouse released
			document.addEventListener("mouseup", e =>
			{
				document.removeEventListener("mouseup", this);
				document.removeEventListener("mousemove", moved);
			});
			
			// watch now for movements
			document.addEventListener("mousemove", moved);
	  	}

		// listen on mouse clicks on that element
		element.addEventListener("mousedown", md);
	}
	
	static get styles() 
	{
	    return css`
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
			.dragable {
				padding: 10px;
				position: absolute;
				top: 0px;
				left: 70%;
				width: 30%;
			  	background-color: #00000011;
			  	border: 1px solid #d3d3d3;
				z-axis: 1;
		 		resize: both;
    			overflow: hidden;
			}
			.grid {
				display: grid;
				grid-template-columns: auto 1fr;
				grid-gap: 10px;
			}
			.marginbottom {
				margin-top: 0px;
				margin-left: 0px;
				margin-right: 0px;
				margin-bottom: 0.5em;
			}
	    `;
	}
	
	asyncRender() 
	{
		return html`
			<div id="componenttree"></div>
			<div id="details" class="dragable ${this.info!=null? 'visible': 'hidden'}">
				<div class="close absolute" @click="${e => {this.info=null; this.requestUpdate();}}"></div>
				<h4 class="marginbottom">${this.info!=null? this.info.heading: ""}</h4>
				<div class="grid marginbottom">
					${this.getProps().map(propname => html`
						<div>${propname.charAt(0).toUpperCase() + propname.slice(1)}</div>
						<div text-wrap text-break">${this.info[propname]}</div>
					`)}
				</div>
				<div class="${this.info?.refreshcmd!=null? 'visible': 'hidden'}">
					<button type="button" class="jadexbtn" @click="${e => this.info.refreshcmd(this.info.node)}">${this.app.lang.t('Refresh')}</button>
				</div>
			</div>
		`;
	}
}

if(customElements.get('jadex-componenttree') === undefined)
	customElements.define('jadex-componenttree', ComponentTree);

