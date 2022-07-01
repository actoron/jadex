let { html, css } = modLoad('lit-element');
let { BaseElement } = modLoad('base-element');
let { CidElement } = modLoad('cid-element');

// Tag name 'jadex-modeltree'
class ModelTree extends CidElement 
{
	static get properties() 
	{
		var ret = {};
		if(super.properties!=null)
		{
			for (let key in super.properties)
				ret[key]=super.properties[key];
		}
		ret['progressnow'] = {type: Number, attribute: false};
		ret['progressmax'] = {type: Number, attribute: false};
		return ret;
	}
	
	init()
	{
		console.log("modetree init");
		
		let self = this;
		this.app.lang.listeners.add(this);
		
		this.models = []; // available component models [filename, classname]
		this.reversed = false;
		//this.myservice = "jadex.tools.web.starter.IJCCStarterService";
		this.treeid = "modeltree";
		
		this.progressnow = 0;
		this.progressmax = 100;
		
		var model = this.getMethodPrefix()+'&methodname=loadResource&args_0=jadex/tools/web/commons/images/applications.png';
		
		this.types =
		{
			//"default" : {"icon": b},
		    "model" : {"icon": model}
		}
		
		
		//console.log("modeltree");
		this.loadJSTree().then(function()
		{
			//console.log("jstree");
			
			// init tree
			$(function() 
			{ 
				self.getTree(self.treeid).jstree(
				{
					"core" : {"check_callback" : true, "multiple": false},
					"plugins" : ["sort", "types"],
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
			    	"types": self.types
				});
				
				self.getTree(self.treeid).on('select_node.jstree', function (e, data) 
				{
					//console.log("tree select: "+data.node.id);
					self.select(data.instance.get_path(data.node.id, '.'));
				});
				
				// no args here
				//console.log("getComponentModels start");

				this.progressnow = 0;				
				var t = jadex.getIntermediate(self.getMethodPrefix()+'&methodname=getComponentModelsAsStream'+'&returntype=jadex.commons.future.ISubscriptionIntermediateFuture',
				function(response)
				{
					if(!response.data)
					{
						//console.log("received: "+response.data);
						return;
					}
					 
					self.progressnow++;
					
					var changed = false;
					for(var i=0; i<response.data.length; i++)
					{
						if(self.addModel(response.data[i]))
						{
							changed = true;
							//self.createModelTree(self.treeid);
							self.createNodes(self.treeid, response.data[i]);
						}
					}
					
					if(changed)
					{
						var childs = self.getTree(self.treeid).jstree('get_node', '#').children;
						for(var i=0; i<childs.length; i++)
						{
							self.getTree(self.treeid).jstree("open_node", childs[i]);
						}
					}
					
					//console.log("models loaded");
					
					//$("#"+treeid).jstree("open_node", '#');
					self.requestUpdate();
					
					/*if(self.addModel(response.data))
					{
						//self.createModelTree(self.treeid);
						self.createNodes(self.treeid, response.data[1]);
						
						//self.addToModelTree(self.treeid);
						//$('#'+treeid).jstree('open_all');
						var childs = self.getTree(self.treeid).jstree('get_node', '#').children;
						for(var i=0; i<childs.length; i++)
						{
							self.getTree(self.treeid).jstree("open_node", childs[i]);
						}
						
						//console.log("models loaded");
						
						//$("#"+treeid).jstree("open_node", '#');
						self.requestUpdate();
						
						/*self.getTree(self.treeid).on('select_node.jstree', function (e, data) 
						{
							self.select(data.instance.get_path(data.node, '.'));
						});* /
					}*/
				},
				function(response)
				{
					console.log("Could not load models.");
					console.log("Err: "+JSON.stringify(response));
				},
				function(max)
				{
					//console.log("received max value: "+max);
					self.progressmax = max;
				});
				
				/*axios.get(self.getMethodPrefix()+'&methodname=getComponentModels', self.transform).then(function(resp)
				{
					//console.log("getComponentModels"+resp.data);
					
					self.models = resp.data;
					
					self.createModelTree(self.treeid);
					//$('#'+treeid).jstree('open_all');
					var childs = self.getTree(self.treeid).jstree('get_node', '#').children;
					for(var i=0; i<childs.length; i++)
					{
						self.getTree(self.treeid).jstree("open_node", childs[i]);
					}
					
					//console.log("models loaded");
					
					//$("#"+treeid).jstree("open_node", '#');
					self.requestUpdate();
					
					self.getTree(self.treeid).on('select_node.jstree', function (e, data) 
					{
						self.select(data.instance.get_path(data.node, '.'));
					});
				});*/
			});
		});
	}
	
