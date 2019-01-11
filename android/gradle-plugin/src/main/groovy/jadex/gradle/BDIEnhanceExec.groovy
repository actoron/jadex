package jadex.gradle

import jadex.bdiv3.BDIAgentFactory
import jadex.bdiv3.AbstractAsmBdiClassGenerator
import jadex.bdiv3.ByteKeepingASMBDIClassGenerator
import jadex.bdiv3.KernelBDIV3Agent
import jadex.bdiv3.MavenBDIModelLoader
import jadex.bdiv3.model.BDIModel
import jadex.bridge.ResourceIdentifier
import jadex.bridge.nonfunctional.annotation.NameValue
import jadex.gradle.exceptions.BDIEnhanceException;
import jadex.maven.ResourceUtils
import jadex.commons.SClassReader

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

            boolean accept(File dir, String name) {
                boolean result = false;
				File	f	= new File(dir, name) 
				if(!f.isDirectory()) {
					f.withInputStream { stream ->
						result	= BDIAgentFactory.getLoadableType(SClassReader.getClassInfo(stream))!=null
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

    public void exec() throws BDIEnhanceException {
        log("executing enhance");
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
                catch (Exception t)
                {
                    // fail early
                    throw new BDIEnhanceException(t, agentClassName);

                    // just copy file
//                    if (!inputDir.equals(outputDir))
//                    {
//                        File newFile = new File(outputDir, relativePath);
//                        if (!newFile.exists())
//                        {
//                            newFile.getParentFile().mkdirs();
//                            FileUtils.copyFile(bdiFile, newFile);
//                        }
//                    }
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

}
