import {Jadex} from "./Jadex";
import {ConnectionHandler} from "./websocket/ConnectionHandler";
import {SUtil} from "./SUtil";
import {Scopes} from "./Scopes";
var global:any = window;

// global.jadexclasses = {
//     Jadex,
//     ConnectionHandler,
//     SUtil,
//     Scopes
// };

// These are hacks. Remove and use export line below, pass "--standalone jadex" to browserify afterwards.
global.Scopes = Scopes;
global.jadex = new Jadex();
global.SUtil = SUtil;

export { Jadex, Scopes, SUtil };