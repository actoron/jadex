import {ServiceMessage} from "./BaseMessage";

export class ServiceUnprovideMessage extends ServiceMessage 
{
    constructor(public serviceId:string) 
    {
        super("com.actoron.webservice.messages.ServiceUnprovideMessage");
    }
}