import {LitElement, html, css} from 'lit-element';
import {BaseElement} from '/webcomponents/baseelement.js'

// Tag name 'jadex-security'
class SecurityElement extends BaseElement 
{
	static get properties() 
	{
		var ret = {};
		if(super.properties!=null)
		{
			for (let key in super.properties)
				ret[key]=super.properties[key];
		}
		ret['secstate'] = {attribute: false};
		ret['selnet'] = {attribute: false};
		ret['netname'] = {attribute: false};
		ret['secret'] = {attribute: false};
		ret['nn_option'] = {type: String, attribute: false};
		return ret;
	}
	
	constructor()
	{
		super();
		
		//console.log("security");
		
		this.cid = null;
		this.myservice = "jadex.tools.web.security.IJCCSecurityService";
		
		//console.log("security plugin started: "+opts);
				
		this.secstate = {};
		this.nn_option = "option1";
		this.progress = 0;
		this.secret = null;
		this.selnet = null;
		this.netname = null;
				
		/*$("#nn_opts :input").change(function() 
		{
			console.log(this); 
		});*/
	}
	
	init()
	{
		console.log("security: "+this.cid);
		
		this.myservice = "jadex.tools.web.security.IJCCSecurityService";

		var res1 ="jadex/tools/web/security/scrypt.js";
		var ures1 = this.getMethodPrefix()+'&methodname=loadResource&args_0='+res1+"&argtypes_0=java.lang.String";

		//console.log("jstree load files start");
		
		this.loadScript(ures1).then((values) => 
		{
			console.log("scrypt load ok");
			if(window.scrypt!=null)
				this.scrypt = window.scrypt;
			this.refresh();
		})
		.catch(err => 
		{
			console.log("js tree load files err: "+err);
		});
	}
	
	getMethodPrefix() 
	{
		return 'webjcc/invokeServiceMethod?cid='+this.cid+'&servicetype='+this.myservice;
	}
	
	firstUpdated(p) 
	{
		var el = this.shadowRoot.getElementById("panel");
		el.addEventListener("click", this.collapseOnClick);
	}
	
	// Bootstrap collapse not working in shadow dom :-(
	// This method allows for opening closing collapse elements
	collapseOnClick(e)
	{
    	var target = e.target.getAttribute("data-target")
		if(target==null)
			target = e.target.getAttribute("href");
		if(target!=null)
		{
			if(target.startsWith("#"))
				target = target.substring(1);
			//var el = $(target, this.parentNode);
			var el = this.parentNode.getElementById(target);
			if(el!=null)
			{
    			//el.collapse('toggle');
				if(el.classList.contains("show"))
					el.classList.remove("show");
				else
					el.classList.add("show");
			}
		}
    }
	
	getMethodPrefix() 
	{
		return 'webjcc/invokeServiceMethod?cid='+this.cid+'&servicetype='+this.myservice;
	}
				
	static get styles() 
	{
		var ret = [];
		if(super.styles!=null)
			ret.push(super.styles);
		ret.push(
		    css`
	    	table {
				overflow-wrap: break-word;
				table-layout: fixed
			}
			.w100 {
				width: 100%;
			}
			.h100 {
				height: 100%;
			}
			.selected {
				background-color: rgba(255,255,0,0.5); 
			}
		    `);
		return ret;
	}
	
