import {LitElement} from 'lit-element';
import {html} from 'lit-element';
import {css} from 'lit-element';
import {BaseElement} from '/webcomponents/baseelement.js'

// Tag name 'jadex-registry'
class RegistryViewElement extends BaseElement 
{
	static get properties() 
	{ 
		return { cid: { type: String }};
	}
	
	attributeChangedCallback(name, oldVal, newVal) 
	{
	    console.log('attribute change: ', name, newVal, oldVal);
	    super.attributeChangedCallback(name, oldVal, newVal);
	}
	
	init() 
	{
		this.pconnected = false;
		this.sconnected = false;
		this.qconnected = false;
		this.concom = false;
		this.services = [];
		this.platforms = [];
		this.queries = [];
		
		console.log("relayview");
		
		var res = "jadex/tools/web/registryview/conf.css";
		var ures = this.getMethodPrefix()+'&methodname=loadResource&args_0='+res+"&argtypes_0=java.lang.String";
		
		this.loadStyle(ures)
		.then(()=>
		{
			console.log("loaded conf.css");
		})
		.catch((err)=>
		{
			console.log(err);
		});
	}
	
	connectedCallback()
	{
		super.connectedCallback();
		this.concom = true;	
	
		this.subscribeToServices();
		this.subscribeToPlatforms();
		this.subscribeToQueries();
	}
	
	disconnectedCallback()
	{
		super.disconnectedCallback();
		this.concom = false;	
	}
	
	render() 
	{
		return html`
		<div id="panel" class="container-fluid">
			<div id="accordion">
				<div class="card m-2">
					<div class="card-header">
    					<h4 class="card-link" data-toggle="collapse" href="#collapseOne">Services</h4>
					</div>
					<div id="collapseOne" class="collapse show" data-parent="#accordion">
						<div class="card-body">
							<div class="actwtable section">
								<table class="${this.sconnected?'': 'down'}">
									<tbody>
										<tr>
											<th>Service Type</th>
											<th>Provided By</th>
											<th>Publication Scope</th>
							 				<th>Networks</th>
											<th>Security</th>
										</tr>
										${this.services.map((x) => 
										html`
										<tr class="${x.connected? 'connecting': ''}">
											<td>${x.type}</td>
											<td>${this.beautifyCid(x.providerId.name)}</td>
											<td>${x.scope.toLowerCase()}</td>
							 				<td>${this.beautifyNetworks(x.networkNames)}</td>
											<td>${x.unrestricted && 'unrestricted' || 'restricted'}</td>
										</tr>
										`)}
									</tbody>
								</table>
							</div>
      					</div>
    				</div>
  				</div>
		
				<div class="card m-2">
					<div class="card-header">
						<h4 class="collapsed card-link" data-toggle="collapse" href="#collapseTwo">Networks</h4>
					</div>
					<div id="collapseTwo" class="collapse show" data-parent="#accordion">
						<div class="card-body">
							 <div class="actwtable section">
								<table class="${this.pconnected?'': 'down'}">
									<tbody>
										<tr>
											<th>Name</th>
											<th>Connected</th>
											<th>Protocol</th>
										</tr>
										${this.platforms.map((x) => 
										html`
										<tr class="${x.connected? 'connecting': ''}">
											<td>${x.platform.name}</td>
											<td>${x.connected}</td>
											<td>${x.protocol}</td>
										</tr>
										`)}
									</tbody>
								</table>
							</div>
						</div>
					</div>
				</div>
				
				<div class="card m-2">
					<div class="card-header">
						<h4 class="collapsed card-link" data-toggle="collapse" href="#collapseThree">Queries</h4>
					</div>
					<div id="collapseThree" class="collapse show" data-parent="#accordion">
						<div class="card-body">
							 <div class="actwtable section">
								<table class="${this.qconnected?'': 'down'}">
									<tbody>
										<tr>
											<th>Service Type</th>
											<th>Query Owner</th>
											<th>Search Scope</th>
										</tr>
										${this.queries.map((x) => 
										html`
										<tr>
											<td>${x.serviceType!=null? x.serviceType.value: ''}</td>
											<td>${this.beautifyCid(x.owner.name)}</td>
											<td>${x.scope.value}</td>
										</tr>
										`)}
									</tbody>
								</table>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
		`;
	}
	
	getMethodPrefix() 
	{
		return 'webjcc/invokeServiceMethod?cid='+this.cid+'&servicetype=jadex.tools.web.registryview.IJCCRegistryViewService';
	}
	
	updateService(event)
	{
		var	found	= false;
		
	//	alert("Service: "+JSON.stringify(service));
		for(var i=0; i<this.services.length; i++)
		{
			found = this.services[i].name==event.service.serviceIdentifier.name
				&& this.services[i].providerId.name==event.service.serviceIdentifier.providerId.name;
			if(found)
			{
				// 0: added, 1: removed, 2: changed
				if(event.type==1)	// removed
				{
					this.services.splice(i,1);
				}
				else // added / changed
				{
					this.services[i] = event.service.serviceIdentifier;
				}
				break;
			}
		}
		
		if(!found)
			this.services.push(event.service.serviceIdentifier);
			
		this.requestUpdate();
	}
	
