import {LitElement, html, css} from '../libs/lit-3.2.0/lit-element.js';

export class BaseElement extends LitElement 
{
	static app_singleton;
	
	// now loaded outside to cope with platform disconnect
	/*static apppromise = new Promise((resolve,reject) => 
	{
		// axios.get('/webcomponents/app.js') is better but does not work with path rewrite in proxy
		axios.get('../webcomponents/app.js').then(function(resp) 
		{
			let appfun = new Function("return " + resp.data + "\n//# sourceURL=app.js\n");
			try 
			{
				BaseElement.app_singleton = appfun();
				resolve();
			}
			catch (err) 
			{
				reject(err);
			}
		}).catch(err => 
		{
			reject(err);
		});
	});*/
	
	/**
	 * Get the application provided by app.js
	 */
	getApp()
	{
		// as module load order is undefined a promise
		// is used to indicate when the app has been loaded
		if(window.jadexapp!=null)
		{
			return window.jadexapp;
		}
		else
		{
			return new Promise(function(resolve, reject) 
			{
				// otherwise resolve function is not accessible from outside 
				this.resolve = resolve;
			}); 
		}
	}
	
	static loadedelements = [];
	
	static loaded = {};
	
	app = null;
	jadexservice = null;
	cid = null;
	//langlistener = null;
	inited = false;
	
	constructor() 
	{
		super();
		
		//console.log("const of baseelem");
		
		let self = this;
		//console.log(typeof self.prototype.init);
		self.preInit().then(() =>
		{
			//console.log('checking for init function.... ' + typeof self.init + " " + self.constructor.name);
			let initprom;
			//if(name === 'cid' && newval!=null && typeof this.init === 'function')
			if(typeof self.init === 'function')
		    {
		    	//console.log('init found, calling...');
		    	initprom = self.init();
		    }
			let callpostinit = function() 
			{
				self.inited = true;
				self.requestUpdate();//.then(() => 
				self.updateComplete.then(() =>
				{
					//console.log("update done, calling postinit");
					self.postInit();
				}).catch(err => 
				{
					console.log("Error updating element: " + self.constructor.name)
					console.log(err);
				});
			};
			if(typeof initprom === 'object' && typeof initprom.then === 'function')
			{
				initprom.then(() => 
				{
					callpostinit();
				}).
				catch(err =>
				{
					console.log("Error on init: " + self.constructor.name)
					console.log(err);
				});
			}
			else
				callpostinit();
		})
		.catch(err =>
		{
			console.log("Error on preinit: " + self.constructor.name)
			console.log(err);
		});
	}
	
	preInit()
	{
		let self = this;
		//console.log("starting preinit " + this.constructor.name);
		
		let preinitprom = new Promise((resolv, rejec) => 
		{
			//console.log('starting init prom');
			//BaseElement.apppromise
			self.getApp()
			.then((app)=>
			{
				//console.log('app loaded');
				self.app = app;//BaseElement.app_singleton;
				
				var cont = () => 
				{
					self.loadStyle("/css/style.css")
					.then(()=>{
						
						//console.log("loaded all, requesting update...");
						resolv();
						
					})
					.catch(err => 
					{
						self.addStyle(self.fallbackcss);
						console.log("could not load style.css, using fallback css: "+err);
						resolv();
					});
				};
				
				// store pass?!
				var prom = self.app.login.relogin();
				prom.then(l =>
				{
					self.createInfoMessage("login successful");
					cont();
				})
				.catch(err =>
				{
					self.createErrorMessage("login failed", err);
					cont();
				});
			})
			.catch((err)=>rejec(err));
		});
		
		return preinitprom;
	}
	
	postInit()
	{
	}
	
	connectedCallback() 
	{
		super.connectedCallback();

		var self = this;
		//console.log('connected')
		
		/*if(this.langlistener==null)
		{
			this.langlistener = e => 
			{
				//console.log("update: "+self);
				self.requestUpdate();
			};
		}
		
		BaseElement.language.addListener(this.langlistener);*/
		
		/*if(this.loginlistener==null)
		{
			this.loginlistener = e => 
			{
				//console.log("login update: "+self);
				self.requestUpdate();
			};
		}
		BaseElement.login.addListener(this.loginlistener);*/
	}
	