	render() 
	{
		return html`
			<div id="panel" class="container-fluid">
				<div id="accordion">
					<div class="card m-2">
						<div class="card-header">
        					<h4 class="card-link" data-toggle="collapse" href="#collapseOne">General Settings</h4>
    					</div>
						<div id="collapseOne" class="collapse show" data-parent="#accordion">
							<div class="card-body">
								<div class="row m-1">
									<div class="col-6">
										<input type="checkbox" id="usesecret" @click="${e => {this.useSecret()}}" .checked="${this.secstate.useSecret}">Use secret</input>
										${this.secstate.useSecret}
									</div>
									<div class="col-6">
										<input type="checkbox" id="printsecret" @click="${e => {this.printSecret()}}" .checked="${this.secstate.printSecret}">Print secret</input>
									</div>
								</div>
								<div class="row m-1">
									<div class="col-12">
										<label for="platformsecret">Platform Secret (Password, Key, Certificate)</label>
  										<textarea class="form-control rounded-0" id="platformsecret" rows="10" id="platformsecret" disabled="true">${this.secstate.platformSecret}</textarea>
									</div>
								</div>
	      					</div>
	    				</div>
	  				</div>
			
					<div class="card m-2">
						<div class="card-header">
							<h4 class="collapsed card-link" data-toggle="collapse" href="#collapseTwo">Networks</h4>
						</div>
						<div id="collapseTwo" class="collapse" data-parent="#accordion">
							<div class="card-body">
								<div class="row m-1">
									<div class="col-12">
										<table class="table">
											<thead>
					    						<tr class="d-flex">
					      							<th class="col-4" scope="col">Network Name</th>
					      							<th class="col-8" scope="col">Network Secret</th>
											    </tr>
					  						</thead>
					  						<tbody>
												${this.getNetworks().map((net) => html`
						    						<tr class="d-flex net" @click="${e => {this.selectNetwork(net, e)}}">
						      							<td class="col-4">${net[0]}</td>
						     							<td class="col-8">${net[1]}</td>
												    </tr>
												`)}
											</tbody>
										</table>
									</div>
								</div>
								<div class="row m-1">
									<div class="col-9">
									</div>
									<div class="col-3">
										<button type="button" class="btn btn-primary w100" @click="${e => {this.removeNetwork()}}" ?disabled="${this.selnet == null}">Remove Network</button>
									</div>
								</div>
								<div class="row m-1">
									<div class="col">
										<input class="w100 h100" type="text" placeholder="Network Name" id="network" @change="${e=> this.netname=e.target.value}" required>
									</div>
								</div>
								<div class="row m-1">
									<div class="col">
										<div class="btn-group btn-group-toggle">
											<label class="btn btn-secondary ${this.nn_option=='option1'? 'active': ''}" @click="${e => this.nn_option='option1'}">
												<input type="radio" name="options" autocomplete="off" checked> Key
											</label>
											<label class="btn btn-secondary ${this.nn_option=='option2'? 'active': ''}" @click="${e => { console.log('opt2'); this.nn_option='option2'}}">
												<input type="radio" name="options" autocomplete="off"> Password
											</label>
											<!--<label class="btn btn-secondary ${this.nn_option=='option3'? 'active': ''}" @click="${e => this.nn_option='option3'}">
												<input type="radio" name="options" autocomplete="off"> X509 Certificates
											</label>-->
											<label class="btn btn-secondary ${this.nn_option=='option4'? 'active': ''}" @click="${e => this.nn_option='option4'}">
												<input type="radio" name="options" autocomplete="off"> Encoded Secret
											</label>
										</div>
									</div>
								</div>
								<div class="row m-1 p-0 ${this.nn_option==='option1'? 'visible': 'hidden'}">
									<div class="col m-0 p-0">
										<div class="row ml-0 mr-0 mb-0 mt-1 p-0">
											<div class="col-9">
												<input class="w100 h100" type="text" placeholder="Key" id="key" disabled="true">
											</div>
											<div class="col-3">
												<button type="button" class="btn btn-primary w100 h100" @click="${e => this.generateRandom()}">Generate Key</button>
											</div>
										</div>
										<div class="row ml-0 mr-0 mb-0 mt-1 p-0">
											<div class="col-6">
												<input class="w100 h100" type="text" placeholder="Password (min 16 characters, 24 recommended)" id="pass">
											</div>
											<div class="col-3">
												<div class="progress w100 h100">
		  											<div class="progress-bar progress-bar-striped" style="width: ${this.progress}%">${this.progress}%</div>
												</div> 
											</div>
											<div class="col-3">
												<button type="button" class="btn btn-primary w100 h100" @click="${e => this.generateFromPassword()}">Derive Key</button>
											</div>
										</div>
									</div>
								</div>
								<div class="row m-1 ${this.nn_option==='option2'? 'visible': 'hidden'}">
									<div class="col m-0 p-0">
										<div class="row ml-0 mr-0 mb-0 mt-1 p-0">
											<div class="col-12">
												<input class="w100 h100" type="text" placeholder="Password (min 10 characters)" id="pass2" @change="${e => this.secret = "pw:"+e.target.value}">
											</div>
										</div>
									</div>
								</div>
								<!--<div class="row m-1 ${this.nn_option==='option3'? 'visible': 'hidden'}">
									<div class="col">
										Option 3
									</div>
								</div>-->
								<div class="row m-1 ${this.nn_option==='option4'? 'visible': 'hidden'}">
									<div class="col m-0 p-0">
										<div class="row ml-0 mr-0 mb-0 mt-1 p-0">
											<div class="col-12">
												<label for="rawsecret">Secret (Password, Key, Certificate) in Jadex format (pw:, key:, pem:)</label>
		  										<textarea class="form-control rounded-0" id="rawsecret" rows="10" @change="${e => this.secret = e.target.value}"></textarea>
											</div>
										</div>
									</div>
								</div>
								<div class="row m-1">
									<div class="col-9">
									</div>
									<div class="col-3">
										<button type="button" class="btn btn-primary w100" @click="${e => this.addNetwork()}" ?disabled="${this.netname==null || this.secret==null}">Add Network</button>
									</div>
								</div>
							</div>
						</div>
					</div>
				
					<div class="card m-2">
						<div class="card-header">
							<h4 class="collapsed card-link" data-toggle="collapse" href="#collapseThree">Roles</h4>
						</div>
						<div id="collapseThree" class="collapse" data-parent="#accordion">
							<div class="card-body">
								<div class="row m-1">
									<div class="col-12">
										<table class="table">
											<thead>
					    						<tr>
					      							<th scope="col">Entity</th>
					      							<th scope="col">Role</th>
											    </tr>
					  						</thead>
					  						<tbody>
												${this.getRoles().map((roles) => html`
						    						<tr class="role" @click="${e => this.selectRole(e)}">
						      							<td>${roles[0]}</td>
						     							<td>${roles[1]}</td>
												    </tr>
												`)}
											</tbody>
										</table>
									</div>
								</div>
								<div class="row m-1">
									<div class="col-4">
										<input type="text" placeholder="Entity" id="entity" @change="${this.requestUpdate()}" required>
									</div>
									<div class="col-4">
										<input type="text" placeholder="Role" id="role" @change="${this.requestUpdate()}" required>
									</div>
									<div class="col-4">
										<button type="button" class="btn btn-primary" @click="${e => this.addRole(e)}" disabled="${this.isRoleDisabled()}">Add</button>
										<button type="button" class="btn btn-primary" @click="${e => this.removeRole(e)}" disabled="${this.isRoleDisabled()}">Remove</button>
									</div>
								</div>
							</div>
						</div>
					</div>
					
					<div class="card m-2">
						<div class="card-header">
							<h4 class="collapsed card-link" data-toggle="collapse" href="#collapseFour">Name Authorities</h4>
						</div>
						<div id="collapseFour" class="collapse" data-parent="#accordion">
							<div class="card-body">
								<div class="row m-1">
									<div class="col-12">
										<table class="table">
											<thead>
					    						<tr>
					      							<th scope="col-5">Subject Common Name</th>
					      							<th scope="col-5">Subject Distinguished Name</th>
					      							<th scope="col-2">Type</th>
											    </tr>
					  						</thead>
					  						<tbody>
												${this.getNameAuthorities().map((na) => html`
						    						<tr class="na" @click="${e => this.selectNameAuthority(e)}">
						      							<td>{na[0]}</td>
						     							<td>{na[1]}</td>
						     							<td>{na[2]}</td>
												    </tr>
												`)}
											</tbody>
										</table>
									</div>
								</div>
							</div>
						</div>
					</div>
				
					<div class="card m-2">
						<div class="card-header">
							<h4 class="collapsed card-link" data-toggle="collapse" href="#collapseFive">Trusted Platform Names</h4>
						</div>
						<div id="collapseFive" class="collapse" data-parent="#accordion">
							<div class="card-body">
								<div class="row m-1">
									<div class="col-12">
										<table class="table">
											<thead>
					    						<tr>
					      							<th scope="col-5">Trusted Platform Name</th>
											    </tr>
					  						</thead>
					  						<tbody>
												${this.getTrustedPlatformNames().map((tpn) => html`
						    						<tr class="tpn" @click="${e => this.selectTrustedPlatformName(tpn, e)}">
					      								<td>${tpn}</td>
											   		</tr>
												`)}
											</tbody>
										</table>
									</div>
								</div>
								<div class="row m-1">
									<div class="col-8">
										<input class="w100 h100" type="text" placeholder="Trusted Platform Name" id="tpn" @change="${this.requestUpdate()}" required>
									</div>
									<div class="col-4">
										<button type="button" class="btn btn-primary" @click="${e => this.addTrustedPlatformName()}" disabled="${this.isTrustedPlatformNameDisabled()}">Add</button>
										<button type="button" class="btn btn-primary" @click="${e => this.removeTrustedPlatformName()}" disabled="${this.isTrustedPlatformNameDisabled()}">Remove</button>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
			
				<div class="row m-1">
					<div class="col">
						<button type="button" class="btn btn-success" @click="${e => this.refresh()}">Refresh</button>
					</div>
				</div>
			</div>
		`;
	}
	
