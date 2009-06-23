package jadex.bdi.interpreter.bpmn.model.state.task;

import jadex.bdi.interpreter.bpmn.model.ITaskProperty;

import java.util.HashMap;
import java.util.Map;

public class BasicTaskProperty implements ITaskProperty {
    
	/** The name for this property */
    private String name;
    
    // HACK! at least store the values provided by the parser (read reason below)
    protected Map values;

    // ---- constructors ----
    
    public BasicTaskProperty(String name) {
    	this.name = name;
    	this.values = new HashMap();
    }
    
    // ---- getter / setter ----
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    // TODO: getter / setter for all elements

    // ---- methods ----
    
    public Object get(Object key) 
    {
        return values.get(key);
    }

    public void set(Object key, Object o) 
    {
    	values.put(key, o);
    }
    
    // TODO: remove indexed access !
    // TODO: maybe add (copy) some mapping functions?
    
    // Explanation:
    // A Property was stored in a Table, each property in one line (<tr>) that contains the values from
    // left to right (<td>). The meaning of the value (index of td) was dependent on the property class impl.
    // This is somewhat STUPID, we have to re-implement this with <properties> list, <property> elements and
    // sub elemets (<text>, <tooltip>, ...). For the time being at least store the values in the stupid way,
    // accessible via the index of the table-column.
    
    public Object get(int i) 
    {
        return values.get(new Integer(i));
    }

    public void set(int i, Object o) 
    {
    	values.put(new Integer(i), o);
    }
}
