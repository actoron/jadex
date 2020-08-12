import {LitElement} from 'lit-element';
import {html} from 'lit-element';
import {css} from 'lit-element';

export class BaseElement extends LitElement 
{
	static loaded = {};
	
	cid = null;
	jadexservice = null;
	langlistener = null;
	
	static get properties() 
	{
		return { 
			cid: { type: String },
			language: { attribute: false }
		};
	}
	
	attributeChangedCallback(name, oldval, newval) 
	{
	    //console.log('attribute change: ', this, name, newval, oldval);
	    
		super.attributeChangedCallback(name, oldval, newval);
	    
	    //console.log('checking for init function.... ' + typeof this.init + " " + this.constructor.name);
	    if(name === 'cid' && typeof this.init === 'function')
	    {
	    	//console.log('init found, calling...');
	    	this.init();
	    }
	    
		//console.log("baseelement: "+this.cid);
	}
	
	constructor() 
	{
		super();
		this.language = language;

		// must load sync to ensure that style.css rules are defined and gain precedence		
		this.loadStyle("/libs/bootstrap_4.5.0/bootstrap.min.css")
		.then(()=>
		{
			console.log("loaded bootstrap css")
			this.loadScript("libs/jquery_3.4.1/jquery.js")
			.then(()=>
			{
				console.log("loaded jquery")
				this.loadScript("/libs/bootstrap_4.5.0/bootstrap.bundle.min.js")
				.then(()=>
				{
					console.log("loaded bootstrap")
					this.loadStyle("/css/style.css")
					.then(()=>{console.log("loaded jadex css")})
				});
			});
		});
	}
	
	init()
	{
	}
	
	connectedCallback() 
	{
		super.connectedCallback();

		var self = this;
		//console.log('connected')
		
		if(this.langlistener==null)
		this.langlistener = e => 
		{
			//console.log("update: "+self);
			self.requestUpdate();
		};
		
		language.addListener(this.langlistener);
	}
	
	disconnectedCallback()
	{
		super.disconnectedCallback();
		if(this.langlistener!=null)
			language.removeListener(this.langlistener);
	}
	
	loadStyle(url)
	{
		var self = this;
		
		return new Promise(function(resolve, reject) 
		{
			axios.get(url).then(function(resp)
			{
				var css = resp.data;    
				//console.log(css);
				var sheet = new CSSStyleSheet();
				sheet.replaceSync(css);
				self.shadowRoot.adoptedStyleSheets = self.shadowRoot.adoptedStyleSheets.concat(sheet);
				resolve(sheet);
			})
			.catch(function(err)
			{
				reject(err);
			});
		});
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
   		            importShim.topLevelLoad(importShim.getFakeUrl(), js+"\n window.loadedScript('"+url+"')");
   		            
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
	    language.switchLanguage(); 
	    //this.requestUpdate(); // update is done via event listeners on the language object
	}
	
	createErrorMessage(text, data) 
	{
		var text = data!=null? data: "No further info";
		this.dispatchMessageEvent({text: text, type: "error"});
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
