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
		
		this.subscribe();
		this.initGoalbase();
	}
	
	initGoalbase()
	{
		var self = this;
		
		this.loadJSTree().then(function()
		{
			//console.log("jstree");
			
			// init tree
			$(function() 
			{ 
				self.getTree("goalbase").jstree(
				{
					"plugins": ["sort"]/*, "types", "contextmenu"]*/,
					"core": 
					{
						"check_callback" : true,
						"data": function(node, cb) 
						{
							console.log("data: "+node.id);
	
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
			});
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
	
	createNodeDataForEvent(event)
	{
		var id = "bla";
		var name = "blub";
		var type = null;
		var icon = null;
		var parent = null; 
		var info = null;
		
		self.createNodeData(id, name, type, icon, parent, info);
	}
	
	createNodeData(id, name, type, icon, parent, info)
	{
		console.log("create node data: "+id+" "+name+" "+type+" "+parent);
		var paid = parent==null || parent.length==0? "#": parent;
		// must use _parent as parent is already used by jstree
		var node = {"id": id, "text": name, "type": type, "icon": icon, "children": true, "_parent": paid, "info": info}; // "original": {'hello': true}
		var key = parent==null || parent.length==0? "#": parent;
		
		this.addChildData(key, node);
	}
		
	deleteNodeData(nodeid)
	{
		//console.log("remove node data: "+nodeid);
		this.removeChildDataFromParent(nodeid);
		delete this.treedata[nodeid];
		//delete this.treedata[nodeid].children;

		/*var ac = this.getChildData("Applications");
		if(ac==null || ac.length==0)
		{
			this.removeChildDataFromParent("Applications");
			delete this.treedata["Applications"];
			//delete this.treedata["Applications_children"]
		}*/
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
		self.sub.terminate = jadex.getIntermediate(this.getMethodPrefix()+'&methodname=subscribeToComponent&args_0='+this.cid+'&returntype=jadex.commons.future.ISubscriptionIntermediateFuture',
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
			jadex.terminateCall(self.callid).then(() => 
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
		
		if(event.type.startsWith("created") && event.type.endsWith("step"))
		{
			//console.log("add step: "+event.properties.id);
			this.steps.push(event);
			//if(laststep==null && steps.size()==1)
			//	sl.setSelectedIndex(0);
		}
		else if(event.type.startsWith("disposed") && event.type.endsWith("step"))
		{
			for(var i=0; i<this.steps.length; i++)
			{
				var tmp = this.steps[i];
				if(event.properties.id===tmp.properties.id)
				{
					//console.log("remove step: "+event.properties.id);
					this.steps.splice(i, 1);
					//if(laststep!=null && laststep.getProperty("id").equals(tmp.getProperty("id")))
					//	laststep = null;
					break;
				}
			}
			
			//if(laststep==null)
			//	sl.setSelectedIndex(0);
			
			if(this.isHistoryEnabled())
			{
				this.history.push(event);
				//hl.ensureIndexIsVisible(history.size()-1);
			}
		}
		
		this.requestUpdate();
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
				margin-bottom: 0px;
			}
		    `);
		return ret;
	}
	
	asyncRender() 
	{
		return html`
			<div id="panel" class="grid-container">
				
				<div class="back-lightgray span">
					<h4 class="margin nomargintop nomarginbottom">${this.app.lang.t('Goalbase')}</h4>
					<div id="goalbase"></div>
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
