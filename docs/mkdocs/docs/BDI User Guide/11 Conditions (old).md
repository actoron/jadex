Chapter 11. Conditions
===================================

In essence, a condition is a monitored boolean expression, what means that whenever some of the referenced entities (e.g., beliefs) change the expression of the condition is evaluated. Associated with a condition is an action, that gets executed whenever the condition is triggered. Context-specific conditions as defined in the ADF have special associated actions (e.g., for activating goals).

The trigger of a predefined condition depends on the context, for example the maintain condition of a maintain goal is triggered when the expression value changes to false, because the goal should be processed whenever the maintain condition is violated. 

When programming plans, it is also possible to explicitly wait for certain conditions using the *waitForCondition(ICondition cond)* method. Conditions are obtained in a similar fashion to expressions, either by instantiating a predefined condition from the ADF, or by creating a new condition from an expression string. When waiting for a condition, the plan will be blocked until the condition triggers, which by default means that its value changes to true. The condition is monitored automatically by the agent, by considering all internal state changes that may affect the condition value, e.g., when some other plan changes a belief.

The following example uses the "timer" belief from Section 6.3 to execute some action when the alarmtime has reached (belief not shown here).


```xml

<agent ...>
    ...
    <expressions>
        <condition name="alarmtime&#95;reached">
            $beliefbase.timer >= $beliefbase.alarmtime
        </condition>
        ...
    </expressions>
    ...
</agent>

```



```java

public void body
{
    ICondition condition = getCondition("alarmtime&#95;reached");
    ...
    IEvent event = waitForCondition(condition); 
    ...
}

```

