let { LitElement, html, css } = modLoad('lit-element');
let { BaseElement } = modLoad('base-element');
let { CidElement } = modLoad('cid-element');

// Tag name 'jadex-microagentdebugger'
class MicroAgentDebuggerElement extends CidElement 
{
	init() 
	{
		console.log("micro debugger: "+this.cid);
		this.app.lang.listeners.add(this);
		this.comp = null; // selected component
		this.concom = false;
		this.myservice = "jadex.tools.web.debugger.IJCCDebuggerService";
		this.sub = {};
		this.steps = [];
		this.history = [];
		this.selstep = null;
		this.subscribe();
	}
	
	connectedCallback()
	{
		super.connectedCallback();
		this.concom = true;	
		//this.subscribe();
	}
	
	disconnectedCallback()
	{
		super.disconnectedCallback();
		this.concom = false;	
		this.terminateSubscription();
	}
	
	subscribe(interval)
	{
		//console.log("subscribeTo"+x);
		
		this.terminateSubscription();
		
		if(interval===undefined)
			interval = 5000;

		var self = this;
		//console.log("sub at: "+this.cid);
		var types = ["step"];
		self.sub.callid = jadex.getIntermediate(this.getMethodPrefix()+'&methodname=subscribeToComponent'
			+'&args_0='+this.cid+'&args_1='+JSON.stringify(types)
			+'&argtypes_0=jadex.bridge.IComponentIdentifier&argtypes_1=java.lang.String[]'
			+'&returntype=jadex.commons.future.ISubscriptionIntermediateFuture',
		response =>
		{
			//console.log("service sub received: "+response.data);
			
			self.sub.connected = true;
			var event = response.data;
			
			if(event.bulkEvents!=null && event.bulkEvents.length>0)
			{
				// bulk event indicates initial event -> remove all before adding
				self.steps.length = 0;
				for(var i=0; i<event.bulkEvents.length; i++)
				{
					self.updateEvent(event.bulkEvents[i]);
				}
			}
			else
			{
				self.updateEvent(event);
			}
		},
		err =>
		{
			console.log("Err: "+err);
			self.sub.connected = false;
			self.requestUpdate();
			
			setTimeout(function()
			{
				if(self.concom)
				{
					console.log("Retrying platform connection...");
					self.subscribe(interval);
				}
				else
				{
					//console.log("Subcribe terminated due to component disconnect: "+x);
				}
			}, interval);
		});
	}
	
	terminateSubscription()
	{
		var callid = this.sub.callid;
		if(callid!=null)
		{
			this.sub.callid = null;
			jadex.terminateCall(callid).then(() => 
			{
				this.sub.connected = false;
				//console.log("Terminated subscription: "+self.callid)
			})
			.catch(err => {console.log("Could not terminate subscription: "+err+" "+callid)});
		}
	}
	
	updateEvent(event)
	{
		if(event.type.startsWith("created") && event.type.endsWith("step"))
		{
			//console.log("add step: "+event.properties.id);
			this.steps.push(event);
			//if(laststep==null && steps.size()==1)
			//	sl.setSelectedIndex(0);
		}
		else if(event.type.startsWith("disposed") && event.type.endsWith("step"))
		{
			for(var i=0; i<this.steps.length; i++)
			{
				var tmp = this.steps[i];
				if(event.properties.id===tmp.properties.id)
				{
					//console.log("remove step: "+event.properties.id);
					this.steps.splice(i, 1);
					//if(laststep!=null && laststep.getProperty("id").equals(tmp.getProperty("id")))
					//	laststep = null;
					break;
				}
			}
			
			//if(laststep==null)
			//	sl.setSelectedIndex(0);
			
			if(this.isHistoryEnabled())
			{
				this.history.push(event);
				//hl.ensureIndexIsVisible(history.size()-1);
			}
		}
		
		this.requestUpdate();
	}
	
	getMethodPrefix() 
	{
		return 'webjcc/invokeServiceMethod?cid='+this.getPlatformCid(this.cid)+'&servicetype='+this.myservice;
	}
	
	getPlatformCid(cid)
	{
		var idx = cid.indexOf('@');
		return idx!=-1? cid.substring(idx+1): cid;
	}
	
