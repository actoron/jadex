package jadex.gradle

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
import org.gradle.api.GradleScriptException
import org.gradle.api.Project
import org.gradle.api.file.FileCollection
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging

/**
 * Created by kalinowski on 12.04.16.
 */
class BDIEnhanceExec {


    FileCollection classpath
    File inputDir;
    File outputDir;
    FileCollection includedFiles
    MavenBDIModelLoader modelLoader
    ByteKeepingASMBDIClassGenerator gen
    private Logger logger
    Project project

    IOFileFilter bdiFileFilter;

    public BDIEnhanceExec(Project project) {
        this.project = project;

        modelLoader = new MavenBDIModelLoader()
        gen = new ByteKeepingASMBDIClassGenerator();
        modelLoader.setGenerator(gen);
        logger = Logging.getLogger(this.getClass());

        bdiFileFilter = new IOFileFilter() {
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
    }

    def void log(msg) {
        logger.info(msg);
    }

    public void exec() {
        log("executing enhance...");
        Collection<File> allBDIFiles = FileUtils.listFiles(inputDir, bdiFileFilter, TrueFileFilter.INSTANCE);
        String[] imports = getImportPath(allBDIFiles, inputDir);
        ResourceIdentifier rid = new ResourceIdentifier();

        if (allBDIFiles.size() > 0)
        {
            log("Found " + allBDIFiles.size() + " BDI V3 Agent classes in " + inputDir);
        } else {
            log("Found no BDI V3 Agent classes in " + inputDir);
        }
        URL inputUrl = inputDir.toURI().toURL();

        ClassLoader originalCl = this.getClass().getClassLoader()
        URLClassLoader inputCl = new URLClassLoader([inputUrl] as URL[], originalCl)

        Collection<File> allFiles = FileUtils.listFiles(inputDir, null, true);

        for (File bdiFile : allFiles)
        {
            gen.clearRecentClassBytes();
            BDIModel model = null;

            String relativePath = ResourceUtils
                    .getRelativePath(bdiFile.getAbsolutePath(), inputDir.getAbsolutePath(), File.separator);

            if (bdiFileFilter.accept(bdiFile) && (includedFiles == null || includedFiles.contains(bdiFile)))
            {
                String agentClassName = relativePath.replace(File.separator, ".").replace(".class", "");

                Class<?> loadClass = inputCl.loadClass(agentClassName);
                if (AbstractAsmBdiClassGenerator.isEnhanced(loadClass)) {
                    log("Already enhanced: " + relativePath);
                    continue;
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
                    throw new GradleScriptException("Error loading model: " + agentClassName, t)
                    // just copy file
                    if (!inputDir.equals(outputDir))
                    {
                        File newFile = new File(outputDir, relativePath);
                        if (!newFile.exists())
                        {
                            newFile.getParentFile().mkdirs();
                            FileUtils.copyFile(bdiFile, newFile);
                        }
                    }
                    continue;
                }

                log("Generating classes for: " + relativePath);

                Set<Map.Entry<String,byte[]>> classEntrySet = gen.getRecentClassBytes().entrySet();

                for (Map.Entry<String, byte[]> entry : classEntrySet)
                {
                    String className = entry.getKey();
                    byte[] classBytes = entry.getValue();
                    logger.debug("    ... " + className);
                    String path = className.replace('.' as char, File.separatorChar) + ".class";
                    try
                    {
                        // write enhanced class
                        File enhancedFile = new File(outputDir, path);
                        enhancedFile.getParentFile().mkdirs();
                        DataOutputStream dos = new DataOutputStream(new FileOutputStream(enhancedFile));
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
                        throw new Exception(e.getMessage());
                    }
                }
            }
            else
            {
                // just copy file
                if (!inputDir.equals(outputDir))
                {
                    File newFile = new File(outputDir, relativePath);
                    if (!newFile.exists())
                    {
                        newFile.getParentFile().mkdirs();
                        FileUtils.copyFile(bdiFile, newFile);
                    }
                }
            }
        }
        inputCl.close()
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
}
