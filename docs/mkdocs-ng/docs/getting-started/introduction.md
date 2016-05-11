# Jadex Active Components
With Jadex, you can use the *Active Components* approach to develop distributed applications. This approach combines a hierachical service component architecture (SCA) with the possibility of abstract business logic implementation base on [BDI Agents](../tutorials/bdiv3/01 Introduction) or [BPMN Workflows](../tutorials/bpmn/01 Introduction) (see [component types](../component-types/component-types) for more information).

The communication model is based on [services](../services/implementation).
An Active Component is an entity that has defined dependencies with its environment. 
Similar to other component models these dependencies are described using required and provided services, i.e. services that a component needs to consume from other components for its functioning and services that it provides to others.

The interaction between components is fully network-transparent, so components can be executed on one or different machines without changing their code.
The figure below summarizes the general concept of an Active Component.

![03 Active Components@ac.png](ac.png)  
*Active Component Structure*