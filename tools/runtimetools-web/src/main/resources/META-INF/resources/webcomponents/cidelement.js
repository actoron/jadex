import {LitElement, html, css} from '../libs/lit-3.2.0/lit-element.js';
import { BaseElement } from "../webcomponents/baseelement.js"

/**
 *  Superclass for elements requiring a platform ID (cid) to function.
 */
export class CidElement extends BaseElement 
{
	preInit()
	{
		this.cidresolve = null;
		this.cidresolved = false;
		let self = this;
		
		let preinitprom = super.preInit();
		preinitprom.then(() => {
			//console.log("Resolving prinitnprom");
		});
		
		let cidprom = new Promise((resolv, rejec) => 
		{
			//console.log("Resolving CIDPROM " + self.constructor.name + " " + typeof resolv + " " + typeof rejec);
			self.cidresolve = resolv;
		});
		
		// automatically triggered by url via attribute change
		/*if(window.location.href.indexOf("webgateway")!=-1)
		{
			self.findWebGatewayCid().then(cid =>
			{
				self.cid = cid;
				if(!self.cidresolved)
				{
					self.cidresolved = true;
					self.cidresolve();
				}
			})
			.catch(ex => 
			{
				console.log("Could not get webgateway cid: "+ex);
			});
		}*/
		
		let ret = Promise.all([cidprom,preinitprom]);
		
		ret.then(() => {
			//console.log("ALL RESOVLED");
		});
		return ret;
	}
	
	findWebGatewayCid()
	{
		var self = this;
		return new Promise(function(resolve, reject) 
		{
			axios.get('webjcc/getPlatformId', self.transform).then(function(resp)
			{
				//console.log("cid resolved: "+resp.data);
				resolve(resp.data);
			})
			.catch(function(err) 
			{
				console.log("cid webgateway resolve failed: "+err);	
				reject(err);
			});
		});
	}
	
	attributeChangedCallback(name, oldval, newval) 
	{
	    super.attributeChangedCallback(name, oldval, newval);
		var self = this;
		
	    if("cid"==name) 
		{
			if("webgateway"===newval)
			{
				self.findWebGatewayCid().then(cid =>
				{
					self.cid = cid.name;
					if(!self.cidresolved)
					{
						self.cidresolved = true;
						self.cidresolve();
					}
				})
				.catch(ex => 
				{
					console.log("Could not get webgateway cid: "+ex);
				});
			}
			else
			{
				this.cid = newval;
				
				if(!this.cidresolved)
				{
					this.cidresolved = true;
					this.cidresolve();
				}
			}
	    }
	}

	/**
	 *  Extracts the specified element in the URL-hash (anchor).
	 *  0 is always "#"
	 *  1 is usually "platform"
	 *
	 * @param index Index of the element.
	 * @returns URL-hash element or {null} if out of bounds.
	 */
	getUrlHashParam(index)
	{
		let hashstr = window.location.hash;
		let elems = hashstr.split('/');

		let ret = null;
		if ( elems.length > index)
			ret = elems[index];

		return ret;
	}
	
	static get properties() 
	{
		return { 
			cid: { type: String },
		};
	}
}