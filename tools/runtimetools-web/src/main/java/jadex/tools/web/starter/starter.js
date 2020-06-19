console.log("starter script started loading");

import {LitElement} from 'https://unpkg.com/lit-element@latest/lit-element.js?module';
import {html} from 'https://unpkg.com/lit-html@latest/lit-html.js?module';
import {css} from 'https://unpkg.com/lit-element@latest/lit-element.js?module';
import {BaseElement} from '/webcomponents/baseelement.js'

// Tag name 'jadex-starter'
class StarterElement extends BaseElement {

	static get properties() 
	{ 
		return { cid: { type: String }};
	}
	
	attributeChangedCallback(name, oldVal, newVal) 
	{
	    console.log('attribute change: ', name, newVal, oldVal);
	    super.attributeChangedCallback(name, oldVal, newVal);
	    
		console.log("starter: "+this.cid);
	}
	
	constructor() {
		super();

		console.log("starter");
		
		this.cid = null;
		this.models = []; // available component models [filename, classname]
		this.model = null; // loaded model
		this.reversed = false;
		this.myservice = "jadex.tools.web.starter.IJCCStarterService";
		
		var self = this;
		
		var treeid = "modeltree";
		
		var res1 ="jadex/tools/web/starter/libs/jstree_3.3.7.css";
		var res2 = "jadex/tools/web/starter/libs/jstree_3.3.7.js";
		var res3 = "jadex/tools/web/starter/components.js";
		var ures1 = self.getMethodPrefix()+'&methodname=loadResource&args_0='+res1+"&argtypes_0=java.lang.String";
		var ures2 = self.getMethodPrefix()+'&methodname=loadResource&args_0='+res2+"&argtypes_0=java.lang.String";
		var ures3 = self.getMethodPrefix()+'&methodname=loadResource&args_0='+res3+"&argtypes_0=java.lang.String";

		//console.log(ures1);
		//console.log(ures2);
		
		// dynamically load jstree lib and style
		//self.loadFiles(["libs/jstree_3.2.1.min.css", "libs/jstree_3.2.1.min.js"], function()
		
		// load files is only for javascript and css because it is added to dom
		console.log("starter load files start");
		
		axios.get(ures1).then(function(resp)
		{
			var css = resp.data;    
			//console.log(css);
			var sheet = new CSSStyleSheet();
			sheet.replaceSync(css);
			self.shadowRoot.adoptedStyleSheets = self.shadowRoot.adoptedStyleSheets.concat(sheet);
		});
		
		loader.loadFiles([], [ures2, ures3], function()
		{
			console.log("starter load files ok");
	
			// init tree
			$(function() { self.getTree(treeid).jstree(
			{
				"core" : {"check_callback" : true},
				"plugins" : ["sort"],
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
			})});
			
			// no args here
			console.log("getComponentModels start");
			axios.get(self.getMethodPrefix()+'&methodname=getComponentModels', self.transform).then(function(resp)
			{
				//console.log("getComponentModels"+resp.data);
				
				self.models = resp.data;
				
				self.createModelTree(treeid);
				//$('#'+treeid).jstree('open_all');
				var childs = self.getTree(treeid).jstree('get_node', '#').children;
				for(var i=0; i<childs.length; i++)
				{
					self.getTree(treeid).jstree("open_node", childs[i]);
				}
				console.log("models loaded");
				//$("#"+treeid).jstree("open_node", '#');
				self.requestUpdate();
				
				self.getTree(treeid).on('select_node.jstree', function (e, data) 
				{
					self.select(data.instance.get_path(data.node, '.'));
				});
			});
		});
	}
	
	getMethodPrefix() 
	{
		return 'webjcc/invokeServiceMethod?cid='+this.cid+'&servicetype='+this.myservice;
	}
		
