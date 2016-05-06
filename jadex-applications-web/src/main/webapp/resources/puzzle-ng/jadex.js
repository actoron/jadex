/**
 *  Jadex Javascript API.
 */
var jadex = new (function()
{		
	/** The websocket connections. */
	var connections = [];

	/** The map of open calls. */
	var calls = [];
	
	/**
	 *  Get a service from a publication point.
	 *  @param type The service type.
	 */
	this.getService = function(type)
	{
		return getService(null, type);
	};
	
	/**
	 *  Get a service from a publication point.
	 *  @param url The url of the websocket.
	 *  @param type The service type.
	 */
	this.getService = function(url, type)
	{
		return new Promise(function(resolve, reject)
		{
			var cmd = 
			{
				__classname: "com.actoron.webservice.messages.ServiceSearchMessage",
				type: type,
				multiple: false,
				//scope: "global" 
			};
			
			sendMessage(url, cmd, "search", resolve, reject);
		});
	};
	
	/**
	 *  Get services from a publication point.
	 *  @param url The url of the websocket.
	 *  @param type The service type.
	 */
	this.getServices = function(type, callback)
	{
		return getServices(null, type, callback);
	}
	
	/**
	 *  Get services from a publication point.
	 *  @param url The url of the websocket.
	 *  @param type The service type.
	 */
	this.getServices = function(url, type, callback)
	{
		return new Promise(function(resolve, reject)
		{
			var cmd = 
			{
				__classname: "com.actoron.webservice.messages.ServiceSearchMessage",
				type: type,
				multiple: true,
				//scope: "global" 
			};
			
			sendMessage(url, cmd, "search", resolve, reject, callback);
		});
	};
	
	/**
	 *  Internal function to get a web socket for a url.
	 */
	var getConnection = function getConnection(url)
	{
		var ret = connections[url];
		if(ret!=null)
		{
			return new Promise(function(resolve, reject)
			{
				resolve(ret);
			});
		}
		else
		{
			return jadex.addConnection(url);
		}
	};
	
	/**
	 *  Add a new server connection.
	 *  @param url The url.
	 */
	this.addConnection = function(url)
	{
		return new Promise(function(resolve, reject)
		{
			try
			{
				var ws = new WebSocket(url);
			
				ws.onopen = function()
				{
					connections[url] = ws;
					resolve(ws);
				};
				
				ws.onmessage = function(message)
				{
					if(message.type == "message")
			    	{
						var msg = JSON.parse(message.data);
						if(msg.result!=null)
						{
							msg.result = msg.result.__array; 
							msg.result[1] = msg.result[1].__collection;
						}
						
						var call = calls[msg.callid];
						
						if(call!=null)
						{
							if(msg.finished)
							{
								delete calls[msg.callid];
		//						alert("call deleted: "+msg.callid);
								call.finished = true;
							}
							
							if(call["type"]=="search")
							{
								var serproxy;
								
								if(msg.exception==null && msg.result!=null)
								{
									serproxy = 
									{
										serviceId: msg.result[0],
										
										invoke: function(name, params, callback)
										{
											var callid;
											
											var ret = new Promise(function(resolve, reject)
											{
												var cmd = 
												{
													__classname: "com.actoron.webservice.messages.ServiceInvocationMessage",
													serviceId: msg.result[0],
													methodName: name,
													parameterValues: params
												};
												
												// Remember the callid of the invocation
												callid = sendMessage(url, cmd, "invoke", resolve, reject, callback);
											});
											
											ret.terminate = function()
											{
												var cmd = 
												{
													__classname: "com.actoron.webservice.messages.ServiceTerminateInvocationMessage",
													callid: callid  
												};
												
												sendConversationMessage(cmd);
											};
											
											ret.pull = function()
											{
												var cmd = 
												{
													__classname: "com.actoron.webservice.messages.PullResultMessage",
													callid: callid  
												};
												
												sendConversationMessage(cmd);
											};
											
											return ret;
										}
									}
									
									for(var i=0; i<msg.result[1].length; i++)
									{
										serproxy[msg.result[1][i]] = createMethod(msg.result[1][i]);
									}
								}
								
								resume(call, serproxy, msg.exception);
							}
							
							else if(call["type"]=="invoke")
							{
								resume(call, msg.result, msg.exception);
							}
						}
			    	}
			    	else if(message.type == "binary")
			    	{
			    		console.log("Binary messages currently not supported");
			    	}
			    	// else: do not handle pong messages 
				};
			}
			catch(e)
			{
				reject(e);
			}
		});
	};
	
	/**
	 *  Create method function (needed to preserve the name).
	 */
	function createMethod(name) 
	{
	    return function()
		{
			var params = [];
			var callback;
			
			for(var j=0; j<arguments.length; j++)
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
			return this.invoke(name, params, callback);
		}
	}
	
	/**
	 *  Send a message to the server and create a callid for the answer message.
	 */
	function sendMessage(url, cmd, type, resolve, reject, callback)
	{
		var callid = randomString(-1);
		getConnection(url).then(function(ws)
		{
			calls[callid] = {type: type, rl: resolve, el: reject, cb: callback};
			cmd["callid"] = callid;
			ws.send(JSON.stringify(cmd));			
		});
		return callid;
	};
	
	/**
	 *  Send a message to the server in an ongoing conversation.
	 */
	function sendConversationMessage(cmd)
	{
		getConnection(null).then(function(ws)
		{
			ws.send(JSON.stringify(cmd));			
		});
	};
	
	/**
	 *  Resume the listeners of promise.
	 */
	function resume(call, res, ex)
	{
		if(call.cb!=null && (ex===null || ex===undefined) && !call.finished)
		{
			call["cb"](res);
		}
		else if(call.finished)
		{
			ex==null? call["rl"](res): call["el"](ex);
		}
	}
	
	//-------- helper methods --------
	
	/**
	 *  Create a random string.
	 *  @param length The length of the string.
	 *  @returns The random string.
	 */
	function randomString(length) 
	{
		if(length<1)
			length = 10;
	    return Math.round((Math.pow(36, length + 1) - Math.random() * Math.pow(36, length))).toString(36).slice(1);
	};	
	
	/**
	 *  Test if a number is a float.
	 *  @param n The number to test.
	 *  @return True, if is float.
	 */
	function isFloat(n) 
	{
	    return n === +n && n !== (n|0);
	}

	/**
	 *  Test if a number is an integer.
	 *  @param n The number to test.
	 *  @return True, if is integer.
	 */
	function isInteger(n) 
	{
	    return n === +n && n === (n|0);
	}
		
	/**
	 *  JSOn.parse extension to handle Jadex reference mechanism.
	 *  @param str The string of the json object to parse.
	 *  @return The parsed object.
	 */
	function parse(str) 
	{
		var idmarker = "__id";
		var refmarker = "__ref";
//		var arraymarker = "__array";
//		var collectionmarker = "__collection";
		var replacemarker = ["__array", "__collection"];
		
		var os = {}; // the objects per id
		var refs = []; // the unresolved references
		var obj = JSON.parse(str);
		
		function recurse(obj, prop, parent)
		{
			if(!isBasicType(obj))
			{
				// test if it is just a placeholder object that must be changed
//				if(prop!=null)
//				{
					for(var i=0; i<replacemarker.length; i++)
					{
						if(replacemarker[i] in obj)
						{
							obj = obj.replacemarker;
							break;
						}
					}
//				}
				
				if(isArray(obj)) 
				{
		            for(var i=0; i<obj.length; i++)
		            {
		                if(!isBasicType(obj[i]))
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
	        		if(id!=null)
	        		{
	        			os[id] = obj;
	        		}
	        	}
	        }
			return obj;
		}
		
		obj = recurse(obj, null, null);
		
		// resolve lazy references
		for(var i=0; i<refs.length; i++) 
		{ 
			var ref = refs[i];
		    ref[0][ref[1]] = os[ref[2]];     
		}
		return obj;
	}
	
	/**
	 *  Test if an object is a basic type.
	 *  @param obj The object.
	 *  @return True, if is a basic type.
	 */
	function isBasicType(obj)
	{
		return typeof obj !== 'object' || !obj;
	}
	
	/**
	 *  Test if an object is an array.
	 *  @param obj The object.
	 *  @return True, if is an array.
	 */
	function isArray(obj) 
	{
		return Object.prototype.toString.call(obj) == '[object Array]';
	}
		
	var baseurl = "ws://localhost:8080/jadex-applications-web/wswebapi"; //"ws://localhost:8080/actoron-webapi-web/wsock");
	this.addConnection(baseurl).then(function(result)
	{
		connections[null] = result;
	});
})

