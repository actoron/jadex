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
		this.concom = false;
		this.services = [];
		this.platforms = [];
		this.queries = [];
		
		console.log("relayview");
	}
	
	connectedCallback()
	{
		super.connectedCallback();
		this.concom = true;	
	
		this.subscribeToServices();
		//this.subscribeToPlatforms();
	}
	
	disconnectedCallback()
	{
		super.disconnectedCallback();
		this.concom = false;	
	}
	
	render() 
	{
		return html`
		<div class="actwtable section">
			<h3 id="HConnectedPlatforms">Provided Services</h3>
			<p>
				This page shows a self-updating list of provided services currently registered at this superpeer.
			</p>
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
			console.log("service sub received: "+response.data);
			self.sconnected = true;
			self.updateService(response.data);
		},
		function(response)
		{
			console.log("Could not reach platform.");
			//console.log("Err: "+JSON.stringify(err));
			self.sconnected = false;
			self.services = [];
			self.requestUpdate();
			
			setTimeout(function()
			{
				if(self.concom)
				{
					console.log("Retrying platform connection...");
					self.subscribeToServices(interval);
				}
				else
				{
					console.log("Subcribe terminated due to component disconnect");
				}
			}, interval);
		});
	}
	
	subscribeToPlatforms(interval)
	{
		if(interval===undefined)
			interval = 5000;
			
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
			console.log("Could not reach platform.");
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
	
	getQueries() 
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
	}
	
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
	
	updatePlatform(platform)
	{
		console.log("update platform");
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
}
if(customElements.get('jadex-registry') === undefined)
	customElements.define('jadex-registry', RegistryViewElement);
