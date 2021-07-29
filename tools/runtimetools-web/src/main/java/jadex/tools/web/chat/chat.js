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
		this.users = {};
		this.selectedusers = [];
		this.userimage = this.getMethodPrefix()+'&methodname=loadResource&args_0=jadex/tools/web/chat/images/user.png';
		this.overlay_away = this.getMethodPrefix()+'&methodname=loadResource&args_0=jadex/tools/web/chat/images/overlay_away.png';
		this.overlay_typing = this.getMethodPrefix()+'&methodname=loadResource&args_0=jadex/tools/web/chat/images/overlay_typing.png';
		this.overlay_sending = this.getMethodPrefix()+'&methodname=loadResource&args_0=jadex/tools/web/chat/images/overlay_sending.png';
		this.previewimages = [];
		
		var res1 ="jadex/tools/web/chat/libs/emojibutton/emojibutton.js";
		var ures1 = this.getMethodPrefix()+'&methodname=loadResource&args_0='+res1+"&argtypes_0=java.lang.String";
		return this.loadScript(ures1);
	}
	
	postInit()
	{
		var self = this;
		
		const picker = new EmojiButton(
		{
			//rootElement: this.shadowRoot,
			//position: "auto",
			autoHide: false,
			//emojisPerRow: 7,
			//showCategoryButtons: false,
			showPreview: false,
			//emojiSize: '16px',
     		//style: 'twemoji', //native
		});
		const trigger = this.shadowRoot.getElementById("emoji");
		picker.on('emoji', selection => {
  			trigger.innerHTML = selection;
			this.shadowRoot.getElementById("msg").value += selection;
		});
		trigger.addEventListener('click', () => picker.togglePicker(trigger));
		
		this.shadowRoot.getElementById('msg').addEventListener('keyup', function onEvent(e) 
		{
			if(e.keyCode === 13)
				self.postMessage();
		});
		
		var sheet = new CSSStyleSheet();
		sheet.insertRule('.away { background-image: url('+this.overlay_away+") }", 0);
		sheet.insertRule('.typing { background-image: url('+this.overlay_typing+") }", 0);
		sheet.insertRule('.sending { background-image: url('+this.overlay_sending+") }", 0);
		sheet.insertRule('.imageanon { background-image: url('+this.userimage+") }", 0);
		sheet.insertRule('.imageuser { background-image: url($("data:image/png;base64,"+user.image.__base64)}', 0);
		self.shadowRoot.adoptedStyleSheets = self.shadowRoot.adoptedStyleSheets.concat(sheet);
		
		self.subscribe(10000);
		self.updateUserList();
	}
	
	updateUserList()
	{
		var self = this;
		this.users = {};
		this.searchUsers().then(users =>
		{
			for(var user of users)
			{
				var cid = user.serviceIdentifier.providerId.name;
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

	subscribe(interval)
	{
		var self = this;
		if(interval==null)
			interval = 10000;
		self.terminate = jadex.getIntermediate(this.getMethodPrefix()
			+'&args_0='+self.cid+"&argtypes_0=jadex.bridge.IComponentIdentifier"
			+'&methodname=subscribeToEvents&returntype=jadex.commons.future.ISubscriptionIntermediateFuture',
		function(response)
		{
			self.connected = true;
						
			//console.log("subscribed: "+response.data);
			//self.updateMe(response.data);
			var ce = response.data;
			
			if("message"===ce.type)
			{
				console.log("message: "+ce.value);
				self.addMessage(ce.componentIdentifier.name, ce.value, ce.nick, ce.privateMessage, false);
			}
			else if("image"===ce.type)
			{
				console.log("image: "+ce);
				var url = 'data:image/png;base64,'+ce.value.__base64;
				self.addMessage(ce.componentIdentifier.name, "<img src='"+url+"'></img>", ce.nick, ce.privateMessage, false);
			}
			else if("statechange"===ce.type)
			{
				console.log("state change: "+ce);
				self.setUserState(ce.componentIdentifier.name, "dead"!==ce.value, "typing"===ce.value, "away"===ce.value, ce.nick, ce.image?.__base64);
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
					var cid = event.service.serviceIdentifier.providerId.name;
					//console.log("user added event: "+cid);
					self.updateChatUser(cid);
				}
				else if(event.type===1)
				{
					var cid = event.service.providerId.name;
					//console.log("user removed: "+cid);
					self.setUserState(cid, false, false, false, ce.nick, ce.image?.__base64);
				}
			}
			
			// Update view after each event
			self.requestUpdate();
			//console.log("users: "+self.users);
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
					// hack, should not be in subscribe
					self.updateUserList();
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
	
	postMessage(e)
	{
		var self = this;
		var msg = this.shadowRoot.getElementById("msg").value;
		this.shadowRoot.getElementById("msg").value = "";
		var url = this.getMethodPrefix()+'&methodname=postMessage'+
			'&args_0='+msg+"&argtypes_0=java.lang.String"+
			'&args_1='+'null'+"&argtypes_1=jadex.bridge.IComponentIdentifier[]"+
			'&args_2='+'false'+"&argtypes_2=boolean"+
			'&args_3='+self.cid+"&argtypes_3=jadex.bridge.IComponentIdentifier";
		//url = encodeURIComponent(url);
		
		//console.log("sendmsg: "+msg);
		//console.log("sendmsg: "+url);
		
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
		elem.scrollTop = elem.scrollHeight; // scroll to newest text
		//notifyChatEvent(sendfailure ? NOTIFICATION_MSG_FAILED : NOTIFICATION_NEW_MSG, cid, text, false);
		//setUserState(cid, Boolean.TRUE, null, null, null, null);
	}
	
	searchUsers()
	{
		var self = this;
		var url = this.getMethodPrefix()+'&methodname=getUsers'+
		'&args_0='+self.cid+"&argtypes_0=jadex.bridge.IComponentIdentifier";
		//url = encodeURIComponent(url);
		
		console.log("getUsers: "+url);

		return new Promise(function(resolve, reject) 
		{
			axios.get(url, self.transform).then(function(resp)
			{
				//console.log("getUsers called: "+resp.data);
				resolve(resp.data);
			}).catch(ex => reject(ex));
		});
	}
	
	updateChatUser(cid)
	{
		console.log("user: "+cid);
		
		var self = this;
		
		self.setUserState(cid, true);
		
		var cu = self.users[cid];
		
		if(cu==null || cu.nick==null)
		{
			self.getNickName(cid).then(nick =>
			{
				console.log("nick is: "+nick+" for: "+cid);
				self.setUserState(cid, true, null, null, nick, null);
			}).catch(ex => console.log("ex: "+ex));
		}
		
		if(cu==null || cu.image==null)
		{
			self.getImage(cid).then(img =>
			{
				//console.log("image is: "+cu.cid+" "+img);
				self.setUserState(cid, true, null, null, null, img);
			}).catch(ex => console.log("ex: "+ex));
		}
		
		self.getStatus(cid).then(status =>
		{
			console.log("getstatus: "+status+" for: "+cid);
			self.setUserState(cid, true, "typing"===status, "away"===status);
		}).catch(ex => console.log("ex: "+ex));
	}
	
	setUserState(cid, online, typing, away, nickname, image)
	{
		console.log("setUserState: "+cid+" "+online+" "+nickname);
		if(cid==null)
			throw new Exception("cid must not null");
		
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
				delete this.users[cid];
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
					//console.log("setting user nick: "+cid+" "+nickname);
					cu.nick = nickname;
				}
				if(image!=null)
				{
					//console.log("setting user image: "+cid+" "+image);
					cu.image = image;
				}
				if(isnew)
				{
					console.log("new user: "+cid);
					//notifyChatEvent(NOTIFICATION_NEW_USER, cid, null, false);
				}
			}
		}
		
		this.requestUpdate();
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
			'&args_0='+cid+"&argtypes_0=jadex.bridge.IComponentIdentifier";
		//url = encodeURIComponent(url);
		console.log("getNickName: "+url);
		
		return new Promise(function(resolve, reject) 
		{
			axios.get(url, self.transform).then(function(resp)
			{
				console.log("getNickname called: "+resp.data);
				resolve(resp.data);
			}).catch(ex => reject(ex));
		});
	}
	
	setNickName(nick, cid)
	{
		var self = this;
		var url = this.getMethodPrefix()+'&methodname=setNickName'
			+'&args_0='+nick+"&argtypes_0=java.lang.String"
			+'&args_1='+cid+"&argtypes_1=jadex.bridge.IComponentIdentifier";
		//url = encodeURIComponent(url);
		console.log("setNickName: "+url);
		
		return new Promise(function(resolve, reject) 
		{
			axios.get(url, self.transform).then(function(resp)
			{
				//console.log("setNickname called: "+nick);
				resolve(resp.data);
			}).catch(ex => reject(ex));
		});
	}
	
	getImage(cid)
	{
		var self = this;
		var url = this.getMethodPrefix()+'&methodname=getImage'+
			'&args_0='+cid+"&argtypes_0=jadex.bridge.IComponentIdentifier";
		//url = encodeURIComponent(url);
		console.log("getImage: "+url);
		
		// getImage() delivers the result as raw byte[]
		// should it use base64 str?!
		return new Promise(function(resolve, reject) 
		{
			axios.get(url, {responseType: 'arraybuffer'}).then(function(resp)
			{
				//console.log("getImage called: "+resp.data);
				var imgstr = btoa(String.fromCharCode.apply(null, new Uint8Array(resp.data)));
				if(imgstr!=null && imgstr.length==0)
					imgstr = null;
				resolve(resp.data!=null && resp.data.length==0? null: imgstr);
			}).catch(ex => reject(ex));
		});
	}
	
	getStatus(cid)
	{
		var self = this;
		var url = this.getMethodPrefix()+'&methodname=getStatus'+
			'&args_0='+cid+"&argtypes_0=jadex.bridge.IComponentIdentifier";
		//url = encodeURIComponent(url);
		console.log("getStatus: "+url);
		
		return new Promise(function(resolve, reject) 
		{
			axios.get(url, self.transform).then(function(resp)
			{
				console.log("getStatus called: "+resp.data);
				resolve(resp.data);
			}).catch(ex => reject(ex));
		});
	}
	
	getUsers()
	{
		return Object.values(this.users);
	}
	
	setTo(user)
	{
		var elem = this.shadowRoot.getElementById("to");
		if(user==null)
		{
			this.selectedusers = [];
			elem.textContent = "To: All";
		}
		else
		{
			var contains = false;
			for(var i=0; i<this.selectedusers.length; i++)
			{
				if(this.selectedusers[i].cid===user.cid)
				{
					contains = true;
					this.selectedusers.splice(i, 1);
					break;
				}
			}
			if(!contains)
				this.selectedusers.push(user);
			
			var text = "To: ";
			for(var i=0; i<this.selectedusers.length; i++)
			{
				text += this.selectedusers[i].nick;
				if(i+1<this.selectedusers.length)
					text += ", ";
			}
			elem.textContent = text;
			
			for(var u of this.selectedusers)
			{
				console.log("seluser: "+u.cid);
			}
		}
	}
	
	resizeImage(file, max)
	{
		return new Promise(function(resolve, reject) 
		{
			if(!file.type.match(/image.*/)) 
			{
				reject("file is not an image: "+file.type);
			}
			else
			{
	        	console.log('An image has been selected');
				if(max==null)
					max = 50;
		        // Load the image
		        var reader = new FileReader();
		        reader.onload = function(revent) 
				{
		        	var image = new Image();
		            image.onload = function(ievent) 
					{
		                var canvas = document.createElement('canvas');
		                var width = image.width;
		                var height = image.height;

						var fac = Math.max(width, height)/max;
						if(fac>1)
						{
							width /= fac;
							height /= fac; 
						}
		                canvas.width = width;
		                canvas.height = height;
		                canvas.getContext('2d').drawImage(image, 0, 0, width, height);
		                //var img = canvas.toDataURL(file.type);
						canvas.toBlob(resolve, file.type);//, QUALITY);
					}
		            image.src = revent.target.result;
		        }
		        reader.readAsDataURL(file);
			}
		});
	}
	
	// Upload avatar image
	uploadImage(e)
	{
		//var ii = this.shadowRoot.getElementById("imageinput");
		var ii = e.target;
		this.resizeImage(ii.files[0], 50).then(img =>
		{
			var fd = new FormData();
			//fd.append('args_0', ii.files[0]);
			fd.append('args_0', img);
			fd.append('args_1', this.cid);
			fd.append('argtypes_0', "byte[]");
			fd.append('argtypes_1', "jadex.bridge.IComponentIdentifier");
			
			var url = this.getMethodPrefix()+'&methodname=setImage';
			
			//axios.post(url, {args_0: fd, args_1: this.cid}).then(function(resp)
			axios.post(url, fd).then(function(resp)
			{
				console.log("setImage called: "+resp.data);
				//self.createInfoMessage("Sent message "+resp.data); 
			});
		});
	}
	
	addPreviewImage(e)
	{
		var self = this;
		//var ii = this.shadowRoot.getElementById("imageinput");
		var ii = e.target;
				
		this.resizeImage(ii.files[0], 500).then(img =>
		{
			///* Preview code
			var uc = window.URL || window.webkitURL;
  	 		var url = uc.createObjectURL(img);
			//self.addMessage(ce.componentIdentifier.name, "<img src='"+url+"'></img>", ce.nick, ce.privateMessage, false);
			ii.files[0].url = url;
			ii.files[0].image = img;
			this.previewimages.push(ii.files[0]);
			this.requestUpdate();
		});
	}
	
	removePreviewImage(image)
	{
		var ret = false;
		for(var i=0; i<this.previewimages.length; i++)
		{
			if(image===this.previewimages[i])
			{
				this.previewimages.splice(i, 1);
				ret = true;
				break;
			}
		}
		return ret;
	}
	
	postPreviewImages()
	{
		for(var file of this.previewimages)
		{
			this.postImage(file.image);
		}
		this.previewimages.length = 0; // clear the array
	}
	
	postImage(img)
	{
		var fd = new FormData();
		fd.append('args_0', img);
		fd.append('args_1', null);
		fd.append('args_2', false);
		fd.append('args_3', this.cid);
		fd.append('argtypes_0', "byte[]");
		fd.append('argtypes_1', "jadex.bridge.IComponentIdentifier[]");
		fd.append('argtypes_2', "boolean");
		fd.append('argtypes_3', "jadex.bridge.IComponentIdentifier");
		
		var url = this.getMethodPrefix()+'&methodname=postImage';
		
		//axios.post(url, {args_0: fd, args_1: this.cid}).then(function(resp)
		axios.post(url, fd).then(function(resp)
		{
			console.log("postImage called: "+resp.data);
			//self.createInfoMessage("Sent message "+resp.data); 
		});
	}
	
	/*tell(text)
	{
		var sendusers = {};
		//var id = ++reqcnt;
		
		int[] sels = usertable.getSelectedRows();
		
		IComponentIdentifier[] recs = new IComponentIdentifier[sels.length];
		if(sels.length>0)
		{
			for(int i=0; i<sels.length; i++)
			{
				ChatUser cu = (ChatUser)usertable.getModel().getValueAt(sels[i], 0);
				cu.addMessage(id);
				sendusers.add(cu);
				recs[i] = cu.getComponentIdentifier();
			}
		}
		else
		{
			for(ChatUser cu: usermodel.getUsers())
			{
				cu.addMessage(id);
				sendusers.add(cu);				
			}
		}
		
		usertable.repaint();

		getService().message(text, recs, true).addResultListener(new SwingIntermediateDefaultResultListener<IChatService>()
		{
			public void customIntermediateResultAvailable(final IChatService chat)
			{
				ChatUser cu = usermodel.getUser(((IService)chat).getServiceId().getProviderId());
				if(cu!=null)
				{
					sendusers.remove(cu);
					cu.removeMessage(id);
					usertable.repaint();
				}
			}
			
			public void customFinished()
			{
				ret.setResult(null);
				printFailures();
			}
			
			public void customExceptionOccurred(Exception exception)
			{
				ret.setException(exception);
				printFailures();
			}
			
			protected void	printFailures()
			{
				if(!sendusers.isEmpty())
				{
					StringBuffer	nick	= new StringBuffer();
					nick.append("failed to deliver message to");
					for(ChatUser cu: sendusers)
					{
						nick.append(" ");
						nick.append(cu.getNick());
						nick.append(",");
						cu.removeMessage(id);
					}
					usertable.repaint();
					addMessage(((IService)getService()).getServiceId().getProviderId(),
						text, nick.substring(0, nick.length()-1), false, true); // Strip last comma.
				}
			}
		});
		
		return ret;
	}*/
	
	getPlatformName(cid)
	{
		var rootname = cid;
		var idx;
		if((idx = rootname.indexOf('@')) != -1)
			rootname = rootname.substring(idx + 1);
		if((idx = rootname.lastIndexOf(':')) != -1)
			rootname = rootname.substring(idx + 1);
		//console.log("platform name is: "+rootname+" "+cid);
		return rootname;
	}
	
	asyncRender() 
	{
		return html`
		<div id="panel" class="grid-container">
			<div id="messages" class="yscrollable">
			</div>
			<div id="users" class="yscrollable">
				<table>
				${this.getUsers().map((user) => html`
				<tr>
					<td>
						<div class="grid-container2" @click="${e => {if(this.getPlatformName(user.cid)===this.cid) this.shadowRoot.getElementById('imageinput').click(); }}">
							<img class="grid-item-21" id="user" src="${user.image!=null? 'data:image/png;base64,'+user.image: this.userimage}"/>
							<img class="grid-item-21" id="overlay" src="${user.away? this.overlay_away: user.typing? this.overlay_typing: user.sending? this.overlay_sending: ''}"/>
						</div>
						<input type="file" id="imageinput" style="display: none;" @change="${e => this.uploadImage(e)}" />
					</td>
					<td @click="${e => {if(this.getPlatformName(user.cid)===this.cid) e.target.contentEditable=true;}}" 
						@blur="${e => {e.target.contentEditable=false; this.setNickName(e.target.textContent, user.cid);}}">${user.nick}</td>
					<td>[${user.cid}]</td>
			    </tr>
				`)}
				</table>
			</div>
			<div class="grid-item grid-item-3 grid-container-inner">
				<p id="to" @click="${e => this.setTo(null)}">To: All</p>
				<input id="msg" type="text"></input>
				<button id="emoji" class="jadexbtn" type="button">&#128512;</button>
				<input type="file" id="imageinput2" style="display: none;" @change="${e => this.addPreviewImage(e)}" />
				<button id="file" class="jadexbtn" type="button" @click="${e => this.shadowRoot.getElementById('imageinput2').click()}">&#128206;</button>
				<button class="jadexbtn" type="button" @click="${e => {this.postMessage(e); this.postPreviewImages();}}">Send</button>
				<div id="imagepreview" class="previews-element previews-container ${this.previewimages.length>0? '': 'hidden'}">
				${this.previewimages.map((image) => html`
					<div class="preview-element">
						<img class="h100px" src="${image.url}"/>
						<div class="preview-textbox">
							<div class="bold">${image.name}</div> 
							<div>${Math.ceil(image.size/1024)+" KB"}</div>
						</div>
						<div class="close relative" @click="${e => {this.removePreviewImage(image); this.requestUpdate();}}"></div>
					</div>
				`)}
				</div>
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
				grid-template-columns: 3fr 2fr; 
				grid-template-rows: 1fr minmax(min-content, max-content);
				grid-gap: 10px;
				height: calc(60vh);
			}
			.yscrollable {
				overflow-y: auto;
			}
			.grid-item {
				/*background-color: red;*/
			}
			.grid-item-3 {
				grid-column: span 2;
			}
			.grid-container-inner {
				display: grid;
				grid-template-columns: minmax(min-content, max-content) auto minmax(min-content, max-content) minmax(min-content, max-content) minmax(min-content, max-content); 
				grid-gap: 10px;
			}
			#to {
				vertical-align: middle;
				margin: auto;
			}
			#users {
				background: transparent;
			}
			#user {
				position: relative;
			}
			#overlay {
				position: relative;
				z-index: 1;
			}
			.grid-container2 {
				display: grid;
				grid-template-columns: 1fr; 
			}
			.grid-item-21 {
				grid-area: 1 / 1 / 1 / 2;
			}
			.previews-element {
				grid-area: 2 / 1 / 2 / 6;
			}
			.previews-container {
				display: flex;
				flex-shrink: 0;
				flex-wrap: wrap;
				margin: 1px 0 5px;
				overflow-x: auto;
				overflow-y: hidden;
				white-space: nowrap;
			}
			.preview-textbox {
				display: flex;
  				align-content: center;
				justify-content: center;
  				flex-direction: column;
				margin-left: 5px;
			}
			.preview-element {
				display: flex;
				flex-shrink: 0;
				flex-wrap: nowrap;
				margin: 0 10px 10px 0; 
				border: 1px black dotted;
				padding: 2px;
			}
			.aligncenter {
				display: flex;
				align-items: center;
			}
			.w100px {
				width: 100px;
			}
			.h100px {
				height: 100px;
			}
		    `);
		return ret;
	}
}

if(customElements.get('jadex-chat') === undefined)
	customElements.define('jadex-chat', ChatElement);
