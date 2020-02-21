import { LitElement, html, css } from 'lit-element';

class AppElement extends LitElement {

	render() {
		return html`
			<h1>hiii</h1>
		`;
	}
}

customElements.define('jadex-app', PlatformsElement);

