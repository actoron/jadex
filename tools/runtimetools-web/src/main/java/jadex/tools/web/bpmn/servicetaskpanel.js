let { LitElement, html, css } = modLoad('lit-element');
let { BaseElement } = modLoad('base-element');

// Tag name 'jadex-starter'
class ServiceTaskPanel extends BaseElement {
	
	servicetype = "servicetype";
	
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
	
	attached() {
		this.selectType(this.shadowRoot.getElementById(this.servicetype).value);
	}
	
	selectTypeEvent(selectEvent) {
		let selected = selectEvent.srcElement.value;
		this.selectType(selected);
	}
	
	selectType(selected) {
		let content = this.shadowRoot.getElementById("servicetaskpanelcontent");
		
		while (content.hasChildNodes()) {
			content.removeChild(content.firstChild);
		}
		
		if ("jadexservice" === selected)
		{
			content.innerHTML = "<jadex-jadexservicetaskpanel></jadex-jadexservicetaskpanel>";
		}
	}
	
	asyncRender() {
		return html`
		<div id="servicetaskpanel">
			<!-- <label for=${this.servicetype}>Choose a service type:</label> -->
			<select id=${this.servicetype} class="jadexbtn" @change="${this.selectTypeEvent}">
				<option selected value="jadexservice">Jadex Service</option>
				<option value="restservice">REST Service</option>
			</select>
			<div id="servicetaskpanelcontent"></div>
		</div>
		`;
	}
}
if (customElements.get('jadex-servicetaskpanel') === undefined)
	customElements.define('jadex-servicetaskpanel', ServiceTaskPanel);
