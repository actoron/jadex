package jadex.gradle

import com.android.build.api.transform.*
import org.gradle.api.Project
import org.gradle.api.file.FileCollection
import org.gradle.api.logging.LogLevel

class BDITransform extends Transform {

    JadexPluginExtension jadexExtension;
    Project project

    public BDITransform(Project project, JadexPluginExtension jadexExtension) {
        this.project = project;
        this.jadexExtension = jadexExtension;
    }

    @Override
    void transform(TransformInvocation ti) throws TransformException, InterruptedException, IOException {
        ti.context.logging.captureStandardOutput(LogLevel.INFO);


        ti.inputs.each {input ->
            def outputDir = ti.outputProvider.getContentLocation("bditransform", outputTypes, scopes, Format.DIRECTORY)
            println input

            input.directoryInputs.each {directoryInput ->
                File inputFile = directoryInput.file;
                FileCollection changed
                if (isIncremental()) {
                    changed = project.files()
                    directoryInput.changedFiles.each { File file, Status status ->
                        if (status == Status.ADDED || status == Status.CHANGED) {
                            changed += project.files(file)
                        }
                        if (status == Status.CHANGED || status == Status.REMOVED) {
                            // remove output
                        }
                    }
                } else {
                    changed = null;
                }

                def exec = new BDIEnhanceExec(project)
                exec.includedFiles = changed;
                exec.inputDir = inputFile
                exec.outputDir = outputDir
                exec.exec()
            }

        }
    }

    @Override
    String getName() {
        "bditransform"
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        Collections.singleton(QualifiedContent.DefaultContentType.CLASSES);
    }

    @Override
    Set<QualifiedContent.Scope> getScopes() {
        def scopes = new ArrayList<>();
        scopes.add(QualifiedContent.Scope.PROJECT)
        if (jadexExtension.includeLocalDeps) {
            scopes.add(QualifiedContent.Scope.PROJECT_LOCAL_DEPS);
        }
        if (jadexExtension.includeExternalDeps) {
            scopes.add(QualifiedContent.Scope.EXTERNAL_LIBRARIES);
        }
//        return Collections.unmodifiableCollection(scopes)
        return scopes;
    }

    @Override
    boolean isIncremental() {
//        jadexExtension.incremental
        false
    }
}
