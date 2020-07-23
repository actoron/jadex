import {LitElement} from 'https://unpkg.com/lit-element@latest/lit-element.js?module';
import {html} from 'https://unpkg.com/lit-html@latest/lit-html.js?module';
import {css} from 'https://unpkg.com/lit-element@latest/lit-element.js?module';
import {BaseElement} from '/webcomponents/baseelement.js'

// Tag name 'jadex-starter'
class BpmnElement extends BaseElement {

	static get properties() 
	{ 
		return { cid: { type: String }};
	}
	
	attributeChangedCallback(name, oldVal, newVal) 
	{
	    console.log('attribute change: ', name, newVal, oldVal);
	    super.attributeChangedCallback(name, oldVal, newVal);
	    
		console.log("bpmn: "+this.cid);
	}
	
	init() {
		console.log("bpmn");
		
		this.model = null; // loaded model
		this.reversed = false;
		this.jadexservice = "jadex.tools.web.bpmn.IJCCBpmnService";
		
		let self = this;
		
		let scripts = ["jadex/tools/web/bpmn/bpmnmodeler.js"]
		this.loadServiceScripts(scripts).then((values) => 
		{
			console.log("BPMN load files ok");
		});
		
		//const myElement = document.querySelector('my-element');
		this.addEventListener('jadex-model-selected', (e) => 
		{
			console.log(e)
			self.model = e.detail.model;
			self.requestUpdate();
		});
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
	    	
	    	.w100 {
				width: 100%;
			}
			.loader {
				border: 8px solid #f3f3f3;
				border-top: 8px solid #070707; 
				border-radius: 50%;
				width: 60px;
				height: 60px;
				animation: spin 2s linear infinite;
			}
			@keyframes spin {
	  			0% { transform: rotate(0deg); }
	  			100% { transform: rotate(360deg); }
			}
	    `;
	}
	
	render() {
		return html`<h1>Tolles BPMN</h1>`;
	}
}

customElements.define('jadex-bpmn', BpmnElement);