	disconnectedCallback()
	{
		super.disconnectedCallback();
		/*if(this.langlistener!=null)
			BaseElement.language.removeListener(this.langlistener);*/
		if(this.loginlistener!=null)
			BaseElement.login.removeListener(this.loginlistener);
	}
	
	// todo: why does caching (above) not work :-(
	loadStyle(url)
	{
		//console.log("###########LOADING STY " + url);
		var self = this;
		var ret = null;
		
		ret = new Promise(function(resolve, reject) 
		{
			axios.get(url).then(function(resp)
			{
				//console.log("loaded version: "+url+" "+self.shadowRoot.adoptedStyleSheets.length);
				//console.log(resp.data);
				var css = resp.data;
				resolve(self.addStyle(css));
				BaseElement.loaded[url] = css;
			})
			.catch(function(err)
			{
				reject(err);
			});
		});
		
		return ret;
	}
	
	addStyle(css)
	{
		var sheet = new CSSStyleSheet();
		sheet.replaceSync(css);
		this.shadowRoot.adoptedStyleSheets = this.shadowRoot.adoptedStyleSheets.concat(sheet);
		return sheet;
	}
	
	loadScript(url)
    {
		if(window.loadedScript==null)
		{
			window.calls = {};
			window.loadedScript = function(url)
			{
				var callback = window.calls[url];
				if(callback!=null)
				{
					delete window.calls[url];
					callback();
				}
				else
				{
					console.log("Callback not found for: "+url);
				}
			}
		}
		
   		return new Promise(function(resolve, reject) 
		{
   			if(BaseElement.loaded[url]!=null)
   	    	{
   	    		//console.log("already loaded script: "+url);
   	    		resolve();
   	    	}
   	    	else
   	    	{
   	    		BaseElement.loaded[url] = url;
   	    		
   				//console.log("loading script content start: "+url);
   	    		axios.get(url).then(function(resp)
   				{
   					//console.log("loaded script content end: "+url);//+" "+resp.data);
   					
   					// directly loading via a script src attributes has the
   					// disadvantage that the type is checked from the response
   					// throwing check errors :-(
   							
   					var js = resp.data;    				
   		            var script = document.createElement('script');
   		            script.type = "module";
   		            //script.textContent = js+"\n console.log('LOOADED: "+url+"'); window.loadedScript('"+url+"')";
   		            script.textContent = js+"\n window.loadedScript('"+url+"')";
   		            //script.innerHTML = js;
   		            //script.async = false;
   		            //script.onload = function() {console.log("LOADED SCRIPT: "+url);};
   		            window.calls[url] = resolve;
   		            document.getElementsByTagName("head")[0].appendChild(script);
   		            // https://stackoverflow.com/questions/40663150/script-injected-with-innerhtml-doesnt-trigger-onload-and-onerror
   		            // append child returns BEFORE script is executed :-( In contrast to text
   		            
   		            //console.log('APPENDED: '+url);
   		            //setInterval(function(){ resolve(); }, 1000);
   				})
   				.catch(function(err)
   				{
   					console.log("Error loading script: "+url);
   					reject();
   				});
   	    	}
		});	
    }
	
