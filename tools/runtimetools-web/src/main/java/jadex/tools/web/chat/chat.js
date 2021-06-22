let { LitElement, html, css } = modLoad('lit-element');
let { BaseElement } = modLoad('base-element');
let { CidElement } = modLoad('cid-element');

// Tag name 'jadex-chat'
class ChatElement extends CidElement 
{
	init() 
	{
		console.log("chat");
		this.app.lang.listeners.add(this);
		this.connected = false;
		this.terminate = null;
		this.concom = false;
		this.users = {};
	}
	
	postInit()
	{
		var self = this;
		this.subscribe();
		
		this.searchUsers().then(users =>
		{
			for(var user of users)
			{
				var cid = user.serviceIdentifier.providerId;
				self.updateChatUser(cid);
			}
			
			// update view after users have been searched
			self.requestUpdate();
		}).catch(ex => console.log(ex));
	}
	
	connectedCallback()
	{
		super.connectedCallback();
		this.concom = true;	
	}
	
	disconnectedCallback()
	{
		super.disconnectedCallback();
		this.concom = false;	
		this.terminateSubscription();
	}

	subscribe()
	{
		var self = this;
		var interval = 10000;
		self.terminate = jadex.getIntermediate(this.getMethodPrefix()+'&methodname=subscribeToEvents&returntype=jadex.commons.future.ISubscriptionIntermediateFuture',
		function(response)
		{
			self.connected = true;
			console.log("subscribed: "+response.data);
			//self.updateMe(response.data);
			var ce = response.data;
			
			if("message"===ce.type)
			{
				//console.log("message: "+ce.value);
				self.addMessage(ce.componentIdentifier, ce.value, ce.nick, ce.privateMessage, false);
			}
			else if("statechange"===ce.type)
			{
				console.log("state change: "+ce);
				self.setUserState(ce.componentIdentifier, "dead"!==ce.value, "typing"===ce.value, "away"===ce.value, ce.nick, ce.image);
			}
			else if("file"===ce.type)
			{
				var ti = ce.value;
				
				console.log("transfer: "+ti);
				
				//self.updateTransfer(ti);
							
				if(ti.download && "waiting"===ti.state)
				{
					//notifyChatEvent("newfile", ti.other, ti, false);
								
/*					if(panel.isShowing())
					{
						acceptFile(ti).then(filepath =>
						{
							console.log("accept file");
							//getService().acceptFile(ti.id, filepath)
						})
						.catch(ex =>
						{
							console.log("ex: "+ex);
							//getService().rejectFile(ti.getId());
						});
					}*/
				}
			}
			else if("user"===ce.type)
			{
				var event = ce.value;
							
				console.log("user event: "+event);
							
				if(event.type===0)
				{
					var cid = event.service.serviceIdentifier.providerId;
					console.log("user added event: "+id);
					self.updateChatUser(event);
				}
				else if(event.type===1)
				{
					var cid = event.service.providerId;
					console.log("user removed: "+cid);
					self.setUserState(cid, false, false, false, ce.nick, ce.image);
				}
			}
			
			// Update view after each event
			self.requestUpdate();
		},
		function(err)
		{
			console.log("Could not reach platform.");
			console.log("Err: "+JSON.stringify(err));
			
			self.connected = false;
			self.requestUpdate();
			
			setTimeout(function()
			{
				if(self.concom)
				{
					console.log("Retrying platform connection...");
					self.subscribe();
				}
				else
				{
					console.log("Subcribe terminated due to web component disconnect");
				}
			}, interval);
		});
	}
	
	terminateSubscription()
	{
		if(self.terminate!=null)
		{
			console.log("terminate subscription");
			self.terminate();
		}
	}
	
	getMethodPrefix() 
	{
		return 'webjcc/invokeServiceMethod?cid='+this.cid+'&servicetype=jadex.tools.web.chat.IJCCChatService';
	}
	
	sendMessage(e)
	{
		var self = this;
		var msg = this.shadowRoot.getElementById("msg").value;
		var url = this.getMethodPrefix()+'&methodname=message'+
			'&args_0='+msg+"&argtypes_0=java.lang.String"+
			'&args_1='+'null'+"&argtypes_0=jadex.bridge.IComponentIdentifier[]"+
			'&args_2='+'false'+"&argtypes_0=boolean";
		//url = encodeURIComponent(url);
		
		console.log("sendmsg: "+msg);
		console.log("sendmsg: "+url);
		
		axios.get(url, this.transform).then(function(resp)
		{
			// todo: show running components?!
			console.log("message called: "+resp.data);
			//self.createInfoMessage("Sent message "+resp.data); 
		});
	}
	
	addMessage(cid, text, nick, privatemessage, sendfailure)
	{
		var time = new Date().toLocaleTimeString();
		var col = sendfailure? "gray": privatemessage? "red": "black";
		var text = "<div style='color:"+col+"'>["+time+", "+nick+"]: "+text+"</div>";
		var elem = this.shadowRoot.getElementById("messages");
		elem.innerHTML+=text;
		//notifyChatEvent(sendfailure ? NOTIFICATION_MSG_FAILED : NOTIFICATION_NEW_MSG, cid, text, false);
		//setUserState(cid, Boolean.TRUE, null, null, null, null);
	}
	
