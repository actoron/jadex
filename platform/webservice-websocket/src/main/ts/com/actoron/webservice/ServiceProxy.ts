import {JadexPromise} from "./JadexPromise";
import {JadexConnectionHandler} from "./websocket/JadexConnectionHandler";
import {ServiceInvocationMessage} from "./messages/ServiceInvocationMessage";

export class ServiceProxy 
{
    /**
     *  Create a service proxy for a Jadex service.
     */
    constructor(private serviceId:any, methodNames, private url:string) 
    { 
        // Generic invoke method called on each service invocation
        for(let i = 0; i < methodNames.length; i++) 
        {
            this[methodNames[i]] = this.createMethod(methodNames[i]);
        }
    }

    /**
     *  Generic invoke method that sends a method call to the server side.
     */
    public invoke(name:string, params, callback):PromiseLike<any> 
    {
        let ret = new JadexPromise(this.url);
        
        let conm:JadexConnectionHandler = JadexConnectionHandler.getInstance();
        
        // Convert parameters seperately, one by one
        let cparams: string[] = [];
        for(let i=0; i<params.length; i++)
        {
            cparams.push(conm.objectToJson(params[i]));
        }
        
        let cmd = new ServiceInvocationMessage(this.serviceId, name, cparams);

        // console.log(cmd);

        // wrap callback to allow JadexPromise.intermediateThen
        let wrapCb = intermediateResult => {
            // console.log("calling intermediate result with: " + intermediateResult);
            ret.resolveIntermediate(intermediateResult);
            if (callback) {
                callback(intermediateResult);
            }
        };
        ret.callid = conm.sendMessage(this.url, cmd, "invoke", res => ret.resolve(res), ex => ret.reject(ex), wrapCb);

        return ret;
    }

    /**
     *  Create method function (needed to preserve the name).
     *
     *  Creates an argument array and invokes generic invoke method.
     *
     *  TODO: callback function hack!
     */
    createMethod(name):Function 
    {
        let outer = this;
        return function () 
        {
            let params = [];
            let callback;

            for(let j = 0; j < arguments.length; j++) 
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
}