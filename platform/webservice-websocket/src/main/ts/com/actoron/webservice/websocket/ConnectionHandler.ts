import {JsonParser} from "../JsonParser";
import {SUtil} from "../SUtil";
import {WebsocketCall} from "./WebsocketCall";

export abstract class ConnectionHandler 
{
    /** The websocket connections. */
    connections:Promise<WebSocket>[] = [];

    // private static INSTANCE:ConnectionHandler = new ConnectionHandler();
    //
    // public static getInstance():ConnectionHandler {
    //     return ConnectionHandler.INSTANCE;
    // }

    private baseurl:string;

    constructor() 
    {
        let scripts: NodeListOf<HTMLScriptElement>|NodeListOf<Element> = document.getElementsByTagName('script');
        let script: Node = scripts[scripts.length - 1];
        let prot: string = SUtil.isSecure()? "wss": "ws";
        
        if(script["src"]) 
        {
            this.baseurl = script["src"];
            this.baseurl = prot+this.baseurl.substring(this.baseurl.indexOf("://"));
        } 
        else if(script.hasAttributes())
        {
            //this.baseurl = "ws://" + window.location.hostname + ":" + window.location.port + "/wswebapi";
            this.baseurl = prot+"://" + window.location.hostname + ":" + window.location.port + script.attributes.getNamedItem("src").value;
        } 
        else 
        {
            // fail?
            throw new Error("Could not find websocket url");
        }

        this.baseurl = this.baseurl.substring(0, this.baseurl.lastIndexOf("jadex.js")-1);

        this.connections[""] = this.addConnection(this.baseurl);
        //this.connections[undefined] = this.connections[null];
    }

    /**
     *  Internal function to get a web socket for a url.
     */
    getConnection(url:string)
    {
        if(url == null)
            url = "";
        
        let ret = this.connections[url];
        if(ret!=null)
        {
            return ret;
        }
        else
        {
            return this.addConnection(url);
        }
    };

    /**
     *  Add a new server connection.
     *  @param url The url.
     */
    addConnection(url):Promise<WebSocket>
    {
        this.connections[url] = new Promise<WebSocket>((resolve, reject) =>
        {
            try
            {
                let ws = new WebSocket(url);

                ws.onopen = () =>
                {
                    resolve(ws);
                };

                ws.onmessage = message =>
                {
                    this.onMessage(message, url);

                };
            }
            catch(e)
            {
                reject(e);
            }
        });

        return this.connections[url];
    };

    /**
     *  Send a message to the server and create a callid for the answer message.
     */
    sendData(url, data)
    {
        this.getConnection(url).then(ws =>
        {
            ws.send(data);
        });
    }


    /**
     *  Send a message to the server in an ongoing conversation.
     */
    sendConversationMessage(url, cmd)
    {
        this.getConnection(url).then((ws) =>
        {
            ws.send(JSON.stringify(cmd));
        });
    };

    abstract onMessage(message: MessageEvent, url:string);
}