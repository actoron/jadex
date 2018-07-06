import {ServiceMessage} from "./BaseMessage";

export class ServiceProvideMessage extends ServiceMessage 
{
    constructor(public type: string, public scope: string, public tags: string[]) 
    {
        super("com.actoron.webservice.messages.ServiceProvideMessage");
    }
}