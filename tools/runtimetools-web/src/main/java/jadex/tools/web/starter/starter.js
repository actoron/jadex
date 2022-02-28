let { LitElement, html, css } = modLoad('lit-element');
let { BaseElement } = modLoad('base-element');
let { CidElement } = modLoad('cid-element');

// Tag name 'jadex-starter'
class StarterElement extends CidElement 
{
	listener;
	
	init() 
	{
		console.log("starter: "+this.cid);
		this.app.lang.listeners.add(this);
		this.model = null; // loaded model
		this.reversed = false;
		this.myservice = "jadex.tools.web.starter.IJCCStarterService";
		
		var self = this;
		
		var res1 = "jadex/tools/web/commons/modeltree.js";
		var res2 = "jadex/tools/web/commons/componenttree.js";
		var ures1 = self.getMethodPrefix()+'&methodname=loadResource&args_0='+res1+"&argtypes_0=java.lang.String";
		var ures2 = self.getMethodPrefix()+'&methodname=loadResource&args_0='+res2+"&argtypes_0=java.lang.String";

		// load subcomponents
		var p1 = this.loadSubmodule(ures1);
		var p2 = this.loadSubmodule(ures2);
		
		//Promise.all([p1, p2]).then((values) => 
		//{
			//console.log("starter load files ok");
		//});
		return Promise.all([p1, p2]);
	}
	
	connectedCallback() 
	{
		super.connectedCallback();

		var self = this;
		if(this.listener==null)
		{
			this.listener = (e) => 
			{
				//console.log("jadex model event: "+e)
				self.model = e.detail.model;
				self.requestUpdate();
			}
		}
		
		//const myElement = document.querySelector('my-element');
		this.addEventListener('jadex-model-selected', this.listener);
	}
	
	disconnectedCallback()
	{
		super.disconnectedCallback();
		if(this.listener!=null)
			this.removeEventListener('jadex-model-selected', this.listener);
	}
	
	getMethodPrefix() 
	{
		return 'webjcc/invokeServiceMethod?cid='+this.cid+'&servicetype='+this.myservice;
	}
		
	// todo: order by name
	orderBy(data) 
	{ 
		var order = this.reversed ? -1 : 1;
		
		var res = data.slice().sort(function(a, b) 
		{ 
			return a===b? 0: a > b? order: -order 
		});
		
		return res; 
	}
		
	getConfigurationNames()
	{
		var ret = [];
		if(this.model!=null)
		{
			if(this.model.configurations!=null)
			{
				for(var i=0; i<this.model.configurations.length; i++)
				{
					if(i==0)
						ret.push("");
					ret.push(this.model.configurations[i].name);
				}
			}
		}
		return ret;
	}
		
	getArguments()
	{
		return this.model!=null && this.model.arguments!=null? this.model.arguments: [];
	}
		
	start(e)
	{
		if(this.model!=null)
		{
			var self = this;
			
			var conf = this.shadowRoot.getElementById("config").value;
			var sync = this.shadowRoot.getElementById("synchronous").checked;
			var sus = this.shadowRoot.getElementById("suspended").checked;
			var mon = this.shadowRoot.getElementById("monitoring").value;
			
			var gen = this.shadowRoot.getElementById("autogen").checked;
			var gencnt = this.shadowRoot.getElementById("gencnt").value;
			var name = this.shadowRoot.getElementById("name").value;

			var args = {};
			if(this.model!=null && this.model.arguments!=null)
			{
				for(var i=0; i<this.model.arguments.length; i++)
				{
					var el = this.shadowRoot.getElementById('arg_'+i);
					var argval = el.value;
					//console.log('arg_'+i+": "+argval);
					args[this.model.arguments[i].name] = argval;
				}
			}
			
			var ci = {filename: this.model.filename};
			if(conf!=null && conf.length>0)
				ci.configuration = conf;
			ci.synchronous = sync;
			ci.suspend = sus;
			ci.monitoring = mon;
			if(name!=null && name.length>0)
				ci.name = name;
			ci.arguments = args;
			
			//axios.get(self.getMethodPrefix()+'&methodname=createComponent&args_0='+selected+"&argtypes_0=java.lang.String", self.transform).then(function(resp)
			//console.log("starting: "+ci);
			axios.get(this.getMethodPrefix()+'&methodname=createComponent&args_0='+JSON.stringify(ci)
				+"&argtypes_0=jadex.bridge.service.types.cms.CreationInfo", this.transform).then(function(resp)
			{
				// todo: show running components?!
				//console.log("started: "+resp.data);
				self.createInfoMessage("Started component "+resp.data.name+" ["+self.model.filename+"]"); 
			});
		}
	}
		
