let { LitElement, html, css } = modLoad('lit-element');
let { BaseElement } = modLoad('base-element');

// Tag name 'jadex-suggestionbox'
class SuggestionBox extends BaseElement {
	
	// ID of the input element
	inputid;
	
	// ID of the datalist element
	datalistid;
	
	// Data callback
	datacallback;
	
	// Input change listener
	inputchangelistener;
	
	init() {
		let rarr = new Uint8Array(16);
		crypto.getRandomValues(rarr);
		this.datalistid = rarr.reduce((v, i) => {return v + ('0' + i.toString(16)).slice(-2)})
		crypto.getRandomValues(rarr);
		this.inputid = rarr.reduce((v, i) => {return v + ('0' + i.toString(16)).slice(-2)})
	}
	
	attached() {
		let self = this;
		this.inputchangelistener = (e) => {
			if (typeof callback === 'function') {
				self.inputChange();
			}
		}
		document.getElementById(self.inputid).addEventListener('input', this.inputchangelistener);
		self.inputChange();
	}
	
	detached() {
		document.getElementById(inputid).removeEventListener('input', this.inputchangelistener);
	}
	
	getInput() {
		return this.shadowRoot.getElementById(inputid);
	}
	
	getDataList() {
		return this.shadowRoot.getElementById(this.datalistid);
	}
	
	setDataCallback(callback) {
		if (typeof callback === 'function') {
			this.datacallback = callback;
			this.inputChange();
		}
		else {
			throw "Not a callback function.";
		}
	}
	
	inputChange() {
		if (typeof callback === 'function') {
			let inputval = this.shadowRoot.getElementById(inputid).value;
			let data = this.datacallback(inputval);
			if (Array.isArray(data)) {
				let html = "";
				data.forEach((d) => {
					html += '<option value="' + d + '"></option>\n';
				});
				this.shadowRoot.getElementById(this.datalistid).innerHTML = html;
			}
		}
		
	}
	
	asyncRender() {
		return html`
		<div>
			<input id=${this.inputid} @input="inputChange" list=${this.datalistid} name="" >
			<datalist id=${this.datalistid}>
			</datalist>
		</div>
		`;
	}
}
if (customElements.get('jadex-suggestionbox') === undefined)
	customElements.define('jadex-suggestionbox', SuggestionBox);
