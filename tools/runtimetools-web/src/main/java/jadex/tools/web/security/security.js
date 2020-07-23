import {LitElement, html, css} from 'lit-element';
import {BaseElement} from '/webcomponents/baseelement.js'

// Tag name 'jadex-security'
class SecurityElement extends BaseElement 
{

	static get properties() 
	{ 
		return { cid: { type: String }};
	}
	
	attributeChangedCallback(name, oldVal, newVal) 
	{
	    console.log('attribute change: ', name, newVal, oldVal);
	    super.attributeChangedCallback(name, oldVal, newVal);
	    
		console.log("security: "+this.cid);
	}
	
	constructor() {
		super();

		console.log("security");
		
		this.cid = null;
		this.myservice = "jadex.tools.web.starter.IJCCSecurityService";
		
		//console.log("security plugin started: "+opts);
				
		self.secstate = {};
		self.nn_option = "option1";
		self.progress = 0;
		self.secret = null;
		self.selected = null;
		
		/*$("#nn_opts :input").change(function() 
		{
			console.log(this); 
		});*/
	}
	
	getMethodPrefix() 
	{
		return 'webjcc/invokeServiceMethod?cid='+this.cid+'&servicetype='+this.myservice;
	}
				
	static get styles() {
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
	
	render() {
		return html`
			<div class="container-fluid">
				<div id="accordion">
					<div class="card m-2">
						<div class="card-header">
        					<h4 class="card-link" data-toggle="collapse" href="#collapseOne">General Settings</h4>
    					</div>
						<div id="collapseOne" class="collapse show" data-parent="#accordion">
							<div class="card-body">
								<div class="row m-1">
									<div class="col-6">
										<input type="checkbox" ref="usesecret" @click="${useSecret}" checked="${secstate.useSecret}">Use secret</input>
									</div>
									<div class="col-6">
										<input type="checkbox" ref="printsecret" @click="${printSecret}" checked="${secstate.printSecret}">Print secret</input>
									</div>
								</div>
								<div class="row m-1">
									<div class="col-12">
										<label for="platformsecret">Platform Secret (Password, Key, Certificate)</label>
  										<textarea class="form-control rounded-0" id="platformsecret" rows="10" ref="platformsecret" disabled="true">${secstate.platformSecret}</textarea>
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
						    						<tr class="d-flex net" @click="{this.selectNetwork(${net})}">
						      							<td class="col-4">{net[0]}</td>
						     							<td class="col-8">{net[1]}</td>
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
										<button type="button" class="btn w100" @click="${this.removeNetwork()}" disabled="${this.isNetworkRemoveDisabled()}">Remove Network</button>
									</div>
								</div>
								<div class="row m-1">
									<div class="col">
										<input class="w100 h100" type="text" placeholder="Network Name" ref="network" @change="${update()}" required>
									</div>
								</div>
								<div class="row m-1">
									<div class="col">
										<div class="btn-group btn-group-toggle" data-toggle="buttons">
											<label class="btn btn-secondary active" @click="${this.networksOptionsClicked()}">
												<input type="radio" name="options" id="option1" autocomplete="off" checked> Key
											</label>
											<label class="btn btn-secondary" @click="${this.networksOptionsClicked()}">
												<input type="radio" name="options" id="option2" autocomplete="off"> Password
											</label>
											<label class="btn btn-secondary" @click="${this.networksOptionsClicked()}">
												<input type="radio" name="options" id="option3" autocomplete="off"> X509 Certificates
											</label>
											<label class="btn btn-secondary" @click="${this.networksOptionsClicked()}">
												<input type="radio" name="options" id="option4" autocomplete="off"> Encoded Secret
											</label>
										</div>
									</div>
								</div>
								<div class="row m-1 p-0" show="{nn_option=='option1'}">
									<div class="col m-0 p-0">
										<div class="row ml-0 mr-0 mb-0 mt-1 p-0">
											<div class="col-9">
												<input class="w100 h100" type="text" placeholder="Key" ref="key" disabled="true">
											</div>
											<div class="col-3">
												<button type="button" class="btn w100 h100" @click="${this.generateRandom()}">Generate Key</button>
											</div>
										</div>
										<div class="row ml-0 mr-0 mb-0 mt-1 p-0">
											<div class="col-6">
												<input class="w100 h100" type="text" placeholder="Password (min 16 characters, 24 recommended)" ref="pass">
											</div>
											<div class="col-3">
												<div class="progress w100 h100">
		  											<div class="progress-bar progress-bar-striped" style="width: ${progress}%">${progress}%</div>
												</div> 
											</div>
											<div class="col-3">
												<button type="button" class="btn w100 h100" @click="${this.generateFromPassword()}">Derive Key</button>
											</div>
										</div>
									</div>
								</div>
								<div class="row m-1" show="${nn_option=='option2'}">
									<div class="col m-0 p-0">
										<div class="row ml-0 mr-0 mb-0 mt-1 p-0">
											<div class="col-12">
												<input class="w100 h100" type="text" placeholder="Password (min 10 characters)" ref="pass2" @change="${this.pass2Changed()}">
											</div>
										</div>
									</div>
								</div>
								<div class="row m-1" show="${nn_option=='option3'}">
									<div class="col">
										Option 3
									</div>
								</div>
								<div class="row m-1" show="${nn_option=='option4'}">
									<div class="col m-0 p-0">
										<div class="row ml-0 mr-0 mb-0 mt-1 p-0">
											<div class="col-12">
												<label for="rawsecret">Secret (Password, Key, Certificate) in Jadex format (pw:, key:, pem:)</label>
		  										<textarea class="form-control rounded-0" id="rawsecret" rows="10" ref="rawsecret" @change="${this.rawSecretChanged()}"></textarea>
											</div>
										</div>
									</div>
								</div>
								<div class="row m-1">
									<div class="col-9">
									</div>
									<div class="col-3">
										<button type="button" class="btn w100" @click="${this.addNetwork()}" disabled="${this.isNetworkDisabled()}">Add Network</button>
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
						    						<tr class="role" @click="${this.selectRole()}">
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
										<input type="text" placeholder="Entity" ref="entity" @change="${update()}" required>
									</div>
									<div class="col-4">
										<input type="text" placeholder="Role" ref="role" @change="${update()}" required>
									</div>
									<div class="col-4">
										<button type="button" class="btn" @click="${e => this.addRole(e)}" disabled="${this.isRoleDisabled()}">Add</button>
										<button type="button" class="btn" @click="${e => this.removeRole(e)}" disabled="${this.isRoleDisabled()}">Remove</button>
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
						    						<tr class="na" @click="{this.selectNameAuthority()}">
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
						    						<tr class="tpn" @click="${this.selectTrustedPlatformName()}">
					      								<td>${tpn}</td>
											   		</tr>
												`)}
											</tbody>
										</table>
									</div>
								</div>
								<div class="row m-1">
									<div class="col-8">
										<input class="w100 h100" type="text" placeholder="Trusted Platform Name" ref="tpn" @change="${update}" required>
									</div>
									<div class="col-4">
										<button type="button" class="btn" @click="${this.addTrustedPlatformName()}" disabled="${this.isTrustedPlatformNameDisabled()}">Add</button>
										<button type="button" class="btn" @click="${this.removeTrustedPlatformName()}" disabled="${this.isTrustedPlatformNameDisabled()}">Remove</button>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
			
