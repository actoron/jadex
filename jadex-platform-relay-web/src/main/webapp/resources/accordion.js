if(typeof Effect=="undefined"){throw ("accordion.js requires including script.aculo.us' effects.js library!")
}var accordion=Class.create({showAccordion:null,currentAccordion:null,duration:null,effects:[],animating:false,initialize:function(b,c){if(!$(b)){throw (b+" doesn't exist!");
return false
}this.options=Object.extend({resizeSpeed:8,classNames:{toggle:"accordion_toggle",toggleActive:"accordion_toggle_active",content:"accordion_content"},defaultSize:{height:null,width:null},direction:"vertical",onEvent:"click"},c||{});
this.duration=((11-this.options.resizeSpeed)*0.15);
var a=$$("#"+b+" ."+this.options.classNames.toggle);
a.each(function(d){Event.observe(d,this.options.onEvent,this.activate.bind(this,d),false);
if(this.options.onEvent=="click"){d.onclick=function(){return false
}
}if(this.options.direction=="horizontal"){var e={width:"0px"}
}else{var e={height:"0px"}
}Object.extend(e,{display:"none"});
this.currentAccordion=$(d.next(0)).setStyle(e)
}.bind(this))
},activate:function(a){if(this.animating){return false
}this.effects=[];
this.currentAccordion=$(a.next(0));
this.currentAccordion.setStyle({display:"block"});
this.currentAccordion.previous(0).addClassName(this.options.classNames.toggleActive);
if(this.options.direction=="horizontal"){this.scaling={scaleX:true,scaleY:false}
}else{this.scaling={scaleX:false,scaleY:true}
}if(this.currentAccordion==this.showAccordion){this.deactivate()
}else{this._handleAccordion()
}},deactivate:function(){var a={duration:this.duration,scaleContent:false,transition:Effect.Transitions.sinoidal,queue:{position:"end",scope:"accordionAnimation"},scaleMode:{originalHeight:this.options.defaultSize.height?this.options.defaultSize.height:this.currentAccordion.scrollHeight,originalWidth:this.options.defaultSize.width?this.options.defaultSize.width:this.currentAccordion.scrollWidth},afterFinish:function(){this.showAccordion.setStyle({height:"0px",display:"none"});
this.showAccordion=null;
this.animating=false
}.bind(this)};
Object.extend(a,this.scaling);
this.showAccordion.previous(0).removeClassName(this.options.classNames.toggleActive);
new Effect.Scale(this.showAccordion,0,a)
},_handleAccordion:function(){var a={sync:true,scaleFrom:0,scaleContent:false,transition:Effect.Transitions.sinoidal,scaleMode:{originalHeight:this.options.defaultSize.height?this.options.defaultSize.height:this.currentAccordion.scrollHeight,originalWidth:this.options.defaultSize.width?this.options.defaultSize.width:this.currentAccordion.scrollWidth}};
Object.extend(a,this.scaling);
this.effects.push(new Effect.Scale(this.currentAccordion,100,a));
if(this.showAccordion){this.showAccordion.previous(0).removeClassName(this.options.classNames.toggleActive);
a={sync:true,scaleContent:false,transition:Effect.Transitions.sinoidal};
Object.extend(a,this.scaling);
this.effects.push(new Effect.Scale(this.showAccordion,0,a))
}new Effect.Parallel(this.effects,{duration:this.duration,queue:{position:"end",scope:"accordionAnimation"},beforeStart:function(){this.animating=true
}.bind(this),afterFinish:function(){if(this.showAccordion){this.showAccordion.setStyle({display:"none"})
}this.showAccordion=this.currentAccordion;
this.animating=false
}.bind(this)})
}});
function createAccordion(b){var a=new accordion(b.div,{resizeSpeed:10,classNames:{toggle:"accordionTabTitleBar",content:"accordionTabContentBox"},defaultSize:{width:("width" in b?b.width:null),height:("height" in b?b.height:null)}});
a.activate($$("#"+b.div+" .accordionTabTitleBar")[b.no])
};