	loadJSTree()
	{
		var self = this;
		
		return new Promise(function(resolve, reject) 
		{
			//var res1 ="jadex/tools/web/commons/libs/jstree_3.3.7.css";
			//var res2 = "jadex/tools/web/commons/libs/jstree_3.3.7.js";
			var res1 ="jadex/tools/web/commons/libs/jstree-3.3.12/themes/default/style.css";
			var res2 = "jadex/tools/web/commons/libs/jstree-3.3.12/jstree.js";
			//var ures1 = self.getMethodPrefix()+'&methodname=loadResource&args_0='+res1+"&argtypes_0=java.lang.String";
			//var ures2 = self.getMethodPrefix()+'&methodname=loadResource&args_0='+res2+"&argtypes_0=java.lang.String";
	
			//console.log("jstree load files start");
			
			var p1 = self.loadServiceStyle(res1);
			var p2 = self.import(res2);
			
			Promise.all([p1, p2]).then((values) => 
			{
				//console.log("js tree load files ok");
				resolve();
			})
			.catch(err => 
			{
				//console.log("js tree load files err: "+err);
				reject(err);
			});
		});
	}
	
	getMethodPrefix() 
	{
		return 'webjcc/invokeServiceMethod?cid='+this.cid+'&servicetype=jadex.tools.web.starter.IJCCStarterService';
	}
	
	getJadexService()
	{
		return "jadex.tools.web.starter.IJCCStarterService";
	}
		
	getModelNames()
	{
		var ret = [];
		
		if(this.models.length>0)
		{
			for(var i=0; i<this.models.length; i++)
			{
				var sep = this.getFilenameSeparator(this.models[i][0]);
				var name = this.models[i][0].substring(this.models[i][0].lastIndexOf(sep)+1);
				ret.push({name: name, pck: this.models[i][1]});
			}
		}
		
		return ret;
	}
	
	addModel(model)
	{
		var ret = true;
		// only add model if not contained
		for(var i=0; i<this.models.length; i++)
		{
			if(this.models[i][0]===model[0] && this.models[i][1]===model[1])
			{
				//console.log("found duplicate: "+this.models[i][0]);
				ret = false;
				break;
			}
		}
		
		if(ret)
		{
			//console.log("adding: "+model[0]);
			this.models.push(model);
		}
		
		return ret;
	}
	
	/*getModelName(name)
	{
		var ret = null;
		var n = name.lastIndexOf(".");
		
		if(n>=0)
		{
			ret = {name: name.substring(n+1), pck: name.substring(0,n)};
		}
		else
		{
			ret = {name: name, pck: null};
		}
		
		return ret;
	}*/
		
	selectModel(filename)
	{
		var self = this;
		
		//console.log("selected: "+filename);
		
		axios.get(this.getMethodPrefix()+'&methodname=loadComponentModel&args_0='+filename+"&argtypes_0=java.lang.String", this.transform).then(function(resp)
		{
			//console.log("selected model is (event dispatch): "+resp.data);
			
			var event = new CustomEvent("jadex-model-selected", 
			{ 
				detail: {model: resp.data},
	            bubbles: true, 
	            composed: true 
	        });
	        self.dispatchEvent(event);
			
	        //self.requestUpdate();
		});
	}
		
	select(name)
	{
		var sel;
		var filename;
		// called from input box
		if(typeof name!="string")
		{
			sel = this.shadowRoot.getElementById("model").value;
			
			var opts = this.shadowRoot.getElementById("models").options;
			var idx = -1;
	
			for(var i=0; i<opts.length; i++)
			{
				if(opts[i].value==sel)
				{
					idx = i;
					break;
				}
			}
			//console.log(idx);
			
			if(idx>-1)
			{
				this.getTree(this.treeid).jstree('close_all');
				filename = this.models[idx][0];
				this.openPath(this.treeid, this.models[idx]);
				this.getTree(this.treeid).jstree('activate_node', filename);
			}
		}
		// called from tree
		else
		{
			filename = name;
		}
		
		if(filename)
		{
			this.selectModel(filename);
		}
		else
		{
			console.log("selected element not found: "+filename);
		}
	}
		
