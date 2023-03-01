let { LitElement, html, css } = modLoad('lit-element');
let { BaseElement } = modLoad('base-element');

// Tag name 'jadex-starter'
class JadexServiceTaskPanel extends BaseElement {
	
	static get styles() 	{
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
	
	asyncRender() {
		return html`
		<div id="jadexservicetaskpanel">
			<p>JADEX SERVICE PANEL</p>
		</div>
		`;
	}
}
if (customElements.get('jadex-jadexservicetaskpanel') === undefined)
	customElements.define('jadex-jadexservicetaskpanel', JadexServiceTaskPanel);
