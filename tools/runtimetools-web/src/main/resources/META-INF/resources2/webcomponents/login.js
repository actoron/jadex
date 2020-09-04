import { LitElement, html, css } from 'lit-element';
import { BaseElement } from 'base-element';

class LoginElement extends BaseElement 
{
	//loggedin; WARNING: must NOT be declared. Property changes do not work then
	
	constructor() 
	{
		super();	
		this.loggedin = false;
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
		ret['loggedin'] = {type: Boolean};
		return ret;
	}
	
	connectedCallback() 
	{
		super.connectedCallback();
		this.isLoggedIn();
	}
	
	render() 
	{
    	return html`
			<div class="flexcontainerrow ${this.loggedin? 'hidden': ''}">
				<input class="flexcellgrow mt-1 mb-1" id="pass" name="platformpass" type="text" placeholder="Platform password"></input>
				<button class="btn btn-primary mt-1 mb-1 ml-1" @click="${e => {this.login(this.shadowRoot.getElementById('pass').value)}}">Login</button>
			</div>
			<button class="btn btn-primary mt-1 mb-1 ml-1 flow-right ${this.loggedin? '': 'hidden'}" @click="${e => {this.logout()}}">Logout</button>
    	`;
 	}

	login(pass)
	{
		var self = this;
		axios.get('webjcc/login?pass='+pass, {headers: {'x-jadex-login': pass}}, self.transform).then(function(resp)
		//axios.get('webjcc/login?pass='+pass, self.transform).then(function(resp)
		{
			console.log("logged in: "+resp);
			self.loggedin = true;
			//window.location.href = "/#/platforms";
			self.createInfoMessage("logged in");
		})
		.catch(function(err) 
		{
			console.log("login failed: "+err);	
			self.createErrorMessage("login failed", err);
			self.loggedin = false;
		});
	}
	
	logout()
	{
		var self = this;
		axios.get('webjcc/logout', {headers: {'x-jadex-logout': true}}, self.transform).then(function(resp)
		//axios.get('webjcc/login?pass='+pass, self.transform).then(function(resp)
		{
			console.log("logged out: "+resp);
			self.loggedin = false;
			self.createInfoMessage("logged out");
		})
		.catch(function(err) 
		{
			console.log("logout failed: "+err);	
			self.loggedin = false;
			self.createErrorMessage("logout failed", err);
		});
	}
	
	isLoggedIn()
	{
		var self = this;
		axios.get('webjcc/isLoggedIn', {headers: {'x-jadex-isloggedin': true}}, self.transform).then(function(resp)
		{
			console.log("is logged in: "+resp);
			self.loggedin = resp.data;
		})
		.catch(function(err) 
		{
			console.log("check failed: "+err);	
			self.createErrorMessage("check failed", err);
		});
	}
}

customElements.define('jadex-login', LoginElement);

