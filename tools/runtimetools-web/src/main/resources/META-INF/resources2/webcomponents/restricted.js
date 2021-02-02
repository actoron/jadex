let { LitElement, html, css } = modLoad('lit-element');
let { BaseElement } = modLoad('base-element');

// Defined as <jadex-restricted> tag
class RestrictedElement extends BaseElement 
{
	init()
	{
		this.app.lang.listeners.add(this);
	}

	asyncRender() 
	{
		return html`
			<div class="jumbotron jumbotron-fluid m-3 p-3">
				<div class="row">
					<div class="col-12" class="${this.app.lang.getLanguage()? 'visible': 'hidden'}">
						This plugin is restricted and can used be used when logged in.
					</div>
					<div class="col-12" class="${!this.app.lang.getLanguage()? 'visible': 'hidden'}">
						Dieses plugin kann nur genutzt werden, wenn man eingeloggt ist.
					</div>
				</div>
			</div>
		`
	}
}

if(customElements.get('jadex-restricted') === undefined)
	customElements.define('jadex-restricted', RestrictedElement);