	// todo: order by name
	orderBy(data) 
	{ 
		var order = this.reversed ? -1 : 1;
		
		var res = data.slice().sort(function(a, b) { 
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
		
	getModelNames()
	{
		var ret = [];
		if(this.models.length>0)
		{
			for(var i=0; i<this.models.length; i++)
			{
				ret.push(this.getModelName(this.models[i][1]));
			}
		}
		return ret;
	}
	
	getModelName(name)
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
	}
		
	selectModel(filename)
	{
		var self = this;
		
		console.log("selected: "+filename);
		
		axios.get(this.getMethodPrefix()+'&methodname=loadComponentModel&args_0='+filename+"&argtypes_0=java.lang.String", this.transform).then(function(resp)
		{
			console.log("model is: "+resp.data);
			self.model = resp.data;
			self.requestUpdate();
		});
	}
		
	select(name)
	{
		var sel;
		// called from input box
		if(typeof name!="string")
		{
			sel = this.shadowRoot.getElementById("model").value;
		}
		// called from tree
		else
		{
			var m = this.getModelName(name);
			sel = m.name+" ["+m.pck+"]";
		}
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
		console.log(idx);
		
		if(idx>-1)
		{
			var filename = this.models[idx][0];
			this.selectModel(filename);
		}
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
					var el = document.getElementById('arg_'+i);
					var argval = el.value;
					console.log('arg_'+i+": "+argval);
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
			
			//axios.get(self.getMethodPrefix()+'&methodname=createComponent&args_0='+selected+"&argtypes_0=java.lang.String", self.transform).then(function(resp)
			//console.log("starting: "+ci);
			axios.get(this.getMethodPrefix()+'&methodname=createComponent&args_0='+JSON.stringify(ci)+"&argtypes_0=jadex.bridge.service.types.cms.CreationInfo", this.transform).then(function(resp)
			{
				// todo: show running components?!
				console.log("started: "+resp.data);
			});
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
			this.createNodes(treeid, this.models[i][1]);
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
		
	createNodes(treeid, model)
	{
		var sep = ".";
		//var sep = "/";
		//if(model.indexOf("\\")!=-1)
		//	sep = "\\";
		var parts = model.split(sep);
		
		var lastprefix = '';
		var prefix = parts[0];
		
		for(var i=0; i<parts.length; i++)
		{
			prefix = !lastprefix? parts[i]: lastprefix+sep+parts[i];
			if(!this.getTree(treeid).jstree('get_node', prefix))
				this.createNode(treeid, lastprefix, prefix, parts[i], 'last');
			//else
			//	console.log("not creating: "+prefix);
			lastprefix = prefix;
		}
	}
		
	// createNode(parent, id, text, position), position 'first' or 'last'
	createNode(treeid, parent_node_id, new_node_id, new_node_text, position)//, donefunc) 
	{
		//console.log("parent="+parent_node_id+" child="+new_node_id+" childtext="+new_node_text);
		this.getTree(treeid).jstree('create_node', '#'+parent_node_id, {"text": new_node_text, "id": new_node_id }, 'last');	
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
			<div class="container-fluid">
				<div class="row m-1">
					<div class="col-12 m-1">
						<h3>Components</h3>
						<jadex-components cid='${this.cid}'></jadex-components>
					</div>
				</div>
				
				<div class="row m-1">
					<div class="col-12 m-1">
						<h3>Available Models</h3>
					</div>
				</div>
				
				<div class="row m-1">
					<div class="col-12 m-1">
						<input id="model" list="models" class="w100" type="text" @change="${(e) => this.select(e)}"></input>
						<datalist id="models">
							${this.getModelNames().map((model) => html`<option class="w100" value="${model.name+' ['+model.pck+']'}"></option>`)}
						</datalist>
					</div>
					<div class="col-12 m-1">
						<div id="modeltree"></div> <!-- class="scroll" -->
					</div>
				</div>
				
				${this.models.length==0? html`
				<div class="row m-1">
					<div class="col-12 m-1">
				 		<div class="loader"></div> 
				 	</div>
				</div>
				`: ''}
				
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
						${this.getArguments().map(arg => html`
						<div class="col-4"">
							${"["+arg.clazz.value+"] "+arg.name}
						</div>
						<div class="col-4 p-0">
							<input class="w100" type="text" value="{arg.value}" readonly></input>
						</div>
						<!--<div class="col-4 pl-2">  ref="{'arg_'+i} 
							<input class="w100" type="text" id="{'arg_'+i}">
						</div>-->
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
			</div>
		`;
	}
}

customElements.define('jadex-starter', StarterElement);

console.log("starter script ended loading");
