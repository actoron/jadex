let { LitElement, html, css } = modLoad('lit-element');
let { BaseElement } = modLoad('base-element');

// Defined as <jadex-app> tag
class AppElement extends BaseElement 
{
	message = {};
	listener = null;
	loggedin = false;
	version = "n/a";
	
	static get properties() 
	{
		var ret = {};
		if(super.properties!=null)
		{
			for (let key in super.properties)
				ret[key]=super.properties[key];
		}
		ret['version'] = { type: String };
		return ret;
	}
	
	postInit() 
	{
		//console.log("app element");
		
		let self = this;
		
		this.app.lang.listeners.add(self);
		
		page.base('/#');
		page('/index.html', () => {
			page.redirect("/platform/webgateway");
	    });
	    page('/', () => {
	    	page.redirect("/platform/webgateway");
	    });
 		page('/login', () => {
	    	self.shadowRoot.getElementById("content").innerHTML = "<jadex-login></jadex-login>";
	    });
	    page('/platforms', () => {
	    	self.shadowRoot.getElementById("content").innerHTML = "<jadex-platforms></jadex-platforms>";
	    });
	    page('/platform/:cid', (ctx, next) => {
	    	//console.log("router, cid: "+ctx.params.cid);
	    	self.shadowRoot.getElementById("content").innerHTML = "<jadex-platform cid='"+ctx.params.cid+"'></jadex-platform>";
	    });
	    page('/platform/:cid/:plugin', (ctx, next) => {
	    	//console.log("router, cid: "+ctx.params.cid+ " plugin: " + ctx.params.plugin);
	    	self.shadowRoot.getElementById("content").innerHTML = "<jadex-platform cid='"+ctx.params.cid+"' plugin='"+ctx.params.plugin+"'></jadex-platform>";
	    });
	    page('/about', () => {
	    	self.shadowRoot.getElementById("content").innerHTML = "<jadex-about></jadex-about>";
	    });
	    page('/imprint', () => {
	    	self.shadowRoot.getElementById("content").innerHTML = "<jadex-imprint></jadex-imprint>";
	    });
	    page('/privacy', () => {
	    	self.shadowRoot.getElementById("content").innerHTML = "<jadex-privacy></jadex-privacy>";
	    });
	    /*page('*', (ctx, next) => {
	    	console.log("nav not found: "+ctx);
	    });*/

		page();
		
		this.getVersion().then(v =>
		{
			this.version = v;
		})
		.catch(err =>
		{
			console.log("could not get version: "+err);
		});
	}
	
	connectedCallback() 
	{
		super.connectedCallback();

		var self = this;
		if(this.listener==null)
		{
			this.listener = (e) => 
			{
				//console.log(e)
				self.message = e.detail==null? {}: e.detail;
				self.requestUpdate();
			}
		}
		
		//const myElement = document.querySelector('my-element');
		this.addEventListener('jadex-message', this.listener);
	}
	
	switchLanguage()
	{
		this.app.lang.setLanguage(this.app.lang.getLang()==='en'? 'de' : 'en');
	}
	
	getVersion()
	{
		var self = this;
		return new Promise(function(resolve, reject) 
		{
			axios.get('webjcc/getVersion', self.transform).then(function(resp)
			{
				console.log("version resolved: "+resp.data);
				resolve(resp.data);
			})
			.catch(function(err) 
			{
				//console.log("get version failed: "+err);	
				reject(err);
			});
		});
	}
	
	/*disconnectedCallback()
	{
		super.disconnectedCallback();
		if(this.listener!=null)
			this.removeEventListener(this.listener);
	}*/
	
	/*firstUpdated(props) 
	{
		page();
	}*/
	
	
	
	static get styles() 
	{
	    return css`
			.grid {
	    		display: grid;
				grid-template-rows: auto 1fr auto auto;
				grid-template-columns: 100%;
				min-height: 100%;
			}
			#content {
				display: flex;
			}
			.borderbottom {
				border-bottom: 0.5em solid #2a6795;
			}
			.borderbottom1 {
				border-bottom: 1em solid #2a6795;
			}
			.minheight {
				min-height: 3em;
				height: 3em;
			}
			.posrel {
				position: relative;
			}
			.h1px {
				height: 1px;
			}
			.flexmiddle {
				display: flex;
  				justify-content: center;
  				align-content: center;
  				flex-direction: column;
			}
	    `;
	}
	
	asyncRender() 
	{
		return html`
			<div class="grid h100 margin1">
				<div class="borderbottom paddingbottom marginbottom1">
		 			<a href="#/platforms"><img src="images/jadex_logo_ac_new_webjcc.png" width="200px"/></a>
					<img @click="${this.switchLanguage}" src="${this.app.lang.getFlagUrl()}" />
					<jadex-login class="right h100"></jadex-login>
				</div>
		
				<div id="content"></div>
		
				<div class="${this.message.text!=null? 'visible': 'hidden'}">
					<div class="minheight ${(this.message.type=='error'? 'colorerror': 'colorinfo')}">
						<div class="close relative right" @click="${e => this.clearMessage()}"></div>
						<div class="flexmiddle h100 marginleft1">${this.message.text}</div>
					</div>
				</div>
		
				<footer>
			        <span>Jadex ${this.version}, <a href="http://www.actoron.com">Actoron GmbH</a> 2017-${new Date().getFullYear()}</span>
			    	
					<div class="right">
						<a href="#/about">${this.app.lang.t("About")}</a>
			    		<a href="#/privacy">${this.app.lang.t("Privacy")}</a>
			    		<a href="#/imprint">${this.app.lang.t("Imprint")}</a>
			    	</div>
			    </footer>
			</div>
		`;
	}
}

if(customElements.get('jadex-app') === undefined)
	customElements.define('jadex-app', AppElement);