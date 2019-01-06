# Jadex Active Components

The Jadex Active Components Framework provides programming and execution facilities for distributed and concurrent systems. 
The general idea is to consider systems to be composed of components acting as service providers and consumers. 
Hence, it is very similar to the Service Component Architecture (SCA) approach and extends it with agents-oriented concepts. 
In contrast to SCA, components are always active entities, i.e. they posses autonomy with respect to what they do and when they perform actions making them akin to agents. In contrast to agents, communication is preferably done using service invocations.

Read more about Active Components at www.activecomponents.org and visit our [documentation pages](https://download.actoron.com/docs/releases/latest/jadex-mkdocs/).

# Maven/Gradle dependencies

Gradle:

```compile 'org.activecomponents.jadex:jadex-distribution-standard:${jadex-version}'```

Maven:
```xml
<dependency>
    <groupId>org.activecomponents.jadex</groupId>
    <artifactId>jadex-distribution-minimal</artifactId>
    <version>${jadex-version}</version>
</dependency>
```

# Releases
For the latest release versions, please have a look at our [download page](https://www.activecomponents.org/index.html#/download).