import {ServiceMessage} from "./BaseMessage";

export class PartialMessage extends ServiceMessage 
{
    constructor(public callid:string, public data, public number:number, public count:number) 
    {
        super("com.actoron.webservice.messages.PartialMessage");
    }
}
