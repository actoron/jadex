package jadex.webservice.examples.rs.hello;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.JAXBContext;

import com.sun.jersey.api.json.JSONConfiguration;
import com.sun.jersey.api.json.JSONJAXBContext;

@Provider
public final class JAXBProvider implements ContextResolver<JAXBContext> {
    
    private final JAXBContext context;
    
    private final Set<Class> types;
    
    private final Class[] cTypes = {A.class};
    
    public JAXBProvider() throws Exception 
    {
        this.types = new HashSet(Arrays.asList(cTypes));
        this.context = new JSONJAXBContext(JSONConfiguration.mapped().build(), cTypes);
    }
    
    public JAXBContext getContext(Class<?> objectType) 
    {
        return (types.contains(objectType)) ? context : null;
    }
}
