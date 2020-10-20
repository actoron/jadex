import { LitElement, html, css } from 'lit-element';
import { BaseElement } from 'base-element';

class PlatformsElement extends BaseElement 
{
	reversed = false;
	platforms = [];
	connected = false;
	termcmd = null;
	comconn = false;
	
	static get properties() 
	{
		var ret = {};
		if(super.properties!=null)
		{
			for(let key in super.properties)
				ret[key]=super.properties[key];
		}
		ret['reversed'] = {attribute: false, type: Boolean};
		ret['platforms'] = {attribute: false};
		ret['connected'] = {attribute: false, type: Boolean};
		return ret;
	}
	
	constructor() 
	{
		super();	
		console.log("platforms");
	}
	
	connectedCallback() 
	{
		this.comconn = true;
		console.log("connected platforms: "+this.comconn);
		super.connectedCallback();
		this.subscribe(5000);
	}
	
	disconnectedCallback()
	{
		console.log("disconnected platforms: "+this.comconn);
		this.comconn = false;
		this.terminateSubscription();
	}
	
	terminateSubscription()
	{
		if(this.termcmd!=null)
			this.termcmd().then(() => {console.log("Terminated subscription.")})
				.catch(() => {console.log("Could not terminate subscription")});
	}
	
	subscribe(interval)
	{
		var self = this;
		
		this.terminateSubscription();
			
		var tc = jadex.getIntermediate('webjcc/subscribeToPlatforms',
			function(resp)
			{
				console.log("Set up subscription");
				self.updatePlatform(resp.data.service.name, resp.data.service.type);
				self.connected = true;
				self.termcmd = tc;
			},
			function(err)
			{
				console.log("Could not reach Jadex webjcc.");
				if(err!=null && err.response!=null && err.response.status==401)
				{
					self.createErrorMessage("Login required to WebJCC platform (use platform secret)");
				}
				else
				{
					self.createErrorMessage("Could not reach Jadex WebJCC platform", err);
				}
				//console.log("Err: "+JSON.stringify(err));
				self.connected = false;
				self.platforms = [];
				self.requestUpdate();
				
				setTimeout(function()
				{
					if(self.comconn)
					{
						console.log("Retrying Jadex webjcc connection...");
						self.subscribe(interval);
					}
					else
					{
						console.log("Subcribe terminated due to component disconnect");
					}
				}, interval);
			}
		);
	}
	
	render() 
	{
		return html`
			<div class="actwtable section">
				<div>
					<div class="head">
						<h1 class="m-0 p-0 inline" id="HConnectedPlatforms">Connected Platforms</h1>
						<span id="connected" class="dot fl ${this.connected? "green": "red"}"></span>
					</div>
					<p>This page shows a self-updating list of remote platforms known to this Jadex platform.</p>
				</div>
				<table>
					<tbody>
						<tr>
							<th>Name</th>
							<!-- <th>Connected</th> 
							<th>Protocol</th>-->
						</tr>
						
						${this.platforms.map((p) => html`<tr><td><a href="#/platform/${p}">${p}</a></td></tr>`)}
					</tbody>
				</table>
			</div>
		`;
	}
	
	// todo: order by name
	orderBy(data) 
	{ 
		var order = this.reversed ? -1 : 1;
		
		var res = data.slice().sort(function(a, b) 
		{ 
			return a===b? 0: a > b? order: -order 
		});
		
		/*res.forEach(function(q) 
		{ 
			console.log(q); 
		})*/ 
		return res; 
	}
	
	updatePlatform(platform, rem)
	{
		//console.log("updatePlatform: "+platform+" "+rem);
		
		var	found = false;
		
		var i;
		for(i=0; i<this.platforms.length; i++)
		{
			found = this.platforms[i]===platform;
			if(found)
			{
				if(rem)
					this.platforms.splice(i, 1);
				break;
			}
		}
		
		if(!found && !rem)
			this.platforms.push(platform);
		
		this.requestUpdate();
	}
	
	static get styles() 
	{
	    return css`
			.inline {
				display:inline
			}
	    	.dot {
				display: inline-block;
				border-radius: 50%;
   				width: 20px; /* CSS can't align width to height (of H3 here) :-( */
				height: 20px;
			}
			.red {
				background-color: red;
			}
			.green {
				background-color: green;
			}
	    `;
	}
	
}

if(customElements.get('jadex-platforms') === undefined)
	customElements.define('jadex-platforms', PlatformsElement);


