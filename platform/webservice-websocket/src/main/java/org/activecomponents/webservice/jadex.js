var Scopes =
{
		/** None component scope (nothing will be searched). */
//		const SCOPE_NONE = "none";
	//
//		/** Local component scope. */
//		const SCOPE_LOCAL = "local";
	//
//		/** Component scope. */
//		const SCOPE_COMPONENT = "component";
	//
//		/** Application scope. */
//		const SCOPE_APPLICATION = "application";

	    /** Platform scope. */
	    SCOPE_PLATFORM: "platform",

	    /** Global scope. */
	    SCOPE_GLOBAL: "global",

//		/** Parent scope. */
//		SCOPE_PARENT:string = "parent";

	    /** Session scope. */
	    SCOPE_SESSION: "session"
};

var SUtil =
{
	wrappedConversionTypes:
	{
        java_lang_Integer: "number",
        java_lang_Byte: "number",
        java_lang_Short: "number",
        java_lang_Long: "number",
        java_lang_Float: "number",
        java_lang_Double: "number",
        java_lang_Character: "string",
        java_lang_Boolean: "boolean"
    },

    /**
     *  Test if an object is a basic type.
     *  @param obj The object.
     *  @return True, if is a basic type.
     */
    isBasicType: function(obj)
    {
        return typeof obj !== 'object' || !obj;
    },

    /**
     *  Test if an object is a java wrapped type.
     *  @param obj The object.
     *  @return False, if is not a wrapped primitive type, else returns the corresponding JS type.
     */
    isWrappedType: function(obj)
    {
        if("__classname" in obj) 
        {
            var searching = obj.__classname.replace(/\./g, '_')
            return this.wrappedConversionTypes[searching];
        } 
        else 
        {
            return false;
        }
    },

    /**
     *  Check of an obj is an enum.
     */
    isEnum: function(obj)
    {
        return ("enum" in obj)
    },

    /**
     *  Test if an object is an array.
     *  @param obj The object.
     *  @return True, if is an array.
     */
    isArray: function(obj)
    {
        return Object.prototype.toString.call(obj) == '[object Array]';
    },

    /**
     *  Compute the approx. size of an object.
     *  @param obj The object.
     */
    sizeOf: function(object) 
    {
        var objects = [object];
        var size = 0;

        for(var i = 0; i < objects.length; i++) 
        {
            switch(typeof objects[i]) 
            {
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

                    if(Object.prototype.toString.call(objects[i]) != '[object Array]') 
                    {
                        for(var key in objects[i])
                            size += 2 * key.length;
                    }

                    var processed = false;
                    var key;
                    for(key in objects[i]) 
                    {
                        for(var search = 0; search < objects.length; search++) 
                        {
                            if(objects[search] === objects[i][key]) 
                            {
                                processed = true;
                                break;
                            }
                        }
                    }

                    if(!processed)
                        objects.push(objects[i][key]);
            }
        }
        return size;
    },

    /**
     *  Check if object is true by inspecting if it contains a true property.
     */
    isTrue: function(obj) 
    {
        return obj == true || (obj != null && obj.hasOwnProperty("value") && obj.value == true);
    },


    /**
     *  Assert that throws an error if not holds.
     */
    assert: function(condition, message) 
    {
        if(!condition) 
        {
            message = message || "Assertion failed";
            if(typeof Error !== "undefined") 
            {
                throw new Error(message);
            }
            throw message; // Fallback
        }
    },

    /**
     *  Get the service id as string.
     *  (otherwise it cannot be used as key in a map because
     *  no equals exists).
     */
    getServiceIdAsString: function(sid)
    {
        return sid.serviceName+"@"+sid.providerId;
    },

    /**
     *  Add a console out error handler to the promise.
     */
    addErrHandler: function(p)
    {
        p.oldcatch = p.catch;
        p.hasErrorhandler = false;
        p.catch = function(eh)
        {
            p.hasErrorHandler = true;
            return p.oldcatch(eh);
        };
        p.oldcatch(function(err)
        {
            if(!p.hasErrorHandler)
                console.log("Error occurred: "+err);
        });
        p.oldthen = p.then;
        p.then = function(t, e)
        {
            if(e)
                p.hasErrorHandler = true;
            return p.oldthen(t, e);
        };
        return p;
    },

    /**
     *  Test if a number is a float.
     *  @param n The number to test.
     *  @return True, if is float.
     */
    isFloat: function(n)
    {
        return n === +n && n !== (n | 0);
    },

    /**
     *  Test if a number is an integer.
     *  @param n The number to test.
     *  @return True, if is integer.
     */
    isInteger: function(n)
    {
        return n === +n && n === (n | 0);
    },

    /**
     *  Check if an object is contained in an array.
     *  Uses equal function to check equality of objects.
     *  If not provided uses reference test.
     *  @param object The object to check.
     *  @param objects The array.
     *  @param equals The equals method.
     *  @return True, if is contained.
     */
    containsObject: function(object, objects, equals)
	{
		var ret = false;
		
		for(var i=0; i<objects.length && !ret; i++)
		{
			ret = equals? equals(object, objects[i]): object===objects[i];
		}
		
		return ret;
	},

	/**
     *  Get the index of an object in an array. -1 for not contained.
     *  @param object The object to check.
     *  @param objects The array.
     *  @param equals The equals method.
     *  @return The index or -1.
     */
	indexOfObject: function(object, objects, equals)
	{
		var ret = -1;
		
		for(var i=0; i<objects.length; i++)
		{
			if(equals? equals(object, objects[i]): object===objects[i])
			{
				ret = i;
				break;
			}
		}
		
		return ret;
	},

	/**
     *  Remove an object from an array.
     *  @param object The object to remove.
     *  @param objects The array.
     *  @param equals The equals method.
     *  @return True, if was removed.
     */
	removeObject: function(object, objects, equals)
	{
		var ret = SUtil.indexOfObject(object, objects, equals);

		if(ret!=-1)		
			objects.splice(ret, 1);
		
		return ret==-1? false: true;
	},

    /**
     *  Check if the call was https.
     *  @return True if https.
     */
    isSecure: function()
    {
        return window.location.protocol == 'https:';
    }
};

