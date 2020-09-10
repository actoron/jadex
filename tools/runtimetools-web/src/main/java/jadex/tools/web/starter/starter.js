import {LitElement, html, css} from 'lit-element';
import {BaseElement} from '/webcomponents/baseelement.js'

// Tag name 'jadex-starter'
class StarterElement extends BaseElement 
{
	listener;
	
	init() 
	{
		console.log("starter");
		
		this.model = null; // loaded model
		this.reversed = false;
		this.myservice = "jadex.tools.web.starter.IJCCStarterService";
		
		var self = this;
		
		var res1 = "jadex/tools/web/starter/modeltree.js";
		var res2 = "jadex/tools/web/starter/componenttree.js";
		var ures1 = self.getMethodPrefix()+'&methodname=loadResource&args_0='+res1+"&argtypes_0=java.lang.String";
		var ures2 = self.getMethodPrefix()+'&methodname=loadResource&args_0='+res2+"&argtypes_0=java.lang.String";

		// load subcomponents
		var p1 = this.loadSubmodule(ures1);
		var p2 = this.loadSubmodule(ures2);
		
		Promise.all([p1, p2]).then((values) => 
		{
			//console.log("starter load files ok");
		});
	}
	
	connectedCallback() 
	{
		super.connectedCallback();

		var self = this;
		if(this.listener==null)
		{
			this.listener = (e) => 
			{
				console.log(e)
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
					console.log('arg_'+i+": "+argval);
					args[this.model.arguments[i].name] = argval;
				}
			}
			
			var ci = {filename: this.model.filename};
			if(conf!=null && conf.length>0)
				ci.configuration = conf;
			ci.synchronous = sync;
			ci.suspended = sus;
			ci.monitoring = mon;
			if(name!=null && name.length>0)
				ci.name = name;
			ci.arguments = args;
			
			//axios.get(self.getMethodPrefix()+'&methodname=createComponent&args_0='+selected+"&argtypes_0=java.lang.String", self.transform).then(function(resp)
			//console.log("starting: "+ci);
			axios.get(this.getMethodPrefix()+'&methodname=createComponent&args_0='+JSON.stringify(ci)+"&argtypes_0=jadex.bridge.service.types.cms.CreationInfo", this.transform).then(function(resp)
			{
				// todo: show running components?!
				console.log("started: "+resp.data);
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
		    `);
		return ret;
	}
	
	render() {
		return html`
			<div class="container-fluid">
				<div class="row m-1">
					<div class="col-12 m-1">
						<h3>Components</h3>
						<jadex-componenttree cid='${this.cid}'></jadex-componenttree>
					</div>
				</div>
				
				<div class="row m-1">
					<div class="col-12 m-1">
						<h3>Available Models</h3>
						<jadex-modeltree cid='${this.cid}'></jadex-modeltree>
					</div>
				</div>
				
				${this.model!=null? html`
				<div class="bgwhitealpha m-2 p-2"> <!-- sticky-top  -->
					<div class="row m-1">
						<div class="col-12">
							<h3>Settings</h3>
						</div>
					</div>
					<div class="row m-1">
						<div class="col-2">
							Filename
						</div>
						<div class="col-10" id="filename">
							<input type="text" ref="filename" class="w100" value="${this.model!=null? this.model.filename: ''}">
						</div>
					</div>
					<div class="row m-1">
						<div class="col-2">
							Configuration
						</div>
						<div class="col-10">
							<select id="config" class="w100">
		   						${this.getConfigurationNames().map((c) => html`<option value="${c}"></option>`)}
		 					</select>
						</div>
					</div>
					<div class="row m-1">
						<div class="col-2">
							Comp. name
						</div>
						<div class="col-5">
							<input type="text" class="w100" value="${this.model!=null && this.model.instancename!=null? this.model.instancename: ''}" id="name"></input>
						</div>
						<div class="col-3">
							<input type="checkbox" id="autogen">Auto generate</input>
						</div>
						<div class="col-2">
							<input class="w100" type="number" value="1" id="gencnt"></input>
						</div>
					</div>
					<div class="row m-1">
						<div class="col-4">
							<input type="checkbox" id="suspended">Suspended</input>
						</div>
						<div class="col-4">
							<input type="checkbox" id="synchronous">Synchronous</input>
						</div>
						<div class="col-4">
							<select id="monitoring" class="w100">
		   						<option value="OFF">OFF</option> 
		   						<option value="COARSE">COARSE</option> 
		   						<option value="MEDIUM">MEDIUM</option> 
		   						<option value="FINE">FINE</option> 
		 					</select>
		 				</div>
					</div>
					
					<div class="row m-1">
						${this.getArguments().map((arg, i) => html`
						<div class="col-4"">
							${"["+arg.clazz.value+"] "+arg.name}
						</div>
						<div class="col-4 p-0">
							<input class="w100" type="text" value="${arg.value!=null? arg.value: ''}" readonly></input>
						</div>
						<div class="col-4 pl-2"> 
							<input class="w100" type="text" id="${'arg_'+i}">
						</div>
						`)}
					</div>
					
					<div class="row m-1">
						<div class="col-10">
						</div>
						<div class="col-2">
							<button class=" float-right" @click="${e => this.start(e)}">Start</button> <!-- class="w100" -->
						</div>
					</div>
				</div>
				`: ''}
			</div>
		`;
	}
}

if(customElements.get('jadex-starter') === undefined)
	customElements.define('jadex-starter', StarterElement);
