pipeline {
  agent { label 'jadex-jenkins-agent' }
  agent any
  stages {
  
    // Determine version to build
	stage('Prepare') {
	  steps {
	    script {
	      //sh 'printenv'
	      
	      // Determine build number
		  def build_util = load "src/main/buildutils/jenkinsutil.groovy"
	      def buildname = build_util.fetchNextBuildNameFromGitTag()
          currentBuild.displayName = buildname.full
          env.BUILD_VERSION_SUFFIX = buildname.suffix
	    }
	  }
	}
	
	// Build and check if all tests pass before doing anything else 
	stage('Build and Test') {
	  steps {
	    sh 'gradlew -Pdist=addongradleplugin clean :android:gradle-plugin:test publishToMavenLocal -x javadoc -x processSchemas'
		wrap([$class: 'Xvfb']) {
		  // todo: why build hangs with distzip and javadoc?
		  // No 'clean' as already done for android-gradle-plugin
		  sh './gradlew -Pdist=publishdists :applications:micro:test :platform:base:test test -x javadoc -x processSchemas --continue'
		}
	  }
	}
	
	// Build all kinds of docs/dist files as parallel as possible
	stage('Dist and Docs') {
	  parallel {
		stage('Dist') {
		  steps {
			sh './gradlew -Pdist=publishdists distZips -x javadoc'
		  }
		}
		stage('HTML/PDF Docs') {
		  steps {
			sh './gradlew -b docs/mkdocs-ng/build.gradle buildDocsZip buildDocsPdf'
		  }
		}
		stage('Javadocs') {
		  steps {
			sh './gradlew -Pdist=addonjavadoc javadocZip'
		  }
		}
	  }
	}
	
  }
  post {
    always {
      junit allowEmptyResults: true, testResults: '**/test-results/**/*.xml'
    }
  }
}