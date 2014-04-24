This is a source distribution of Jadex.
For building from the sources unpack the sources to a directory of your choice.

For building with Maven just type:
mvn clean test

For rebuilding the distribution zip type: 
mvn -P dist clean package

On some systems, you might need to increase the Java perm size like this:
set MAVEN_OPTS=-XX:MaxPermSize=256M

You can also import the Jadex projects into an IDE like eclipse or IntelliJ IDEA.
Make sure that your IDE supports Maven builds
(e.g. install the m2eclipse plugin in eclipse).
