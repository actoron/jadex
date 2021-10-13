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
		
		var res = "jadex/tools/web/commons/componenttree.js";
		var ures = this.getMethodPrefix()+'&methodname=loadResource&args_0='+res+"&argtypes_0=java.lang.String";

		// load subcomponent
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
					self.activateDebugger(obj.item.node.id).then(x => self.requestUpdate());
				}).catch(err => console.log("err fetching desc: "+err));
            },
            'icon': self.getMethodPrefix()+'&methodname=loadResource&args_0=jadex/tools/web/debugger/images/search.png'
		};
		var tree = this.shadowRoot.getElementById("componenttree");
		tree.setCommands([debug]);
		
		self.updateButtons();
	}
	
	/*connectedCallback() 
	{
		super.connectedCallback();

		var self = this;
		if(this.listener==null)
		{
			this.listener = (e) => 
			{
				console.log("jadex component selection event: "+e)
				self.comp = e.detail.comp;
				self.requestUpdate();
			}
		}
		
		//const myElement = document.querySelector('my-element');
		this.addEventListener('jadex-component-selected', this.listener);
	}
	
	disconnectedCallback()
	{
		super.disconnectedCallback();
		if(this.listener!=null)
			this.removeEventListener('jadex-component-selected', this.listener);
	}*/
	
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
			axios.get(self.getMethodPrefix()+'&methodname=getComponentDescription&args_0='+cid, self.transform).then(resp =>
			{
				//console.log("received: "+resp);	
				let desc = resp.data;
				let type = self.getType(desc.type);
				
				if(self.debuggers[type]==null)
				{
					self.debuggers[type] = "found";
					self.loadDebugger(cid, type).then(comp =>
					{
						let html = "<jadex-"+type+" cid='"+cid+"'></jadex-"+type+">";
						console.log("Insert debugger element: " + type);
						self.shadowRoot.getElementById("debugger").innerHTML = html;
						resolve(null);
					}).catch(function(err) 
					{
						console.log("err: "+err);	
						self.debuggers[type] = null;
						reject(err);
					});
				}
				else
				{
					let html = "<jadex-"+type+" cid='"+cid+"'></jadex-"+type+">";
					console.log("Insert plugin element: " + type);
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
				
				let funname = type + "debuggerFragment";
				console.log("Dynamically starting " + funname);
				let componentfunc = new Function(fragment + "\n//# sourceURL=" + funname + "\n");
				componentfunc();
				resolve(fragment);
			}).catch(function(err) 
			{
				console.log("err: "+err);	
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
				self.desc = resp.data;
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
			axios.get(self.getMethodPrefix()+'&methodname=suspendComponent&args_0='+self.desc.name.name, self.transform).then(resp =>
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
			axios.get(self.getMethodPrefix()+'&methodname=stepComponent&args_0='+self.desc.name.name+"&args_1="+stepinfo, self.transform).then(resp =>
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
			axios.get(self.getMethodPrefix()+'&methodname=resumeComponent&args_0='+self.desc.name.name, self.transform).then(resp =>
			{
				self.desc = resp.data;
				self.requestUpdate();
				resolve(resp.data);
			}).catch(function(err) 
			{
				console.log("err: "+err);	
				self.getComponentDescription()
				self.requestUpdate();
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
	
	update()
	{
		super.update();
		this.updateButtons();
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
				grid-template-columns: 1fr 2fr; 
				grid-template-rows: 1fr minmax(min-content, max-content) minmax(min-content, max-content);
				grid-gap: 10px;
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
				<div>
					<div id="debugger" class="yscrollable"></div>
					<div id="buttons" class="flex-container ${this.desc!=null? '': 'hidden'}">
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