	static get styles() 
	{
		var ret = [];
		if(super.styles!=null)
			ret.push(super.styles);
		ret.push(
		    css`
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
			
			.flex-container {
				display: flex;
				flex-direction: column;
			}
			.grid-container {
				display: grid;
				grid-template-columns: min-content auto; 
				grid-template-rows: repeat(8, minmax(min-content, max-content));
				grid-gap: 10px;
				align-items: center;
			}
			.span {
				grid-column: 1 / span 2;
			}
			.right { 
				justify-self: right;
			}
			.row-flex-container {
				display: flex;
				flex-direction: row;
				align-items: center;
				flex-shrink: 0;
			}
			.flexgrow {
				flex-grow: 1;
			}
			.yscrollable {
				overflow-y: auto;
			}
			.marginright {
				margin-right: 1em;
			}
			.marginleft {
				margin-right: 1em;
			}
			.marginbottom {
				margin-bottom: 0.3em;
			}
		    `);
		return ret;
	}
	
	asyncRender() {
		return html`
			<div id="panel" class="flex-container">
				<div>
					<h3>${this.app.lang.t('Components')}</h3>
					<jadex-componenttree cid='${this.cid}'></jadex-componenttree>
				</div>
				
				<div>
					<h3>${this.app.lang.t('Available Models')}</h3>
					<jadex-modeltree cid='${this.cid}'></jadex-modeltree>
				</div>
				
				${this.model!=null? html`
				<div class="bgwhitealpha grid-container w100">
					<h3 class="span">${this.app.lang.t('Settings')}</h3>
					
					${this.app.lang.t('Filename')}
					<input type="text" ref="filename" class="w100" value="${this.model!=null? this.model.filename: ''}">
					
					${this.app.lang.t('Configuration')}
					<select id="config" class="w100">
		   				${this.getConfigurationNames().map((c) => html`<option value="${c}"></option>`)}
		 			</select>
					
					${this.app.lang.t('Component Name')}
					<div>
						<input type="text" class="w100 marginbottom" value="${this.model!=null && this.model.instancename!=null? this.model.instancename: ''}" id="name"></input>
						<div class="row-flex-container">
							<span class="marginright">${this.app.lang.t('Auto generate')}</span>
							<input class="marginright" type="checkbox" id="autogen"></input>
							<input class="flexgrow" type="number" value="1" id="gencnt"></input>
						</div>
					</div>
					
					${this.app.lang.t('Monitoring')}
					<select id="monitoring" class="w100">
		   				<option value="OFF">OFF</option> 
		   				<option value="COARSE">${this.app.lang.t('COARSE')}</option> 
		   				<option value="MEDIUM">${this.app.lang.t('MEDIUM')}</option> 
		   				<option value="FINE">${this.app.lang.t('FINE')}</option> 
		 			</select>
					
					${this.app.lang.t('Suspended')}
					<input type="checkbox" id="suspended"></input>
					
					${this.app.lang.t('Synchronous')}
					<input type="checkbox" id="synchronous"></input>
					
					<div class="${this.getArguments().length==0? 'hidden': ''}">
						${this.getArguments().map((arg, i) => html`
						${"["+arg.clazz.value+"] "+arg.name}
						<input class="w100" type="text" value="${arg.value!=null? arg.value: ''}" readonly></input>
						<input class="w100" type="text" id="${'arg_'+i}">
						`)}
					</div>
					
					<div class="span right">
						<button class="jadexbtn" @click="${e => this.start(e)}">Start</button>
					</div>
				</div>
				`: ''}
			</div>
		`;
	}
}

if(customElements.get('jadex-starter') === undefined)
	customElements.define('jadex-starter', StarterElement);
