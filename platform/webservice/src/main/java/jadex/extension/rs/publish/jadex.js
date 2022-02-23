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
		source: null,
		conversations: {},
	
		init: function()
		{
			var self = this;
			
			this.source = new EventSource('webjcc');
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
			this.source.addEventListener('error', function(e) 
			{
			    //if(e.readyState === EventSource.CLOSED) 
			    //	console.log('connection closed');
			}, false);
		},
		
		processEvent: function(event, cnt)
		{
			//console.log("message received: "+JSON.stringify(event.data));
			var self = this;
			
			// [callback, errhandler]
			var cb = self.conversations[event.lastEventId];
			if(cb!=null)
			{
				var event = JSON.parse(event.data);
			
				if(event?.data?.stackTrace!=null)
					cb[1](event);
				else if(event?.max!=null)
					cb[2](event.max);
				else
					cb[0](event);
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
			
			var terminated = false;
			var call;
	
			var errfunc = function(err)
			{
				//if(axios.isCancel(err))
	            //   console.log('request canceled', err.message);
				
				//console.log("errfunc: "+JSON.stringify(err));
				
				if(err.exceptionType?.indexOf("TerminatedException")!=-1)
				{
					//console.log("call terminated: "+JSON.stringify(err));
					return;
				}
				
				if(terminated)
				{
					//console.log("call terminated: "+path);
					return;
				}
					
				errhandler(err);
			}
			
			var	func = function(resp)
			{
				if(terminated)
				{
					//console.log("call terminated: "+path);
					return;
				}
				
				var fini = resp.headers["x-jadex-callidfin"];
				var callid = resp.headers["x-jadex-callid"];
				var max = resp.headers["x-jadex-max"];
				var sse = resp.data=="sse";
				if(fini!=null && callid==null)
					callid = fini;
				//console.log("call sse:"+sse+" "+resp.data);
				
				if(!sse)
				{
					if(resp.status!=202)	// ignore updatetimer commands
					{
						if(max!=null)
							maxhandler(max);
						else
							handler(resp);
					}
				}
				
				call = axios.CancelToken.source();
				
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
					console.log("long poll call: "+path+" "+callid);
					if(callid!=null)
					{
						//console.log("long-poll request sent: "+path);
						
						var headers = {'x-jadex-callid': callid, 'cache-control': 'no-cache, no-store', "x-jadex-sse": true};
						axios.get(path, {cancelToken: call.token, headers: headers}, this.transform).then(func).catch(errfunc); 
					}
				}
				
				return callid;
			};
			
			var prom = new Promise(function(resolve, reject)
			{
				//console.log("initial request sent: "+path);
				call = axios.CancelToken.source();
				
				var ok = false;
				var callid;
				while(!ok)
				{
					callid = self.generateUUID();
				
					//console.log("response via sse: "+path+" "+callid);
					if(self.conversations[callid]==null)
					{
						//console.log("saved conversation: "+callid);
						self.conversations[callid] = [handler, errfunc, maxhandler]; //errhandler
						ok = true;
					}
					else
					{
						console.log("convid collision, retry: "+callid);
					}
				}
				
				var headers = {'x-jadex-callid': callid, 'cache-control': 'no-cache, no-store', "x-jadex-sse": true};
				axios.get(path, {cancelToken: call.token, headers: headers}, this.transform)
					.then(function(resp) 
					{
						var callid = func(resp); 
						if(callid!=null) 
						{
							//console.log("received callid: "+callid);
							resolve(callid);
						}
					})
					.catch(function(err) 
					{
						reject(err); 
						errfunc(err);
					});
			});
			
			var termcom = function(reason)
			{
				return new Promise(function(resolve, reject)
				{
					var errhandler = function(err)
					{
						console.log("error in termination: "+JSON.stringify(err));
						
						if(err.exceptionType?.indexOf("TerminatedException")!=-1)
						{
							console.log("call terminated: "+JSON.stringify(err));
							resolve();
						}
	
						reject(err);
					};
					
					prom.then(function(callid)
					{
						if(call)
				            call.cancel();
						
						terminated = true;
						var r = reason==null? 'true': reason;
						
						//console.log("terminating request sent: "+path);
						axios.get(path, {headers: {'x-jadex-callid': callid, 'x-jadex-terminate': r, headers: {'x-jadex-callid': callid, 'cache-control': 'no-cache, no-store', "x-jadex-sse": true}}}, this.transform)
							.then(resolve).catch(errhandler); 
					})
					.catch(errhandler);
				});
			}
			
			// return termination command
			return termcom;
		},
		
		createProxy: function(cid, servicetype)
		{
			let ret = new Proxy({cid: cid, type:servicetype, transform:self.transform},
			{
				get: function(service, prop)
				{
					let callstrprefix = 'webjcc/invokeServiceMethod?cid='+service.cid+'&servicetype='+service.type+'&methodname='+prop;
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
		}
	};
	Jadex.init();
});
var jadex = Jadex;