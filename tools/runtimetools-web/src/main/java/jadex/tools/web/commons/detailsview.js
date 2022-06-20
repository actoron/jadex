let { LitElement, html, css } = modLoad('lit-element');
let { BaseElement } = modLoad('base-element');

// Tag name 'jadex-detailsview'
class DetailsViewElement extends BaseElement 
{
	init() 
	{
		console.log("detail view");
		this.app.lang.listeners.add(this);
		this.info = null;
	}
	
	postInit()
	{
		// make details dragable
		this.dragElement(this.shadowRoot.getElementById("details"));
	}
	
	setInfo(info)
	{
		this.info = info;
		this.requestUpdate();
	}
	
	getProps()
	{
		var ret = [];
		if(this.info!=null)
		{
			ret = Object.keys(this.info).sort();
			// remove all commands
			for(var i=ret.length-1; i>=0; i--)
			{
				if(ret[i].toLowerCase().endsWith("cmd") 
					|| ret[i].toLowerCase()==="node"
					|| ret[i].toLowerCase()==="heading"
					|| ret[i].toLowerCase()==="hidden"
				)
				{
					ret.splice(i, 1);
				}
			}
		}
		return ret;
	}
	
	// methods for making dragable an element
	dragElement(element) 
	{
		var x1 = 0;
		var y1 = 0;
		var x2 = 0; 
		var y2 = 0;

		var moved = e =>
		{
			e = e || window.event;
	    	e.preventDefault();
	    	x1 = x2 - e.clientX;
	    	y1 = y2 - e.clientY;
	    	x2 = e.clientX;
	    	y2 = e.clientY;
			//console.log("to: "+x2+" "+y2);
	    	// set the element's new position:
			//var y = parseInt(element.style.top) || 0;
	    	//var x = parseInt(element.style.left) || 0;
			element.style.top = element.offsetTop-y1+"px";
	    	element.style.left = element.offsetLeft-x1+"px";
		}

		var md = e => 
		{
			//console.log("offsetx: "+e.offsetX+" "+e.clientX);
			
			if(element.offsetWidth-e.offsetX < 20 && element.offsetHeight-e.offsetY < 20)
			{
				//console.log("at resize border");
				return;
	    	}

			e = e || window.event;
	    	e.preventDefault();
			x2 = e.clientX;
	    	y2 = e.clientY;
			//console.log("from: "+x2+" "+y2);
			
			// clean up document mouse listeners after mouse released
			document.addEventListener("mouseup", e =>
			{
				document.removeEventListener("mouseup", this);
				document.removeEventListener("mousemove", moved);
			});
			
			// watch now for movements
			document.addEventListener("mousemove", moved);
	  	}

		// listen on mouse clicks on that element
		element.addEventListener("mousedown", md);
	}
		
	static get styles() 
	{
		var ret = [];
		if(super.styles!=null)
			ret.push(super.styles);
		ret.push(
		    css`
			.dragable {
				padding: 10px;
				position: fixed;
				left: 50%;
    			top: 50%;
    			transform: translate(-50%, -50%);
				width: 30%;
			  	background-color: #00000011;
			  	border: 1px solid #d3d3d3;
				z-axis: 1;
		 		resize: both;
    			overflow: hidden;
			}
			.grid {
				display: grid;
				grid-template-columns: auto 1fr;
				grid-gap: 10px;
			}
			.marginbottom {
				margin-top: 0px;
				margin-left: 0px;
				margin-right: 0px;
				margin-bottom: 0.5em;
			}
			.maxheight {
				text-overflow: ellipsis;
				word-wrap: break-word;
				overflow: hidden;
				max-height: 3.6em;
				line-height: 1.2em;
			}
			.scroll {
				overflow-y: auto;
			}
		    `);
		return ret;
	}
	
	asyncRender() 
	{
		return html`
			<div id="details" class="dragable ${this.info!=null? 'visible': 'hidden'}">
				<div class="close absolute" @click="${e => {this.info=null; this.requestUpdate();}}"></div>
				<h4 class="marginbottom">${this.info!=null? this.info.heading: ""}</h4>
				<div class="grid marginbottom">
					${this.getProps().map(propname => html`
						<div>${propname.charAt(0).toUpperCase() + propname.slice(1)}</div>
						<div class="scroll maxheight">${this.info[propname]}</div>
					`)}
				</div>
				<div class="${this.info?.refreshcmd!=null? 'visible': 'hidden'}">
					<button type="button" class="jadexbtn" @click="${e => this.info.refreshcmd(this.info.node)}">${this.app.lang.t('Refresh')}</button>
				</div>
			</div>
		`;
	}
}

if(customElements.get('jadex-detailsview') === undefined)
	customElements.define('jadex-detailsview', DetailsViewElement);
			
				
