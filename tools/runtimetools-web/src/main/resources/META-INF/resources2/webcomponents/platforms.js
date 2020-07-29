import { LitElement, html, css } from 'lit-element';
import { BaseElement } from 'base-element';

class PlatformsElement extends BaseElement 
{
	reversed = false;
	platforms = [];
	//serverdown = false;
	
	constructor() 
	{
		super();	
		console.log("platforms");
		this.subscribe();
	}
	
	subscribe()
	{
		var self = this;
		jadex.getIntermediate('webjcc/subscribeToPlatforms',
			function(resp)
			{
				//console.log(resp.data.service);
				self.updatePlatform(resp.data.service.name, resp.data.service.type);
				//return this.PROMISE_DONE;
			},
			function(err)
			{
				console.log("Could not reach Jadex webjcc.");
				//console.log("Err: "+JSON.stringify(err));
				//self.serverdown = true;
				self.requestUpdate();
				
				setTimeout(function()
				{
					console.log("Retrying Jadex webjcc connection...");
					self.subscribe();
				}, 5000);
			}
		);
	}
	
	render() 
	{
		return html`
			<link rel="stylesheet" href="css/style.css">
			<div class="actwtable section">
				<div>
					<h3 id="HConnectedPlatforms">Connected Platforms</h3>
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
	
}

customElements.define('jadex-platforms', PlatformsElement);


