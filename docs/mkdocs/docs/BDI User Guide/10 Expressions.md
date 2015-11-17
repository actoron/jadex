<span>Chapter 10. Expressions</span> 
====================================

For many elements (parameter values, default and initial facts of beliefs, etc.) the developer has to specify expressions in the ADF. The most important part of an expression is the expression string. In addition, some meta information can be attached to expressions, e.g., to specify the class the resulting value should have. 

<span>Expression Syntax</span> 
------------------------------

The expression language follows a Java-like syntax. In general, all of the *operators* of the Java language are supported (with the exception of assignment operators), while no other constructs can be used. Operators are, for example, math operators (+-\*/%), logical operators (&&, ||, !), and method, or constructor invocations. Other unsupported constructs are loops, class declarations, variable declarations, if-then-else blocks, etc. As a rule you can use every Java code that can be contained in the right hand side of a variable assignment (e.g., *var* = &lt;expression&gt;). There are just two exceptions to this rule: Declarations of anonymous inner classes are not supported. Assignment operators (=, +=, \*=...) as well as de- and increment operators (++, --) are not allowed, because they would violate declarativeness.

<div class="wikimodel-emptyline">

</div>

In addition to the Java-like syntax, the language has some extensions: Parameters give access to specific elements depending on the context of the expression. OQL-like select statements allow to create complex queries, e.g., for querying the beliefbase. To simplify the Java statements in the expressions, imports can be declared in the ADF (see <span class="wikiexternallink">[Imports](04%20Imports)</span>) that allow to use unqualified class names. The imports are defined once, and can be used for all expressions throughout the ADF.\
  

<span>Expression Properties</span> 
----------------------------------

Expressions have properties which can be specified as attributes of the enclosing XML tag. The optional class attribute can be specified for any expression, and is used for cross checking the expression string against the expected return type. This allows to detect errors in the ADF already at load time, which could otherwise only be reported at runtime. 

<div class="wikimodel-emptyline">

</div>

The evaluation mode influences when and how often the expression is evaluated at runtime. A "static" expression caches the value once the expression created, while the value of a "dynamic" expression is reevaluated for ervery access. The default values of these properties depend on the context in which the expression is used. E.g. initial facts of beliefs are usually static, while conditions are dynamic.   

<span>Reserved Variables</span> 
-------------------------------

