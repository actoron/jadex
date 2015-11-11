The \\&lt;imports\\&gt; tag is used to specify, which classes and packages can be used by Java expressions throughout an agent or capability definition file. The import section with an ADF resembles very much the Java import section of a class file. A Jadex import statement has the same syntax as in Java allowing single classes as well as whole packages being included.

<div class="wikimodel-emptyline">

</div>

<div class="wikimodel-emptyline">

</div>

{![](jadeximportsadf.png})

\~Figure 1: The Jadex imports XML schema part\~

<div class="wikimodel-emptyline">

</div>

<div class="wikimodel-emptyline">

</div>

\
The imports are used for searching Java classes as well as non-Java agent artifacts such as agent.xml or capability.xml files. It is not necessary to declare an import statement for the actual package of the ADF as this is automatically considered.

1.1 Import Examples

In the following some simple code snippets from an ADF are shown that demonstrate how import statements are declared and subsequently used, e.g., in facts of beliefs, or to include a capability from another package.\
\
{code:xml}\
&lt;imports&gt;\
    &lt;!~~ Import only the HashMap class. ~~&gt;\
    &lt;import&gt;java.util.HashMap&lt;/import&gt;

    &lt;!~~ Import all classes of the awt package. ~~&gt;\
    &lt;import&gt;java.awt.\*&lt;/import&gt;

    &lt;!~~ Import a movement package containing, e.g., a Move capability. ~~&gt;\
    &lt;import&gt;movement.\*&lt;/import&gt;\
    ...\
&lt;/imports&gt;

&lt;capabilities&gt;\
    &lt;!~~ Use the imported movement.Move capability. ~~&gt;\
    &lt;capability name="movecap" file="Move"/&gt;\
&lt;/capabilities&gt;

&lt;beliefs&gt;\
    &lt;!~~ Use the imported java.util.HashMap. ~~&gt;\
    &lt;belief name="data"&gt;\
        &lt;fact&gt;new HashMap()&lt;/fact&gt;\
    &lt;/belief&gt;

    &lt;!~~ Use the imported java.awt.Frame. ~~&gt;\
    &lt;belief name="gui"&gt;\
        &lt;fact&gt;new Frame()&lt;/fact&gt;\
    &lt;/belief&gt;\
&lt;/beliefs&gt;\
{code}\
\~Figure 2: Example import declaration and usage\~

<div class="wikimodel-emptyline">

</div>

<div class="wikimodel-emptyline">

</div>

 
