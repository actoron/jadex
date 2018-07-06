import {SUtil} from "./SUtil";
import {ServiceProxy} from "./ServiceProxy";

/**
 *  Class that can parse json with additional features.
 *  - handles Jadex references
 */
export class JsonParser 
{
    /** The registered classes. */
    private static registeredClasses = {};
    
    static init()
    {
        JsonParser.registerClass2("java.util.Date", {create: function(obj)
        {
            return new Date(obj.value);
        }});
    }
    
    /**
     *  Register a class at the parser.
     */
    static registerClass(clazz: any) 
    {
        if("__classname" in clazz) 
        {
            JsonParser.registeredClasses[clazz.__classname] = clazz;
        } 
        else 
        {
            let instance = new clazz();
            if("__classname" in instance) 
            {
                 JsonParser.registeredClasses[instance.__classname] = clazz;
            } 
            else 
            {
                throw new Error("Cannot register class without __classname static field or member: " + clazz.name);
            }
        }
    }
    
    /**
     *  Register a class at the parser.
     */
    static registerClass2(classname: string, create: any)
    {
         JsonParser.registeredClasses[classname] = create;
    } 

    /**
     *  JSOn.parse extension to handle Jadex reference mechanism.
     *  @param str The string of the json object to parse.
     *  @return The parsed object.
     */
    public static parse(str:string, url:string) 
    {
        let idmarker = "__id";
        let refmarker = "__ref";
 //		let arraymarker = "__array";
 //		let collectionmarker = "__collection";
        let replacemarker = ["__array", "__collection"];

        let os = {}; // the objects per id
        let refs = []; // the unresolved references
        let obj;
        try 
        {
             obj = JSON.parse(str);
        } 
        catch(e) 
        {
            console.error("Could not parse string: " + str);
            throw e;
        }

        let recurse = (obj, prop, parent) => 
        {
    //	    console.log(obj+" "+prop+" "+parent);

            if(!SUtil.isBasicType(obj)) 
            {
                // test if it is just a placeholder object that must be changed
    //			if(prop!=null)
    //			{
                for(let i = 0; i < replacemarker.length; i++) 
                {
                    if(replacemarker[i] in obj) 
                    {
                        obj = obj[replacemarker[i]];
                        break;
                    }
                }
    //		    }

                // instantiate classes
                if("__classname" in obj) 
                {
                    var className = obj["__classname"];
                    if(className == "jadex.bridge.service.IService") 
                    {
                        obj = new ServiceProxy(obj.serviceIdentifier, recurse(obj.methodNames, "methodNames", obj), url)
                    } 
                    else if(className in JsonParser.registeredClasses)
                    {
                        var func = JsonParser.registeredClasses[className];
                        
                        if(func.create)
                        {
                            obj = func.create(obj);
                        }
                        else
                        {
                            // iterate members:
                            var instance = new func();
                            for(let prop in obj) 
                            {
                                instance[prop] = recurse(obj[prop], prop, obj);
                            }
                            obj = instance;
                        }
                    }
                } 
                else 
                {
                    // recreate arrays
                    if(SUtil.isArray(obj)) 
                    {
                        for(let i = 0; i < obj.length; i++) 
                        {
                            if(!SUtil.isBasicType(obj[i])) 
                            {
                                if(refmarker in obj[i]) 
                                {
                                    obj[i] = recurse(obj[i], i, obj);
                                }
                                else 
                                {
                                    obj[i] = recurse(obj[i], prop, obj);
                                }
                            }
                        }
                    }
                }

                if(refmarker in obj) 
                {
                    let ref = obj[refmarker];
                    if(ref in os) 
                    {
                        obj = os[ref];
                    }
                    else 
                    {
                        refs.push([parent, prop, ref]); // lazy evaluation necessary
                    }
                }
                else 
                {
                    let id = null;
                    if(idmarker in obj) 
                    {
                        id = obj[idmarker];
                        delete obj[idmarker];
                    }
                    if("$values" in obj) // an array
                    {
                        obj = obj.$values.map(recurse);
                    }
                    else 
                    {
                        for (let prop in obj)  
                        {
                            obj[prop] = recurse(obj[prop], prop, obj);
                        }
                    }
                    if(id != null) 
                    {
                        os[id] = obj;
                    }
                }

                // unwrap boxed values for JS:
                var wrappedType = SUtil.isWrappedType(obj);
                if(wrappedType) 
                {
                    // console.log("found wrapped: " + isWrappedType(obj) + " for: " + obj.__classname + " with value: " + obj.value)
                    if(wrappedType == "boolean")
                    {
                        // this will not happen, because booleans are already native?
                        // obj = obj.value == "true"
                    }
                    else if(wrappedType == "string")
                    {
                        obj = obj.value
                    }
                    else
                    {
                        // everything else is a number in JS
                        obj = +obj.value
                    }
                }
                else if(SUtil.isEnum(obj))
                {
                    // convert enums to strings
                    obj = obj.value;
                }
            }
            return obj;
        };

        obj = recurse(obj, null, null);

        // resolve lazy references
        for(let i = 0; i < refs.length; i++) 
        {
            let ref = refs[i];
            ref[0][ref[1]] = os[ref[2]];
        }
        return obj;
    }
}
JsonParser.init(); 