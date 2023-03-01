let { LitElement, html, css } = modLoad('lit-element');
let { BaseElement } = modLoad('base-element');
let { CidElement } = modLoad('cid-element');

// Tag name 'jadex-starter'
class BpmnElement extends CidElement {

	// The BPMN.js modeler.
	bpmnmodeler = null;
	
	/**
	 * The Jadex BPMN extensions
	 * <bpmnobjectid> -> <extensionobject>
	 */
	bpmnextensionmodel = {};
	
	static get properties() 
	{ 
		return { cid: { type: String }};
	}
	
	static get styles() 
	{
		let ret = [];
		if(super.styles!=null)
			ret.push(super.styles);
		ret.push(
		    css`
	    		.gridcontainer {
					 display: grid;
				}
				.splitcontainer {
					grid-template-rows: 2fr 1fr; 
					grid-template-columns: 1fr;
				}
				.modelcontainer {
					grid-row-start: 1;
  					grid-row-end: 2;
				}
				.propcontainer {
					grid-row-start: 3;
  					grid-row-end: 3;
				}
		    `);
		return ret;
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
		
		let self = this;
		
		let styles = ["jadex/tools/web/bpmn/diagram-js.css",
					  "jadex/tools/web/bpmn/bpmn-js.css",
					  "jadex/tools/web/bpmn/bpmn.css"];

		this.loadServiceFont('bpmn', 'jadex/tools/web/bpmn/bpmn.woff2').then((values) =>
		{
			console.log("BPMN load fonts ok");

			this.loadServiceStyles(styles).then((values) =>
			{
				console.log("BPMN load styles ok");

				let scripts = ["jadex/tools/web/bpmn/bpmn-modeler.development.js",
							   "jadex/tools/web/bpmn/servicetaskpanel.js",
							   "jadex/tools/web/bpmn/jadexservicetaskpanel.js" ];
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
					self.bpmnextensionmodel = {};
					self.bpmnmodeler.on('selection.changed', (bpmnselection) => {
						let proppanel = self.shadowRoot.getElementById("propertypanel");
						while (proppanel.hasChildNodes()) {
							proppanel.removeChild(proppanel.firstChild);
						}
						
						//console.log(self.bpmnmodeler.businessObject)
						if (bpmnselection.newSelection.length > 0) {
							let bpmnelem = bpmnselection.newSelection[0];
							if (bpmnelem.type && bpmnelem.type === "bpmn:ServiceTask") {
								proppanel.innerHTML = "<jadex-servicetaskpanel></jadex-servicetaskpanel>";
							}
						}
						
					});
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
	
	getBpmnModel() {
		return this.bpmnmodeler;
	}
	
	getExtensionModel() {
		return bthis.bpmnextensionmodel;
	}
	
	asyncRender() {
		return html`
		<div style="height:60vh" class="gridcontainer splitcontainer">
			<div class="modelcontainer" id="bpmnview"></div>
			<div id="propertypanel"></div>
		</div>
		`;
	}
	
	getJadexService()
	{
		return "jadex.tools.web.bpmn.IJCCBpmnService";
	}
}
if (customElements.get('jadex-bpmn') === undefined)
	customElements.define('jadex-bpmn', BpmnElement);
