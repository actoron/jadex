import {ServiceMessage} from "./BaseMessage";

export class ServiceSearchMessage  extends ServiceMessage 
{
    constructor (public type:string, public multiple:boolean, public scope:string) 
    {
        super("com.actoron.webservice.messages.ServiceSearchMessage");
    }
}