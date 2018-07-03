import {SUtil} from "../SUtil";

export class WebsocketCall 
{
    public finished:boolean = false;

    constructor(public type, public resolve: (result?) => any, public reject: (error?) => any, public cb:Function) 
    {
    }

    /**
     *  Resume the listeners of promise.
     */
    resume(result: any, exception: any) 
    {
        if (this.cb != null && (exception === null || exception === undefined) && !SUtil.isTrue(this.finished)) 
        {
            this.cb(result);
        }
        else if (SUtil.isTrue(this.finished)) 
        {
            exception == null ? this.resolve(result) : this.reject(exception);
        }
    }
}