	getStepDetails(step)
	{
		var ret = [];
		if(step?.properties?.details)
		{
			var keys = Object.keys(step.properties.details);
    		keys.sort();
			for(var i=0; i<keys.length; ++i)
			{
        		ret[i] = {name: keys[i], value: step.properties.details[keys[i]]};
    		}
		}
		return ret;
	}
	
	selectStep(step)
	{
		this.selstep = step;
		this.requestUpdate();
	}
	
	getStepInfo()
	{
		return this.selstep==null? null: this.selstep.properties.id;
	}
	
	stepToString(step)
	{
		var ret = "";
		var clazz = step.properties?.details?.Class;
		if(clazz!=null)
			ret += clazz;
		var id = step.properties?.details?.Id;
		if(id!=null)
			ret += " id: "+id;
		var prio = step.properties?.details?.Priority;
		if(prio!=null)
			ret += " prio: "+prio;
		if(ret.length==0)
			ret = JSON.stringify(step);
		return ret;
	}
	
	getSteps()
	{
		return this.steps!=null? this.steps: [];
	}
	
	getHistory()
	{
		return this.history!=null? this.history: [];
	}
	
	toggleHistory()
	{
		var elem = this.shadowRoot.getElementById("historyon");
		if(!elem.checked)
			this.history.length = 0;
		this.requestUpdate();
	}
	
	isHistoryEnabled()
	{
		return this.shadowRoot.getElementById("historyon").checked;
	}
	
	stepEquals(step1, step2)
	{
		return step1?.properties?.details?.Id === step2?.properties?.details?.Id;
	}
		
	static get styles() 
	{
		var ret = [];
		if(super.styles!=null)
			ret.push(super.styles);
		ret.push(
		    css`
			.w100 {
				width: 100%;
			}
			.h100 {
				height: 100%;
			}
			.grid-container {
				display: grid;
				grid-template-columns: minmax(0, 1fr) minmax(0, 1fr); 
				grid-template-rows: minmax(200px, 30vh) minmax(200px, 30vh);
				grid-gap: 10px;
			}
			#details {
				grid-column: 1 / span 2;
			}
			.inner {
				display: flex;
				flex-flow: column;
			}
			
			.yscrollable {
				overflow-y: auto;
			}
			.margin {
				margin-left: 10px;
				margin-right: 10px;
			}
			.nomargintop {
				margin-top: 0px;
			}
			.nomarginbottom {
				margin-bottom: 0px;
			}
			.selected {
				background-color: #beebff;
			}
		    `);
		return ret;
	}
	
	asyncRender() 
	{
		return html`
			<div id="panel" class="grid-container">
				<div id="steps" class="back-lightgray inner">
					<h4 class="margin nomargintop nomarginbottom">${this.app.lang.t('Steps')}</h4>
					<div class="yscrollable h100">
						<table class="margin">
							${this.getSteps().map(step => html`
								<tr class="${this.stepEquals(this.selstep, step)? 'selected': ''}" @click="${e => this.selectStep(step)}">
		  							<td>${this.stepToString(step)}</td>
							    </tr>
							`)}
						</table>
					</div>
				</div>
				<div id="history" class="back-lightgray inner">
					<h4 class="margin nomargintop nomarginbottom">${this.app.lang.t('History')}</h4>
					<div class="yscrollable h100">
						<table class="margin">
							${this.getHistory().map(step => html`
								<tr class="${this.stepEquals(this.selstep, step)? 'selected': ''}" @click="${e => this.selectStep(step)}">
		  							<td>${this.stepToString(step)}</td>
							    </tr>
							`)}
						</table>
					</div>
					<div class="margin">
						<input id="historyon" type="checkbox" class="history_c1" name="his" checked @click="${e => {this.toggleHistory()}}">
						<label for="his">History</label>
					</div>
				</div>
				<div id="details" class="back-lightgray inner">
					<h4 class="margin nomargintop nomarginbottom">${this.app.lang.t('Step Details')}</h4>
					<div class="yscrollable h100">
						<table class="margin">
						${this.getStepDetails(this.selstep).map(prop => html`
							<tr>
	  							<td>${prop.name}</td>
								<td>${prop.value}</td>
						    </tr>
						`)}
						</table>
					</div>
				</div>
			</div>
		`;
	}
}

if(customElements.get('jadex-microagentdebugger') === undefined)
	customElements.define('jadex-microagentdebugger', MicroAgentDebuggerElement);
