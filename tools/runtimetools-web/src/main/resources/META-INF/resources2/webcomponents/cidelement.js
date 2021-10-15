import {LitElement, html, css} from '../libs/lit/lit-element.js';
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
		let ret = Promise.all([cidprom,preinitprom]);
		ret.then(() => {
			//console.log("ALL RESOVLED");
		});
		return ret;
	}
	
	attributeChangedCallback(name, oldVal, newVal) 
	{
	    super.attributeChangedCallback(name, oldVal, newVal);
		
	    if("cid"==name) 
		{
			this.cid = newVal;
			
			if (!this.cidresolved)
			{
				this.cidresolved = true;
				this.cidresolve();
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