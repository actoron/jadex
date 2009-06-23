package jadex.bdi.interpreter.bpmn.model;

public interface ITaskProperty {

    public String getName();
    
    public void setName(String name);
    
    // TODO: remove ...
    public void set(int i, Object o);
    public Object get(int i);
    
}
