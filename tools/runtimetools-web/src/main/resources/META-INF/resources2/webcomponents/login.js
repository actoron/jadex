let { LitElement, html, css } = modLoad('lit-element');
let { BaseElement } = modLoad('base-element');

export class LoginElement extends BaseElement
{
	//loggedin; WARNING: must NOT be declared. Property changes do not work then
	
	static get properties() 
	{
		let ret = {};
		if(super.properties!=null)
		{
			for(let key in super.properties)
				ret[key]=super.properties[key];
		}
		//ret['loggedin'] = {type: Boolean};
		return ret;
	}
	
	init() 
	{
		this.no = 0;
		this.app.login.listeners.add(this);
		
		//console.log("login INITED");
	}
	
	postInit()
	{
		let pass = localStorage.getItem("platformpassword")
		//console.log("session store pass " + pass);
		if(pass)
			this.login(pass);
	}
	
	connectedCallback() 
	{
		super.connectedCallback();
		let self = this;
		//console.log("connnnn----")
		
		// turn on to continuously check if we are still logged in
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
		let self = this;
		
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
	
	asyncRender() 
	{
    	return html`
			<div class="${this.app.login.isLoggedIn()? 'hidden': ''}">
				<div class="flexcontainerrow">
					<input class="flexcellgrow mt-1 mb-1" id="pass" name="platformpass" type="text" placeholder="${this.app.lang.t('Platform password')}"></input>
					<button class="btn btn-primary mt-1 mb-1 ml-1" @click="${e => {this.login(this.shadowRoot.getElementById('pass').value)}}">Login</button>
				</div>
				<input class"mt-1 mb-1 ml-1 flow-right" id="rememberpassword" type="checkbox" >${this.app.lang.t("Remember password")}</input>
			</div>
			<button class="btn btn-primary mt-1 mb-1 ml-1 flow-right ${this.app.login.isLoggedIn()? '': 'hidden'}" @click="${e => {this.logout()}}">Logout</button>
    	`;
 	}

	login(pass)
	{
		let self = this;
		axios.get('webjcc/login?pass='+pass, {headers: {'x-jadex-login': pass}}, self.transform).then(function(resp)
		//axios.get('webjcc/login?pass='+pass, self.transform).then(function(resp)
		{
			//console.log("logged in: "+resp);
			//self.loggedin = true;
			self.app.login.setLogin(true);
			
			if(typeof(Storage) !== undefined) 
			{
				let checkbox = self.shadowRoot.getElementById("rememberpassword");
				if(checkbox.checked) 
				{
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
			self.app.login.setLogin(false);
		});
	}
	
	logout()
	{
		let self = this;
		axios.get('webjcc/logout', {headers: {'x-jadex-logout': true}}, self.transform).then(function(resp)
		//axios.get('webjcc/login?pass='+pass, self.transform).then(function(resp)
		{
			//console.log("logged out: "+resp);
			//self.loggedin = false;
			self.app.login.setLogin(false);
			self.createInfoMessage("logged out");
		})
		.catch(function(err) 
		{
			//console.log("logout failed: "+err);	
			//self.loggedin = false;
			self.app.login.setLogin(false);
			self.createErrorMessage("logout failed", err);
		});
		localStorage.removeItem("platformpassword");
	}
	
	isLoggedIn()
	{
		let self = this;
		return new Promise(function(resolve, reject) 
		{
			axios.get('webjcc/isLoggedIn', {headers: {'x-jadex-isloggedin': true}}, self.transform).then(function(resp)
			{
				//console.log("is logged in: "+resp);
				//self.loggedin = resp.data;
				self.app.login.setLogin(resp.data);
				resolve(self.loggedin);
			})
			.catch(function(err) 
			{
				//console.log("check failed: "+err);
				self.createErrorMessage("check failed", err);
				//this.app.login.setLogin(false);	
				reject(err);
			});
		});
	}
}

if(customElements.get('jadex-login') === undefined)
	customElements.define('jadex-login', LoginElement);
	