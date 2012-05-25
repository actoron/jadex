Sources for the Jadex 3D addon.
For building Jadex with the 3D addon unpack the
Jadex sources AND the 3d addon sources to the same directory.
For building Jadex and the addon with Maven just type:
mvn -P jadex-full-dist clean package

You can also import the Jadex projects into an IDE like eclipse or IntelliJ IDEA.
Make sure that your IDE supports Maven builds
(e.g. install the m2eclipse plugin in eclipse).