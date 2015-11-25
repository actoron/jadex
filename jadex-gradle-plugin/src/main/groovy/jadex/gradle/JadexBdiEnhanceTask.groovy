package jadex.gradle

import com.android.build.gradle.AppExtension
import com.android.builder.model.BuildType
import jadex.bdiv3.AbstractAsmBdiClassGenerator
import jadex.bdiv3.ByteKeepingASMBDIClassGenerator
import jadex.bdiv3.KernelBDIV3Agent
import jadex.bdiv3.MavenBDIModelLoader
import jadex.bdiv3.model.BDIModel
import jadex.bridge.ResourceIdentifier
import jadex.maven.ResourceUtils
import jadex.micro.annotation.NameValue
import org.apache.commons.io.FileUtils
import org.apache.commons.io.filefilter.IOFileFilter
import org.apache.commons.io.filefilter.TrueFileFilter
import org.gradle.api.DefaultTask
import org.gradle.api.GradleScriptException
import org.gradle.api.tasks.TaskAction

class JadexBdiEnhanceTask extends DefaultTask {

    def BuildType buildType

    def File inputDir

    def AppExtension android

//    def File classesDir
//
//    @OutputDirectory
//    def File outputDir
//
//    @Input
//    def inputProperty

//    @TaskAction
//    void execute(IncrementalTaskInputs inputs) {
//        println "Running jadex bdi enhance..."
//        println inputs.incremental ? "CHANGED inputs considered out of date"
//                : "ALL inputs considered out of date"
//        if (!inputs.incremental)
//            project.delete(outputDir.listFiles())
//
//        inputs.outOfDate { change ->
//            println "out of date: ${change.file.name}"
//            def targetFile = new File(outputDir, change.file.name)
//            targetFile.text = change.file.text.reverse()
//        }
//
//        inputs.removed { change ->
//            println "removed: ${change.file.name}"
//            def targetFile = new File(outputDir, change.file.name)
//            targetFile.delete()
//        }
//    }




//    def org.slf4j.Logger logger

    def ByteKeepingASMBDIClassGenerator gen
    def MavenBDIModelLoader modelLoader

    def public JadexBdiEnhanceTask() {
//        org.apache.log4j.Logger.rootLogger.setLevel(org.apache.log4j.Level.INFO)
        modelLoader = new MavenBDIModelLoader()
        gen = new ByteKeepingASMBDIClassGenerator();
        modelLoader.setGenerator(gen);
    }

    @TaskAction
    def generate() {
        log( "Running jadex bdi enhance... xxx")

        generateBDI(inputDir)
    }

    def generateBDI(File classesDir) {
        def inPlace = true
        def outputDirectory = classesDir

        if (!classesDir.exists()) {
//            println "creating dir"
            classesDir.mkdirs()
        }

        Collection<File> allBDIFiles = FileUtils.listFiles(classesDir, bdiFileFilter, TrueFileFilter.INSTANCE);
        String[] imports = getImportPath(allBDIFiles, outputDirectory);
        ResourceIdentifier rid = new ResourceIdentifier();

        if (allBDIFiles.size() > 0)
        {
            log("Found " + allBDIFiles.size() + " BDI V3 Agent classes in " + classesDir);
        } else {
            log("Found no BDI V3 Agent classes in " + classesDir);
        }
        URL inputUrl = classesDir.toURI().toURL();

//        setClassRealm();
//        ClassLoader originalCl = descriptor.getClassRealm();
        ClassLoader originalCl = this.getClass().getClassLoader()
        URLClassLoader inputCl = new URLClassLoader([inputUrl] as URL[], originalCl)

        Collection<File> allClasses = FileUtils.listFiles(classesDir, null, true);

        for (File bdiFile : allClasses)
        {
            gen.clearRecentClassBytes();
            BDIModel model = null;

            String relativePath = ResourceUtils
                    .getRelativePath(bdiFile.getAbsolutePath(), classesDir.getAbsolutePath(), File.separator);

            if (bdiFileFilter.accept(bdiFile) && hasDelta(bdiFile))
            {
                String agentClassName = relativePath.replace(File.separator, ".").replace(".class", "");

                if (inPlace) {
                    Class<?> loadClass = inputCl.loadClass(agentClassName);
                    if (AbstractAsmBdiClassGenerator.isEnhanced(loadClass)) {
                        log("Already enhanced: " + relativePath);
                        continue;
                    }
//					tempLoader.close();
                }

                logger.debug("Loading Model: " + relativePath);

                try
                {
                    model = (BDIModel) modelLoader.loadModel(relativePath, imports, inputCl, inputCl, [rid, null, null] as Object[]);
                }
                catch (Throwable t)
                {
                    // if error during model building, just dont enhance this file.
                    String message = t.getMessage();
                    if (message == null)  {
                        message = t.toString();
                    }
                    logger.error("Error loading model: " + agentClassName + ", exception was: " + t.toString());
//                    buildContext.addMessage(bdiFile, 0, 0, "Error loading model: " + agentClassName, BuildContext.SEVERITY_ERROR, t);
                    throw new GradleScriptException("Error loading model: " + agentClassName, t)
                    // just copy file
                    if (!classesDir.equals(outputDirectory))
                    {
                        File newFile = new File(outputDirectory, relativePath);
                        if (!newFile.exists())
                        {
                            newFile.getParentFile().mkdirs();
                            FileUtils.copyFile(bdiFile, newFile);
                        }
                    }
                    continue;
                }

                log("Generating classes for: " + relativePath);
//				classes = gen.generateBDIClass(agentClassName, model, outputCl);

                Set<Map.Entry<String,byte[]>> classEntrySet = gen.getRecentClassBytes().entrySet();

//				for (Class<?> clazz : classes)
                for (Map.Entry<String, byte[]> entry : classEntrySet)
                {
                    String className = entry.getKey();
                    byte[] classBytes = entry.getValue();
                    logger.debug("    ... " + className);
                    String path = className.replace('.' as char, File.separatorChar) + ".class";
//					byte[] classBytes = gen.getClassBytes(clazz.getName());
                    try
                    {
                        // write enhanced class
                        File enhancedFile = new File(outputDirectory, path);
                        enhancedFile.getParentFile().mkdirs();
                        DataOutputStream dos = new DataOutputStream(new FileOutputStream(enhancedFile));
//                        DataOutputStream dos = new DataOutputStream(buildContext.newFileOutputStream(enhancedFile));
                        dos.write(classBytes);
                        dos.close();
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                        // URLClassLoader.close() not in JDK 1.6
                    if (inputCl != null) {
                        inputCl.close();
                    }
//                    if (outputCl != null) {
//                        outputCl.close();
//                    }
                        throw new Exception(e.getMessage());
                    }
                }
            }
            else
            {
                // just copy file
                if (!classesDir.equals(outputDirectory))
                {
                    File newFile = new File(outputDirectory, relativePath);
                    if (!newFile.exists())
                    {
                        newFile.getParentFile().mkdirs();
                        FileUtils.copyFile(bdiFile, newFile);
                    }
                }
            }
        }
        inputCl.close()
//        outputCl.close()
    }

