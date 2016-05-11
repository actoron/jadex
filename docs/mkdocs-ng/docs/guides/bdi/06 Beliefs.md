Beliefs represent the agent's knowledge about its enviroment and itself. In Jadex the beliefs can be any Java objects. They are stored in a belief base and can be referenced in expressions, as well as accessed and modified from plans using the beliefbase interface.

# Defining Beliefs in the ADF

The beliefbase is the container for the facts known by the agent. Beliefs are usually defined in the ADF and accessed and modified from plans. To define a single valued belief or a multi-valued belief set in the ADF the developer has to use the corresponding &lt;belief&gt; or &lt;beliefset&gt; tags and has to provide a name and a class. The name is used to refer to the fact(s) contained in the belief. The class specifies the (super) class of the fact objects that can be stored in the belief. The default fact(s) of a belief may be supplied in enclosed &lt;fact&gt; tags. Alternatively, for belief sets a collection of initial facts can be directly specified using a &lt;facts&gt; tag. This is useful, when you do not know the number of initial facts in advanvce, e.g., when invoking a static method or retrieving values from a database (see Figure 1). References to beliefs and belief sets from inner capabilities can be defined using the &lt;beliefref&gt; and &lt;beliefsetref&gt; tags (cf. Figure 2 in the Capabilities chapter).



![](jadexbeliefsadf.png)
*Figure 1: The Jadex beliefs XML schema part*
 


```xml
...
  <beliefs>
    <belief name="my_location" class="Location">
      <fact>new Location("Hamburg")</fact>
    </belief>
    <beliefset name="my_friends" class="String">
      <fact>"Alex"</fact>
      <fact>"Blandi"</fact>
      <fact>"Charlie"</fact>
    </beliefset>
    <beliefset name="my_opponents" class="String">
      <facts>Database.getOpponents()</facts>
    </beliefset>
    ...
  </beliefs>
  ...
</agent>

```


*Example belief definition*

# Accessing Beliefs from within Plans

From within a plan, the programmer has access to the beliefbase (interface IBeliefbase) using the getBeliefbase() method. The beliefbase provides getBelief() / getBeliefSet() methods to get the current beliefs and belief sets by name, as well as methods to create new beliefs and belief sets or remove old ones. The content of a belief (interface IBelief) can be accessed by the getFact() method. A belief set (interface IBeliefSet) is accessed through the getFacts() method and will return an appropriately typed array of facts. To check if a fact is contained in a belief set the containsFact() method can be used.



The contents of a single fact belief are modified using the setFact() method. Setting a fact on a belief will result in overwriting the previous value, if any. For deleting the fact of a single fact belief, you can set the belief value to null. Belief sets are manipulated using the addFact(fact) / removeFact(fact) methods. When removing facts that do not exist from the belief set, the belief set remains unchanged and a warning message will be produced. For the remove operation, the beliefbase relies on the implementation of the equals() method of the fact objects. Additionally, *setFact(fact)* can be used to replace an existing fact value.




```java
public void body()
{
  ...
  IBelief hungry = getBeliefbase().getBelief("hungry");
  hungry.setFact(new Boolean(true));
  ...
  Food[] food = (Food[])getBeliefbase().getBeliefSet("food").getFacts();
  ...
}

```


*A simple example of using a boolean belief*
 

# Dynamically Evaluated Beliefs

In the ADF the initial facts of beliefs are specified using expressions. Normally, the fact expressions are evaluated only once: When the agent is born. The evaluation behavior of the fact expression can be adjusted using the evaluationmode attribute. Possible values are 'static' (default), 'pull' and 'push'. Additionally, an updaterate may be specified as attribute of the belief that will cause the fact to be continuously evaluated and updated in the given time interval (in milliseconds).



In the example, the first belief "time" is evaluated on access (pull), and will therefore always contain the exact current time as returned by the Java function System.currentTimeMillis(). The second belief "timer" is not only evaluated on access (i.e., when accessed), but also every 10 seconds (10000 milliseconds). The advantage of using an updaterate for continuously evaluating a belief is that the fact value changes even when it is not accessed, and therefore may trigger conditions referring to that belief. For example, using the "timer" belief you could define a condition to invoke a plan that has to be executed in continuous intervals. Both options also provide an easy and effective way for making an agent aware of external input (e.g., sensory data available through a Java API).




```xml

<beliefs>
  <!-- A belief holding the current time (re-evaluated on every access). -->
  <belief name="time" class="long" evaluationmode="pull">
    <fact>System.currentTimeMillis()</fact>
  </belief>

  <!-- A belief continuously updated every 10 seconds. -->
  <belief name="timer" class="long" updaterate="10000">
    <fact>System.currentTimeMillis()</fact>
  </belief>
</beliefs>

```


*Examples of dynamically evaluated beliefs*

When setting the evaluation mode to 'push' the fact expression will be monitored for changes and a belief change event will be automatically generated as described in the next section.

# Propagation of Belief Changes

To monitor conditions, an agent observes the beliefs and automatically reacts to changes of these beliefs, as necessary. Jadex is aware of manipulation operations that are executed directly on beliefs, e.g., by setting the fact of a belief, and of changes due to belief dependencies (i.e., a dynamically evaluated fact expression referencing another belief).

On the other hand, when you retrieve a complex fact object from a belief or belief set and perform operations on it subsequently, the system cannot detect the changes made. To enable the system detecting these changes the standard Java beans event notification mechanism can be used. This means that the bean has to implement the add/removePropertyChangeListener() methods and has to fire property change events, whenever an important change has occurred. The belief will automatically add and remove itself as a property change listener on its facts. An example how to implement this functionality inside a Java bean is shown below.
   


```java

import java.beans.PropertyChangeSupport;
import java.beans.PropertyChangeListener;

public class Location 
{
  private int x, y;
  private PropertyChangeSupport pcs;

  public Location(int x, int y) 
  {
    this.x = x;
    this.y = y;
    this.pcs = new PropertyChangeSupport(this);
  }

  public int getX() 
  {
    return this.x;
  }
    
  public void setX(int x) 
  {
    int old = this.x;
    this.x = x;
    this.pcs.firePropertyChange("X", old, this.x);
  }

  public int getY() 
  {
    return this.y;
  }

  public void setY(int y) 
  {
    int old = this.y;
    this.y = y;
    this.pcs.firePropertyChange("Y", old, this.y);
  }

  public void addPropertyChangeListener(PropertyChangeListener listener) 
  {
    pcs.addPropertyChangeListener(listener);
  }

  public void removePropertyChangeListener(PropertyChangeListener listener) 
  {
    pcs.removePropertyChangeListener(listener);
  }
}

```


*Example bean class with property change support*
