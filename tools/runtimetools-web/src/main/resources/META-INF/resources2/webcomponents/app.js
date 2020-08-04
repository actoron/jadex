import { LitElement, html, css } from 'lit-element';
import { BaseElement } from 'base-element';

// Defined as <jadex-app> tag
class AppElement extends BaseElement 
{
	messsage = null;
	
	constructor() 
	{
		super();
		
		var self = this; 
		
		console.log("app element");
		
		page.base('/#');
		page('/index.html', () => {
			page.redirect("/platforms");
	    });
	    page('/', () => {
	    	page.redirect("/platforms");
	    });
	    page('/platforms', () => {
	    	self.shadowRoot.getElementById("content").innerHTML = "<jadex-platforms></jadex-platforms>";
	    });
	    page('/platform/:cid', (ctx, next) => {
	    	console.log("router, cid: "+ctx.params.cid);
	    	self.shadowRoot.getElementById("content").innerHTML = "<jadex-platform cid='"+ctx.params.cid+"'></jadex-platform>";
	    });
	    page('/platform/:cid/:plugin', (ctx, next) => {
	    	console.log("router, cid: "+ctx.params.cid+ " plugin: " + ctx.params.plugin);
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
	}
	
	firstUpdated(changedProperties) 
	{
		page();
	}
		
	render() 
	{
		return html`
			<link rel="stylesheet" href="css/style.css">
			<link rel="stylesheet" href="libs/bootstrap_4.3.1/bootstrap.min.css">

			<nav class="navbar navbar-expand-lg navbar-custom navbar-fixed-top">
				<div class="navbar-brand mr-auto">
		 			<img src="images/jadex_logo_ac.png" width="200px"/>
					<a class="navbar-brand pl-2" href="#">WebJCC</a>
				</div>
				<button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarSupportedContent" aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation">
					<span class="navbar-toggler-icon"></span>
				</button>
				<div class="collapse navbar-collapse" id="navbarSupportedContent" ref="navcol">
					<ul class="navbar-nav mr-auto">
		   				<li class="nav-item">
		      				<a class="nav-link" href="">${language.$t("message.home")}</a>
		    			</li>
		    			<li class="nav-item">
		      				<a class="nav-link" href="#about">${language.$t("message.about")}</a>
		    			</li>
		 			</ul>
		 			<form class="form-inline my-2 my-lg-0"></form>
				</div>
		        
		        <form class="form-inline my-2 my-lg-0 ml-2">
					<img class="navbar-nav ml-auto" @click="${this.switchLanguage}" src="${language.lang=='de'? 'images/language_de.png': 'images/language_en.png'}" />
				</form>
			</nav>
		
			<div id="content"></div>
		
			${this.message!=null? html`
			<div class="container-fluid pt-0 pl-3 pr-3 pb-3">
				<div class="row">
					<div class="col">
						<div class="{'alert-danger': message.type=='error', 'alert-info': message.type=='info'}" class="alert m-0 p-3" role="alert">
							${this.message.text}
							<button type="button" class="btn btn-primary mr-1" align="right" style="width: 100px" onclick="clearMessage()">Close</button>
						</div>
					</div>
				</div>
			</div>
			`: ''}
		
			<footer class="container-fluid footer navbar-light bg-light">
		        <span class="text-muted">Copyright by <a href="http://www.actoron.com">Actoron GmbH</a> 2017-${new Date().getFullYear()}</span>
		    	<div class="pull-right">
					<a href="#/about">${language.$t("message.about")}</a>
		    		<a href="#/privacy">${language.$t("message.privacy")}</a>
		    		<a href="#/imprint">${language.$t("message.imprint")}</a>
		    	</div>
		    </footer>
		`;
	}
	
	clearMessage() 
	{
		this.message = null;
		//self.update();
	}
}

if(customElements.get('jadex-app') === undefined)
	customElements.define('jadex-app', AppElement);