(function (global, factory) {
    if (typeof define === 'function' && define.amd) 
	{
        define([], factory);
    } 
	else if (typeof module !== 'undefined' && module.exports)
	{
        module.exports = factory();
    } 
	else 
	{
        global.ReconnectingWebSocket = factory();
    }
})(this, function () 
{
	var Jadex = 
	{
		baseurl: 'webjcc',
		source: null,
		conversations: {},
	
		init: function()
		{
			//console.log("jadex init running");
			
			var self = this;
			
			// create/set cookie for unique id for 
			var cookie = self.getCookie("jadex");
			if(!cookie)
			{
				var id = self.generateUUID();
				self.setCookie("jadex", id);
				console.log("created Jadex cookie: "+id);
			}
			
			this.source = new EventSource(this.baseurl);
			this.source.addEventListener('open', function(e) 
			{
				//console.log('con established');
			}, false);
			this.source.onmessage = function(event) 
			{
				self.processEvent(event, 0);
			};
			/*this.source.addEventListener('message', function (e) 
			{
				console.log("message received: "+e.data);
				
			}, false);*/
			var retries = 0;
			this.source.addEventListener('error', function(e) 
			{
				//console.log('event source err: '+e);
				// notify all ongoing conversations that connection was closed	
				if(e.target.readyState === EventSource.CONNECTING)
				{
					retries++;
				}		
			    if(e.target.readyState === EventSource.CLOSED || retries===2) 
				{
			    	console.log('sse connection closed');
					retries = 0;
					var event = {data: {name: 'sse connection closed'}};
					for(var convid in self.conversations) 
					{
						var cb = self.conversations[convid];
						cb[1](event);
					}
					self.conversations = {};
				}
			}, false);
		},
		
		processEvent: function(event, cnt)
		{
			//console.log("message received: "+JSON.stringify(event.data));
			var self = this;
			
			var sseevent = event.data!=null? JSON.parse(event.data): null;
			var callid = sseevent.callId;
			
			// check if updatetimer command was received
			// send alive when callid is still used, otherwise ignore
			if("updatetimer"===sseevent?.data?.value?.toLowerCase())
			{
				var cinfo = self.conversations[callid];
				if(cinfo==null)
				{
					console.log("updatetimer, conversation not found: "+callid);
					
					// todo: which path???
					axios.get(self.baseurl, {headers: {'x-jadex-callid': callid, 'x-jadex-terminate': "true", 
						'cache-control': 'no-cache, no-store'}}, this.transform)
						.then(x =>
						{
							//console.log("terminate success: "+callid);
						}).catch(err =>
						{
							console.log("terminate err: "+callid+" "+err);
						});
				}
				else
				{
					//console.log("terminating request sent: "+path);
					axios.get(cinfo[3], {headers: {'x-jadex-callid': callid, 'x-jadex-alive': "true", 
						'cache-control': 'no-cache, no-store'}}, this.transform)
						.then(x =>
						{
							//console.log("alive success: "+callid);
						}).catch(err =>
						{
							console.log("alive err: "+callid+" "+err);
						});
				}
			}
			else
			{
				// [callback, errhandler]
				var cb = self.conversations[event.lastEventId];
				if(cb!=null)
				{
					if(sseevent?.data?.stackTrace!=null)
						cb[1](sseevent);
					else if(sseevent?.max!=null)
						cb[2](sseevent.max);
					else
						cb[0](sseevent);
				}
				else
				{
					// done! todo: refactor id handling and create id on client
					// problem: order of http and sse answer is undertermined
					// but currently http answer contains conversation id and is needed before sse event
					// that must use the id to lookup the handlers
					
					/*if(cnt<3)
					{
						console.log("retry event: "+JSON.stringify(event)+" "+cnt);
						setTimeout(() => self.processEvent(event, ++cnt), 1000);
					}
					else
					{*/
						console.log("cannot handle event: "+JSON.stringify(event)+" "+cnt);
					//}
				}
			}
		},
								
		transform: 
		{
			// Currently this impl is the same as internal axios 
			transformResponse: [function(data) 
			{
				/*daterev = function reviver(key, value) 
				{
					if(typeof value === "string" && /^\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}Z$/.test(value)) 
					{
				    	return new Date(value);
				    }
				    return value;
				}*/
				//console.log("received data: "+data);
				try
				{
					var ret = JSON.parse(data);//, daterev);
					//console.log("json: "+ret);
					return ret;
				}
				catch(ex)
				{
					//console.log("ex: "+ex);
				}
				//console.log("data: "+data);
				return data;
			}]
		},	
			
		getIntermediate: function(path, handler, errhandler, maxhandler) 
		{
			//console.log("getIntermediate: "+path);
			
			var self = this;
			
			// problem with hanging requests to same URL because of cache
			// https://stackoverflow.com/questions/27513994/chrome-stalls-when-making-multiple-requests-to-same-resource
			//if(path.indexOf("?")==-1)
			//	path = path+'?__random='+Math.random();
			//else 
			//	path = path+'&__random='+Math.random();
			
			// ensure that errhandler is called at most once
			var finished = false;
	
			var errfunc = function(err)
			{
				if(finished)
					return;
				finished = true;
				
				//if(axios.isCancel(err))
	            //   console.log('request canceled', err.message);
				
				//console.log("errfunc: "+JSON.stringify(err));
				
				if(err.exceptionType!=null && err.exceptionType.indexOf("TerminatedException")!=-1)
				{
					//console.log("call terminated: "+JSON.stringify(err));
					return;
				}
				
				errhandler(err);
			}
			
			var	func = function(resp)
			{
				if(finished)
					return;
				
				var fini = resp.headers["x-jadex-callidfin"];
				var callid = resp.headers["x-jadex-callid"];
				var max = resp.headers["x-jadex-max"];
				var sse = resp.data=="sse";
				if(fini!=null && callid==null)
					callid = fini;
				//console.log("call sse:"+sse+" "+resp.data);
				
				if(!sse)
				{
					if(resp.status!=202)	
					{
						if(max!=null)
							maxhandler(max);
						else
							handler(resp);
					}
					else // updatetimer (and other) commands
					{
						if(resp.data.toLowerCase()==="updatetimer")
						{
							if(self.conversations[callid]==null)
							{
								console.log("ignoring updatetimer, conversation not found: "+callid);
							}
							else
							{
								//console.log("terminating request sent: "+path);
								axios.get(path, {headers: {'x-jadex-callid': callid, 'x-jadex-alive': "alive", 
									'cache-control': 'no-cache, no-store'}}, this.transform)
									.then(x =>
									{
										console.log("alive success: "+callid);
									}).catch(err =>
									{
										console.log("alive err: "+callid+" "+err);
									});
							}
						}
						else
						{
							console.log("received unknown command: "+resp.data);
						}
					}
				}
				
				//call = axios.CancelToken.source();
				
				//console.log("received: "+resp)
				
				//console.log("call headers: "+resp.headers["x-jadex-callid"]+" "+resp.headers["x-jadex-callidfin"]);
				
				/*if(sse)
				{
					//console.log("response via sse: "+path+" "+callid);
					if(self.conversations[callid]==null)
					{
						//console.log("saved conversation: "+callid);
						self.conversations[callid] = [handler, errfunc, maxhandler]; //errhandler
					}
				}
				else */if(fini)
				{
					//console.log("call finished: "+fini);
				}
				else if(!sse)
				{
					console.log("not supported long poll: "+path+" "+callid);
					/*if(callid!=null)
					{
						//console.log("long-poll request sent: "+path);
						
						var headers = {'x-jadex-callid': callid, 'cache-control': 'no-cache, no-store', "x-jadex-sse": true};
						axios.get(path, {cancelToken: call.token, headers: headers}, this.transform).then(func).catch(errfunc); 
					}*/
				}
				
				return callid;
			};
			
			var ok = false;
			var callid;
			while(!ok)
			{
				callid = self.generateUUID();
			
				//console.log("response via sse: "+path+" "+callid);
				if(self.conversations[callid]==null)
				{
					//console.log("saved conversation: "+callid);
					self.conversations[callid] = [handler, errfunc, maxhandler, path]; //errhandler
					ok = true;
				}
				else
				{
					console.log("convid collision, retry: "+callid);
				}
			}
			
			//console.log("initial request sent: "+path);
			//call = axios.CancelToken.source();
			
			var headers = {'x-jadex-callid': callid, 'cache-control': 'no-cache, no-store', "x-jadex-sse": true};
			axios.get(path, {headers: headers}, this.transform)
			//axios.get(path, {cancelToken: call.token, headers: headers}, this.transform)
				.then(function(resp) 
				{
					var callid = func(resp); 
					//if(callid!=null) 
					//{
						//console.log("received callid: "+callid);
						//resolve(callid);
					//}
				})
				.catch(function(err) 
				{
					//reject(err); 
					errfunc(err);
				});
			
			return callid;
		},
		
		terminateCall: function(callid, reason)
		{
			var self = this;
			
			if(this.conversations[callid]==undefined)
			{
				return new Promise(function(resolve, reject)
				{
					reject("Callid unknown callid");
				});
			}
			else
			{
				// remove the property
				var path = this.conversations[callid][3];
				delete this.conversations.callid;
				
				return new Promise(function(resolve, reject)
				{
					var errhandler = function(err)
					{
						if(err.message!=null && err.message.indexOf('Network Error')!=-1)
						{
							// when connection lost
							reject(err);
						}
						else if(err.exceptionType?.indexOf("TerminatedException")!=-1)
						{
							// no real err when it was jadex terminated exception
							//console.log("call terminated: "+JSON.stringify(err));
							resolve();
						}
						else
						{
							console.log("error in termination: "+err);//JSON.stringify(err));
							reject(err);
						}
					};
					
					//if(call)
			        //    call.cancel();
					
					//terminated = true;
					var r = reason==null? 'true': reason;
					
					//console.log("terminating request sent: "+path);
					axios.get(path, {headers: {'x-jadex-callid': callid, 'x-jadex-terminate': r, 
						'cache-control': 'no-cache, no-store', "x-jadex-sse": true}}, this.transform)
						.then(resolve).catch(errhandler);
				});
			}
		},
		
		createProxy: function(cid, servicetype)
		{
			var self = this;
			let ret = new Proxy({cid: cid, type:servicetype, transform:self.transform},
			{
				get: function(service, prop)
				{
					let callstrprefix = self.baseurl+'/invokeServiceMethod?cid='+service.cid+'&servicetype='+service.type+'&methodname='+prop;
					return function(...args)
					{
						let callstr = callstrprefix;
						for (let i = 0; i < args.length; i++)
							callstr += '&args_'+i+'='+args[i];
						
						return axios.get(callstr, service.transform);
			        }
				}
		    });
			return ret;
		},
		
		/*generateUUID: function() 
		{ 
		    var d = new Date().getTime();
		    if(typeof performance !== 'undefined' && typeof performance.now === 'function')
		        d += performance.now();
		    return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) 
		    {
		        var r = (d + Math.random() * 16) % 16 | 0;
		        d = Math.floor(d / 16);
		        return (c === 'x' ? r : (r & 0x3 | 0x8)).toString(16);
		    });
		}*/
		
		// https://www.arungudelli.com/tutorial/javascript/how-to-create-uuid-guid-in-javascript-with-examples/
		generateUUID: function() 
		{
			return ([1e7]+-1e3+-4e3+-8e3+-1e11).replace(/[018]/g, c =>
			    (c ^ crypto.getRandomValues(new Uint8Array(1))[0] & 15 >> c / 4).toString(16));
		},
		
		getCookie: function(cname)
		{
			cname=cname+"=";
			var ret=undefined;
			var toks=document.cookie.split(';');
		
			for(var i=0; i<toks.length; i++) 
			{
				var tok=toks[i].trim();
				if (tok.indexOf(cname)==0) 
				{
					ret=decodeURIComponent(atob(tok.substring(cname.length,tok.length)));
				}
			}
			return ret;
		},
	
		deleteCookie: function(cname)
		{
			document.cookie=cname+"=; expires=Thu, 01 Jan 1970 00:00:01 GMT;";
		},
	
		setCookie: function(cname, value)
		{
			this.deleteCookie(cname);
			document.cookie=cname+"="+btoa(encodeURIComponent(value));
		}
	};
	Jadex.init();
	window.jadex = Jadex;
});
