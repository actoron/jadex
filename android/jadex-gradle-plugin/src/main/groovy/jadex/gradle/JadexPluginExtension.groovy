package jadex.gradle;

import org.gradle.api.Project

class JadexPluginExtension {
    boolean incremental
    boolean includeExternalDeps = false // true does not work yet?
    boolean includeLocalDeps = false // true does not work yet?

    boolean isOnJava8 = (System.properties.'java.version' as String).startsWith('1.8')
    Project project


    public JadexPluginExtension(Project project) {
        this.project = project;
    }


}