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
	
	constructor() {
		super();

		console.log("bpmn");
		
		this.cid = null;
		this.model = null; // loaded model
		this.reversed = false;
		this.jadexservice = "jadex.tools.web.starter.IJCCStarterService";
		
		let self = this;
		
		let scripts = ["jadex/tools/web/bpmn/bpmnmodeler.js"]
		loadServiceScripts(scripts).then((values) => 
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
		return html`
			<div class="container-fluid">
				<div class="row m-1">
					<div class="col-12 m-1">
						<h3>Components</h3>
						<jadex-componenttree cid='${this.cid}'></jadex-componenttree>
					</div>
				</div>
				
				<div class="row m-1">
					<div class="col-12 m-1">
						<h3>Available Models</h3>
						<jadex-modeltree cid='${this.cid}'></jadex-modeltree>
					</div>
				</div>
				
				${this.model!=null? html`
				<div class="bgwhitealpha m-2 p-2"> <!-- sticky-top  -->
					<div class="row m-1">
						<div class="col-12">
							<h3>Settings</h3>
						</div>
					</div>
					<div class="row m-1">
						<div class="col-2">
							Filename
						</div>
						<div class="col-10" id="filename">
							<input type="text" ref="filename" class="w100" value="${this.model!=null? this.model.filename: ''}">
						</div>
					</div>
					<div class="row m-1">
						<div class="col-2">
							Configuration
						</div>
						<div class="col-10">
							<select id="config" class="w100">
		   						${this.getConfigurationNames().map((c) => html`<option value="${c}"></option>`)}
		 					</select>
						</div>
					</div>
					<div class="row m-1">
						<div class="col-2">
							Comp. name
						</div>
						<div class="col-5">
							<input type="text" class="w100" value="${this.model!=null && this.model.instancename!=null? this.model.instancename: ''}" id="name"></input>
						</div>
						<div class="col-3">
							<input type="checkbox" id="autogen">Auto generate</input>
						</div>
						<div class="col-2">
							<input class="w100" type="number" value="1" id="gencnt"></input>
						</div>
					</div>
					<div class="row m-1">
						<div class="col-4">
							<input type="checkbox" id="suspended">Suspended</input>
						</div>
						<div class="col-4">
							<input type="checkbox" id="synchronous">Synchronous</input>
						</div>
						<div class="col-4">
							<select id="monitoring" class="w100">
		   						<option value="OFF">OFF</option> 
		   						<option value="COARSE">COARSE</option> 
		   						<option value="MEDIUM">MEDIUM</option> 
		   						<option value="FINE">FINE</option> 
		 					</select>
		 				</div>
					</div>
					
					<div class="row m-1">
						${this.getArguments().map((arg, i) => html`
						<div class="col-4"">
							${"["+arg.clazz.value+"] "+arg.name}
						</div>
						<div class="col-4 p-0">
							<input class="w100" type="text" value="${arg.value!=null? arg.value: ''}" readonly></input>
						</div>
						<div class="col-4 pl-2"> 
							<input class="w100" type="text" id="${'arg_'+i}">
						</div>
						`)}
					</div>
					
					<div class="row m-1">
						<div class="col-10">
						</div>
						<div class="col-2">
							<button class=" float-right" @click="${e => this.start(e)}">Start</button> <!-- class="w100" -->
						</div>
					</div>
				</div>
				`: ''}
			</div>
		`;
	}
}

customElements.define('jadex-bpmn', BpmnElement);