	searchUsers()
	{
		var self = this;
		var url = this.getMethodPrefix()+'&methodname=getUsers';
		//url = encodeURIComponent(url);
		
		console.log("getUsers: "+url);

		return new Promise(function(resolve, reject) 
		{
			axios.get(url, self.transform).then(function(resp)
			{
				console.log("getUsers called: "+resp.data);
				resolve(resp.data);
			}).catch(ex => reject(ex));
		});
	}
	
	updateChatUser(cid)
	{
		var self = this;
		
		self.setUserState(cid, true);
		
		var cu = self.users[cid.name];
		
		if(cu==null || cu.nickUnknown)
		{
			self.getNickName(cid).then(nick =>
			{
				console.log("nick is: "+nick);
				self.setUserState(cid, true, null, null, nick, null);
			}).catch(ex => console.log("ex: "+ex));
		}
		
		/*if(cu==null || cu.image===null)
		{
			self.getImage(cid).then(img =>
			{
				console.log("image is: "+img);
				self.setUserState(cid, true, null, null, null, img);
			}).catch(ex => console.log("ex: "+ex));
		}*/
		
		/*self.getStatus(cid).then(status =>
		{
			console.log("getstatus: "+status);
			self.setUserState(cid, true, "typing"===statuc, "away"===status);
		}).catch(ex => console.log("ex: "+ex));*/
	}
	
	setUserState(cid, online, typing, away, nickname, image)
	{
		console.log("setUserState: "+cid+" "+online);
		if(cid==null)
			throw new Exception("cid must not null");
		cid = cid.name;
		
		var isnew = false;
		var isdead = false;
		var cu = this.users[cid];
		if(cu==null && online)
		{
			console.log("create User "+cid+", "+online);
			cu = {'cid': cid};
			this.users[cid] = cu;
			isnew	= true;
		}
				
		if(cu!=null)
		{
			isdead = this.setOnline(online, cu);
					
			if(isdead)
			{
				this.users.remove(cid);
			}
			else
			{
				if(away!=null)
				{
					cu.away = away;
				}
				if(typing!=null)
				{
					cu.typing = typing;
				}
				if(nickname!=null)
				{
					cu.nick = nickname;
				}
				if(image!=null)
				{
					cu.image = image;
				}
				if(isnew)
				{
					console.log("new user: "+cid);
					//notifyChatEvent(NOTIFICATION_NEW_USER, cid, null, false);
				}
			}
		}
	}
	
	setOnline(online, cu)
	{
		if(online)
		{
			cu.lastupdate	= Date.now();	// User is known to be online.
		}
		else if(online!=null)
		{
			cu.lastupdate	= 0;	// User is known to be offline.			
		}
		
		return Date.now()-cu.lastupdate > 45000;	// Offline when no update for 45 seconds.
	}
	
	getNickName(cid)
	{
		var self = this;
		var url = this.getMethodPrefix()+'&methodname=getNickName'+
			'&args_0='+cid+"&argtypes_0=jadex.bridge.IComponentIdentifier"+
		//url = encodeURIComponent(url);
		console.log("sendmsg: "+url);
		
		return new Promise(function(resolve, reject) 
		{
			axios.get(url, this.transform).then(function(resp)
			{
				console.log("getNickname called: "+resp.data);
				resolve(resp.data);
			}).catch(ex => reject(ex));
		});
	}
	
	getImage(cid)
	{
		var self = this;
		var url = this.getMethodPrefix()+'&methodname=getImage'+
			'&args_0='+cid+"&argtypes_0=jadex.bridge.IComponentIdentifier"+
		//url = encodeURIComponent(url);
		console.log("sendmsg: "+url);
		
		return new Promise(function(resolve, reject) 
		{
			axios.get(url, this.transform).then(function(resp)
			{
				console.log("getNickname called: "+resp.data);
				resolve(resp.data);
			}).catch(ex => reject(ex));
		});
	}
	
	getUsers()
	{
		return Object.values(this.users);
	}
	
	asyncRender() 
	{
		return html`
		<div id="panel" class="grid-container">
			<div id="messages" class="grid-item grid-item-1">
			</div>
			<div id="users" class="grid-item grid-item-2">
				<table>
				${this.getUsers().map((user) => html`
				<tr @click="${e => console.log(e)}">
					<!--<td>${user.image}</td>-->
					<td>${user.nick}</td>
					<td>[${user.cid}]</td>
			    </tr>
				`)}
				</table>
			</div>
			<div class="grid-item grid-item-3 grid-container-inner">
				<p id="to">To: All</p>
				<input id="msg" type="text"></input>
				<button class="jadexbtn" type="button">Smiley</button>
				<button class="jadexbtn" type="button" @click="${e => this.sendMessage(e)}">Send</button>
			</div>
		</div>
		`;
	}
	
	static get styles() 
	{
		var ret = [];
		if(super.styles!=null)
			ret.push(super.styles);
		ret.push(
		    css`
	    	.grid-container {
				display: grid;
				grid-template-columns: 3fr 1fr; 
				grid-template-rows: 1fr minmax(min-content, max-content);
				grid-gap: 10px;
				height: calc(60vh);
			}
			.grid-item {
				/*background-color: red;*/
			}
			.grid-item-3 {
				grid-column: span 2;
			}
			.grid-container-inner {
				display: grid;
				grid-template-columns: minmax(min-content, max-content) auto minmax(min-content, max-content) minmax(min-content, max-content); 
				grid-gap: 10px;
			}
			#to {
				vertical-align: middle;
				margin: auto;
			}
		    `);
		return ret;
	}
}

if(customElements.get('jadex-chat') === undefined)
	customElements.define('jadex-chat', ChatElement);
