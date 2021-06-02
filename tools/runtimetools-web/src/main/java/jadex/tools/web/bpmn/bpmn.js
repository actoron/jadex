let { LitElement, html, css } = modLoad('lit-element');
let { BaseElement } = modLoad('base-element');

// Tag name 'jadex-starter'
class BpmnElement extends BaseElement {

	bpmnmodeler = null;
	
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
		
		let styles = ["jadex/tools/web/bpmn/diagram-js.css",
					  "jadex/tools/web/bpmn/bpmn.css"];
		
		this.loadServiceFont('bpmn', 'jadex/tools/web/bpmn/bpmn.woff2').then((values) =>
		{
			console.log("BPMN load fonts ok");
			
			this.loadServiceStyles(styles).then((values) => 
			{
				console.log("BPMN load styles ok");
				
				let scripts = ["jadex/tools/web/bpmn/bpmnmodeler.js" ];
				this.loadServiceScripts(scripts).then((values) => 
				{
					console.log("BPMN load scripts ok");
					let celem = this.shadowRoot.getElementById('bpmnview');
					self.bpmnmodeler = new BpmnJS({
			        	container: celem,
			        	keyboard: {
			          		bindTo: window
			        	}
					});
					// Load empty model.
					self.bpmnmodeler.createDiagram();
				});
			});
		});
		
		
		
		//const myElement = document.querySelector('my-element');
		this.addEventListener('jadex-model-selected', (e) => 
		{
			console.log(e)
			self.model = e.detail.model;
			self.requestUpdate();
		});
		
		let body = document.querySelector('body');
		
	}
	
	render() {
		return html`
		<div style="height:100px" id="bpmnview"/>
		`;
	}
}
if (customElements.get('jadex-bpmn') === undefined)
	customElements.define('jadex-bpmn', BpmnElement);