/**
 *  Class that can parse json with additional features.
 *  - handles Jadex references
 */
var JsonParser =  
{
    /** The registered classes. */
    registeredClasses: {},
    
    init: function()
    {
        this.registerClass2("java.util.Date", {create: function(obj)
        {
            return new Date(obj.value);
        }});
    },
    
    /**
     *  Register a class at the parser.
     */
    registerClass: function(clazz) 
    {
        if("__classname" in clazz) 
        {
            this.registeredClasses[clazz.__classname] = clazz;
        } 
        else 
        {
            var instance = new clazz();
            if("__classname" in instance) 
            {
                 this.registeredClasses[instance.__classname] = clazz;
            } 
            else 
            {
                throw new Error("Cannot register class without __classname static field or member: " + clazz.name);
            }
        }
    },
    
    /**
     *  Register a class at the parser.
     */
    registerClass2: function(classname, create)
    {
         this.registeredClasses[classname] = create;
    },

    /**
     *  JSOn.parse extension to handle Jadex reference mechanism.
     *  @param str The string of the json object to parse.
     *  @return The parsed object.
     */
    parse: function(str, url) 
    {
        var idmarker = "__id";
        var refmarker = "__ref";
 //		var arraymarker = "__array";
 //		var collectionmarker = "__collection";
        var replacemarker = ["__array", "__collection"];

        var os = {}; // the objects per id
        var refs = []; // the unresolved references
        var obj;
        try 
        {
             obj = JSON.parse(str);
        } 
        catch(e) 
        {
            console.error("Could not parse string: " + str);
            throw e;
        }

        var recurse = (obj, prop, parent) => 
        {
    //	    console.log(obj+" "+prop+" "+parent);

            if(!SUtil.isBasicType(obj)) 
            {
                // test if it is just a placeholder object that must be changed
    //			if(prop!=null)
    //			{
                for(var i = 0; i < replacemarker.length; i++) 
                {
                    if(replacemarker[i] in obj) 
                    {
                        obj = obj[replacemarker[i]];
                        break;
                    }
                }
    //		    }

                // instantiate classes
                if("__classname" in obj) 
                {
                    var className = obj["__classname"];
                    if(className == "jadex.bridge.service.IService") 
                    {
                        obj = new Jadex.ServiceProxy(obj.serviceIdentifier, recurse(obj.methodNames, "methodNames", obj), url)
                    } 
                    else if(className in JsonParser.registeredClasses)
                    {
                        var func = JsonParser.registeredClasses[className];
                        
                        if(func.create)
                        {
                            obj = func.create(obj);
                        }
                        else
                        {
                            // iterate members:
                            var instance = new func();
                            for(var prop in obj) 
                            {
                                instance[prop] = recurse(obj[prop], prop, obj);
                            }
                            obj = instance;
                        }
                    }
                } 
                else 
                {
                    // recreate arrays
                    if(SUtil.isArray(obj)) 
                    {
                        for(var i = 0; i < obj.length; i++) 
                        {
                            if(!SUtil.isBasicType(obj[i])) 
                            {
                                if(refmarker in obj[i]) 
                                {
                                    obj[i] = recurse(obj[i], i, obj);
                                }
                                else 
                                {
                                    obj[i] = recurse(obj[i], prop, obj);
                                }
                            }
                        }
                    }
                }

                if(refmarker in obj) 
                {
                    var ref = obj[refmarker];
                    if(ref in os) 
                    {
                        obj = os[ref];
                    }
                    else 
                    {
                        refs.push([parent, prop, ref]); // lazy evaluation necessary
                    }
                }
                else 
                {
                    var id = null;
                    if(idmarker in obj) 
                    {
                        id = obj[idmarker];
                        delete obj[idmarker];
                    }
                    if("$values" in obj) // an array
                    {
                        obj = obj.$values.map(recurse);
                    }
                    else 
                    {
                        for(var prop in obj)  
                        {
                            obj[prop] = recurse(obj[prop], prop, obj);
                        }
                    }
                    if(id != null) 
                    {
                        os[id] = obj;
                    }
                }

                // unwrap boxed values for JS:
                var wrappedType = SUtil.isWrappedType(obj);
                if(wrappedType) 
                {
                    // console.log("found wrapped: " + isWrappedType(obj) + " for: " + obj.__classname + " with value: " + obj.value)
                    if(wrappedType == "boolean")
                    {
                        // this will not happen, because booleans are already native?
                        // obj = obj.value == "true"
                    }
                    else if(wrappedType == "string")
                    {
                        obj = obj.value
                    }
                    else
                    {
                        // everything else is a number in JS
                        obj = +obj.value
                    }
                }
                else if(SUtil.isEnum(obj))
                {
                    // convert enums to strings
                    obj = obj.value;
                }
            }
            return obj;
        };

        obj = recurse(obj, null, null);

        // resolve lazy references
        for(var i = 0; i < refs.length; i++) 
        {
            var ref = refs[i];
            ref[0][ref[1]] = os[ref[2]];
        }
        return obj;
    }
};
JsonParser.init(); 