	getMethodPrefix()
	{
		return 'webjcc/invokeServiceMethod?cid='+this.cid+'&servicetype='+this.myservice;
	}
	
	networksOptionsClicked(e)
	{
		console.log("change: "+e);
		//this.nn_option = e.target.children[0].id;
		//this.requestUpdate();
	}
	
	getNetworks()
	{
		return this.secstate.networks!=null? this.secstate.networks: [];
	}
	
	getRoles()
	{
		return this.secstate.roles!=null? this.secstate.roles: [];
	}
	
	getNameAuthorities()
	{
		return this.secstate.nameAuthorities!=null? this.secstate.nameAuthorities: [];
	}
	
	getTrustedPlatformNames()
	{
		return this.secstate.trustedPlatformNames!=null? this.secstate.trustedPlatformNames: [];
	}
	
	useSecret(e)
	{
		var self = this;
		var val = this.shadowRoot.getElementById("usesecret").checked;
		
		if(this.secstate!=null)
			this.secstate.usesecret = val;
		axios.get(this.getMethodPrefix()+'&methodname=setUseSecret&args_0='+val+"&argtypes_0=boolean", this.transform).then(function(resp)
		{
			self.createInfoMessage("Changed use secret to "+val);
			//console.log("setUseSecret: "+resp.data);
		});
	}
	
