import {LitElement} from 'https://unpkg.com/lit-element@latest/lit-element.js?module';
import {html} from 'https://unpkg.com/lit-html@latest/lit-html.js?module';
import {css} from 'https://unpkg.com/lit-element@latest/lit-element.js?module';

export class BaseElement extends LitElement 
{
	static loaded = {};
	
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
				console.log("LOADED SCRIPT: "+url);
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
   	    		console.log("already loaded script: "+url);
   	    		resolve();
   	    	}
   	    	else
   	    	{
   	    		BaseElement.loaded[url] = url;
   	    		
   				console.log("loading script content start: "+url);
   	    		axios.get(url).then(function(resp)
   				{
   					console.log("loaded script content end: "+url);//+" "+resp.data);
   					
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
}

//customElements.define('jadex-base', StarterElement);
