import * as litelement from "./lit/lit-element.js"
import * as baseelement from "../webcomponents/baseelement.js"

console.log("Executing modloader " + Object.keys(litelement));

window.modLoad = (function () {
	let resolvemap = {};
	resolvemap["lit-element"] = litelement;
	resolvemap["base-element"] = baseelement;
	
	document.querySelectorAll("script[data-name]").forEach( (mappedmodule) => {
		console.log("Adding module mapping " +mappedmodule.dataset.name + " to " + mappedmodule.src);
		resolvemap[mappedmodule.dataset.name] = mappedmodule.src;
	});
	
	let syncimport = function (url) {
		let ret = null;
		(async () => {
			console.log("exec async " + url);
			let aret = Promise.resolve(await import(url));
			console.log("exec async ret" + aret);
			return aret;
		})().then( (res) => {
			ret = res;
			console.log("result set:" + res);
		});
		console.log("returning now");
		return ret;
	}
	
	return function(name, url) {
		if (arguments.length === 1) {
			let ret = resolvemap[name];
			console.log("Statically loaded " );//+ ret);
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