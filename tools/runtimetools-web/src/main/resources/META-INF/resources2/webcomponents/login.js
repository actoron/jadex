let { LitElement, html, css } = modLoad('lit-element');
let { BaseElement } = modLoad('base-element');

class LoginElement extends BaseElement 
{
	//loggedin; WARNING: must NOT be declared. Property changes do not work then
	
	constructor() 
	{
		super();	
		//this.loggedin = false;
		this.no = 0;
		console.log("login");
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
		this.isLoggedIn().then(()=>{self.requestUpdate();});
		
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
			<div class="flexcontainerrow ${BaseElement.login.isLoggedIn()? 'hidden': ''}">
				<input class="flexcellgrow mt-1 mb-1" id="pass" name="platformpass" type="text" placeholder="${BaseElement.language.getLanguage()? 'Platform password': 'Plattformpasswort'}"></input>
				<button class="btn btn-primary mt-1 mb-1 ml-1" @click="${e => {this.login(this.shadowRoot.getElementById('pass').value)}}">Login</button>
			</div>
			<button class="btn btn-primary mt-1 mb-1 ml-1 flow-right ${BaseElement.login.isLoggedIn()? '': 'hidden'}" @click="${e => {this.logout()}}">Logout</button>
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
			BaseElement.login.setLogin(true);
			//window.location.href = "/#/platforms";
			self.createInfoMessage("logged in");
		})
		.catch(function(err) 
		{
			//console.log("login failed: "+err);	
			self.createErrorMessage("login failed", err);
			//self.loggedin = false;
			BaseElement.loginsetLogin(false);
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
			BaseElement.loginsetLogin(false);
			self.createInfoMessage("logged out");
		})
		.catch(function(err) 
		{
			console.log("logout failed: "+err);	
			//self.loggedin = false;
			BaseElement.loginsetLogin(false);
			self.createErrorMessage("logout failed", err);
		});
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
				BaseElement.login.setLogin(resp.data);
				resolve(self.loggedin);
			})
			.catch(function(err) 
			{
				console.log("check failed: "+err);
				self.createErrorMessage("check failed", err);
				//BaseElement.loginsetLogin(false);	
				reject(err);
			});
		});
	}
}

if(customElements.get('jadex-login') === undefined)
	customElements.define('jadex-login', LoginElement);

