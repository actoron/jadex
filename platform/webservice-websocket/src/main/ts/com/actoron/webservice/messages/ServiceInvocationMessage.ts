import {ServiceMessage} from "./BaseMessage";

export class ServiceInvocationMessage extends ServiceMessage 
{
    constructor(public serviceId:string, public methodName:string, public parameterValues) 
    {
        super("com.actoron.webservice.messages.ServiceInvocationMessage");
    }
}