var ConnectionHandler =  
{
    /** The websocket connections. */
    connections: [],

    baseurl: null,
    
    /** The map of open outcalls. */
    outcalls: [],
    
    /** The map of provided services (sid -> service invocation function). */
    providedServices: [],

    init: function() 
    {
        var scripts = document.getElementsByTagName('script');
        var script = scripts[scripts.length - 1];
        var prot = SUtil.isSecure()? "wss": "ws";
        
        if(script["src"]) 
        {
            this.baseurl = script["src"];
            this.baseurl = prot+this.baseurl.substring(this.baseurl.indexOf("://"));
        } 
        else if(script.hasAttributes())
        {
            //this.baseurl = "ws://" + window.location.hostname + ":" + window.location.port + "/wswebapi";
            this.baseurl = prot+"://" + window.location.hostname + ":" + window.location.port + script.attributes.getNamedItem("src").value;
        } 
        else 
        {
            // fail?
            throw new Error("Could not find websocket url");
        }

        this.baseurl = this.baseurl.substring(0, this.baseurl.lastIndexOf("jadex.js")-1);

        this.connections[""] = this.addConnection(this.baseurl);
        //this.connections[undefined] = this.connections[null];
    },
    
    WebsocketCall: function(type, resolve, reject, cb)
    {
    	var self = this;
    	this.type = type;
        this.finished = false;
        this.resolve = resolve;
        this.reject = reject;
        this.cb = cb;

        /**
         *  Resume the listeners of promise.
         */
        this.resume = function(result, exception) 
        {
            if(self.cb != null && (exception == null || exception == undefined) && !SUtil.isTrue(self.finished)) 
            {
                self.cb(result);
            }
            else if(SUtil.isTrue(self.finished)) 
            {
                exception == null || exception == undefined? self.resolve(result): self.reject(exception);
            }
        }
    },

    /**
     *  Internal function to get a web socket for a url.
     */
    getConnection: function(url)
    {
        if(url == null)
            url = "";
        
        var ret = this.connections[url];
        if(ret!=null)
        {
            return ret;
        }
        else
        {
            return this.addConnection(url);
        }
    },

    /**
     *  Add a new server connection.
     *  @param url The url.
     */
    addConnection: function(url)
    {
    	var self = this;
    	
        this.connections[url] = new Promise(function(resolve, reject)
        {
            try
            {
                var ws = new WebSocket(url);

                ws.onopen = () =>
                {
                    resolve(ws);
                };

                ws.onmessage = message =>
                {
                    self.onMessage(message, url);
                };
            }
            catch(e)
            {
                reject(e);
            }
        });

        return this.connections[url];
    },

    /**
     *  Send a message to the server and create a callid for the answer message.
     */
    sendData: function(url, data)
    {
        this.getConnection(url).then(ws =>
        {
            ws.send(data);
        });
    },

    /**
     *  Send a message to the server in an ongoing conversation.
     */
    sendConversationMessage: function(url, cmd)
    {
        this.getConnection(url).then((ws) =>
        {
            ws.send(JSON.stringify(cmd));
        });
    },

    /**
     *  Send a message to the server and create a callid for the answer message.
     */
    sendMessage: function(url, cmd, type, resolve, reject, callback)
    {
        // todo: use Jadex binary to serialize message and send
        var callid = this.randomString(-1);

        this.outcalls[callid] = new this.WebsocketCall(type, resolve, reject, callback);

        cmd.callid = callid;

        this.sendRawMessage(url, cmd);

        return callid;
    },

    /**
     *  Send a raw message without callid management.
     */
    sendRawMessage: function(url, cmd) 
    {
        if(!cmd.callid)
            console.log("Sending message without callid: "+cmd);

        var data = this.objectToJson(cmd);
        //console.log(data);
        
        //var size = sizeOf(cmd);
        var size = data.length;
        var limit = 7000; // 8192

        // If message is larger than limit slice the message via partial messages
        if(size>limit)
        {
            var cnt = Math.ceil(size/limit);

            for(var i=0; i<cnt; i++)
            {
                var part = data.substring(i*limit, (i+1)*limit);

                var pcmd = new PartialMessage(cmd.callid, part, i, cnt);

                var pdata = JSON.stringify(pcmd);
                //console.log("sending part, size: "+pdata.length);
                this.sendData(url, pdata);
            }
        }
        else
        {
            this.sendData(url, data);
        }
    },
    
    /**
     *  Convert an object to json.
     *  Similar to JSON.stringify but can handle
     *  binary objects as base 64 strings.
     *  @param object The object.
     *  @return The json string.
     */
    objectToJson: function(object)
    {
        var replacer = (key, value) =>
        {
            if(value instanceof ArrayBuffer)
            {
                //var ret = window.btoa(value);
                var ret = btoa(String.fromCharCode.apply(null, new Uint8Array(value)));
                return ret;
            }
            else
            {
                return value;
            }
            //return value instanceof ArrayBuffer? window.btoa(value): value;
        };

        return JSON.stringify(object, replacer);
    },
    
    /**
     *  Send a result.
     */
    sendResult: function(url, result, finished, callid)
    {
        var cmd =
        {
            __classname: "org.activecomponents.webservice.messages.ResultMessage",
            callid: callid,
            result: result,
            finished: finished
        };

        this.sendRawMessage(url, cmd);
    },

    /**
     *  Send an exception.
     */
    sendException: function(url, err, finished, callid)
    {
        var exception =
        {
            __classname: "java.lang.RuntimeException",
            message: ""+err
        }

        var cmd =
        {
            __classname: "org.activecomponents.webservice.messages.ResultMessage",
            callid: callid,
            exception: exception,
            finished: finished
        };

        this.sendRawMessage(url, cmd);
    },

    /**
     *  Called when a message arrives.
     */
    onMessage: function(message, url) 
    {
        if(message.type == "message")
        {
            var msg = JsonParser.parse(message.data, url);
            var call = this.outcalls[msg.callid];

//		    console.log("outcalls: "+outcalls);

            if(call!=null)
            {
                if(SUtil.isTrue(msg.finished))
                {
                    delete this.outcalls[msg.callid];
//					console.log("outCall deleted: "+msg.callid);
                    call.finished = true;
                }

                if(call.type=="search")
                {
                    if(msg.result!=null)
                    {
                        if(msg.result.hasOwnProperty("__array"))
                            msg.result = msg.result.__array;
                        if(msg.result.hasOwnProperty("__collection"))
                            msg.result[1] = msg.result[1].__collection;
                    }

                    var serproxy;

                    if(msg.exception==null && msg.result!=null)
                    {
                        serproxy = msg.result;//createServiceProxy(msg.result[0], msg.result[1]);
                    }

                    call.resume(serproxy, msg.exception);
                }
                else if(call.type=="invoke")
                {
                    call.resume(msg.result, msg.exception);
                }
                else if(outCall.type=="provide")
                {
                    if(msg.exception!=null)
                    {
                        call.reject(msg.exception);
                    }
                    else
                    {
                        // Save the service functionality in the inca
                        this.providedServices[SUtil.getServiceIdAsString(msg.result)] = call.cb;
                        
                        call.resolve(msg.result);
                    }
                }
                else if(call.type=="unprovide")
                {
                    if(msg.exception!=null)
                    {
                        call.reject(msg.exception);
                    }
                    else
                    {
                    	// removeProperty?!
                        this.providedServices[SUtil.getServiceIdAsString(msg.result)] = null;
                        call.resolve(msg.result);
                    }
                }
            }
            else // incoming call
            {
                if(msg.__classname==="org.activecomponents.webservice.messages.ServiceInvocationMessage")
                {
                    var service = this.providedServices[SUtil.getServiceIdAsString(msg.serviceId)];

                    if(service)
                    {
                        var res;

                        // If it a service object with functions or just a function
                        if(service[msg.methodName])
                        {
                            //res = service[msg.methodName](msg.parameterValues);
                            res = service[msg.methodName].apply(undefined, msg.parameterValues);
                        }
                        // If it is just a function (assume functional interface as in Java)
                        else if(typeof res==="function")
                        {
                        	//res = service(msg.parameterValues);
                            res = service.apply(undefined, msg.parameterValues);
                        }
                        // If it is an invocation handler with a generic invoke method
                        else if(service.invoke)
                        {
                            res = service.invoke(msg.methodName, msg.parameterValues);
                        }
                        else
                        {
                            console.log("Cannot invoke service method (not found): "+msg.methodName);
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
                        Promise.resolve(res).then(res =>
                        {
                            this.sendResult(url, res, true, msg.callid);
                        })
                        .catch(e =>
                        {
                            this.sendException(url, e, true, msg.callid);
                        });
                    }
                    else
                    {
                        console.log("Provided service not found: "+[msg.serviceId]);
                        this.sendException(url, "Provided service not found: "+[msg.serviceId], true, msg.callid);
                    }
                }
                else
                {
                    console.log("Received message without request: "+msg);
                }
            }
        }
        else if(message.type == "binary")
        {
            console.log("Binary messages currently not supported");
        }
        // else: do not handle pong messages
    },

    /**
     *  Create a random string.
     *  @param length The length of the string.
     *  @returns The random string.
     */
    randomString: function(length)
    {
        if(length<1)
            length = 10;
        return Math.round((Math.pow(36, length + 1) - Math.random() * Math.pow(36, length))).toString(36).slice(1);
    }
};
ConnectionHandler.init();

var Jadex =
{
	/**
	 *  Service Proxy constructor function.
	 */
	ServiceProxy: function(serviceid, methodnames, url)
	{
	    var outer = this;
		this.serviceid = serviceid;
		//this.methodnames = methodnames;
		//this.url = url;
		
		/**
		 *  Create method function (needed to preserve the name).
		 *
		 *  Creates an argument array and invokes generic invoke method.
		 *
		 *  TODO: callback function hack!
		 */
		createMethod = function(name) 
		{
		    return function () 
		    {
		        var params = [];
		        var callback;

		        for(var j = 0; j < arguments.length; j++) 
		        {
		            if(typeof arguments[j] === "function") 
		            {
		                callback = arguments[j];
		            }
		            else 
		            {
		                params.push(arguments[j]);
		            }
		        }
		        return outer.invoke(name, params, callback);
		    }
		}
		
		for(var i = 0; i < methodnames.length; i++) 
	    {
	        this[methodnames[i]] = createMethod(methodnames[i]);
	    }
		
		/**
		 *  Generic invoke method that sends a method call to the server side.
		 */
		this.invoke = function(name, params, callback) 
		{
		    var ret = new Jadex.JadexPromise(this.url);
		    
		    // Convert parameters seperately, one by one
		    var cparams = [];
		    for(var i=0; i<params.length; i++)
		    {
		        cparams.push(ConnectionHandler.objectToJson(params[i]));
		    }
		    
		    var cmd = new Jadex.ServiceInvocationMessage(this.serviceid, name, cparams);

		    // console.log(cmd);

		    // wrap callback to allow JadexPromise.intermediateThen
		    var wrapcb = res => 
		    {
		        // console.log("calling intermediate result with: " + intermediateResult);
		        ret.resolveIntermediate(res);
		        if(callback) 
		            callback(res);
		    };
		    ret.callid = ConnectionHandler.sendMessage(this.url, cmd, "invoke", res => ret.resolve(res), ex => ret.reject(ex), wrapcb);

		    return ret;
		}
	},
	
	JadexPromise: function(resolveFunc, rejectFunc, callid)
	{
		this.resolveFunc = resolveFunc;
		this.rejectFunc = rejectFunc;
		this.intermediateResolveCallbacks = [];
		this.callid = callid;
		
		this.promise = new Promise((resolve, reject) => 
		{
            this.resolveFunc = resolve;
            this.rejectFunc = reject;
        });
		
	    this.then = function(onfulfilled, onrejected) 
	    {
	        return this.promise.then(onfulfilled, onrejected);
	    };

	    this.resolve = function(value)
	    {
	        // return this.promise.resolve(value);
	        return this.resolveFunc(value);
	    };

	    this.reject = function(error) 
	    {
	        // return this.promise.reject(error);
	        return this.rejectFunc(error);
	    };

	    this.resolveIntermediate = function(value)
	    {
	        for(var cb of this.intermediateResolveCallbacks)
	        {
	            cb(value);
	        }
	    };

	    this.thenIntermediate = function(onfulfilled, onrejected)//?: (value: T)=>(PromiseLike<TResult>|TResult), onrejected?: (reason: any)=>(PromiseLike<TResult>|TResult))
	    {
	        this.intermediateResolveCallbacks.push(onfulfilled);
	        // this.intermediateRejectCallbacks.push(onrejected);
	    };

	    this.terminate = function()
	    {
	        var cmd =
	        {
	            __classname: "org.activecomponents.webservice.messages.ServiceTerminateInvocationMessage",
	            callid: this.callid
	        };

	        ConnectionHandler.sendConversationMessage(this.url, cmd);
	    };

	    this.pull = function()
	    {
	        var cmd =
	        {
	            __classname: "org.activecomponents.webservice.messages.PullResultMessage",
	            callid: this.callid
	        };

	        ConnectionHandler.sendConversationMessage(this.url, cmd);
	    };
	},
	
	ServiceSearchMessage: function(type, multiple, scope)
	{
		this.__classname = "org.activecomponents.webservice.messages.ServiceSearchMessage"; 
		//this.callid = callid;
		this.type = type;
		this.multiple = multiple;
		this.scope = scope;
	},
	
	ServiceInvocationMessage: function(serviceid, methodname, parametervalues)
	{
		this.__classname = "org.activecomponents.webservice.messages.ServiceInvocationMessage"; 
		this.serviceId = serviceid;
		this.methodName = methodname;
		this.parameterValues = parametervalues;
	},
	
	ServiceUnprovideMessage: function(serviceid) 
    {
        this.__classname = "org.activecomponents.webservice.messages.ServiceUnprovideMessage";
		this.serviceId = serviceid;
    },
    
    PartialMessage: function(callid, data, number, count) 
    {
    	this.__classname = "org.activecomponents.webservice.messages.PartialMessage";
    	this.callid = callid;
    	this.data = data;
    	this.number = number;
    	this.count = count;
    },
    
    ServiceProvideMessage: function(type, scope, tags) 
    {
    	this.__classname = "org.activecomponents.webservice.messages.ServiceProvideMessage";
    	this.type = type;
    	this.scope = scope;
    	this.tags = tags;
    },
	
	/**
     *  Get a service from a publication point.
     *  @param type The service type.
     *  @param scope The scope.
     *  @param url The url of the websocket.
     */
    getService: function(type, scope, url) 
    {
    	var self = this;
        var prom = SUtil.addErrHandler(new Promise((resolve, reject) => 
        {
            var cmd = new self.ServiceSearchMessage(type, false, scope != null ? scope : Scopes.SCOPE_PLATFORM);
            ConnectionHandler.sendMessage(url, cmd, "search", resolve, reject, null);
        }));
        return prom;
    },

    /**
     *  Get services from a publication point.
     *  @param type The service type.
     *  @param callback The callback function for the intermediate results.
     *  @param scope The search scope.
     *  @param url The url of the websocket.
     */
    getServices: function(type, callback, scope, url) 
    {
    	var self = this;
        SUtil.assert(callback instanceof Function && callback != null);
        var prom = SUtil.addErrHandler(new Promise((resolve, reject) => 
        {
            var cmd = new self.ServiceSearchMessage(type, true, scope != null ? scope : Scopes.SCOPE_PLATFORM);
            ConnectionHandler.sendMessage(url, cmd, "search", resolve, reject, callback);
        }));
        return prom;
    },

    /**
     *  Provide a new (client) service.
     *  @param type The service type.
     *  @param scope The provision scope.
     *  @param url The url of the websocket.
     */
    provideService: function(type, scope, tags, callback, url)
    {
    	var self = this;
        return SUtil.addErrHandler(new Promise(function(resolve, reject)
        {
            var cmd = new self.ServiceProvideMessage(type, scope!=null? scope: "global", typeof tags === "string"? [tags]: tags);
            ConnectionHandler.sendMessage(url, cmd, "provide", resolve, reject, callback);
        }));
    },

    /**
     *  Unprovide a (client) service.
     *  @param type The service type.
     *  @param scope The provision scope.
     *  @param url The url of the websocket.
     */
    unprovideService: function(sid, url)
    {
    	var self = this;
        return SUtil.addErrHandler(new Promise(function(resolve, reject)
        {
            var cmd = new self.ServiceUnprovideMessage(sid);
            ConnectionHandler.sendMessage(url, cmd, "unprovide", resolve, reject, null);
        }));
    },

    /**
     *  Register a class for json (de)serialization.
     */
    registerClass: function(clazz) 
    {
        JsonParser.registerClass(clazz);
    }
};

	