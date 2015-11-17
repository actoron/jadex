1 Introduction

Agent applications consist not only of agents but also of an environment the agents are situated in. Hence, the construction of an agent application requires efforts in both areas, whereby the environmental aspects should not be underestimated. One problem is that many different kinds of environments exist and depending on the type of application quite different environment requirements may exist. The environment support, called \~EnvSupport\~ described in this guide is meant to support the rapid development of virtual 2d and 3d environments. This allows quickly developing e.g. simulation applications, in which agents purely act in this virtual environments. But it is also feasible to use a virtual environments as an enhancement for a real one, e.g. a pheromone-based coordination for robots. EnvSupport covers many aspects for a complete and seamless integration of agents with a virtual environments. The idea for building EnvSupport emerged from our experiences with building example applications. We noticed that a lot of similarities between our example applications exist and that it could be beneficial to have a generic infrastructure that supports the construction of virtual worlds. If you have used agent simulation toolkits such as NetLogo or SeSAm, you will know that these toolkits have built-in support for (different kinds of) 2d and 3d environments. In Jadex with EnvSupport something similar is available but with one important difference. EnvSupport is optional and application may or may not make use of it. It has been realized as a specific kind of \~environment space\~, which can be added to the application description. This does also mean that an application may define multiple different environments if this is advantageous. Currently EnvSupport has the following main features and limitations.

<div class="wikimodel-emptyline">

</div>

<div class="wikimodel-emptyline">

</div>

\*Features:\*

-   Declarative specification of the environment as \~application space\~
-   Model definition consisting mainly of \~space objects\~, \~tasks\~ and \~environment processes\~ in a 2d or 3d grid or continuous world
-   Agent-environment interaction via customizable \~percepts\~ and \~actions\~
-   2d and 3d visualization of the environment, its objects and agents, including possibilities for animation etc.
-   Customizable space execution with built-in support for continuous and round-based execution semantics
-   Highly extensible with a lot of ready-to-use components for frequent use cases

<div class="wikimodel-emptyline">

</div>

\*Current Limitations:\*

-   No direct manipulation of the environment from the user interface
-   No integrated collision detection
-   Applications with EnvSupport cannot be distributed over several platform nodes

<div class="wikimodel-emptyline">

</div>

This guide will continue describing the details of EnvSupport following coarsely its internal struturing by \~domain model\~, \~agent environment interaction\~ and \~visualization\~. These aspects will first be explained on a relatively high level with respect to their conceptual meanings and then again be picked up for a more detailed discussion on the programming level.