	printSecret(e)
	{
		var self = this;
		var val = this.shadowRoot.getElementById("printsecret").checked;
		
		if(self.secstate!=null)
			self.secstate.usesecret = val;
		axios.get(this.getMethodPrefix()+'&methodname=setPrintSecret&args_0='+val+"&argtypes_0=boolean", this.transform).then(function(resp)
		{
			self.createInfoMessage("Changed print secret to "+val);
			//console.log("setPrintSecret: "+resp.data);
		});
	}
	
	refresh()
	{
		var self = this;
		
		axios.get(self.getMethodPrefix()+'&methodname=getSecurityState', self.transform).then(function(resp)
		{
			//console.log("refresh");
			self.secstate = resp.data;
			self.requestUpdate();
		}).catch(error => 
		{
			self.createErrorMessage("Refresh error ",error);
		    //console.log(error);
		});
	}
	
	addRole(e)
	{
		var self = this;
		var en = this.shadowRoot.getElementById("entity").value;
		var en = this.shadowRoot.getElementById("role").value;
		
		//console.log("add role: "+en+" "+ro);
		
		axios.get(self.getMethodPrefix()+'&methodname=addRole&args_0='+en+'&args_1='+ro, self.transform).then(function(resp)
		{
			self.createInfoMessage("added role: "+en+" "+ro);
			//console.log("added role: "+en+" "+ro);
			self.refresh();
		});
	}
	
