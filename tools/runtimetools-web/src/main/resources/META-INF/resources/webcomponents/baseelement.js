import {LitElement, html, css} from '../libs/lit/lit-element.js';

export class BaseElement extends LitElement 
{
	static app_singleton;
	
	static apppromise = new Promise((resolve,reject) => 
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
	});
	
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
				self.requestUpdate().then(() => 
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
			BaseElement.apppromise
			.then(()=>
			{
				//console.log('app loaded');
				self.app = BaseElement.app_singleton;
				
				// must load sync to ensure that style.css rules are defined and gain precedence		
				//self.loadStyle("/libs/bootstrap_4.5.0/bootstrap.min.css")
				/*self.loadStyle("/libs/bootstrap_5.0.1/css/bootstrap.min.css")
				.then(()=>
				{*/
					//console.log("loaded bootstrap css")
					//self.loadScript("libs/jquery_3.4.1/jquery.js")
					//.then(()=>
					//{
						//console.log("loaded jquery")
						/*self.loadScript("/libs/bootstrap_5.0.1/js/bootstrap.bundle.min.js")
						.then(()=>
						{*/
							//console.log("loaded bootstrap")
							self.loadStyle("/css/style.css")
							.then(()=>{
								
								//console.log("loaded all, requesting update...");
								resolv();
								
							})
							.catch((err)=>rejec(err));
						/*})
						.catch((err)=>rejec(err));*/
					//})
					//.catch((err)=>rejec(err));
				//})
				//.catch((err)=>rejec(err));
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
				//console.log(css);
				var sheet = new CSSStyleSheet();
				sheet.replaceSync(css);
				self.shadowRoot.adoptedStyleSheets = self.shadowRoot.adoptedStyleSheets.concat(sheet);
				BaseElement.loaded[url] = css;
				resolve(sheet);
			})
			.catch(function(err)
			{
				reject(err);
			});
		});
		
		return ret;
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
		var txt = text+(data!=null? ": "+data: "");
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
}

//customElements.define('jadex-base', StarterElement);