	loadSubmodule(url)
    {
		if(window.loadedScript==null)
		{
			window.calls = {};
			window.loadedScript = function(url)
			{
				var callback = window.calls[url];
				if(callback!=null)
				{
					delete window.calls[url];
					callback();
				}
				else
				{
					console.log("Callback not found for: "+url);
				}
			}
		}
		
   		return new Promise(function(resolve, reject) 
		{
   			if(BaseElement.loaded[url]!=null)
   	    	{
   	    		//console.log("already loaded script: "+url);
   	    		resolve();
   	    	}
   	    	else
   	    	{
   	    		BaseElement.loaded[url] = url;
   	    		
   				//console.log("loading script content start: "+url);
   	    		axios.get(url).then(function(resp)
   				{
   					//console.log("loaded script content end: "+url);//+" "+resp.data);
   					
   					// directly loading via a script src attributes has the
   					// disadvantage that the type is checked from the response
   					// throwing check errors :-(
   							
   					var js = resp.data;
   		            //script.innerHTML = js;
   		            //script.async = false;
   		            //script.onload = function() {console.log("LOADED SCRIPT: "+url);};
   		            window.calls[url] = resolve;
   		            // https://stackoverflow.com/questions/40663150/script-injected-with-innerhtml-doesnt-trigger-onload-and-onerror
   		            // append child returns BEFORE script is executed :-( In contrast to text
   		            let funname = "Submodule_" + url;
					try {
						let componentfunc = new Function(js + "\n window.loadedScript('"+url+"')\n//# sourceURL=" + funname + "\n");
						componentfunc();
					}
					catch (error) {
						console.log("Script " + url + " failed to start " + error);
					}
   		            //importShim.topLevelLoad(importShim.getFakeUrl(), js+"\n window.loadedScript('"+url+"')");
   		            
   		            //console.log('APPENDED: '+url);
   		            //setInterval(function(){ resolve(); }, 1000);
   				})
   				.catch(function(err)
   				{
   					console.log("Error loading script: "+url);
   					reject();
   				});
   	    	}
		});	
    }
	
	loadServiceStyle(stylepath)
    {
		console.log('!!!!!!!!!!!!!!!!!!!!!!!!!!load style ' + stylepath);
		return this.loadStyle(this.getResourceUrl(stylepath));
    }
	
	loadServiceStyles(stylepaths)
    {
		let stylepromises = [];
		let self = this;
		stylepaths.forEach(function(stylepath) {
			stylepromises.push(self.loadServiceStyle(stylepath));
		})
		
		return Promise.all(stylepromises);
    }
	
	loadServiceScript(scriptpath)
    {
		return this.loadScript(this.getResourceUrl(scriptpath));
    }
	
	loadServiceScripts(scriptpaths)
    {
		let scriptpromises = [];
		let self = this;
		scriptpaths.forEach(function(scriptpath) {
			scriptpromises.push(self.loadServiceScript(scriptpath));
		})
		
		return Promise.all(scriptpromises);
    }
	
	loadServiceFont(fontfamily, fontpath)
	{
		let self = this;
		console.log("loadServiceFont");
		return new Promise(function(resolve, reject) 
		{
			console.log("loadServiceFont Promise exec");
			let font = new FontFace(fontfamily, 'url(' + self.getResourceUrl(fontpath) + ')');
			font.load().then(function(fontface)
			{
			    document.fonts.add(fontface)
			    resolve(fontface);
			}).catch(function(err)
			{
				reject(err);
			});
		});
	}
	
	getResourceUrl(respath)
	{
		let prefix = 'webjcc/invokeServiceMethod?cid='+this.cid+'&servicetype='+this.jadexservice;
		let url = prefix+'&methodname=loadResource&args_0='+respath+"&argtypes_0=java.lang.String";
		return url;
	}
	
	switchLanguage() 
	{
	    this.app.lang.switchLanguage(); 
	    //this.requestUpdate(); // update is done via event listeners on the language object
	}
	
	createErrorMessage(text, data) 
	{
		var txt = text;
		if(data!=null)
		{
			if(data.message!=null)
				txt += ": "+data.message;
			else
				txt += ": "+JSON.stringify(data);
		}
		this.dispatchMessageEvent({text: txt, type: "error"});
	}
	
	createInfoMessage(text) 
	{
		this.dispatchMessageEvent({text: text, type: "info"});
	}
	
	clearMessage()
	{
		this.dispatchMessageEvent(null);
	}
	
	dispatchMessageEvent(detail)
	{
		var event = new CustomEvent("jadex-message", 
		{ 
			detail: detail==null? {}: detail,
            bubbles: true, 
            composed: true 
        });
	    this.dispatchEvent(event);
	}
	
	render() 
	{
		if (this.inited)
		{
			//console.log('calling real render: ' + this.constructor.name);
			return this.asyncRender();
		}
		else
		{
			//console.log('calling fake render' + this.constructor.name);
			return html``;
		}
	}
	
	asyncRender()
	{
		return html``;
	}
	
