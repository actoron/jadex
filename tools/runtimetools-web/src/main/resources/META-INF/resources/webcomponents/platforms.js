let { LitElement, html, css } = modLoad('lit-element');
let { BaseElement } = modLoad('base-element');

class PlatformsElement extends BaseElement 
{
	// sort order
	reversed = false;
	
	// discovered platforms
	platforms = [];
	
	// Conversation id of the active subscription
	callid = null;
	
	// Is the gui component connected (otherwise no subscriptions necessary)
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
		ret['callid'] = {attribute: false, type: String};
		return ret;
	}
	
	init()
	{
		this.app.lang.listeners.add(this);
		console.log('platforms');
	}
	
	/*postInit()
	{
		console.log('platforms postinit');
	}*/
	
	connectedCallback() 
	{
		this.comconn = true;
		//console.log("connected platforms: "+this.comconn);
		super.connectedCallback();
		this.subscribe(5000);
	}
	
	disconnectedCallback()
	{
		//console.log("disconnected platforms: "+this.comconn);
		this.comconn = false;
		this.terminateSubscription();
		super.disconnectedCallback();
	}
	
	terminateSubscription()
	{
		var self = this;
		if(self.callid!=null)
		{
			jadex.terminateCall(self.callid).then(() => 
			{
					//console.log("Terminated subscription: "+self.callid)
			})
			.catch(err => {console.log("Could not terminate subscription: "+err+" "+self.callid)});
		}
	}
	
	subscribe(interval)
	{
		var self = this;
		
		this.terminateSubscription();
			
		var callid = jadex.getIntermediate('webjcc/subscribeToPlatforms',
			function(resp)
			{
				// if no ongoing subscription -> dev/null
				//if(self.callid!=resp.callId)
				if(self.callid!==callid)
				{
					console.log("not current subscription (suc): "+callid+" "+self.callid);
					return;
				}
				
				self.createInfoMessage("Webjcc platform subscription successful");
					
				//console.log("Set up subscription");
				self.updatePlatform(resp.data.service.name, resp.data.type);
			},
			function(err)
			{
				console.log("Could not reach Jadex webjcc.");
				
				// if no ongoing subscription -> dev/null
				if(self.callid!==callid)
				//if(self.callid!=err?.response?.callId)
				{
					console.log("not current subscription (err): "+callid+" "+self.callid);
					return;
				}
				/*else
				{
					console.log("current subscription (err)"+callid+" "+self.callid);
				}*/
				
				if(err!=null && err.response!=null && err.response.status==401)
				{
					self.createErrorMessage("Login required to WebJCC platform (use platform secret)");
				}
				else
				{
					self.createErrorMessage("Could not reach Jadex WebJCC platform", err);
				}
				//console.log("Err: "+JSON.stringify(err));
				self.callid = null;
				
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
		//console.log("assinged: "+callid+" "+new Date());
		this.callid = callid;
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
			.left {
				text-align: left;
			}
	    `;
	}
	
	asyncRender() 
	{
		return html`
			<div>
				<div class="head">
					<h1 class="inline" id="HConnectedPlatforms">${this.app.lang.t('Connected Platforms')}</h1>
					<span id="connected" class="dot fl ${this.callid? "green": "red"}"></span>
				</div>
				<p>
					${this.app.lang.t('This page shows a self-updating list of remote platforms known to this Jadex platform.')}
				</p>
			</div>

			<div>
				<table>
					<tbody>
						<tr>
							<th class="left">Name</th>
							<!-- <th>${this.app.lang.t("Connected")}</th> 
							<th>Protocol</th>-->
						</tr>
						
						${this.platforms.map((p) => html`
						<tr>
							<td><a href="#/platform/${p}">${p}</a></td>
						</tr>`
						)}
					</tbody>
				</table>
			</div>
		`;
	}
	
}

if(customElements.get('jadex-platforms') === undefined)
	customElements.define('jadex-platforms', PlatformsElement);


