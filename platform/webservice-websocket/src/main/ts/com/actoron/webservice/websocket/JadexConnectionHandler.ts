import {ConnectionHandler} from "./ConnectionHandler";
import {JsonParser} from "../JsonParser";
import {SUtil} from "../SUtil";
import {WebsocketCall} from "./WebsocketCall";
import {ServiceMessage} from "../messages/BaseMessage";
import {PartialMessage} from "../messages/PartialMessage";

/**
 * 
 */
export class JadexConnectionHandler extends ConnectionHandler 
{
    /** The map of open outcalls. */
    outcalls:WebsocketCall[] = [];

    //	/** The map of open incoming calls. */
    //	var incalls = [];

    /** The map of provided services (sid -> service invocation function). */
    providedServices = [];

    private static INSTANCE:JadexConnectionHandler = new JadexConnectionHandler();

    /**
     *  Get the instance.
     */
    public static getInstance():JadexConnectionHandler 
    {
        return JadexConnectionHandler.INSTANCE;
    }

    /**
     *  Send a message to the server and create a callid for the answer message.
     */
    sendMessage(url, cmd:ServiceMessage, type, resolve: (result?) => any, reject: (result?) => any, callback)
    {
        // todo: use Jadex binary to serialize message and send
        let callid = this.randomString(-1);

        this.outcalls[callid] = new WebsocketCall(type, resolve, reject, callback);

        cmd.callid = callid;

        this.sendRawMessage(url, cmd);

        return callid;
    };

    /**
     *  Send a raw message without callid management.
     */
    sendRawMessage(url, cmd:ServiceMessage) 
    {
        if(!cmd.callid)
            console.log("Sending message without callid: "+cmd);

        let data = this.objectToJson(cmd);
        //console.log(data);
        
        //let size = sizeOf(cmd);
        let size = data.length;
        let limit = 7000; // 8192

        // If message is larger than limit slice the message via partial messages
        if(size>limit)
        {
            let cnt = Math.ceil(size/limit);

            for(let i=0; i<cnt; i++)
            {
                let part = data.substring(i*limit, (i+1)*limit);

                let pcmd = new PartialMessage(cmd.callid, part, i, cnt);

                let pdata = JSON.stringify(pcmd);
                //console.log("sending part, size: "+pdata.length);
                this.sendData(url, pdata);
            }
        }
        else
        {
            this.sendData(url, data);
        }
    }
    
    /**
     *  Convert an object to json.
     *  Similar to JSON.stringify but can handle
     *  binary objects as base 64 strings.
     *  @param object The object.
     *  @return The json string.
     */
    public objectToJson(object: any): string
    {
        let replacer = (key, value) =>
        {
            if(value instanceof ArrayBuffer)
            {
                //let ret = window.btoa(value);
                let ret = btoa(String.fromCharCode.apply(null, new Uint8Array(value)));
                return ret;
            }
            else
            {
                return value;
            }
            //return value instanceof ArrayBuffer? window.btoa(value): value;
        };

        return JSON.stringify(object, replacer);
    }
    
    /**
     *  Send a result.
     */
    sendResult(url, result, finished, callid)
    {
        var cmd =
        {
            __classname: "com.actoron.webservice.messages.ResultMessage",
            callid: callid,
            result: result,
            finished: finished
        };

        this.sendRawMessage(url, cmd);
    }

    /**
     *  Send an exception.
     */
    sendException(url, err, finished, callid)
    {
        var exception =
        {
            __classname: "java.lang.RuntimeException",
            message: ""+err
        }

        var cmd =
        {
            __classname: "com.actoron.webservice.messages.ResultMessage",
            callid: callid,
            exception: exception,
            finished: finished
        };

        this.sendRawMessage(url, cmd);
    }

    /**
     *  Called when a message arrives.
     */
    onMessage(message: MessageEvent, url: string) 
    {
        if(message.type == "message")
        {
            let msg = JsonParser.parse(message.data, url);
            let outCall:WebsocketCall = this.outcalls[msg.callid];

//		    console.log("outcalls: "+outcalls);

            if(outCall!=null)
            {
                if(SUtil.isTrue(msg.finished))
                {
                    delete this.outcalls[msg.callid];
//					console.log("outCall deleted: "+msg.callid);
                    outCall.finished = true;
                }

                if(outCall.type=="search")
                {
                    if(msg.result!=null)
                    {
                        if(msg.result.hasOwnProperty("__array"))
                            msg.result = msg.result.__array;
                        if(msg.result.hasOwnProperty("__collection"))
                            msg.result[1] = msg.result[1].__collection;
                    }

                    let serproxy;

                    if(msg.exception==null && msg.result!=null)
                    {
                        serproxy = msg.result;//createServiceProxy(msg.result[0], msg.result[1]);
                    }

                    outCall.resume(serproxy, msg.exception);
                }
                else if(outCall.type=="invoke")
                {
                    outCall.resume(msg.result, msg.exception);
                }
                else if(outCall.type=="provide")
                {
                    if(msg.exception!=null)
                    {
                        outCall.reject(msg.exception);
                    }
                    else
                    {
                        // Save the service functionality in the inca
                        this.providedServices[SUtil.getServiceIdAsString(msg.result)] = outCall.cb;
                        
                        outCall.resolve(msg.result);
                    }
                }
                else if(outCall.type=="unprovide")
                {
                    if(msg.exception!=null)
                    {
                        outCall.reject(msg.exception);
                    }
                    else
                    {
                    	// removeProperty?!
                        this.providedServices[SUtil.getServiceIdAsString(msg.result)] = null;
                        outCall.resolve(msg.result);
                    }
                }
            }
            else // incoming call
            {
                if(msg.__classname==="com.actoron.webservice.messages.ServiceInvocationMessage")
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

    }

    /**
     *  Create a random string.
     *  @param length The length of the string.
     *  @returns The random string.
     */
    randomString(length)
    {
        if(length<1)
            length = 10;
        return Math.round((Math.pow(36, length + 1) - Math.random() * Math.pow(36, length))).toString(36).slice(1);
    };

}