	removeRole(e)
	{
		var self = this;
		var en = this.shadowRoot.getElementById("entity").value;
		var en = this.shadowRoot.getElementById("role").value;
		
		//console.log("remove role: "+en+" "+ro);
		
		axios.get(self.getMethodPrefix()+'&methodname=removeRole&args_0='+en+'&args_1='+ro, self.transform).then(function(resp)
		{
			self.createInfoMessage("added role: "+en+" "+ro);
			//console.log("removed role: "+en+" "+ro);
			self.refresh();
		});
	}
	
	selectRole(e)
	{
		//console.log(e);
		this.selectRow("role", e.currentTarget);
		this.shadowRoot.getElementById("entity").value = e.item.roles[0];
		this.shadowRoot.getElementById("role").value = e.item.roles[1];
	}
	
	isRoleDisabled()
	{
		if(this.shadowRoot.getElementById("role")==null)
			return true;
		
		var ret = this.shadowRoot.getElementById("entity").value.length==0 || this.shadowRoot.getElementById("role").value.length==0;
		//console.log("isRoleDis: "+ret);
		return ret;
	}
	
	generateRandom()
	{
		var key = new Uint8Array(32);
		crypto.getRandomValues(key);
		var b64key = btoa(String.fromCharCode.apply(null, key));
		//console.log("key: "+key);
		//console.log("b64key: "+b64key);
		
		var elem = this.shadowRoot.getElementById("key");
		elem.value = "key:"+b64key;
		this.secret = elem.value;
		this.requestUpdate();
		return key;
	}
	
	generateFromPassword()
	{
		var self = this;
		var pass = this.shadowRoot.getElementById("pass").value;
		
		if(pass.length<16)
		{
			//console.log("Minimum 16 characters");
			self.createErrorMessage("Minimum 16 characters");
		}
		else if(self.progress!=0)
		{
			//console.log("Key generation is running");
			self.createErrorMessage("Key generation is running");
		}
		else
		{
			var passb = this.stringToUtf8(pass);
			var saltb = passb;
			this.scrypt(passb, saltb, 131072, 8, 4, 32, function(error, progress, key) 
			{
	        	if(error) 
	        	{
	            	console.log("err: "+error);
	            } 
	        	else if(key) 
	        	{
	        		//console.log("key: "+key);
	            			
					var elem = self.shadowRoot.getElementById("key");
					elem.value = "key:"+btoa(String.fromCharCode.apply(null, key));
					self.secret = elem.value;
					self.progress = 0;
					self.requestUpdate();
	        	} 
	        	else
	        	{
	        		//console.log("progress: "+progress);
	        		var oldp = self.progress;
	        		self.progress = Math.round(progress*100);
	        		if(self.progress!=oldp)
	        			self.requestUpdate();
	        	}
			});
		}
	}
	
	isNetworkDisabled()
	{
		if(this.shadowRoot.getElementById("network")==null)
			return true;
		
		var network = this.shadowRoot.getElementById("network").value;
		
		if(network==null || network.length==0)
			return true;
		
		var ret = true;
		if(this.nn_option=='option1')
		{
			ret = this.secret==null;
		}
		else if(self.nn_option=='option2')
		{
			var pw = this.shadowRoot.getElementById("pass2").value;
			ret = pw==null || pw.length<10; 
		}
		else if(self.nn_option=='option3')
		{
			console.log("to do option3");
		}
		else if(self.nn_option=='option4')
		{
			var s = this.shadowRoot.getElementById("rawsecret").value;
			ret = s==null || s.length==0; 
		}
		
		//console.log("isNetworkDis: "+ret);
		return ret;
	}
	
	addNetwork()
	{
		var self = this;
		var network = this.shadowRoot.getElementById("network").value;
		
		//console.log("add network: "+network+" "+self.secret);
		
		axios.get(self.getMethodPrefix()+'&methodname=addNetwork&args_0='+network+'&args_1='+self.secret, self.transform).then(function(resp)
		{
			self.createInfoMessage("added network: "+network+" "+self.secret);
			//console.log("added network: "+network+" "+self.secret);
			self.refresh();
		});
	}
	
