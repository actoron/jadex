# Component Types
While the most basic component type is a *Micro Agent*, Jadex supports much more advanced concepts. This is an overview of the supported component types and their [Features](../../components/components/#component-features).

<!--TODO: List IComponentFeatures of each type-->
## Micro Agents
Micro agents are pojo (plain old java object) based Java classes that support the active component properties and their own behavior description via Java annotations and so called component steps. Micro agents are fast, small and easy to develop for developers with Java experience.

### Micro Agent Features
 
| Feature Name | Description |
|--------------|-------------|
|[IPojoComponentFeature](${URLJavaDoc}/jadex/bridge/component/IPojoComponentFeature)| This feature allows to access the Component POJO object (e.g. from inside a service as in [Accessing the Component](../../services/services/#accessing-the-component)). |
|IMicroInjectionFeature| This feature implements the injections of agent object, arguments, results and features. *No user methods available.* |
|IMicroServiceInjectionFeature| Injects required services into [annotated field values](../../services/services/#using-injection). *No user methods available.*|
|ILifecycleComponentFeature| Implements the Component's [Lifecycle](../../components/components/#component-lifecycle). *No user methods available.*|

 
## BDI Agents
BDI (belief-desire-intention) is a well-know agent architecture that facilitates describing behavior with goals, plans and beliefs. The idea is to clearly distinguish between what is to achieved (goals) and how it is achieved (plans). This separation helps for different reasons. On the one hand, it helps to design complex behaviors in an understandable way and on the other hand the behavior of the agent becomes more understandable as its current (and past) goal and plan executions can be seen. 
Despite, it may sound unfamiliar using mentalistic concepts like goals and plans for programming, the programming is straight forward and relies on Java only, i.e. no new programming language has to be learned.

### BDI Agent Features
| Feature Name | Description |
|--------------|-------------|
|All Micro Agent Features| As BDI Agents extend Micro Agents, all Micro Agent features are available.|
|[IBDIAgentFeature](${URLJavaDoc}/jadex/bdiv3/features/IBDIAgentFeature.html)| Contains the API relevant for BDI Agent programming (handling of goals, plans and belief listeners).

## BPMN Workflows
 BPMN (Business Process Modelling Notation) workflows allow for describing business logic in a graphical easily understandable manner. Jadex makes BPMN executable by using custom properties that can be annotated to the workflow descriptions. The basic concept is a workflow itself is an active component and each BPMN activity can be handled by a specific Java class containing the process logic. Typical active components aspects like provided and required services are specified as properties of the process itself. 

## GPMN Workflows
 GPMN means Goal Oriented Process Notation and extends BPMN with a more high-level goal-driven description layer. GPMN workflows describe behavior in terms of flexible goal hierarchies, which help in realizing flexible runtime behavior. The simplest way to think about GPMN is that it adds a control layer to determine which lower-level BPMN workflows are executed to achieve the business aims. 

## XML Components
 XML components are the reference implementation of active components, which are useful for components without own behavior and also more declarative components that consist of other subcomponents, i.e. which are composites. With initial steps, also minimal behavior can be described as separate Java class that are included within XML components, but XML component are not first choice if components should exhibit own behavior. 
