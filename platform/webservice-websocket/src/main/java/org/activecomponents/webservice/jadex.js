(function(f){if(typeof exports==="object"&&typeof module!=="undefined"){module.exports=f()}else if(typeof define==="function"&&define.amd){define([],f)}else{var g;if(typeof window!=="undefined"){g=window}else if(typeof global!=="undefined"){g=global}else if(typeof self!=="undefined"){g=self}else{g=this}g.jadexclasses = f()}})(function(){var define,module,exports;return (function e(t,n,r){function s(o,u){if(!n[o]){if(!t[o]){var a=typeof require=="function"&&require;if(!u&&a)return a(o,!0);if(i)return i(o,!0);var f=new Error("Cannot find module '"+o+"'");throw f.code="MODULE_NOT_FOUND",f}var l=n[o]={exports:{}};t[o][0].call(l.exports,function(e){var n=t[o][1][e];return s(n?n:e)},l,l.exports,e,t,n,r)}return n[o].exports}var i=typeof require=="function"&&require;for(var o=0;o<r.length;o++)s(r[o]);return s})({1:[function(require,module,exports){
"use strict";
var Jadex_1 = require("./Jadex");
exports.Jadex = Jadex_1.Jadex;
var SUtil_1 = require("./SUtil");
exports.SUtil = SUtil_1.SUtil;
var Scopes_1 = require("./Scopes");
exports.Scopes = Scopes_1.Scopes;
var global = window;
// global.jadexclasses = {
//     Jadex,
//     ConnectionHandler,
//     SUtil,
//     Scopes
// };
// These are hacks. Remove and use export line below, pass "--standalone jadex" to browserify afterwards.
global.Scopes = Scopes_1.Scopes;
global.jadex = new Jadex_1.Jadex();
global.SUtil = SUtil_1.SUtil;

},{"./Jadex":2,"./SUtil":5,"./Scopes":6}],2:[function(require,module,exports){
"use strict";
var JadexConnectionHandler_1 = require("./websocket/JadexConnectionHandler");
var ServiceSearchMessage_1 = require("./messages/ServiceSearchMessage");
var Scopes_1 = require("./Scopes");
var ServiceProvideMessage_1 = require("./messages/ServiceProvideMessage");
var ServiceUnprovideMessage_1 = require("./messages/ServiceUnprovideMessage");
var JsonParser_1 = require("./JsonParser");
var SUtil_1 = require("./SUtil");
/**
 *  Main class with methods to
 *  - getService(s) and
 *  - provideService
 */
var Jadex = (function () {
    /**
     *  Jadex Javascript API.
     */
    function Jadex() {
    }
    /**
     *  Get a service from a publication point.
     *  @param type The service type.
     *  @param scope The scope.
     *  @param url The url of the websocket.
     */
    Jadex.prototype.getService = function (type, scope, url) {
        var prom = SUtil_1.SUtil.addErrHandler(new Promise(function (resolve, reject) {
            var cmd = new ServiceSearchMessage_1.ServiceSearchMessage(type, false, scope != null ? scope : Scopes_1.Scopes.SCOPE_PLATFORM);
            JadexConnectionHandler_1.JadexConnectionHandler.getInstance().sendMessage(url, cmd, "search", resolve, reject, null);
        }));
        return prom;
    };
    ;
    /**
     *  Get services from a publication point.
     *  @param type The service type.
     *  @param callback The callback function for the intermediate results.
     *  @param scope The search scope.
     *  @param url The url of the websocket.
     */
    Jadex.prototype.getServices = function (type, callback, scope, url) {
        SUtil_1.SUtil.assert(callback instanceof Function && callback != null);
        var prom = SUtil_1.SUtil.addErrHandler(new Promise(function (resolve, reject) {
            var cmd = new ServiceSearchMessage_1.ServiceSearchMessage(type, true, scope != null ? scope : Scopes_1.Scopes.SCOPE_PLATFORM);
            JadexConnectionHandler_1.JadexConnectionHandler.getInstance().sendMessage(url, cmd, "search", resolve, reject, callback);
        }));
        return prom;
    };
    ;
    /**
     *  Provide a new (client) service.
     *  @param type The service type.
     *  @param scope The provision scope.
     *  @param url The url of the websocket.
     */
    Jadex.prototype.provideService = function (type, scope, tags, callback, url) {
        return SUtil_1.SUtil.addErrHandler(new Promise(function (resolve, reject) {
            var cmd = new ServiceProvideMessage_1.ServiceProvideMessage(type, scope != null ? scope : "global", typeof tags === "string" ? [tags] : tags);
            JadexConnectionHandler_1.JadexConnectionHandler.getInstance().sendMessage(url, cmd, "provide", resolve, reject, callback);
        }));
    };
    /**
     *  Unprovide a (client) service.
     *  @param type The service type.
     *  @param scope The provision scope.
     *  @param url The url of the websocket.
     */
    Jadex.prototype.unprovideService = function (sid, url) {
        return SUtil_1.SUtil.addErrHandler(new Promise(function (resolve, reject) {
            var cmd = new ServiceUnprovideMessage_1.ServiceUnprovideMessage(sid);
            JadexConnectionHandler_1.JadexConnectionHandler.getInstance().sendMessage(url, cmd, "unprovide", resolve, reject, null);
        }));
    };
    /**
     *  Register a class for json (de)serialization.
     */
    Jadex.prototype.registerClass = function (clazz) {
        JsonParser_1.JsonParser.registerClass(clazz);
    };
    return Jadex;
}());
exports.Jadex = Jadex;

},{"./JsonParser":4,"./SUtil":5,"./Scopes":6,"./messages/ServiceProvideMessage":11,"./messages/ServiceSearchMessage":12,"./messages/ServiceUnprovideMessage":13,"./websocket/JadexConnectionHandler":15}],3:[function(require,module,exports){
"use strict";
var JadexConnectionHandler_1 = require("./websocket/JadexConnectionHandler");
var JadexPromise = (function () {
    /**
     * Instantiate a promise but do not execute anything now.
     * Resolving is handled from outside via resolve().
     */
    function JadexPromise(url) {
        var _this = this;
        this.url = url;
        this.intermediateResolveCallbacks = [];
        this.promise = new Promise(function (resolve, reject) {
            _this.resolveFunc = resolve;
            _this.rejectFunc = reject;
        });
    }
    // private intermediateRejectCallbacks = [];
    JadexPromise.prototype.then = function (onfulfilled, onrejected) {
        return this.promise.then(onfulfilled, onrejected);
    };
    JadexPromise.prototype.resolve = function (value) {
        // return this.promise.resolve(value);
        return this.resolveFunc(value);
    };
    JadexPromise.prototype.reject = function (error) {
        // return this.promise.reject(error);
        return this.rejectFunc(error);
    };
    JadexPromise.prototype.resolveIntermediate = function (value) {
        for (var _i = 0, _a = this.intermediateResolveCallbacks; _i < _a.length; _i++) {
            var cb = _a[_i];
            cb(value);
        }
    };
    JadexPromise.prototype.thenIntermediate = function (onfulfilled, onrejected) {
        this.intermediateResolveCallbacks.push(onfulfilled);
        // this.intermediateRejectCallbacks.push(onrejected);
    };
    JadexPromise.prototype.terminate = function () {
        var cmd = {
            __classname: "com.actoron.webservice.messages.ServiceTerminateInvocationMessage",
            callid: this.callid
        };
        JadexConnectionHandler_1.JadexConnectionHandler.getInstance().sendConversationMessage(this.url, cmd);
    };
    ;
    JadexPromise.prototype.pull = function () {
        var cmd = {
            __classname: "com.actoron.webservice.messages.PullResultMessage",
            callid: this.callid
        };
        JadexConnectionHandler_1.JadexConnectionHandler.getInstance().sendConversationMessage(this.url, cmd);
    };
    ;
    return JadexPromise;
}());
exports.JadexPromise = JadexPromise;

},{"./websocket/JadexConnectionHandler":15}],4:[function(require,module,exports){
"use strict";
var SUtil_1 = require("./SUtil");
var ServiceProxy_1 = require("./ServiceProxy");
/**
 *  Class that can parse json with additional features.
 *  - handles Jadex references
 */
var JsonParser = (function () {
    function JsonParser() {
    }
    JsonParser.init = function () {
        JsonParser.registerClass2("java.util.Date", { create: function (obj) {
                return new Date(obj.value);
            } });
    };
    /**
     *  Register a class at the parser.
     */
    JsonParser.registerClass = function (clazz) {
        if ("__classname" in clazz) {
            JsonParser.registeredClasses[clazz.__classname] = clazz;
        }
        else {
            var instance = new clazz();
            if ("__classname" in instance) {
                JsonParser.registeredClasses[instance.__classname] = clazz;
            }
            else {
                throw new Error("Cannot register class without __classname static field or member: " + clazz.name);
            }
        }
    };
    /**
     *  Register a class at the parser.
     */
    JsonParser.registerClass2 = function (classname, create) {
        JsonParser.registeredClasses[classname] = create;
    };
    /**
     *  JSOn.parse extension to handle Jadex reference mechanism.
     *  @param str The string of the json object to parse.
     *  @return The parsed object.
     */
    JsonParser.parse = function (str, url) {
        var idmarker = "__id";
        var refmarker = "__ref";
        //		let arraymarker = "__array";
        //		let collectionmarker = "__collection";
        var replacemarker = ["__array", "__collection"];
        var os = {}; // the objects per id
        var refs = []; // the unresolved references
        var obj;
        try {
            obj = JSON.parse(str);
        }
        catch (e) {
            console.error("Could not parse string: " + str);
            throw e;
        }
        var recurse = function (obj, prop, parent) {
            //	    console.log(obj+" "+prop+" "+parent);
            if (!SUtil_1.SUtil.isBasicType(obj)) {
                // test if it is just a placeholder object that must be changed
                //			if(prop!=null)
                //			{
                for (var i = 0; i < replacemarker.length; i++) {
                    if (replacemarker[i] in obj) {
                        obj = obj[replacemarker[i]];
                        break;
                    }
                }
                //		    }
                // instantiate classes
                if ("__classname" in obj) {
                    var className = obj["__classname"];
                    if (className == "jadex.bridge.service.IService") {
                        obj = new ServiceProxy_1.ServiceProxy(obj.serviceIdentifier, recurse(obj.methodNames, "methodNames", obj), url);
                    }
                    else if (className in JsonParser.registeredClasses) {
                        var func = JsonParser.registeredClasses[className];
                        if (func.create) {
                            obj = func.create(obj);
                        }
                        else {
                            // iterate members:
                            var instance = new func();
                            for (var prop_1 in obj) {
                                instance[prop_1] = recurse(obj[prop_1], prop_1, obj);
                            }
                            obj = instance;
                        }
                    }
                }
                else {
                    // recreate arrays
                    if (SUtil_1.SUtil.isArray(obj)) {
                        for (var i = 0; i < obj.length; i++) {
                            if (!SUtil_1.SUtil.isBasicType(obj[i])) {
                                if (refmarker in obj[i]) {
                                    obj[i] = recurse(obj[i], i, obj);
                                }
                                else {
                                    obj[i] = recurse(obj[i], prop, obj);
                                }
                            }
                        }
                    }
                }
                if (refmarker in obj) {
                    var ref = obj[refmarker];
                    if (ref in os) {
                        obj = os[ref];
                    }
                    else {
                        refs.push([parent, prop, ref]); // lazy evaluation necessary
                    }
                }
                else {
                    var id = null;
                    if (idmarker in obj) {
                        id = obj[idmarker];
                        delete obj[idmarker];
                    }
                    if ("$values" in obj) {
                        obj = obj.$values.map(recurse);
                    }
                    else {
                        for (var prop_2 in obj) {
                            obj[prop_2] = recurse(obj[prop_2], prop_2, obj);
                        }
                    }
                    if (id != null) {
                        os[id] = obj;
                    }
                }
                // unwrap boxed values for JS:
                var wrappedType = SUtil_1.SUtil.isWrappedType(obj);
                if (wrappedType) {
                    // console.log("found wrapped: " + isWrappedType(obj) + " for: " + obj.__classname + " with value: " + obj.value)
                    if (wrappedType == "boolean") {
                    }
                    else if (wrappedType == "string") {
                        obj = obj.value;
                    }
                    else {
                        // everything else is a number in JS
                        obj = +obj.value;
                    }
                }
                else if (SUtil_1.SUtil.isEnum(obj)) {
                    // convert enums to strings
                    obj = obj.value;
                }
            }
            return obj;
        };
        obj = recurse(obj, null, null);
        // resolve lazy references
        for (var i = 0; i < refs.length; i++) {
            var ref = refs[i];
            ref[0][ref[1]] = os[ref[2]];
        }
        return obj;
    };
    /** The registered classes. */
    JsonParser.registeredClasses = {};
    return JsonParser;
}());
exports.JsonParser = JsonParser;
JsonParser.init();

},{"./SUtil":5,"./ServiceProxy":7}],5:[function(require,module,exports){
"use strict";
/**
 *  Static helper methods.
 */
var SUtil = (function () {
    function SUtil() {
    }
    /**
     *  Test if an object is a basic type.
     *  @param obj The object.
     *  @return True, if is a basic type.
     */
    SUtil.isBasicType = function (obj) {
        return typeof obj !== 'object' || !obj;
    };
    /**
     *  Test if an object is a java wrapped type.
     *  @param obj The object.
     *  @return False, if is not a wrapped primitive type, else returns the corresponding JS type.
     */
    SUtil.isWrappedType = function (obj) {
        if ("__classname" in obj) {
            var searching = obj.__classname.replace(/\./g, '_');
            return SUtil.wrappedConversionTypes[searching];
        }
        else {
            return false;
        }
    };
    /**
     *  Check of an obj is an enum.
     */
    SUtil.isEnum = function (obj) {
        return ("enum" in obj);
    };
    /**
     *  Test if an object is an array.
     *  @param obj The object.
     *  @return True, if is an array.
     */
    SUtil.isArray = function (obj) {
        return Object.prototype.toString.call(obj) == '[object Array]';
    };
    /**
     *  Compute the approx. size of an object.
     *  @param obj The object.
     */
    SUtil.sizeOf = function (object) {
        var objects = [object];
        var size = 0;
        for (var i = 0; i < objects.length; i++) {
            switch (typeof objects[i]) {
                case 'boolean':
                    size += 4;
                    break;
                case 'number':
                    size += 8;
                    break;
                case 'string':
                    size += 2 * objects[i].length;
                    break;
                case 'object':
                    if (Object.prototype.toString.call(objects[i]) != '[object Array]') {
                        for (var key_1 in objects[i])
                            size += 2 * key_1.length;
                    }
                    var processed = false;
                    var key = void 0;
                    for (key in objects[i]) {
                        for (var search = 0; search < objects.length; search++) {
                            if (objects[search] === objects[i][key]) {
                                processed = true;
                                break;
                            }
                        }
                    }
                    if (!processed)
                        objects.push(objects[i][key]);
            }
        }
        return size;
    };
    /**
     *  Check if object is true by inspecting if it contains a true property.
     */
    SUtil.isTrue = function (obj) {
        return obj == true || (obj != null && obj.hasOwnProperty("value") && obj.value == true);
    };
    /**
     *  Assert that throws an error if not holds.
     */
    SUtil.assert = function (condition, message) {
        if (!condition) {
            message = message || "Assertion failed";
            if (typeof Error !== "undefined") {
                throw new Error(message);
            }
            throw message; // Fallback
        }
    };
    /**
     *  Get the service id as string.
     *  (otherwise it cannot be used as key in a map because
     *  no equals exists).
     */
    SUtil.getServiceIdAsString = function (sid) {
        return sid.serviceName + "@" + sid.providerId;
    };
    /**
     *  Add a console out error handler to the promise.
     */
    SUtil.addErrHandler = function (p) {
        p.oldcatch = p.catch;
        p.hasErrorhandler = false;
        p.catch = function (eh) {
            p.hasErrorHandler = true;
            return p.oldcatch(eh);
        };
        p.oldcatch(function (err) {
            if (!p.hasErrorHandler)
                console.log("Error occurred: " + err);
        });
        p.oldthen = p.then;
        p.then = function (t, e) {
            if (e)
                p.hasErrorHandler = true;
            return p.oldthen(t, e);
        };
        return p;
    };
    /**
     *  Test if a number is a float.
     *  @param n The number to test.
     *  @return True, if is float.
     */
    SUtil.isFloat = function (n) {
        return n === +n && n !== (n | 0);
    };
    /**
     *  Test if a number is an integer.
     *  @param n The number to test.
     *  @return True, if is integer.
     */
    SUtil.isInteger = function (n) {
        return n === +n && n === (n | 0);
    };
    /**
     *  Check if an object is contained in an array.
     *  Uses equal function to check equality of objects.
     *  If not provided uses reference test.
     *  @param object The object to check.
     *  @param objects The array.
     *  @param equals The equals method.
     *  @return True, if is contained.
     */
    SUtil.containsObject = function (object, objects, equals) {
        var ret = false;
        for (var i = 0; i < objects.length && !ret; i++) {
            ret = equals ? equals(object, objects[i]) : object === objects[i];
        }
        return ret;
    };
    /**
     *  Get the index of an object in an array. -1 for not contained.
     *  @param object The object to check.
     *  @param objects The array.
     *  @param equals The equals method.
     *  @return The index or -1.
     */
    SUtil.indexOfObject = function (object, objects, equals) {
        var ret = -1;
        for (var i = 0; i < objects.length; i++) {
            if (equals ? equals(object, objects[i]) : object === objects[i]) {
                ret = i;
                break;
            }
        }
        return ret;
    };
    /**
     *  Remove an object from an array.
     *  @param object The object to remove.
     *  @param objects The array.
     *  @param equals The equals method.
     *  @return True, if was removed.
     */
    SUtil.removeObject = function (object, objects, equals) {
        var ret = SUtil.indexOfObject(object, objects, equals);
        if (ret != -1)
            objects.splice(ret, 1);
        return ret == -1 ? false : true;
    };
    SUtil.wrappedConversionTypes = {
        java_lang_Integer: "number",
        java_lang_Byte: "number",
        java_lang_Short: "number",
        java_lang_Long: "number",
        java_lang_Float: "number",
        java_lang_Double: "number",
        java_lang_Character: "string",
        java_lang_Boolean: "boolean"
    };
    return SUtil;
}());
exports.SUtil = SUtil;

},{}],6:[function(require,module,exports){
"use strict";
var Scopes = (function () {
    function Scopes() {
    }
    //	/** None component scope (nothing will be searched). */
    //	const SCOPE_NONE = "none";
    //
    //	/** Local component scope. */
    //	const SCOPE_LOCAL = "local";
    //
    //	/** Component scope. */
    //	const SCOPE_COMPONENT = "component";
    //
    //	/** Application scope. */
    //	const SCOPE_APPLICATION = "application";
    /** Platform scope. */
    Scopes.SCOPE_PLATFORM = "platform";
    /** Global scope. */
    Scopes.SCOPE_GLOBAL = "global";
    //	/** Parent scope. */
    //	SCOPE_PARENT:string = "parent";
    /** Session scope. */
    Scopes.SCOPE_SESSION = "session";
    return Scopes;
}());
exports.Scopes = Scopes;

},{}],7:[function(require,module,exports){
"use strict";
var JadexPromise_1 = require("./JadexPromise");
var JadexConnectionHandler_1 = require("./websocket/JadexConnectionHandler");
var ServiceInvocationMessage_1 = require("./messages/ServiceInvocationMessage");
var ServiceProxy = (function () {
    /**
     *  Create a service proxy for a Jadex service.
     */
    function ServiceProxy(serviceId, methodNames, url) {
        this.serviceId = serviceId;
        this.url = url;
        // Generic invoke method called on each service invocation
        for (var i = 0; i < methodNames.length; i++) {
            this[methodNames[i]] = this.createMethod(methodNames[i]);
        }
    }
    /**
     *  Generic invoke method that sends a method call to the server side.
     */
    ServiceProxy.prototype.invoke = function (name, params, callback) {
        var ret = new JadexPromise_1.JadexPromise(this.url);
        var conm = JadexConnectionHandler_1.JadexConnectionHandler.getInstance();
        // Convert parameters seperately, one by one
        var cparams = [];
        for (var i = 0; i < params.length; i++) {
            cparams.push(conm.objectToJson(params[i]));
        }
        var cmd = new ServiceInvocationMessage_1.ServiceInvocationMessage(this.serviceId, name, cparams);
        // console.log(cmd);
        // wrap callback to allow JadexPromise.intermediateThen
        var wrapCb = function (intermediateResult) {
            // console.log("calling intermediate result with: " + intermediateResult);
            ret.resolveIntermediate(intermediateResult);
            if (callback) {
                callback(intermediateResult);
            }
        };
        ret.callid = conm.sendMessage(this.url, cmd, "invoke", function (res) { return ret.resolve(res); }, function (ex) { return ret.reject(ex); }, wrapCb);
        return ret;
    };
    /**
     *  Create method function (needed to preserve the name).
     *
     *  Creates an argument array and invokes generic invoke method.
     *
     *  TODO: callback function hack!
     */
    ServiceProxy.prototype.createMethod = function (name) {
        var outer = this;
        return function () {
            var params = [];
            var callback;
            for (var j = 0; j < arguments.length; j++) {
                if (typeof arguments[j] === "function") {
                    callback = arguments[j];
                }
                else {
                    params.push(arguments[j]);
                }
            }
            return outer.invoke(name, params, callback);
        };
    };
    return ServiceProxy;
}());
exports.ServiceProxy = ServiceProxy;

},{"./JadexPromise":3,"./messages/ServiceInvocationMessage":10,"./websocket/JadexConnectionHandler":15}],8:[function(require,module,exports){
"use strict";
var ServiceMessage = (function () {
    function ServiceMessage(__classname) {
        this.__classname = __classname;
    }
    return ServiceMessage;
}());
exports.ServiceMessage = ServiceMessage;

},{}],9:[function(require,module,exports){
"use strict";
var __extends = (this && this.__extends) || function (d, b) {
    for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p];
    function __() { this.constructor = d; }
    d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
};
var BaseMessage_1 = require("./BaseMessage");
var PartialMessage = (function (_super) {
    __extends(PartialMessage, _super);
    function PartialMessage(callid, data, number, count) {
        _super.call(this, "com.actoron.webservice.messages.PartialMessage");
        this.callid = callid;
        this.data = data;
        this.number = number;
        this.count = count;
    }
    return PartialMessage;
}(BaseMessage_1.ServiceMessage));
exports.PartialMessage = PartialMessage;

},{"./BaseMessage":8}],10:[function(require,module,exports){
"use strict";
var __extends = (this && this.__extends) || function (d, b) {
    for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p];
    function __() { this.constructor = d; }
    d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
};
var BaseMessage_1 = require("./BaseMessage");
var ServiceInvocationMessage = (function (_super) {
    __extends(ServiceInvocationMessage, _super);
    function ServiceInvocationMessage(serviceId, methodName, parameterValues) {
        _super.call(this, "com.actoron.webservice.messages.ServiceInvocationMessage");
        this.serviceId = serviceId;
        this.methodName = methodName;
        this.parameterValues = parameterValues;
    }
    return ServiceInvocationMessage;
}(BaseMessage_1.ServiceMessage));
exports.ServiceInvocationMessage = ServiceInvocationMessage;

},{"./BaseMessage":8}],11:[function(require,module,exports){
"use strict";
var __extends = (this && this.__extends) || function (d, b) {
    for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p];
    function __() { this.constructor = d; }
    d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
};
var BaseMessage_1 = require("./BaseMessage");
var ServiceProvideMessage = (function (_super) {
    __extends(ServiceProvideMessage, _super);
    function ServiceProvideMessage(type, scope, tags) {
        _super.call(this, "com.actoron.webservice.messages.ServiceProvideMessage");
        this.type = type;
        this.scope = scope;
        this.tags = tags;
    }
    return ServiceProvideMessage;
}(BaseMessage_1.ServiceMessage));
exports.ServiceProvideMessage = ServiceProvideMessage;

},{"./BaseMessage":8}],12:[function(require,module,exports){
"use strict";
var __extends = (this && this.__extends) || function (d, b) {
    for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p];
    function __() { this.constructor = d; }
    d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
};
var BaseMessage_1 = require("./BaseMessage");
var ServiceSearchMessage = (function (_super) {
    __extends(ServiceSearchMessage, _super);
    function ServiceSearchMessage(type, multiple, scope) {
        _super.call(this, "com.actoron.webservice.messages.ServiceSearchMessage");
        this.type = type;
        this.multiple = multiple;
        this.scope = scope;
    }
    return ServiceSearchMessage;
}(BaseMessage_1.ServiceMessage));
exports.ServiceSearchMessage = ServiceSearchMessage;

},{"./BaseMessage":8}],13:[function(require,module,exports){
"use strict";
var __extends = (this && this.__extends) || function (d, b) {
    for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p];
    function __() { this.constructor = d; }
    d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
};
var BaseMessage_1 = require("./BaseMessage");
var ServiceUnprovideMessage = (function (_super) {
    __extends(ServiceUnprovideMessage, _super);
    function ServiceUnprovideMessage(serviceId) {
        _super.call(this, "com.actoron.webservice.messages.ServiceUnprovideMessage");
        this.serviceId = serviceId;
    }
    return ServiceUnprovideMessage;
}(BaseMessage_1.ServiceMessage));
exports.ServiceUnprovideMessage = ServiceUnprovideMessage;

},{"./BaseMessage":8}],14:[function(require,module,exports){
"use strict";
var ConnectionHandler = (function () {
    function ConnectionHandler() {
        /** The websocket connections. */
        this.connections = [];
        var scripts = document.getElementsByTagName('script');
        var script = scripts[scripts.length - 1];
        if (script["src"]) {
            this.baseurl = script["src"];
            this.baseurl = "ws" + this.baseurl.substring(this.baseurl.indexOf("://"));
        }
        else if (script.hasAttributes()) {
            //this.baseurl = "ws://" + window.location.hostname + ":" + window.location.port + "/wswebapi";
            this.baseurl = "ws://" + window.location.hostname + ":" + window.location.port + script.attributes.getNamedItem("src").value;
        }
        else {
            // fail?
            throw new Error("Could not find websocket url");
        }
        this.baseurl = this.baseurl.substring(0, this.baseurl.lastIndexOf("jadex.js") - 1);
        this.connections[""] = this.addConnection(this.baseurl);
        //this.connections[undefined] = this.connections[null];
    }
    /**
     *  Internal function to get a web socket for a url.
     */
    ConnectionHandler.prototype.getConnection = function (url) {
        if (url == null)
            url = "";
        var ret = this.connections[url];
        if (ret != null) {
            return ret;
        }
        else {
            return this.addConnection(url);
        }
    };
    ;
    /**
     *  Add a new server connection.
     *  @param url The url.
     */
    ConnectionHandler.prototype.addConnection = function (url) {
        var _this = this;
        this.connections[url] = new Promise(function (resolve, reject) {
            try {
                var ws_1 = new WebSocket(url);
                ws_1.onopen = function () {
                    resolve(ws_1);
                };
                ws_1.onmessage = function (message) {
                    _this.onMessage(message, url);
                };
            }
            catch (e) {
                reject(e);
            }
        });
        return this.connections[url];
    };
    ;
    /**
     *  Send a message to the server and create a callid for the answer message.
     */
    ConnectionHandler.prototype.sendData = function (url, data) {
        this.getConnection(url).then(function (ws) {
            ws.send(data);
        });
    };
    /**
     *  Send a message to the server in an ongoing conversation.
     */
    ConnectionHandler.prototype.sendConversationMessage = function (url, cmd) {
        this.getConnection(url).then(function (ws) {
            ws.send(JSON.stringify(cmd));
        });
    };
    ;
    return ConnectionHandler;
}());
exports.ConnectionHandler = ConnectionHandler;

},{}],15:[function(require,module,exports){
"use strict";
var __extends = (this && this.__extends) || function (d, b) {
    for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p];
    function __() { this.constructor = d; }
    d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
};
var ConnectionHandler_1 = require("./ConnectionHandler");
var JsonParser_1 = require("../JsonParser");
var SUtil_1 = require("../SUtil");
var WebsocketCall_1 = require("./WebsocketCall");
var PartialMessage_1 = require("../messages/PartialMessage");
/**
 *
 */
var JadexConnectionHandler = (function (_super) {
    __extends(JadexConnectionHandler, _super);
    function JadexConnectionHandler() {
        _super.apply(this, arguments);
        /** The map of open outcalls. */
        this.outcalls = [];
        //	/** The map of open incoming calls. */
        //	var incalls = [];
        /** The map of provided services (sid -> service invocation function). */
        this.providedServices = [];
    }
    /**
     *  Get the instance.
     */
    JadexConnectionHandler.getInstance = function () {
        return JadexConnectionHandler.INSTANCE;
    };
    /**
     *  Send a message to the server and create a callid for the answer message.
     */
    JadexConnectionHandler.prototype.sendMessage = function (url, cmd, type, resolve, reject, callback) {
        // todo: use Jadex binary to serialize message and send
        var callid = this.randomString(-1);
        this.outcalls[callid] = new WebsocketCall_1.WebsocketCall(type, resolve, reject, callback);
        cmd.callid = callid;
        this.sendRawMessage(url, cmd);
        return callid;
    };
    ;
    /**
     *  Send a raw message without callid management.
     */
    JadexConnectionHandler.prototype.sendRawMessage = function (url, cmd) {
        if (!cmd.callid)
            console.log("Sending message without callid: " + cmd);
        var data = this.objectToJson(cmd);
        //console.log(data);
        //let size = sizeOf(cmd);
        var size = data.length;
        var limit = 7000; // 8192
        // If message is larger than limit slice the message via partial messages
        if (size > limit) {
            var cnt = Math.ceil(size / limit);
            for (var i = 0; i < cnt; i++) {
                var part = data.substring(i * limit, (i + 1) * limit);
                var pcmd = new PartialMessage_1.PartialMessage(cmd.callid, part, i, cnt);
                var pdata = JSON.stringify(pcmd);
                //console.log("sending part, size: "+pdata.length);
                this.sendData(url, pdata);
            }
        }
        else {
            this.sendData(url, data);
        }
    };
    /**
     *  Convert an object to json.
     *  Similar to JSON.stringify but can handle
     *  binary objects as base 64 strings.
     *  @param object The object.
     *  @return The json string.
     */
    JadexConnectionHandler.prototype.objectToJson = function (object) {
        var replacer = function (key, value) {
            if (value instanceof ArrayBuffer) {
                //let ret = window.btoa(value);
                var ret = btoa(String.fromCharCode.apply(null, new Uint8Array(value)));
                return ret;
            }
            else {
                return value;
            }
            //return value instanceof ArrayBuffer? window.btoa(value): value;
        };
        return JSON.stringify(object, replacer);
    };
    /**
     *  Send a result.
     */
    JadexConnectionHandler.prototype.sendResult = function (url, result, finished, callid) {
        var cmd = {
            __classname: "com.actoron.webservice.messages.ResultMessage",
            callid: callid,
            result: result,
            finished: finished
        };
        this.sendRawMessage(url, cmd);
    };
    /**
     *  Send an exception.
     */
    JadexConnectionHandler.prototype.sendException = function (url, err, finished, callid) {
        var exception = {
            __classname: "java.lang.RuntimeException",
            message: "" + err
        };
        var cmd = {
            __classname: "com.actoron.webservice.messages.ResultMessage",
            callid: callid,
            exception: exception,
            finished: finished
        };
        this.sendRawMessage(url, cmd);
    };
    /**
     *  Called when a message arrives.
     */
    JadexConnectionHandler.prototype.onMessage = function (message, url) {
        var _this = this;
        if (message.type == "message") {
            var msg_1 = JsonParser_1.JsonParser.parse(message.data, url);
            var outCall = this.outcalls[msg_1.callid];
            //		    console.log("outcalls: "+outcalls);
            if (outCall != null) {
                if (SUtil_1.SUtil.isTrue(msg_1.finished)) {
                    delete this.outcalls[msg_1.callid];
                    //					console.log("outCall deleted: "+msg.callid);
                    outCall.finished = true;
                }
                if (outCall.type == "search") {
                    if (msg_1.result != null) {
                        if (msg_1.result.hasOwnProperty("__array"))
                            msg_1.result = msg_1.result.__array;
                        if (msg_1.result.hasOwnProperty("__collection"))
                            msg_1.result[1] = msg_1.result[1].__collection;
                    }
                    var serproxy = void 0;
                    if (msg_1.exception == null && msg_1.result != null) {
                        serproxy = msg_1.result; //createServiceProxy(msg.result[0], msg.result[1]);
                    }
                    outCall.resume(serproxy, msg_1.exception);
                }
                else if (outCall.type == "invoke") {
                    outCall.resume(msg_1.result, msg_1.exception);
                }
                else if (outCall.type == "provide") {
                    if (msg_1.exception != null) {
                        outCall.reject(msg_1.exception);
                    }
                    else {
                        // Save the service functionality in the inca
                        this.providedServices[SUtil_1.SUtil.getServiceIdAsString(msg_1.result)] = outCall.cb;
                        outCall.resolve(msg_1.result);
                    }
                }
                else if (outCall.type == "unprovide") {
                    if (msg_1.exception != null) {
                        outCall.reject(msg_1.exception);
                    }
                    else {
                        // removeProperty?!
                        this.providedServices[SUtil_1.SUtil.getServiceIdAsString(msg_1.result)] = null;
                        outCall.resolve(msg_1.result);
                    }
                }
            }
            else {
                if (msg_1.__classname === "com.actoron.webservice.messages.ServiceInvocationMessage") {
                    var service = this.providedServices[SUtil_1.SUtil.getServiceIdAsString(msg_1.serviceId)];
                    if (service) {
                        var res;
                        // If it a service object with functions or just a function
                        if (service[msg_1.methodName]) {
                            //res = service[msg.methodName](msg.parameterValues);
                            res = service[msg_1.methodName].apply(undefined, msg_1.parameterValues);
                        }
                        else if (typeof res === "function") {
                            //res = service(msg.parameterValues);
                            res = service.apply(undefined, msg_1.parameterValues);
                        }
                        else if (service.invoke) {
                            res = service.invoke(msg_1.methodName, msg_1.parameterValues);
                        }
                        else {
                            console.log("Cannot invoke service method (not found): " + msg_1.methodName);
                        }
                        // Hack, seems to loose this in callback :-( 
                        //						var fthis = this;
                        // Make anything that comes back to a promise
                        //                        Promise.resolve(res).then(function(res)
                        //                        {
                        //                            fthis.sendResult(url, res, true, msg.callid);
                        //                        })
                        //                        .catch(function(e)
                        //                        {
                        //                            fthis.sendException(url, e, true, msg.callid);
                        //                        });
                        Promise.resolve(res).then(function (res) {
                            _this.sendResult(url, res, true, msg_1.callid);
                        })
                            .catch(function (e) {
                            _this.sendException(url, e, true, msg_1.callid);
                        });
                    }
                    else {
                        console.log("Provided service not found: " + [msg_1.serviceId]);
                        this.sendException(url, "Provided service not found: " + [msg_1.serviceId], true, msg_1.callid);
                    }
                }
                else {
                    console.log("Received message without request: " + msg_1);
                }
            }
        }
        else if (message.type == "binary") {
            console.log("Binary messages currently not supported");
        }
        // else: do not handle pong messages
    };
    /**
     *  Create a random string.
     *  @param length The length of the string.
     *  @returns The random string.
     */
    JadexConnectionHandler.prototype.randomString = function (length) {
        if (length < 1)
            length = 10;
        return Math.round((Math.pow(36, length + 1) - Math.random() * Math.pow(36, length))).toString(36).slice(1);
    };
    ;
    JadexConnectionHandler.INSTANCE = new JadexConnectionHandler();
    return JadexConnectionHandler;
}(ConnectionHandler_1.ConnectionHandler));
exports.JadexConnectionHandler = JadexConnectionHandler;

},{"../JsonParser":4,"../SUtil":5,"../messages/PartialMessage":9,"./ConnectionHandler":14,"./WebsocketCall":16}],16:[function(require,module,exports){
"use strict";
var SUtil_1 = require("../SUtil");
var WebsocketCall = (function () {
    function WebsocketCall(type, resolve, reject, cb) {
        this.type = type;
        this.resolve = resolve;
        this.reject = reject;
        this.cb = cb;
        this.finished = false;
    }
    /**
     *  Resume the listeners of promise.
     */
    WebsocketCall.prototype.resume = function (result, exception) {
        if (this.cb != null && (exception === null || exception === undefined) && !SUtil_1.SUtil.isTrue(this.finished)) {
            this.cb(result);
        }
        else if (SUtil_1.SUtil.isTrue(this.finished)) {
            exception == null ? this.resolve(result) : this.reject(exception);
        }
    };
    return WebsocketCall;
}());
exports.WebsocketCall = WebsocketCall;

},{"../SUtil":5}]},{},[1])(1)
});
//# sourceMappingURL=data:application/json;charset=utf-8;base64,eyJ2ZXJzaW9uIjozLCJzb3VyY2VzIjpbIm5vZGVfbW9kdWxlcy9icm93c2VyaWZ5L25vZGVfbW9kdWxlcy9icm93c2VyLXBhY2svX3ByZWx1ZGUuanMiLCJidWlsZC90cy9HbG9iYWwuanMiLCJidWlsZC90cy9KYWRleC5qcyIsImJ1aWxkL3RzL0phZGV4UHJvbWlzZS5qcyIsImJ1aWxkL3RzL0pzb25QYXJzZXIuanMiLCJidWlsZC90cy9TVXRpbC5qcyIsImJ1aWxkL3RzL1Njb3Blcy5qcyIsImJ1aWxkL3RzL1NlcnZpY2VQcm94eS5qcyIsImJ1aWxkL3RzL21lc3NhZ2VzL0Jhc2VNZXNzYWdlLmpzIiwiYnVpbGQvdHMvbWVzc2FnZXMvUGFydGlhbE1lc3NhZ2UuanMiLCJidWlsZC90cy9tZXNzYWdlcy9TZXJ2aWNlSW52b2NhdGlvbk1lc3NhZ2UuanMiLCJidWlsZC90cy9tZXNzYWdlcy9TZXJ2aWNlUHJvdmlkZU1lc3NhZ2UuanMiLCJidWlsZC90cy9tZXNzYWdlcy9TZXJ2aWNlU2VhcmNoTWVzc2FnZS5qcyIsImJ1aWxkL3RzL21lc3NhZ2VzL1NlcnZpY2VVbnByb3ZpZGVNZXNzYWdlLmpzIiwiYnVpbGQvdHMvd2Vic29ja2V0L0Nvbm5lY3Rpb25IYW5kbGVyLmpzIiwiYnVpbGQvdHMvd2Vic29ja2V0L0phZGV4Q29ubmVjdGlvbkhhbmRsZXIuanMiLCJidWlsZC90cy93ZWJzb2NrZXQvV2Vic29ja2V0Q2FsbC5qcyJdLCJuYW1lcyI6W10sIm1hcHBpbmdzIjoiQUFBQTtBQ0FBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBOztBQ2xCQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBOztBQ2xGQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTs7QUN6REE7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBOztBQzFLQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBOztBQzdNQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7O0FDMUJBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBOztBQ2xFQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7O0FDUkE7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTs7QUNuQkE7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7O0FDbEJBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBOztBQ2xCQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTs7QUNsQkE7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTs7QUNoQkE7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7O0FDakZBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7O0FDOU9BO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBIiwiZmlsZSI6ImdlbmVyYXRlZC5qcyIsInNvdXJjZVJvb3QiOiIiLCJzb3VyY2VzQ29udGVudCI6WyIoZnVuY3Rpb24gZSh0LG4scil7ZnVuY3Rpb24gcyhvLHUpe2lmKCFuW29dKXtpZighdFtvXSl7dmFyIGE9dHlwZW9mIHJlcXVpcmU9PVwiZnVuY3Rpb25cIiYmcmVxdWlyZTtpZighdSYmYSlyZXR1cm4gYShvLCEwKTtpZihpKXJldHVybiBpKG8sITApO3ZhciBmPW5ldyBFcnJvcihcIkNhbm5vdCBmaW5kIG1vZHVsZSAnXCIrbytcIidcIik7dGhyb3cgZi5jb2RlPVwiTU9EVUxFX05PVF9GT1VORFwiLGZ9dmFyIGw9bltvXT17ZXhwb3J0czp7fX07dFtvXVswXS5jYWxsKGwuZXhwb3J0cyxmdW5jdGlvbihlKXt2YXIgbj10W29dWzFdW2VdO3JldHVybiBzKG4/bjplKX0sbCxsLmV4cG9ydHMsZSx0LG4scil9cmV0dXJuIG5bb10uZXhwb3J0c312YXIgaT10eXBlb2YgcmVxdWlyZT09XCJmdW5jdGlvblwiJiZyZXF1aXJlO2Zvcih2YXIgbz0wO288ci5sZW5ndGg7bysrKXMocltvXSk7cmV0dXJuIHN9KSIsIlwidXNlIHN0cmljdFwiO1xyXG52YXIgSmFkZXhfMSA9IHJlcXVpcmUoXCIuL0phZGV4XCIpO1xyXG5leHBvcnRzLkphZGV4ID0gSmFkZXhfMS5KYWRleDtcclxudmFyIFNVdGlsXzEgPSByZXF1aXJlKFwiLi9TVXRpbFwiKTtcclxuZXhwb3J0cy5TVXRpbCA9IFNVdGlsXzEuU1V0aWw7XHJcbnZhciBTY29wZXNfMSA9IHJlcXVpcmUoXCIuL1Njb3Blc1wiKTtcclxuZXhwb3J0cy5TY29wZXMgPSBTY29wZXNfMS5TY29wZXM7XHJcbnZhciBnbG9iYWwgPSB3aW5kb3c7XHJcbi8vIGdsb2JhbC5qYWRleGNsYXNzZXMgPSB7XHJcbi8vICAgICBKYWRleCxcclxuLy8gICAgIENvbm5lY3Rpb25IYW5kbGVyLFxyXG4vLyAgICAgU1V0aWwsXHJcbi8vICAgICBTY29wZXNcclxuLy8gfTtcclxuLy8gVGhlc2UgYXJlIGhhY2tzLiBSZW1vdmUgYW5kIHVzZSBleHBvcnQgbGluZSBiZWxvdywgcGFzcyBcIi0tc3RhbmRhbG9uZSBqYWRleFwiIHRvIGJyb3dzZXJpZnkgYWZ0ZXJ3YXJkcy5cclxuZ2xvYmFsLlNjb3BlcyA9IFNjb3Blc18xLlNjb3BlcztcclxuZ2xvYmFsLmphZGV4ID0gbmV3IEphZGV4XzEuSmFkZXgoKTtcclxuZ2xvYmFsLlNVdGlsID0gU1V0aWxfMS5TVXRpbDtcclxuLy8jIHNvdXJjZU1hcHBpbmdVUkw9R2xvYmFsLmpzLm1hcCIsIlwidXNlIHN0cmljdFwiO1xyXG52YXIgSmFkZXhDb25uZWN0aW9uSGFuZGxlcl8xID0gcmVxdWlyZShcIi4vd2Vic29ja2V0L0phZGV4Q29ubmVjdGlvbkhhbmRsZXJcIik7XHJcbnZhciBTZXJ2aWNlU2VhcmNoTWVzc2FnZV8xID0gcmVxdWlyZShcIi4vbWVzc2FnZXMvU2VydmljZVNlYXJjaE1lc3NhZ2VcIik7XHJcbnZhciBTY29wZXNfMSA9IHJlcXVpcmUoXCIuL1Njb3Blc1wiKTtcclxudmFyIFNlcnZpY2VQcm92aWRlTWVzc2FnZV8xID0gcmVxdWlyZShcIi4vbWVzc2FnZXMvU2VydmljZVByb3ZpZGVNZXNzYWdlXCIpO1xyXG52YXIgU2VydmljZVVucHJvdmlkZU1lc3NhZ2VfMSA9IHJlcXVpcmUoXCIuL21lc3NhZ2VzL1NlcnZpY2VVbnByb3ZpZGVNZXNzYWdlXCIpO1xyXG52YXIgSnNvblBhcnNlcl8xID0gcmVxdWlyZShcIi4vSnNvblBhcnNlclwiKTtcclxudmFyIFNVdGlsXzEgPSByZXF1aXJlKFwiLi9TVXRpbFwiKTtcclxuLyoqXHJcbiAqICBNYWluIGNsYXNzIHdpdGggbWV0aG9kcyB0b1xyXG4gKiAgLSBnZXRTZXJ2aWNlKHMpIGFuZFxyXG4gKiAgLSBwcm92aWRlU2VydmljZVxyXG4gKi9cclxudmFyIEphZGV4ID0gKGZ1bmN0aW9uICgpIHtcclxuICAgIC8qKlxyXG4gICAgICogIEphZGV4IEphdmFzY3JpcHQgQVBJLlxyXG4gICAgICovXHJcbiAgICBmdW5jdGlvbiBKYWRleCgpIHtcclxuICAgIH1cclxuICAgIC8qKlxyXG4gICAgICogIEdldCBhIHNlcnZpY2UgZnJvbSBhIHB1YmxpY2F0aW9uIHBvaW50LlxyXG4gICAgICogIEBwYXJhbSB0eXBlIFRoZSBzZXJ2aWNlIHR5cGUuXHJcbiAgICAgKiAgQHBhcmFtIHNjb3BlIFRoZSBzY29wZS5cclxuICAgICAqICBAcGFyYW0gdXJsIFRoZSB1cmwgb2YgdGhlIHdlYnNvY2tldC5cclxuICAgICAqL1xyXG4gICAgSmFkZXgucHJvdG90eXBlLmdldFNlcnZpY2UgPSBmdW5jdGlvbiAodHlwZSwgc2NvcGUsIHVybCkge1xyXG4gICAgICAgIHZhciBwcm9tID0gU1V0aWxfMS5TVXRpbC5hZGRFcnJIYW5kbGVyKG5ldyBQcm9taXNlKGZ1bmN0aW9uIChyZXNvbHZlLCByZWplY3QpIHtcclxuICAgICAgICAgICAgdmFyIGNtZCA9IG5ldyBTZXJ2aWNlU2VhcmNoTWVzc2FnZV8xLlNlcnZpY2VTZWFyY2hNZXNzYWdlKHR5cGUsIGZhbHNlLCBzY29wZSAhPSBudWxsID8gc2NvcGUgOiBTY29wZXNfMS5TY29wZXMuU0NPUEVfUExBVEZPUk0pO1xyXG4gICAgICAgICAgICBKYWRleENvbm5lY3Rpb25IYW5kbGVyXzEuSmFkZXhDb25uZWN0aW9uSGFuZGxlci5nZXRJbnN0YW5jZSgpLnNlbmRNZXNzYWdlKHVybCwgY21kLCBcInNlYXJjaFwiLCByZXNvbHZlLCByZWplY3QsIG51bGwpO1xyXG4gICAgICAgIH0pKTtcclxuICAgICAgICByZXR1cm4gcHJvbTtcclxuICAgIH07XHJcbiAgICA7XHJcbiAgICAvKipcclxuICAgICAqICBHZXQgc2VydmljZXMgZnJvbSBhIHB1YmxpY2F0aW9uIHBvaW50LlxyXG4gICAgICogIEBwYXJhbSB0eXBlIFRoZSBzZXJ2aWNlIHR5cGUuXHJcbiAgICAgKiAgQHBhcmFtIGNhbGxiYWNrIFRoZSBjYWxsYmFjayBmdW5jdGlvbiBmb3IgdGhlIGludGVybWVkaWF0ZSByZXN1bHRzLlxyXG4gICAgICogIEBwYXJhbSBzY29wZSBUaGUgc2VhcmNoIHNjb3BlLlxyXG4gICAgICogIEBwYXJhbSB1cmwgVGhlIHVybCBvZiB0aGUgd2Vic29ja2V0LlxyXG4gICAgICovXHJcbiAgICBKYWRleC5wcm90b3R5cGUuZ2V0U2VydmljZXMgPSBmdW5jdGlvbiAodHlwZSwgY2FsbGJhY2ssIHNjb3BlLCB1cmwpIHtcclxuICAgICAgICBTVXRpbF8xLlNVdGlsLmFzc2VydChjYWxsYmFjayBpbnN0YW5jZW9mIEZ1bmN0aW9uICYmIGNhbGxiYWNrICE9IG51bGwpO1xyXG4gICAgICAgIHZhciBwcm9tID0gU1V0aWxfMS5TVXRpbC5hZGRFcnJIYW5kbGVyKG5ldyBQcm9taXNlKGZ1bmN0aW9uIChyZXNvbHZlLCByZWplY3QpIHtcclxuICAgICAgICAgICAgdmFyIGNtZCA9IG5ldyBTZXJ2aWNlU2VhcmNoTWVzc2FnZV8xLlNlcnZpY2VTZWFyY2hNZXNzYWdlKHR5cGUsIHRydWUsIHNjb3BlICE9IG51bGwgPyBzY29wZSA6IFNjb3Blc18xLlNjb3Blcy5TQ09QRV9QTEFURk9STSk7XHJcbiAgICAgICAgICAgIEphZGV4Q29ubmVjdGlvbkhhbmRsZXJfMS5KYWRleENvbm5lY3Rpb25IYW5kbGVyLmdldEluc3RhbmNlKCkuc2VuZE1lc3NhZ2UodXJsLCBjbWQsIFwic2VhcmNoXCIsIHJlc29sdmUsIHJlamVjdCwgY2FsbGJhY2spO1xyXG4gICAgICAgIH0pKTtcclxuICAgICAgICByZXR1cm4gcHJvbTtcclxuICAgIH07XHJcbiAgICA7XHJcbiAgICAvKipcclxuICAgICAqICBQcm92aWRlIGEgbmV3IChjbGllbnQpIHNlcnZpY2UuXHJcbiAgICAgKiAgQHBhcmFtIHR5cGUgVGhlIHNlcnZpY2UgdHlwZS5cclxuICAgICAqICBAcGFyYW0gc2NvcGUgVGhlIHByb3Zpc2lvbiBzY29wZS5cclxuICAgICAqICBAcGFyYW0gdXJsIFRoZSB1cmwgb2YgdGhlIHdlYnNvY2tldC5cclxuICAgICAqL1xyXG4gICAgSmFkZXgucHJvdG90eXBlLnByb3ZpZGVTZXJ2aWNlID0gZnVuY3Rpb24gKHR5cGUsIHNjb3BlLCB0YWdzLCBjYWxsYmFjaywgdXJsKSB7XHJcbiAgICAgICAgcmV0dXJuIFNVdGlsXzEuU1V0aWwuYWRkRXJySGFuZGxlcihuZXcgUHJvbWlzZShmdW5jdGlvbiAocmVzb2x2ZSwgcmVqZWN0KSB7XHJcbiAgICAgICAgICAgIHZhciBjbWQgPSBuZXcgU2VydmljZVByb3ZpZGVNZXNzYWdlXzEuU2VydmljZVByb3ZpZGVNZXNzYWdlKHR5cGUsIHNjb3BlICE9IG51bGwgPyBzY29wZSA6IFwiZ2xvYmFsXCIsIHR5cGVvZiB0YWdzID09PSBcInN0cmluZ1wiID8gW3RhZ3NdIDogdGFncyk7XHJcbiAgICAgICAgICAgIEphZGV4Q29ubmVjdGlvbkhhbmRsZXJfMS5KYWRleENvbm5lY3Rpb25IYW5kbGVyLmdldEluc3RhbmNlKCkuc2VuZE1lc3NhZ2UodXJsLCBjbWQsIFwicHJvdmlkZVwiLCByZXNvbHZlLCByZWplY3QsIGNhbGxiYWNrKTtcclxuICAgICAgICB9KSk7XHJcbiAgICB9O1xyXG4gICAgLyoqXHJcbiAgICAgKiAgVW5wcm92aWRlIGEgKGNsaWVudCkgc2VydmljZS5cclxuICAgICAqICBAcGFyYW0gdHlwZSBUaGUgc2VydmljZSB0eXBlLlxyXG4gICAgICogIEBwYXJhbSBzY29wZSBUaGUgcHJvdmlzaW9uIHNjb3BlLlxyXG4gICAgICogIEBwYXJhbSB1cmwgVGhlIHVybCBvZiB0aGUgd2Vic29ja2V0LlxyXG4gICAgICovXHJcbiAgICBKYWRleC5wcm90b3R5cGUudW5wcm92aWRlU2VydmljZSA9IGZ1bmN0aW9uIChzaWQsIHVybCkge1xyXG4gICAgICAgIHJldHVybiBTVXRpbF8xLlNVdGlsLmFkZEVyckhhbmRsZXIobmV3IFByb21pc2UoZnVuY3Rpb24gKHJlc29sdmUsIHJlamVjdCkge1xyXG4gICAgICAgICAgICB2YXIgY21kID0gbmV3IFNlcnZpY2VVbnByb3ZpZGVNZXNzYWdlXzEuU2VydmljZVVucHJvdmlkZU1lc3NhZ2Uoc2lkKTtcclxuICAgICAgICAgICAgSmFkZXhDb25uZWN0aW9uSGFuZGxlcl8xLkphZGV4Q29ubmVjdGlvbkhhbmRsZXIuZ2V0SW5zdGFuY2UoKS5zZW5kTWVzc2FnZSh1cmwsIGNtZCwgXCJ1bnByb3ZpZGVcIiwgcmVzb2x2ZSwgcmVqZWN0LCBudWxsKTtcclxuICAgICAgICB9KSk7XHJcbiAgICB9O1xyXG4gICAgLyoqXHJcbiAgICAgKiAgUmVnaXN0ZXIgYSBjbGFzcyBmb3IganNvbiAoZGUpc2VyaWFsaXphdGlvbi5cclxuICAgICAqL1xyXG4gICAgSmFkZXgucHJvdG90eXBlLnJlZ2lzdGVyQ2xhc3MgPSBmdW5jdGlvbiAoY2xhenopIHtcclxuICAgICAgICBKc29uUGFyc2VyXzEuSnNvblBhcnNlci5yZWdpc3RlckNsYXNzKGNsYXp6KTtcclxuICAgIH07XHJcbiAgICByZXR1cm4gSmFkZXg7XHJcbn0oKSk7XHJcbmV4cG9ydHMuSmFkZXggPSBKYWRleDtcclxuLy8jIHNvdXJjZU1hcHBpbmdVUkw9SmFkZXguanMubWFwIiwiXCJ1c2Ugc3RyaWN0XCI7XHJcbnZhciBKYWRleENvbm5lY3Rpb25IYW5kbGVyXzEgPSByZXF1aXJlKFwiLi93ZWJzb2NrZXQvSmFkZXhDb25uZWN0aW9uSGFuZGxlclwiKTtcclxudmFyIEphZGV4UHJvbWlzZSA9IChmdW5jdGlvbiAoKSB7XHJcbiAgICAvKipcclxuICAgICAqIEluc3RhbnRpYXRlIGEgcHJvbWlzZSBidXQgZG8gbm90IGV4ZWN1dGUgYW55dGhpbmcgbm93LlxyXG4gICAgICogUmVzb2x2aW5nIGlzIGhhbmRsZWQgZnJvbSBvdXRzaWRlIHZpYSByZXNvbHZlKCkuXHJcbiAgICAgKi9cclxuICAgIGZ1bmN0aW9uIEphZGV4UHJvbWlzZSh1cmwpIHtcclxuICAgICAgICB2YXIgX3RoaXMgPSB0aGlzO1xyXG4gICAgICAgIHRoaXMudXJsID0gdXJsO1xyXG4gICAgICAgIHRoaXMuaW50ZXJtZWRpYXRlUmVzb2x2ZUNhbGxiYWNrcyA9IFtdO1xyXG4gICAgICAgIHRoaXMucHJvbWlzZSA9IG5ldyBQcm9taXNlKGZ1bmN0aW9uIChyZXNvbHZlLCByZWplY3QpIHtcclxuICAgICAgICAgICAgX3RoaXMucmVzb2x2ZUZ1bmMgPSByZXNvbHZlO1xyXG4gICAgICAgICAgICBfdGhpcy5yZWplY3RGdW5jID0gcmVqZWN0O1xyXG4gICAgICAgIH0pO1xyXG4gICAgfVxyXG4gICAgLy8gcHJpdmF0ZSBpbnRlcm1lZGlhdGVSZWplY3RDYWxsYmFja3MgPSBbXTtcclxuICAgIEphZGV4UHJvbWlzZS5wcm90b3R5cGUudGhlbiA9IGZ1bmN0aW9uIChvbmZ1bGZpbGxlZCwgb25yZWplY3RlZCkge1xyXG4gICAgICAgIHJldHVybiB0aGlzLnByb21pc2UudGhlbihvbmZ1bGZpbGxlZCwgb25yZWplY3RlZCk7XHJcbiAgICB9O1xyXG4gICAgSmFkZXhQcm9taXNlLnByb3RvdHlwZS5yZXNvbHZlID0gZnVuY3Rpb24gKHZhbHVlKSB7XHJcbiAgICAgICAgLy8gcmV0dXJuIHRoaXMucHJvbWlzZS5yZXNvbHZlKHZhbHVlKTtcclxuICAgICAgICByZXR1cm4gdGhpcy5yZXNvbHZlRnVuYyh2YWx1ZSk7XHJcbiAgICB9O1xyXG4gICAgSmFkZXhQcm9taXNlLnByb3RvdHlwZS5yZWplY3QgPSBmdW5jdGlvbiAoZXJyb3IpIHtcclxuICAgICAgICAvLyByZXR1cm4gdGhpcy5wcm9taXNlLnJlamVjdChlcnJvcik7XHJcbiAgICAgICAgcmV0dXJuIHRoaXMucmVqZWN0RnVuYyhlcnJvcik7XHJcbiAgICB9O1xyXG4gICAgSmFkZXhQcm9taXNlLnByb3RvdHlwZS5yZXNvbHZlSW50ZXJtZWRpYXRlID0gZnVuY3Rpb24gKHZhbHVlKSB7XHJcbiAgICAgICAgZm9yICh2YXIgX2kgPSAwLCBfYSA9IHRoaXMuaW50ZXJtZWRpYXRlUmVzb2x2ZUNhbGxiYWNrczsgX2kgPCBfYS5sZW5ndGg7IF9pKyspIHtcclxuICAgICAgICAgICAgdmFyIGNiID0gX2FbX2ldO1xyXG4gICAgICAgICAgICBjYih2YWx1ZSk7XHJcbiAgICAgICAgfVxyXG4gICAgfTtcclxuICAgIEphZGV4UHJvbWlzZS5wcm90b3R5cGUudGhlbkludGVybWVkaWF0ZSA9IGZ1bmN0aW9uIChvbmZ1bGZpbGxlZCwgb25yZWplY3RlZCkge1xyXG4gICAgICAgIHRoaXMuaW50ZXJtZWRpYXRlUmVzb2x2ZUNhbGxiYWNrcy5wdXNoKG9uZnVsZmlsbGVkKTtcclxuICAgICAgICAvLyB0aGlzLmludGVybWVkaWF0ZVJlamVjdENhbGxiYWNrcy5wdXNoKG9ucmVqZWN0ZWQpO1xyXG4gICAgfTtcclxuICAgIEphZGV4UHJvbWlzZS5wcm90b3R5cGUudGVybWluYXRlID0gZnVuY3Rpb24gKCkge1xyXG4gICAgICAgIHZhciBjbWQgPSB7XHJcbiAgICAgICAgICAgIF9fY2xhc3NuYW1lOiBcImNvbS5hY3Rvcm9uLndlYnNlcnZpY2UubWVzc2FnZXMuU2VydmljZVRlcm1pbmF0ZUludm9jYXRpb25NZXNzYWdlXCIsXHJcbiAgICAgICAgICAgIGNhbGxpZDogdGhpcy5jYWxsaWRcclxuICAgICAgICB9O1xyXG4gICAgICAgIEphZGV4Q29ubmVjdGlvbkhhbmRsZXJfMS5KYWRleENvbm5lY3Rpb25IYW5kbGVyLmdldEluc3RhbmNlKCkuc2VuZENvbnZlcnNhdGlvbk1lc3NhZ2UodGhpcy51cmwsIGNtZCk7XHJcbiAgICB9O1xyXG4gICAgO1xyXG4gICAgSmFkZXhQcm9taXNlLnByb3RvdHlwZS5wdWxsID0gZnVuY3Rpb24gKCkge1xyXG4gICAgICAgIHZhciBjbWQgPSB7XHJcbiAgICAgICAgICAgIF9fY2xhc3NuYW1lOiBcImNvbS5hY3Rvcm9uLndlYnNlcnZpY2UubWVzc2FnZXMuUHVsbFJlc3VsdE1lc3NhZ2VcIixcclxuICAgICAgICAgICAgY2FsbGlkOiB0aGlzLmNhbGxpZFxyXG4gICAgICAgIH07XHJcbiAgICAgICAgSmFkZXhDb25uZWN0aW9uSGFuZGxlcl8xLkphZGV4Q29ubmVjdGlvbkhhbmRsZXIuZ2V0SW5zdGFuY2UoKS5zZW5kQ29udmVyc2F0aW9uTWVzc2FnZSh0aGlzLnVybCwgY21kKTtcclxuICAgIH07XHJcbiAgICA7XHJcbiAgICByZXR1cm4gSmFkZXhQcm9taXNlO1xyXG59KCkpO1xyXG5leHBvcnRzLkphZGV4UHJvbWlzZSA9IEphZGV4UHJvbWlzZTtcclxuLy8jIHNvdXJjZU1hcHBpbmdVUkw9SmFkZXhQcm9taXNlLmpzLm1hcCIsIlwidXNlIHN0cmljdFwiO1xyXG52YXIgU1V0aWxfMSA9IHJlcXVpcmUoXCIuL1NVdGlsXCIpO1xyXG52YXIgU2VydmljZVByb3h5XzEgPSByZXF1aXJlKFwiLi9TZXJ2aWNlUHJveHlcIik7XHJcbi8qKlxyXG4gKiAgQ2xhc3MgdGhhdCBjYW4gcGFyc2UganNvbiB3aXRoIGFkZGl0aW9uYWwgZmVhdHVyZXMuXHJcbiAqICAtIGhhbmRsZXMgSmFkZXggcmVmZXJlbmNlc1xyXG4gKi9cclxudmFyIEpzb25QYXJzZXIgPSAoZnVuY3Rpb24gKCkge1xyXG4gICAgZnVuY3Rpb24gSnNvblBhcnNlcigpIHtcclxuICAgIH1cclxuICAgIEpzb25QYXJzZXIuaW5pdCA9IGZ1bmN0aW9uICgpIHtcclxuICAgICAgICBKc29uUGFyc2VyLnJlZ2lzdGVyQ2xhc3MyKFwiamF2YS51dGlsLkRhdGVcIiwgeyBjcmVhdGU6IGZ1bmN0aW9uIChvYmopIHtcclxuICAgICAgICAgICAgICAgIHJldHVybiBuZXcgRGF0ZShvYmoudmFsdWUpO1xyXG4gICAgICAgICAgICB9IH0pO1xyXG4gICAgfTtcclxuICAgIC8qKlxyXG4gICAgICogIFJlZ2lzdGVyIGEgY2xhc3MgYXQgdGhlIHBhcnNlci5cclxuICAgICAqL1xyXG4gICAgSnNvblBhcnNlci5yZWdpc3RlckNsYXNzID0gZnVuY3Rpb24gKGNsYXp6KSB7XHJcbiAgICAgICAgaWYgKFwiX19jbGFzc25hbWVcIiBpbiBjbGF6eikge1xyXG4gICAgICAgICAgICBKc29uUGFyc2VyLnJlZ2lzdGVyZWRDbGFzc2VzW2NsYXp6Ll9fY2xhc3NuYW1lXSA9IGNsYXp6O1xyXG4gICAgICAgIH1cclxuICAgICAgICBlbHNlIHtcclxuICAgICAgICAgICAgdmFyIGluc3RhbmNlID0gbmV3IGNsYXp6KCk7XHJcbiAgICAgICAgICAgIGlmIChcIl9fY2xhc3NuYW1lXCIgaW4gaW5zdGFuY2UpIHtcclxuICAgICAgICAgICAgICAgIEpzb25QYXJzZXIucmVnaXN0ZXJlZENsYXNzZXNbaW5zdGFuY2UuX19jbGFzc25hbWVdID0gY2xheno7XHJcbiAgICAgICAgICAgIH1cclxuICAgICAgICAgICAgZWxzZSB7XHJcbiAgICAgICAgICAgICAgICB0aHJvdyBuZXcgRXJyb3IoXCJDYW5ub3QgcmVnaXN0ZXIgY2xhc3Mgd2l0aG91dCBfX2NsYXNzbmFtZSBzdGF0aWMgZmllbGQgb3IgbWVtYmVyOiBcIiArIGNsYXp6Lm5hbWUpO1xyXG4gICAgICAgICAgICB9XHJcbiAgICAgICAgfVxyXG4gICAgfTtcclxuICAgIC8qKlxyXG4gICAgICogIFJlZ2lzdGVyIGEgY2xhc3MgYXQgdGhlIHBhcnNlci5cclxuICAgICAqL1xyXG4gICAgSnNvblBhcnNlci5yZWdpc3RlckNsYXNzMiA9IGZ1bmN0aW9uIChjbGFzc25hbWUsIGNyZWF0ZSkge1xyXG4gICAgICAgIEpzb25QYXJzZXIucmVnaXN0ZXJlZENsYXNzZXNbY2xhc3NuYW1lXSA9IGNyZWF0ZTtcclxuICAgIH07XHJcbiAgICAvKipcclxuICAgICAqICBKU09uLnBhcnNlIGV4dGVuc2lvbiB0byBoYW5kbGUgSmFkZXggcmVmZXJlbmNlIG1lY2hhbmlzbS5cclxuICAgICAqICBAcGFyYW0gc3RyIFRoZSBzdHJpbmcgb2YgdGhlIGpzb24gb2JqZWN0IHRvIHBhcnNlLlxyXG4gICAgICogIEByZXR1cm4gVGhlIHBhcnNlZCBvYmplY3QuXHJcbiAgICAgKi9cclxuICAgIEpzb25QYXJzZXIucGFyc2UgPSBmdW5jdGlvbiAoc3RyLCB1cmwpIHtcclxuICAgICAgICB2YXIgaWRtYXJrZXIgPSBcIl9faWRcIjtcclxuICAgICAgICB2YXIgcmVmbWFya2VyID0gXCJfX3JlZlwiO1xyXG4gICAgICAgIC8vXHRcdGxldCBhcnJheW1hcmtlciA9IFwiX19hcnJheVwiO1xyXG4gICAgICAgIC8vXHRcdGxldCBjb2xsZWN0aW9ubWFya2VyID0gXCJfX2NvbGxlY3Rpb25cIjtcclxuICAgICAgICB2YXIgcmVwbGFjZW1hcmtlciA9IFtcIl9fYXJyYXlcIiwgXCJfX2NvbGxlY3Rpb25cIl07XHJcbiAgICAgICAgdmFyIG9zID0ge307IC8vIHRoZSBvYmplY3RzIHBlciBpZFxyXG4gICAgICAgIHZhciByZWZzID0gW107IC8vIHRoZSB1bnJlc29sdmVkIHJlZmVyZW5jZXNcclxuICAgICAgICB2YXIgb2JqO1xyXG4gICAgICAgIHRyeSB7XHJcbiAgICAgICAgICAgIG9iaiA9IEpTT04ucGFyc2Uoc3RyKTtcclxuICAgICAgICB9XHJcbiAgICAgICAgY2F0Y2ggKGUpIHtcclxuICAgICAgICAgICAgY29uc29sZS5lcnJvcihcIkNvdWxkIG5vdCBwYXJzZSBzdHJpbmc6IFwiICsgc3RyKTtcclxuICAgICAgICAgICAgdGhyb3cgZTtcclxuICAgICAgICB9XHJcbiAgICAgICAgdmFyIHJlY3Vyc2UgPSBmdW5jdGlvbiAob2JqLCBwcm9wLCBwYXJlbnQpIHtcclxuICAgICAgICAgICAgLy9cdCAgICBjb25zb2xlLmxvZyhvYmorXCIgXCIrcHJvcCtcIiBcIitwYXJlbnQpO1xyXG4gICAgICAgICAgICBpZiAoIVNVdGlsXzEuU1V0aWwuaXNCYXNpY1R5cGUob2JqKSkge1xyXG4gICAgICAgICAgICAgICAgLy8gdGVzdCBpZiBpdCBpcyBqdXN0IGEgcGxhY2Vob2xkZXIgb2JqZWN0IHRoYXQgbXVzdCBiZSBjaGFuZ2VkXHJcbiAgICAgICAgICAgICAgICAvL1x0XHRcdGlmKHByb3AhPW51bGwpXHJcbiAgICAgICAgICAgICAgICAvL1x0XHRcdHtcclxuICAgICAgICAgICAgICAgIGZvciAodmFyIGkgPSAwOyBpIDwgcmVwbGFjZW1hcmtlci5sZW5ndGg7IGkrKykge1xyXG4gICAgICAgICAgICAgICAgICAgIGlmIChyZXBsYWNlbWFya2VyW2ldIGluIG9iaikge1xyXG4gICAgICAgICAgICAgICAgICAgICAgICBvYmogPSBvYmpbcmVwbGFjZW1hcmtlcltpXV07XHJcbiAgICAgICAgICAgICAgICAgICAgICAgIGJyZWFrO1xyXG4gICAgICAgICAgICAgICAgICAgIH1cclxuICAgICAgICAgICAgICAgIH1cclxuICAgICAgICAgICAgICAgIC8vXHRcdCAgICB9XHJcbiAgICAgICAgICAgICAgICAvLyBpbnN0YW50aWF0ZSBjbGFzc2VzXHJcbiAgICAgICAgICAgICAgICBpZiAoXCJfX2NsYXNzbmFtZVwiIGluIG9iaikge1xyXG4gICAgICAgICAgICAgICAgICAgIHZhciBjbGFzc05hbWUgPSBvYmpbXCJfX2NsYXNzbmFtZVwiXTtcclxuICAgICAgICAgICAgICAgICAgICBpZiAoY2xhc3NOYW1lID09IFwiamFkZXguYnJpZGdlLnNlcnZpY2UuSVNlcnZpY2VcIikge1xyXG4gICAgICAgICAgICAgICAgICAgICAgICBvYmogPSBuZXcgU2VydmljZVByb3h5XzEuU2VydmljZVByb3h5KG9iai5zZXJ2aWNlSWRlbnRpZmllciwgcmVjdXJzZShvYmoubWV0aG9kTmFtZXMsIFwibWV0aG9kTmFtZXNcIiwgb2JqKSwgdXJsKTtcclxuICAgICAgICAgICAgICAgICAgICB9XHJcbiAgICAgICAgICAgICAgICAgICAgZWxzZSBpZiAoY2xhc3NOYW1lIGluIEpzb25QYXJzZXIucmVnaXN0ZXJlZENsYXNzZXMpIHtcclxuICAgICAgICAgICAgICAgICAgICAgICAgdmFyIGZ1bmMgPSBKc29uUGFyc2VyLnJlZ2lzdGVyZWRDbGFzc2VzW2NsYXNzTmFtZV07XHJcbiAgICAgICAgICAgICAgICAgICAgICAgIGlmIChmdW5jLmNyZWF0ZSkge1xyXG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgb2JqID0gZnVuYy5jcmVhdGUob2JqKTtcclxuICAgICAgICAgICAgICAgICAgICAgICAgfVxyXG4gICAgICAgICAgICAgICAgICAgICAgICBlbHNlIHtcclxuICAgICAgICAgICAgICAgICAgICAgICAgICAgIC8vIGl0ZXJhdGUgbWVtYmVyczpcclxuICAgICAgICAgICAgICAgICAgICAgICAgICAgIHZhciBpbnN0YW5jZSA9IG5ldyBmdW5jKCk7XHJcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICBmb3IgKHZhciBwcm9wXzEgaW4gb2JqKSB7XHJcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgaW5zdGFuY2VbcHJvcF8xXSA9IHJlY3Vyc2Uob2JqW3Byb3BfMV0sIHByb3BfMSwgb2JqKTtcclxuICAgICAgICAgICAgICAgICAgICAgICAgICAgIH1cclxuICAgICAgICAgICAgICAgICAgICAgICAgICAgIG9iaiA9IGluc3RhbmNlO1xyXG4gICAgICAgICAgICAgICAgICAgICAgICB9XHJcbiAgICAgICAgICAgICAgICAgICAgfVxyXG4gICAgICAgICAgICAgICAgfVxyXG4gICAgICAgICAgICAgICAgZWxzZSB7XHJcbiAgICAgICAgICAgICAgICAgICAgLy8gcmVjcmVhdGUgYXJyYXlzXHJcbiAgICAgICAgICAgICAgICAgICAgaWYgKFNVdGlsXzEuU1V0aWwuaXNBcnJheShvYmopKSB7XHJcbiAgICAgICAgICAgICAgICAgICAgICAgIGZvciAodmFyIGkgPSAwOyBpIDwgb2JqLmxlbmd0aDsgaSsrKSB7XHJcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICBpZiAoIVNVdGlsXzEuU1V0aWwuaXNCYXNpY1R5cGUob2JqW2ldKSkge1xyXG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIGlmIChyZWZtYXJrZXIgaW4gb2JqW2ldKSB7XHJcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIG9ialtpXSA9IHJlY3Vyc2Uob2JqW2ldLCBpLCBvYmopO1xyXG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIH1cclxuICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICBlbHNlIHtcclxuICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgb2JqW2ldID0gcmVjdXJzZShvYmpbaV0sIHByb3AsIG9iaik7XHJcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgfVxyXG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgfVxyXG4gICAgICAgICAgICAgICAgICAgICAgICB9XHJcbiAgICAgICAgICAgICAgICAgICAgfVxyXG4gICAgICAgICAgICAgICAgfVxyXG4gICAgICAgICAgICAgICAgaWYgKHJlZm1hcmtlciBpbiBvYmopIHtcclxuICAgICAgICAgICAgICAgICAgICB2YXIgcmVmID0gb2JqW3JlZm1hcmtlcl07XHJcbiAgICAgICAgICAgICAgICAgICAgaWYgKHJlZiBpbiBvcykge1xyXG4gICAgICAgICAgICAgICAgICAgICAgICBvYmogPSBvc1tyZWZdO1xyXG4gICAgICAgICAgICAgICAgICAgIH1cclxuICAgICAgICAgICAgICAgICAgICBlbHNlIHtcclxuICAgICAgICAgICAgICAgICAgICAgICAgcmVmcy5wdXNoKFtwYXJlbnQsIHByb3AsIHJlZl0pOyAvLyBsYXp5IGV2YWx1YXRpb24gbmVjZXNzYXJ5XHJcbiAgICAgICAgICAgICAgICAgICAgfVxyXG4gICAgICAgICAgICAgICAgfVxyXG4gICAgICAgICAgICAgICAgZWxzZSB7XHJcbiAgICAgICAgICAgICAgICAgICAgdmFyIGlkID0gbnVsbDtcclxuICAgICAgICAgICAgICAgICAgICBpZiAoaWRtYXJrZXIgaW4gb2JqKSB7XHJcbiAgICAgICAgICAgICAgICAgICAgICAgIGlkID0gb2JqW2lkbWFya2VyXTtcclxuICAgICAgICAgICAgICAgICAgICAgICAgZGVsZXRlIG9ialtpZG1hcmtlcl07XHJcbiAgICAgICAgICAgICAgICAgICAgfVxyXG4gICAgICAgICAgICAgICAgICAgIGlmIChcIiR2YWx1ZXNcIiBpbiBvYmopIHtcclxuICAgICAgICAgICAgICAgICAgICAgICAgb2JqID0gb2JqLiR2YWx1ZXMubWFwKHJlY3Vyc2UpO1xyXG4gICAgICAgICAgICAgICAgICAgIH1cclxuICAgICAgICAgICAgICAgICAgICBlbHNlIHtcclxuICAgICAgICAgICAgICAgICAgICAgICAgZm9yICh2YXIgcHJvcF8yIGluIG9iaikge1xyXG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgb2JqW3Byb3BfMl0gPSByZWN1cnNlKG9ialtwcm9wXzJdLCBwcm9wXzIsIG9iaik7XHJcbiAgICAgICAgICAgICAgICAgICAgICAgIH1cclxuICAgICAgICAgICAgICAgICAgICB9XHJcbiAgICAgICAgICAgICAgICAgICAgaWYgKGlkICE9IG51bGwpIHtcclxuICAgICAgICAgICAgICAgICAgICAgICAgb3NbaWRdID0gb2JqO1xyXG4gICAgICAgICAgICAgICAgICAgIH1cclxuICAgICAgICAgICAgICAgIH1cclxuICAgICAgICAgICAgICAgIC8vIHVud3JhcCBib3hlZCB2YWx1ZXMgZm9yIEpTOlxyXG4gICAgICAgICAgICAgICAgdmFyIHdyYXBwZWRUeXBlID0gU1V0aWxfMS5TVXRpbC5pc1dyYXBwZWRUeXBlKG9iaik7XHJcbiAgICAgICAgICAgICAgICBpZiAod3JhcHBlZFR5cGUpIHtcclxuICAgICAgICAgICAgICAgICAgICAvLyBjb25zb2xlLmxvZyhcImZvdW5kIHdyYXBwZWQ6IFwiICsgaXNXcmFwcGVkVHlwZShvYmopICsgXCIgZm9yOiBcIiArIG9iai5fX2NsYXNzbmFtZSArIFwiIHdpdGggdmFsdWU6IFwiICsgb2JqLnZhbHVlKVxyXG4gICAgICAgICAgICAgICAgICAgIGlmICh3cmFwcGVkVHlwZSA9PSBcImJvb2xlYW5cIikge1xyXG4gICAgICAgICAgICAgICAgICAgIH1cclxuICAgICAgICAgICAgICAgICAgICBlbHNlIGlmICh3cmFwcGVkVHlwZSA9PSBcInN0cmluZ1wiKSB7XHJcbiAgICAgICAgICAgICAgICAgICAgICAgIG9iaiA9IG9iai52YWx1ZTtcclxuICAgICAgICAgICAgICAgICAgICB9XHJcbiAgICAgICAgICAgICAgICAgICAgZWxzZSB7XHJcbiAgICAgICAgICAgICAgICAgICAgICAgIC8vIGV2ZXJ5dGhpbmcgZWxzZSBpcyBhIG51bWJlciBpbiBKU1xyXG4gICAgICAgICAgICAgICAgICAgICAgICBvYmogPSArb2JqLnZhbHVlO1xyXG4gICAgICAgICAgICAgICAgICAgIH1cclxuICAgICAgICAgICAgICAgIH1cclxuICAgICAgICAgICAgICAgIGVsc2UgaWYgKFNVdGlsXzEuU1V0aWwuaXNFbnVtKG9iaikpIHtcclxuICAgICAgICAgICAgICAgICAgICAvLyBjb252ZXJ0IGVudW1zIHRvIHN0cmluZ3NcclxuICAgICAgICAgICAgICAgICAgICBvYmogPSBvYmoudmFsdWU7XHJcbiAgICAgICAgICAgICAgICB9XHJcbiAgICAgICAgICAgIH1cclxuICAgICAgICAgICAgcmV0dXJuIG9iajtcclxuICAgICAgICB9O1xyXG4gICAgICAgIG9iaiA9IHJlY3Vyc2Uob2JqLCBudWxsLCBudWxsKTtcclxuICAgICAgICAvLyByZXNvbHZlIGxhenkgcmVmZXJlbmNlc1xyXG4gICAgICAgIGZvciAodmFyIGkgPSAwOyBpIDwgcmVmcy5sZW5ndGg7IGkrKykge1xyXG4gICAgICAgICAgICB2YXIgcmVmID0gcmVmc1tpXTtcclxuICAgICAgICAgICAgcmVmWzBdW3JlZlsxXV0gPSBvc1tyZWZbMl1dO1xyXG4gICAgICAgIH1cclxuICAgICAgICByZXR1cm4gb2JqO1xyXG4gICAgfTtcclxuICAgIC8qKiBUaGUgcmVnaXN0ZXJlZCBjbGFzc2VzLiAqL1xyXG4gICAgSnNvblBhcnNlci5yZWdpc3RlcmVkQ2xhc3NlcyA9IHt9O1xyXG4gICAgcmV0dXJuIEpzb25QYXJzZXI7XHJcbn0oKSk7XHJcbmV4cG9ydHMuSnNvblBhcnNlciA9IEpzb25QYXJzZXI7XHJcbkpzb25QYXJzZXIuaW5pdCgpO1xyXG4vLyMgc291cmNlTWFwcGluZ1VSTD1Kc29uUGFyc2VyLmpzLm1hcCIsIlwidXNlIHN0cmljdFwiO1xyXG4vKipcclxuICogIFN0YXRpYyBoZWxwZXIgbWV0aG9kcy5cclxuICovXHJcbnZhciBTVXRpbCA9IChmdW5jdGlvbiAoKSB7XHJcbiAgICBmdW5jdGlvbiBTVXRpbCgpIHtcclxuICAgIH1cclxuICAgIC8qKlxyXG4gICAgICogIFRlc3QgaWYgYW4gb2JqZWN0IGlzIGEgYmFzaWMgdHlwZS5cclxuICAgICAqICBAcGFyYW0gb2JqIFRoZSBvYmplY3QuXHJcbiAgICAgKiAgQHJldHVybiBUcnVlLCBpZiBpcyBhIGJhc2ljIHR5cGUuXHJcbiAgICAgKi9cclxuICAgIFNVdGlsLmlzQmFzaWNUeXBlID0gZnVuY3Rpb24gKG9iaikge1xyXG4gICAgICAgIHJldHVybiB0eXBlb2Ygb2JqICE9PSAnb2JqZWN0JyB8fCAhb2JqO1xyXG4gICAgfTtcclxuICAgIC8qKlxyXG4gICAgICogIFRlc3QgaWYgYW4gb2JqZWN0IGlzIGEgamF2YSB3cmFwcGVkIHR5cGUuXHJcbiAgICAgKiAgQHBhcmFtIG9iaiBUaGUgb2JqZWN0LlxyXG4gICAgICogIEByZXR1cm4gRmFsc2UsIGlmIGlzIG5vdCBhIHdyYXBwZWQgcHJpbWl0aXZlIHR5cGUsIGVsc2UgcmV0dXJucyB0aGUgY29ycmVzcG9uZGluZyBKUyB0eXBlLlxyXG4gICAgICovXHJcbiAgICBTVXRpbC5pc1dyYXBwZWRUeXBlID0gZnVuY3Rpb24gKG9iaikge1xyXG4gICAgICAgIGlmIChcIl9fY2xhc3NuYW1lXCIgaW4gb2JqKSB7XHJcbiAgICAgICAgICAgIHZhciBzZWFyY2hpbmcgPSBvYmouX19jbGFzc25hbWUucmVwbGFjZSgvXFwuL2csICdfJyk7XHJcbiAgICAgICAgICAgIHJldHVybiBTVXRpbC53cmFwcGVkQ29udmVyc2lvblR5cGVzW3NlYXJjaGluZ107XHJcbiAgICAgICAgfVxyXG4gICAgICAgIGVsc2Uge1xyXG4gICAgICAgICAgICByZXR1cm4gZmFsc2U7XHJcbiAgICAgICAgfVxyXG4gICAgfTtcclxuICAgIC8qKlxyXG4gICAgICogIENoZWNrIG9mIGFuIG9iaiBpcyBhbiBlbnVtLlxyXG4gICAgICovXHJcbiAgICBTVXRpbC5pc0VudW0gPSBmdW5jdGlvbiAob2JqKSB7XHJcbiAgICAgICAgcmV0dXJuIChcImVudW1cIiBpbiBvYmopO1xyXG4gICAgfTtcclxuICAgIC8qKlxyXG4gICAgICogIFRlc3QgaWYgYW4gb2JqZWN0IGlzIGFuIGFycmF5LlxyXG4gICAgICogIEBwYXJhbSBvYmogVGhlIG9iamVjdC5cclxuICAgICAqICBAcmV0dXJuIFRydWUsIGlmIGlzIGFuIGFycmF5LlxyXG4gICAgICovXHJcbiAgICBTVXRpbC5pc0FycmF5ID0gZnVuY3Rpb24gKG9iaikge1xyXG4gICAgICAgIHJldHVybiBPYmplY3QucHJvdG90eXBlLnRvU3RyaW5nLmNhbGwob2JqKSA9PSAnW29iamVjdCBBcnJheV0nO1xyXG4gICAgfTtcclxuICAgIC8qKlxyXG4gICAgICogIENvbXB1dGUgdGhlIGFwcHJveC4gc2l6ZSBvZiBhbiBvYmplY3QuXHJcbiAgICAgKiAgQHBhcmFtIG9iaiBUaGUgb2JqZWN0LlxyXG4gICAgICovXHJcbiAgICBTVXRpbC5zaXplT2YgPSBmdW5jdGlvbiAob2JqZWN0KSB7XHJcbiAgICAgICAgdmFyIG9iamVjdHMgPSBbb2JqZWN0XTtcclxuICAgICAgICB2YXIgc2l6ZSA9IDA7XHJcbiAgICAgICAgZm9yICh2YXIgaSA9IDA7IGkgPCBvYmplY3RzLmxlbmd0aDsgaSsrKSB7XHJcbiAgICAgICAgICAgIHN3aXRjaCAodHlwZW9mIG9iamVjdHNbaV0pIHtcclxuICAgICAgICAgICAgICAgIGNhc2UgJ2Jvb2xlYW4nOlxyXG4gICAgICAgICAgICAgICAgICAgIHNpemUgKz0gNDtcclxuICAgICAgICAgICAgICAgICAgICBicmVhaztcclxuICAgICAgICAgICAgICAgIGNhc2UgJ251bWJlcic6XHJcbiAgICAgICAgICAgICAgICAgICAgc2l6ZSArPSA4O1xyXG4gICAgICAgICAgICAgICAgICAgIGJyZWFrO1xyXG4gICAgICAgICAgICAgICAgY2FzZSAnc3RyaW5nJzpcclxuICAgICAgICAgICAgICAgICAgICBzaXplICs9IDIgKiBvYmplY3RzW2ldLmxlbmd0aDtcclxuICAgICAgICAgICAgICAgICAgICBicmVhaztcclxuICAgICAgICAgICAgICAgIGNhc2UgJ29iamVjdCc6XHJcbiAgICAgICAgICAgICAgICAgICAgaWYgKE9iamVjdC5wcm90b3R5cGUudG9TdHJpbmcuY2FsbChvYmplY3RzW2ldKSAhPSAnW29iamVjdCBBcnJheV0nKSB7XHJcbiAgICAgICAgICAgICAgICAgICAgICAgIGZvciAodmFyIGtleV8xIGluIG9iamVjdHNbaV0pXHJcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICBzaXplICs9IDIgKiBrZXlfMS5sZW5ndGg7XHJcbiAgICAgICAgICAgICAgICAgICAgfVxyXG4gICAgICAgICAgICAgICAgICAgIHZhciBwcm9jZXNzZWQgPSBmYWxzZTtcclxuICAgICAgICAgICAgICAgICAgICB2YXIga2V5ID0gdm9pZCAwO1xyXG4gICAgICAgICAgICAgICAgICAgIGZvciAoa2V5IGluIG9iamVjdHNbaV0pIHtcclxuICAgICAgICAgICAgICAgICAgICAgICAgZm9yICh2YXIgc2VhcmNoID0gMDsgc2VhcmNoIDwgb2JqZWN0cy5sZW5ndGg7IHNlYXJjaCsrKSB7XHJcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICBpZiAob2JqZWN0c1tzZWFyY2hdID09PSBvYmplY3RzW2ldW2tleV0pIHtcclxuICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICBwcm9jZXNzZWQgPSB0cnVlO1xyXG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIGJyZWFrO1xyXG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgfVxyXG4gICAgICAgICAgICAgICAgICAgICAgICB9XHJcbiAgICAgICAgICAgICAgICAgICAgfVxyXG4gICAgICAgICAgICAgICAgICAgIGlmICghcHJvY2Vzc2VkKVxyXG4gICAgICAgICAgICAgICAgICAgICAgICBvYmplY3RzLnB1c2gob2JqZWN0c1tpXVtrZXldKTtcclxuICAgICAgICAgICAgfVxyXG4gICAgICAgIH1cclxuICAgICAgICByZXR1cm4gc2l6ZTtcclxuICAgIH07XHJcbiAgICAvKipcclxuICAgICAqICBDaGVjayBpZiBvYmplY3QgaXMgdHJ1ZSBieSBpbnNwZWN0aW5nIGlmIGl0IGNvbnRhaW5zIGEgdHJ1ZSBwcm9wZXJ0eS5cclxuICAgICAqL1xyXG4gICAgU1V0aWwuaXNUcnVlID0gZnVuY3Rpb24gKG9iaikge1xyXG4gICAgICAgIHJldHVybiBvYmogPT0gdHJ1ZSB8fCAob2JqICE9IG51bGwgJiYgb2JqLmhhc093blByb3BlcnR5KFwidmFsdWVcIikgJiYgb2JqLnZhbHVlID09IHRydWUpO1xyXG4gICAgfTtcclxuICAgIC8qKlxyXG4gICAgICogIEFzc2VydCB0aGF0IHRocm93cyBhbiBlcnJvciBpZiBub3QgaG9sZHMuXHJcbiAgICAgKi9cclxuICAgIFNVdGlsLmFzc2VydCA9IGZ1bmN0aW9uIChjb25kaXRpb24sIG1lc3NhZ2UpIHtcclxuICAgICAgICBpZiAoIWNvbmRpdGlvbikge1xyXG4gICAgICAgICAgICBtZXNzYWdlID0gbWVzc2FnZSB8fCBcIkFzc2VydGlvbiBmYWlsZWRcIjtcclxuICAgICAgICAgICAgaWYgKHR5cGVvZiBFcnJvciAhPT0gXCJ1bmRlZmluZWRcIikge1xyXG4gICAgICAgICAgICAgICAgdGhyb3cgbmV3IEVycm9yKG1lc3NhZ2UpO1xyXG4gICAgICAgICAgICB9XHJcbiAgICAgICAgICAgIHRocm93IG1lc3NhZ2U7IC8vIEZhbGxiYWNrXHJcbiAgICAgICAgfVxyXG4gICAgfTtcclxuICAgIC8qKlxyXG4gICAgICogIEdldCB0aGUgc2VydmljZSBpZCBhcyBzdHJpbmcuXHJcbiAgICAgKiAgKG90aGVyd2lzZSBpdCBjYW5ub3QgYmUgdXNlZCBhcyBrZXkgaW4gYSBtYXAgYmVjYXVzZVxyXG4gICAgICogIG5vIGVxdWFscyBleGlzdHMpLlxyXG4gICAgICovXHJcbiAgICBTVXRpbC5nZXRTZXJ2aWNlSWRBc1N0cmluZyA9IGZ1bmN0aW9uIChzaWQpIHtcclxuICAgICAgICByZXR1cm4gc2lkLnNlcnZpY2VOYW1lICsgXCJAXCIgKyBzaWQucHJvdmlkZXJJZDtcclxuICAgIH07XHJcbiAgICAvKipcclxuICAgICAqICBBZGQgYSBjb25zb2xlIG91dCBlcnJvciBoYW5kbGVyIHRvIHRoZSBwcm9taXNlLlxyXG4gICAgICovXHJcbiAgICBTVXRpbC5hZGRFcnJIYW5kbGVyID0gZnVuY3Rpb24gKHApIHtcclxuICAgICAgICBwLm9sZGNhdGNoID0gcC5jYXRjaDtcclxuICAgICAgICBwLmhhc0Vycm9yaGFuZGxlciA9IGZhbHNlO1xyXG4gICAgICAgIHAuY2F0Y2ggPSBmdW5jdGlvbiAoZWgpIHtcclxuICAgICAgICAgICAgcC5oYXNFcnJvckhhbmRsZXIgPSB0cnVlO1xyXG4gICAgICAgICAgICByZXR1cm4gcC5vbGRjYXRjaChlaCk7XHJcbiAgICAgICAgfTtcclxuICAgICAgICBwLm9sZGNhdGNoKGZ1bmN0aW9uIChlcnIpIHtcclxuICAgICAgICAgICAgaWYgKCFwLmhhc0Vycm9ySGFuZGxlcilcclxuICAgICAgICAgICAgICAgIGNvbnNvbGUubG9nKFwiRXJyb3Igb2NjdXJyZWQ6IFwiICsgZXJyKTtcclxuICAgICAgICB9KTtcclxuICAgICAgICBwLm9sZHRoZW4gPSBwLnRoZW47XHJcbiAgICAgICAgcC50aGVuID0gZnVuY3Rpb24gKHQsIGUpIHtcclxuICAgICAgICAgICAgaWYgKGUpXHJcbiAgICAgICAgICAgICAgICBwLmhhc0Vycm9ySGFuZGxlciA9IHRydWU7XHJcbiAgICAgICAgICAgIHJldHVybiBwLm9sZHRoZW4odCwgZSk7XHJcbiAgICAgICAgfTtcclxuICAgICAgICByZXR1cm4gcDtcclxuICAgIH07XHJcbiAgICAvKipcclxuICAgICAqICBUZXN0IGlmIGEgbnVtYmVyIGlzIGEgZmxvYXQuXHJcbiAgICAgKiAgQHBhcmFtIG4gVGhlIG51bWJlciB0byB0ZXN0LlxyXG4gICAgICogIEByZXR1cm4gVHJ1ZSwgaWYgaXMgZmxvYXQuXHJcbiAgICAgKi9cclxuICAgIFNVdGlsLmlzRmxvYXQgPSBmdW5jdGlvbiAobikge1xyXG4gICAgICAgIHJldHVybiBuID09PSArbiAmJiBuICE9PSAobiB8IDApO1xyXG4gICAgfTtcclxuICAgIC8qKlxyXG4gICAgICogIFRlc3QgaWYgYSBudW1iZXIgaXMgYW4gaW50ZWdlci5cclxuICAgICAqICBAcGFyYW0gbiBUaGUgbnVtYmVyIHRvIHRlc3QuXHJcbiAgICAgKiAgQHJldHVybiBUcnVlLCBpZiBpcyBpbnRlZ2VyLlxyXG4gICAgICovXHJcbiAgICBTVXRpbC5pc0ludGVnZXIgPSBmdW5jdGlvbiAobikge1xyXG4gICAgICAgIHJldHVybiBuID09PSArbiAmJiBuID09PSAobiB8IDApO1xyXG4gICAgfTtcclxuICAgIC8qKlxyXG4gICAgICogIENoZWNrIGlmIGFuIG9iamVjdCBpcyBjb250YWluZWQgaW4gYW4gYXJyYXkuXHJcbiAgICAgKiAgVXNlcyBlcXVhbCBmdW5jdGlvbiB0byBjaGVjayBlcXVhbGl0eSBvZiBvYmplY3RzLlxyXG4gICAgICogIElmIG5vdCBwcm92aWRlZCB1c2VzIHJlZmVyZW5jZSB0ZXN0LlxyXG4gICAgICogIEBwYXJhbSBvYmplY3QgVGhlIG9iamVjdCB0byBjaGVjay5cclxuICAgICAqICBAcGFyYW0gb2JqZWN0cyBUaGUgYXJyYXkuXHJcbiAgICAgKiAgQHBhcmFtIGVxdWFscyBUaGUgZXF1YWxzIG1ldGhvZC5cclxuICAgICAqICBAcmV0dXJuIFRydWUsIGlmIGlzIGNvbnRhaW5lZC5cclxuICAgICAqL1xyXG4gICAgU1V0aWwuY29udGFpbnNPYmplY3QgPSBmdW5jdGlvbiAob2JqZWN0LCBvYmplY3RzLCBlcXVhbHMpIHtcclxuICAgICAgICB2YXIgcmV0ID0gZmFsc2U7XHJcbiAgICAgICAgZm9yICh2YXIgaSA9IDA7IGkgPCBvYmplY3RzLmxlbmd0aCAmJiAhcmV0OyBpKyspIHtcclxuICAgICAgICAgICAgcmV0ID0gZXF1YWxzID8gZXF1YWxzKG9iamVjdCwgb2JqZWN0c1tpXSkgOiBvYmplY3QgPT09IG9iamVjdHNbaV07XHJcbiAgICAgICAgfVxyXG4gICAgICAgIHJldHVybiByZXQ7XHJcbiAgICB9O1xyXG4gICAgLyoqXHJcbiAgICAgKiAgR2V0IHRoZSBpbmRleCBvZiBhbiBvYmplY3QgaW4gYW4gYXJyYXkuIC0xIGZvciBub3QgY29udGFpbmVkLlxyXG4gICAgICogIEBwYXJhbSBvYmplY3QgVGhlIG9iamVjdCB0byBjaGVjay5cclxuICAgICAqICBAcGFyYW0gb2JqZWN0cyBUaGUgYXJyYXkuXHJcbiAgICAgKiAgQHBhcmFtIGVxdWFscyBUaGUgZXF1YWxzIG1ldGhvZC5cclxuICAgICAqICBAcmV0dXJuIFRoZSBpbmRleCBvciAtMS5cclxuICAgICAqL1xyXG4gICAgU1V0aWwuaW5kZXhPZk9iamVjdCA9IGZ1bmN0aW9uIChvYmplY3QsIG9iamVjdHMsIGVxdWFscykge1xyXG4gICAgICAgIHZhciByZXQgPSAtMTtcclxuICAgICAgICBmb3IgKHZhciBpID0gMDsgaSA8IG9iamVjdHMubGVuZ3RoOyBpKyspIHtcclxuICAgICAgICAgICAgaWYgKGVxdWFscyA/IGVxdWFscyhvYmplY3QsIG9iamVjdHNbaV0pIDogb2JqZWN0ID09PSBvYmplY3RzW2ldKSB7XHJcbiAgICAgICAgICAgICAgICByZXQgPSBpO1xyXG4gICAgICAgICAgICAgICAgYnJlYWs7XHJcbiAgICAgICAgICAgIH1cclxuICAgICAgICB9XHJcbiAgICAgICAgcmV0dXJuIHJldDtcclxuICAgIH07XHJcbiAgICAvKipcclxuICAgICAqICBSZW1vdmUgYW4gb2JqZWN0IGZyb20gYW4gYXJyYXkuXHJcbiAgICAgKiAgQHBhcmFtIG9iamVjdCBUaGUgb2JqZWN0IHRvIHJlbW92ZS5cclxuICAgICAqICBAcGFyYW0gb2JqZWN0cyBUaGUgYXJyYXkuXHJcbiAgICAgKiAgQHBhcmFtIGVxdWFscyBUaGUgZXF1YWxzIG1ldGhvZC5cclxuICAgICAqICBAcmV0dXJuIFRydWUsIGlmIHdhcyByZW1vdmVkLlxyXG4gICAgICovXHJcbiAgICBTVXRpbC5yZW1vdmVPYmplY3QgPSBmdW5jdGlvbiAob2JqZWN0LCBvYmplY3RzLCBlcXVhbHMpIHtcclxuICAgICAgICB2YXIgcmV0ID0gU1V0aWwuaW5kZXhPZk9iamVjdChvYmplY3QsIG9iamVjdHMsIGVxdWFscyk7XHJcbiAgICAgICAgaWYgKHJldCAhPSAtMSlcclxuICAgICAgICAgICAgb2JqZWN0cy5zcGxpY2UocmV0LCAxKTtcclxuICAgICAgICByZXR1cm4gcmV0ID09IC0xID8gZmFsc2UgOiB0cnVlO1xyXG4gICAgfTtcclxuICAgIFNVdGlsLndyYXBwZWRDb252ZXJzaW9uVHlwZXMgPSB7XHJcbiAgICAgICAgamF2YV9sYW5nX0ludGVnZXI6IFwibnVtYmVyXCIsXHJcbiAgICAgICAgamF2YV9sYW5nX0J5dGU6IFwibnVtYmVyXCIsXHJcbiAgICAgICAgamF2YV9sYW5nX1Nob3J0OiBcIm51bWJlclwiLFxyXG4gICAgICAgIGphdmFfbGFuZ19Mb25nOiBcIm51bWJlclwiLFxyXG4gICAgICAgIGphdmFfbGFuZ19GbG9hdDogXCJudW1iZXJcIixcclxuICAgICAgICBqYXZhX2xhbmdfRG91YmxlOiBcIm51bWJlclwiLFxyXG4gICAgICAgIGphdmFfbGFuZ19DaGFyYWN0ZXI6IFwic3RyaW5nXCIsXHJcbiAgICAgICAgamF2YV9sYW5nX0Jvb2xlYW46IFwiYm9vbGVhblwiXHJcbiAgICB9O1xyXG4gICAgcmV0dXJuIFNVdGlsO1xyXG59KCkpO1xyXG5leHBvcnRzLlNVdGlsID0gU1V0aWw7XHJcbi8vIyBzb3VyY2VNYXBwaW5nVVJMPVNVdGlsLmpzLm1hcCIsIlwidXNlIHN0cmljdFwiO1xyXG52YXIgU2NvcGVzID0gKGZ1bmN0aW9uICgpIHtcclxuICAgIGZ1bmN0aW9uIFNjb3BlcygpIHtcclxuICAgIH1cclxuICAgIC8vXHQvKiogTm9uZSBjb21wb25lbnQgc2NvcGUgKG5vdGhpbmcgd2lsbCBiZSBzZWFyY2hlZCkuICovXHJcbiAgICAvL1x0Y29uc3QgU0NPUEVfTk9ORSA9IFwibm9uZVwiO1xyXG4gICAgLy9cclxuICAgIC8vXHQvKiogTG9jYWwgY29tcG9uZW50IHNjb3BlLiAqL1xyXG4gICAgLy9cdGNvbnN0IFNDT1BFX0xPQ0FMID0gXCJsb2NhbFwiO1xyXG4gICAgLy9cclxuICAgIC8vXHQvKiogQ29tcG9uZW50IHNjb3BlLiAqL1xyXG4gICAgLy9cdGNvbnN0IFNDT1BFX0NPTVBPTkVOVCA9IFwiY29tcG9uZW50XCI7XHJcbiAgICAvL1xyXG4gICAgLy9cdC8qKiBBcHBsaWNhdGlvbiBzY29wZS4gKi9cclxuICAgIC8vXHRjb25zdCBTQ09QRV9BUFBMSUNBVElPTiA9IFwiYXBwbGljYXRpb25cIjtcclxuICAgIC8qKiBQbGF0Zm9ybSBzY29wZS4gKi9cclxuICAgIFNjb3Blcy5TQ09QRV9QTEFURk9STSA9IFwicGxhdGZvcm1cIjtcclxuICAgIC8qKiBHbG9iYWwgc2NvcGUuICovXHJcbiAgICBTY29wZXMuU0NPUEVfR0xPQkFMID0gXCJnbG9iYWxcIjtcclxuICAgIC8vXHQvKiogUGFyZW50IHNjb3BlLiAqL1xyXG4gICAgLy9cdFNDT1BFX1BBUkVOVDpzdHJpbmcgPSBcInBhcmVudFwiO1xyXG4gICAgLyoqIFNlc3Npb24gc2NvcGUuICovXHJcbiAgICBTY29wZXMuU0NPUEVfU0VTU0lPTiA9IFwic2Vzc2lvblwiO1xyXG4gICAgcmV0dXJuIFNjb3BlcztcclxufSgpKTtcclxuZXhwb3J0cy5TY29wZXMgPSBTY29wZXM7XHJcbi8vIyBzb3VyY2VNYXBwaW5nVVJMPVNjb3Blcy5qcy5tYXAiLCJcInVzZSBzdHJpY3RcIjtcclxudmFyIEphZGV4UHJvbWlzZV8xID0gcmVxdWlyZShcIi4vSmFkZXhQcm9taXNlXCIpO1xyXG52YXIgSmFkZXhDb25uZWN0aW9uSGFuZGxlcl8xID0gcmVxdWlyZShcIi4vd2Vic29ja2V0L0phZGV4Q29ubmVjdGlvbkhhbmRsZXJcIik7XHJcbnZhciBTZXJ2aWNlSW52b2NhdGlvbk1lc3NhZ2VfMSA9IHJlcXVpcmUoXCIuL21lc3NhZ2VzL1NlcnZpY2VJbnZvY2F0aW9uTWVzc2FnZVwiKTtcclxudmFyIFNlcnZpY2VQcm94eSA9IChmdW5jdGlvbiAoKSB7XHJcbiAgICAvKipcclxuICAgICAqICBDcmVhdGUgYSBzZXJ2aWNlIHByb3h5IGZvciBhIEphZGV4IHNlcnZpY2UuXHJcbiAgICAgKi9cclxuICAgIGZ1bmN0aW9uIFNlcnZpY2VQcm94eShzZXJ2aWNlSWQsIG1ldGhvZE5hbWVzLCB1cmwpIHtcclxuICAgICAgICB0aGlzLnNlcnZpY2VJZCA9IHNlcnZpY2VJZDtcclxuICAgICAgICB0aGlzLnVybCA9IHVybDtcclxuICAgICAgICAvLyBHZW5lcmljIGludm9rZSBtZXRob2QgY2FsbGVkIG9uIGVhY2ggc2VydmljZSBpbnZvY2F0aW9uXHJcbiAgICAgICAgZm9yICh2YXIgaSA9IDA7IGkgPCBtZXRob2ROYW1lcy5sZW5ndGg7IGkrKykge1xyXG4gICAgICAgICAgICB0aGlzW21ldGhvZE5hbWVzW2ldXSA9IHRoaXMuY3JlYXRlTWV0aG9kKG1ldGhvZE5hbWVzW2ldKTtcclxuICAgICAgICB9XHJcbiAgICB9XHJcbiAgICAvKipcclxuICAgICAqICBHZW5lcmljIGludm9rZSBtZXRob2QgdGhhdCBzZW5kcyBhIG1ldGhvZCBjYWxsIHRvIHRoZSBzZXJ2ZXIgc2lkZS5cclxuICAgICAqL1xyXG4gICAgU2VydmljZVByb3h5LnByb3RvdHlwZS5pbnZva2UgPSBmdW5jdGlvbiAobmFtZSwgcGFyYW1zLCBjYWxsYmFjaykge1xyXG4gICAgICAgIHZhciByZXQgPSBuZXcgSmFkZXhQcm9taXNlXzEuSmFkZXhQcm9taXNlKHRoaXMudXJsKTtcclxuICAgICAgICB2YXIgY29ubSA9IEphZGV4Q29ubmVjdGlvbkhhbmRsZXJfMS5KYWRleENvbm5lY3Rpb25IYW5kbGVyLmdldEluc3RhbmNlKCk7XHJcbiAgICAgICAgLy8gQ29udmVydCBwYXJhbWV0ZXJzIHNlcGVyYXRlbHksIG9uZSBieSBvbmVcclxuICAgICAgICB2YXIgY3BhcmFtcyA9IFtdO1xyXG4gICAgICAgIGZvciAodmFyIGkgPSAwOyBpIDwgcGFyYW1zLmxlbmd0aDsgaSsrKSB7XHJcbiAgICAgICAgICAgIGNwYXJhbXMucHVzaChjb25tLm9iamVjdFRvSnNvbihwYXJhbXNbaV0pKTtcclxuICAgICAgICB9XHJcbiAgICAgICAgdmFyIGNtZCA9IG5ldyBTZXJ2aWNlSW52b2NhdGlvbk1lc3NhZ2VfMS5TZXJ2aWNlSW52b2NhdGlvbk1lc3NhZ2UodGhpcy5zZXJ2aWNlSWQsIG5hbWUsIGNwYXJhbXMpO1xyXG4gICAgICAgIC8vIGNvbnNvbGUubG9nKGNtZCk7XHJcbiAgICAgICAgLy8gd3JhcCBjYWxsYmFjayB0byBhbGxvdyBKYWRleFByb21pc2UuaW50ZXJtZWRpYXRlVGhlblxyXG4gICAgICAgIHZhciB3cmFwQ2IgPSBmdW5jdGlvbiAoaW50ZXJtZWRpYXRlUmVzdWx0KSB7XHJcbiAgICAgICAgICAgIC8vIGNvbnNvbGUubG9nKFwiY2FsbGluZyBpbnRlcm1lZGlhdGUgcmVzdWx0IHdpdGg6IFwiICsgaW50ZXJtZWRpYXRlUmVzdWx0KTtcclxuICAgICAgICAgICAgcmV0LnJlc29sdmVJbnRlcm1lZGlhdGUoaW50ZXJtZWRpYXRlUmVzdWx0KTtcclxuICAgICAgICAgICAgaWYgKGNhbGxiYWNrKSB7XHJcbiAgICAgICAgICAgICAgICBjYWxsYmFjayhpbnRlcm1lZGlhdGVSZXN1bHQpO1xyXG4gICAgICAgICAgICB9XHJcbiAgICAgICAgfTtcclxuICAgICAgICByZXQuY2FsbGlkID0gY29ubS5zZW5kTWVzc2FnZSh0aGlzLnVybCwgY21kLCBcImludm9rZVwiLCBmdW5jdGlvbiAocmVzKSB7IHJldHVybiByZXQucmVzb2x2ZShyZXMpOyB9LCBmdW5jdGlvbiAoZXgpIHsgcmV0dXJuIHJldC5yZWplY3QoZXgpOyB9LCB3cmFwQ2IpO1xyXG4gICAgICAgIHJldHVybiByZXQ7XHJcbiAgICB9O1xyXG4gICAgLyoqXHJcbiAgICAgKiAgQ3JlYXRlIG1ldGhvZCBmdW5jdGlvbiAobmVlZGVkIHRvIHByZXNlcnZlIHRoZSBuYW1lKS5cclxuICAgICAqXHJcbiAgICAgKiAgQ3JlYXRlcyBhbiBhcmd1bWVudCBhcnJheSBhbmQgaW52b2tlcyBnZW5lcmljIGludm9rZSBtZXRob2QuXHJcbiAgICAgKlxyXG4gICAgICogIFRPRE86IGNhbGxiYWNrIGZ1bmN0aW9uIGhhY2shXHJcbiAgICAgKi9cclxuICAgIFNlcnZpY2VQcm94eS5wcm90b3R5cGUuY3JlYXRlTWV0aG9kID0gZnVuY3Rpb24gKG5hbWUpIHtcclxuICAgICAgICB2YXIgb3V0ZXIgPSB0aGlzO1xyXG4gICAgICAgIHJldHVybiBmdW5jdGlvbiAoKSB7XHJcbiAgICAgICAgICAgIHZhciBwYXJhbXMgPSBbXTtcclxuICAgICAgICAgICAgdmFyIGNhbGxiYWNrO1xyXG4gICAgICAgICAgICBmb3IgKHZhciBqID0gMDsgaiA8IGFyZ3VtZW50cy5sZW5ndGg7IGorKykge1xyXG4gICAgICAgICAgICAgICAgaWYgKHR5cGVvZiBhcmd1bWVudHNbal0gPT09IFwiZnVuY3Rpb25cIikge1xyXG4gICAgICAgICAgICAgICAgICAgIGNhbGxiYWNrID0gYXJndW1lbnRzW2pdO1xyXG4gICAgICAgICAgICAgICAgfVxyXG4gICAgICAgICAgICAgICAgZWxzZSB7XHJcbiAgICAgICAgICAgICAgICAgICAgcGFyYW1zLnB1c2goYXJndW1lbnRzW2pdKTtcclxuICAgICAgICAgICAgICAgIH1cclxuICAgICAgICAgICAgfVxyXG4gICAgICAgICAgICByZXR1cm4gb3V0ZXIuaW52b2tlKG5hbWUsIHBhcmFtcywgY2FsbGJhY2spO1xyXG4gICAgICAgIH07XHJcbiAgICB9O1xyXG4gICAgcmV0dXJuIFNlcnZpY2VQcm94eTtcclxufSgpKTtcclxuZXhwb3J0cy5TZXJ2aWNlUHJveHkgPSBTZXJ2aWNlUHJveHk7XHJcbi8vIyBzb3VyY2VNYXBwaW5nVVJMPVNlcnZpY2VQcm94eS5qcy5tYXAiLCJcInVzZSBzdHJpY3RcIjtcclxudmFyIFNlcnZpY2VNZXNzYWdlID0gKGZ1bmN0aW9uICgpIHtcclxuICAgIGZ1bmN0aW9uIFNlcnZpY2VNZXNzYWdlKF9fY2xhc3NuYW1lKSB7XHJcbiAgICAgICAgdGhpcy5fX2NsYXNzbmFtZSA9IF9fY2xhc3NuYW1lO1xyXG4gICAgfVxyXG4gICAgcmV0dXJuIFNlcnZpY2VNZXNzYWdlO1xyXG59KCkpO1xyXG5leHBvcnRzLlNlcnZpY2VNZXNzYWdlID0gU2VydmljZU1lc3NhZ2U7XHJcbi8vIyBzb3VyY2VNYXBwaW5nVVJMPUJhc2VNZXNzYWdlLmpzLm1hcCIsIlwidXNlIHN0cmljdFwiO1xyXG52YXIgX19leHRlbmRzID0gKHRoaXMgJiYgdGhpcy5fX2V4dGVuZHMpIHx8IGZ1bmN0aW9uIChkLCBiKSB7XHJcbiAgICBmb3IgKHZhciBwIGluIGIpIGlmIChiLmhhc093blByb3BlcnR5KHApKSBkW3BdID0gYltwXTtcclxuICAgIGZ1bmN0aW9uIF9fKCkgeyB0aGlzLmNvbnN0cnVjdG9yID0gZDsgfVxyXG4gICAgZC5wcm90b3R5cGUgPSBiID09PSBudWxsID8gT2JqZWN0LmNyZWF0ZShiKSA6IChfXy5wcm90b3R5cGUgPSBiLnByb3RvdHlwZSwgbmV3IF9fKCkpO1xyXG59O1xyXG52YXIgQmFzZU1lc3NhZ2VfMSA9IHJlcXVpcmUoXCIuL0Jhc2VNZXNzYWdlXCIpO1xyXG52YXIgUGFydGlhbE1lc3NhZ2UgPSAoZnVuY3Rpb24gKF9zdXBlcikge1xyXG4gICAgX19leHRlbmRzKFBhcnRpYWxNZXNzYWdlLCBfc3VwZXIpO1xyXG4gICAgZnVuY3Rpb24gUGFydGlhbE1lc3NhZ2UoY2FsbGlkLCBkYXRhLCBudW1iZXIsIGNvdW50KSB7XHJcbiAgICAgICAgX3N1cGVyLmNhbGwodGhpcywgXCJjb20uYWN0b3Jvbi53ZWJzZXJ2aWNlLm1lc3NhZ2VzLlBhcnRpYWxNZXNzYWdlXCIpO1xyXG4gICAgICAgIHRoaXMuY2FsbGlkID0gY2FsbGlkO1xyXG4gICAgICAgIHRoaXMuZGF0YSA9IGRhdGE7XHJcbiAgICAgICAgdGhpcy5udW1iZXIgPSBudW1iZXI7XHJcbiAgICAgICAgdGhpcy5jb3VudCA9IGNvdW50O1xyXG4gICAgfVxyXG4gICAgcmV0dXJuIFBhcnRpYWxNZXNzYWdlO1xyXG59KEJhc2VNZXNzYWdlXzEuU2VydmljZU1lc3NhZ2UpKTtcclxuZXhwb3J0cy5QYXJ0aWFsTWVzc2FnZSA9IFBhcnRpYWxNZXNzYWdlO1xyXG4vLyMgc291cmNlTWFwcGluZ1VSTD1QYXJ0aWFsTWVzc2FnZS5qcy5tYXAiLCJcInVzZSBzdHJpY3RcIjtcclxudmFyIF9fZXh0ZW5kcyA9ICh0aGlzICYmIHRoaXMuX19leHRlbmRzKSB8fCBmdW5jdGlvbiAoZCwgYikge1xyXG4gICAgZm9yICh2YXIgcCBpbiBiKSBpZiAoYi5oYXNPd25Qcm9wZXJ0eShwKSkgZFtwXSA9IGJbcF07XHJcbiAgICBmdW5jdGlvbiBfXygpIHsgdGhpcy5jb25zdHJ1Y3RvciA9IGQ7IH1cclxuICAgIGQucHJvdG90eXBlID0gYiA9PT0gbnVsbCA/IE9iamVjdC5jcmVhdGUoYikgOiAoX18ucHJvdG90eXBlID0gYi5wcm90b3R5cGUsIG5ldyBfXygpKTtcclxufTtcclxudmFyIEJhc2VNZXNzYWdlXzEgPSByZXF1aXJlKFwiLi9CYXNlTWVzc2FnZVwiKTtcclxudmFyIFNlcnZpY2VJbnZvY2F0aW9uTWVzc2FnZSA9IChmdW5jdGlvbiAoX3N1cGVyKSB7XHJcbiAgICBfX2V4dGVuZHMoU2VydmljZUludm9jYXRpb25NZXNzYWdlLCBfc3VwZXIpO1xyXG4gICAgZnVuY3Rpb24gU2VydmljZUludm9jYXRpb25NZXNzYWdlKHNlcnZpY2VJZCwgbWV0aG9kTmFtZSwgcGFyYW1ldGVyVmFsdWVzKSB7XHJcbiAgICAgICAgX3N1cGVyLmNhbGwodGhpcywgXCJjb20uYWN0b3Jvbi53ZWJzZXJ2aWNlLm1lc3NhZ2VzLlNlcnZpY2VJbnZvY2F0aW9uTWVzc2FnZVwiKTtcclxuICAgICAgICB0aGlzLnNlcnZpY2VJZCA9IHNlcnZpY2VJZDtcclxuICAgICAgICB0aGlzLm1ldGhvZE5hbWUgPSBtZXRob2ROYW1lO1xyXG4gICAgICAgIHRoaXMucGFyYW1ldGVyVmFsdWVzID0gcGFyYW1ldGVyVmFsdWVzO1xyXG4gICAgfVxyXG4gICAgcmV0dXJuIFNlcnZpY2VJbnZvY2F0aW9uTWVzc2FnZTtcclxufShCYXNlTWVzc2FnZV8xLlNlcnZpY2VNZXNzYWdlKSk7XHJcbmV4cG9ydHMuU2VydmljZUludm9jYXRpb25NZXNzYWdlID0gU2VydmljZUludm9jYXRpb25NZXNzYWdlO1xyXG4vLyMgc291cmNlTWFwcGluZ1VSTD1TZXJ2aWNlSW52b2NhdGlvbk1lc3NhZ2UuanMubWFwIiwiXCJ1c2Ugc3RyaWN0XCI7XHJcbnZhciBfX2V4dGVuZHMgPSAodGhpcyAmJiB0aGlzLl9fZXh0ZW5kcykgfHwgZnVuY3Rpb24gKGQsIGIpIHtcclxuICAgIGZvciAodmFyIHAgaW4gYikgaWYgKGIuaGFzT3duUHJvcGVydHkocCkpIGRbcF0gPSBiW3BdO1xyXG4gICAgZnVuY3Rpb24gX18oKSB7IHRoaXMuY29uc3RydWN0b3IgPSBkOyB9XHJcbiAgICBkLnByb3RvdHlwZSA9IGIgPT09IG51bGwgPyBPYmplY3QuY3JlYXRlKGIpIDogKF9fLnByb3RvdHlwZSA9IGIucHJvdG90eXBlLCBuZXcgX18oKSk7XHJcbn07XHJcbnZhciBCYXNlTWVzc2FnZV8xID0gcmVxdWlyZShcIi4vQmFzZU1lc3NhZ2VcIik7XHJcbnZhciBTZXJ2aWNlUHJvdmlkZU1lc3NhZ2UgPSAoZnVuY3Rpb24gKF9zdXBlcikge1xyXG4gICAgX19leHRlbmRzKFNlcnZpY2VQcm92aWRlTWVzc2FnZSwgX3N1cGVyKTtcclxuICAgIGZ1bmN0aW9uIFNlcnZpY2VQcm92aWRlTWVzc2FnZSh0eXBlLCBzY29wZSwgdGFncykge1xyXG4gICAgICAgIF9zdXBlci5jYWxsKHRoaXMsIFwiY29tLmFjdG9yb24ud2Vic2VydmljZS5tZXNzYWdlcy5TZXJ2aWNlUHJvdmlkZU1lc3NhZ2VcIik7XHJcbiAgICAgICAgdGhpcy50eXBlID0gdHlwZTtcclxuICAgICAgICB0aGlzLnNjb3BlID0gc2NvcGU7XHJcbiAgICAgICAgdGhpcy50YWdzID0gdGFncztcclxuICAgIH1cclxuICAgIHJldHVybiBTZXJ2aWNlUHJvdmlkZU1lc3NhZ2U7XHJcbn0oQmFzZU1lc3NhZ2VfMS5TZXJ2aWNlTWVzc2FnZSkpO1xyXG5leHBvcnRzLlNlcnZpY2VQcm92aWRlTWVzc2FnZSA9IFNlcnZpY2VQcm92aWRlTWVzc2FnZTtcclxuLy8jIHNvdXJjZU1hcHBpbmdVUkw9U2VydmljZVByb3ZpZGVNZXNzYWdlLmpzLm1hcCIsIlwidXNlIHN0cmljdFwiO1xyXG52YXIgX19leHRlbmRzID0gKHRoaXMgJiYgdGhpcy5fX2V4dGVuZHMpIHx8IGZ1bmN0aW9uIChkLCBiKSB7XHJcbiAgICBmb3IgKHZhciBwIGluIGIpIGlmIChiLmhhc093blByb3BlcnR5KHApKSBkW3BdID0gYltwXTtcclxuICAgIGZ1bmN0aW9uIF9fKCkgeyB0aGlzLmNvbnN0cnVjdG9yID0gZDsgfVxyXG4gICAgZC5wcm90b3R5cGUgPSBiID09PSBudWxsID8gT2JqZWN0LmNyZWF0ZShiKSA6IChfXy5wcm90b3R5cGUgPSBiLnByb3RvdHlwZSwgbmV3IF9fKCkpO1xyXG59O1xyXG52YXIgQmFzZU1lc3NhZ2VfMSA9IHJlcXVpcmUoXCIuL0Jhc2VNZXNzYWdlXCIpO1xyXG52YXIgU2VydmljZVNlYXJjaE1lc3NhZ2UgPSAoZnVuY3Rpb24gKF9zdXBlcikge1xyXG4gICAgX19leHRlbmRzKFNlcnZpY2VTZWFyY2hNZXNzYWdlLCBfc3VwZXIpO1xyXG4gICAgZnVuY3Rpb24gU2VydmljZVNlYXJjaE1lc3NhZ2UodHlwZSwgbXVsdGlwbGUsIHNjb3BlKSB7XHJcbiAgICAgICAgX3N1cGVyLmNhbGwodGhpcywgXCJjb20uYWN0b3Jvbi53ZWJzZXJ2aWNlLm1lc3NhZ2VzLlNlcnZpY2VTZWFyY2hNZXNzYWdlXCIpO1xyXG4gICAgICAgIHRoaXMudHlwZSA9IHR5cGU7XHJcbiAgICAgICAgdGhpcy5tdWx0aXBsZSA9IG11bHRpcGxlO1xyXG4gICAgICAgIHRoaXMuc2NvcGUgPSBzY29wZTtcclxuICAgIH1cclxuICAgIHJldHVybiBTZXJ2aWNlU2VhcmNoTWVzc2FnZTtcclxufShCYXNlTWVzc2FnZV8xLlNlcnZpY2VNZXNzYWdlKSk7XHJcbmV4cG9ydHMuU2VydmljZVNlYXJjaE1lc3NhZ2UgPSBTZXJ2aWNlU2VhcmNoTWVzc2FnZTtcclxuLy8jIHNvdXJjZU1hcHBpbmdVUkw9U2VydmljZVNlYXJjaE1lc3NhZ2UuanMubWFwIiwiXCJ1c2Ugc3RyaWN0XCI7XHJcbnZhciBfX2V4dGVuZHMgPSAodGhpcyAmJiB0aGlzLl9fZXh0ZW5kcykgfHwgZnVuY3Rpb24gKGQsIGIpIHtcclxuICAgIGZvciAodmFyIHAgaW4gYikgaWYgKGIuaGFzT3duUHJvcGVydHkocCkpIGRbcF0gPSBiW3BdO1xyXG4gICAgZnVuY3Rpb24gX18oKSB7IHRoaXMuY29uc3RydWN0b3IgPSBkOyB9XHJcbiAgICBkLnByb3RvdHlwZSA9IGIgPT09IG51bGwgPyBPYmplY3QuY3JlYXRlKGIpIDogKF9fLnByb3RvdHlwZSA9IGIucHJvdG90eXBlLCBuZXcgX18oKSk7XHJcbn07XHJcbnZhciBCYXNlTWVzc2FnZV8xID0gcmVxdWlyZShcIi4vQmFzZU1lc3NhZ2VcIik7XHJcbnZhciBTZXJ2aWNlVW5wcm92aWRlTWVzc2FnZSA9IChmdW5jdGlvbiAoX3N1cGVyKSB7XHJcbiAgICBfX2V4dGVuZHMoU2VydmljZVVucHJvdmlkZU1lc3NhZ2UsIF9zdXBlcik7XHJcbiAgICBmdW5jdGlvbiBTZXJ2aWNlVW5wcm92aWRlTWVzc2FnZShzZXJ2aWNlSWQpIHtcclxuICAgICAgICBfc3VwZXIuY2FsbCh0aGlzLCBcImNvbS5hY3Rvcm9uLndlYnNlcnZpY2UubWVzc2FnZXMuU2VydmljZVVucHJvdmlkZU1lc3NhZ2VcIik7XHJcbiAgICAgICAgdGhpcy5zZXJ2aWNlSWQgPSBzZXJ2aWNlSWQ7XHJcbiAgICB9XHJcbiAgICByZXR1cm4gU2VydmljZVVucHJvdmlkZU1lc3NhZ2U7XHJcbn0oQmFzZU1lc3NhZ2VfMS5TZXJ2aWNlTWVzc2FnZSkpO1xyXG5leHBvcnRzLlNlcnZpY2VVbnByb3ZpZGVNZXNzYWdlID0gU2VydmljZVVucHJvdmlkZU1lc3NhZ2U7XHJcbi8vIyBzb3VyY2VNYXBwaW5nVVJMPVNlcnZpY2VVbnByb3ZpZGVNZXNzYWdlLmpzLm1hcCIsIlwidXNlIHN0cmljdFwiO1xyXG52YXIgQ29ubmVjdGlvbkhhbmRsZXIgPSAoZnVuY3Rpb24gKCkge1xyXG4gICAgZnVuY3Rpb24gQ29ubmVjdGlvbkhhbmRsZXIoKSB7XHJcbiAgICAgICAgLyoqIFRoZSB3ZWJzb2NrZXQgY29ubmVjdGlvbnMuICovXHJcbiAgICAgICAgdGhpcy5jb25uZWN0aW9ucyA9IFtdO1xyXG4gICAgICAgIHZhciBzY3JpcHRzID0gZG9jdW1lbnQuZ2V0RWxlbWVudHNCeVRhZ05hbWUoJ3NjcmlwdCcpO1xyXG4gICAgICAgIHZhciBzY3JpcHQgPSBzY3JpcHRzW3NjcmlwdHMubGVuZ3RoIC0gMV07XHJcbiAgICAgICAgaWYgKHNjcmlwdFtcInNyY1wiXSkge1xyXG4gICAgICAgICAgICB0aGlzLmJhc2V1cmwgPSBzY3JpcHRbXCJzcmNcIl07XHJcbiAgICAgICAgICAgIHRoaXMuYmFzZXVybCA9IFwid3NcIiArIHRoaXMuYmFzZXVybC5zdWJzdHJpbmcodGhpcy5iYXNldXJsLmluZGV4T2YoXCI6Ly9cIikpO1xyXG4gICAgICAgIH1cclxuICAgICAgICBlbHNlIGlmIChzY3JpcHQuaGFzQXR0cmlidXRlcygpKSB7XHJcbiAgICAgICAgICAgIC8vdGhpcy5iYXNldXJsID0gXCJ3czovL1wiICsgd2luZG93LmxvY2F0aW9uLmhvc3RuYW1lICsgXCI6XCIgKyB3aW5kb3cubG9jYXRpb24ucG9ydCArIFwiL3dzd2ViYXBpXCI7XHJcbiAgICAgICAgICAgIHRoaXMuYmFzZXVybCA9IFwid3M6Ly9cIiArIHdpbmRvdy5sb2NhdGlvbi5ob3N0bmFtZSArIFwiOlwiICsgd2luZG93LmxvY2F0aW9uLnBvcnQgKyBzY3JpcHQuYXR0cmlidXRlcy5nZXROYW1lZEl0ZW0oXCJzcmNcIikudmFsdWU7XHJcbiAgICAgICAgfVxyXG4gICAgICAgIGVsc2Uge1xyXG4gICAgICAgICAgICAvLyBmYWlsP1xyXG4gICAgICAgICAgICB0aHJvdyBuZXcgRXJyb3IoXCJDb3VsZCBub3QgZmluZCB3ZWJzb2NrZXQgdXJsXCIpO1xyXG4gICAgICAgIH1cclxuICAgICAgICB0aGlzLmJhc2V1cmwgPSB0aGlzLmJhc2V1cmwuc3Vic3RyaW5nKDAsIHRoaXMuYmFzZXVybC5sYXN0SW5kZXhPZihcImphZGV4LmpzXCIpIC0gMSk7XHJcbiAgICAgICAgdGhpcy5jb25uZWN0aW9uc1tcIlwiXSA9IHRoaXMuYWRkQ29ubmVjdGlvbih0aGlzLmJhc2V1cmwpO1xyXG4gICAgICAgIC8vdGhpcy5jb25uZWN0aW9uc1t1bmRlZmluZWRdID0gdGhpcy5jb25uZWN0aW9uc1tudWxsXTtcclxuICAgIH1cclxuICAgIC8qKlxyXG4gICAgICogIEludGVybmFsIGZ1bmN0aW9uIHRvIGdldCBhIHdlYiBzb2NrZXQgZm9yIGEgdXJsLlxyXG4gICAgICovXHJcbiAgICBDb25uZWN0aW9uSGFuZGxlci5wcm90b3R5cGUuZ2V0Q29ubmVjdGlvbiA9IGZ1bmN0aW9uICh1cmwpIHtcclxuICAgICAgICBpZiAodXJsID09IG51bGwpXHJcbiAgICAgICAgICAgIHVybCA9IFwiXCI7XHJcbiAgICAgICAgdmFyIHJldCA9IHRoaXMuY29ubmVjdGlvbnNbdXJsXTtcclxuICAgICAgICBpZiAocmV0ICE9IG51bGwpIHtcclxuICAgICAgICAgICAgcmV0dXJuIHJldDtcclxuICAgICAgICB9XHJcbiAgICAgICAgZWxzZSB7XHJcbiAgICAgICAgICAgIHJldHVybiB0aGlzLmFkZENvbm5lY3Rpb24odXJsKTtcclxuICAgICAgICB9XHJcbiAgICB9O1xyXG4gICAgO1xyXG4gICAgLyoqXHJcbiAgICAgKiAgQWRkIGEgbmV3IHNlcnZlciBjb25uZWN0aW9uLlxyXG4gICAgICogIEBwYXJhbSB1cmwgVGhlIHVybC5cclxuICAgICAqL1xyXG4gICAgQ29ubmVjdGlvbkhhbmRsZXIucHJvdG90eXBlLmFkZENvbm5lY3Rpb24gPSBmdW5jdGlvbiAodXJsKSB7XHJcbiAgICAgICAgdmFyIF90aGlzID0gdGhpcztcclxuICAgICAgICB0aGlzLmNvbm5lY3Rpb25zW3VybF0gPSBuZXcgUHJvbWlzZShmdW5jdGlvbiAocmVzb2x2ZSwgcmVqZWN0KSB7XHJcbiAgICAgICAgICAgIHRyeSB7XHJcbiAgICAgICAgICAgICAgICB2YXIgd3NfMSA9IG5ldyBXZWJTb2NrZXQodXJsKTtcclxuICAgICAgICAgICAgICAgIHdzXzEub25vcGVuID0gZnVuY3Rpb24gKCkge1xyXG4gICAgICAgICAgICAgICAgICAgIHJlc29sdmUod3NfMSk7XHJcbiAgICAgICAgICAgICAgICB9O1xyXG4gICAgICAgICAgICAgICAgd3NfMS5vbm1lc3NhZ2UgPSBmdW5jdGlvbiAobWVzc2FnZSkge1xyXG4gICAgICAgICAgICAgICAgICAgIF90aGlzLm9uTWVzc2FnZShtZXNzYWdlLCB1cmwpO1xyXG4gICAgICAgICAgICAgICAgfTtcclxuICAgICAgICAgICAgfVxyXG4gICAgICAgICAgICBjYXRjaCAoZSkge1xyXG4gICAgICAgICAgICAgICAgcmVqZWN0KGUpO1xyXG4gICAgICAgICAgICB9XHJcbiAgICAgICAgfSk7XHJcbiAgICAgICAgcmV0dXJuIHRoaXMuY29ubmVjdGlvbnNbdXJsXTtcclxuICAgIH07XHJcbiAgICA7XHJcbiAgICAvKipcclxuICAgICAqICBTZW5kIGEgbWVzc2FnZSB0byB0aGUgc2VydmVyIGFuZCBjcmVhdGUgYSBjYWxsaWQgZm9yIHRoZSBhbnN3ZXIgbWVzc2FnZS5cclxuICAgICAqL1xyXG4gICAgQ29ubmVjdGlvbkhhbmRsZXIucHJvdG90eXBlLnNlbmREYXRhID0gZnVuY3Rpb24gKHVybCwgZGF0YSkge1xyXG4gICAgICAgIHRoaXMuZ2V0Q29ubmVjdGlvbih1cmwpLnRoZW4oZnVuY3Rpb24gKHdzKSB7XHJcbiAgICAgICAgICAgIHdzLnNlbmQoZGF0YSk7XHJcbiAgICAgICAgfSk7XHJcbiAgICB9O1xyXG4gICAgLyoqXHJcbiAgICAgKiAgU2VuZCBhIG1lc3NhZ2UgdG8gdGhlIHNlcnZlciBpbiBhbiBvbmdvaW5nIGNvbnZlcnNhdGlvbi5cclxuICAgICAqL1xyXG4gICAgQ29ubmVjdGlvbkhhbmRsZXIucHJvdG90eXBlLnNlbmRDb252ZXJzYXRpb25NZXNzYWdlID0gZnVuY3Rpb24gKHVybCwgY21kKSB7XHJcbiAgICAgICAgdGhpcy5nZXRDb25uZWN0aW9uKHVybCkudGhlbihmdW5jdGlvbiAod3MpIHtcclxuICAgICAgICAgICAgd3Muc2VuZChKU09OLnN0cmluZ2lmeShjbWQpKTtcclxuICAgICAgICB9KTtcclxuICAgIH07XHJcbiAgICA7XHJcbiAgICByZXR1cm4gQ29ubmVjdGlvbkhhbmRsZXI7XHJcbn0oKSk7XHJcbmV4cG9ydHMuQ29ubmVjdGlvbkhhbmRsZXIgPSBDb25uZWN0aW9uSGFuZGxlcjtcclxuLy8jIHNvdXJjZU1hcHBpbmdVUkw9Q29ubmVjdGlvbkhhbmRsZXIuanMubWFwIiwiXCJ1c2Ugc3RyaWN0XCI7XHJcbnZhciBfX2V4dGVuZHMgPSAodGhpcyAmJiB0aGlzLl9fZXh0ZW5kcykgfHwgZnVuY3Rpb24gKGQsIGIpIHtcclxuICAgIGZvciAodmFyIHAgaW4gYikgaWYgKGIuaGFzT3duUHJvcGVydHkocCkpIGRbcF0gPSBiW3BdO1xyXG4gICAgZnVuY3Rpb24gX18oKSB7IHRoaXMuY29uc3RydWN0b3IgPSBkOyB9XHJcbiAgICBkLnByb3RvdHlwZSA9IGIgPT09IG51bGwgPyBPYmplY3QuY3JlYXRlKGIpIDogKF9fLnByb3RvdHlwZSA9IGIucHJvdG90eXBlLCBuZXcgX18oKSk7XHJcbn07XHJcbnZhciBDb25uZWN0aW9uSGFuZGxlcl8xID0gcmVxdWlyZShcIi4vQ29ubmVjdGlvbkhhbmRsZXJcIik7XHJcbnZhciBKc29uUGFyc2VyXzEgPSByZXF1aXJlKFwiLi4vSnNvblBhcnNlclwiKTtcclxudmFyIFNVdGlsXzEgPSByZXF1aXJlKFwiLi4vU1V0aWxcIik7XHJcbnZhciBXZWJzb2NrZXRDYWxsXzEgPSByZXF1aXJlKFwiLi9XZWJzb2NrZXRDYWxsXCIpO1xyXG52YXIgUGFydGlhbE1lc3NhZ2VfMSA9IHJlcXVpcmUoXCIuLi9tZXNzYWdlcy9QYXJ0aWFsTWVzc2FnZVwiKTtcclxuLyoqXHJcbiAqXHJcbiAqL1xyXG52YXIgSmFkZXhDb25uZWN0aW9uSGFuZGxlciA9IChmdW5jdGlvbiAoX3N1cGVyKSB7XHJcbiAgICBfX2V4dGVuZHMoSmFkZXhDb25uZWN0aW9uSGFuZGxlciwgX3N1cGVyKTtcclxuICAgIGZ1bmN0aW9uIEphZGV4Q29ubmVjdGlvbkhhbmRsZXIoKSB7XHJcbiAgICAgICAgX3N1cGVyLmFwcGx5KHRoaXMsIGFyZ3VtZW50cyk7XHJcbiAgICAgICAgLyoqIFRoZSBtYXAgb2Ygb3BlbiBvdXRjYWxscy4gKi9cclxuICAgICAgICB0aGlzLm91dGNhbGxzID0gW107XHJcbiAgICAgICAgLy9cdC8qKiBUaGUgbWFwIG9mIG9wZW4gaW5jb21pbmcgY2FsbHMuICovXHJcbiAgICAgICAgLy9cdHZhciBpbmNhbGxzID0gW107XHJcbiAgICAgICAgLyoqIFRoZSBtYXAgb2YgcHJvdmlkZWQgc2VydmljZXMgKHNpZCAtPiBzZXJ2aWNlIGludm9jYXRpb24gZnVuY3Rpb24pLiAqL1xyXG4gICAgICAgIHRoaXMucHJvdmlkZWRTZXJ2aWNlcyA9IFtdO1xyXG4gICAgfVxyXG4gICAgLyoqXHJcbiAgICAgKiAgR2V0IHRoZSBpbnN0YW5jZS5cclxuICAgICAqL1xyXG4gICAgSmFkZXhDb25uZWN0aW9uSGFuZGxlci5nZXRJbnN0YW5jZSA9IGZ1bmN0aW9uICgpIHtcclxuICAgICAgICByZXR1cm4gSmFkZXhDb25uZWN0aW9uSGFuZGxlci5JTlNUQU5DRTtcclxuICAgIH07XHJcbiAgICAvKipcclxuICAgICAqICBTZW5kIGEgbWVzc2FnZSB0byB0aGUgc2VydmVyIGFuZCBjcmVhdGUgYSBjYWxsaWQgZm9yIHRoZSBhbnN3ZXIgbWVzc2FnZS5cclxuICAgICAqL1xyXG4gICAgSmFkZXhDb25uZWN0aW9uSGFuZGxlci5wcm90b3R5cGUuc2VuZE1lc3NhZ2UgPSBmdW5jdGlvbiAodXJsLCBjbWQsIHR5cGUsIHJlc29sdmUsIHJlamVjdCwgY2FsbGJhY2spIHtcclxuICAgICAgICAvLyB0b2RvOiB1c2UgSmFkZXggYmluYXJ5IHRvIHNlcmlhbGl6ZSBtZXNzYWdlIGFuZCBzZW5kXHJcbiAgICAgICAgdmFyIGNhbGxpZCA9IHRoaXMucmFuZG9tU3RyaW5nKC0xKTtcclxuICAgICAgICB0aGlzLm91dGNhbGxzW2NhbGxpZF0gPSBuZXcgV2Vic29ja2V0Q2FsbF8xLldlYnNvY2tldENhbGwodHlwZSwgcmVzb2x2ZSwgcmVqZWN0LCBjYWxsYmFjayk7XHJcbiAgICAgICAgY21kLmNhbGxpZCA9IGNhbGxpZDtcclxuICAgICAgICB0aGlzLnNlbmRSYXdNZXNzYWdlKHVybCwgY21kKTtcclxuICAgICAgICByZXR1cm4gY2FsbGlkO1xyXG4gICAgfTtcclxuICAgIDtcclxuICAgIC8qKlxyXG4gICAgICogIFNlbmQgYSByYXcgbWVzc2FnZSB3aXRob3V0IGNhbGxpZCBtYW5hZ2VtZW50LlxyXG4gICAgICovXHJcbiAgICBKYWRleENvbm5lY3Rpb25IYW5kbGVyLnByb3RvdHlwZS5zZW5kUmF3TWVzc2FnZSA9IGZ1bmN0aW9uICh1cmwsIGNtZCkge1xyXG4gICAgICAgIGlmICghY21kLmNhbGxpZClcclxuICAgICAgICAgICAgY29uc29sZS5sb2coXCJTZW5kaW5nIG1lc3NhZ2Ugd2l0aG91dCBjYWxsaWQ6IFwiICsgY21kKTtcclxuICAgICAgICB2YXIgZGF0YSA9IHRoaXMub2JqZWN0VG9Kc29uKGNtZCk7XHJcbiAgICAgICAgLy9jb25zb2xlLmxvZyhkYXRhKTtcclxuICAgICAgICAvL2xldCBzaXplID0gc2l6ZU9mKGNtZCk7XHJcbiAgICAgICAgdmFyIHNpemUgPSBkYXRhLmxlbmd0aDtcclxuICAgICAgICB2YXIgbGltaXQgPSA3MDAwOyAvLyA4MTkyXHJcbiAgICAgICAgLy8gSWYgbWVzc2FnZSBpcyBsYXJnZXIgdGhhbiBsaW1pdCBzbGljZSB0aGUgbWVzc2FnZSB2aWEgcGFydGlhbCBtZXNzYWdlc1xyXG4gICAgICAgIGlmIChzaXplID4gbGltaXQpIHtcclxuICAgICAgICAgICAgdmFyIGNudCA9IE1hdGguY2VpbChzaXplIC8gbGltaXQpO1xyXG4gICAgICAgICAgICBmb3IgKHZhciBpID0gMDsgaSA8IGNudDsgaSsrKSB7XHJcbiAgICAgICAgICAgICAgICB2YXIgcGFydCA9IGRhdGEuc3Vic3RyaW5nKGkgKiBsaW1pdCwgKGkgKyAxKSAqIGxpbWl0KTtcclxuICAgICAgICAgICAgICAgIHZhciBwY21kID0gbmV3IFBhcnRpYWxNZXNzYWdlXzEuUGFydGlhbE1lc3NhZ2UoY21kLmNhbGxpZCwgcGFydCwgaSwgY250KTtcclxuICAgICAgICAgICAgICAgIHZhciBwZGF0YSA9IEpTT04uc3RyaW5naWZ5KHBjbWQpO1xyXG4gICAgICAgICAgICAgICAgLy9jb25zb2xlLmxvZyhcInNlbmRpbmcgcGFydCwgc2l6ZTogXCIrcGRhdGEubGVuZ3RoKTtcclxuICAgICAgICAgICAgICAgIHRoaXMuc2VuZERhdGEodXJsLCBwZGF0YSk7XHJcbiAgICAgICAgICAgIH1cclxuICAgICAgICB9XHJcbiAgICAgICAgZWxzZSB7XHJcbiAgICAgICAgICAgIHRoaXMuc2VuZERhdGEodXJsLCBkYXRhKTtcclxuICAgICAgICB9XHJcbiAgICB9O1xyXG4gICAgLyoqXHJcbiAgICAgKiAgQ29udmVydCBhbiBvYmplY3QgdG8ganNvbi5cclxuICAgICAqICBTaW1pbGFyIHRvIEpTT04uc3RyaW5naWZ5IGJ1dCBjYW4gaGFuZGxlXHJcbiAgICAgKiAgYmluYXJ5IG9iamVjdHMgYXMgYmFzZSA2NCBzdHJpbmdzLlxyXG4gICAgICogIEBwYXJhbSBvYmplY3QgVGhlIG9iamVjdC5cclxuICAgICAqICBAcmV0dXJuIFRoZSBqc29uIHN0cmluZy5cclxuICAgICAqL1xyXG4gICAgSmFkZXhDb25uZWN0aW9uSGFuZGxlci5wcm90b3R5cGUub2JqZWN0VG9Kc29uID0gZnVuY3Rpb24gKG9iamVjdCkge1xyXG4gICAgICAgIHZhciByZXBsYWNlciA9IGZ1bmN0aW9uIChrZXksIHZhbHVlKSB7XHJcbiAgICAgICAgICAgIGlmICh2YWx1ZSBpbnN0YW5jZW9mIEFycmF5QnVmZmVyKSB7XHJcbiAgICAgICAgICAgICAgICAvL2xldCByZXQgPSB3aW5kb3cuYnRvYSh2YWx1ZSk7XHJcbiAgICAgICAgICAgICAgICB2YXIgcmV0ID0gYnRvYShTdHJpbmcuZnJvbUNoYXJDb2RlLmFwcGx5KG51bGwsIG5ldyBVaW50OEFycmF5KHZhbHVlKSkpO1xyXG4gICAgICAgICAgICAgICAgcmV0dXJuIHJldDtcclxuICAgICAgICAgICAgfVxyXG4gICAgICAgICAgICBlbHNlIHtcclxuICAgICAgICAgICAgICAgIHJldHVybiB2YWx1ZTtcclxuICAgICAgICAgICAgfVxyXG4gICAgICAgICAgICAvL3JldHVybiB2YWx1ZSBpbnN0YW5jZW9mIEFycmF5QnVmZmVyPyB3aW5kb3cuYnRvYSh2YWx1ZSk6IHZhbHVlO1xyXG4gICAgICAgIH07XHJcbiAgICAgICAgcmV0dXJuIEpTT04uc3RyaW5naWZ5KG9iamVjdCwgcmVwbGFjZXIpO1xyXG4gICAgfTtcclxuICAgIC8qKlxyXG4gICAgICogIFNlbmQgYSByZXN1bHQuXHJcbiAgICAgKi9cclxuICAgIEphZGV4Q29ubmVjdGlvbkhhbmRsZXIucHJvdG90eXBlLnNlbmRSZXN1bHQgPSBmdW5jdGlvbiAodXJsLCByZXN1bHQsIGZpbmlzaGVkLCBjYWxsaWQpIHtcclxuICAgICAgICB2YXIgY21kID0ge1xyXG4gICAgICAgICAgICBfX2NsYXNzbmFtZTogXCJjb20uYWN0b3Jvbi53ZWJzZXJ2aWNlLm1lc3NhZ2VzLlJlc3VsdE1lc3NhZ2VcIixcclxuICAgICAgICAgICAgY2FsbGlkOiBjYWxsaWQsXHJcbiAgICAgICAgICAgIHJlc3VsdDogcmVzdWx0LFxyXG4gICAgICAgICAgICBmaW5pc2hlZDogZmluaXNoZWRcclxuICAgICAgICB9O1xyXG4gICAgICAgIHRoaXMuc2VuZFJhd01lc3NhZ2UodXJsLCBjbWQpO1xyXG4gICAgfTtcclxuICAgIC8qKlxyXG4gICAgICogIFNlbmQgYW4gZXhjZXB0aW9uLlxyXG4gICAgICovXHJcbiAgICBKYWRleENvbm5lY3Rpb25IYW5kbGVyLnByb3RvdHlwZS5zZW5kRXhjZXB0aW9uID0gZnVuY3Rpb24gKHVybCwgZXJyLCBmaW5pc2hlZCwgY2FsbGlkKSB7XHJcbiAgICAgICAgdmFyIGV4Y2VwdGlvbiA9IHtcclxuICAgICAgICAgICAgX19jbGFzc25hbWU6IFwiamF2YS5sYW5nLlJ1bnRpbWVFeGNlcHRpb25cIixcclxuICAgICAgICAgICAgbWVzc2FnZTogXCJcIiArIGVyclxyXG4gICAgICAgIH07XHJcbiAgICAgICAgdmFyIGNtZCA9IHtcclxuICAgICAgICAgICAgX19jbGFzc25hbWU6IFwiY29tLmFjdG9yb24ud2Vic2VydmljZS5tZXNzYWdlcy5SZXN1bHRNZXNzYWdlXCIsXHJcbiAgICAgICAgICAgIGNhbGxpZDogY2FsbGlkLFxyXG4gICAgICAgICAgICBleGNlcHRpb246IGV4Y2VwdGlvbixcclxuICAgICAgICAgICAgZmluaXNoZWQ6IGZpbmlzaGVkXHJcbiAgICAgICAgfTtcclxuICAgICAgICB0aGlzLnNlbmRSYXdNZXNzYWdlKHVybCwgY21kKTtcclxuICAgIH07XHJcbiAgICAvKipcclxuICAgICAqICBDYWxsZWQgd2hlbiBhIG1lc3NhZ2UgYXJyaXZlcy5cclxuICAgICAqL1xyXG4gICAgSmFkZXhDb25uZWN0aW9uSGFuZGxlci5wcm90b3R5cGUub25NZXNzYWdlID0gZnVuY3Rpb24gKG1lc3NhZ2UsIHVybCkge1xyXG4gICAgICAgIHZhciBfdGhpcyA9IHRoaXM7XHJcbiAgICAgICAgaWYgKG1lc3NhZ2UudHlwZSA9PSBcIm1lc3NhZ2VcIikge1xyXG4gICAgICAgICAgICB2YXIgbXNnXzEgPSBKc29uUGFyc2VyXzEuSnNvblBhcnNlci5wYXJzZShtZXNzYWdlLmRhdGEsIHVybCk7XHJcbiAgICAgICAgICAgIHZhciBvdXRDYWxsID0gdGhpcy5vdXRjYWxsc1ttc2dfMS5jYWxsaWRdO1xyXG4gICAgICAgICAgICAvL1x0XHQgICAgY29uc29sZS5sb2coXCJvdXRjYWxsczogXCIrb3V0Y2FsbHMpO1xyXG4gICAgICAgICAgICBpZiAob3V0Q2FsbCAhPSBudWxsKSB7XHJcbiAgICAgICAgICAgICAgICBpZiAoU1V0aWxfMS5TVXRpbC5pc1RydWUobXNnXzEuZmluaXNoZWQpKSB7XHJcbiAgICAgICAgICAgICAgICAgICAgZGVsZXRlIHRoaXMub3V0Y2FsbHNbbXNnXzEuY2FsbGlkXTtcclxuICAgICAgICAgICAgICAgICAgICAvL1x0XHRcdFx0XHRjb25zb2xlLmxvZyhcIm91dENhbGwgZGVsZXRlZDogXCIrbXNnLmNhbGxpZCk7XHJcbiAgICAgICAgICAgICAgICAgICAgb3V0Q2FsbC5maW5pc2hlZCA9IHRydWU7XHJcbiAgICAgICAgICAgICAgICB9XHJcbiAgICAgICAgICAgICAgICBpZiAob3V0Q2FsbC50eXBlID09IFwic2VhcmNoXCIpIHtcclxuICAgICAgICAgICAgICAgICAgICBpZiAobXNnXzEucmVzdWx0ICE9IG51bGwpIHtcclxuICAgICAgICAgICAgICAgICAgICAgICAgaWYgKG1zZ18xLnJlc3VsdC5oYXNPd25Qcm9wZXJ0eShcIl9fYXJyYXlcIikpXHJcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICBtc2dfMS5yZXN1bHQgPSBtc2dfMS5yZXN1bHQuX19hcnJheTtcclxuICAgICAgICAgICAgICAgICAgICAgICAgaWYgKG1zZ18xLnJlc3VsdC5oYXNPd25Qcm9wZXJ0eShcIl9fY29sbGVjdGlvblwiKSlcclxuICAgICAgICAgICAgICAgICAgICAgICAgICAgIG1zZ18xLnJlc3VsdFsxXSA9IG1zZ18xLnJlc3VsdFsxXS5fX2NvbGxlY3Rpb247XHJcbiAgICAgICAgICAgICAgICAgICAgfVxyXG4gICAgICAgICAgICAgICAgICAgIHZhciBzZXJwcm94eSA9IHZvaWQgMDtcclxuICAgICAgICAgICAgICAgICAgICBpZiAobXNnXzEuZXhjZXB0aW9uID09IG51bGwgJiYgbXNnXzEucmVzdWx0ICE9IG51bGwpIHtcclxuICAgICAgICAgICAgICAgICAgICAgICAgc2VycHJveHkgPSBtc2dfMS5yZXN1bHQ7IC8vY3JlYXRlU2VydmljZVByb3h5KG1zZy5yZXN1bHRbMF0sIG1zZy5yZXN1bHRbMV0pO1xyXG4gICAgICAgICAgICAgICAgICAgIH1cclxuICAgICAgICAgICAgICAgICAgICBvdXRDYWxsLnJlc3VtZShzZXJwcm94eSwgbXNnXzEuZXhjZXB0aW9uKTtcclxuICAgICAgICAgICAgICAgIH1cclxuICAgICAgICAgICAgICAgIGVsc2UgaWYgKG91dENhbGwudHlwZSA9PSBcImludm9rZVwiKSB7XHJcbiAgICAgICAgICAgICAgICAgICAgb3V0Q2FsbC5yZXN1bWUobXNnXzEucmVzdWx0LCBtc2dfMS5leGNlcHRpb24pO1xyXG4gICAgICAgICAgICAgICAgfVxyXG4gICAgICAgICAgICAgICAgZWxzZSBpZiAob3V0Q2FsbC50eXBlID09IFwicHJvdmlkZVwiKSB7XHJcbiAgICAgICAgICAgICAgICAgICAgaWYgKG1zZ18xLmV4Y2VwdGlvbiAhPSBudWxsKSB7XHJcbiAgICAgICAgICAgICAgICAgICAgICAgIG91dENhbGwucmVqZWN0KG1zZ18xLmV4Y2VwdGlvbik7XHJcbiAgICAgICAgICAgICAgICAgICAgfVxyXG4gICAgICAgICAgICAgICAgICAgIGVsc2Uge1xyXG4gICAgICAgICAgICAgICAgICAgICAgICAvLyBTYXZlIHRoZSBzZXJ2aWNlIGZ1bmN0aW9uYWxpdHkgaW4gdGhlIGluY2FcclxuICAgICAgICAgICAgICAgICAgICAgICAgdGhpcy5wcm92aWRlZFNlcnZpY2VzW1NVdGlsXzEuU1V0aWwuZ2V0U2VydmljZUlkQXNTdHJpbmcobXNnXzEucmVzdWx0KV0gPSBvdXRDYWxsLmNiO1xyXG4gICAgICAgICAgICAgICAgICAgICAgICBvdXRDYWxsLnJlc29sdmUobXNnXzEucmVzdWx0KTtcclxuICAgICAgICAgICAgICAgICAgICB9XHJcbiAgICAgICAgICAgICAgICB9XHJcbiAgICAgICAgICAgICAgICBlbHNlIGlmIChvdXRDYWxsLnR5cGUgPT0gXCJ1bnByb3ZpZGVcIikge1xyXG4gICAgICAgICAgICAgICAgICAgIGlmIChtc2dfMS5leGNlcHRpb24gIT0gbnVsbCkge1xyXG4gICAgICAgICAgICAgICAgICAgICAgICBvdXRDYWxsLnJlamVjdChtc2dfMS5leGNlcHRpb24pO1xyXG4gICAgICAgICAgICAgICAgICAgIH1cclxuICAgICAgICAgICAgICAgICAgICBlbHNlIHtcclxuICAgICAgICAgICAgICAgICAgICAgICAgLy8gcmVtb3ZlUHJvcGVydHk/IVxyXG4gICAgICAgICAgICAgICAgICAgICAgICB0aGlzLnByb3ZpZGVkU2VydmljZXNbU1V0aWxfMS5TVXRpbC5nZXRTZXJ2aWNlSWRBc1N0cmluZyhtc2dfMS5yZXN1bHQpXSA9IG51bGw7XHJcbiAgICAgICAgICAgICAgICAgICAgICAgIG91dENhbGwucmVzb2x2ZShtc2dfMS5yZXN1bHQpO1xyXG4gICAgICAgICAgICAgICAgICAgIH1cclxuICAgICAgICAgICAgICAgIH1cclxuICAgICAgICAgICAgfVxyXG4gICAgICAgICAgICBlbHNlIHtcclxuICAgICAgICAgICAgICAgIGlmIChtc2dfMS5fX2NsYXNzbmFtZSA9PT0gXCJjb20uYWN0b3Jvbi53ZWJzZXJ2aWNlLm1lc3NhZ2VzLlNlcnZpY2VJbnZvY2F0aW9uTWVzc2FnZVwiKSB7XHJcbiAgICAgICAgICAgICAgICAgICAgdmFyIHNlcnZpY2UgPSB0aGlzLnByb3ZpZGVkU2VydmljZXNbU1V0aWxfMS5TVXRpbC5nZXRTZXJ2aWNlSWRBc1N0cmluZyhtc2dfMS5zZXJ2aWNlSWQpXTtcclxuICAgICAgICAgICAgICAgICAgICBpZiAoc2VydmljZSkge1xyXG4gICAgICAgICAgICAgICAgICAgICAgICB2YXIgcmVzO1xyXG4gICAgICAgICAgICAgICAgICAgICAgICAvLyBJZiBpdCBhIHNlcnZpY2Ugb2JqZWN0IHdpdGggZnVuY3Rpb25zIG9yIGp1c3QgYSBmdW5jdGlvblxyXG4gICAgICAgICAgICAgICAgICAgICAgICBpZiAoc2VydmljZVttc2dfMS5tZXRob2ROYW1lXSkge1xyXG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgLy9yZXMgPSBzZXJ2aWNlW21zZy5tZXRob2ROYW1lXShtc2cucGFyYW1ldGVyVmFsdWVzKTtcclxuICAgICAgICAgICAgICAgICAgICAgICAgICAgIHJlcyA9IHNlcnZpY2VbbXNnXzEubWV0aG9kTmFtZV0uYXBwbHkodW5kZWZpbmVkLCBtc2dfMS5wYXJhbWV0ZXJWYWx1ZXMpO1xyXG4gICAgICAgICAgICAgICAgICAgICAgICB9XHJcbiAgICAgICAgICAgICAgICAgICAgICAgIGVsc2UgaWYgKHR5cGVvZiByZXMgPT09IFwiZnVuY3Rpb25cIikge1xyXG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgLy9yZXMgPSBzZXJ2aWNlKG1zZy5wYXJhbWV0ZXJWYWx1ZXMpO1xyXG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgcmVzID0gc2VydmljZS5hcHBseSh1bmRlZmluZWQsIG1zZ18xLnBhcmFtZXRlclZhbHVlcyk7XHJcbiAgICAgICAgICAgICAgICAgICAgICAgIH1cclxuICAgICAgICAgICAgICAgICAgICAgICAgZWxzZSBpZiAoc2VydmljZS5pbnZva2UpIHtcclxuICAgICAgICAgICAgICAgICAgICAgICAgICAgIHJlcyA9IHNlcnZpY2UuaW52b2tlKG1zZ18xLm1ldGhvZE5hbWUsIG1zZ18xLnBhcmFtZXRlclZhbHVlcyk7XHJcbiAgICAgICAgICAgICAgICAgICAgICAgIH1cclxuICAgICAgICAgICAgICAgICAgICAgICAgZWxzZSB7XHJcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICBjb25zb2xlLmxvZyhcIkNhbm5vdCBpbnZva2Ugc2VydmljZSBtZXRob2QgKG5vdCBmb3VuZCk6IFwiICsgbXNnXzEubWV0aG9kTmFtZSk7XHJcbiAgICAgICAgICAgICAgICAgICAgICAgIH1cclxuICAgICAgICAgICAgICAgICAgICAgICAgLy8gSGFjaywgc2VlbXMgdG8gbG9vc2UgdGhpcyBpbiBjYWxsYmFjayA6LSggXHJcbiAgICAgICAgICAgICAgICAgICAgICAgIC8vXHRcdFx0XHRcdFx0dmFyIGZ0aGlzID0gdGhpcztcclxuICAgICAgICAgICAgICAgICAgICAgICAgLy8gTWFrZSBhbnl0aGluZyB0aGF0IGNvbWVzIGJhY2sgdG8gYSBwcm9taXNlXHJcbiAgICAgICAgICAgICAgICAgICAgICAgIC8vICAgICAgICAgICAgICAgICAgICAgICAgUHJvbWlzZS5yZXNvbHZlKHJlcykudGhlbihmdW5jdGlvbihyZXMpXHJcbiAgICAgICAgICAgICAgICAgICAgICAgIC8vICAgICAgICAgICAgICAgICAgICAgICAge1xyXG4gICAgICAgICAgICAgICAgICAgICAgICAvLyAgICAgICAgICAgICAgICAgICAgICAgICAgICBmdGhpcy5zZW5kUmVzdWx0KHVybCwgcmVzLCB0cnVlLCBtc2cuY2FsbGlkKTtcclxuICAgICAgICAgICAgICAgICAgICAgICAgLy8gICAgICAgICAgICAgICAgICAgICAgICB9KVxyXG4gICAgICAgICAgICAgICAgICAgICAgICAvLyAgICAgICAgICAgICAgICAgICAgICAgIC5jYXRjaChmdW5jdGlvbihlKVxyXG4gICAgICAgICAgICAgICAgICAgICAgICAvLyAgICAgICAgICAgICAgICAgICAgICAgIHtcclxuICAgICAgICAgICAgICAgICAgICAgICAgLy8gICAgICAgICAgICAgICAgICAgICAgICAgICAgZnRoaXMuc2VuZEV4Y2VwdGlvbih1cmwsIGUsIHRydWUsIG1zZy5jYWxsaWQpO1xyXG4gICAgICAgICAgICAgICAgICAgICAgICAvLyAgICAgICAgICAgICAgICAgICAgICAgIH0pO1xyXG4gICAgICAgICAgICAgICAgICAgICAgICBQcm9taXNlLnJlc29sdmUocmVzKS50aGVuKGZ1bmN0aW9uIChyZXMpIHtcclxuICAgICAgICAgICAgICAgICAgICAgICAgICAgIF90aGlzLnNlbmRSZXN1bHQodXJsLCByZXMsIHRydWUsIG1zZ18xLmNhbGxpZCk7XHJcbiAgICAgICAgICAgICAgICAgICAgICAgIH0pXHJcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICAuY2F0Y2goZnVuY3Rpb24gKGUpIHtcclxuICAgICAgICAgICAgICAgICAgICAgICAgICAgIF90aGlzLnNlbmRFeGNlcHRpb24odXJsLCBlLCB0cnVlLCBtc2dfMS5jYWxsaWQpO1xyXG4gICAgICAgICAgICAgICAgICAgICAgICB9KTtcclxuICAgICAgICAgICAgICAgICAgICB9XHJcbiAgICAgICAgICAgICAgICAgICAgZWxzZSB7XHJcbiAgICAgICAgICAgICAgICAgICAgICAgIGNvbnNvbGUubG9nKFwiUHJvdmlkZWQgc2VydmljZSBub3QgZm91bmQ6IFwiICsgW21zZ18xLnNlcnZpY2VJZF0pO1xyXG4gICAgICAgICAgICAgICAgICAgICAgICB0aGlzLnNlbmRFeGNlcHRpb24odXJsLCBcIlByb3ZpZGVkIHNlcnZpY2Ugbm90IGZvdW5kOiBcIiArIFttc2dfMS5zZXJ2aWNlSWRdLCB0cnVlLCBtc2dfMS5jYWxsaWQpO1xyXG4gICAgICAgICAgICAgICAgICAgIH1cclxuICAgICAgICAgICAgICAgIH1cclxuICAgICAgICAgICAgICAgIGVsc2Uge1xyXG4gICAgICAgICAgICAgICAgICAgIGNvbnNvbGUubG9nKFwiUmVjZWl2ZWQgbWVzc2FnZSB3aXRob3V0IHJlcXVlc3Q6IFwiICsgbXNnXzEpO1xyXG4gICAgICAgICAgICAgICAgfVxyXG4gICAgICAgICAgICB9XHJcbiAgICAgICAgfVxyXG4gICAgICAgIGVsc2UgaWYgKG1lc3NhZ2UudHlwZSA9PSBcImJpbmFyeVwiKSB7XHJcbiAgICAgICAgICAgIGNvbnNvbGUubG9nKFwiQmluYXJ5IG1lc3NhZ2VzIGN1cnJlbnRseSBub3Qgc3VwcG9ydGVkXCIpO1xyXG4gICAgICAgIH1cclxuICAgICAgICAvLyBlbHNlOiBkbyBub3QgaGFuZGxlIHBvbmcgbWVzc2FnZXNcclxuICAgIH07XHJcbiAgICAvKipcclxuICAgICAqICBDcmVhdGUgYSByYW5kb20gc3RyaW5nLlxyXG4gICAgICogIEBwYXJhbSBsZW5ndGggVGhlIGxlbmd0aCBvZiB0aGUgc3RyaW5nLlxyXG4gICAgICogIEByZXR1cm5zIFRoZSByYW5kb20gc3RyaW5nLlxyXG4gICAgICovXHJcbiAgICBKYWRleENvbm5lY3Rpb25IYW5kbGVyLnByb3RvdHlwZS5yYW5kb21TdHJpbmcgPSBmdW5jdGlvbiAobGVuZ3RoKSB7XHJcbiAgICAgICAgaWYgKGxlbmd0aCA8IDEpXHJcbiAgICAgICAgICAgIGxlbmd0aCA9IDEwO1xyXG4gICAgICAgIHJldHVybiBNYXRoLnJvdW5kKChNYXRoLnBvdygzNiwgbGVuZ3RoICsgMSkgLSBNYXRoLnJhbmRvbSgpICogTWF0aC5wb3coMzYsIGxlbmd0aCkpKS50b1N0cmluZygzNikuc2xpY2UoMSk7XHJcbiAgICB9O1xyXG4gICAgO1xyXG4gICAgSmFkZXhDb25uZWN0aW9uSGFuZGxlci5JTlNUQU5DRSA9IG5ldyBKYWRleENvbm5lY3Rpb25IYW5kbGVyKCk7XHJcbiAgICByZXR1cm4gSmFkZXhDb25uZWN0aW9uSGFuZGxlcjtcclxufShDb25uZWN0aW9uSGFuZGxlcl8xLkNvbm5lY3Rpb25IYW5kbGVyKSk7XHJcbmV4cG9ydHMuSmFkZXhDb25uZWN0aW9uSGFuZGxlciA9IEphZGV4Q29ubmVjdGlvbkhhbmRsZXI7XHJcbi8vIyBzb3VyY2VNYXBwaW5nVVJMPUphZGV4Q29ubmVjdGlvbkhhbmRsZXIuanMubWFwIiwiXCJ1c2Ugc3RyaWN0XCI7XHJcbnZhciBTVXRpbF8xID0gcmVxdWlyZShcIi4uL1NVdGlsXCIpO1xyXG52YXIgV2Vic29ja2V0Q2FsbCA9IChmdW5jdGlvbiAoKSB7XHJcbiAgICBmdW5jdGlvbiBXZWJzb2NrZXRDYWxsKHR5cGUsIHJlc29sdmUsIHJlamVjdCwgY2IpIHtcclxuICAgICAgICB0aGlzLnR5cGUgPSB0eXBlO1xyXG4gICAgICAgIHRoaXMucmVzb2x2ZSA9IHJlc29sdmU7XHJcbiAgICAgICAgdGhpcy5yZWplY3QgPSByZWplY3Q7XHJcbiAgICAgICAgdGhpcy5jYiA9IGNiO1xyXG4gICAgICAgIHRoaXMuZmluaXNoZWQgPSBmYWxzZTtcclxuICAgIH1cclxuICAgIC8qKlxyXG4gICAgICogIFJlc3VtZSB0aGUgbGlzdGVuZXJzIG9mIHByb21pc2UuXHJcbiAgICAgKi9cclxuICAgIFdlYnNvY2tldENhbGwucHJvdG90eXBlLnJlc3VtZSA9IGZ1bmN0aW9uIChyZXN1bHQsIGV4Y2VwdGlvbikge1xyXG4gICAgICAgIGlmICh0aGlzLmNiICE9IG51bGwgJiYgKGV4Y2VwdGlvbiA9PT0gbnVsbCB8fCBleGNlcHRpb24gPT09IHVuZGVmaW5lZCkgJiYgIVNVdGlsXzEuU1V0aWwuaXNUcnVlKHRoaXMuZmluaXNoZWQpKSB7XHJcbiAgICAgICAgICAgIHRoaXMuY2IocmVzdWx0KTtcclxuICAgICAgICB9XHJcbiAgICAgICAgZWxzZSBpZiAoU1V0aWxfMS5TVXRpbC5pc1RydWUodGhpcy5maW5pc2hlZCkpIHtcclxuICAgICAgICAgICAgZXhjZXB0aW9uID09IG51bGwgPyB0aGlzLnJlc29sdmUocmVzdWx0KSA6IHRoaXMucmVqZWN0KGV4Y2VwdGlvbik7XHJcbiAgICAgICAgfVxyXG4gICAgfTtcclxuICAgIHJldHVybiBXZWJzb2NrZXRDYWxsO1xyXG59KCkpO1xyXG5leHBvcnRzLldlYnNvY2tldENhbGwgPSBXZWJzb2NrZXRDYWxsO1xyXG4vLyMgc291cmNlTWFwcGluZ1VSTD1XZWJzb2NrZXRDYWxsLmpzLm1hcCJdfQ==