				<div class="row m-1">
					<div class="col">
						<button type="button" class="btn btn-success" @click="${this.refresh()}">Refresh</button>
					</div>
				</div>
			</div>
		`;
	}
	
	getMethodPrefix()
	{
		return 'webjcc/invokeServiceMethod?cid='+self.cid+'&servicetype='+myservice;
	}
	
	networksOptionsClicked(e)
	{
		//console.log("change: "+e);
		self.nn_option = e.target.children[0].id;
		self.update();
	}
	
	getNetworks()
	{
		return self.secstate!=null? self.secstate.networks: [];
	}
	
	getRoles()
	{
		return self.secstate!=null? self.secstate.roles: [];
	}
	
	getNameAuthorities()
	{
		return self.secstate!=null? self.secstate.nameAuthorities: [];
	}
	
	getTrustedPlatformNames()
	{
		return self.secstate!=null? self.secstate.trustedPlatformNames: [];
	}
	
	useSecret(e)
	{
		var val = self.refs.usesecret.value;
		if(self.secstate!=null)
			self.secstate.usesecret = val;
		axios.get(self.getMethodPrefix()+'&methodname=setUseSecret&args_0='+val+"&argtypes_0=boolean", self.transform).then(function(resp)
		{
			console.log("setUseSecret: "+resp.data);
		});
	}
	
	printSecret(e)
	{
		var val = self.refs.printsecret.value;
		if(self.secstate!=null)
			self.secstate.usesecret = val;
		axios.get(self.getMethodPrefix()+'&methodname=setPrintSecret&args_0='+val+"&argtypes_0=boolean", self.transform).then(function(resp)
		{
			console.log("setPrintSecret: "+resp.data);
		});
	}
	
	refresh()
	{
		axios.get(self.getMethodPrefix()+'&methodname=getSecurityState', self.transform).then(function(resp)
		{
			console.log("ss: "+resp.data);
			self.secstate = resp.data;
			self.update();
		}).catch(error => 
		{
		    console.log(error);
		});
	}
	
	addRole(e)
	{
		var en = self.refs.entity.value;
		var ro = self.refs.role.value;
		
		console.log("add role: "+en+" "+ro);
		
		axios.get(self.getMethodPrefix()+'&methodname=addRole&args_0='+en+'&args_1='+ro, self.transform).then(function(resp)
		{
			console.log("added role: "+en+" "+ro);
			self.refresh();
		});
	}
	
	removeRole(e)
	{
		var en = self.refs.entity.value;
		var ro = self.refs.role.value;
		
		console.log("remove role: "+en+" "+ro);
		
		axios.get(self.getMethodPrefix()+'&methodname=removeRole&args_0='+en+'&args_1='+ro, self.transform).then(function(resp)
		{
			console.log("removed role: "+en+" "+ro);
			self.refresh();
		});
	}
	
	selectRole(e)
	{
		//console.log(e);
		self.selectRow("role", e.currentTarget);
		self.refs.entity.value = e.item.roles[0];
		self.refs.role.value = e.item.roles[1];
	}
	
	isRoleDisabled()
	{
		var ret = self.refs.entity.value.length==0 || self.refs.role.value.length==0;
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
		self.refs.key.value = "key:"+b64key;
		self.secret = self.refs.key.value;
		self.update();
		return key;
	}
	
	generateFromPassword()
	{
		var pass = self.refs.pass.value;
		
		if(pass.length<16)
		{
			console.log("Minimum 16 characters");
		}
		else if(self.progress!=0)
		{
			console.log("Key generation is running");
		}
		else
		{
			var passb = stringToUtf8(pass);
			var saltb = passb;
			scrypt(passb, saltb, 131072, 8, 4, 32, function(error, progress, key) 
			{
	        	if(error) 
	        	{
	            	console.log("err: "+error);
	            } 
	        	else if(key) 
	        	{
	        		console.log("key: "+key);
	            	self.refs.key.value = "key:"+btoa(String.fromCharCode.apply(null, key));
	            	self.progress = 0;
	            	self.secret = self.refs.key.value;
	            	self.update();
	        	} 
	        	else
	        	{
	        		//console.log("progress: "+progress);
	        		var oldp = self.progress;
	        		self.progress = Math.round(progress*100);
	        		if(self.progress!=oldp)
	        			self.update();
	        	}
			});
		}
	}
	
	isNetworkDisabled()
	{
		var network = self.refs.network.value;
		if(network==null || network.length==0)
			return true;
		
		var ret = true;
		if(self.nn_option=='option1')
		{
			ret = self.secret==null;
		}
		else if(self.nn_option=='option2')
		{
			var pw = self.refs.pass2.value;
			ret = pw==null || pw.length<10; 
		}
		else if(self.nn_option=='option3')
		{
		}
		else if(self.nn_option=='option4')
		{
			var s = self.refs.rawsecret.value;
			ret = s==null || s.length==0; 
		}
		
		//console.log("isNetworkDis: "+ret);
		return ret;
	}
	
	isNetworkRemoveDisabled()
	{
		return self.selected == null;
	}
	
	addNetwork()
	{
		var network = self.refs.network.value;
		console.log("add network: "+network+" "+self.secret);
		
		axios.get(self.getMethodPrefix()+'&methodname=addNetwork&args_0='+network+'&args_1='+self.secret, self.transform).then(function(resp)
		{
			console.log("added network: "+network+" "+self.secret);
			self.refresh();
		});
	}
	
	removeNetwork()
	{			
		if(self.selected!=null)
		{
			console.log("remove network: "+self.selected);

			axios.get(self.getMethodPrefix()+'&methodname=removeNetwork&args_0='+self.selected[0]+'&args_1='+self.selected[1], self.transform).then(function(resp)
			{
				console.log("removed network: "+self.selected);
				self.refresh();
			});
		}
	}
	
	selectNetwork(e)
	{
		//console.log(e.item);
		self.selectRow("net", e.currentTarget);
		self.selected = e.item.net;
	}
	
	selectNameAuthority(e)
	{
		self.selectRow("na", e.currentTarget);
	}
	
	selectTrustedPlatformName(e)
	{
		self.selectRow("tpn", e.currentTarget);
		self.refs.tpn.value = e.item.tpn;
	}
	
	isTrustedPlatformNameDisabled()
	{
		var ret = self.refs.tpn.value.length==0;
		//console.log("isTPNDis: "+ret);
		return ret;
	}
	
	addTrustedPlatformName()
	{
		var name = self.refs.tpn.value;
		
		if(name.length>0)
		{
			axios.get(self.getMethodPrefix()+'&methodname=addTrustedPlatformName&args_0='+name, self.transform).then(function(resp)
			{
				console.log("added trusted platform name: "+name);
				self.refresh();
			});
		}
	}
	
	removeTrustedPlatformName(e)
	{
		var name = self.refs.tpn.value;
		
		if(name.length>0)
		{
			axios.get(self.getMethodPrefix()+'&methodname=removeTrustedPlatformName&args_0='+name, self.transform).then(function(resp)
			{
				console.log("removed trusted platform name: "+name);
				self.refresh();
			});
		}
	}
	
	/** Select a row. Finds all rows via clazz and selected current. Deselects rest. */
	selectRow(clazz, selel)
	{
		var sel = -1;
		var oldsel = -1;
		var elems = document.querySelectorAll("."+clazz);
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
	
	pass2Changed(e)
	{
		self.secret = "pw:"+self.refs.pass2.value;
		self.update();
	}
	
	rawSecretChanged(e)
	{
		self.secret = self.refs.rawsecret.value;
		self.update();
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

customElements.define('jadex-security', SecurityElement);