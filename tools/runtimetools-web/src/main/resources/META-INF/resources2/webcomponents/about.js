import { LitElement, html, css } from 'lit-element';
import { BaseElement } from 'base-element';

// Defined as <jadex-about> tag
class AboutElement extends BaseElement {

	constructor() {
		super();
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
	    `;
	}
	
	render() {
		return html`
			<div class="jumbotron jumbotron-fluid m-3 p-3">
				<div class="row">
					<div class="col-12" if="{lang=='en'}">
						<a href="http://www.actoron.com">Actoron GmbH</a><br/><br/>
			
						Richardstra&szlig;e 49<br/>
						22081 Hamburg<br/>
						Tel.: +49 (0)15751299571<br/>
						E-Mail: info@actoron.com<br/>
						USt-IdNr.: DE-296923623<br/>
						Amtsgericht Hamburg, HRB 133943<br/><br/>
			
						Managing Directors: Alexander Pokahr (CEO), Lars Braubach (CTO), Kai Jander (COO)<br/>
						VAT identification number: DE-296923623<br/><br/>
			
						Contentwise responsible person in accordance with &sect;55 paragraph 2 RStV:<br/>
						Kai Jander, Richardstra&szlig;e 49, 22081 Hamburg<br/><br/>
			
						&copy; Copyright 2014-{new Date().getFullYear()} All rights reserved.<br/>
					</div>
					<div class="col-12" if="{lang=='de'}">
						<a href="http://www.actoron.com">Actoron GmbH</a><br/><br/>
			
						Richardstra&szlig;e 49<br/>
						22081 Hamburg<br/>
						Tel.: +49 (0)15751299571<br/>
						E-Mail: info@actoron.com<br/>
						USt-IdNr.: DE-296923623<br/>
						Amtsgericht Hamburg, HRB 133943<br/><br/>
			
						Gesch&auml;ftsf&uuml;hrer der Actoron GmbH (einzelvertretungsberechtigt): Alexander Pokahr (CEO), Lars Braubach (CTO), Kai Jander (COO)<br/>
			
						Redaktionelle Verantwortung:<br/>
						Kai Jander, Richardstra&szlig;e 49, 22081 Hamburg<br/><br/>
			
						Haftungshinweis:<br/>
						Wir &uuml;bernehmen keinerlei Verantwortung oder Haftung f&uuml;r die Angaben auf dieser Webseite. Unser Ziel ist es, aktuelle und genaue Informationen bereitzustellen. Allerdings kann nicht garantiert werden, dass die auf dieser Webseite verf&uuml;gbaren Angaben tats&auml;chlich aktuell, umfassend, komplett oder genau sind. Bei den bereitgestellten Informationen handelt es sich um solche allgemeiner Art, die nicht auf die besonderen Bed&uuml;rfnisse bestimmter Personen oder Unternehmen abgestimmt sind. Insbesondere soll durch sie keine Beratung erfolgen. Sofern von dieser Webseite auf andere Webseiten verwiesen wird, k&ouml;nnen wir deren Inhalt nicht beeinflussen und f&uuml;r diesen auch keine Verantwortung &uuml;bernehmen.<br/><br/>
			
						&copy; Copyright 2014-{new Date().getFullYear()} Alle Rechte vorbehalten.
					</div>
				</div>
			</div>
		`;
	}
	
	switchLanguage() {
	    language.switchLanguage(); 
	    this.requestUpdate(); // needs manual update as language.lang is not mapped to an attribute 
	}
}

customElements.define('jadex-about', AboutElement);
