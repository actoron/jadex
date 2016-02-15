The &lt;imports&gt; tag is used to specify, which classes and packages can be used by Java expressions throughout an agent or capability definition file. The import section with an ADF resembles very much the Java import section of a class file. A Jadex import statement has the same syntax as in Java allowing single classes as well as whole packages being included.


![](jadeximportsadf.png)

*Figure 1: The Jadex imports XML schema part*


The imports are used for searching Java classes as well as non-Java agent artifacts such as agent.xml or capability.xml files. It is not necessary to declare an import statement for the actual package of the ADF as this is automatically considered.

# Import Examples

In the following some simple code snippets from an ADF are shown that demonstrate how import statements are declared and subsequently used, e.g., in facts of beliefs, or to include a capability from another package.



```xml
<imports>
    <!-- Import only the HashMap class. -->
    <import>java.util.HashMap</import>

    <!-- Import all classes of the awt package. -->
    <import>java.awt.**</import>

    <!-- Import a movement package containing, e.g., a Move capability. -->
    <import>movement.**</import>
    ...
</imports>

<capabilities>
    <!-- Use the imported movement.Move capability. -->
    <capability name="movecap" file="Move"/>
</capabilities>

<beliefs>
    <!-- Use the imported java.util.HashMap. -->
    <belief name="data">
        <fact>new HashMap()</fact>
    </belief>

    <!-- Use the imported java.awt.Frame. -->
    <belief name="gui">
        <fact>new Frame()</fact>
    </belief>
</beliefs>

```

