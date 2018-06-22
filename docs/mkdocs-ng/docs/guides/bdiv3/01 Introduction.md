${SorryOutdatedv3}

# Introduction

Jadex BDIV3 is an agent-oriented reasoning engine for writing rational agents in Java. Thereby, Jadex represents a conservative approach towards agent-orientation for several reasons. One main aspect is that no new programming language is introduced. Instead, Jadex agents can be programmed in the state-of-the art object-oriented integrated development environments (IDEs) such as [eclipse](http://www.eclipse.org/). The other important aspect concerns the middleware independence of Jadex. As Jadex BDIV3 is loosely coupled with its underlying middleware, Jadex can be used in very different scenarios on top of agent platforms as well as enterprise systems such as J2EE.

Similar to the paradigm shift towards object-orientation agents represent a new conceptual level of abstraction extending well-known and accepted object-oriented practices. Agent-oriented programs add the explicit concept of autonomous actors to the world of passive objects. In this respect agents represent active components with individual resoning capabilities. This means that agents can exhibit reactive behavior (responding to external events) as well as pro-active behavior (motivated by the agents own goals).

## History 
As the name indicates Jadex BDIV3 it is the third version of the Jadex BDI kernel. 
The V1 kernel version was based on XML and Java and introduced an goal-oriented reasoning mechanism embraces the full BDI reasoning cycle including the selection of goals to pursue (goal deliberation) and the realization phase in which different plans can be tried out to achieve a goal.

In BDI kernel V2 the programming model was kept the same but the engine itself was completely rebuilt based on a RETE rule engine operating on BDI rules.

Finally, in V3 the main objective was to create a new programming model that allows fast prototyping and hides as much of the framework as possible. Thus, in the new V3 kernel BDI agents are written in Java only (no XMLs any more) and annotations are used to designate BDI elements. 
Another important aspect is the much stronger integration of BDI and object oriented concepts in the new kernel, i.e. it becomes much simpler to program BDI agent having a solid background on object-oriented concepts (supporting e.g. inheritance, POJO programming, dependency injection). 

# Table of Contents
This guide will cover the following topics, which correspond coarsely to the available Jadex BDIV3 language elements:

-   [02 Concepts](02 Concepts) describes the basic BDI concepts.
-   [03 Agent Specification](03 Agent Specification) describes how an agent can be programmed.
-   [04 Imports](04 Imports) describes how elements can be imported.
-   [05 Capabilities](05 Capabilities) describes how agents modules (capabilities) can be used.
-   [06 Beliefs](06 Beliefs) describes how agents knowledge can be specified.
-   [07 Goals](07 Goals) treats how goals can be used.
-   [08 Plans](08 Plans) describes how procedural plans are used.
-   [09 Events](09 Events) handles internal as well as message events.
-   [10 Expressions](10 Expressions) treats the usage of Jadex expressions and their underlying language.
-   [11 Conditions](11 Conditions) describes Jadex conditions and their underlying language.
-   [12 Properties](12 Properties) describes how agent and capability properties can be defined.
-   [13 Configurations](13 Configurations) introduces configurations for starting agents with different settings.
-   [14 External Interactions](14 External Interactions) treats the interaction of external processes with BDI agents.
-   [16 Predefined Capabilities](16 Predefined Capabilities) explains the library of ready-to use capabilities.