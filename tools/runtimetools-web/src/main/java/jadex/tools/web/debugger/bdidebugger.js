let { LitElement, html, css } = modLoad('lit-element');
let { BaseElement } = modLoad('base-element');
let { CidElement } = modLoad('cid-element');

// Tag name 'jadex-bdiagentdebugger'
class BDIV3AgentDebuggerElement extends CidElement 
{
	init() 
	{
		console.log("bdi debugger: "+this.cid);
		this.app.lang.listeners.add(this);
		this.comp = null; // selected component
		this.concom = false;
		this.myservice = "jadex.tools.web.debugger.IJCCDebuggerService";
		this.sub = {};
		this.steps = [];
		this.history = [];
		this.selstep = null;
		this.treedata = {};
		this.treedata["#"] = {};
		this.treeopens = {};
		
		this.allgoals = [];
		this.allplans = [];
		this.allbeliefs = {}; // id -> belief
		
		var agent = this.getMethodPrefix()+'&methodname=loadResource&args_0=jadex/tools/web/debugger/images/agent.png';
		var goal = this.getMethodPrefix()+'&methodname=loadResource&args_0=jadex/tools/web/debugger/images/goal.png';
		var plan = this.getMethodPrefix()+'&methodname=loadResource&args_0=jadex/tools/web/debugger/images/plan.png';
		var belief = this.getMethodPrefix()+'&methodname=loadResource&args_0=jadex/tools/web/debugger/images/belief.png';

		this.types =
		{
			//"default" : {"icon": b},
		    "agent" : {"icon": agent},
 			"goal" : {"icon": goal},
 			"plan" : {"icon": plan},
			"belief" : {"icon": belief}
		}
		
		this.initTree()
			.then(() => this.subscribe())
			.catch(err => console.log(err));
	}
	
