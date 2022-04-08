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

	login(pass)
	{
		let self = this;
		let checkbox = self.shadowRoot.getElementById("rememberpassword");
		var store = checkbox.checked; 
		
		var prom = this.app.login.login(pass, store);
		
		prom.then(l =>
		{
			if(typeof(Storage) !== undefined) 
			{
				localStorage.setItem("platformpassword", pass);
				checkbox.checked = false;
			}
			
			self.createInfoMessage("login successful");
		})
		.catch(err =>
		{
			self.createErrorMessage("login failed", err);
		});
		
		return prom;
	}
	
	logout()
	{
		let self = this;
		let prom = this.app.login.logout();
		
		prom.then(l =>
		{
			self.createInfoMessage("logged out");
		})
		.catch(err =>
		{
			self.createErrorMessage("logout failed", err);
		});
		
		return prom;
	}
	
	isLoggedIn()
	{
		let self = this;
		
		var prom = this.app.login.checkLoggedIn();
		
		prom.catch(err =>
		{
			self.createErrorMessage("check failed", err);
		});
		
		return prom;
	}
	
	static get styles() 
	{
	    return css`
	    	.flexcenter {
				display: flex;
				align-items: center;
			}
	    `;
	}
	
	asyncRender() 
	{
    	return html`
			<div class="${this.app.login.isLoggedIn()? 'hidden': ''}">
				<div class="flexcontainerrow">
					<input class="flexcellgrow marginright" id="pass" name="platformpass" type="text" placeholder="${this.app.lang.t('Platform password')}"></input>
					<button class="jadexbtn" @click="${e => {this.login(this.shadowRoot.getElementById('pass').value)}}">Login</button>
				</div>
				<input id="rememberpassword" class="margintop" type="checkbox" >${this.app.lang.t("Remember password")}</input>
			</div>
			<div class="flexcenter h100 ${this.app.login.isLoggedIn()? '': 'hidden'}">
				<button class="jadexbtn right vmiddle ${this.app.login.isLoggedIn()? '': 'hidden'}" @click="${e => {this.logout()}}">Logout</button>
			</div>
    	`;
 	}
}

if(customElements.get('jadex-login') === undefined)
	customElements.define('jadex-login', LoginElement);
	