	subscribeToServices(interval)
	{
		if(interval===undefined)
			interval = 5000;

		var self = this;
		var tc = jadex.getIntermediate(this.getMethodPrefix()+'&methodname=subscribeToServices&returntype=jadex.commons.future.ISubscriptionIntermediateFuture',
		function(response)
		{
			//console.log("service sub received: "+response.data);
			self.sconnected = true;
			self.updateService(response.data);
		},
		function(response)
		{
			//console.log("Could not reach platform.");
			//console.log("Err: "+JSON.stringify(err));
			self.sconnected = false;
			self.services = [];
			self.requestUpdate();
			
			setTimeout(function()
			{
				if(self.concom)
				{
					//console.log("Retrying platform connection...");
					self.subscribeToServices(interval);
				}
				else
				{
					console.log("Subcribe terminated due to component disconnect");
				}
			}, interval);
		});
	}
	
	updatePlatform(platform)
	{
		//console.log("update platform");
		var	found = false;
		for(var i=0; i<this.platforms.length; i++)
		{
			found = this.platforms[i].platform.name==platform.platform.name
				&& this.platforms[i].protocol==platform.protocol;
			if(found)
			{
				if(platform.connected===undefined)
				{
					this.platforms.splice(i,1);
				}
				else
				{
					this.platforms[i] = platform;
				}
				break;
			}
		}
		
		if(!found)
			this.platforms.push(platform);
			
		this.requestUpdate();
	}
	
	subscribeToPlatforms(interval)
	{
		if(interval===undefined)
			interval = 5000;
		
		//console.log("sub platform");
			
		var self = this;
		jadex.getIntermediate(this.getMethodPrefix()+'&methodname=subscribeToConnections&returntype=jadex.commons.future.ISubscriptionIntermediateFuture',
		function(response)
		{
			//console.log("Set up platforms subscription");
			self.pconnected = true;
			self.updatePlatform(response.data);
		},
		function(response)
		{
			//console.log("Could not reach platform.");
			//console.log("Err: "+JSON.stringify(err));
			self.pconnected = false;
			self.platforms = [];
			self.requestUpdate();
			
			setTimeout(function()
			{
				if(self.concom)
				{
					console.log("Retrying platform connection...");
					self.subscribeToPlatforms(interval);
				}
				else
				{
					console.log("Subcribe terminated due to component disconnect");
				}
			}, interval);
		});
	}
	
	/*getQueries() 
	{
		axios.get(this.getMethodPrefix()+'&methodname=getQueries&scope='+JSON.stringify(["global","network"]))	
		.then(function(response) 
		{
			this.queries = response.data;
		})
		.catch(function(err)
		{
			console.log("getQueries err: "+err);	
		});
	}*/
	
	updateQuery(event)
	{
		//console.log("update query");
		var	found = false;
		
		for(var i=0; i<this.queries.length; i++)
		{
			found = this.queries[i].id==event.query.id;
			if(found)
			{
				if(event.type==1)	// removed
				{
					this.queries[i].splice(i,1);
				}
				else // added / changed
				{
					this.queries[i] = event.query;
				}
			}
			break;
		}
		
		if(!found)
			this.queries.push(event.query);
			
		this.requestUpdate();
	}
	
	subscribeToQueries(interval)
	{
		if(interval===undefined)
			interval = 5000;
		
		console.log("sub queries");
			
		var self = this;
		jadex.getIntermediate(this.getMethodPrefix()+'&methodname=subscribeToQueries&returntype=jadex.commons.future.ISubscriptionIntermediateFuture',
		function(response)
		{
			console.log("Set up queries subscription");
			self.qconnected = true;
			self.updateQuery(response.data);
		},
		function(response)
		{
			console.log("Could not reach platform.");
			//console.log("Err: "+JSON.stringify(err));
			self.qconnected = false;
			self.queries = [];
			self.requestUpdate();
			
			setTimeout(function()
			{
				if(self.concom)
				{
					console.log("Retrying connection...");
					self.subscribeToQueries(interval);
				}
				else
				{
					console.log("Subcribe terminated due to component disconnect");
				}
			}, interval);
		});
	}
	
	// helpers for string representation
	
	beautifyCid(cid)
	{
		var	cidparts = cid.split(/[@\.]+/);	// Split at '@' and '.', cf. https://stackoverflow.com/questions/650022/how-do-i-split-a-string-with-multiple-separators-in-javascript
		return cidparts.length>1 ? cidparts[cidparts.length-1] +" ("+cid+")" : cid;
	}
	
	beautifyNetworks(networks)
	{
		var nets = null;
		if(networks!=null)
		{
			networks.forEach(function(network)
			{
				if("___GLOBAL___".localeCompare(network)!=0)
				{
					if(nets==null)
					{
						nets = network;
					}
					else
					{
						nets += ", "+network
					}
				}
			});
		}
		return nets==null ? "" : nets;
	}
	
	// helpers for bootstrap panel
	
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

	firstUpdated(p) 
	{
		var el = this.shadowRoot.getElementById("panel");
		el.addEventListener("click", this.collapseOnClick);
	}
}
if(customElements.get('jadex-registry') === undefined)
	customElements.define('jadex-registry', RegistryViewElement);