Within expressions, several variables can be accessed depending on the context the expression is used in. Generally, the following variable names are reserved for agent components and can be accessed directly by their name. In the following table the reserved variables, their type and accessibility settings are summarized. Values of beliefs and belief sets (from the \$beliefbase) and parameter(set)s (from \$plan, \$event, \$goal, and \$ref) can be accessed using a shortcut notation (allowing to write statements like "\$beliefbase.mybelief", which is the same as "\$beliefbase.getBelief("mybelief").getFact()).\
&lt;br/&gt;

  --------------------------------------------------------------------------------------------------------------------
  Name               Class             Accessibiliy
  ------------------ ----------------- -------------------------------------------------------------------------------
  \$scope            ICapability       In any expression

  \$beliefbase       IBeliefbase       In any expression

  \$planbase         IPlanbase         In any expression

  \$goalbase         IGoalbase         In any expression

  \$eventbase        IEventbase        In any expression

  \$expressionbase   IExpressionbase   In any expression

  \$propertybase     IPropertybase     In any expression

  \$goal             IGoal             In any goal expression (except creation condition and binding options)

  \$plan             IPlan             In any plan expression (except trigger and pre condition and binding options)

  \$event            IEvent            In any event expression (except binding otpions)

  \$ref              IGoal             In any inhibition arc expression.

  \$messagemap       Map               In match expressions of message events.\
                                       &lt;p/&gt;\
                                       *Reserved expression variables*
  --------------------------------------------------------------------------------------------------------------------

<span>Expressions Examples</span> 
---------------------------------

 \
In the following, two example expressions are shown. Here the expressions are used to specifiy the facts of some beliefs. In fact there are many places besides beliefs in the ADF where expressions can be used. In the first case, the "starttime" fact expression is evaluated only once when the agent is born. The second belief represents the agent's lifetime and is recalculated on every access.


```xml

<belief name="starttime" class="long">
    <fact>
        System.currentTimeMillis()
    </fact>
</belief>
    
<belief name="lifetime" class="long" evaluationmode="pull">
    <fact>
        System.currentTimeMillis() - $beliefbase.starttime
    </fact>
</belief>

```


*Example expressions*

<span>ADF Expressions</span> 
----------------------------

The expression language cannot only be used to specify values for beliefs, plans, etc. in the ADF but also for dynamic evaluation, e.g., to perform queries on the state of the agent, most notably the current beliefs. Expressions (*jadex.bdi.runtime.IExpression*) can be created at runtime by providing an expression string. A better way is to predefine expressions in the ADF in the expression base (see Figure below). Because predefined expressions only have to be parsed and precompiled once and can be reused by different plans, they are more efficient. The following example shows a predefined expression for searching the beliefbase for a certain person contained in the belief persons, using the OQL-like language extension described in more detail below. Moreover, this example uses a custom parameter \$surname to specify which person to retrieve from the belief set.

![](jadexexpressionsadf.png)\\\
*The Jadex expressions XML schema part*

Primary usage of predefined expression is to perform queries, when executing plans. The *getExpression(String name)* method creates an expression object based on the predefined expression with the given name. In addition, the *createExpression(String exp \[, String\[\] paramnames, Class\[\] paramtypes\])* method is used to create an expression directly by parsing the expression string (without referring to a predefined expression). Custom parameters can be optionally be defined for such queries by additionally providing the parameter names and classes. Values for these parameters have to be supplied when executing the query. The expression object provides several *execute()* methods to evaluate a query specifying either no parameters, a single parameter as name/value pair, or a set of parameters that are defined as a *String* and an *Object array* containing parameter names and values separately. &lt;!-- You can also pre-set parameters before executing the query using the *setParameter()* method. For example, one can execute the person query with a given surname. --&gt;


```xml

<agent ...>
  ...
  <expressions>
    <expression name="find_person" class="Person">
      select one Person $person
      from $person in $beliefbase.persons
      where $person.getSurname().equals($surname)
    </expression>
  ...
  </expressions>
    ...
</agent>

```



```java

public void body 
{
  IExpression query = getExpression("find_person");
  ...
  Person person = (Person)query.execute("$surname", "Miller");
  ...
}

```


*Evaluating an expression from a plan*\
 

<span>OQL-like Select Statements</span> 
---------------------------------------

 \
Jadex provides an OQL-like query syntax, which can be used in conjunction with any other expression statements. OQL (Object-Query-Language) is an extension of SQL (Structured-Query-Language) for object-oriented databases. The generic query syntax as supported by Jadex is very similar to OQL (note that until now only select statements are supported). The syntax is shown in next code snippet.\
  


```

[select (one)? <class>? <result-expression>
from (<class>? <element> in)? <collection-expression>
    (, <class>? <element> in <collection-expression>)*
(where <where-expression>)?
(order by <ordering-expression> (asc | desc)? )?

```


*Syntax of OQL-like select statements*\
    

&lt;p/&gt;\
The &lt;collection-expression&gt; has to evaluate to an object that can be iterated (an array or an object implementing *Iterator*, *Enumeration*, *Collection*, or *Map*). In the other expressions (result, where, ordering) the query variables can be accessed using &lt;element&gt;. When using "&lt;element&gt;" as result expression, the second "&lt;element&gt; in" part can be omitted for readability. While you are free to use any expression for the result and the ordering, the where clause, of course, has to evaluate to a boolean value.\
&lt;br/&gt;\
  \
Some simple example queries (assuming that the beliefbase contains a belief set "persons", where each person has attributes "forename", "surname", "age", and "address") are shown in the code snippets below. The first query returns a *java.util.List* of all persons in the order they are contained in the belief set. The second query only returns persons that are older than 21. In this case a cast is used to invoke the *getAge()* method. The third example orders the returned list by the addresses of the persons, using a type declaration at the beginning of the query, and therefore does not need a cast for accessing the *getAddress()* method. The order-by implementation relies on the *java.lang.Comparable* interface. In the example, the addresses have to be comparable for the query to work. The next query shows that it is possible to use complex expressions to create the result elements. Note, that in this case, the "\$person in" part cannot be ommited. The last example shows how to do a join. The expression returns a list of strings of any two (distinct) persons, which have the same address.


```java

select $person from $beliefbase.persons

select $person from $beliefbase.persons where ((Person)$person).getAge()>21

select Person $person from $beliefbase.persons order by $person.getAddress()

select $person.getSurname()+", "+$person.getForename()
from Person $person in $beliefbase.persons

select $p1+", "+$p2 from Person $p1 in $beliefbase.persons,
                         Person $p2 in $beliefbase.persons
where $p1!=$p2 &amp;&amp; $p1.getAddress().equals($p2.getAddress())</programlisting>

```


*Examples of OQL-like select statements*

An extension to OQL is the support of the "one" keyword. The default (without "one") is standard OQL semantics to return all objects matching the query. The "one" keyword is used to select a single element. For queries without ordering, this returns the first found element that matches the query. When using ordering, the query is evaluated for all input elements and returns the first element after having applied the ordering. In both cases null is returned, when no element matches the query. Without the "one" keyword, an empty collection is returned, when no element matches the query.
