let { LitElement, html, css } = modLoad('lit-element');
let { BaseElement } = modLoad('base-element');
let { CidElement } = modLoad('cid-element');

// Tag name 'jadex-debugger'
class DebuggerElement extends CidElement 
{
	init() 
	{
		console.log("debugger: "+this.cid);
		//this.listener = null;
		this.app.lang.listeners.add(this);
		this.comp = null; // selected component
		this.myservice = "jadex.tools.web.debugger.IJCCDebuggerService";
		this.debuggers = {};
		this.desc = null;
		this.breakpointnames = []; // loaded from model
		this.breakpoints = []; // active breakpoints selected by user
		this.sub = {};
		
		// load subcomponent
		var res = "jadex/tools/web/commons/componenttree.js";
		var ures = this.getMethodPrefix()+'&methodname=loadResource&args_0='+res+"&argtypes_0=java.lang.String";
		return this.loadSubmodule(ures);
	}
	
	postInit()
	{
		var self = this;
		
		// add debug command on component tree
		
		var debug = 
		{
            'label': "Debug",
            'action': function(obj) 
            {
            	//console.log("debug me: "+obj.item.node.id);	
				self.getComponentDescription(obj.item.node.id).then(desc => 
				{
					// Fetch component desc of debugged component
					self.desc = desc;
					
					self.loadBreakpointNames().then(brs =>
					{
						self.breakpointnames = brs;
						self.activateDebugger(obj.item.node.id).then(x => self.requestUpdate());
					}).catch(err => console.log("err fetching breakpoints: "+err));
					
				}).catch(err => console.log("err fetching desc: "+err));
            },
            'icon': self.getMethodPrefix()+'&methodname=loadResource&args_0=jadex/tools/web/debugger/images/search.png'
		};
		var tree = this.shadowRoot.getElementById("componenttree");
		tree.setCommands([debug]);
		
		self.subscribe();
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
		self.sub.terminate = jadex.getIntermediate(this.getMethodPrefix()+'&methodname=subscribeToCMS&args_0='+this.cid+'&returntype=jadex.commons.future.ISubscriptionIntermediateFuture',
		response =>
		{
			//console.log("service sub received: "+response.data);
			
			self.sub.connected = true;
			var event = response.data;
			
			if(event.componentDescription?.name?.name===self.getAgentName())
			{
				//console.log("update of comp: "+self.getAgentName());
				
				self.desc = event.componentDescription;
				
				if(self.desc.state==="terminated")
				{
					//console.log("terminated: "+self.desc?.name?.name);
					self.removeDebugger();
				}
				
				self.requestUpdate();
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
	
	removeDebugger()
	{
		this.desc = null;
		var elem = this.shadowRoot.getElementById("debugger");
		if(elem!=null)
			elem.innerHTML = "";
		this.breakpointnames = [];
		this.breakpoints = [];
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
	
	getMethodPrefix() 
	{
		return 'webjcc/invokeServiceMethod?cid='+this.cid+'&servicetype='+this.myservice;
	}
	
	getType(type)
	{
		// removes white spaces in names to generate tag-compatible name
		return type.replace(/\s+/g, '').toLowerCase()+"debugger";
	}
	
	activateDebugger(cid)
	{
		var self = this;
		
		// get component description
		return new Promise(function(resolve, reject) 
		{
			self.getComponentDescription(cid).then(desc =>
			{
				//console.log("received: "+resp);	
				self.desc = desc;
				let type = self.getType(desc.type);
				
				if(self.debuggers[type]==null)
				{
					self.debuggers[type] = "found";
					self.loadDebugger(cid, type).then(comp =>
					{
						let html = "<jadex-"+type+" cid='"+cid+"'></jadex-"+type+">";
						//console.log("Insert debugger element: " + type);
						self.shadowRoot.getElementById("debugger").innerHTML = html;
						resolve(null);
					}).catch(function(err) 
					{
						//console.log("err: "+err);	
						self.debuggers[type] = null;
						reject(err);
					});
				}
				else
				{
					let html = "<jadex-"+type+" cid='"+cid+"'></jadex-"+type+">";
					//console.log("Insert plugin element: " + type);
					self.shadowRoot.getElementById("debugger").innerHTML = html;
					resolve(null);
				}
			}).catch(function(err) 
			{
				//console.log("err: "+err);	
				reject(err);
			});
		});
	}
	
	loadDebugger(cid, type)
	{
		var self = this;
		return new Promise(function(resolve, reject) 
		{
			axios.get(self.getMethodPrefix()+'&methodname=getDebuggerFragment&args_0='+cid, self.transform).then(resp =>
			{
				//console.log("received: "+resp);	
				var fragment = resp.data;
				//console.log("plug: "+fragment);
				
				self.debuggers[type] = fragment;
				
				// only execute fragment script if not yet defined (=eval)
				if(customElements.get(type) === undefined)
				{	
					let funname = type + "debuggerFragment";
					console.log("Dynamically starting " + funname);
					let componentfunc = new Function(fragment + "\n//# sourceURL=" + funname + "\n");
					componentfunc();
				}
				
				resolve(fragment);
			}).catch(function(err) 
			{
				console.log("Could not load debugger fragment"+err);	
				reject(err);
			});
		});		
	}
	
	getComponentDescription(cid)
	{
		var self = this;
		return new Promise(function(resolve, reject) 
		{
			axios.get(self.getMethodPrefix()+'&methodname=getComponentDescription&args_0='+cid, self.transform).then(resp =>
			{
				resolve(resp.data);
			}).catch(function(err) 
			{
				console.log("err: "+err);	
				reject(err);
			});
		});
	}			
	
	pause()
	{
		var self = this;
		
		this.setEnabled("pause", false);
		return new Promise(function(resolve, reject) 
		{
			axios.get(self.getMethodPrefix()+'&methodname=suspendComponent&args_0='+self.getAgentName(), self.transform).then(resp =>
			{
				self.desc = resp.data;
				self.requestUpdate();
				resolve(resp.data);
			}).catch(function(err) 
			{
				console.log("err: "+err);	
				self.requestUpdate();
				reject(err);
			});
		});		
	}
	
	step()
	{
		var self = this;
		
		this.setEnabled("step", false);

		// internal debugger panel must have function getStepInfo()
		var md = this.shadowRoot.getElementById("debugger");
		var stepinfo = null;
		if(md.children[0].getStepInfo!=null)
			stepinfo = md.children[0].getStepInfo();
		//console.log("dostep with: "+stepinfo);
		
		return new Promise(function(resolve, reject) 
		{
			axios.get(self.getMethodPrefix()+'&methodname=stepComponent&args_0='+self.getAgentName()+"&args_1="+stepinfo, self.transform).then(resp =>
			{
				self.desc = resp.data;
				self.requestUpdate();
				resolve(resp.data);
			}).catch(function(err) 
			{
				console.log("err: "+err);
				self.requestUpdate();	
				reject(err);
			});
		});		
	}
	
	run()
	{
		var self = this;
		
		this.setEnabled("run", false);
		return new Promise(function(resolve, reject) 
		{
			axios.get(self.getMethodPrefix()+'&methodname=resumeComponent&args_0='+self.getAgentName(), self.transform).then(resp =>
			{
				self.desc = resp.data;
				self.requestUpdate();
				resolve(resp.data);
			}).catch(function(err) 
			{
				console.log("err: "+err);	
				self.getComponentDescription().then(desc => 
				{
					self.desc = desc; self.requestUpdate();
				}).catch(e => console.log("err: "+e));
				reject(err);
			});
		});		
	}
	
	updateButtons()
	{
		this.setEnabled("pause", "suspended"!==this.desc?.state);
		this.setEnabled("step", "suspended"===this.desc?.state);
		this.setEnabled("run", "suspended"===this.desc?.state); 
	}
	
	setEnabled(elemid, enabled)
	{
		var elem = this.shadowRoot.getElementById(elemid);
		if(elem!=null)
			elem.disabled = !enabled;
	}
	
	getBreakpointNames()
	{
		return this.breakpointnames==null? []: this.breakpointnames;
	}
	
	loadBreakpointNames()
	{
		var self = this;
		return new Promise(function(resolve, reject) 
		{
			axios.get(self.getMethodPrefix()+'&methodname=getBreakpoints&args_0='+self.getAgentName(), self.transform).then(resp =>
			{
				var res = null;
				if(resp.data!=null && resp.data.length!=0) // why can it be ''?
					res = resp.data;
				resolve(res);
			}).catch(function(err) 
			{
				console.log("err: "+err);
				reject(err);
			});
		});		
	}
	
	addBreakpoint(name)
	{
		//console.log("add breakpoint: "+name);
		this.breakpoints.push(name);
		this.setComponentBreakpoints().then(x => console.log("breakpoints set"+this.breakpoints));
	}
	
	removeBreakpoint(name)
	{
		//console.log("remove breakpoint: "+name);
		this.breakpoints.push(name);
		this.breakpoints.splice(this.breakpoints.indexOf(name), 1);
		this.setComponentBreakpoints().then(x => console.log("breakpoints set: "+this.breakpoints));
	}
	
	setComponentBreakpoints()
	{
		var self = this;
		return new Promise((resolve, reject) => 
		{
			var bs = JSON.stringify(self.breakpoints);
			axios.get(self.getMethodPrefix()+'&methodname=setComponentBreakpoints&args_0='+self.getAgentName()+"&args_1="+bs, self.transform).then(resp =>
			{
				resolve(resp.data);
			}).catch(function(err) 
			{
				console.log("err: "+err);
				reject(err);
			});
		});		
	}
	
	getAgentName()
	{
		return this.desc?.name?.name;
	}
	
	update()
	{
		this.updateButtons();
		super.update();
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
			.grid-container {
				display: grid;
				grid-template-columns: 1fr 2fr auto; 
				grid-template-rows: minmax(50vh, max-content) minmax(min-content, max-content) minmax(min-content, max-content) minmax(min-content, max-content);
				grid-gap: 10px;
			}
			.grid-container2 {
				display: grid;
				grid-template-columns: 1fr auto; 
				grid-template-rows: minmax(min-content, max-content) 1fr minmax(min-content, max-content);
				grid-gap: 10px;
			}
			.span {
				grid-column: 1 / span 2;
			}
			.yscrollable {
				overflow-y: auto;
			}
			.flex-container {
				display: flex;
				margin-top: 10px;
				gap: 10px;
				justify-content: flex-end;
			}
			.inline {
				display: inline-block;
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
				
				<div id="components" class="yscrollable">
					<h3>${this.app.lang.t('Components')}</h3>
					<jadex-componenttree id="componenttree" cid='${this.cid}'></jadex-componenttree>
				</div>
				
				<div class="grid-container2">
					<div class="span ${this.desc!=null? '': 'hidden'}">
						<h3 class="inline nomarginbottom">${this.desc==null? '': this.app.lang.t(this.desc.type+" Debugger")}</h3>
						<span class="right w100"> [for ${this.getAgentName()!=null? this.getAgentName().substring(0, this.getAgentName().indexOf('@')): ''}]</span>
					</div>
					
					<div id="debugger" class="yscrollable"></div>
					
					<div id="breakpoints">
						<div class="h100 back-lightgray ${this.getBreakpointNames().length>0? '': 'hidden'}">
							<div class="back-lightgray">
								<h4 class="margin">${this.app.lang.t('Breakpoints')}</h4>
								<table>
									${this.getBreakpointNames().map(name => html`
										<tr>
				  							<td><input type="checkbox" @click="${e => e.target.checked? this.addBreakpoint(name): this.removeBreakpoint(name)}"></dt></td>
											<td>${name}</td>
									    </tr>
									`)}
								</table>
							</div>
						</div>
					</div>
					
					<div id="buttons" class="span flex-container ${this.desc!=null? '': 'hidden'}">
						<button id="pause" class="jadexbtn" type="button" @click="${e => {this.pause()}}">Pause</button>
						<button id="step" class="jadexbtn" type="button" @click="${e => {this.step()}}">Step</button>
						<button id="run" class="jadexbtn" type="button" @click="${e => {this.run()}}">Run</button>
					</div>
				</div>
				
			</div>
		`;
	}
}

if(customElements.get('jadex-debugger') === undefined)
	customElements.define('jadex-debugger', DebuggerElement);