	getTree(treeid)
	{
		return $("#"+treeid, this.shadowRoot);
	}
		
	createModelTree(treeid)
	{
		this.empty(treeid);
		
		for(var i=0; i<this.models.length; i++)
		{
			//console.log(self.models[i]);
			this.createNodes(treeid, this.models[i]);
		}
	}
	
	addToModelTree(treeid)
	{
		for(var i=0; i<this.models.length; i++)
		{
			//console.log(self.models[i]);
			this.createNodes(treeid, this.models[i]);
		}
	}
		
	empty(treeid)
	{
		// $('#'+treeid).empty(); has problem when readding nodes :-(
		
		var roots = this.getTree(treeid).jstree().get_node('#').children;
		for(var i=0; i<roots.length; i++)
		{
			this.getTree(treeid).jstree('delete_node', roots[i]);
		}
	}
		
	getFilenameSeparator(filename)
	{
		var sep = "/";
		if(filename.indexOf("\\")!=-1)
			sep = "\\";
		return sep;
	}
	
	openPath(treeid, model)
	{
		var filename = model[0];
		var pack = model[1];
		
		var nodeid = filename;
		while(nodeid != '#') 
		{
        	this.getTree(treeid).jstree('open_node', nodeid);
        	var node = this.getTree(treeid).jstree("get_node", nodeid);
        	nodeid = this.getTree(treeid).jstree("get_parent", node);
		}
	}
	
	createNodes(treeid, model)
	{
		var filename = model[0];
		var pack = model[1];
		
		var sep = this.getFilenameSeparator(filename);
		
		var name = filename.substring(filename.lastIndexOf(sep)+1);
		
		var parts = pack.split(".");
		
		var lastprefix = '';
		var prefix = parts[0];
		
		for(var i=0; i<parts.length; i++)
		{
			prefix = !lastprefix? parts[i]: lastprefix+"."+parts[i];
			
			// if node not already exists, create it
			if(!this.getTree(treeid).jstree('get_node', prefix))
				this.createNode(treeid, lastprefix, prefix, parts[i], 'last', "package");
			//else
			//	console.log("not creating: "+prefix);
			lastprefix = prefix;
		}
		
		if(!this.getTree(treeid).jstree('get_node', filename))
			this.createNode(treeid, lastprefix, filename, name, 'last', "model");
	}
		
	// createNode(parent, id, text, position), position 'first' or 'last'
	createNode(treeid, parent_node_id, new_node_id, new_node_text, position, type)//, donefunc) 
	{
		//console.log("parent="+parent_node_id+" child="+new_node_id+" childtext="+new_node_text);
		this.getTree(treeid).jstree('create_node', '#'+parent_node_id, {"text": new_node_text, "id": new_node_id, "type": type}, position);	
	}
		
	static get styles() 
	{
		// https://stackoverflow.com/questions/18031354/100-width-is-bigger-than-parents-div
	    return css`
	    	.w100 {
				width: 100%;
				box-sizing: border-box;
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
	
	asyncRender() 
	{
		return html`
			<div>
				${this.progressnow<this.progressmax? html`
				<div>
					<label for="mpro">Loading models:</label>
					<progress id="mpro" class="w100" value="${this.progressnow}" max="${this.progressmax}">${this.progressnow}</progress>
				
					<!--<div class="progress mb-1">
						<div class="progress-bar" style="width:${this.progressnow/this.progressmax*100}%">${Math.trunc(this.progressnow/this.progressmax*100)}%</div>
					</div>-->
				</div>
				`: ''}
				
				<div>
					<div>
						<input id="model" list="models" placeholder="${this.app.lang.t('Search models...')+' ['+this.models.length+']'}" class="w100" type="text" 
							@input="${(e) => this.select(e)}">
						</input>
						<datalist id="models">
							${this.getModelNames().map((model) => html`<option class="w100" value="${model.name+' ['+model.pck+']'}"></option>`)}
						</datalist>
					</div>
					<div>
						<div id="modeltree"></div> <!-- class="scroll" -->
					</div>
				</div>
				
				<!-- ${this.models.length==0? html`
				<div class="loader"></div> 
				`: ''} -->
			</div>
		`;
	}
}

if(customElements.get('jadex-modeltree') === undefined)
	customElements.define('jadex-modeltree', ModelTree);
