import {JadexConnectionHandler} from "./websocket/JadexConnectionHandler";
export class JadexPromise<T> implements PromiseLike<T> {

    public callid;

    private promise:Promise<T>;

    private resolveFunc:Function;
    private rejectFunc:Function;

    private intermediateResolveCallbacks = [];
    // private intermediateRejectCallbacks = [];

    then<TResult>(onfulfilled?: (value: T)=>(PromiseLike<TResult>|TResult), onrejected?: (reason: any)=>(PromiseLike<TResult>|TResult)): PromiseLike<TResult> {
        return this.promise.then(onfulfilled, onrejected);
    }

    resolve<T>(value?: T | PromiseLike<T>): Promise<T> {
        // return this.promise.resolve(value);
        return this.resolveFunc(value);
    }

    reject(error: any): Promise<any> {
        // return this.promise.reject(error);
        return this.rejectFunc(error);
    }

    resolveIntermediate<T>(value?: T | PromiseLike<T>){
        for (let cb of this.intermediateResolveCallbacks)
        {
            cb(value);
        }
    }

    thenIntermediate<TResult>(onfulfilled?: (value: T)=>(PromiseLike<TResult>|TResult), onrejected?: (reason: any)=>(PromiseLike<TResult>|TResult)){
        this.intermediateResolveCallbacks.push(onfulfilled);
        // this.intermediateRejectCallbacks.push(onrejected);
    }

    /**
     * Instantiate a promise but do not execute anything now.
     * Resolving is handled from outside via resolve().
     */
    constructor(private url:string) {
        this.promise = new Promise<T>((resolve, reject) => {
            this.resolveFunc = resolve;
            this.rejectFunc = reject;
        });
    }


    public terminate()
    {
        let cmd =
        {
            __classname: "com.actoron.webservice.messages.ServiceTerminateInvocationMessage",
            callid: this.callid
        };

        JadexConnectionHandler.getInstance().sendConversationMessage(this.url, cmd);
    };

    public pull()
    {
        let cmd =
        {
            __classname: "com.actoron.webservice.messages.PullResultMessage",
            callid: this.callid
        };

        JadexConnectionHandler.getInstance().sendConversationMessage(this.url, cmd);
    };


}