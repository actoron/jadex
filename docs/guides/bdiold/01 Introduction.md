# Introduction

**Outdated Documentation**: This page is yet to be updated to the latest Jadex version. The documentation is still valid and the explanations still apply. Yet, due to some API changes, not all code examples in this document may be used as such. When in doubt, check the example sources in the *applications* modules available on GitHub, e.g. for [Micro](https://github.com/actoron/jadex/tree/master/applications/micro/src/main/java/jadex/micro) and [BDI](https://github.com/actoron/jadex/tree/master/applications/bdiv3/src/main/java/jadex/bdiv3) agents.

Jadex BDI is an agent-oriented reasoning engine for writing rational agents with XML and the Java programming language. Thereby, Jadex represents a conservative approach towards agent-orientation for several reasons. One main aspect is that no new programming language is introduced. Instead, Jadex agents can be programmed in the state-of-the art object-oriented integrated development environments (IDEs) such as [eclipse](http://www.eclipse.org/). The other important aspect concerns the middleware independence of Jadex. As Jadex BDI is loosely coupled with its underlying middleware, Jadex can be used in very different scenarios on top of agent platforms as well as enterprise systems such as J2EE.

Similar to the paradigm shift towards object-orientation agents represent a new conceptual level of abstraction extending well-known and accepted object-oriented practices. Agent-oriented programs add the explicit concept of autonomous actors to the world of passive objects. In this respect agents represent active components with individual resoning capabilities. This means that agents can exhibit reactive behavior (responding to external events) as well as pro-active behavior (motivated by the agents own goals).

This tutorial will cover the following topics, which correspond coarsely to the available Jadex language elements:

- [02 Concepts](02%20Concepts.md) describes the basic BDI concepts.
- [03 Agent Specification](03%20Agent%20Specification.md) describes how an agent can be programmed.
- [04 Imports](04%20Imports.md) describes how elements can be imported.
- [05 Capabilities](05%20Capabilities.md) describes how agents modules (capabilities) can be used.
- [06 Beliefs](06%20Beliefs.md) describes how agents knowledge can be specified.
- [07 Goals](07%20Goals.md) treats how goals can be used.
- [08 Plans](08%20Plans.md) describes how procedural plans are used.
- [09 Events](09%20Events.md) handles internal as well as message events.
- [10 Expressions](10%20Expressions.md) treats the usage of Jadex expressions and their underlying language.
- [11 Conditions](11%20Conditions%20(old).md) describes Jadex conditions and their underlying language.
- [12 Properties](12%20Properties.md) describes how agent and capability properties can be defined.
- [13 Configurations](13%20Configurations.md) introduces configurations for starting agents with different settings.
- [14 External Interactions](14%20External%20Interactions.md) treats the interaction of external processes with BDI agents.
- [16 Predefined Capabilities](16%20Predefined%20Capabilities.md) explains the library of ready-to use capabilities.
