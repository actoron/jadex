import { LitElement, html, css } from 'lit-element';

class PlatformsElement extends LitElement {

	reversed = false;
	platforms = [];
	serverdown = false;
	
	constructor() {
		super();
		
		console.log("platforms");
		
		var self = this;
		
		jadex.getIntermediate('webjcc/subscribeToPlatforms',
			function(resp)
			{
				console.log(resp.data.service);
				self.updatePlatform(resp.data.service.name, resp.data.service.type);
				//return this.PROMISE_DONE;
			},
			function(err)
			{
				console.log("Err: "+JSON.stringify(err));
				self.serverdown = true;
				self.requestUpdate();
				//return this.PROMISE_DONE;
			}
		);
	}
	
	/*static get styles() {
	    return css`
	    	:host {
	    		color: green;
	    	}
	    `;
	}*/
	
	render() {
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
		console.log("updatePlatform: "+platform+" "+rem);
		
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


/*<platforms>

	<div class="actwtable section">
		<div if="{lang=='en'}">
			<h3 id="HConnectedPlatforms">Connected Platforms</h3>
			<p>This page shows a self-updating list of remote platforms known to this Jadex platform.</p>
		</div>
		<div if="{lang=='de'}">
			<h3 id="HConnectedPlatforms">Verbundene Plattformen</h3>
			<p>Diese Seite zeigt eine selbst-aktualisiernde Liste von Jadex-Plattformen.</p>
		</div>
		<table class="{down: serverdown}">
			<tbody>
				<tr>
					<th>Name</th>
					<!-- <th>Connected</th> 
					<th>Protocol</th>-->
				</tr>
				
				<tr each="{x in orderBy(platforms)}"> <!-- class="{connecting: !x.connected}" --> 
					<td><a href="#/platform/{x}">{x}</a></td>
					<!--  <td>{x.connected}</td>
					<td>{x.protocol}</td> -->
				</tr>
			</tbody>
		</table>
	</div>
	
	<script>
		var self = this;
		self.reversed = false;
		self.platforms = [];
		self.serverdown = false;
	
		// todo: order by name
		orderBy(data) 
		{ 
			var order = self.reversed ? -1 : 1;
			
			var res = data.slice().sort(function(a, b) 
			{ 
				return a===b? 0: a > b? order: -order 
			});
			
			/ *res.forEach(function(q) 
			{ 
				console.log(q); 
			})* / 
			return res; 
		}
		
		function updatePlatform(platform, rem)
		{
			//console.log("updatePlatform: "+platform+" "+rem);
			
			var	found = false;
			
			var i;
			for(i=0; i<self.platforms.length; i++)
			{
				found = self.platforms[i]===platform;
				if(found)
				{
					if(rem)
						self.platforms.splice(i, 1);
					break;
				}
			}
			
			if(!found && !rem)
				self.platforms.push(platform);
			
			self.update();
		}
		
		self.getIntermediate('webjcc/subscribeToPlatforms',
			function(resp)
			{
				updatePlatform(resp.data.service.name, resp.data.service.type);
				//return this.PROMISE_DONE;
			},
			function(err)
			{
				console.log("Err: "+JSON.stringify(err));
				self.serverdown = true;
				self.update();
				//return this.PROMISE_DONE;
			});
	</script>

</platforms>*/


