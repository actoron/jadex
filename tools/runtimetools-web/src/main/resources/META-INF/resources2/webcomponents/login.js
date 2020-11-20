import {LitElement, html, css} from '../libs/lit/lit-element.js';
import { BaseElement } from "../webcomponents/baseelement.js"

export class LoginElement extends BaseElement
{
	//loggedin; WARNING: must NOT be declared. Property changes do not work then
	
	constructor() 
	{
		super();	
		//this.loggedin = false;
		this.no = 0;
		console.log("login");
	}
	
	static loginhandler = 
	{
		loggedin: false,
		listeners: [],
		setLogin: function(loggedin)
		{
			if(this.loggedin!=loggedin && loggedin!=null)
			{
				console.log("login change happened")
				this.loggedin = loggedin;
				for(var i=0; i<this.listeners.length; i++)
				{
					this.listeners[i](this.loggedin);
				}
				
				BaseElement.loadedelements.forEach( elem => {
					console.log("Updating " + elem)
					elem.requestUpdate();
				});
			}
			console.log("loggedin is: "+this.loggedin);
		},
		isLoggedIn: function()
		{
			return this.loggedin;
		},
		addListener: function(listener)
		{
			this.listeners.push(listener);
		},
		removeListener: function(listener)
		{
			for(var i=0; i < this.listeners.length; i++) 
			{
				if(this.listeners[i] === listener) 
				{
					this.listeners.splice(i, 1);
					break;
			    } 
			}
		},
		updateLogin()
		{
			var self = this;
			return new Promise(function(resolve, reject) 
			{
				axios.get('webjcc/isLoggedIn', {headers: {'x-jadex-isloggedin': true}}, self.transform).then(function(resp)
				{
					//console.log("is logged in: "+resp);
					self.setLogin(resp.data);
					resolve(self.loggedin);
				})
				.catch(function(err) 
				{
					console.log("check failed: "+err);	
					reject(err);
				});
			});
		}
	}
	
	static get properties() 
	{
		var ret = {};
		if(super.properties!=null)
		{
			for(let key in super.properties)
				ret[key]=super.properties[key];
		}
		//ret['loggedin'] = {type: Boolean};
		return ret;
	}
	
	connectedCallback() 
	{
		super.connectedCallback();
		var self = this;
		console.log("connnnn")
		this.isLoggedIn().then((res)=>{
			self.requestUpdate();
			if (!res)
			{
				let pass = localStorage.getItem("platformpassword")
				console.log("session store pass " + pass);
				if (pass)
					self.login(pass);
			}
		});
		
		// turn on to cintinuously check if we are stll logged in
		//this.checkLoggedIn();
		
		/*super.init().then(()=>
		{
			super.connectedCallback();
			self.isLoggedIn();
			console.log("connected end");
		})
		.catch((err)=>console.log(err));*/
	}
	
	disconnectedCallback()
	{
		super.disconnectedCallback();
	
		// terminate login check
		this.no++;
	}
	
	checkLoggedIn(interval)
	{
		this.internalCheckPlatform(interval, ++this.no);
	}
	
	internalCheckLoggedIn(interval, no) 
	{
		var self = this;
		
		// terminate when another call to checkPlatform() has been performed
		//console.log("check platform: "+no+" "+this.no);
		if(no!=this.no)
		{
			console.log("terminate platform check: "+this.no+" "+no);
			return;
		}
	
		checkLoggedIn(interval).then(function()
		{
			this.isLoggedIn().then(function()
			{
				setTimeout(function(){self.internalCheckLoggedIn(interval, no)}, interval!=undefined? interval: 10000);
			})
			.catch(function(err)
			{
				setTimeout(function(){self.internalCheckLoggedIn(interval, no)}, interval!=undefined? interval: 10000);
			});
		});
	}
	
	render() 
	{
    	return html`
			<div class="${LoginElement.loginhandler.isLoggedIn()? 'hidden': ''}">
				<div class="flexcontainerrow">
					<input class="flexcellgrow mt-1 mb-1" id="pass" name="platformpass" type="text" placeholder="${BaseElement.language.getLanguage()? 'Platform password': 'Plattformpasswort'}"></input>
					<button class="btn btn-primary mt-1 mb-1 ml-1" @click="${e => {this.login(this.shadowRoot.getElementById('pass').value)}}">Login</button>
				</div>
				<input class"mt-1 mb-1 ml-1 flow-right" id="rememberpassword" type="checkbox" >${BaseElement.language.tl("Remember password")}</input>
			</div>
			<button class="btn btn-primary mt-1 mb-1 ml-1 flow-right ${LoginElement.loginhandler.isLoggedIn()? '': 'hidden'}" @click="${e => {this.logout()}}">Logout</button>
    	`;
 	}

	login(pass)
	{
		var self = this;
		axios.get('webjcc/login?pass='+pass, {headers: {'x-jadex-login': pass}}, self.transform).then(function(resp)
		//axios.get('webjcc/login?pass='+pass, self.transform).then(function(resp)
		{
			//console.log("logged in: "+resp);
			//self.loggedin = true;
			LoginElement.loginhandler.setLogin(true);
			
			if (typeof(Storage) !== undefined) {
				let checkbox = self.shadowRoot.getElementById("rememberpassword");
				if (checkbox.checked) {
					localStorage.setItem("platformpassword", pass);
					checkbox.checked = false
				}
			}
			
			//window.location.href = "/#/platforms";
			self.createInfoMessage("logged in");
		})
		.catch(function(err) 
		{
			//console.log("login failed: "+err);	
			self.createErrorMessage("login failed", err);
			//self.loggedin = false;
			LoginElement.loginhandler.setLogin(false);
		});
	}
	
	logout()
	{
		var self = this;
		axios.get('webjcc/logout', {headers: {'x-jadex-logout': true}}, self.transform).then(function(resp)
		//axios.get('webjcc/login?pass='+pass, self.transform).then(function(resp)
		{
			//console.log("logged out: "+resp);
			//self.loggedin = false;
			LoginElement.loginhandler.setLogin(false);
			self.createInfoMessage("logged out");
		})
		.catch(function(err) 
		{
			console.log("logout failed: "+err);	
			//self.loggedin = false;
			LoginElement.loginhandler.setLogin(false);
			self.createErrorMessage("logout failed", err);
		});
		localStorage.removeItem("platformpassword");
	}
	
	isLoggedIn()
	{
		var self = this;
		return new Promise(function(resolve, reject) 
		{
			axios.get('webjcc/isLoggedIn', {headers: {'x-jadex-isloggedin': true}}, self.transform).then(function(resp)
			{
				//console.log("is logged in: "+resp);
				//self.loggedin = resp.data;
				LoginElement.loginhandler.setLogin(resp.data);
				resolve(self.loggedin);
			})
			.catch(function(err) 
			{
				console.log("check failed: "+err);
				self.createErrorMessage("check failed", err);
				//LoginElement.loginhandler.setLogin(false);	
				reject(err);
			});
		});
	}
}

if(customElements.get('jadex-login') === undefined)
	customElements.define('jadex-login', LoginElement);

