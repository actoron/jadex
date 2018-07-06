import {JadexConnectionHandler} from "./websocket/JadexConnectionHandler";
import {ServiceSearchMessage} from "./messages/ServiceSearchMessage";
import {Scopes} from "./Scopes";
import {ServiceProvideMessage} from "./messages/ServiceProvideMessage";
import {ServiceUnprovideMessage} from "./messages/ServiceUnprovideMessage";
import {JsonParser} from "./JsonParser";
import {SUtil} from "./SUtil";

/**
 *  Main class with methods to 
 *  - getService(s) and
 *  - provideService 
 */
export class Jadex 
{
    /**
     *  Jadex Javascript API.
     */
    constructor() 
    {
    }

    /**
     *  Get a service from a publication point.
     *  @param type The service type.
     *  @param scope The scope.
     *  @param url The url of the websocket.
     */
    public getService(type:string, scope?:string, url?):Promise<any> 
    {
        let prom = SUtil.addErrHandler(new Promise((resolve, reject) => 
        {
            let cmd = new ServiceSearchMessage(type, false, scope != null ? scope : Scopes.SCOPE_PLATFORM);
            JadexConnectionHandler.getInstance().sendMessage(url, cmd, "search", resolve, reject, null);
        }));
        return prom;
    };

    /**
     *  Get services from a publication point.
     *  @param type The service type.
     *  @param callback The callback function for the intermediate results.
     *  @param scope The search scope.
     *  @param url The url of the websocket.
     */
    public getServices(type:string, callback:Function, scope?:string, url?) 
    {
        SUtil.assert(callback instanceof Function && callback != null);
        let prom = SUtil.addErrHandler(new Promise((resolve, reject) => 
        {
            let cmd = new ServiceSearchMessage(type, true, scope != null ? scope : Scopes.SCOPE_PLATFORM);
            JadexConnectionHandler.getInstance().sendMessage(url, cmd, "search", resolve, reject, callback);
        }));
        return prom;
    };


    /**
     *  Provide a new (client) service.
     *  @param type The service type.
     *  @param scope The provision scope.
     *  @param url The url of the websocket.
     */
    public provideService(type:string, scope:string, tags:string[] | string, callback, url?):Promise<any>
    {
        return SUtil.addErrHandler(new Promise(function(resolve, reject)
        {
            let cmd = new ServiceProvideMessage(type, scope!=null? scope: "global", typeof tags === "string"? [tags]: tags);
            JadexConnectionHandler.getInstance().sendMessage(url, cmd, "provide", resolve, reject, callback);
        }));
    }
    
    /**
     *  Unprovide a (client) service.
     *  @param type The service type.
     *  @param scope The provision scope.
     *  @param url The url of the websocket.
     */
    public unprovideService(sid:any, url?):Promise<any>
    {
        return SUtil.addErrHandler(new Promise(function(resolve, reject)
        {
            let cmd = new ServiceUnprovideMessage(sid);
            JadexConnectionHandler.getInstance().sendMessage(url, cmd, "unprovide", resolve, reject, null);
        }));
    }

    /**
     *  Register a class for json (de)serialization.
     */
    public registerClass(clazz: any) 
    {
        JsonParser.registerClass(clazz);
    }
}