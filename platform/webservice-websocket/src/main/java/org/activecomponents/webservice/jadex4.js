	var SUtil =
	{
		wrappedConversionTypes:
		{
	        java_lang_Integer: "number",
	        java_lang_Byte: "number",
	        java_lang_Short: "number",
	        java_lang_Long: "number",
	        java_lang_Float: "number",
	        java_lang_Double: "number",
	        java_lang_Character: "string",
	        java_lang_Boolean: "boolean"
	    },
    
    /**
     *  Test if an object is a basic type.
     *  @param obj The object.
     *  @return True, if is a basic type.
     */
    function isBasicType(obj) 
    {
        return typeof obj !== 'object' || !obj;
    }

    /**
     *  Test if an object is a java wrapped type.
     *  @param obj The object.
     *  @return False, if is not a wrapped primitive type, else returns the corresponding JS type.
     */
    function isWrappedType(obj)
    {
        if("__classname" in obj) 
        {
            var searching = obj.__classname.replace(/\./g, '_')
            return wrappedConversionTypes[searching];
        } 
        else 
        {
            return false;
        }
    }

    /**
     *  Check of an obj is an enum.
     */
    function isEnum(obj)
    {
        return ("enum" in obj)
    }

    /**
     *  Test if an object is an array.
     *  @param obj The object.
     *  @return True, if is an array.
     */
    function isArray(obj) 
    {
        return Object.prototype.toString.call(obj) == '[object Array]';
    }

    /**
     *  Compute the approx. size of an object.
     *  @param obj The object.
     */
    function sizeOf(object) 
    {
        let objects = [object];
        let size = 0;

        for(let i = 0; i < objects.length; i++) 
        {
            switch(typeof objects[i]) 
            {
                case 'boolean':
                    size += 4;
                    break;
                case 'number':
                    size += 8;
                    break;
                case 'string':
                    size += 2 * objects[i].length;
                    break;
                case 'object':

                    if(Object.prototype.toString.call(objects[i]) != '[object Array]') 
                    {
                        for (let key in objects[i])
                            size += 2 * key.length;
                    }

                    let processed = false;
                    let key;
                    for(key in objects[i]) 
                    {
                        for(let search: number = 0; search < objects.length; search++) 
                        {
                            if(objects[search] === objects[i][key]) 
                            {
                                processed = true;
                                break;
                            }
                        }
                    }

                    if(!processed)
                        objects.push(objects[i][key]);
            }
        }
        return size;
    }

    /**
     *  Check if object is true by inspecting if it contains a true property.
     */
    function isTrue(obj) 
    {
        return obj == true || (obj != null && obj.hasOwnProperty("value") && obj.value == true);
    }


    /**
     *  Assert that throws an error if not holds.
     */
    function assert(condition, message) 
    {
        if(!condition) 
        {
            message = message || "Assertion failed";
            if(typeof Error !== "undefined") 
            {
                throw new Error(message);
            }
            throw message; // Fallback
        }
    }

    /**
     *  Get the service id as string.
     *  (otherwise it cannot be used as key in a map because
     *  no equals exists).
     */
    function getServiceIdAsString(sid)
    {
        return sid.serviceName+"@"+sid.providerId;
    }

    /**
     *  Add a console out error handler to the promise.
     */
    function addErrHandler(p)
    {
        p.oldcatch = p.catch;
        p.hasErrorhandler = false;
        p.catch = function(eh)
        {
            p.hasErrorHandler = true;
            return p.oldcatch(eh);
        };
        p.oldcatch(function(err)
        {
            if(!p.hasErrorHandler)
                console.log("Error occurred: "+err);
        });
        p.oldthen = p.then;
        p.then = function(t, e)
        {
            if(e)
                p.hasErrorHandler = true;
            return p.oldthen(t, e);
        };
        return p;
    }
    
    /**
     *  Test if a number is a float.
     *  @param n The number to test.
     *  @return True, if is float.
     */
    function isFloat(n)
    {
        return n === +n && n !== (n | 0);
    }

    /**
     *  Test if a number is an integer.
     *  @param n The number to test.
     *  @return True, if is integer.
     */
    function isInteger(n):boolean 
    {
        return n === +n && n === (n | 0);
    }
    
    /**
     *  Check if an object is contained in an array.
     *  Uses equal function to check equality of objects.
     *  If not provided uses reference test.
     *  @param object The object to check.
     *  @param objects The array.
     *  @param equals The equals method.
     *  @return True, if is contained.
     */
    function containsObject(object, objects, equals)
	{
		var ret = false;
		
		for(var i=0; i<objects.length && !ret; i++)
		{
			ret = equals? equals(object, objects[i]): object===objects[i];
		}
		
		return ret;
	}
	
	/**
     *  Get the index of an object in an array. -1 for not contained.
     *  @param object The object to check.
     *  @param objects The array.
     *  @param equals The equals method.
     *  @return The index or -1.
     */
	function indexOfObject(object, objects, equals)
	{
		var ret = -1;
		
		for(var i=0; i<objects.length; i++)
		{
			if(equals? equals(object, objects[i]): object===objects[i])
			{
				ret = i;
				break;
			}
		}
		
		return ret;
	}
	
	/**
     *  Remove an object from an array.
     *  @param object The object to remove.
     *  @param objects The array.
     *  @param equals The equals method.
     *  @return True, if was removed.
     */
	function removeObject(object, objects, equals)
	{
		var ret = SUtil.indexOfObject(object, objects, equals);

		if(ret!=-1)		
			objects.splice(ret, 1);
		
		return ret==-1? false: true;
	}
    
    /**
     *  Check if the call was https.
     *  @return True if https.
     */
    function isSecure()
    {
        return window.location.protocol == 'https:';
    }
