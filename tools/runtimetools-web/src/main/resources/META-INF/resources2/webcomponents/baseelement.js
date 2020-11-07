import {LitElement, html, css} from '../libs/lit/lit-element.js';

export class BaseElement extends LitElement 
{
	static language = {
		lang: "en",
		listeners: [],
		messages: {
    		en: {
   				message: {
   					home: "Home",
   					privacy: "Privacy", 
   					imprint: "Imprint",
   					about: "About"
    			}
  			},
	    	de: {
				message: {
					home: "Home",
					privacy: "Datenschutz",
   					imprint: "Impressum",
					about: "Über"
				}
  			}
		}, 
		translate: function(text) {
			var msg = this.messages[this.lang];
			if(msg) 
			{
				var toks = text.split('.');
				var tmp = msg;
				for(var i=0; tmp!=null && i<toks.length; i++) 
				{
					tmp = tmp[toks[i]];
				}
				//console.log("text: "+text+" "+tmp);
				return tmp;
			}
			else 
			{
				return null;
			}
		},
		$t: function(text) 
		{
			return this.translate(text);
		},
		getLanguage: function() 
		{
			return this.lang=='de'? 0: 1;
		},
		switchLanguage: function()
		{
			this.lang=='de'? this.lang='en': this.lang='de';
			for(var i=0; i<this.listeners.length; i++)
			{
				this.listeners[i]({lang: this.lang});
			}
			//console.log("language is: "+this.lang);
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
		}
	};
	
	static login = 
	{
		loggedin: false,
		listeners: [],
		setLogin: function(loggedin)
		{
			if(this.loggedin!=loggedin && loggedin!=null)
			{
				this.loggedin = loggedin;
				for(var i=0; i<this.listeners.length; i++)
				{
					this.listeners[i](this.loggedin);
				}
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
	
	static loaded = {};
	
	cid = null;
	jadexservice = null;
	langlistener = null;
	loginlistener = null;
	loadedprom = null;
	
	static get properties() 
	{
		return { 
			cid: { type: String },
		};
	}
	
	attributeChangedCallback(name, oldval, newval) 
	{
	    //console.log('attribute change: ', this, name, newval, oldval);
	    
		super.attributeChangedCallback(name, oldval, newval);
	    
	    //console.log('checking for init function.... ' + typeof this.init + " " + this.constructor.name);
	    if(name === 'cid' && newval!=null && typeof this.init === 'function')
	    {
			this.cid = newval;
	    	//console.log('init found, calling...');
	    	this.init();
	    }
	    
		//console.log("baseelement: "+this.cid);
	}
	
	constructor() 
	{
		super();
		var self = this;
		
		self.loadedprom = new Promise(function(resolve, reject) 
		{
			// must load sync to ensure that style.css rules are defined and gain precedence		
			self.loadStyle("/libs/bootstrap_4.5.0/bootstrap.min.css")
			.then(()=>
			{
				//console.log("loaded bootstrap css")
				self.loadScript("libs/jquery_3.4.1/jquery.js")
				.then(()=>
				{
					//console.log("loaded jquery")
					self.loadScript("/libs/bootstrap_4.5.0/bootstrap.bundle.min.js")
					.then(()=>
					{
						//console.log("loaded bootstrap")
						self.loadStyle("/css/style.css")
						.then(()=>{
							//console.log("loaded all"); 
							resolve();
						})
						.catch((err)=>reject(err));
					})
					.catch((err)=>reject(err));
				})
				.catch((err)=>reject(err));
			})
			.catch((err)=>reject(err));
		});
	}
	
	init()
	{
		return this.loadedprom;
	}
	
	connectedCallback() 
	{
		super.connectedCallback();

		var self = this;
		//console.log('connected')
		
		if(this.langlistener==null)
		{
			this.langlistener = e => 
			{
				//console.log("update: "+self);
				self.requestUpdate();
			};
		}
		
		BaseElement.language.addListener(this.langlistener);
		
		if(this.loginlistener==null)
		{
			this.loginlistener = e => 
			{
				//console.log("login update: "+self);
				self.requestUpdate();
			};
		}
		BaseElement.login.addListener(this.loginlistener);
	}
	
	disconnectedCallback()
	{
		super.disconnectedCallback();
		if(this.langlistener!=null)
			BaseElement.language.removeListener(this.langlistener);
		if(this.loginlistener!=null)
			BaseElement.login.removeListener(this.loginlistener);
	}
	
	/*loadStyle(url)
	{
		var self = this;
		var ret = null;
		
		var sheet = BaseElement.loaded[url];
		if(sheet!=null)
		{
			if(sheet instanceof Promise)
			{
				ret = sheet;	
			}
			else
			{
				ret = new Promise(function(resolve, reject) 
				{
					//var sheet = new CSSStyleSheet();
					//sheet.replaceSync(css);
					self.shadowRoot.adoptedStyleSheets = self.shadowRoot.adoptedStyleSheets.concat(sheet);
					resolve(sheet);
					//console.log("cached version: "+url+" "+self.shadowRoot.adoptedStyleSheets.length);
				});
			}	
		}
		else
		{
			ret = new Promise(function(resolve, reject) 
			{
				axios.get(url).then(function(resp)
				{
					//console.log("loaded version: "+url+" "+self.shadowRoot.adoptedStyleSheets.length);
					var css = resp.data;    
					//console.log(css);
					var sheet = new CSSStyleSheet();
					sheet.replaceSync(css);
					self.shadowRoot.adoptedStyleSheets = self.shadowRoot.adoptedStyleSheets.concat(sheet);
					BaseElement.loaded[url] = sheet;
					resolve(sheet);
				})
				.catch(function(err)
				{
					reject(err);
				});
			});
			BaseElement.loaded[url] = ret;
		}
		
		return ret;
	}*/
	
	/*loadStyle(url)
	{
		var self = this;
		var ret = null;
		
		var css = BaseElement.loaded[url];
		if(css!=null)
		{
			if(css instanceof Promise)
			{
				ret = css;	
			}
			else
			{
				ret = new Promise(function(resolve, reject) 
				{
					var sheet = new CSSStyleSheet();
					sheet.replaceSync(css);
					self.shadowRoot.adoptedStyleSheets = self.shadowRoot.adoptedStyleSheets.concat(sheet);
					resolve(css);
					console.log("cached version: "+url+" "+self.shadowRoot.adoptedStyleSheets.length);
					console.log("cache: "+css);
				});
			}	
		}
		else
		{
			ret = new Promise(function(resolve, reject) 
			{
				axios.get(url).then(function(resp)
				{
					console.log("loaded version: "+url+" "+self.shadowRoot.adoptedStyleSheets.length);
					var css = resp.data;    
					console.log(css);
					var sheet = new CSSStyleSheet();
					sheet.replaceSync(css);
					self.shadowRoot.adoptedStyleSheets = self.shadowRoot.adoptedStyleSheets.concat(sheet);
					BaseElement.loaded[url] = css;
					resolve(css);
				})
				.catch(function(err)
				{
					reject(err);
				});
			});
			BaseElement.loaded[url] = ret;
		}
		
		return ret;
	}*/
	
	// todo: why does caching (above) not work :-(
	loadStyle(url)
	{
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
	    BaseElement.language.switchLanguage(); 
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
}

//customElements.define('jadex-base', StarterElement);
