import * as litelement from "./lit-3.2.0/lit-element.js"
import * as baseelement from "../webcomponents/baseelement.js"
import * as cidelement from "../webcomponents/cidelement.js"

//console.log("Executing modloader " + Object.keys(litelement));

window.modLoad = (function () {
	let resolvemap = {};
	resolvemap["lit-element"] = litelement;
	resolvemap["base-element"] = baseelement;
	resolvemap["cid-element"] = cidelement;
	
	document.querySelectorAll("script[data-name]").forEach( (mappedmodule) => {
		console.log("Adding module mapping " +mappedmodule.dataset.name + " to " + mappedmodule.src);
		resolvemap[mappedmodule.dataset.name] = mappedmodule.src;
	});
	
	return function(name, url) {
		if (arguments.length === 1) {
			let ret = resolvemap[name];
			//console.log("Statically loaded " +name);//+ ret);
			if (!ret)
				throw "Module not found: " + name;
			return ret;
		}
		else
		{
			resolvemap[name] = url;
		}
	}
	
	/*return function(name, url) {
		if (arguments.length === 1) {
			let ret = syncimport(resolvemap[name]);
			console.log("Dynamically loaded " + ret);
			return ret;
		}
		else
		{
			resolvemap[name] = url;
		}
	}*/
})();