    boolean hasDelta(File file) {
        // TODO implement
        true
    }

    def private String[] getImportPath(Collection<File> allBDIFiles, File parentDir)
    {
        logger.debug("Building imports Path...");
        List<String> result = new ArrayList<String>();
        String absoluteOutput = parentDir.getAbsolutePath();

        for (File bdiFile : allBDIFiles)
        {
            String relativePath = ResourceUtils.getRelativePath(bdiFile.getAbsolutePath(), absoluteOutput, File.separator);
            String importPath = relativePath.replace(File.separator, ".").replace(".class", "");
            result.add(importPath);
        }

        return result.toArray(new String[result.size()]);
    }

    def IOFileFilter bdiFileFilter = new IOFileFilter() {
        def kernelTypes = getBDIKernelTypes()

        boolean accept(File dir, String name) {
            boolean result = false;
            for (String string : kernelTypes) {
                if (name.endsWith(string)) {
                    result = true;
                    break;
                }
            }
            return result;
        }

        boolean accept(File file) {
            return accept(file.getParentFile() as File, file.getName())
        }
    }

    private List<String> getBDIKernelTypes()
    {
        def annotation = KernelBDIV3Agent.getAnnotation(jadex.micro.annotation.Properties);

        NameValue[] value = annotation.value()

        String types = null;
        for (int i = 0; i < value.length; i++)
        {
            logger.debug("possible annotation: " + value[i]);
            if (value[i].name().equals("kernel.types"))
            {
                types = value[i].value();
            }
        }

        final List<String> kernelTypes = new ArrayList<String>();
        int begin = types.indexOf("\"");;
        while (begin != -1)
        {
            int end = types.indexOf("\"", begin + 1);
            String kernelType = types.substring(begin + 1, end);
            if (kernelType.length() > 0)
            {
                kernelTypes.add(kernelType);
            }
            begin = types.indexOf("\"", end + 1);
        }

        logger.debug("KernelBDIV3 Types: " + kernelTypes);
        return kernelTypes;
    }



    def setClassRealm()
    {
        // collect runtime classpath elements of the user project
//        List<String> classPathElements;
//        try
//        {
//            final ClassRealm realm = descriptor.getClassRealm();
//            classPathElements = project.getRuntimeClasspathElements();
//            classPathElements.addAll(project.getCompileClasspathElements());
//
//            for (String element : classPathElements)
//            {
//                File elementFile = new File(element);
//                realm.addURL(elementFile.toURI().toURL());
//            }
//
//        }
//        catch(DependencyResolutionRequiredException e1)
//        {
//            e1.printStackTrace();
//        }
//        catch(MalformedURLException e)
//        {
//            e.printStackTrace();
//        }
    }

    def void log(msg) {
        println msg
    }
}