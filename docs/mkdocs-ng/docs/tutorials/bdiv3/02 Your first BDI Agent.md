# Your first BDI Agent

Before you can create your first BDI agent, please setup your environment as described in [IDE Setup](../../getting-started/getting-started/#ide-setup) in order to have access to the required Jadex libraries.

# Exercise A1 - Create first simple Jadex agent

Open a source code editor or an IDE of your choice and create a new package called a1 and an agent class called *TranslationBDI.java*.
The agent is a normal Java class that uses the ```@Agent``` annotation to state that it is an agent.  
Also please note that it is currently **required that the Java file ends with "BDI"**.
Otherwise it will not be recognized as BDI agent.  
Optionally, the ```@Description``` annotation can be used to specify a documentation text that is displayed when the agent is loaded within the [JCC](../../tools/01 Introduction/).
The resulting code should look like this:

```java
package a1;

import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Description;

@Agent
@Description("The translation agent A1. <br> Empty agent that can be loaded and started.")
public class TranslationBDI
{
}
```


** Start your first Jadex agent **

The easiest way to start your agent is to create a class *Main* with a main method that starts up a Jadex Platform together with your agent:
```java
public class Main {
    public static void main(String[] args) {
        PlatformConfiguration   config  = PlatformConfiguration.getDefaultNoGui();
        
        config.addComponent("a1.TranslationBDI.class");
        Starter.createPlatform(config).get();
    }
}
```
Note: You ** must not ** reference the TranslationBDI class directly for technical reasons. Just use it's qualified class name as in the example above.

As you Agent does not do anything, there will be no useful output.

More about starting your components/applications can be found in the [Getting Started](../../getting-started/getting-started/#starting-your-applications) section.
