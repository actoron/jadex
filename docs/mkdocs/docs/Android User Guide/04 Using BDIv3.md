Using BDIv3 on Android
===================================

The BDI v3 programming model heavily depends on code-generation based on java annotations.
It's using the ASM Bytecode manipulation framework to generate the code.
Android not only uses a different virtual machine than any Java SE environment, the Dalvik Virtual Machine (DVM), it also uses a different bytecode representation, which is not supported by ASM.

As runtime bytecode generation is slow on android anyway, we use a different mechanism to make BDIv3 work: Generating the bytecode at compile time, which is then processed and converted by standard android tools.

For this method to work, however, we need to include an additional compile step, which is done using maven.
So you **need to setup a maven project to use BDIv3** Components!

Additionally, you need to have a copy of the jadex-android-maven-plugin, so this is the first step

jadex-android-maven-plugin
---------------------------------------

The simplest way to get the plugin is to insert the following code in your pom.xml, which adds my jadex repository:


```xml

<repositories>
  <repository>
    <id>jadex-android</id>
    <url>http://jadex.julakali.org/nexus/content/groups/public/</url>
  </repository>
</repositories>

```




Then, in the build section of your pom, define that you want to use the plugin:


```xml

<build>
  <plugins>
    <plugin>
      <groupId>net.sourceforge.jadex</groupId>
      <artifactId>jadex-android-maven-plugin</artifactId>
      <executions>
        <execution>
          <goals>
            <goal>generateBDI</goal>
          </goals>
        </execution>
      </executions>
    </plugin>
  </plugins>
</build>

```


If you compile your project with maven now, the plugin will run, detect all BDIV3 Agents and will enhance the classes as needed by the Jadex runtime.
