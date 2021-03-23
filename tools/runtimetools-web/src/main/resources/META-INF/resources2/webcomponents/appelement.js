let { LitElement, html, css } = modLoad('lit-element');
let { BaseElement } = modLoad('base-element');

// Defined as <jadex-app> tag
class AppElement extends BaseElement 
{
	message = {};
	listener = null;
	loggedin = false;
	
	postInit() 
	{
		//console.log("app element");
		
		let self = this;
		
		this.app.lang.listeners.add(self);
		
		page.base('/#');
		page('/index.html', () => {
			page.redirect("/platforms");
	    });
	    page('/', () => {
	    	page.redirect("/platforms");
	    });
 		page('/login', () => {
	    	self.shadowRoot.getElementById("content").innerHTML = "<jadex-login></jadex-login>";
	    });
	    page('/platforms', () => {
	    	self.shadowRoot.getElementById("content").innerHTML = "<jadex-platforms></jadex-platforms>";
	    });
	    page('/platform/:cid', (ctx, next) => {
	    	//console.log("router, cid: "+ctx.params.cid);
	    	self.shadowRoot.getElementById("content").innerHTML = "<jadex-platform cid='"+ctx.params.cid+"'></jadex-platform>";
	    });
	    page('/platform/:cid/:plugin', (ctx, next) => {
	    	//console.log("router, cid: "+ctx.params.cid+ " plugin: " + ctx.params.plugin);
	    	self.shadowRoot.getElementById("content").innerHTML = "<jadex-platform cid='"+ctx.params.cid+"' plugin='"+ctx.params.plugin+"'></jadex-platform>";
	    });
	    page('/about', () => {
	    	self.shadowRoot.getElementById("content").innerHTML = "<jadex-about></jadex-about>";
	    });
	    page('/imprint', () => {
	    	self.shadowRoot.getElementById("content").innerHTML = "<jadex-imprint></jadex-imprint>";
	    });
	    page('/privacy', () => {
	    	self.shadowRoot.getElementById("content").innerHTML = "<jadex-privacy></jadex-privacy>";
	    });
	    /*page('*', (ctx, next) => {
	    	console.log("nav not found: "+ctx);
	    });*/

		page();
	}
	
	connectedCallback() 
	{
		super.connectedCallback();

		var self = this;
		if(this.listener==null)
		{
			this.listener = (e) => 
			{
				//console.log(e)
				self.message = e.detail==null? {}: e.detail;
				self.requestUpdate();
			}
		}
		
		//const myElement = document.querySelector('my-element');
		this.addEventListener('jadex-message', this.listener);
	}
	
	switchLanguage()
	{
		this.app.lang.setLanguage(this.app.lang.getLang()==='en'? 'de' : 'en');
	}
	
	/*disconnectedCallback()
	{
		super.disconnectedCallback();
		if(this.listener!=null)
			this.removeEventListener(this.listener);
	}*/
	
	/*firstUpdated(props) 
	{
		page();
	}*/
	
	asyncRender() 
	{
		return html`
			<div style="height:100%" class="d-flex flex-column">
			<nav class="navbar navbar-expand-lg navbar-custom navbar-fixed-top">
				<div class="navbar-brand mr-auto">
		 			<a class="p-0 m-0" href="#"><img src="images/jadex_logo_ac_new_webjcc.png" width="200px"/></a>
					<img class="p-0 m-0" @click="${this.switchLanguage}" src="${this.app.lang.getFlagUrl()}" />
				</div>
				<!--
				<button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarSupportedContent" aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation">
					<span class="navbar-toggler-icon"></span>
				</button>
				<div class="collapse navbar-collapse" id="navbarSupportedContent" ref="navcol">
					<ul class="navbar-nav mr-auto">
		   				<li class="nav-item">
		      				<a class="nav-link" href="">${this.app.lang.t("message.home")}</a>
		    			</li>
		    			<li class="nav-item">
		      				<a class="nav-link" href="#about">${this.app.lang.t("message.about")}</a>
		    			</li>
		 			</ul>
		 			<form class="form-inline my-2 my-lg-0"></form>
				</div>
				-->		        

				<div class="flexcontainerrow">
					<jadex-login class="flexcellgrow"></jadex-login>
				</div>
			</nav>
		
			<div class="flex-grow-1" id="content"></div>
		
			<div class="container-fluid pt-0 pl-0 pr-0 pb-0 ${this.message.text!=null? 'visible': 'hidden'}">
				<div class="row p-0">
					<div class="col">
						<div class="alert m-0 p-0 ${(this.message.type=='error'? 'alert-danger': 'alert-info')}">
							<div class="close" @click="${e => this.clearMessage()}"></div>
							<div class="row p-1 m-1">
								<div class="col-12 p-0 align-self-center">
									${this.message.text}
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		
			<footer class="container-fluid footer navbar-light bg-light">
		        <span class="text-muted">Copyright by <a href="http://www.actoron.com">Actoron GmbH</a> 2017-${new Date().getFullYear()}</span>
		    	<div class="float-right">
					<a href="#/about">${this.app.lang.t("About")}</a>
		    		<a href="#/privacy">${this.app.lang.t("Privacy")}</a>
		    		<a href="#/imprint">${this.app.lang.t("Imprint")}</a>
		    	</div>
		    </footer>
		    </div>
		`;
	}
}

if(customElements.get('jadex-app') === undefined)
	customElements.define('jadex-app', AppElement);