	removeNetwork()
	{			
		var self = this;
		if(this.selnet!=null)
		{
			//console.log("remove network: "+this.selnet);

			axios.get(self.getMethodPrefix()+'&methodname=removeNetwork&args_0='+self.selnet[0]+'&args_1='+self.selnet[1], self.transform).then(function(resp)
			{
				self.createInfoMessage("removed network: "+self.selnet);
				//console.log("removed network: "+self.selnet);
				self.refresh();
			});
		}
	}
	
	selectNetwork(net, e)
	{
		//console.log(e.item);
		this.selectRow("net", e.currentTarget);
		this.selnet = net;
	}
	
	selectNameAuthority(e)
	{
		this.selectRow("na", e.currentTarget);
	}
	
	selectTrustedPlatformName(tpn, e)
	{
		this.selectRow("tpn", e.currentTarget);
		//this.tpn = tpn;
	}
	
	isTrustedPlatformNameDisabled()
	{
		if(this.shadowRoot.getElementById("tpn")==null)
			return true;
		
		var ret = this.shadowRoot.getElementById("tpn").value.length==0;

		//console.log("isTPNDis: "+ret);
		return ret;
	}
	
	addTrustedPlatformName()
	{
		var self = this;
		var name = this.shadowRoot.getElementById("tpn").value
		
		if(name.length>0)
		{
			axios.get(self.getMethodPrefix()+'&methodname=addTrustedPlatformName&args_0='+name, self.transform).then(function(resp)
			{
				self.createInfoMessage("added trusted platform name: "+name);
				//console.log("added trusted platform name: "+name);
				self.refresh();
			});
		}
	}
	
	removeTrustedPlatformName(e)
	{
		var self = this;
		var name = this.shadowRoot.getElementById("tpn").value

		if(name.length>0)
		{
			axios.get(self.getMethodPrefix()+'&methodname=removeTrustedPlatformName&args_0='+name, self.transform).then(function(resp)
			{
				self.createInfoMessage("removed trusted platform name: "+name);
				//console.log("removed trusted platform name: "+name);
				self.refresh();
			});
		}
	}
	
	/** Select a row. Finds all rows via clazz and selected current. Deselects rest. */
	selectRow(clazz, selel)
	{
		var sel = -1;
		var oldsel = -1;
		//var elems = document.querySelectorAll("."+clazz);
		var elems = this.shadowRoot.querySelectorAll("."+clazz);
		for(var i=0; i<elems.length; i++)
		{
			if(elems[i].classList.contains("selected"))
				oldsel = i;
			if(elems[i]==selel)
				sel = i;
			
			elems[i].classList.remove("selected");	
		}
		if(sel!=oldsel)
			selel.classList.add("selected");
	}
	
	stringToUtf8 = function(str) 
	{
		var out = [], p = 0;
		for(var i = 0; i < str.length; i++) 
		{
			var c = str.charCodeAt(i);
		    if(c < 128) 
		    {
		    	out[p++] = c;
		    } 
		    else if(c < 2048) 
		    {
		    	out[p++] = (c >> 6) | 192;
		    	out[p++] = (c & 63) | 128;
		    } 
		    else if(((c & 0xFC00) == 0xD800) && (i + 1) < str.length &&
		        ((str.charCodeAt(i + 1) & 0xFC00) == 0xDC00)) 
			{
		    	c = 0x10000 + ((c & 0x03FF) << 10) + (str.charCodeAt(++i) & 0x03FF);
		    	out[p++] = (c >> 18) | 240;
		    	out[p++] = ((c >> 12) & 63) | 128;
		    	out[p++] = ((c >> 6) & 63) | 128;
		    	out[p++] = (c & 63) | 128;
		    } 
		    else 
		    {
		    	out[p++] = (c >> 12) | 224;
		    	out[p++] = ((c >> 6) & 63) | 128;
		    	out[p++] = (c & 63) | 128;
			}
		}
		return out;
	};
}

if(customElements.get('jadex-security') === undefined)
	customElements.define('jadex-security', SecurityElement);