	initTree()
	{
		var self = this;
		
		return new Promise(function(resolve, reject) 
		{
			self.loadJSTree().then(function()
			{
				//console.log("jstree");
				
				// init tree
				$(function() 
				{ 
					self.getTree("agent").jstree(
					{
						"plugins": ["sort", "types"],
						"types": self.types,
						"core": 
						{
							"animation": false,
							"check_callback" : true,
							"data": function(node, cb) 
							{
								//console.log("data: "+node.id);
		
								function getChildData(id)
								{					
									var children = self.getChildData(id);
									// problem: js tree changes data structures :-( give jstree only a clone?
									//console.log("children of: "+id+" "+children.length);
									return JSON.parse(JSON.stringify(children));
								}
								
								var data = getChildData(node.id);
								if(data==null)
									data = [];
								cb.call(this, data);
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
						}
					});
					
					self.getTree("agent").on("open_node.jstree", (e, data) => 
					{
						//console.log("node open: "+data.node.id);
						self.treeopens[data.node.id] = [data.node.id];
						//self.openChildren(data.node.id);
						//console.log("trigger open children of: "+data.node.id);
						self.reopenChildren(data.node.id);
					});
					self.getTree("agent").on("close_node.jstree", (e, data) => 
					{
						//console.log("node close: "+data.node.id);
						delete self.treeopens[data.node.id];
					});
					
					// Open nodes after node has been loaded asyncronously
					self.getTree("agent").on('refresh.jstree.jstree', function (event, args) 
					{
						//console.log("refresh tree event: ");
						self.reopenNode("#");
					});
					self.getTree("agent").on('refresh_node.jstree.jstree', function (event, args) 
					{
						var nodeid = args.node?.id;
						//console.log("refresh node event: "+nodeid);
						self.reopenNode(nodeid? nodeid: "#");
					});
					/*self.getTree("agent").on('create_node.jstree', function (event, args) 
					{
						console.log("create node event: "+event+" "+args.node.id);
						//if(args.node.id==="#")
						//{
						self.reopenNode(args.node.id);
						//}
					});*/
					/*self.getTree("agent").on('show_node.jstree.jstree', function (event, args) 
					{
						console.log("show node event: "+event+" "+args.node.id);
						//if(args.node.id==="#")
						//{
						self.reopenNode(args.node.id);
						//}
					});*/
				});
				
				resolve();
			}).catch(err => reject(err));
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
				//console.log("js tree load files err: "+err);
				reject(err);
			});
		});
	}
	
	getTree(treeid)
	{
		return $("#"+treeid, this.shadowRoot);
	}
	
	getChildData(id)
	{
		if(this.treedata[id]==null)
			console.log("node not found: "+id);
			
		var cids = this.treedata[id]?._children;
		var ret = null;
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
	
	getParentId(id)
	{
		var paid = this.treedata[id]?._parent;
		return paid;
	}
	
	createNodeDataForEvent(event)
	{
		var self = this;
		var info = event.properties.details;
				
		var name = info.type;
		var type = null;
		var icon = null;
		var myinfo = null;
		var id = null;
		var ret = null;

		var parts = name.split(".");
		parts.unshift("<agent>");
		
		if(info.parentId!=null && self.treedata[info.parentId]!=null)
		{
			ret = info.parentId;
			id = info.id;
			myinfo = info;
			type = info.element;  
			var nodename = parts[parts.length-1];
			if(info.valinfo!=null)
				nodename += info.valinfo;
			self.createNodeData(id, nodename, type, icon, info.parentId, myinfo);
			self.treeopens[id]=id; // auto open
		}
		else
		{
			var myname = "";
			var lastname = null;
			//ret = "#";
			for(var i=0; i<parts.length; i++)
			{
				var part = parts[i];
				if(myname.length>0)
					myname +=".";
				myname += part;
				var nodename = part;
				
				// ret must be last existing parent
				if(self.treedata[i==parts.length-1? info.id: myname]!=null)
				{
					ret = i==parts.length-1? info.id: myname;
				}
				else
				{
					//var type = self.typemap[names[i]];
					//var icon = null;
					
					//console.log(cid+" "+type+" "+icon);
					if(part==="<agent>")
					{
						type = "agent";
						var anames = this.cid.split("@");
						//part = anames[0];
						nodename = anames[0];
						ret = myname;
					}
					else
					{
						type = null;
					}
	
					// Last path element				
					if(i==parts.length-1)
					{
						id = info.id;
						myinfo = info;
						type = info.element; 
						if(info.valinfo!=null)
							nodename += info.valinfo;		
					}
					else
					{
						id = myname;
					}
					
					self.createNodeData(id, nodename, type, icon, lastname, myinfo);
					self.treeopens[id]=id; // auto open
				}
				lastname = myname;
			}
		}
		//else
		//	console.log("not creating: "+names[i]);
		
		return ret;
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
		//console.log("remove node data: "+nodeid);
		//var paid = this.treedata[nodeid]?._parent;

		this.removeChildDataFromParent(nodeid);
		delete this.treedata[nodeid];
		//delete this.treedata[nodeid].children;

		// recursively delete parents when it was last child
		/*var pcs = this.getChildData(paid);
		if(pcs==null || pcs.length==0)
		{
			this.deleteNodeData(paid);
		}*/
	}
	
	removeChildDataFromParent(nodeid)
	{
		var removed = false;
		var paid = this.getParentId(nodeid);
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
					//console.log("removed: "+nodeid);
				}
			}
		}
		
		if(!removed)
			console.log("not removed from parent: "+nodeid);
	}
	
	openNode(id)
	{
		var self = this;
		//console.log("open called: "+id);
		var node = self.getTree("agent").jstree("get_node", id);
		self.getTree("agent").jstree("open_node", node);
	}
	
	reopenChildren(id)
	{
		//console.log("open children called: "+id);
		var self = this;
		
		var childs = this.treedata[id]?._children;
		if(childs!=null)
		{
			for(var i=0; i<childs.length; i++)
			{
				if(self.treeopens[childs[i]]!=null) 
				{
					//console.log("reopen child node: "+childs[i]);
					self.openNode(childs[i]);
				}
			}
		}
	}
	
	reopenNode(id)
	{
		//console.log("reopen called: "+id);
		var self = this;
		
		if(self.treeopens[id]!=null || id==="#")
		{
			//console.log("reopen node: "+id);
			self.openNode(id);
			self.reopenChildren(id);
		}
	}
	
	connectedCallback()
	{
		super.connectedCallback();
		this.concom = true;	
		//this.subscribe();
	}
	
	disconnectedCallback()
	{
		super.disconnectedCallback();
		this.concom = false;	
		this.terminateSubscription();
	}
	
	subscribe(interval)
	{
		//console.log("subscribeTo"+x);
		
		this.terminateSubscription();
		
		if(interval===undefined)
			interval = 5000;

		var self = this;
		//console.log("sub at: "+this.cid);
		var types = ["goal", "plan", "fact", "step"];
			self.sub.callid = jadex.getIntermediate(this.getMethodPrefix()+'&methodname=subscribeToComponent'
			+'&args_0='+this.cid+'&args_1='+JSON.stringify(types)
			+'&argtypes_0=jadex.bridge.IComponentIdentifier&argtypes_1=java.lang.String[]'
			+'&returntype=jadex.commons.future.ISubscriptionIntermediateFuture',
		response =>
		{
			//console.log("service sub received: "+response.data);
			
			self.sub.connected = true;
			var event = response.data;
			
			if(event.bulkEvents!=null && event.bulkEvents.length>0)
			{
				// bulk event indicates initial event -> remove all before adding
				self.steps.length = 0;
				for(var i=0; i<event.bulkEvents.length; i++)
				{
					self.updateEvent(event.bulkEvents[i]);
				}
			}
			else
			{
				self.updateEvent(event);
			}
		},
		err =>
		{
			console.log("Err: "+err);
			self.sub.connected = false;
			self.requestUpdate();
			
			setTimeout(function()
			{
				if(self.concom)
				{
					console.log("Retrying platform connection...");
					self.subscribe(interval);
				}
				else
				{
					//console.log("Subcribe terminated due to component disconnect: "+x);
				}
			}, interval);
		});
	}
	
	terminateSubscription()
	{
		var callid = this.sub.callid;
		if(callid!=null)
		{
			this.sub.callid = null;
			jadex.terminateCall(callid).then(() => 
			{
				this.sub.connected = false;
				//console.log("Terminated subscription: "+self.callid)
			})
			.catch(err => {console.log("Could not terminate subscription: "+err+" "+callid)});
		}
	}
	
	updateEvent(event)
	{
		console.log("event: "+event.type);
		
		var type = event.type.toLowerCase();
		var info = event.properties.details;
		var refresh = null;
		
		if(type.startsWith("created") && type.endsWith("step"))
		{
			//console.log("add step: "+event.properties.id);
			this.steps.push(event);
			//if(laststep==null && steps.size()==1)
			//	sl.setSelectedIndex(0);
		}
		else if(type.startsWith("disposed") && type.endsWith("step"))
		{
			this.removeElement(info, this.steps);
			
			//if(laststep==null)
			//	sl.setSelectedIndex(0);
			
			if(this.isHistoryEnabled())
			{
				this.history.push(event);
				//hl.ensureIndexIsVisible(history.size()-1);
			}
		}
		else if(type.endsWith("fact"))
		{
			//console.log("fact event: "+event);
			
			info.element = "belief";
			
			var oldbel = this.allbeliefs[info.id];
			if(oldbel)
				info.type = oldbel.type; // Hack!!! Keep capability information which is unavailable for modified events.
			this.allbeliefs[info.id] = info;
			
			var valinfo = ":"+info.valueType;
			//if(kind.toLowerCase().indexof("beliefset")!=-1)
			//	valinfo = " : beliefset : "+valinfo;
			if(Array.isArray(info.value))
			{
				valinfo += " [";
				for(var i=0; i<Math.max(2, info.value.length); i++)
				{
					if(i>0)
						valifo += " ,";
					valinfo += info.value[i];
				}
				if(info.value.length>2)
					valinfo += ", ...";
				valinfo += "]";
			}
			else
			{
				valinfo += " "+info.value;
			}
			info.valinfo = valinfo;
			
			refresh = this.createNodeDataForEvent(event);
			
			/*
			// Hack!!! create/disposal only for facts, not for beliefs, just check for changes, removal not supported.
			int	index	= allbeliefs.indexOf(event.getProperty("details"));
			if(index!=-1)
			{
				BeliefInfo	newinfo	= (BeliefInfo)event.getProperty("details");
				BeliefInfo	oldinfo	= (BeliefInfo)allbeliefs.remove(index);
				beliefs.remove(newinfo);
				newinfo.setType(oldinfo.getType());	// Hack!!! Keep capability information which is unavailable for modified events.
				allbeliefs.add(newinfo);
				if(checkCapa(newinfo.getType()))
					beliefs.add(newinfo);								
			}
			else
			{
				BeliefInfo bi = (BeliefInfo)event.getProperty("details");
				allbeliefs.add(bi);
				if(checkCapa(bi.getType()))
					beliefs.add(bi);
			}*/
		}
		else if(type.endsWith("goal"))
		{
			info.element = "goal";
			
			if(type.startsWith("created"))
			{
				//console.log("created goal");
				this.allgoals.push(info);
				refresh = this.createNodeDataForEvent(event);
				
				/*GoalInfo gi = (GoalInfo)event.getProperty("details");
				allgoals.add(gi);
				if(checkCapa(gi.getType()))
					goals.add(gi);*/
			}
			else if(type.startsWith("disposed"))
			{
				//console.log("disposed goal");
				refresh = this.getParentId(info.id);
				this.removeElement(info, this.allgoals);
				this.deleteNodeData(info.id);
				
				// todo remove tree node
				
				/*allgoals.remove(event.getProperty("details"));
				goals.remove(event.getProperty("details"));*/
			}
			else if(type.startsWith("modified"))
			{
				//console.log("modified goal");
				/*int	index	= allgoals.indexOf(event.getProperty("details"));
				if(index!=-1)
				{
					GoalInfo	newinfo	= (GoalInfo)event.getProperty("details");
					GoalInfo	oldinfo	= (GoalInfo)allgoals.remove(index);
					goals.remove(newinfo);
					newinfo.setType(oldinfo.getType());	// Hack!!! Keep capability information which is unavailable for modified events.
					allgoals.add(newinfo);
					if(checkCapa(newinfo.getType()))
						goals.add(newinfo);
				}*/
			}
		}
		else if(type.endsWith("plan"))
		{
			info.element = "plan";
			
			if(type.startsWith("created"))
			{
				//console.log("created plan");
				this.allplans.push(info);
				refresh = this.createNodeDataForEvent(event);
				
				/*PlanInfo pi = (PlanInfo)event.getProperty("details");
				allplans.add(pi);
				if(checkCapa(pi.getType()))
					plans.add(pi);*/
			}
			else if(type.startsWith("disposed"))
			{
				//console.log("disposed plan");
				refresh = this.getParentId(info.id);
				this.removeElement(info, this.allplans);
				this.deleteNodeData(info.id);
				/*allplans.remove(event.getProperty("details"));
				plans.remove(event.getProperty("details"));*/
			}
			else if(type.startsWith("modified"))
			{
				//console.log("modified plan");
				/*int	index	= allplans.indexOf(event.getProperty("details"));
				if(index!=-1)
				{
					PlanInfo	newinfo	= (PlanInfo)event.getProperty("details");
					PlanInfo	oldinfo	= (PlanInfo)allplans.remove(index);
					plans.remove(newinfo);
					newinfo.setType(oldinfo.getType());	// Hack!!! Keep capability information which is unavailable for modified events.
					allplans.add(newinfo);
					if(checkCapa(newinfo.getType()))
						plans.add(newinfo);
				}*/
			}
		}
		
		if(refresh!=null)
		{
			//console.log("refresh entered: "+this.getTree("agent").jstree());
			var hasnode = this.getTree("agent").jstree().get_node(refresh)!=false;
			if(hasnode)
			{
				//console.log("refresh node: "+refresh);
				this.getTree("agent").jstree().refresh_node(refresh);
			}
			else
			{
				//console.log("refresh all, node not in tree: "+refresh);
				this.getTree("agent").jstree("refresh");
			}
		}
		else if(refresh==="all")
		{
			//console.log("refresh all: "+refresh);
			this.getTree("agent").jstree("refresh");
		}
		
		this.requestUpdate();
	}
	
	removeElement(info, ar)
	{
		for(var i=0; i<ar.length; i++)
		{
			if(info.id===ar[i].id)
			{
				ar.splice(i, 1);
				break;
			}
		}
	}
	
	getMethodPrefix() 
	{
		return 'webjcc/invokeServiceMethod?cid='+this.getPlatformCid(this.cid)+'&servicetype='+this.myservice;
	}
	
	getPlatformCid(cid)
	{
		var idx = cid.indexOf('@');
		return idx!=-1? cid.substring(idx+1): cid;
	}
	
	getStepDetails(step)
	{
		var ret = [];
		if(step?.properties?.details)
		{
			var keys = Object.keys(step.properties.details);
    		keys.sort();
			for(var i=0; i<keys.length; ++i)
			{
        		ret[i] = {name: keys[i], value: step.properties.details[keys[i]]};
    		}
		}
		return ret;
	}
	
	selectStep(step)
	{
		this.selstep = step;
		this.requestUpdate();
	}
	
	getStepInfo()
	{
		return this.selstep==null? null: this.selstep.properties.id;
	}
	
	hasSteps()
	{
		return this.steps!=null? this.steps.length>0: false;
	}
	
	stepToString(step)
	{
		var ret = "";
		var clazz = step.properties?.details?.Class;
		if(clazz!=null)
			ret += clazz;
		var id = step.properties?.details?.Id;
		if(id!=null)
			ret += " id: "+id;
		var prio = step.properties?.details?.Priority;
		if(prio!=null)
			ret += " prio: "+prio;
		if(ret.length==0)
			ret = JSON.stringify(step);
		return ret;
	}
	
	getSteps()
	{
		return this.steps!=null? this.steps: [];
	}
	
	getHistory()
	{
		return this.history!=null? this.history: [];
	}
	
	toggleHistory()
	{
		var elem = this.shadowRoot.getElementById("historyon");
		if(!elem.checked)
			this.history.length = 0;
		this.requestUpdate();
	}
	
	isHistoryEnabled()
	{
		return this.shadowRoot.getElementById("historyon").checked;
	}
	
	stepEquals(step1, step2)
	{
		return step1?.properties?.details?.Id === step2?.properties?.details?.Id;
	}
		
	static get styles() 
	{
		var ret = [];
		if(super.styles!=null)
			ret.push(super.styles);
		ret.push(
		    css`
			.w100 {
				width: 100%;
			}
			.h100 {
				height: 100%;
			}
			.grid-container {
				display: grid;
				grid-template-columns: minmax(auto, 1fr) minmax(0, 1fr); 
				grid-template-rows: minmax(200px, 30vh) minmax(200px, 30vh);
				grid-gap: 10px;
			}
			.span {
				grid-row: 1 / span 2;
			}
			.inner {
				display: flex;
				flex-flow: column;
			}
			
			.yscrollable {
				overflow-y: auto;
			}
			.margin {
				margin-left: 10px;
				margin-right: 10px;
			}
			.selected {
				background-color: #beebff;
			}
			.nomargintop {
				margin-top: 0px;
			}
			.nomarginbottom {
				margin-bottom: 0.5em;
			}
		    `);
		return ret;
	}
	
	asyncRender() 
	{
		return html`
			<div id="panel" class="grid-container">
				
				<div class="back-lightgray span">
					<h4 class="margin nomargintop nomarginbottom">${this.app.lang.t('BDI')}</h4>
					<div id="agent"></div>
				</div>
				
				<div id="steps" class="back-lightgray inner">
					<h4 class="margin nomargintop nomarginbottom">${this.app.lang.t('Steps')}</h4>
					<div class="yscrollable h100">
						<table class="margin">
							${this.getSteps().map(step => html`
								<tr class="${this.stepEquals(this.selstep, step)? 'selected': ''}" @click="${e => this.selectStep(step)}">
		  							<td>${this.stepToString(step)}</td>
							    </tr>
							`)}
						</table>
					</div>
				</div>
				
				<div id="history" class="back-lightgray inner">
					<h4 class="margin nomargintop nomarginbottom">${this.app.lang.t('History')}</h4>
					<div class="yscrollable h100">
						<table class="margin">
							${this.getHistory().map(step => html`
								<tr class="${this.stepEquals(this.selstep, step)? 'selected': ''}" @click="${e => this.selectStep(step)}">
		  							<td>${this.stepToString(step)}</td>
							    </tr>
							`)}
						</table>
					</div>
					<div class="margin">
						<input id="historyon" type="checkbox" class="history_c1" name="his" checked @click="${e => {this.toggleHistory()}}">
						<label for="his">History</label>
					</div>
				</div>
			
			</div>
		`;
	}
}

if(customElements.get('jadex-bdiv3agentdebugger') === undefined)
	customElements.define('jadex-bdiv3agentdebugger', BDIV3AgentDebuggerElement);
