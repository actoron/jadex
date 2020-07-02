import { LitElement, html, css } from 'lit-element';
import { BaseElement } from 'base-element';

// Defined as <jadex-app> tag
class AppElement extends BaseElement {

	messsage = null;
	
	constructor() {
		super();
		
		var self = this;
		
		console.log("app element");
		
		page.base('/#');
		page('/index2.html', () => {
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
	    page('/about', () => {
	    	self.shadowRoot.getElementById("content").innerHTML = "<jadex-about></jadex-about>";
	    });
	    page('/imprint', () => {
	    	self.shadowRoot.getElementById("content").innerHTML = "<jadex-imprint></jadex-imprint>";
	    });
	    page('/privacy', () => {
	    	self.shadowRoot.getElementById("content").innerHTML = "<jadex-privacy></jadex-privacy>";
	    });
	    page('*', (ctx, next) => {
	    	console.log("not found: "+ctx);
	    });
	    
		/*var curpage = null;
	   	var target = "div#content";
	   	var routes = ["platforms", "platform", "about", "imprint", "privacy"];
	   	riot.store.language = "en";
	   	
	   	// As subpaths are supplied by separate args, internally arguments are used
	   	route(function changePath(path) 
	   	{
	   		path = path.toLowerCase();
	   		var paths = [];
	   		var params = null;
	   		for(i=0; i<arguments.length; i++)
	   		{
	   			if(arguments[i].indexOf("=")!=-1)
	   				params = arguments[i];
	   			else
	   				paths.push(arguments[i]);
	   		}
	   		//console.log(path);
	   		if(path.length==0)
	   			path="platforms";
	   		if(routes.includes(path)) 
	   		{ 
	   			if(curpage)
					curpage.unmount(true);
	   	        var tags = riot.mount(target, path, {paths: paths, params: params});
	   	        curpage = tags[0];
	   	        //console.log(tags);
	   		}
	   	});*/
	}
	
	firstUpdated(changedProperties) {
		page();
	}
	
	static get styles() {
	    return css`
	    	/* Navbar styling. */
	    	/* background color. */
	    	.navbar-custom {
	    		background-color: #aaaaaa;
	    	}
	    	/* brand and text color */
	    	.navbar-custom .navbar-brand,
	    	.navbar-custom .navbar-text {
	    		color: rgba(255,255,255,.8);
	    	}
	    	/* link color */
	    	.navbar-custom .navbar-nav .nav-link {
	    		color: rgba(255,255,255,.5);
	    	}
	    	/* color of active or hovered links */
	    	.navbar-custom .nav-item.active .nav-link,
	    	.navbar-custom .nav-item:focus .nav-link,
	    	.navbar-custom .nav-item:hover .nav-link {
	    		color: #ffffff;
	    	}
	    `;
	}
	
	render() {
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
		    		<a href="#/privacy">${language.$t("message.privacy")}</a>
		    		<a href="#/imprint">${language.$t("message.imprint")}</a>
		    	</div>
		    </footer>
		`;
	}
	
	clearMessage() {
		this.message = null;
		//self.update();
	}
	
	switchLanguage() {
	    language.switchLanguage(); 
	    this.requestUpdate(); // needs manual update as language.lang is not mapped to an attribute 
	}
}

customElements.define('jadex-app', AppElement);

/*<app>
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
      			<a class="nav-link" href="">{$t("message.home")}</a>
    		</li>
    		<li class="nav-item">
      			<a class="nav-link" href="#about">{$t("message.about")}</a>
    		</li>
 				</ul>
 				<form class="form-inline my-2 my-lg-0">
 					<!-- <input class="form-control mr-sm-2" type="search" placeholder="Search" aria-label="Search" v-model="tag" list="mytags2"/>
					<datalist id="mytags2">
						<dynoption v-for="tag in tagNames" v-bind:datalisttag="tag"></dynoption>
					</datalist> -->
				</form>
			</div>
			<!--<div class="d-flex flex-row order-2 order-lg-3">
         <ul class="navbar-nav flex-row">
            <li class="nav-item"><a class="nav-link px-2" href="#"><span class="fa fa-facebook"></span></a></li>
            <li class="nav-item"><a class="nav-link px-2" href="#"><span class="fa fa-twitter"></span></a></li>
            <li class="nav-item"><a class="nav-link px-2" href="#"><span class="fa fa-youtube"></span></a></li>
            <li class="nav-item"><a class="nav-link px-2" href="#"><span class="fa fa-linkedin"></span></a></li>
        </ul>
        <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarNavDropdown">
            <span class="navbar-toggler-icon"></span>
        </button> -->
        
        <form class="form-inline my-2 my-lg-0 ml-2">
			<!-- 
			<input class="form-control mr-sm-2" type="search" placeholder="Search" aria-label="Search">
			<button class="btn btn-outline-success my-2 my-sm-0" type="submit">Search</button>-->
			<img class="navbar-nav ml-auto" onclick="riot.store.switchLanguage()" src="{lang=='de'? 'images/language_de.png': 'images/language_en.png'}" />
		</form>
	</nav>
	
	<div id="content"></div>
	
	<div if="{message!=null}" class="container-fluid pt-0 pl-3 pr-3 pb-3">
		<div class="row">
			<div class="col">
				<div class="{'alert-danger': message.type=='error', 'alert-info': message.type=='info'}" class="alert m-0 p-3" role="alert">
					{message.text}
					<button type="button" class="btn btn-primary mr-1" align="right" style="width: 100px" onclick="clearMessage()">Close</button>
				</div>
			</div>
		</div>
	</div>
	
	<footer class="container-fluid footer navbar-light bg-light">
        <span class="text-muted">Copyright by <a href="http://www.actoron.com">Actoron GmbH</a> 2017-{new Date().getFullYear()}</span>
    	<div class="pull-right">
    		<a href="#/privacy">{$t("message.privacy")}</a>
    		<a href="#/imprint">{$t("message.imprint")}</a>
    	</div>
    </footer>

	<style>
		.navbar-custom {
		    background-color: #aaaaaa;
		}
		.navbar-custom .navbar-brand,
		.navbar-custom .navbar-text {
		    color: rgba(255,255,255,.8);
		}
		.navbar-custom .navbar-nav .nav-link {
		    color: rgba(255,255,255,.5);
		}
		.navbar-custom .nav-item.active .nav-link,
		.navbar-custom .nav-item:focus .nav-link,
		.navbar-custom .nav-item:hover .nav-link {
		    color: #ffffff;
		}
	</style>

    <script>
    	var self = this;
    	this.message = null;
    	
    	//console.log("tag app: "+self.id);
    
	    riot.store.on('message', function(m) {
			//console.log("message: "+m);
			//console.log("lang: "+self.lang);
			self.message = m;
			self.update();
		});
	    
	    clearMessage = function()
	    {
	    	self.message = null;
	    	self.update();
	    }
    </script>
</app>*/

