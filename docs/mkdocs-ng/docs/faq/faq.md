# Frequently Asked Questions

### What does "retrydelay" flag mean?
Without retrydelay goal processing works as follows:  
goal -&gt; plan 1 -&gt; plan 2 -&gt; plan 3 -&gt; ...  
until the goal is failed or succeeded. The retrydelay just specifies a delay in milliseconds before trying the next plan, when the previous plan has finished, i.e.:  
goal -&gt; plan 1 -&gt; wait -&gt; plan 2 -&gt; wait -&gt; plan 3 -&gt; ...  
until goal fails or succeeds.

This is e.g. useful, when already tried plans are not excluded from the applicable plan set, leading to the same plan being tried over and over again.

### How can the environment of a Jadex MAS be programmed?
If distribution is needed we used the approach of a separate environment. The environment holds the global state permits tasks, actions and processes being executed. See the environment user guide for details.

### I have change the .java file, e.g. a plan. Why are my changes not reflected in the running Jadex system?
Jadex relies on the Java class loading mechanism. This means that normally Java classes are loaded only once into the VM. You need to restart the Platform for taking the changes effect. 

### In my agents there is always one plan for each goal. Why do I need goals anyway?
You don't need to use goals for every problem. But, in our opinion using goals in many cases simplifies the development and allows for easier extensions of an application. The difference between plans and goals is fundamental. Goals represent the "what" is desired while plans are characterized by the "how" could things be accomplished. So if you e.g. use a goal "achieve happy programmers" you did not specify how you want to pursue this goals. One option might be the increase of salary, another might be to buy new TFT monitors. Genereally, the usefulness of goals depends on the concrete problem and its complexity at hand.

If you find that you don't need goals for your application, consider using the more light-weight Jadex micro agents.

### How can the agent become aware of or react to its own death?
Jadex supports not only an initialization phase but also a termination phase. Whenever an agent is terminated its execution will not be immediately stopped. Instead the agent changes its state to "terminating", aborts all running goals and plans and activates elements declared in the end state. For details please have a look at Chapter 13, Configurations.

If you want to be notified when an agent dies you can use an agent listener.

### How can I parametrize an agent and set parameter values before starting?
All Jadex components support the use of arguments. For BDI agents , beliefs can be marked as arguments. The JCC gui automatically creates input fields for these arguments. Programmatically, the arguments can e.g. be directly referenced via their associated beliefs.

### Is there some preferred persistence mechanism for beliefs?
In the current version Jadex does not provide a ready-to-use persistence mechanism for the beliefs of an agent. We have successfully used normal object-relational mapping frameworks such as Hibernate in combination with Jadex. Nonetheless, the task of persisting data cannot be fully automated and needs to be done in plans. This topic should be an issue of further research and improvement.

### Can capabilities be used for group communication, i.e. are they some kind of tuple space, where one agent puts in data and other can read it?
No, this seems to be a common misunderstanding of the concept. A capability is comparable to a module. Each agent that includes a capability get a separate instance of that module. For details have a look at [Capabilities](../guides/bdi/05 Capabilities/).