	// fallback when loading from server fails (needs to be manually synced with style.css)
	static fallbackcss = `
	body {
	font-family: 'Roboto', sans-serif;
	}
	
	#app {
	    height: 100vh;
	}
	
	.navbar {
		/*font-family: 'Open Sans', sans-serif;
	    color:#f2f2f2;*/
		padding: 20px;
		background: linear-gradient(to bottom, #ffffff, #ffffff 10%, #ffffff 10%, #ffffff 90%, #2a6699 90%);
	}
	
	jadex-platform {
		flex: 1;
	}
	
	table.actwtable {
	    border-collapse: separate;
		border-spacing: 0px;
	}
	
	.actwtable th, .actwtable td {
	  border-right: 1px solid #2a6699;
	  border-bottom: 1px solid #2a6699;
	    padding: 3px 4px;
	}
	
	.actwtable tr th:first-child, .actwtable tr td:first-child {
	  border-left: 1px solid #2a6699;
	}
	
	.actwtable tr:first-child th, .actwtable tr:first-child td {
	  border-top: 1px solid #2a6699;
	}
	
	.actwtable th{
	    background-color: #2a6699;
	    color:	white;
	    font-weight: 700;
	    text-align:	center;
	}
	
	.actwtable th a:link, .actwtable th a:visited {
	    color:	white;
	}
	.actwtable caption, .actwtable th, .actwtable td{
	    font-size: 1em;
	}
	
	.actwtable tr:first-child td:first-child, .actwtable tr:first-child th:first-child {
		border-top-left-radius: 6px;
	}
	.actwtable tr:last-child td:last-child, .actwtable tr:last-child th:last-child {
		border-bottom-right-radius: 6px;
	}
	.actwtable tr:first-child td:last-child, .actwtable tr:first-child th:last-child {
		border-top-right-radius: 6px;
	}
	.actwtable tr:last-child td:first-child, .actwtable tr:last-child th:first-child {
		border-bottom-left-radius: 6px;
	}
	
	.jadexbtn {
		/*
		background-color: #1f79a0;
		border-color: #73b4d0;
		border-radius: 6px;
		line-height: 1.5;
		padding: 3px 3px;
		*/
		background-color:#2a6699;
		border-radius:6px 6px 6px 6px;
		/*font-size:16px;*/
		font-weight:bold;
		font-weight:600;
		padding-top:10px;
		padding-bottom:10px;
		border: 0px;
		
		color: #fff;
		/*margin-top:5px;
		display: block;
		width:100%;*/
	}
	
	.jadexbtn:disabled,
	.jadexbtn.disabled {
	  border: 1px solid #999999;
	  background-color: #cccccc;
	  color: #666666;
	}
	
	.w100 {
		width: 100%;
	}
	
	.h100 {
		height: 100%;
	}
	
	h1 {
	  font-size: 1.6em; /* x px/16=y em */
	}
	
	h2 {
	  font-size: 1.5em;
	}
	
	h3 {
	  font-size: 1.4em;
	}
	
	h4 {
	  font-size: 1.3em;
	}
	
	h5 {
	  font-size: 1.2em; 
	}
	
	h6 {
	  font-size: 1.1em; 
	}
	
	.flexcontainerrow {
		display:flex;
		flex-direction:row;
	}
	
	.flexcontainercol {
		display:flex;
		flex-direction:column;
	}
	
	.flexcellgrow {
		flex:1;
	}
	
	@media (max-width: 768px) {
	  .btn-responsive.r {
	    padding:4px 6px;
	    font-size:90%;
	    line-height: 1;
	    border-radius:3px;
	  }
	  
	  /*.rate {
	  	font-size: 20px;
	  }*/
	}
	
	@media (min-width: 769px) { /*and (max-width: 992px)*/
	  .btn-responsive.r {
	    padding:6px 12px;
	    font-size:95%;
	    line-height: 1;
	  }
	  
	  .rate {
	  	font-size: 25px;
	  }
	  
	  #app {
	    width: calc(100% - 330px);
	  }
	  
	  #ad {
	  	position: fixed;
	    right: 0;
	    width: 330px; 
	  }
	}
	
	#ad {
		padding: 10px;
	}
	
	.rate-base-layer {
	    color: #aaa;
	}
	
	.rate-hover-layer {
	    color: orange;
	}
	
	
	fieldset, label { 
		margin: 0; 
		padding: 0; 
	}
	
	body{ 
	    /*padding-top: 100px;*/
		min-height: 100vh;
		display: flex;
		flex-direction: column;
		height: 100%;
		padding: 0px; 
		padding-bottom: 60px;
		/*background-color: #cfcfcf!important;*/
	}
	
	/*h1 { 
		font-size: 1.5em; 
		margin: 10px; 
	}*/
	
	html {
		position: relative;
		min-height: 100%;
		height: 100%;
	    /*background-size: cover;
	    background-repeat:no-repeat;*/
	    /*background-color: #70bg32;*/
	    /*background: linear-gradient( to left top, blue, red);*/
		/*background: linear-gradient(to bottom, rgba(30,87,153,1) 0%,rgba(89,148,202,1) 62%,rgba(95,154,207,0.7) 68%,rgba(125,185,232,0) 100%);
		*/
	}
	
	/*.footer {
	  position: absolute;
	  bottom: 0;
	  width: 100%;
	  height: 60px;
	  line-height: 60px; 
	  background-color: #f5f5f5;
	}*/
	
	/*.footer {
	    position: fixed;
	    left: 0;
	    bottom: 0;
	    right: 0;
	    height: 60px;
	    z-index: 1030;
	    background-color: #f5f5f5;
	}*/
	
	.footer {
	  /*position: fixed;
	  bottom: 0;*/
	  display: flex;
	  justify-content: space-between;
	  width: 100%;
	  height: 60px;
	  line-height: 60px; 
	  /*background-color: #343a40!important;*/
	  /*background-color: #cfcfcf!important;*/
	  /*padding-left: 20px!important;*/
	  /*background-color: #4B589D;*/
	  /*background-color: #f8f9fa!important;*/
	  /*margin: 4px;
	  box-shadow: 0 0 4px grey;*/
	}
	
	footer#footer {
	    background: none repeat scroll 0 0 #000000;
	}
	
	/*.container-fluid {
		background-color: #f8f9fa!important;
	}*/
	
	/*.footer {
	  position: absolute;
	  bottom: 0;
	  width: 100%;
	  height: 60px; 
	  line-height: 60px; 
	  background-color: #f5f5f5;
	}*/
	
	/*.navbar{*/
	  /*margin: 4px;
	  box-shadow: 0 0 4px grey;*/
	  /*background-color: #4B589D;*/
	  /*width: 100%;*/
	  /*border-radius: 3px;*/
	/*}*/
	
	/*.fixed-top {
	    position: fixed;
	    top: 0;
	    right: 0;
	    left: 0;
	    z-index: 1030;
	}*/
	
	/*.sorry
	{
	    display:block;
	    position:relative;
	}
	
	.sorry:before {
		display:block;
	    content:'';
	    position:absolute;
	    width:100%;
	    height:100%;
	    box-shadow:inset 0px 0px 6px 6px rgba(255,255,255,1);
	}*/
	
	/*.sorry {
	border:1px solid black;
	z-index: -1;
	}
	
	.sorry:hover {
	border: none;
	box-shadow: 0 0 60px black inset;
	z-index: 1;
	}*/
	
	/*.sorry {
		position: relative; z-index: 0;
	}
	
	.sorry div {
	    box-shadow: inset 0px 0px 50px 30px rgba(255,255,255,0.8);
	    display: inline-block;
	}
	
	.sorry img {
	    position: relative;
	    z-index: -1;
	}*/
	
	.bgjotd {
		/*background: linear-gradient(307deg, #f50200, #f5b500);*/
		/*background: linear-gradient(to bottom, rgba(30,87,153,1) 0%,rgba(125,185,232,0) 100%);*/
		/*background: linear-gradient(45deg, rgba(254,252,234,1) 0%,rgba(241,218,54,1) 100%);*/
		/*background: linear-gradient(45deg, rgba(252,236,252,1) 0%,rgba(251,166,225,1) 50%,rgba(253,137,215,1) 51%,rgba(255,124,216,1) 100%);*/
		/*background: linear-gradient(to bottom, rgba(240,183,161,1) 0%,rgba(140,51,16,1) 50%,rgba(117,34,1,1) 51%,rgba(191,110,78,1) 100%);*/
		/*background: linear-gradient(to bottom, rgba(254,255,255,1) 0%,rgba(221,241,249,1) 35%,rgba(160,216,239,1) 100%);*/
		/*background: linear-gradient(to right, #ffecd2 0%, #fcb69f 100%);*/
		/*background: linear-gradient(to right, #FFFF00 0%, #ffffff 100%);*/
		
		/*background-size: 400% 400%;*/
		/*animation: ani 3s ease infinite;*/
	}
	
	@keyframes ani { 
	    0%{background-position:0% 7%}
	    50%{background-position:100% 94%}
	    100%{background-position:0% 7%}
	}
	
	.visible {
		display: block;
	}
	
	.hidden {
		display: none;
	}
	
	.bold {
		font-weight: bold;
	}
	
	.close {
		margin: 2px;
		/*position: absolute;*/
		right: 0px;
		top: 0px;
		width: 16px;
		height: 16px;
		opacity: 0.3;
		z-index: 2;
	}
	
	.close:hover {
		opacity: 1;
	}
	
	.close:before, .close:after {
		position: absolute;
		left: 8px;
		content: ' ';
		height: 17px;
		width: 2px;
		background-color: #333;
	}
	
	.close:before {
		transform: rotate(45deg);
	}
	
	.close:after {
		transform: rotate(-45deg);
	}
	
	.relative {
		position: relative;
	}
	
	.absolute {
		position: absolute;
	}
	
	.back-lightgray {
		background-color: #F9F9F9;
	}
	
	jadex-app {
		flex: 1;
	}
	
	#plugin {
		display: flex;
		height: 100%;
	}
	
	.margintop {
		margin-top: 0.5em;
	}
	
	.marginright {
		margin-right: 0.5em;
	}
	
	.marginleft {
		margin-left: 0.5em;
	}
	
	.marginbottom {
		margin-bottom: 0.5em;
	}
	
	.margintop1 {
		margin-top: 1em;
	}
	
	.marginright1 {
		margin-right: 1em;
	}
	
	.marginleft1 {
		margin-left: 1em;
	}
	
	.marginbottom1 {
		margin-bottom: 1em;
	}
	
	.marginbottom2 {
		margin-bottom: 1em;
	}
	
	.margin1 {
		margin: 1em;
	}
	
	.paddingright {
		padding-right: 0.5em;
	}
	
	.paddingleft {
		padding-left: 0.5em;
	}
	
	.paddingtop {
		padding-top: 0.5em;
	}
	
	.paddingbottom {
		padding-bottom: 0.5em;
	}
	
	.right {
		float: right;
	}
	
	.vmiddle {
		vertical-align: middle;
	}
	
	.border {
		border: solid 1px #DFDFDF
	}
	
	.block {
		display: block;
	}
	
	.w50 {
		width: 50%;
	}
	
	.colorerror {
		background-color: #f8d7da;
	}
	
	.colorinfo {
		background-color: #cff4fc;
	}
	
	/* Panel style */
	.accordion .a-container .a-panel {
		width: 100%;
		transition: all 0.2s ease-in-out;
		opacity: 0;
	    height: auto;
	    max-height: 0;
	    overflow: hidden;
	    padding: 0px 10px;
	}
	   
	/* Panel style when active */
	.accordion .a-container.active .a-panel {
		padding: 0.5em 0.4em 0.4em 0.4em;
	    opacity: 1;
	    height: auto;
	    max-height: 10000px;
	}
	
	.a-btn {
		margin: 0;
	    position: relative;
	    padding: 0.5em 0 0.5em 0.5em;
	    width: 100%;
	    font-weight: 400;
	    display: block;
	    font-weight: 500;
	    cursor: pointer;
	    transition: all 0.3s ease-in-out;
	    border: solid 1px #DFDFDF;
		background-color: #F7F7F7;
	}
`
}
