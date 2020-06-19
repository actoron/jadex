import {LitElement} from 'https://unpkg.com/lit-element@latest/lit-element.js?module';
import {html} from 'https://unpkg.com/lit-html@latest/lit-html.js?module';
import {css} from 'https://unpkg.com/lit-element@latest/lit-element.js?module';

export class BaseElement extends LitElement {

	loadCSS(url)
	{
		axios.get(url).then(function(resp)
		{
			var css = resp.data;    
			//console.log(css);
			var sheet = new CSSStyleSheet();
			sheet.replaceSync(css);
			self.shadowRoot.adoptedStyleSheets = self.shadowRoot.adoptedStyleSheets.concat(sheet);
		});
	}
}

//customElements.define('jadex-base', StarterElement);
