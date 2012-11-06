This is a source distribution of Jadex-Android.
For building from the sources unpack the sources to a directory of your choice.

For building with Maven type:
mvn -f pom-android-dist.xml -P dist clean test

For rebuilding the distribution zip type: 
mvn -f pom-android-dist.xml -P dist clean package

You can also import the Jadex projects into an IDE like eclipse or IntelliJ IDEA.
Make sure that your IDE supports Maven builds
(e.g. install the m2eclipse plugin in eclipse).
