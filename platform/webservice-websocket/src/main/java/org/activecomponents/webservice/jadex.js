(function(f){if(typeof exports==="object"&&typeof module!=="undefined"){module.exports=f()}else if(typeof define==="function"&&define.amd){define([],f)}else{var g;if(typeof window!=="undefined"){g=window}else if(typeof global!=="undefined"){g=global}else if(typeof self!=="undefined"){g=self}else{g=this}g.jadexclasses = f()}})(function(){var define,module,exports;return (function(){function r(e,n,t){function o(i,f){if(!n[i]){if(!e[i]){var c="function"==typeof require&&require;if(!f&&c)return c(i,!0);if(u)return u(i,!0);var a=new Error("Cannot find module '"+i+"'");throw a.code="MODULE_NOT_FOUND",a}var p=n[i]={exports:{}};e[i][0].call(p.exports,function(r){var n=e[i][1][r];return o(n||r)},p,p.exports,r,e,n,t)}return n[i].exports}for(var u="function"==typeof require&&require,i=0;i<t.length;i++)o(t[i]);return o}return r})()({1:[function(require,module,exports){
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
    /**
     *  Check if the call was https.
     *  @return True if https.
     */
    SUtil.isSecure = function () {
        return window.location.protocol == 'https:';
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
var SUtil_1 = require("../SUtil");
var ConnectionHandler = (function () {
    function ConnectionHandler() {
        /** The websocket connections. */
        this.connections = [];
        var scripts = document.getElementsByTagName('script');
        var script = scripts[scripts.length - 1];
        var prot = SUtil_1.SUtil.isSecure() ? "wss" : "ws";
        if (script["src"]) {
            this.baseurl = script["src"];
            this.baseurl = prot + this.baseurl.substring(this.baseurl.indexOf("://"));
        }
        else if (script.hasAttributes()) {
            //this.baseurl = "ws://" + window.location.hostname + ":" + window.location.port + "/wswebapi";
            this.baseurl = prot + "://" + window.location.hostname + ":" + window.location.port + script.attributes.getNamedItem("src").value;
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

},{"../SUtil":5}],15:[function(require,module,exports){
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

//# sourceMappingURL=data:application/json;charset=utf-8;base64,eyJ2ZXJzaW9uIjozLCJzb3VyY2VzIjpbIm5vZGVfbW9kdWxlcy9icm93c2VyaWZ5L25vZGVfbW9kdWxlcy9icm93c2VyLXBhY2svX3ByZWx1ZGUuanMiLCJidWlsZC90cy9HbG9iYWwuanMiLCJidWlsZC90cy9KYWRleC5qcyIsImJ1aWxkL3RzL0phZGV4UHJvbWlzZS5qcyIsImJ1aWxkL3RzL0pzb25QYXJzZXIuanMiLCJidWlsZC90cy9TVXRpbC5qcyIsImJ1aWxkL3RzL1Njb3Blcy5qcyIsImJ1aWxkL3RzL1NlcnZpY2VQcm94eS5qcyIsImJ1aWxkL3RzL21lc3NhZ2VzL0Jhc2VNZXNzYWdlLmpzIiwiYnVpbGQvdHMvbWVzc2FnZXMvUGFydGlhbE1lc3NhZ2UuanMiLCJidWlsZC90cy9tZXNzYWdlcy9TZXJ2aWNlSW52b2NhdGlvbk1lc3NhZ2UuanMiLCJidWlsZC90cy9tZXNzYWdlcy9TZXJ2aWNlUHJvdmlkZU1lc3NhZ2UuanMiLCJidWlsZC90cy9tZXNzYWdlcy9TZXJ2aWNlU2VhcmNoTWVzc2FnZS5qcyIsImJ1aWxkL3RzL21lc3NhZ2VzL1NlcnZpY2VVbnByb3ZpZGVNZXNzYWdlLmpzIiwiYnVpbGQvdHMvd2Vic29ja2V0L0Nvbm5lY3Rpb25IYW5kbGVyLmpzIiwiYnVpbGQvdHMvd2Vic29ja2V0L0phZGV4Q29ubmVjdGlvbkhhbmRsZXIuanMiLCJidWlsZC90cy93ZWJzb2NrZXQvV2Vic29ja2V0Q2FsbC5qcyJdLCJuYW1lcyI6W10sIm1hcHBpbmdzIjoiQUFBQTtBQ0FBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBOztBQ2xCQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBOztBQ2xGQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTs7QUN6REE7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBOztBQzFLQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7O0FDcE5BO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTs7QUMxQkE7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7O0FDbEVBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTs7QUNSQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBOztBQ25CQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTs7QUNsQkE7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7O0FDbEJBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBOztBQ2xCQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBOztBQ2hCQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7O0FDbkZBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7O0FDOU9BO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBIiwiZmlsZSI6ImdlbmVyYXRlZC5qcyIsInNvdXJjZVJvb3QiOiIiLCJzb3VyY2VzQ29udGVudCI6WyIoZnVuY3Rpb24oKXtmdW5jdGlvbiByKGUsbix0KXtmdW5jdGlvbiBvKGksZil7aWYoIW5baV0pe2lmKCFlW2ldKXt2YXIgYz1cImZ1bmN0aW9uXCI9PXR5cGVvZiByZXF1aXJlJiZyZXF1aXJlO2lmKCFmJiZjKXJldHVybiBjKGksITApO2lmKHUpcmV0dXJuIHUoaSwhMCk7dmFyIGE9bmV3IEVycm9yKFwiQ2Fubm90IGZpbmQgbW9kdWxlICdcIitpK1wiJ1wiKTt0aHJvdyBhLmNvZGU9XCJNT0RVTEVfTk9UX0ZPVU5EXCIsYX12YXIgcD1uW2ldPXtleHBvcnRzOnt9fTtlW2ldWzBdLmNhbGwocC5leHBvcnRzLGZ1bmN0aW9uKHIpe3ZhciBuPWVbaV1bMV1bcl07cmV0dXJuIG8obnx8cil9LHAscC5leHBvcnRzLHIsZSxuLHQpfXJldHVybiBuW2ldLmV4cG9ydHN9Zm9yKHZhciB1PVwiZnVuY3Rpb25cIj09dHlwZW9mIHJlcXVpcmUmJnJlcXVpcmUsaT0wO2k8dC5sZW5ndGg7aSsrKW8odFtpXSk7cmV0dXJuIG99cmV0dXJuIHJ9KSgpIiwiXCJ1c2Ugc3RyaWN0XCI7XHJcbnZhciBKYWRleF8xID0gcmVxdWlyZShcIi4vSmFkZXhcIik7XHJcbmV4cG9ydHMuSmFkZXggPSBKYWRleF8xLkphZGV4O1xyXG52YXIgU1V0aWxfMSA9IHJlcXVpcmUoXCIuL1NVdGlsXCIpO1xyXG5leHBvcnRzLlNVdGlsID0gU1V0aWxfMS5TVXRpbDtcclxudmFyIFNjb3Blc18xID0gcmVxdWlyZShcIi4vU2NvcGVzXCIpO1xyXG5leHBvcnRzLlNjb3BlcyA9IFNjb3Blc18xLlNjb3BlcztcclxudmFyIGdsb2JhbCA9IHdpbmRvdztcclxuLy8gZ2xvYmFsLmphZGV4Y2xhc3NlcyA9IHtcclxuLy8gICAgIEphZGV4LFxyXG4vLyAgICAgQ29ubmVjdGlvbkhhbmRsZXIsXHJcbi8vICAgICBTVXRpbCxcclxuLy8gICAgIFNjb3Blc1xyXG4vLyB9O1xyXG4vLyBUaGVzZSBhcmUgaGFja3MuIFJlbW92ZSBhbmQgdXNlIGV4cG9ydCBsaW5lIGJlbG93LCBwYXNzIFwiLS1zdGFuZGFsb25lIGphZGV4XCIgdG8gYnJvd3NlcmlmeSBhZnRlcndhcmRzLlxyXG5nbG9iYWwuU2NvcGVzID0gU2NvcGVzXzEuU2NvcGVzO1xyXG5nbG9iYWwuamFkZXggPSBuZXcgSmFkZXhfMS5KYWRleCgpO1xyXG5nbG9iYWwuU1V0aWwgPSBTVXRpbF8xLlNVdGlsO1xyXG4vLyMgc291cmNlTWFwcGluZ1VSTD1HbG9iYWwuanMubWFwIiwiXCJ1c2Ugc3RyaWN0XCI7XHJcbnZhciBKYWRleENvbm5lY3Rpb25IYW5kbGVyXzEgPSByZXF1aXJlKFwiLi93ZWJzb2NrZXQvSmFkZXhDb25uZWN0aW9uSGFuZGxlclwiKTtcclxudmFyIFNlcnZpY2VTZWFyY2hNZXNzYWdlXzEgPSByZXF1aXJlKFwiLi9tZXNzYWdlcy9TZXJ2aWNlU2VhcmNoTWVzc2FnZVwiKTtcclxudmFyIFNjb3Blc18xID0gcmVxdWlyZShcIi4vU2NvcGVzXCIpO1xyXG52YXIgU2VydmljZVByb3ZpZGVNZXNzYWdlXzEgPSByZXF1aXJlKFwiLi9tZXNzYWdlcy9TZXJ2aWNlUHJvdmlkZU1lc3NhZ2VcIik7XHJcbnZhciBTZXJ2aWNlVW5wcm92aWRlTWVzc2FnZV8xID0gcmVxdWlyZShcIi4vbWVzc2FnZXMvU2VydmljZVVucHJvdmlkZU1lc3NhZ2VcIik7XHJcbnZhciBKc29uUGFyc2VyXzEgPSByZXF1aXJlKFwiLi9Kc29uUGFyc2VyXCIpO1xyXG52YXIgU1V0aWxfMSA9IHJlcXVpcmUoXCIuL1NVdGlsXCIpO1xyXG4vKipcclxuICogIE1haW4gY2xhc3Mgd2l0aCBtZXRob2RzIHRvXHJcbiAqICAtIGdldFNlcnZpY2UocykgYW5kXHJcbiAqICAtIHByb3ZpZGVTZXJ2aWNlXHJcbiAqL1xyXG52YXIgSmFkZXggPSAoZnVuY3Rpb24gKCkge1xyXG4gICAgLyoqXHJcbiAgICAgKiAgSmFkZXggSmF2YXNjcmlwdCBBUEkuXHJcbiAgICAgKi9cclxuICAgIGZ1bmN0aW9uIEphZGV4KCkge1xyXG4gICAgfVxyXG4gICAgLyoqXHJcbiAgICAgKiAgR2V0IGEgc2VydmljZSBmcm9tIGEgcHVibGljYXRpb24gcG9pbnQuXHJcbiAgICAgKiAgQHBhcmFtIHR5cGUgVGhlIHNlcnZpY2UgdHlwZS5cclxuICAgICAqICBAcGFyYW0gc2NvcGUgVGhlIHNjb3BlLlxyXG4gICAgICogIEBwYXJhbSB1cmwgVGhlIHVybCBvZiB0aGUgd2Vic29ja2V0LlxyXG4gICAgICovXHJcbiAgICBKYWRleC5wcm90b3R5cGUuZ2V0U2VydmljZSA9IGZ1bmN0aW9uICh0eXBlLCBzY29wZSwgdXJsKSB7XHJcbiAgICAgICAgdmFyIHByb20gPSBTVXRpbF8xLlNVdGlsLmFkZEVyckhhbmRsZXIobmV3IFByb21pc2UoZnVuY3Rpb24gKHJlc29sdmUsIHJlamVjdCkge1xyXG4gICAgICAgICAgICB2YXIgY21kID0gbmV3IFNlcnZpY2VTZWFyY2hNZXNzYWdlXzEuU2VydmljZVNlYXJjaE1lc3NhZ2UodHlwZSwgZmFsc2UsIHNjb3BlICE9IG51bGwgPyBzY29wZSA6IFNjb3Blc18xLlNjb3Blcy5TQ09QRV9QTEFURk9STSk7XHJcbiAgICAgICAgICAgIEphZGV4Q29ubmVjdGlvbkhhbmRsZXJfMS5KYWRleENvbm5lY3Rpb25IYW5kbGVyLmdldEluc3RhbmNlKCkuc2VuZE1lc3NhZ2UodXJsLCBjbWQsIFwic2VhcmNoXCIsIHJlc29sdmUsIHJlamVjdCwgbnVsbCk7XHJcbiAgICAgICAgfSkpO1xyXG4gICAgICAgIHJldHVybiBwcm9tO1xyXG4gICAgfTtcclxuICAgIDtcclxuICAgIC8qKlxyXG4gICAgICogIEdldCBzZXJ2aWNlcyBmcm9tIGEgcHVibGljYXRpb24gcG9pbnQuXHJcbiAgICAgKiAgQHBhcmFtIHR5cGUgVGhlIHNlcnZpY2UgdHlwZS5cclxuICAgICAqICBAcGFyYW0gY2FsbGJhY2sgVGhlIGNhbGxiYWNrIGZ1bmN0aW9uIGZvciB0aGUgaW50ZXJtZWRpYXRlIHJlc3VsdHMuXHJcbiAgICAgKiAgQHBhcmFtIHNjb3BlIFRoZSBzZWFyY2ggc2NvcGUuXHJcbiAgICAgKiAgQHBhcmFtIHVybCBUaGUgdXJsIG9mIHRoZSB3ZWJzb2NrZXQuXHJcbiAgICAgKi9cclxuICAgIEphZGV4LnByb3RvdHlwZS5nZXRTZXJ2aWNlcyA9IGZ1bmN0aW9uICh0eXBlLCBjYWxsYmFjaywgc2NvcGUsIHVybCkge1xyXG4gICAgICAgIFNVdGlsXzEuU1V0aWwuYXNzZXJ0KGNhbGxiYWNrIGluc3RhbmNlb2YgRnVuY3Rpb24gJiYgY2FsbGJhY2sgIT0gbnVsbCk7XHJcbiAgICAgICAgdmFyIHByb20gPSBTVXRpbF8xLlNVdGlsLmFkZEVyckhhbmRsZXIobmV3IFByb21pc2UoZnVuY3Rpb24gKHJlc29sdmUsIHJlamVjdCkge1xyXG4gICAgICAgICAgICB2YXIgY21kID0gbmV3IFNlcnZpY2VTZWFyY2hNZXNzYWdlXzEuU2VydmljZVNlYXJjaE1lc3NhZ2UodHlwZSwgdHJ1ZSwgc2NvcGUgIT0gbnVsbCA/IHNjb3BlIDogU2NvcGVzXzEuU2NvcGVzLlNDT1BFX1BMQVRGT1JNKTtcclxuICAgICAgICAgICAgSmFkZXhDb25uZWN0aW9uSGFuZGxlcl8xLkphZGV4Q29ubmVjdGlvbkhhbmRsZXIuZ2V0SW5zdGFuY2UoKS5zZW5kTWVzc2FnZSh1cmwsIGNtZCwgXCJzZWFyY2hcIiwgcmVzb2x2ZSwgcmVqZWN0LCBjYWxsYmFjayk7XHJcbiAgICAgICAgfSkpO1xyXG4gICAgICAgIHJldHVybiBwcm9tO1xyXG4gICAgfTtcclxuICAgIDtcclxuICAgIC8qKlxyXG4gICAgICogIFByb3ZpZGUgYSBuZXcgKGNsaWVudCkgc2VydmljZS5cclxuICAgICAqICBAcGFyYW0gdHlwZSBUaGUgc2VydmljZSB0eXBlLlxyXG4gICAgICogIEBwYXJhbSBzY29wZSBUaGUgcHJvdmlzaW9uIHNjb3BlLlxyXG4gICAgICogIEBwYXJhbSB1cmwgVGhlIHVybCBvZiB0aGUgd2Vic29ja2V0LlxyXG4gICAgICovXHJcbiAgICBKYWRleC5wcm90b3R5cGUucHJvdmlkZVNlcnZpY2UgPSBmdW5jdGlvbiAodHlwZSwgc2NvcGUsIHRhZ3MsIGNhbGxiYWNrLCB1cmwpIHtcclxuICAgICAgICByZXR1cm4gU1V0aWxfMS5TVXRpbC5hZGRFcnJIYW5kbGVyKG5ldyBQcm9taXNlKGZ1bmN0aW9uIChyZXNvbHZlLCByZWplY3QpIHtcclxuICAgICAgICAgICAgdmFyIGNtZCA9IG5ldyBTZXJ2aWNlUHJvdmlkZU1lc3NhZ2VfMS5TZXJ2aWNlUHJvdmlkZU1lc3NhZ2UodHlwZSwgc2NvcGUgIT0gbnVsbCA/IHNjb3BlIDogXCJnbG9iYWxcIiwgdHlwZW9mIHRhZ3MgPT09IFwic3RyaW5nXCIgPyBbdGFnc10gOiB0YWdzKTtcclxuICAgICAgICAgICAgSmFkZXhDb25uZWN0aW9uSGFuZGxlcl8xLkphZGV4Q29ubmVjdGlvbkhhbmRsZXIuZ2V0SW5zdGFuY2UoKS5zZW5kTWVzc2FnZSh1cmwsIGNtZCwgXCJwcm92aWRlXCIsIHJlc29sdmUsIHJlamVjdCwgY2FsbGJhY2spO1xyXG4gICAgICAgIH0pKTtcclxuICAgIH07XHJcbiAgICAvKipcclxuICAgICAqICBVbnByb3ZpZGUgYSAoY2xpZW50KSBzZXJ2aWNlLlxyXG4gICAgICogIEBwYXJhbSB0eXBlIFRoZSBzZXJ2aWNlIHR5cGUuXHJcbiAgICAgKiAgQHBhcmFtIHNjb3BlIFRoZSBwcm92aXNpb24gc2NvcGUuXHJcbiAgICAgKiAgQHBhcmFtIHVybCBUaGUgdXJsIG9mIHRoZSB3ZWJzb2NrZXQuXHJcbiAgICAgKi9cclxuICAgIEphZGV4LnByb3RvdHlwZS51bnByb3ZpZGVTZXJ2aWNlID0gZnVuY3Rpb24gKHNpZCwgdXJsKSB7XHJcbiAgICAgICAgcmV0dXJuIFNVdGlsXzEuU1V0aWwuYWRkRXJySGFuZGxlcihuZXcgUHJvbWlzZShmdW5jdGlvbiAocmVzb2x2ZSwgcmVqZWN0KSB7XHJcbiAgICAgICAgICAgIHZhciBjbWQgPSBuZXcgU2VydmljZVVucHJvdmlkZU1lc3NhZ2VfMS5TZXJ2aWNlVW5wcm92aWRlTWVzc2FnZShzaWQpO1xyXG4gICAgICAgICAgICBKYWRleENvbm5lY3Rpb25IYW5kbGVyXzEuSmFkZXhDb25uZWN0aW9uSGFuZGxlci5nZXRJbnN0YW5jZSgpLnNlbmRNZXNzYWdlKHVybCwgY21kLCBcInVucHJvdmlkZVwiLCByZXNvbHZlLCByZWplY3QsIG51bGwpO1xyXG4gICAgICAgIH0pKTtcclxuICAgIH07XHJcbiAgICAvKipcclxuICAgICAqICBSZWdpc3RlciBhIGNsYXNzIGZvciBqc29uIChkZSlzZXJpYWxpemF0aW9uLlxyXG4gICAgICovXHJcbiAgICBKYWRleC5wcm90b3R5cGUucmVnaXN0ZXJDbGFzcyA9IGZ1bmN0aW9uIChjbGF6eikge1xyXG4gICAgICAgIEpzb25QYXJzZXJfMS5Kc29uUGFyc2VyLnJlZ2lzdGVyQ2xhc3MoY2xhenopO1xyXG4gICAgfTtcclxuICAgIHJldHVybiBKYWRleDtcclxufSgpKTtcclxuZXhwb3J0cy5KYWRleCA9IEphZGV4O1xyXG4vLyMgc291cmNlTWFwcGluZ1VSTD1KYWRleC5qcy5tYXAiLCJcInVzZSBzdHJpY3RcIjtcclxudmFyIEphZGV4Q29ubmVjdGlvbkhhbmRsZXJfMSA9IHJlcXVpcmUoXCIuL3dlYnNvY2tldC9KYWRleENvbm5lY3Rpb25IYW5kbGVyXCIpO1xyXG52YXIgSmFkZXhQcm9taXNlID0gKGZ1bmN0aW9uICgpIHtcclxuICAgIC8qKlxyXG4gICAgICogSW5zdGFudGlhdGUgYSBwcm9taXNlIGJ1dCBkbyBub3QgZXhlY3V0ZSBhbnl0aGluZyBub3cuXHJcbiAgICAgKiBSZXNvbHZpbmcgaXMgaGFuZGxlZCBmcm9tIG91dHNpZGUgdmlhIHJlc29sdmUoKS5cclxuICAgICAqL1xyXG4gICAgZnVuY3Rpb24gSmFkZXhQcm9taXNlKHVybCkge1xyXG4gICAgICAgIHZhciBfdGhpcyA9IHRoaXM7XHJcbiAgICAgICAgdGhpcy51cmwgPSB1cmw7XHJcbiAgICAgICAgdGhpcy5pbnRlcm1lZGlhdGVSZXNvbHZlQ2FsbGJhY2tzID0gW107XHJcbiAgICAgICAgdGhpcy5wcm9taXNlID0gbmV3IFByb21pc2UoZnVuY3Rpb24gKHJlc29sdmUsIHJlamVjdCkge1xyXG4gICAgICAgICAgICBfdGhpcy5yZXNvbHZlRnVuYyA9IHJlc29sdmU7XHJcbiAgICAgICAgICAgIF90aGlzLnJlamVjdEZ1bmMgPSByZWplY3Q7XHJcbiAgICAgICAgfSk7XHJcbiAgICB9XHJcbiAgICAvLyBwcml2YXRlIGludGVybWVkaWF0ZVJlamVjdENhbGxiYWNrcyA9IFtdO1xyXG4gICAgSmFkZXhQcm9taXNlLnByb3RvdHlwZS50aGVuID0gZnVuY3Rpb24gKG9uZnVsZmlsbGVkLCBvbnJlamVjdGVkKSB7XHJcbiAgICAgICAgcmV0dXJuIHRoaXMucHJvbWlzZS50aGVuKG9uZnVsZmlsbGVkLCBvbnJlamVjdGVkKTtcclxuICAgIH07XHJcbiAgICBKYWRleFByb21pc2UucHJvdG90eXBlLnJlc29sdmUgPSBmdW5jdGlvbiAodmFsdWUpIHtcclxuICAgICAgICAvLyByZXR1cm4gdGhpcy5wcm9taXNlLnJlc29sdmUodmFsdWUpO1xyXG4gICAgICAgIHJldHVybiB0aGlzLnJlc29sdmVGdW5jKHZhbHVlKTtcclxuICAgIH07XHJcbiAgICBKYWRleFByb21pc2UucHJvdG90eXBlLnJlamVjdCA9IGZ1bmN0aW9uIChlcnJvcikge1xyXG4gICAgICAgIC8vIHJldHVybiB0aGlzLnByb21pc2UucmVqZWN0KGVycm9yKTtcclxuICAgICAgICByZXR1cm4gdGhpcy5yZWplY3RGdW5jKGVycm9yKTtcclxuICAgIH07XHJcbiAgICBKYWRleFByb21pc2UucHJvdG90eXBlLnJlc29sdmVJbnRlcm1lZGlhdGUgPSBmdW5jdGlvbiAodmFsdWUpIHtcclxuICAgICAgICBmb3IgKHZhciBfaSA9IDAsIF9hID0gdGhpcy5pbnRlcm1lZGlhdGVSZXNvbHZlQ2FsbGJhY2tzOyBfaSA8IF9hLmxlbmd0aDsgX2krKykge1xyXG4gICAgICAgICAgICB2YXIgY2IgPSBfYVtfaV07XHJcbiAgICAgICAgICAgIGNiKHZhbHVlKTtcclxuICAgICAgICB9XHJcbiAgICB9O1xyXG4gICAgSmFkZXhQcm9taXNlLnByb3RvdHlwZS50aGVuSW50ZXJtZWRpYXRlID0gZnVuY3Rpb24gKG9uZnVsZmlsbGVkLCBvbnJlamVjdGVkKSB7XHJcbiAgICAgICAgdGhpcy5pbnRlcm1lZGlhdGVSZXNvbHZlQ2FsbGJhY2tzLnB1c2gob25mdWxmaWxsZWQpO1xyXG4gICAgICAgIC8vIHRoaXMuaW50ZXJtZWRpYXRlUmVqZWN0Q2FsbGJhY2tzLnB1c2gob25yZWplY3RlZCk7XHJcbiAgICB9O1xyXG4gICAgSmFkZXhQcm9taXNlLnByb3RvdHlwZS50ZXJtaW5hdGUgPSBmdW5jdGlvbiAoKSB7XHJcbiAgICAgICAgdmFyIGNtZCA9IHtcclxuICAgICAgICAgICAgX19jbGFzc25hbWU6IFwiY29tLmFjdG9yb24ud2Vic2VydmljZS5tZXNzYWdlcy5TZXJ2aWNlVGVybWluYXRlSW52b2NhdGlvbk1lc3NhZ2VcIixcclxuICAgICAgICAgICAgY2FsbGlkOiB0aGlzLmNhbGxpZFxyXG4gICAgICAgIH07XHJcbiAgICAgICAgSmFkZXhDb25uZWN0aW9uSGFuZGxlcl8xLkphZGV4Q29ubmVjdGlvbkhhbmRsZXIuZ2V0SW5zdGFuY2UoKS5zZW5kQ29udmVyc2F0aW9uTWVzc2FnZSh0aGlzLnVybCwgY21kKTtcclxuICAgIH07XHJcbiAgICA7XHJcbiAgICBKYWRleFByb21pc2UucHJvdG90eXBlLnB1bGwgPSBmdW5jdGlvbiAoKSB7XHJcbiAgICAgICAgdmFyIGNtZCA9IHtcclxuICAgICAgICAgICAgX19jbGFzc25hbWU6IFwiY29tLmFjdG9yb24ud2Vic2VydmljZS5tZXNzYWdlcy5QdWxsUmVzdWx0TWVzc2FnZVwiLFxyXG4gICAgICAgICAgICBjYWxsaWQ6IHRoaXMuY2FsbGlkXHJcbiAgICAgICAgfTtcclxuICAgICAgICBKYWRleENvbm5lY3Rpb25IYW5kbGVyXzEuSmFkZXhDb25uZWN0aW9uSGFuZGxlci5nZXRJbnN0YW5jZSgpLnNlbmRDb252ZXJzYXRpb25NZXNzYWdlKHRoaXMudXJsLCBjbWQpO1xyXG4gICAgfTtcclxuICAgIDtcclxuICAgIHJldHVybiBKYWRleFByb21pc2U7XHJcbn0oKSk7XHJcbmV4cG9ydHMuSmFkZXhQcm9taXNlID0gSmFkZXhQcm9taXNlO1xyXG4vLyMgc291cmNlTWFwcGluZ1VSTD1KYWRleFByb21pc2UuanMubWFwIiwiXCJ1c2Ugc3RyaWN0XCI7XHJcbnZhciBTVXRpbF8xID0gcmVxdWlyZShcIi4vU1V0aWxcIik7XHJcbnZhciBTZXJ2aWNlUHJveHlfMSA9IHJlcXVpcmUoXCIuL1NlcnZpY2VQcm94eVwiKTtcclxuLyoqXHJcbiAqICBDbGFzcyB0aGF0IGNhbiBwYXJzZSBqc29uIHdpdGggYWRkaXRpb25hbCBmZWF0dXJlcy5cclxuICogIC0gaGFuZGxlcyBKYWRleCByZWZlcmVuY2VzXHJcbiAqL1xyXG52YXIgSnNvblBhcnNlciA9IChmdW5jdGlvbiAoKSB7XHJcbiAgICBmdW5jdGlvbiBKc29uUGFyc2VyKCkge1xyXG4gICAgfVxyXG4gICAgSnNvblBhcnNlci5pbml0ID0gZnVuY3Rpb24gKCkge1xyXG4gICAgICAgIEpzb25QYXJzZXIucmVnaXN0ZXJDbGFzczIoXCJqYXZhLnV0aWwuRGF0ZVwiLCB7IGNyZWF0ZTogZnVuY3Rpb24gKG9iaikge1xyXG4gICAgICAgICAgICAgICAgcmV0dXJuIG5ldyBEYXRlKG9iai52YWx1ZSk7XHJcbiAgICAgICAgICAgIH0gfSk7XHJcbiAgICB9O1xyXG4gICAgLyoqXHJcbiAgICAgKiAgUmVnaXN0ZXIgYSBjbGFzcyBhdCB0aGUgcGFyc2VyLlxyXG4gICAgICovXHJcbiAgICBKc29uUGFyc2VyLnJlZ2lzdGVyQ2xhc3MgPSBmdW5jdGlvbiAoY2xhenopIHtcclxuICAgICAgICBpZiAoXCJfX2NsYXNzbmFtZVwiIGluIGNsYXp6KSB7XHJcbiAgICAgICAgICAgIEpzb25QYXJzZXIucmVnaXN0ZXJlZENsYXNzZXNbY2xhenouX19jbGFzc25hbWVdID0gY2xheno7XHJcbiAgICAgICAgfVxyXG4gICAgICAgIGVsc2Uge1xyXG4gICAgICAgICAgICB2YXIgaW5zdGFuY2UgPSBuZXcgY2xhenooKTtcclxuICAgICAgICAgICAgaWYgKFwiX19jbGFzc25hbWVcIiBpbiBpbnN0YW5jZSkge1xyXG4gICAgICAgICAgICAgICAgSnNvblBhcnNlci5yZWdpc3RlcmVkQ2xhc3Nlc1tpbnN0YW5jZS5fX2NsYXNzbmFtZV0gPSBjbGF6ejtcclxuICAgICAgICAgICAgfVxyXG4gICAgICAgICAgICBlbHNlIHtcclxuICAgICAgICAgICAgICAgIHRocm93IG5ldyBFcnJvcihcIkNhbm5vdCByZWdpc3RlciBjbGFzcyB3aXRob3V0IF9fY2xhc3NuYW1lIHN0YXRpYyBmaWVsZCBvciBtZW1iZXI6IFwiICsgY2xhenoubmFtZSk7XHJcbiAgICAgICAgICAgIH1cclxuICAgICAgICB9XHJcbiAgICB9O1xyXG4gICAgLyoqXHJcbiAgICAgKiAgUmVnaXN0ZXIgYSBjbGFzcyBhdCB0aGUgcGFyc2VyLlxyXG4gICAgICovXHJcbiAgICBKc29uUGFyc2VyLnJlZ2lzdGVyQ2xhc3MyID0gZnVuY3Rpb24gKGNsYXNzbmFtZSwgY3JlYXRlKSB7XHJcbiAgICAgICAgSnNvblBhcnNlci5yZWdpc3RlcmVkQ2xhc3Nlc1tjbGFzc25hbWVdID0gY3JlYXRlO1xyXG4gICAgfTtcclxuICAgIC8qKlxyXG4gICAgICogIEpTT24ucGFyc2UgZXh0ZW5zaW9uIHRvIGhhbmRsZSBKYWRleCByZWZlcmVuY2UgbWVjaGFuaXNtLlxyXG4gICAgICogIEBwYXJhbSBzdHIgVGhlIHN0cmluZyBvZiB0aGUganNvbiBvYmplY3QgdG8gcGFyc2UuXHJcbiAgICAgKiAgQHJldHVybiBUaGUgcGFyc2VkIG9iamVjdC5cclxuICAgICAqL1xyXG4gICAgSnNvblBhcnNlci5wYXJzZSA9IGZ1bmN0aW9uIChzdHIsIHVybCkge1xyXG4gICAgICAgIHZhciBpZG1hcmtlciA9IFwiX19pZFwiO1xyXG4gICAgICAgIHZhciByZWZtYXJrZXIgPSBcIl9fcmVmXCI7XHJcbiAgICAgICAgLy9cdFx0bGV0IGFycmF5bWFya2VyID0gXCJfX2FycmF5XCI7XHJcbiAgICAgICAgLy9cdFx0bGV0IGNvbGxlY3Rpb25tYXJrZXIgPSBcIl9fY29sbGVjdGlvblwiO1xyXG4gICAgICAgIHZhciByZXBsYWNlbWFya2VyID0gW1wiX19hcnJheVwiLCBcIl9fY29sbGVjdGlvblwiXTtcclxuICAgICAgICB2YXIgb3MgPSB7fTsgLy8gdGhlIG9iamVjdHMgcGVyIGlkXHJcbiAgICAgICAgdmFyIHJlZnMgPSBbXTsgLy8gdGhlIHVucmVzb2x2ZWQgcmVmZXJlbmNlc1xyXG4gICAgICAgIHZhciBvYmo7XHJcbiAgICAgICAgdHJ5IHtcclxuICAgICAgICAgICAgb2JqID0gSlNPTi5wYXJzZShzdHIpO1xyXG4gICAgICAgIH1cclxuICAgICAgICBjYXRjaCAoZSkge1xyXG4gICAgICAgICAgICBjb25zb2xlLmVycm9yKFwiQ291bGQgbm90IHBhcnNlIHN0cmluZzogXCIgKyBzdHIpO1xyXG4gICAgICAgICAgICB0aHJvdyBlO1xyXG4gICAgICAgIH1cclxuICAgICAgICB2YXIgcmVjdXJzZSA9IGZ1bmN0aW9uIChvYmosIHByb3AsIHBhcmVudCkge1xyXG4gICAgICAgICAgICAvL1x0ICAgIGNvbnNvbGUubG9nKG9iaitcIiBcIitwcm9wK1wiIFwiK3BhcmVudCk7XHJcbiAgICAgICAgICAgIGlmICghU1V0aWxfMS5TVXRpbC5pc0Jhc2ljVHlwZShvYmopKSB7XHJcbiAgICAgICAgICAgICAgICAvLyB0ZXN0IGlmIGl0IGlzIGp1c3QgYSBwbGFjZWhvbGRlciBvYmplY3QgdGhhdCBtdXN0IGJlIGNoYW5nZWRcclxuICAgICAgICAgICAgICAgIC8vXHRcdFx0aWYocHJvcCE9bnVsbClcclxuICAgICAgICAgICAgICAgIC8vXHRcdFx0e1xyXG4gICAgICAgICAgICAgICAgZm9yICh2YXIgaSA9IDA7IGkgPCByZXBsYWNlbWFya2VyLmxlbmd0aDsgaSsrKSB7XHJcbiAgICAgICAgICAgICAgICAgICAgaWYgKHJlcGxhY2VtYXJrZXJbaV0gaW4gb2JqKSB7XHJcbiAgICAgICAgICAgICAgICAgICAgICAgIG9iaiA9IG9ialtyZXBsYWNlbWFya2VyW2ldXTtcclxuICAgICAgICAgICAgICAgICAgICAgICAgYnJlYWs7XHJcbiAgICAgICAgICAgICAgICAgICAgfVxyXG4gICAgICAgICAgICAgICAgfVxyXG4gICAgICAgICAgICAgICAgLy9cdFx0ICAgIH1cclxuICAgICAgICAgICAgICAgIC8vIGluc3RhbnRpYXRlIGNsYXNzZXNcclxuICAgICAgICAgICAgICAgIGlmIChcIl9fY2xhc3NuYW1lXCIgaW4gb2JqKSB7XHJcbiAgICAgICAgICAgICAgICAgICAgdmFyIGNsYXNzTmFtZSA9IG9ialtcIl9fY2xhc3NuYW1lXCJdO1xyXG4gICAgICAgICAgICAgICAgICAgIGlmIChjbGFzc05hbWUgPT0gXCJqYWRleC5icmlkZ2Uuc2VydmljZS5JU2VydmljZVwiKSB7XHJcbiAgICAgICAgICAgICAgICAgICAgICAgIG9iaiA9IG5ldyBTZXJ2aWNlUHJveHlfMS5TZXJ2aWNlUHJveHkob2JqLnNlcnZpY2VJZGVudGlmaWVyLCByZWN1cnNlKG9iai5tZXRob2ROYW1lcywgXCJtZXRob2ROYW1lc1wiLCBvYmopLCB1cmwpO1xyXG4gICAgICAgICAgICAgICAgICAgIH1cclxuICAgICAgICAgICAgICAgICAgICBlbHNlIGlmIChjbGFzc05hbWUgaW4gSnNvblBhcnNlci5yZWdpc3RlcmVkQ2xhc3Nlcykge1xyXG4gICAgICAgICAgICAgICAgICAgICAgICB2YXIgZnVuYyA9IEpzb25QYXJzZXIucmVnaXN0ZXJlZENsYXNzZXNbY2xhc3NOYW1lXTtcclxuICAgICAgICAgICAgICAgICAgICAgICAgaWYgKGZ1bmMuY3JlYXRlKSB7XHJcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICBvYmogPSBmdW5jLmNyZWF0ZShvYmopO1xyXG4gICAgICAgICAgICAgICAgICAgICAgICB9XHJcbiAgICAgICAgICAgICAgICAgICAgICAgIGVsc2Uge1xyXG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgLy8gaXRlcmF0ZSBtZW1iZXJzOlxyXG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgdmFyIGluc3RhbmNlID0gbmV3IGZ1bmMoKTtcclxuICAgICAgICAgICAgICAgICAgICAgICAgICAgIGZvciAodmFyIHByb3BfMSBpbiBvYmopIHtcclxuICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICBpbnN0YW5jZVtwcm9wXzFdID0gcmVjdXJzZShvYmpbcHJvcF8xXSwgcHJvcF8xLCBvYmopO1xyXG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgfVxyXG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgb2JqID0gaW5zdGFuY2U7XHJcbiAgICAgICAgICAgICAgICAgICAgICAgIH1cclxuICAgICAgICAgICAgICAgICAgICB9XHJcbiAgICAgICAgICAgICAgICB9XHJcbiAgICAgICAgICAgICAgICBlbHNlIHtcclxuICAgICAgICAgICAgICAgICAgICAvLyByZWNyZWF0ZSBhcnJheXNcclxuICAgICAgICAgICAgICAgICAgICBpZiAoU1V0aWxfMS5TVXRpbC5pc0FycmF5KG9iaikpIHtcclxuICAgICAgICAgICAgICAgICAgICAgICAgZm9yICh2YXIgaSA9IDA7IGkgPCBvYmoubGVuZ3RoOyBpKyspIHtcclxuICAgICAgICAgICAgICAgICAgICAgICAgICAgIGlmICghU1V0aWxfMS5TVXRpbC5pc0Jhc2ljVHlwZShvYmpbaV0pKSB7XHJcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgaWYgKHJlZm1hcmtlciBpbiBvYmpbaV0pIHtcclxuICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgb2JqW2ldID0gcmVjdXJzZShvYmpbaV0sIGksIG9iaik7XHJcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgfVxyXG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIGVsc2Uge1xyXG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICBvYmpbaV0gPSByZWN1cnNlKG9ialtpXSwgcHJvcCwgb2JqKTtcclxuICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICB9XHJcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICB9XHJcbiAgICAgICAgICAgICAgICAgICAgICAgIH1cclxuICAgICAgICAgICAgICAgICAgICB9XHJcbiAgICAgICAgICAgICAgICB9XHJcbiAgICAgICAgICAgICAgICBpZiAocmVmbWFya2VyIGluIG9iaikge1xyXG4gICAgICAgICAgICAgICAgICAgIHZhciByZWYgPSBvYmpbcmVmbWFya2VyXTtcclxuICAgICAgICAgICAgICAgICAgICBpZiAocmVmIGluIG9zKSB7XHJcbiAgICAgICAgICAgICAgICAgICAgICAgIG9iaiA9IG9zW3JlZl07XHJcbiAgICAgICAgICAgICAgICAgICAgfVxyXG4gICAgICAgICAgICAgICAgICAgIGVsc2Uge1xyXG4gICAgICAgICAgICAgICAgICAgICAgICByZWZzLnB1c2goW3BhcmVudCwgcHJvcCwgcmVmXSk7IC8vIGxhenkgZXZhbHVhdGlvbiBuZWNlc3NhcnlcclxuICAgICAgICAgICAgICAgICAgICB9XHJcbiAgICAgICAgICAgICAgICB9XHJcbiAgICAgICAgICAgICAgICBlbHNlIHtcclxuICAgICAgICAgICAgICAgICAgICB2YXIgaWQgPSBudWxsO1xyXG4gICAgICAgICAgICAgICAgICAgIGlmIChpZG1hcmtlciBpbiBvYmopIHtcclxuICAgICAgICAgICAgICAgICAgICAgICAgaWQgPSBvYmpbaWRtYXJrZXJdO1xyXG4gICAgICAgICAgICAgICAgICAgICAgICBkZWxldGUgb2JqW2lkbWFya2VyXTtcclxuICAgICAgICAgICAgICAgICAgICB9XHJcbiAgICAgICAgICAgICAgICAgICAgaWYgKFwiJHZhbHVlc1wiIGluIG9iaikge1xyXG4gICAgICAgICAgICAgICAgICAgICAgICBvYmogPSBvYmouJHZhbHVlcy5tYXAocmVjdXJzZSk7XHJcbiAgICAgICAgICAgICAgICAgICAgfVxyXG4gICAgICAgICAgICAgICAgICAgIGVsc2Uge1xyXG4gICAgICAgICAgICAgICAgICAgICAgICBmb3IgKHZhciBwcm9wXzIgaW4gb2JqKSB7XHJcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICBvYmpbcHJvcF8yXSA9IHJlY3Vyc2Uob2JqW3Byb3BfMl0sIHByb3BfMiwgb2JqKTtcclxuICAgICAgICAgICAgICAgICAgICAgICAgfVxyXG4gICAgICAgICAgICAgICAgICAgIH1cclxuICAgICAgICAgICAgICAgICAgICBpZiAoaWQgIT0gbnVsbCkge1xyXG4gICAgICAgICAgICAgICAgICAgICAgICBvc1tpZF0gPSBvYmo7XHJcbiAgICAgICAgICAgICAgICAgICAgfVxyXG4gICAgICAgICAgICAgICAgfVxyXG4gICAgICAgICAgICAgICAgLy8gdW53cmFwIGJveGVkIHZhbHVlcyBmb3IgSlM6XHJcbiAgICAgICAgICAgICAgICB2YXIgd3JhcHBlZFR5cGUgPSBTVXRpbF8xLlNVdGlsLmlzV3JhcHBlZFR5cGUob2JqKTtcclxuICAgICAgICAgICAgICAgIGlmICh3cmFwcGVkVHlwZSkge1xyXG4gICAgICAgICAgICAgICAgICAgIC8vIGNvbnNvbGUubG9nKFwiZm91bmQgd3JhcHBlZDogXCIgKyBpc1dyYXBwZWRUeXBlKG9iaikgKyBcIiBmb3I6IFwiICsgb2JqLl9fY2xhc3NuYW1lICsgXCIgd2l0aCB2YWx1ZTogXCIgKyBvYmoudmFsdWUpXHJcbiAgICAgICAgICAgICAgICAgICAgaWYgKHdyYXBwZWRUeXBlID09IFwiYm9vbGVhblwiKSB7XHJcbiAgICAgICAgICAgICAgICAgICAgfVxyXG4gICAgICAgICAgICAgICAgICAgIGVsc2UgaWYgKHdyYXBwZWRUeXBlID09IFwic3RyaW5nXCIpIHtcclxuICAgICAgICAgICAgICAgICAgICAgICAgb2JqID0gb2JqLnZhbHVlO1xyXG4gICAgICAgICAgICAgICAgICAgIH1cclxuICAgICAgICAgICAgICAgICAgICBlbHNlIHtcclxuICAgICAgICAgICAgICAgICAgICAgICAgLy8gZXZlcnl0aGluZyBlbHNlIGlzIGEgbnVtYmVyIGluIEpTXHJcbiAgICAgICAgICAgICAgICAgICAgICAgIG9iaiA9ICtvYmoudmFsdWU7XHJcbiAgICAgICAgICAgICAgICAgICAgfVxyXG4gICAgICAgICAgICAgICAgfVxyXG4gICAgICAgICAgICAgICAgZWxzZSBpZiAoU1V0aWxfMS5TVXRpbC5pc0VudW0ob2JqKSkge1xyXG4gICAgICAgICAgICAgICAgICAgIC8vIGNvbnZlcnQgZW51bXMgdG8gc3RyaW5nc1xyXG4gICAgICAgICAgICAgICAgICAgIG9iaiA9IG9iai52YWx1ZTtcclxuICAgICAgICAgICAgICAgIH1cclxuICAgICAgICAgICAgfVxyXG4gICAgICAgICAgICByZXR1cm4gb2JqO1xyXG4gICAgICAgIH07XHJcbiAgICAgICAgb2JqID0gcmVjdXJzZShvYmosIG51bGwsIG51bGwpO1xyXG4gICAgICAgIC8vIHJlc29sdmUgbGF6eSByZWZlcmVuY2VzXHJcbiAgICAgICAgZm9yICh2YXIgaSA9IDA7IGkgPCByZWZzLmxlbmd0aDsgaSsrKSB7XHJcbiAgICAgICAgICAgIHZhciByZWYgPSByZWZzW2ldO1xyXG4gICAgICAgICAgICByZWZbMF1bcmVmWzFdXSA9IG9zW3JlZlsyXV07XHJcbiAgICAgICAgfVxyXG4gICAgICAgIHJldHVybiBvYmo7XHJcbiAgICB9O1xyXG4gICAgLyoqIFRoZSByZWdpc3RlcmVkIGNsYXNzZXMuICovXHJcbiAgICBKc29uUGFyc2VyLnJlZ2lzdGVyZWRDbGFzc2VzID0ge307XHJcbiAgICByZXR1cm4gSnNvblBhcnNlcjtcclxufSgpKTtcclxuZXhwb3J0cy5Kc29uUGFyc2VyID0gSnNvblBhcnNlcjtcclxuSnNvblBhcnNlci5pbml0KCk7XHJcbi8vIyBzb3VyY2VNYXBwaW5nVVJMPUpzb25QYXJzZXIuanMubWFwIiwiXCJ1c2Ugc3RyaWN0XCI7XHJcbi8qKlxyXG4gKiAgU3RhdGljIGhlbHBlciBtZXRob2RzLlxyXG4gKi9cclxudmFyIFNVdGlsID0gKGZ1bmN0aW9uICgpIHtcclxuICAgIGZ1bmN0aW9uIFNVdGlsKCkge1xyXG4gICAgfVxyXG4gICAgLyoqXHJcbiAgICAgKiAgVGVzdCBpZiBhbiBvYmplY3QgaXMgYSBiYXNpYyB0eXBlLlxyXG4gICAgICogIEBwYXJhbSBvYmogVGhlIG9iamVjdC5cclxuICAgICAqICBAcmV0dXJuIFRydWUsIGlmIGlzIGEgYmFzaWMgdHlwZS5cclxuICAgICAqL1xyXG4gICAgU1V0aWwuaXNCYXNpY1R5cGUgPSBmdW5jdGlvbiAob2JqKSB7XHJcbiAgICAgICAgcmV0dXJuIHR5cGVvZiBvYmogIT09ICdvYmplY3QnIHx8ICFvYmo7XHJcbiAgICB9O1xyXG4gICAgLyoqXHJcbiAgICAgKiAgVGVzdCBpZiBhbiBvYmplY3QgaXMgYSBqYXZhIHdyYXBwZWQgdHlwZS5cclxuICAgICAqICBAcGFyYW0gb2JqIFRoZSBvYmplY3QuXHJcbiAgICAgKiAgQHJldHVybiBGYWxzZSwgaWYgaXMgbm90IGEgd3JhcHBlZCBwcmltaXRpdmUgdHlwZSwgZWxzZSByZXR1cm5zIHRoZSBjb3JyZXNwb25kaW5nIEpTIHR5cGUuXHJcbiAgICAgKi9cclxuICAgIFNVdGlsLmlzV3JhcHBlZFR5cGUgPSBmdW5jdGlvbiAob2JqKSB7XHJcbiAgICAgICAgaWYgKFwiX19jbGFzc25hbWVcIiBpbiBvYmopIHtcclxuICAgICAgICAgICAgdmFyIHNlYXJjaGluZyA9IG9iai5fX2NsYXNzbmFtZS5yZXBsYWNlKC9cXC4vZywgJ18nKTtcclxuICAgICAgICAgICAgcmV0dXJuIFNVdGlsLndyYXBwZWRDb252ZXJzaW9uVHlwZXNbc2VhcmNoaW5nXTtcclxuICAgICAgICB9XHJcbiAgICAgICAgZWxzZSB7XHJcbiAgICAgICAgICAgIHJldHVybiBmYWxzZTtcclxuICAgICAgICB9XHJcbiAgICB9O1xyXG4gICAgLyoqXHJcbiAgICAgKiAgQ2hlY2sgb2YgYW4gb2JqIGlzIGFuIGVudW0uXHJcbiAgICAgKi9cclxuICAgIFNVdGlsLmlzRW51bSA9IGZ1bmN0aW9uIChvYmopIHtcclxuICAgICAgICByZXR1cm4gKFwiZW51bVwiIGluIG9iaik7XHJcbiAgICB9O1xyXG4gICAgLyoqXHJcbiAgICAgKiAgVGVzdCBpZiBhbiBvYmplY3QgaXMgYW4gYXJyYXkuXHJcbiAgICAgKiAgQHBhcmFtIG9iaiBUaGUgb2JqZWN0LlxyXG4gICAgICogIEByZXR1cm4gVHJ1ZSwgaWYgaXMgYW4gYXJyYXkuXHJcbiAgICAgKi9cclxuICAgIFNVdGlsLmlzQXJyYXkgPSBmdW5jdGlvbiAob2JqKSB7XHJcbiAgICAgICAgcmV0dXJuIE9iamVjdC5wcm90b3R5cGUudG9TdHJpbmcuY2FsbChvYmopID09ICdbb2JqZWN0IEFycmF5XSc7XHJcbiAgICB9O1xyXG4gICAgLyoqXHJcbiAgICAgKiAgQ29tcHV0ZSB0aGUgYXBwcm94LiBzaXplIG9mIGFuIG9iamVjdC5cclxuICAgICAqICBAcGFyYW0gb2JqIFRoZSBvYmplY3QuXHJcbiAgICAgKi9cclxuICAgIFNVdGlsLnNpemVPZiA9IGZ1bmN0aW9uIChvYmplY3QpIHtcclxuICAgICAgICB2YXIgb2JqZWN0cyA9IFtvYmplY3RdO1xyXG4gICAgICAgIHZhciBzaXplID0gMDtcclxuICAgICAgICBmb3IgKHZhciBpID0gMDsgaSA8IG9iamVjdHMubGVuZ3RoOyBpKyspIHtcclxuICAgICAgICAgICAgc3dpdGNoICh0eXBlb2Ygb2JqZWN0c1tpXSkge1xyXG4gICAgICAgICAgICAgICAgY2FzZSAnYm9vbGVhbic6XHJcbiAgICAgICAgICAgICAgICAgICAgc2l6ZSArPSA0O1xyXG4gICAgICAgICAgICAgICAgICAgIGJyZWFrO1xyXG4gICAgICAgICAgICAgICAgY2FzZSAnbnVtYmVyJzpcclxuICAgICAgICAgICAgICAgICAgICBzaXplICs9IDg7XHJcbiAgICAgICAgICAgICAgICAgICAgYnJlYWs7XHJcbiAgICAgICAgICAgICAgICBjYXNlICdzdHJpbmcnOlxyXG4gICAgICAgICAgICAgICAgICAgIHNpemUgKz0gMiAqIG9iamVjdHNbaV0ubGVuZ3RoO1xyXG4gICAgICAgICAgICAgICAgICAgIGJyZWFrO1xyXG4gICAgICAgICAgICAgICAgY2FzZSAnb2JqZWN0JzpcclxuICAgICAgICAgICAgICAgICAgICBpZiAoT2JqZWN0LnByb3RvdHlwZS50b1N0cmluZy5jYWxsKG9iamVjdHNbaV0pICE9ICdbb2JqZWN0IEFycmF5XScpIHtcclxuICAgICAgICAgICAgICAgICAgICAgICAgZm9yICh2YXIga2V5XzEgaW4gb2JqZWN0c1tpXSlcclxuICAgICAgICAgICAgICAgICAgICAgICAgICAgIHNpemUgKz0gMiAqIGtleV8xLmxlbmd0aDtcclxuICAgICAgICAgICAgICAgICAgICB9XHJcbiAgICAgICAgICAgICAgICAgICAgdmFyIHByb2Nlc3NlZCA9IGZhbHNlO1xyXG4gICAgICAgICAgICAgICAgICAgIHZhciBrZXkgPSB2b2lkIDA7XHJcbiAgICAgICAgICAgICAgICAgICAgZm9yIChrZXkgaW4gb2JqZWN0c1tpXSkge1xyXG4gICAgICAgICAgICAgICAgICAgICAgICBmb3IgKHZhciBzZWFyY2ggPSAwOyBzZWFyY2ggPCBvYmplY3RzLmxlbmd0aDsgc2VhcmNoKyspIHtcclxuICAgICAgICAgICAgICAgICAgICAgICAgICAgIGlmIChvYmplY3RzW3NlYXJjaF0gPT09IG9iamVjdHNbaV1ba2V5XSkge1xyXG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIHByb2Nlc3NlZCA9IHRydWU7XHJcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgYnJlYWs7XHJcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICB9XHJcbiAgICAgICAgICAgICAgICAgICAgICAgIH1cclxuICAgICAgICAgICAgICAgICAgICB9XHJcbiAgICAgICAgICAgICAgICAgICAgaWYgKCFwcm9jZXNzZWQpXHJcbiAgICAgICAgICAgICAgICAgICAgICAgIG9iamVjdHMucHVzaChvYmplY3RzW2ldW2tleV0pO1xyXG4gICAgICAgICAgICB9XHJcbiAgICAgICAgfVxyXG4gICAgICAgIHJldHVybiBzaXplO1xyXG4gICAgfTtcclxuICAgIC8qKlxyXG4gICAgICogIENoZWNrIGlmIG9iamVjdCBpcyB0cnVlIGJ5IGluc3BlY3RpbmcgaWYgaXQgY29udGFpbnMgYSB0cnVlIHByb3BlcnR5LlxyXG4gICAgICovXHJcbiAgICBTVXRpbC5pc1RydWUgPSBmdW5jdGlvbiAob2JqKSB7XHJcbiAgICAgICAgcmV0dXJuIG9iaiA9PSB0cnVlIHx8IChvYmogIT0gbnVsbCAmJiBvYmouaGFzT3duUHJvcGVydHkoXCJ2YWx1ZVwiKSAmJiBvYmoudmFsdWUgPT0gdHJ1ZSk7XHJcbiAgICB9O1xyXG4gICAgLyoqXHJcbiAgICAgKiAgQXNzZXJ0IHRoYXQgdGhyb3dzIGFuIGVycm9yIGlmIG5vdCBob2xkcy5cclxuICAgICAqL1xyXG4gICAgU1V0aWwuYXNzZXJ0ID0gZnVuY3Rpb24gKGNvbmRpdGlvbiwgbWVzc2FnZSkge1xyXG4gICAgICAgIGlmICghY29uZGl0aW9uKSB7XHJcbiAgICAgICAgICAgIG1lc3NhZ2UgPSBtZXNzYWdlIHx8IFwiQXNzZXJ0aW9uIGZhaWxlZFwiO1xyXG4gICAgICAgICAgICBpZiAodHlwZW9mIEVycm9yICE9PSBcInVuZGVmaW5lZFwiKSB7XHJcbiAgICAgICAgICAgICAgICB0aHJvdyBuZXcgRXJyb3IobWVzc2FnZSk7XHJcbiAgICAgICAgICAgIH1cclxuICAgICAgICAgICAgdGhyb3cgbWVzc2FnZTsgLy8gRmFsbGJhY2tcclxuICAgICAgICB9XHJcbiAgICB9O1xyXG4gICAgLyoqXHJcbiAgICAgKiAgR2V0IHRoZSBzZXJ2aWNlIGlkIGFzIHN0cmluZy5cclxuICAgICAqICAob3RoZXJ3aXNlIGl0IGNhbm5vdCBiZSB1c2VkIGFzIGtleSBpbiBhIG1hcCBiZWNhdXNlXHJcbiAgICAgKiAgbm8gZXF1YWxzIGV4aXN0cykuXHJcbiAgICAgKi9cclxuICAgIFNVdGlsLmdldFNlcnZpY2VJZEFzU3RyaW5nID0gZnVuY3Rpb24gKHNpZCkge1xyXG4gICAgICAgIHJldHVybiBzaWQuc2VydmljZU5hbWUgKyBcIkBcIiArIHNpZC5wcm92aWRlcklkO1xyXG4gICAgfTtcclxuICAgIC8qKlxyXG4gICAgICogIEFkZCBhIGNvbnNvbGUgb3V0IGVycm9yIGhhbmRsZXIgdG8gdGhlIHByb21pc2UuXHJcbiAgICAgKi9cclxuICAgIFNVdGlsLmFkZEVyckhhbmRsZXIgPSBmdW5jdGlvbiAocCkge1xyXG4gICAgICAgIHAub2xkY2F0Y2ggPSBwLmNhdGNoO1xyXG4gICAgICAgIHAuaGFzRXJyb3JoYW5kbGVyID0gZmFsc2U7XHJcbiAgICAgICAgcC5jYXRjaCA9IGZ1bmN0aW9uIChlaCkge1xyXG4gICAgICAgICAgICBwLmhhc0Vycm9ySGFuZGxlciA9IHRydWU7XHJcbiAgICAgICAgICAgIHJldHVybiBwLm9sZGNhdGNoKGVoKTtcclxuICAgICAgICB9O1xyXG4gICAgICAgIHAub2xkY2F0Y2goZnVuY3Rpb24gKGVycikge1xyXG4gICAgICAgICAgICBpZiAoIXAuaGFzRXJyb3JIYW5kbGVyKVxyXG4gICAgICAgICAgICAgICAgY29uc29sZS5sb2coXCJFcnJvciBvY2N1cnJlZDogXCIgKyBlcnIpO1xyXG4gICAgICAgIH0pO1xyXG4gICAgICAgIHAub2xkdGhlbiA9IHAudGhlbjtcclxuICAgICAgICBwLnRoZW4gPSBmdW5jdGlvbiAodCwgZSkge1xyXG4gICAgICAgICAgICBpZiAoZSlcclxuICAgICAgICAgICAgICAgIHAuaGFzRXJyb3JIYW5kbGVyID0gdHJ1ZTtcclxuICAgICAgICAgICAgcmV0dXJuIHAub2xkdGhlbih0LCBlKTtcclxuICAgICAgICB9O1xyXG4gICAgICAgIHJldHVybiBwO1xyXG4gICAgfTtcclxuICAgIC8qKlxyXG4gICAgICogIFRlc3QgaWYgYSBudW1iZXIgaXMgYSBmbG9hdC5cclxuICAgICAqICBAcGFyYW0gbiBUaGUgbnVtYmVyIHRvIHRlc3QuXHJcbiAgICAgKiAgQHJldHVybiBUcnVlLCBpZiBpcyBmbG9hdC5cclxuICAgICAqL1xyXG4gICAgU1V0aWwuaXNGbG9hdCA9IGZ1bmN0aW9uIChuKSB7XHJcbiAgICAgICAgcmV0dXJuIG4gPT09ICtuICYmIG4gIT09IChuIHwgMCk7XHJcbiAgICB9O1xyXG4gICAgLyoqXHJcbiAgICAgKiAgVGVzdCBpZiBhIG51bWJlciBpcyBhbiBpbnRlZ2VyLlxyXG4gICAgICogIEBwYXJhbSBuIFRoZSBudW1iZXIgdG8gdGVzdC5cclxuICAgICAqICBAcmV0dXJuIFRydWUsIGlmIGlzIGludGVnZXIuXHJcbiAgICAgKi9cclxuICAgIFNVdGlsLmlzSW50ZWdlciA9IGZ1bmN0aW9uIChuKSB7XHJcbiAgICAgICAgcmV0dXJuIG4gPT09ICtuICYmIG4gPT09IChuIHwgMCk7XHJcbiAgICB9O1xyXG4gICAgLyoqXHJcbiAgICAgKiAgQ2hlY2sgaWYgYW4gb2JqZWN0IGlzIGNvbnRhaW5lZCBpbiBhbiBhcnJheS5cclxuICAgICAqICBVc2VzIGVxdWFsIGZ1bmN0aW9uIHRvIGNoZWNrIGVxdWFsaXR5IG9mIG9iamVjdHMuXHJcbiAgICAgKiAgSWYgbm90IHByb3ZpZGVkIHVzZXMgcmVmZXJlbmNlIHRlc3QuXHJcbiAgICAgKiAgQHBhcmFtIG9iamVjdCBUaGUgb2JqZWN0IHRvIGNoZWNrLlxyXG4gICAgICogIEBwYXJhbSBvYmplY3RzIFRoZSBhcnJheS5cclxuICAgICAqICBAcGFyYW0gZXF1YWxzIFRoZSBlcXVhbHMgbWV0aG9kLlxyXG4gICAgICogIEByZXR1cm4gVHJ1ZSwgaWYgaXMgY29udGFpbmVkLlxyXG4gICAgICovXHJcbiAgICBTVXRpbC5jb250YWluc09iamVjdCA9IGZ1bmN0aW9uIChvYmplY3QsIG9iamVjdHMsIGVxdWFscykge1xyXG4gICAgICAgIHZhciByZXQgPSBmYWxzZTtcclxuICAgICAgICBmb3IgKHZhciBpID0gMDsgaSA8IG9iamVjdHMubGVuZ3RoICYmICFyZXQ7IGkrKykge1xyXG4gICAgICAgICAgICByZXQgPSBlcXVhbHMgPyBlcXVhbHMob2JqZWN0LCBvYmplY3RzW2ldKSA6IG9iamVjdCA9PT0gb2JqZWN0c1tpXTtcclxuICAgICAgICB9XHJcbiAgICAgICAgcmV0dXJuIHJldDtcclxuICAgIH07XHJcbiAgICAvKipcclxuICAgICAqICBHZXQgdGhlIGluZGV4IG9mIGFuIG9iamVjdCBpbiBhbiBhcnJheS4gLTEgZm9yIG5vdCBjb250YWluZWQuXHJcbiAgICAgKiAgQHBhcmFtIG9iamVjdCBUaGUgb2JqZWN0IHRvIGNoZWNrLlxyXG4gICAgICogIEBwYXJhbSBvYmplY3RzIFRoZSBhcnJheS5cclxuICAgICAqICBAcGFyYW0gZXF1YWxzIFRoZSBlcXVhbHMgbWV0aG9kLlxyXG4gICAgICogIEByZXR1cm4gVGhlIGluZGV4IG9yIC0xLlxyXG4gICAgICovXHJcbiAgICBTVXRpbC5pbmRleE9mT2JqZWN0ID0gZnVuY3Rpb24gKG9iamVjdCwgb2JqZWN0cywgZXF1YWxzKSB7XHJcbiAgICAgICAgdmFyIHJldCA9IC0xO1xyXG4gICAgICAgIGZvciAodmFyIGkgPSAwOyBpIDwgb2JqZWN0cy5sZW5ndGg7IGkrKykge1xyXG4gICAgICAgICAgICBpZiAoZXF1YWxzID8gZXF1YWxzKG9iamVjdCwgb2JqZWN0c1tpXSkgOiBvYmplY3QgPT09IG9iamVjdHNbaV0pIHtcclxuICAgICAgICAgICAgICAgIHJldCA9IGk7XHJcbiAgICAgICAgICAgICAgICBicmVhaztcclxuICAgICAgICAgICAgfVxyXG4gICAgICAgIH1cclxuICAgICAgICByZXR1cm4gcmV0O1xyXG4gICAgfTtcclxuICAgIC8qKlxyXG4gICAgICogIFJlbW92ZSBhbiBvYmplY3QgZnJvbSBhbiBhcnJheS5cclxuICAgICAqICBAcGFyYW0gb2JqZWN0IFRoZSBvYmplY3QgdG8gcmVtb3ZlLlxyXG4gICAgICogIEBwYXJhbSBvYmplY3RzIFRoZSBhcnJheS5cclxuICAgICAqICBAcGFyYW0gZXF1YWxzIFRoZSBlcXVhbHMgbWV0aG9kLlxyXG4gICAgICogIEByZXR1cm4gVHJ1ZSwgaWYgd2FzIHJlbW92ZWQuXHJcbiAgICAgKi9cclxuICAgIFNVdGlsLnJlbW92ZU9iamVjdCA9IGZ1bmN0aW9uIChvYmplY3QsIG9iamVjdHMsIGVxdWFscykge1xyXG4gICAgICAgIHZhciByZXQgPSBTVXRpbC5pbmRleE9mT2JqZWN0KG9iamVjdCwgb2JqZWN0cywgZXF1YWxzKTtcclxuICAgICAgICBpZiAocmV0ICE9IC0xKVxyXG4gICAgICAgICAgICBvYmplY3RzLnNwbGljZShyZXQsIDEpO1xyXG4gICAgICAgIHJldHVybiByZXQgPT0gLTEgPyBmYWxzZSA6IHRydWU7XHJcbiAgICB9O1xyXG4gICAgLyoqXHJcbiAgICAgKiAgQ2hlY2sgaWYgdGhlIGNhbGwgd2FzIGh0dHBzLlxyXG4gICAgICogIEByZXR1cm4gVHJ1ZSBpZiBodHRwcy5cclxuICAgICAqL1xyXG4gICAgU1V0aWwuaXNTZWN1cmUgPSBmdW5jdGlvbiAoKSB7XHJcbiAgICAgICAgcmV0dXJuIHdpbmRvdy5sb2NhdGlvbi5wcm90b2NvbCA9PSAnaHR0cHM6JztcclxuICAgIH07XHJcbiAgICBTVXRpbC53cmFwcGVkQ29udmVyc2lvblR5cGVzID0ge1xyXG4gICAgICAgIGphdmFfbGFuZ19JbnRlZ2VyOiBcIm51bWJlclwiLFxyXG4gICAgICAgIGphdmFfbGFuZ19CeXRlOiBcIm51bWJlclwiLFxyXG4gICAgICAgIGphdmFfbGFuZ19TaG9ydDogXCJudW1iZXJcIixcclxuICAgICAgICBqYXZhX2xhbmdfTG9uZzogXCJudW1iZXJcIixcclxuICAgICAgICBqYXZhX2xhbmdfRmxvYXQ6IFwibnVtYmVyXCIsXHJcbiAgICAgICAgamF2YV9sYW5nX0RvdWJsZTogXCJudW1iZXJcIixcclxuICAgICAgICBqYXZhX2xhbmdfQ2hhcmFjdGVyOiBcInN0cmluZ1wiLFxyXG4gICAgICAgIGphdmFfbGFuZ19Cb29sZWFuOiBcImJvb2xlYW5cIlxyXG4gICAgfTtcclxuICAgIHJldHVybiBTVXRpbDtcclxufSgpKTtcclxuZXhwb3J0cy5TVXRpbCA9IFNVdGlsO1xyXG4vLyMgc291cmNlTWFwcGluZ1VSTD1TVXRpbC5qcy5tYXAiLCJcInVzZSBzdHJpY3RcIjtcclxudmFyIFNjb3BlcyA9IChmdW5jdGlvbiAoKSB7XHJcbiAgICBmdW5jdGlvbiBTY29wZXMoKSB7XHJcbiAgICB9XHJcbiAgICAvL1x0LyoqIE5vbmUgY29tcG9uZW50IHNjb3BlIChub3RoaW5nIHdpbGwgYmUgc2VhcmNoZWQpLiAqL1xyXG4gICAgLy9cdGNvbnN0IFNDT1BFX05PTkUgPSBcIm5vbmVcIjtcclxuICAgIC8vXHJcbiAgICAvL1x0LyoqIExvY2FsIGNvbXBvbmVudCBzY29wZS4gKi9cclxuICAgIC8vXHRjb25zdCBTQ09QRV9MT0NBTCA9IFwibG9jYWxcIjtcclxuICAgIC8vXHJcbiAgICAvL1x0LyoqIENvbXBvbmVudCBzY29wZS4gKi9cclxuICAgIC8vXHRjb25zdCBTQ09QRV9DT01QT05FTlQgPSBcImNvbXBvbmVudFwiO1xyXG4gICAgLy9cclxuICAgIC8vXHQvKiogQXBwbGljYXRpb24gc2NvcGUuICovXHJcbiAgICAvL1x0Y29uc3QgU0NPUEVfQVBQTElDQVRJT04gPSBcImFwcGxpY2F0aW9uXCI7XHJcbiAgICAvKiogUGxhdGZvcm0gc2NvcGUuICovXHJcbiAgICBTY29wZXMuU0NPUEVfUExBVEZPUk0gPSBcInBsYXRmb3JtXCI7XHJcbiAgICAvKiogR2xvYmFsIHNjb3BlLiAqL1xyXG4gICAgU2NvcGVzLlNDT1BFX0dMT0JBTCA9IFwiZ2xvYmFsXCI7XHJcbiAgICAvL1x0LyoqIFBhcmVudCBzY29wZS4gKi9cclxuICAgIC8vXHRTQ09QRV9QQVJFTlQ6c3RyaW5nID0gXCJwYXJlbnRcIjtcclxuICAgIC8qKiBTZXNzaW9uIHNjb3BlLiAqL1xyXG4gICAgU2NvcGVzLlNDT1BFX1NFU1NJT04gPSBcInNlc3Npb25cIjtcclxuICAgIHJldHVybiBTY29wZXM7XHJcbn0oKSk7XHJcbmV4cG9ydHMuU2NvcGVzID0gU2NvcGVzO1xyXG4vLyMgc291cmNlTWFwcGluZ1VSTD1TY29wZXMuanMubWFwIiwiXCJ1c2Ugc3RyaWN0XCI7XHJcbnZhciBKYWRleFByb21pc2VfMSA9IHJlcXVpcmUoXCIuL0phZGV4UHJvbWlzZVwiKTtcclxudmFyIEphZGV4Q29ubmVjdGlvbkhhbmRsZXJfMSA9IHJlcXVpcmUoXCIuL3dlYnNvY2tldC9KYWRleENvbm5lY3Rpb25IYW5kbGVyXCIpO1xyXG52YXIgU2VydmljZUludm9jYXRpb25NZXNzYWdlXzEgPSByZXF1aXJlKFwiLi9tZXNzYWdlcy9TZXJ2aWNlSW52b2NhdGlvbk1lc3NhZ2VcIik7XHJcbnZhciBTZXJ2aWNlUHJveHkgPSAoZnVuY3Rpb24gKCkge1xyXG4gICAgLyoqXHJcbiAgICAgKiAgQ3JlYXRlIGEgc2VydmljZSBwcm94eSBmb3IgYSBKYWRleCBzZXJ2aWNlLlxyXG4gICAgICovXHJcbiAgICBmdW5jdGlvbiBTZXJ2aWNlUHJveHkoc2VydmljZUlkLCBtZXRob2ROYW1lcywgdXJsKSB7XHJcbiAgICAgICAgdGhpcy5zZXJ2aWNlSWQgPSBzZXJ2aWNlSWQ7XHJcbiAgICAgICAgdGhpcy51cmwgPSB1cmw7XHJcbiAgICAgICAgLy8gR2VuZXJpYyBpbnZva2UgbWV0aG9kIGNhbGxlZCBvbiBlYWNoIHNlcnZpY2UgaW52b2NhdGlvblxyXG4gICAgICAgIGZvciAodmFyIGkgPSAwOyBpIDwgbWV0aG9kTmFtZXMubGVuZ3RoOyBpKyspIHtcclxuICAgICAgICAgICAgdGhpc1ttZXRob2ROYW1lc1tpXV0gPSB0aGlzLmNyZWF0ZU1ldGhvZChtZXRob2ROYW1lc1tpXSk7XHJcbiAgICAgICAgfVxyXG4gICAgfVxyXG4gICAgLyoqXHJcbiAgICAgKiAgR2VuZXJpYyBpbnZva2UgbWV0aG9kIHRoYXQgc2VuZHMgYSBtZXRob2QgY2FsbCB0byB0aGUgc2VydmVyIHNpZGUuXHJcbiAgICAgKi9cclxuICAgIFNlcnZpY2VQcm94eS5wcm90b3R5cGUuaW52b2tlID0gZnVuY3Rpb24gKG5hbWUsIHBhcmFtcywgY2FsbGJhY2spIHtcclxuICAgICAgICB2YXIgcmV0ID0gbmV3IEphZGV4UHJvbWlzZV8xLkphZGV4UHJvbWlzZSh0aGlzLnVybCk7XHJcbiAgICAgICAgdmFyIGNvbm0gPSBKYWRleENvbm5lY3Rpb25IYW5kbGVyXzEuSmFkZXhDb25uZWN0aW9uSGFuZGxlci5nZXRJbnN0YW5jZSgpO1xyXG4gICAgICAgIC8vIENvbnZlcnQgcGFyYW1ldGVycyBzZXBlcmF0ZWx5LCBvbmUgYnkgb25lXHJcbiAgICAgICAgdmFyIGNwYXJhbXMgPSBbXTtcclxuICAgICAgICBmb3IgKHZhciBpID0gMDsgaSA8IHBhcmFtcy5sZW5ndGg7IGkrKykge1xyXG4gICAgICAgICAgICBjcGFyYW1zLnB1c2goY29ubS5vYmplY3RUb0pzb24ocGFyYW1zW2ldKSk7XHJcbiAgICAgICAgfVxyXG4gICAgICAgIHZhciBjbWQgPSBuZXcgU2VydmljZUludm9jYXRpb25NZXNzYWdlXzEuU2VydmljZUludm9jYXRpb25NZXNzYWdlKHRoaXMuc2VydmljZUlkLCBuYW1lLCBjcGFyYW1zKTtcclxuICAgICAgICAvLyBjb25zb2xlLmxvZyhjbWQpO1xyXG4gICAgICAgIC8vIHdyYXAgY2FsbGJhY2sgdG8gYWxsb3cgSmFkZXhQcm9taXNlLmludGVybWVkaWF0ZVRoZW5cclxuICAgICAgICB2YXIgd3JhcENiID0gZnVuY3Rpb24gKGludGVybWVkaWF0ZVJlc3VsdCkge1xyXG4gICAgICAgICAgICAvLyBjb25zb2xlLmxvZyhcImNhbGxpbmcgaW50ZXJtZWRpYXRlIHJlc3VsdCB3aXRoOiBcIiArIGludGVybWVkaWF0ZVJlc3VsdCk7XHJcbiAgICAgICAgICAgIHJldC5yZXNvbHZlSW50ZXJtZWRpYXRlKGludGVybWVkaWF0ZVJlc3VsdCk7XHJcbiAgICAgICAgICAgIGlmIChjYWxsYmFjaykge1xyXG4gICAgICAgICAgICAgICAgY2FsbGJhY2soaW50ZXJtZWRpYXRlUmVzdWx0KTtcclxuICAgICAgICAgICAgfVxyXG4gICAgICAgIH07XHJcbiAgICAgICAgcmV0LmNhbGxpZCA9IGNvbm0uc2VuZE1lc3NhZ2UodGhpcy51cmwsIGNtZCwgXCJpbnZva2VcIiwgZnVuY3Rpb24gKHJlcykgeyByZXR1cm4gcmV0LnJlc29sdmUocmVzKTsgfSwgZnVuY3Rpb24gKGV4KSB7IHJldHVybiByZXQucmVqZWN0KGV4KTsgfSwgd3JhcENiKTtcclxuICAgICAgICByZXR1cm4gcmV0O1xyXG4gICAgfTtcclxuICAgIC8qKlxyXG4gICAgICogIENyZWF0ZSBtZXRob2QgZnVuY3Rpb24gKG5lZWRlZCB0byBwcmVzZXJ2ZSB0aGUgbmFtZSkuXHJcbiAgICAgKlxyXG4gICAgICogIENyZWF0ZXMgYW4gYXJndW1lbnQgYXJyYXkgYW5kIGludm9rZXMgZ2VuZXJpYyBpbnZva2UgbWV0aG9kLlxyXG4gICAgICpcclxuICAgICAqICBUT0RPOiBjYWxsYmFjayBmdW5jdGlvbiBoYWNrIVxyXG4gICAgICovXHJcbiAgICBTZXJ2aWNlUHJveHkucHJvdG90eXBlLmNyZWF0ZU1ldGhvZCA9IGZ1bmN0aW9uIChuYW1lKSB7XHJcbiAgICAgICAgdmFyIG91dGVyID0gdGhpcztcclxuICAgICAgICByZXR1cm4gZnVuY3Rpb24gKCkge1xyXG4gICAgICAgICAgICB2YXIgcGFyYW1zID0gW107XHJcbiAgICAgICAgICAgIHZhciBjYWxsYmFjaztcclxuICAgICAgICAgICAgZm9yICh2YXIgaiA9IDA7IGogPCBhcmd1bWVudHMubGVuZ3RoOyBqKyspIHtcclxuICAgICAgICAgICAgICAgIGlmICh0eXBlb2YgYXJndW1lbnRzW2pdID09PSBcImZ1bmN0aW9uXCIpIHtcclxuICAgICAgICAgICAgICAgICAgICBjYWxsYmFjayA9IGFyZ3VtZW50c1tqXTtcclxuICAgICAgICAgICAgICAgIH1cclxuICAgICAgICAgICAgICAgIGVsc2Uge1xyXG4gICAgICAgICAgICAgICAgICAgIHBhcmFtcy5wdXNoKGFyZ3VtZW50c1tqXSk7XHJcbiAgICAgICAgICAgICAgICB9XHJcbiAgICAgICAgICAgIH1cclxuICAgICAgICAgICAgcmV0dXJuIG91dGVyLmludm9rZShuYW1lLCBwYXJhbXMsIGNhbGxiYWNrKTtcclxuICAgICAgICB9O1xyXG4gICAgfTtcclxuICAgIHJldHVybiBTZXJ2aWNlUHJveHk7XHJcbn0oKSk7XHJcbmV4cG9ydHMuU2VydmljZVByb3h5ID0gU2VydmljZVByb3h5O1xyXG4vLyMgc291cmNlTWFwcGluZ1VSTD1TZXJ2aWNlUHJveHkuanMubWFwIiwiXCJ1c2Ugc3RyaWN0XCI7XHJcbnZhciBTZXJ2aWNlTWVzc2FnZSA9IChmdW5jdGlvbiAoKSB7XHJcbiAgICBmdW5jdGlvbiBTZXJ2aWNlTWVzc2FnZShfX2NsYXNzbmFtZSkge1xyXG4gICAgICAgIHRoaXMuX19jbGFzc25hbWUgPSBfX2NsYXNzbmFtZTtcclxuICAgIH1cclxuICAgIHJldHVybiBTZXJ2aWNlTWVzc2FnZTtcclxufSgpKTtcclxuZXhwb3J0cy5TZXJ2aWNlTWVzc2FnZSA9IFNlcnZpY2VNZXNzYWdlO1xyXG4vLyMgc291cmNlTWFwcGluZ1VSTD1CYXNlTWVzc2FnZS5qcy5tYXAiLCJcInVzZSBzdHJpY3RcIjtcclxudmFyIF9fZXh0ZW5kcyA9ICh0aGlzICYmIHRoaXMuX19leHRlbmRzKSB8fCBmdW5jdGlvbiAoZCwgYikge1xyXG4gICAgZm9yICh2YXIgcCBpbiBiKSBpZiAoYi5oYXNPd25Qcm9wZXJ0eShwKSkgZFtwXSA9IGJbcF07XHJcbiAgICBmdW5jdGlvbiBfXygpIHsgdGhpcy5jb25zdHJ1Y3RvciA9IGQ7IH1cclxuICAgIGQucHJvdG90eXBlID0gYiA9PT0gbnVsbCA/IE9iamVjdC5jcmVhdGUoYikgOiAoX18ucHJvdG90eXBlID0gYi5wcm90b3R5cGUsIG5ldyBfXygpKTtcclxufTtcclxudmFyIEJhc2VNZXNzYWdlXzEgPSByZXF1aXJlKFwiLi9CYXNlTWVzc2FnZVwiKTtcclxudmFyIFBhcnRpYWxNZXNzYWdlID0gKGZ1bmN0aW9uIChfc3VwZXIpIHtcclxuICAgIF9fZXh0ZW5kcyhQYXJ0aWFsTWVzc2FnZSwgX3N1cGVyKTtcclxuICAgIGZ1bmN0aW9uIFBhcnRpYWxNZXNzYWdlKGNhbGxpZCwgZGF0YSwgbnVtYmVyLCBjb3VudCkge1xyXG4gICAgICAgIF9zdXBlci5jYWxsKHRoaXMsIFwiY29tLmFjdG9yb24ud2Vic2VydmljZS5tZXNzYWdlcy5QYXJ0aWFsTWVzc2FnZVwiKTtcclxuICAgICAgICB0aGlzLmNhbGxpZCA9IGNhbGxpZDtcclxuICAgICAgICB0aGlzLmRhdGEgPSBkYXRhO1xyXG4gICAgICAgIHRoaXMubnVtYmVyID0gbnVtYmVyO1xyXG4gICAgICAgIHRoaXMuY291bnQgPSBjb3VudDtcclxuICAgIH1cclxuICAgIHJldHVybiBQYXJ0aWFsTWVzc2FnZTtcclxufShCYXNlTWVzc2FnZV8xLlNlcnZpY2VNZXNzYWdlKSk7XHJcbmV4cG9ydHMuUGFydGlhbE1lc3NhZ2UgPSBQYXJ0aWFsTWVzc2FnZTtcclxuLy8jIHNvdXJjZU1hcHBpbmdVUkw9UGFydGlhbE1lc3NhZ2UuanMubWFwIiwiXCJ1c2Ugc3RyaWN0XCI7XHJcbnZhciBfX2V4dGVuZHMgPSAodGhpcyAmJiB0aGlzLl9fZXh0ZW5kcykgfHwgZnVuY3Rpb24gKGQsIGIpIHtcclxuICAgIGZvciAodmFyIHAgaW4gYikgaWYgKGIuaGFzT3duUHJvcGVydHkocCkpIGRbcF0gPSBiW3BdO1xyXG4gICAgZnVuY3Rpb24gX18oKSB7IHRoaXMuY29uc3RydWN0b3IgPSBkOyB9XHJcbiAgICBkLnByb3RvdHlwZSA9IGIgPT09IG51bGwgPyBPYmplY3QuY3JlYXRlKGIpIDogKF9fLnByb3RvdHlwZSA9IGIucHJvdG90eXBlLCBuZXcgX18oKSk7XHJcbn07XHJcbnZhciBCYXNlTWVzc2FnZV8xID0gcmVxdWlyZShcIi4vQmFzZU1lc3NhZ2VcIik7XHJcbnZhciBTZXJ2aWNlSW52b2NhdGlvbk1lc3NhZ2UgPSAoZnVuY3Rpb24gKF9zdXBlcikge1xyXG4gICAgX19leHRlbmRzKFNlcnZpY2VJbnZvY2F0aW9uTWVzc2FnZSwgX3N1cGVyKTtcclxuICAgIGZ1bmN0aW9uIFNlcnZpY2VJbnZvY2F0aW9uTWVzc2FnZShzZXJ2aWNlSWQsIG1ldGhvZE5hbWUsIHBhcmFtZXRlclZhbHVlcykge1xyXG4gICAgICAgIF9zdXBlci5jYWxsKHRoaXMsIFwiY29tLmFjdG9yb24ud2Vic2VydmljZS5tZXNzYWdlcy5TZXJ2aWNlSW52b2NhdGlvbk1lc3NhZ2VcIik7XHJcbiAgICAgICAgdGhpcy5zZXJ2aWNlSWQgPSBzZXJ2aWNlSWQ7XHJcbiAgICAgICAgdGhpcy5tZXRob2ROYW1lID0gbWV0aG9kTmFtZTtcclxuICAgICAgICB0aGlzLnBhcmFtZXRlclZhbHVlcyA9IHBhcmFtZXRlclZhbHVlcztcclxuICAgIH1cclxuICAgIHJldHVybiBTZXJ2aWNlSW52b2NhdGlvbk1lc3NhZ2U7XHJcbn0oQmFzZU1lc3NhZ2VfMS5TZXJ2aWNlTWVzc2FnZSkpO1xyXG5leHBvcnRzLlNlcnZpY2VJbnZvY2F0aW9uTWVzc2FnZSA9IFNlcnZpY2VJbnZvY2F0aW9uTWVzc2FnZTtcclxuLy8jIHNvdXJjZU1hcHBpbmdVUkw9U2VydmljZUludm9jYXRpb25NZXNzYWdlLmpzLm1hcCIsIlwidXNlIHN0cmljdFwiO1xyXG52YXIgX19leHRlbmRzID0gKHRoaXMgJiYgdGhpcy5fX2V4dGVuZHMpIHx8IGZ1bmN0aW9uIChkLCBiKSB7XHJcbiAgICBmb3IgKHZhciBwIGluIGIpIGlmIChiLmhhc093blByb3BlcnR5KHApKSBkW3BdID0gYltwXTtcclxuICAgIGZ1bmN0aW9uIF9fKCkgeyB0aGlzLmNvbnN0cnVjdG9yID0gZDsgfVxyXG4gICAgZC5wcm90b3R5cGUgPSBiID09PSBudWxsID8gT2JqZWN0LmNyZWF0ZShiKSA6IChfXy5wcm90b3R5cGUgPSBiLnByb3RvdHlwZSwgbmV3IF9fKCkpO1xyXG59O1xyXG52YXIgQmFzZU1lc3NhZ2VfMSA9IHJlcXVpcmUoXCIuL0Jhc2VNZXNzYWdlXCIpO1xyXG52YXIgU2VydmljZVByb3ZpZGVNZXNzYWdlID0gKGZ1bmN0aW9uIChfc3VwZXIpIHtcclxuICAgIF9fZXh0ZW5kcyhTZXJ2aWNlUHJvdmlkZU1lc3NhZ2UsIF9zdXBlcik7XHJcbiAgICBmdW5jdGlvbiBTZXJ2aWNlUHJvdmlkZU1lc3NhZ2UodHlwZSwgc2NvcGUsIHRhZ3MpIHtcclxuICAgICAgICBfc3VwZXIuY2FsbCh0aGlzLCBcImNvbS5hY3Rvcm9uLndlYnNlcnZpY2UubWVzc2FnZXMuU2VydmljZVByb3ZpZGVNZXNzYWdlXCIpO1xyXG4gICAgICAgIHRoaXMudHlwZSA9IHR5cGU7XHJcbiAgICAgICAgdGhpcy5zY29wZSA9IHNjb3BlO1xyXG4gICAgICAgIHRoaXMudGFncyA9IHRhZ3M7XHJcbiAgICB9XHJcbiAgICByZXR1cm4gU2VydmljZVByb3ZpZGVNZXNzYWdlO1xyXG59KEJhc2VNZXNzYWdlXzEuU2VydmljZU1lc3NhZ2UpKTtcclxuZXhwb3J0cy5TZXJ2aWNlUHJvdmlkZU1lc3NhZ2UgPSBTZXJ2aWNlUHJvdmlkZU1lc3NhZ2U7XHJcbi8vIyBzb3VyY2VNYXBwaW5nVVJMPVNlcnZpY2VQcm92aWRlTWVzc2FnZS5qcy5tYXAiLCJcInVzZSBzdHJpY3RcIjtcclxudmFyIF9fZXh0ZW5kcyA9ICh0aGlzICYmIHRoaXMuX19leHRlbmRzKSB8fCBmdW5jdGlvbiAoZCwgYikge1xyXG4gICAgZm9yICh2YXIgcCBpbiBiKSBpZiAoYi5oYXNPd25Qcm9wZXJ0eShwKSkgZFtwXSA9IGJbcF07XHJcbiAgICBmdW5jdGlvbiBfXygpIHsgdGhpcy5jb25zdHJ1Y3RvciA9IGQ7IH1cclxuICAgIGQucHJvdG90eXBlID0gYiA9PT0gbnVsbCA/IE9iamVjdC5jcmVhdGUoYikgOiAoX18ucHJvdG90eXBlID0gYi5wcm90b3R5cGUsIG5ldyBfXygpKTtcclxufTtcclxudmFyIEJhc2VNZXNzYWdlXzEgPSByZXF1aXJlKFwiLi9CYXNlTWVzc2FnZVwiKTtcclxudmFyIFNlcnZpY2VTZWFyY2hNZXNzYWdlID0gKGZ1bmN0aW9uIChfc3VwZXIpIHtcclxuICAgIF9fZXh0ZW5kcyhTZXJ2aWNlU2VhcmNoTWVzc2FnZSwgX3N1cGVyKTtcclxuICAgIGZ1bmN0aW9uIFNlcnZpY2VTZWFyY2hNZXNzYWdlKHR5cGUsIG11bHRpcGxlLCBzY29wZSkge1xyXG4gICAgICAgIF9zdXBlci5jYWxsKHRoaXMsIFwiY29tLmFjdG9yb24ud2Vic2VydmljZS5tZXNzYWdlcy5TZXJ2aWNlU2VhcmNoTWVzc2FnZVwiKTtcclxuICAgICAgICB0aGlzLnR5cGUgPSB0eXBlO1xyXG4gICAgICAgIHRoaXMubXVsdGlwbGUgPSBtdWx0aXBsZTtcclxuICAgICAgICB0aGlzLnNjb3BlID0gc2NvcGU7XHJcbiAgICB9XHJcbiAgICByZXR1cm4gU2VydmljZVNlYXJjaE1lc3NhZ2U7XHJcbn0oQmFzZU1lc3NhZ2VfMS5TZXJ2aWNlTWVzc2FnZSkpO1xyXG5leHBvcnRzLlNlcnZpY2VTZWFyY2hNZXNzYWdlID0gU2VydmljZVNlYXJjaE1lc3NhZ2U7XHJcbi8vIyBzb3VyY2VNYXBwaW5nVVJMPVNlcnZpY2VTZWFyY2hNZXNzYWdlLmpzLm1hcCIsIlwidXNlIHN0cmljdFwiO1xyXG52YXIgX19leHRlbmRzID0gKHRoaXMgJiYgdGhpcy5fX2V4dGVuZHMpIHx8IGZ1bmN0aW9uIChkLCBiKSB7XHJcbiAgICBmb3IgKHZhciBwIGluIGIpIGlmIChiLmhhc093blByb3BlcnR5KHApKSBkW3BdID0gYltwXTtcclxuICAgIGZ1bmN0aW9uIF9fKCkgeyB0aGlzLmNvbnN0cnVjdG9yID0gZDsgfVxyXG4gICAgZC5wcm90b3R5cGUgPSBiID09PSBudWxsID8gT2JqZWN0LmNyZWF0ZShiKSA6IChfXy5wcm90b3R5cGUgPSBiLnByb3RvdHlwZSwgbmV3IF9fKCkpO1xyXG59O1xyXG52YXIgQmFzZU1lc3NhZ2VfMSA9IHJlcXVpcmUoXCIuL0Jhc2VNZXNzYWdlXCIpO1xyXG52YXIgU2VydmljZVVucHJvdmlkZU1lc3NhZ2UgPSAoZnVuY3Rpb24gKF9zdXBlcikge1xyXG4gICAgX19leHRlbmRzKFNlcnZpY2VVbnByb3ZpZGVNZXNzYWdlLCBfc3VwZXIpO1xyXG4gICAgZnVuY3Rpb24gU2VydmljZVVucHJvdmlkZU1lc3NhZ2Uoc2VydmljZUlkKSB7XHJcbiAgICAgICAgX3N1cGVyLmNhbGwodGhpcywgXCJjb20uYWN0b3Jvbi53ZWJzZXJ2aWNlLm1lc3NhZ2VzLlNlcnZpY2VVbnByb3ZpZGVNZXNzYWdlXCIpO1xyXG4gICAgICAgIHRoaXMuc2VydmljZUlkID0gc2VydmljZUlkO1xyXG4gICAgfVxyXG4gICAgcmV0dXJuIFNlcnZpY2VVbnByb3ZpZGVNZXNzYWdlO1xyXG59KEJhc2VNZXNzYWdlXzEuU2VydmljZU1lc3NhZ2UpKTtcclxuZXhwb3J0cy5TZXJ2aWNlVW5wcm92aWRlTWVzc2FnZSA9IFNlcnZpY2VVbnByb3ZpZGVNZXNzYWdlO1xyXG4vLyMgc291cmNlTWFwcGluZ1VSTD1TZXJ2aWNlVW5wcm92aWRlTWVzc2FnZS5qcy5tYXAiLCJcInVzZSBzdHJpY3RcIjtcclxudmFyIFNVdGlsXzEgPSByZXF1aXJlKFwiLi4vU1V0aWxcIik7XHJcbnZhciBDb25uZWN0aW9uSGFuZGxlciA9IChmdW5jdGlvbiAoKSB7XHJcbiAgICBmdW5jdGlvbiBDb25uZWN0aW9uSGFuZGxlcigpIHtcclxuICAgICAgICAvKiogVGhlIHdlYnNvY2tldCBjb25uZWN0aW9ucy4gKi9cclxuICAgICAgICB0aGlzLmNvbm5lY3Rpb25zID0gW107XHJcbiAgICAgICAgdmFyIHNjcmlwdHMgPSBkb2N1bWVudC5nZXRFbGVtZW50c0J5VGFnTmFtZSgnc2NyaXB0Jyk7XHJcbiAgICAgICAgdmFyIHNjcmlwdCA9IHNjcmlwdHNbc2NyaXB0cy5sZW5ndGggLSAxXTtcclxuICAgICAgICB2YXIgcHJvdCA9IFNVdGlsXzEuU1V0aWwuaXNTZWN1cmUoKSA/IFwid3NzXCIgOiBcIndzXCI7XHJcbiAgICAgICAgaWYgKHNjcmlwdFtcInNyY1wiXSkge1xyXG4gICAgICAgICAgICB0aGlzLmJhc2V1cmwgPSBzY3JpcHRbXCJzcmNcIl07XHJcbiAgICAgICAgICAgIHRoaXMuYmFzZXVybCA9IHByb3QgKyB0aGlzLmJhc2V1cmwuc3Vic3RyaW5nKHRoaXMuYmFzZXVybC5pbmRleE9mKFwiOi8vXCIpKTtcclxuICAgICAgICB9XHJcbiAgICAgICAgZWxzZSBpZiAoc2NyaXB0Lmhhc0F0dHJpYnV0ZXMoKSkge1xyXG4gICAgICAgICAgICAvL3RoaXMuYmFzZXVybCA9IFwid3M6Ly9cIiArIHdpbmRvdy5sb2NhdGlvbi5ob3N0bmFtZSArIFwiOlwiICsgd2luZG93LmxvY2F0aW9uLnBvcnQgKyBcIi93c3dlYmFwaVwiO1xyXG4gICAgICAgICAgICB0aGlzLmJhc2V1cmwgPSBwcm90ICsgXCI6Ly9cIiArIHdpbmRvdy5sb2NhdGlvbi5ob3N0bmFtZSArIFwiOlwiICsgd2luZG93LmxvY2F0aW9uLnBvcnQgKyBzY3JpcHQuYXR0cmlidXRlcy5nZXROYW1lZEl0ZW0oXCJzcmNcIikudmFsdWU7XHJcbiAgICAgICAgfVxyXG4gICAgICAgIGVsc2Uge1xyXG4gICAgICAgICAgICAvLyBmYWlsP1xyXG4gICAgICAgICAgICB0aHJvdyBuZXcgRXJyb3IoXCJDb3VsZCBub3QgZmluZCB3ZWJzb2NrZXQgdXJsXCIpO1xyXG4gICAgICAgIH1cclxuICAgICAgICB0aGlzLmJhc2V1cmwgPSB0aGlzLmJhc2V1cmwuc3Vic3RyaW5nKDAsIHRoaXMuYmFzZXVybC5sYXN0SW5kZXhPZihcImphZGV4LmpzXCIpIC0gMSk7XHJcbiAgICAgICAgdGhpcy5jb25uZWN0aW9uc1tcIlwiXSA9IHRoaXMuYWRkQ29ubmVjdGlvbih0aGlzLmJhc2V1cmwpO1xyXG4gICAgICAgIC8vdGhpcy5jb25uZWN0aW9uc1t1bmRlZmluZWRdID0gdGhpcy5jb25uZWN0aW9uc1tudWxsXTtcclxuICAgIH1cclxuICAgIC8qKlxyXG4gICAgICogIEludGVybmFsIGZ1bmN0aW9uIHRvIGdldCBhIHdlYiBzb2NrZXQgZm9yIGEgdXJsLlxyXG4gICAgICovXHJcbiAgICBDb25uZWN0aW9uSGFuZGxlci5wcm90b3R5cGUuZ2V0Q29ubmVjdGlvbiA9IGZ1bmN0aW9uICh1cmwpIHtcclxuICAgICAgICBpZiAodXJsID09IG51bGwpXHJcbiAgICAgICAgICAgIHVybCA9IFwiXCI7XHJcbiAgICAgICAgdmFyIHJldCA9IHRoaXMuY29ubmVjdGlvbnNbdXJsXTtcclxuICAgICAgICBpZiAocmV0ICE9IG51bGwpIHtcclxuICAgICAgICAgICAgcmV0dXJuIHJldDtcclxuICAgICAgICB9XHJcbiAgICAgICAgZWxzZSB7XHJcbiAgICAgICAgICAgIHJldHVybiB0aGlzLmFkZENvbm5lY3Rpb24odXJsKTtcclxuICAgICAgICB9XHJcbiAgICB9O1xyXG4gICAgO1xyXG4gICAgLyoqXHJcbiAgICAgKiAgQWRkIGEgbmV3IHNlcnZlciBjb25uZWN0aW9uLlxyXG4gICAgICogIEBwYXJhbSB1cmwgVGhlIHVybC5cclxuICAgICAqL1xyXG4gICAgQ29ubmVjdGlvbkhhbmRsZXIucHJvdG90eXBlLmFkZENvbm5lY3Rpb24gPSBmdW5jdGlvbiAodXJsKSB7XHJcbiAgICAgICAgdmFyIF90aGlzID0gdGhpcztcclxuICAgICAgICB0aGlzLmNvbm5lY3Rpb25zW3VybF0gPSBuZXcgUHJvbWlzZShmdW5jdGlvbiAocmVzb2x2ZSwgcmVqZWN0KSB7XHJcbiAgICAgICAgICAgIHRyeSB7XHJcbiAgICAgICAgICAgICAgICB2YXIgd3NfMSA9IG5ldyBXZWJTb2NrZXQodXJsKTtcclxuICAgICAgICAgICAgICAgIHdzXzEub25vcGVuID0gZnVuY3Rpb24gKCkge1xyXG4gICAgICAgICAgICAgICAgICAgIHJlc29sdmUod3NfMSk7XHJcbiAgICAgICAgICAgICAgICB9O1xyXG4gICAgICAgICAgICAgICAgd3NfMS5vbm1lc3NhZ2UgPSBmdW5jdGlvbiAobWVzc2FnZSkge1xyXG4gICAgICAgICAgICAgICAgICAgIF90aGlzLm9uTWVzc2FnZShtZXNzYWdlLCB1cmwpO1xyXG4gICAgICAgICAgICAgICAgfTtcclxuICAgICAgICAgICAgfVxyXG4gICAgICAgICAgICBjYXRjaCAoZSkge1xyXG4gICAgICAgICAgICAgICAgcmVqZWN0KGUpO1xyXG4gICAgICAgICAgICB9XHJcbiAgICAgICAgfSk7XHJcbiAgICAgICAgcmV0dXJuIHRoaXMuY29ubmVjdGlvbnNbdXJsXTtcclxuICAgIH07XHJcbiAgICA7XHJcbiAgICAvKipcclxuICAgICAqICBTZW5kIGEgbWVzc2FnZSB0byB0aGUgc2VydmVyIGFuZCBjcmVhdGUgYSBjYWxsaWQgZm9yIHRoZSBhbnN3ZXIgbWVzc2FnZS5cclxuICAgICAqL1xyXG4gICAgQ29ubmVjdGlvbkhhbmRsZXIucHJvdG90eXBlLnNlbmREYXRhID0gZnVuY3Rpb24gKHVybCwgZGF0YSkge1xyXG4gICAgICAgIHRoaXMuZ2V0Q29ubmVjdGlvbih1cmwpLnRoZW4oZnVuY3Rpb24gKHdzKSB7XHJcbiAgICAgICAgICAgIHdzLnNlbmQoZGF0YSk7XHJcbiAgICAgICAgfSk7XHJcbiAgICB9O1xyXG4gICAgLyoqXHJcbiAgICAgKiAgU2VuZCBhIG1lc3NhZ2UgdG8gdGhlIHNlcnZlciBpbiBhbiBvbmdvaW5nIGNvbnZlcnNhdGlvbi5cclxuICAgICAqL1xyXG4gICAgQ29ubmVjdGlvbkhhbmRsZXIucHJvdG90eXBlLnNlbmRDb252ZXJzYXRpb25NZXNzYWdlID0gZnVuY3Rpb24gKHVybCwgY21kKSB7XHJcbiAgICAgICAgdGhpcy5nZXRDb25uZWN0aW9uKHVybCkudGhlbihmdW5jdGlvbiAod3MpIHtcclxuICAgICAgICAgICAgd3Muc2VuZChKU09OLnN0cmluZ2lmeShjbWQpKTtcclxuICAgICAgICB9KTtcclxuICAgIH07XHJcbiAgICA7XHJcbiAgICByZXR1cm4gQ29ubmVjdGlvbkhhbmRsZXI7XHJcbn0oKSk7XHJcbmV4cG9ydHMuQ29ubmVjdGlvbkhhbmRsZXIgPSBDb25uZWN0aW9uSGFuZGxlcjtcclxuLy8jIHNvdXJjZU1hcHBpbmdVUkw9Q29ubmVjdGlvbkhhbmRsZXIuanMubWFwIiwiXCJ1c2Ugc3RyaWN0XCI7XHJcbnZhciBfX2V4dGVuZHMgPSAodGhpcyAmJiB0aGlzLl9fZXh0ZW5kcykgfHwgZnVuY3Rpb24gKGQsIGIpIHtcclxuICAgIGZvciAodmFyIHAgaW4gYikgaWYgKGIuaGFzT3duUHJvcGVydHkocCkpIGRbcF0gPSBiW3BdO1xyXG4gICAgZnVuY3Rpb24gX18oKSB7IHRoaXMuY29uc3RydWN0b3IgPSBkOyB9XHJcbiAgICBkLnByb3RvdHlwZSA9IGIgPT09IG51bGwgPyBPYmplY3QuY3JlYXRlKGIpIDogKF9fLnByb3RvdHlwZSA9IGIucHJvdG90eXBlLCBuZXcgX18oKSk7XHJcbn07XHJcbnZhciBDb25uZWN0aW9uSGFuZGxlcl8xID0gcmVxdWlyZShcIi4vQ29ubmVjdGlvbkhhbmRsZXJcIik7XHJcbnZhciBKc29uUGFyc2VyXzEgPSByZXF1aXJlKFwiLi4vSnNvblBhcnNlclwiKTtcclxudmFyIFNVdGlsXzEgPSByZXF1aXJlKFwiLi4vU1V0aWxcIik7XHJcbnZhciBXZWJzb2NrZXRDYWxsXzEgPSByZXF1aXJlKFwiLi9XZWJzb2NrZXRDYWxsXCIpO1xyXG52YXIgUGFydGlhbE1lc3NhZ2VfMSA9IHJlcXVpcmUoXCIuLi9tZXNzYWdlcy9QYXJ0aWFsTWVzc2FnZVwiKTtcclxuLyoqXHJcbiAqXHJcbiAqL1xyXG52YXIgSmFkZXhDb25uZWN0aW9uSGFuZGxlciA9IChmdW5jdGlvbiAoX3N1cGVyKSB7XHJcbiAgICBfX2V4dGVuZHMoSmFkZXhDb25uZWN0aW9uSGFuZGxlciwgX3N1cGVyKTtcclxuICAgIGZ1bmN0aW9uIEphZGV4Q29ubmVjdGlvbkhhbmRsZXIoKSB7XHJcbiAgICAgICAgX3N1cGVyLmFwcGx5KHRoaXMsIGFyZ3VtZW50cyk7XHJcbiAgICAgICAgLyoqIFRoZSBtYXAgb2Ygb3BlbiBvdXRjYWxscy4gKi9cclxuICAgICAgICB0aGlzLm91dGNhbGxzID0gW107XHJcbiAgICAgICAgLy9cdC8qKiBUaGUgbWFwIG9mIG9wZW4gaW5jb21pbmcgY2FsbHMuICovXHJcbiAgICAgICAgLy9cdHZhciBpbmNhbGxzID0gW107XHJcbiAgICAgICAgLyoqIFRoZSBtYXAgb2YgcHJvdmlkZWQgc2VydmljZXMgKHNpZCAtPiBzZXJ2aWNlIGludm9jYXRpb24gZnVuY3Rpb24pLiAqL1xyXG4gICAgICAgIHRoaXMucHJvdmlkZWRTZXJ2aWNlcyA9IFtdO1xyXG4gICAgfVxyXG4gICAgLyoqXHJcbiAgICAgKiAgR2V0IHRoZSBpbnN0YW5jZS5cclxuICAgICAqL1xyXG4gICAgSmFkZXhDb25uZWN0aW9uSGFuZGxlci5nZXRJbnN0YW5jZSA9IGZ1bmN0aW9uICgpIHtcclxuICAgICAgICByZXR1cm4gSmFkZXhDb25uZWN0aW9uSGFuZGxlci5JTlNUQU5DRTtcclxuICAgIH07XHJcbiAgICAvKipcclxuICAgICAqICBTZW5kIGEgbWVzc2FnZSB0byB0aGUgc2VydmVyIGFuZCBjcmVhdGUgYSBjYWxsaWQgZm9yIHRoZSBhbnN3ZXIgbWVzc2FnZS5cclxuICAgICAqL1xyXG4gICAgSmFkZXhDb25uZWN0aW9uSGFuZGxlci5wcm90b3R5cGUuc2VuZE1lc3NhZ2UgPSBmdW5jdGlvbiAodXJsLCBjbWQsIHR5cGUsIHJlc29sdmUsIHJlamVjdCwgY2FsbGJhY2spIHtcclxuICAgICAgICAvLyB0b2RvOiB1c2UgSmFkZXggYmluYXJ5IHRvIHNlcmlhbGl6ZSBtZXNzYWdlIGFuZCBzZW5kXHJcbiAgICAgICAgdmFyIGNhbGxpZCA9IHRoaXMucmFuZG9tU3RyaW5nKC0xKTtcclxuICAgICAgICB0aGlzLm91dGNhbGxzW2NhbGxpZF0gPSBuZXcgV2Vic29ja2V0Q2FsbF8xLldlYnNvY2tldENhbGwodHlwZSwgcmVzb2x2ZSwgcmVqZWN0LCBjYWxsYmFjayk7XHJcbiAgICAgICAgY21kLmNhbGxpZCA9IGNhbGxpZDtcclxuICAgICAgICB0aGlzLnNlbmRSYXdNZXNzYWdlKHVybCwgY21kKTtcclxuICAgICAgICByZXR1cm4gY2FsbGlkO1xyXG4gICAgfTtcclxuICAgIDtcclxuICAgIC8qKlxyXG4gICAgICogIFNlbmQgYSByYXcgbWVzc2FnZSB3aXRob3V0IGNhbGxpZCBtYW5hZ2VtZW50LlxyXG4gICAgICovXHJcbiAgICBKYWRleENvbm5lY3Rpb25IYW5kbGVyLnByb3RvdHlwZS5zZW5kUmF3TWVzc2FnZSA9IGZ1bmN0aW9uICh1cmwsIGNtZCkge1xyXG4gICAgICAgIGlmICghY21kLmNhbGxpZClcclxuICAgICAgICAgICAgY29uc29sZS5sb2coXCJTZW5kaW5nIG1lc3NhZ2Ugd2l0aG91dCBjYWxsaWQ6IFwiICsgY21kKTtcclxuICAgICAgICB2YXIgZGF0YSA9IHRoaXMub2JqZWN0VG9Kc29uKGNtZCk7XHJcbiAgICAgICAgLy9jb25zb2xlLmxvZyhkYXRhKTtcclxuICAgICAgICAvL2xldCBzaXplID0gc2l6ZU9mKGNtZCk7XHJcbiAgICAgICAgdmFyIHNpemUgPSBkYXRhLmxlbmd0aDtcclxuICAgICAgICB2YXIgbGltaXQgPSA3MDAwOyAvLyA4MTkyXHJcbiAgICAgICAgLy8gSWYgbWVzc2FnZSBpcyBsYXJnZXIgdGhhbiBsaW1pdCBzbGljZSB0aGUgbWVzc2FnZSB2aWEgcGFydGlhbCBtZXNzYWdlc1xyXG4gICAgICAgIGlmIChzaXplID4gbGltaXQpIHtcclxuICAgICAgICAgICAgdmFyIGNudCA9IE1hdGguY2VpbChzaXplIC8gbGltaXQpO1xyXG4gICAgICAgICAgICBmb3IgKHZhciBpID0gMDsgaSA8IGNudDsgaSsrKSB7XHJcbiAgICAgICAgICAgICAgICB2YXIgcGFydCA9IGRhdGEuc3Vic3RyaW5nKGkgKiBsaW1pdCwgKGkgKyAxKSAqIGxpbWl0KTtcclxuICAgICAgICAgICAgICAgIHZhciBwY21kID0gbmV3IFBhcnRpYWxNZXNzYWdlXzEuUGFydGlhbE1lc3NhZ2UoY21kLmNhbGxpZCwgcGFydCwgaSwgY250KTtcclxuICAgICAgICAgICAgICAgIHZhciBwZGF0YSA9IEpTT04uc3RyaW5naWZ5KHBjbWQpO1xyXG4gICAgICAgICAgICAgICAgLy9jb25zb2xlLmxvZyhcInNlbmRpbmcgcGFydCwgc2l6ZTogXCIrcGRhdGEubGVuZ3RoKTtcclxuICAgICAgICAgICAgICAgIHRoaXMuc2VuZERhdGEodXJsLCBwZGF0YSk7XHJcbiAgICAgICAgICAgIH1cclxuICAgICAgICB9XHJcbiAgICAgICAgZWxzZSB7XHJcbiAgICAgICAgICAgIHRoaXMuc2VuZERhdGEodXJsLCBkYXRhKTtcclxuICAgICAgICB9XHJcbiAgICB9O1xyXG4gICAgLyoqXHJcbiAgICAgKiAgQ29udmVydCBhbiBvYmplY3QgdG8ganNvbi5cclxuICAgICAqICBTaW1pbGFyIHRvIEpTT04uc3RyaW5naWZ5IGJ1dCBjYW4gaGFuZGxlXHJcbiAgICAgKiAgYmluYXJ5IG9iamVjdHMgYXMgYmFzZSA2NCBzdHJpbmdzLlxyXG4gICAgICogIEBwYXJhbSBvYmplY3QgVGhlIG9iamVjdC5cclxuICAgICAqICBAcmV0dXJuIFRoZSBqc29uIHN0cmluZy5cclxuICAgICAqL1xyXG4gICAgSmFkZXhDb25uZWN0aW9uSGFuZGxlci5wcm90b3R5cGUub2JqZWN0VG9Kc29uID0gZnVuY3Rpb24gKG9iamVjdCkge1xyXG4gICAgICAgIHZhciByZXBsYWNlciA9IGZ1bmN0aW9uIChrZXksIHZhbHVlKSB7XHJcbiAgICAgICAgICAgIGlmICh2YWx1ZSBpbnN0YW5jZW9mIEFycmF5QnVmZmVyKSB7XHJcbiAgICAgICAgICAgICAgICAvL2xldCByZXQgPSB3aW5kb3cuYnRvYSh2YWx1ZSk7XHJcbiAgICAgICAgICAgICAgICB2YXIgcmV0ID0gYnRvYShTdHJpbmcuZnJvbUNoYXJDb2RlLmFwcGx5KG51bGwsIG5ldyBVaW50OEFycmF5KHZhbHVlKSkpO1xyXG4gICAgICAgICAgICAgICAgcmV0dXJuIHJldDtcclxuICAgICAgICAgICAgfVxyXG4gICAgICAgICAgICBlbHNlIHtcclxuICAgICAgICAgICAgICAgIHJldHVybiB2YWx1ZTtcclxuICAgICAgICAgICAgfVxyXG4gICAgICAgICAgICAvL3JldHVybiB2YWx1ZSBpbnN0YW5jZW9mIEFycmF5QnVmZmVyPyB3aW5kb3cuYnRvYSh2YWx1ZSk6IHZhbHVlO1xyXG4gICAgICAgIH07XHJcbiAgICAgICAgcmV0dXJuIEpTT04uc3RyaW5naWZ5KG9iamVjdCwgcmVwbGFjZXIpO1xyXG4gICAgfTtcclxuICAgIC8qKlxyXG4gICAgICogIFNlbmQgYSByZXN1bHQuXHJcbiAgICAgKi9cclxuICAgIEphZGV4Q29ubmVjdGlvbkhhbmRsZXIucHJvdG90eXBlLnNlbmRSZXN1bHQgPSBmdW5jdGlvbiAodXJsLCByZXN1bHQsIGZpbmlzaGVkLCBjYWxsaWQpIHtcclxuICAgICAgICB2YXIgY21kID0ge1xyXG4gICAgICAgICAgICBfX2NsYXNzbmFtZTogXCJjb20uYWN0b3Jvbi53ZWJzZXJ2aWNlLm1lc3NhZ2VzLlJlc3VsdE1lc3NhZ2VcIixcclxuICAgICAgICAgICAgY2FsbGlkOiBjYWxsaWQsXHJcbiAgICAgICAgICAgIHJlc3VsdDogcmVzdWx0LFxyXG4gICAgICAgICAgICBmaW5pc2hlZDogZmluaXNoZWRcclxuICAgICAgICB9O1xyXG4gICAgICAgIHRoaXMuc2VuZFJhd01lc3NhZ2UodXJsLCBjbWQpO1xyXG4gICAgfTtcclxuICAgIC8qKlxyXG4gICAgICogIFNlbmQgYW4gZXhjZXB0aW9uLlxyXG4gICAgICovXHJcbiAgICBKYWRleENvbm5lY3Rpb25IYW5kbGVyLnByb3RvdHlwZS5zZW5kRXhjZXB0aW9uID0gZnVuY3Rpb24gKHVybCwgZXJyLCBmaW5pc2hlZCwgY2FsbGlkKSB7XHJcbiAgICAgICAgdmFyIGV4Y2VwdGlvbiA9IHtcclxuICAgICAgICAgICAgX19jbGFzc25hbWU6IFwiamF2YS5sYW5nLlJ1bnRpbWVFeGNlcHRpb25cIixcclxuICAgICAgICAgICAgbWVzc2FnZTogXCJcIiArIGVyclxyXG4gICAgICAgIH07XHJcbiAgICAgICAgdmFyIGNtZCA9IHtcclxuICAgICAgICAgICAgX19jbGFzc25hbWU6IFwiY29tLmFjdG9yb24ud2Vic2VydmljZS5tZXNzYWdlcy5SZXN1bHRNZXNzYWdlXCIsXHJcbiAgICAgICAgICAgIGNhbGxpZDogY2FsbGlkLFxyXG4gICAgICAgICAgICBleGNlcHRpb246IGV4Y2VwdGlvbixcclxuICAgICAgICAgICAgZmluaXNoZWQ6IGZpbmlzaGVkXHJcbiAgICAgICAgfTtcclxuICAgICAgICB0aGlzLnNlbmRSYXdNZXNzYWdlKHVybCwgY21kKTtcclxuICAgIH07XHJcbiAgICAvKipcclxuICAgICAqICBDYWxsZWQgd2hlbiBhIG1lc3NhZ2UgYXJyaXZlcy5cclxuICAgICAqL1xyXG4gICAgSmFkZXhDb25uZWN0aW9uSGFuZGxlci5wcm90b3R5cGUub25NZXNzYWdlID0gZnVuY3Rpb24gKG1lc3NhZ2UsIHVybCkge1xyXG4gICAgICAgIHZhciBfdGhpcyA9IHRoaXM7XHJcbiAgICAgICAgaWYgKG1lc3NhZ2UudHlwZSA9PSBcIm1lc3NhZ2VcIikge1xyXG4gICAgICAgICAgICB2YXIgbXNnXzEgPSBKc29uUGFyc2VyXzEuSnNvblBhcnNlci5wYXJzZShtZXNzYWdlLmRhdGEsIHVybCk7XHJcbiAgICAgICAgICAgIHZhciBvdXRDYWxsID0gdGhpcy5vdXRjYWxsc1ttc2dfMS5jYWxsaWRdO1xyXG4gICAgICAgICAgICAvL1x0XHQgICAgY29uc29sZS5sb2coXCJvdXRjYWxsczogXCIrb3V0Y2FsbHMpO1xyXG4gICAgICAgICAgICBpZiAob3V0Q2FsbCAhPSBudWxsKSB7XHJcbiAgICAgICAgICAgICAgICBpZiAoU1V0aWxfMS5TVXRpbC5pc1RydWUobXNnXzEuZmluaXNoZWQpKSB7XHJcbiAgICAgICAgICAgICAgICAgICAgZGVsZXRlIHRoaXMub3V0Y2FsbHNbbXNnXzEuY2FsbGlkXTtcclxuICAgICAgICAgICAgICAgICAgICAvL1x0XHRcdFx0XHRjb25zb2xlLmxvZyhcIm91dENhbGwgZGVsZXRlZDogXCIrbXNnLmNhbGxpZCk7XHJcbiAgICAgICAgICAgICAgICAgICAgb3V0Q2FsbC5maW5pc2hlZCA9IHRydWU7XHJcbiAgICAgICAgICAgICAgICB9XHJcbiAgICAgICAgICAgICAgICBpZiAob3V0Q2FsbC50eXBlID09IFwic2VhcmNoXCIpIHtcclxuICAgICAgICAgICAgICAgICAgICBpZiAobXNnXzEucmVzdWx0ICE9IG51bGwpIHtcclxuICAgICAgICAgICAgICAgICAgICAgICAgaWYgKG1zZ18xLnJlc3VsdC5oYXNPd25Qcm9wZXJ0eShcIl9fYXJyYXlcIikpXHJcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICBtc2dfMS5yZXN1bHQgPSBtc2dfMS5yZXN1bHQuX19hcnJheTtcclxuICAgICAgICAgICAgICAgICAgICAgICAgaWYgKG1zZ18xLnJlc3VsdC5oYXNPd25Qcm9wZXJ0eShcIl9fY29sbGVjdGlvblwiKSlcclxuICAgICAgICAgICAgICAgICAgICAgICAgICAgIG1zZ18xLnJlc3VsdFsxXSA9IG1zZ18xLnJlc3VsdFsxXS5fX2NvbGxlY3Rpb247XHJcbiAgICAgICAgICAgICAgICAgICAgfVxyXG4gICAgICAgICAgICAgICAgICAgIHZhciBzZXJwcm94eSA9IHZvaWQgMDtcclxuICAgICAgICAgICAgICAgICAgICBpZiAobXNnXzEuZXhjZXB0aW9uID09IG51bGwgJiYgbXNnXzEucmVzdWx0ICE9IG51bGwpIHtcclxuICAgICAgICAgICAgICAgICAgICAgICAgc2VycHJveHkgPSBtc2dfMS5yZXN1bHQ7IC8vY3JlYXRlU2VydmljZVByb3h5KG1zZy5yZXN1bHRbMF0sIG1zZy5yZXN1bHRbMV0pO1xyXG4gICAgICAgICAgICAgICAgICAgIH1cclxuICAgICAgICAgICAgICAgICAgICBvdXRDYWxsLnJlc3VtZShzZXJwcm94eSwgbXNnXzEuZXhjZXB0aW9uKTtcclxuICAgICAgICAgICAgICAgIH1cclxuICAgICAgICAgICAgICAgIGVsc2UgaWYgKG91dENhbGwudHlwZSA9PSBcImludm9rZVwiKSB7XHJcbiAgICAgICAgICAgICAgICAgICAgb3V0Q2FsbC5yZXN1bWUobXNnXzEucmVzdWx0LCBtc2dfMS5leGNlcHRpb24pO1xyXG4gICAgICAgICAgICAgICAgfVxyXG4gICAgICAgICAgICAgICAgZWxzZSBpZiAob3V0Q2FsbC50eXBlID09IFwicHJvdmlkZVwiKSB7XHJcbiAgICAgICAgICAgICAgICAgICAgaWYgKG1zZ18xLmV4Y2VwdGlvbiAhPSBudWxsKSB7XHJcbiAgICAgICAgICAgICAgICAgICAgICAgIG91dENhbGwucmVqZWN0KG1zZ18xLmV4Y2VwdGlvbik7XHJcbiAgICAgICAgICAgICAgICAgICAgfVxyXG4gICAgICAgICAgICAgICAgICAgIGVsc2Uge1xyXG4gICAgICAgICAgICAgICAgICAgICAgICAvLyBTYXZlIHRoZSBzZXJ2aWNlIGZ1bmN0aW9uYWxpdHkgaW4gdGhlIGluY2FcclxuICAgICAgICAgICAgICAgICAgICAgICAgdGhpcy5wcm92aWRlZFNlcnZpY2VzW1NVdGlsXzEuU1V0aWwuZ2V0U2VydmljZUlkQXNTdHJpbmcobXNnXzEucmVzdWx0KV0gPSBvdXRDYWxsLmNiO1xyXG4gICAgICAgICAgICAgICAgICAgICAgICBvdXRDYWxsLnJlc29sdmUobXNnXzEucmVzdWx0KTtcclxuICAgICAgICAgICAgICAgICAgICB9XHJcbiAgICAgICAgICAgICAgICB9XHJcbiAgICAgICAgICAgICAgICBlbHNlIGlmIChvdXRDYWxsLnR5cGUgPT0gXCJ1bnByb3ZpZGVcIikge1xyXG4gICAgICAgICAgICAgICAgICAgIGlmIChtc2dfMS5leGNlcHRpb24gIT0gbnVsbCkge1xyXG4gICAgICAgICAgICAgICAgICAgICAgICBvdXRDYWxsLnJlamVjdChtc2dfMS5leGNlcHRpb24pO1xyXG4gICAgICAgICAgICAgICAgICAgIH1cclxuICAgICAgICAgICAgICAgICAgICBlbHNlIHtcclxuICAgICAgICAgICAgICAgICAgICAgICAgLy8gcmVtb3ZlUHJvcGVydHk/IVxyXG4gICAgICAgICAgICAgICAgICAgICAgICB0aGlzLnByb3ZpZGVkU2VydmljZXNbU1V0aWxfMS5TVXRpbC5nZXRTZXJ2aWNlSWRBc1N0cmluZyhtc2dfMS5yZXN1bHQpXSA9IG51bGw7XHJcbiAgICAgICAgICAgICAgICAgICAgICAgIG91dENhbGwucmVzb2x2ZShtc2dfMS5yZXN1bHQpO1xyXG4gICAgICAgICAgICAgICAgICAgIH1cclxuICAgICAgICAgICAgICAgIH1cclxuICAgICAgICAgICAgfVxyXG4gICAgICAgICAgICBlbHNlIHtcclxuICAgICAgICAgICAgICAgIGlmIChtc2dfMS5fX2NsYXNzbmFtZSA9PT0gXCJjb20uYWN0b3Jvbi53ZWJzZXJ2aWNlLm1lc3NhZ2VzLlNlcnZpY2VJbnZvY2F0aW9uTWVzc2FnZVwiKSB7XHJcbiAgICAgICAgICAgICAgICAgICAgdmFyIHNlcnZpY2UgPSB0aGlzLnByb3ZpZGVkU2VydmljZXNbU1V0aWxfMS5TVXRpbC5nZXRTZXJ2aWNlSWRBc1N0cmluZyhtc2dfMS5zZXJ2aWNlSWQpXTtcclxuICAgICAgICAgICAgICAgICAgICBpZiAoc2VydmljZSkge1xyXG4gICAgICAgICAgICAgICAgICAgICAgICB2YXIgcmVzO1xyXG4gICAgICAgICAgICAgICAgICAgICAgICAvLyBJZiBpdCBhIHNlcnZpY2Ugb2JqZWN0IHdpdGggZnVuY3Rpb25zIG9yIGp1c3QgYSBmdW5jdGlvblxyXG4gICAgICAgICAgICAgICAgICAgICAgICBpZiAoc2VydmljZVttc2dfMS5tZXRob2ROYW1lXSkge1xyXG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgLy9yZXMgPSBzZXJ2aWNlW21zZy5tZXRob2ROYW1lXShtc2cucGFyYW1ldGVyVmFsdWVzKTtcclxuICAgICAgICAgICAgICAgICAgICAgICAgICAgIHJlcyA9IHNlcnZpY2VbbXNnXzEubWV0aG9kTmFtZV0uYXBwbHkodW5kZWZpbmVkLCBtc2dfMS5wYXJhbWV0ZXJWYWx1ZXMpO1xyXG4gICAgICAgICAgICAgICAgICAgICAgICB9XHJcbiAgICAgICAgICAgICAgICAgICAgICAgIGVsc2UgaWYgKHR5cGVvZiByZXMgPT09IFwiZnVuY3Rpb25cIikge1xyXG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgLy9yZXMgPSBzZXJ2aWNlKG1zZy5wYXJhbWV0ZXJWYWx1ZXMpO1xyXG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgcmVzID0gc2VydmljZS5hcHBseSh1bmRlZmluZWQsIG1zZ18xLnBhcmFtZXRlclZhbHVlcyk7XHJcbiAgICAgICAgICAgICAgICAgICAgICAgIH1cclxuICAgICAgICAgICAgICAgICAgICAgICAgZWxzZSBpZiAoc2VydmljZS5pbnZva2UpIHtcclxuICAgICAgICAgICAgICAgICAgICAgICAgICAgIHJlcyA9IHNlcnZpY2UuaW52b2tlKG1zZ18xLm1ldGhvZE5hbWUsIG1zZ18xLnBhcmFtZXRlclZhbHVlcyk7XHJcbiAgICAgICAgICAgICAgICAgICAgICAgIH1cclxuICAgICAgICAgICAgICAgICAgICAgICAgZWxzZSB7XHJcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICBjb25zb2xlLmxvZyhcIkNhbm5vdCBpbnZva2Ugc2VydmljZSBtZXRob2QgKG5vdCBmb3VuZCk6IFwiICsgbXNnXzEubWV0aG9kTmFtZSk7XHJcbiAgICAgICAgICAgICAgICAgICAgICAgIH1cclxuICAgICAgICAgICAgICAgICAgICAgICAgLy8gSGFjaywgc2VlbXMgdG8gbG9vc2UgdGhpcyBpbiBjYWxsYmFjayA6LSggXHJcbiAgICAgICAgICAgICAgICAgICAgICAgIC8vXHRcdFx0XHRcdFx0dmFyIGZ0aGlzID0gdGhpcztcclxuICAgICAgICAgICAgICAgICAgICAgICAgLy8gTWFrZSBhbnl0aGluZyB0aGF0IGNvbWVzIGJhY2sgdG8gYSBwcm9taXNlXHJcbiAgICAgICAgICAgICAgICAgICAgICAgIC8vICAgICAgICAgICAgICAgICAgICAgICAgUHJvbWlzZS5yZXNvbHZlKHJlcykudGhlbihmdW5jdGlvbihyZXMpXHJcbiAgICAgICAgICAgICAgICAgICAgICAgIC8vICAgICAgICAgICAgICAgICAgICAgICAge1xyXG4gICAgICAgICAgICAgICAgICAgICAgICAvLyAgICAgICAgICAgICAgICAgICAgICAgICAgICBmdGhpcy5zZW5kUmVzdWx0KHVybCwgcmVzLCB0cnVlLCBtc2cuY2FsbGlkKTtcclxuICAgICAgICAgICAgICAgICAgICAgICAgLy8gICAgICAgICAgICAgICAgICAgICAgICB9KVxyXG4gICAgICAgICAgICAgICAgICAgICAgICAvLyAgICAgICAgICAgICAgICAgICAgICAgIC5jYXRjaChmdW5jdGlvbihlKVxyXG4gICAgICAgICAgICAgICAgICAgICAgICAvLyAgICAgICAgICAgICAgICAgICAgICAgIHtcclxuICAgICAgICAgICAgICAgICAgICAgICAgLy8gICAgICAgICAgICAgICAgICAgICAgICAgICAgZnRoaXMuc2VuZEV4Y2VwdGlvbih1cmwsIGUsIHRydWUsIG1zZy5jYWxsaWQpO1xyXG4gICAgICAgICAgICAgICAgICAgICAgICAvLyAgICAgICAgICAgICAgICAgICAgICAgIH0pO1xyXG4gICAgICAgICAgICAgICAgICAgICAgICBQcm9taXNlLnJlc29sdmUocmVzKS50aGVuKGZ1bmN0aW9uIChyZXMpIHtcclxuICAgICAgICAgICAgICAgICAgICAgICAgICAgIF90aGlzLnNlbmRSZXN1bHQodXJsLCByZXMsIHRydWUsIG1zZ18xLmNhbGxpZCk7XHJcbiAgICAgICAgICAgICAgICAgICAgICAgIH0pXHJcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICAuY2F0Y2goZnVuY3Rpb24gKGUpIHtcclxuICAgICAgICAgICAgICAgICAgICAgICAgICAgIF90aGlzLnNlbmRFeGNlcHRpb24odXJsLCBlLCB0cnVlLCBtc2dfMS5jYWxsaWQpO1xyXG4gICAgICAgICAgICAgICAgICAgICAgICB9KTtcclxuICAgICAgICAgICAgICAgICAgICB9XHJcbiAgICAgICAgICAgICAgICAgICAgZWxzZSB7XHJcbiAgICAgICAgICAgICAgICAgICAgICAgIGNvbnNvbGUubG9nKFwiUHJvdmlkZWQgc2VydmljZSBub3QgZm91bmQ6IFwiICsgW21zZ18xLnNlcnZpY2VJZF0pO1xyXG4gICAgICAgICAgICAgICAgICAgICAgICB0aGlzLnNlbmRFeGNlcHRpb24odXJsLCBcIlByb3ZpZGVkIHNlcnZpY2Ugbm90IGZvdW5kOiBcIiArIFttc2dfMS5zZXJ2aWNlSWRdLCB0cnVlLCBtc2dfMS5jYWxsaWQpO1xyXG4gICAgICAgICAgICAgICAgICAgIH1cclxuICAgICAgICAgICAgICAgIH1cclxuICAgICAgICAgICAgICAgIGVsc2Uge1xyXG4gICAgICAgICAgICAgICAgICAgIGNvbnNvbGUubG9nKFwiUmVjZWl2ZWQgbWVzc2FnZSB3aXRob3V0IHJlcXVlc3Q6IFwiICsgbXNnXzEpO1xyXG4gICAgICAgICAgICAgICAgfVxyXG4gICAgICAgICAgICB9XHJcbiAgICAgICAgfVxyXG4gICAgICAgIGVsc2UgaWYgKG1lc3NhZ2UudHlwZSA9PSBcImJpbmFyeVwiKSB7XHJcbiAgICAgICAgICAgIGNvbnNvbGUubG9nKFwiQmluYXJ5IG1lc3NhZ2VzIGN1cnJlbnRseSBub3Qgc3VwcG9ydGVkXCIpO1xyXG4gICAgICAgIH1cclxuICAgICAgICAvLyBlbHNlOiBkbyBub3QgaGFuZGxlIHBvbmcgbWVzc2FnZXNcclxuICAgIH07XHJcbiAgICAvKipcclxuICAgICAqICBDcmVhdGUgYSByYW5kb20gc3RyaW5nLlxyXG4gICAgICogIEBwYXJhbSBsZW5ndGggVGhlIGxlbmd0aCBvZiB0aGUgc3RyaW5nLlxyXG4gICAgICogIEByZXR1cm5zIFRoZSByYW5kb20gc3RyaW5nLlxyXG4gICAgICovXHJcbiAgICBKYWRleENvbm5lY3Rpb25IYW5kbGVyLnByb3RvdHlwZS5yYW5kb21TdHJpbmcgPSBmdW5jdGlvbiAobGVuZ3RoKSB7XHJcbiAgICAgICAgaWYgKGxlbmd0aCA8IDEpXHJcbiAgICAgICAgICAgIGxlbmd0aCA9IDEwO1xyXG4gICAgICAgIHJldHVybiBNYXRoLnJvdW5kKChNYXRoLnBvdygzNiwgbGVuZ3RoICsgMSkgLSBNYXRoLnJhbmRvbSgpICogTWF0aC5wb3coMzYsIGxlbmd0aCkpKS50b1N0cmluZygzNikuc2xpY2UoMSk7XHJcbiAgICB9O1xyXG4gICAgO1xyXG4gICAgSmFkZXhDb25uZWN0aW9uSGFuZGxlci5JTlNUQU5DRSA9IG5ldyBKYWRleENvbm5lY3Rpb25IYW5kbGVyKCk7XHJcbiAgICByZXR1cm4gSmFkZXhDb25uZWN0aW9uSGFuZGxlcjtcclxufShDb25uZWN0aW9uSGFuZGxlcl8xLkNvbm5lY3Rpb25IYW5kbGVyKSk7XHJcbmV4cG9ydHMuSmFkZXhDb25uZWN0aW9uSGFuZGxlciA9IEphZGV4Q29ubmVjdGlvbkhhbmRsZXI7XHJcbi8vIyBzb3VyY2VNYXBwaW5nVVJMPUphZGV4Q29ubmVjdGlvbkhhbmRsZXIuanMubWFwIiwiXCJ1c2Ugc3RyaWN0XCI7XHJcbnZhciBTVXRpbF8xID0gcmVxdWlyZShcIi4uL1NVdGlsXCIpO1xyXG52YXIgV2Vic29ja2V0Q2FsbCA9IChmdW5jdGlvbiAoKSB7XHJcbiAgICBmdW5jdGlvbiBXZWJzb2NrZXRDYWxsKHR5cGUsIHJlc29sdmUsIHJlamVjdCwgY2IpIHtcclxuICAgICAgICB0aGlzLnR5cGUgPSB0eXBlO1xyXG4gICAgICAgIHRoaXMucmVzb2x2ZSA9IHJlc29sdmU7XHJcbiAgICAgICAgdGhpcy5yZWplY3QgPSByZWplY3Q7XHJcbiAgICAgICAgdGhpcy5jYiA9IGNiO1xyXG4gICAgICAgIHRoaXMuZmluaXNoZWQgPSBmYWxzZTtcclxuICAgIH1cclxuICAgIC8qKlxyXG4gICAgICogIFJlc3VtZSB0aGUgbGlzdGVuZXJzIG9mIHByb21pc2UuXHJcbiAgICAgKi9cclxuICAgIFdlYnNvY2tldENhbGwucHJvdG90eXBlLnJlc3VtZSA9IGZ1bmN0aW9uIChyZXN1bHQsIGV4Y2VwdGlvbikge1xyXG4gICAgICAgIGlmICh0aGlzLmNiICE9IG51bGwgJiYgKGV4Y2VwdGlvbiA9PT0gbnVsbCB8fCBleGNlcHRpb24gPT09IHVuZGVmaW5lZCkgJiYgIVNVdGlsXzEuU1V0aWwuaXNUcnVlKHRoaXMuZmluaXNoZWQpKSB7XHJcbiAgICAgICAgICAgIHRoaXMuY2IocmVzdWx0KTtcclxuICAgICAgICB9XHJcbiAgICAgICAgZWxzZSBpZiAoU1V0aWxfMS5TVXRpbC5pc1RydWUodGhpcy5maW5pc2hlZCkpIHtcclxuICAgICAgICAgICAgZXhjZXB0aW9uID09IG51bGwgPyB0aGlzLnJlc29sdmUocmVzdWx0KSA6IHRoaXMucmVqZWN0KGV4Y2VwdGlvbik7XHJcbiAgICAgICAgfVxyXG4gICAgfTtcclxuICAgIHJldHVybiBXZWJzb2NrZXRDYWxsO1xyXG59KCkpO1xyXG5leHBvcnRzLldlYnNvY2tldENhbGwgPSBXZWJzb2NrZXRDYWxsO1xyXG4vLyMgc291cmNlTWFwcGluZ1VSTD1XZWJzb2NrZXRDYWxsLmpzLm1hcCJdfQ==
