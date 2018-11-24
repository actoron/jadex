pipeline {
  //agent { label 'jadex-jenkins-agent' }
  agent any
  stages {
  
    // Determine version to build
	stage('Prepare') {
	  steps {
	    script {
	      sh 'printenv'
	      
	      // Read major.minor version from properties file
	      def versionprops = readProperties  file: 'src/main/buildutils/jadexversion.properties'
	      def version = versionprops.jadexversion_major + "." + versionprops.jadexversion_minor
	      def patch = "0";	// Default for new major/minor version
	      
	      // Fetch latest major.minor.patch tag from git
	      def status = sh (
	        returnStatus: true,
	        script: "git describe --match \"${version}.*\" --abbrev=0 > tagversion.txt"
	      )
		  if(status==0) {
		    patch = readFile('tagversion.txt').trim()
		    echo "pre strip ${patch}"
		    patch = patch.substring(patch.lastIndexOf(".")+1)
		    echo "post strip1 ${patch}"
		    if(patch.lastIndexOf("-")!=-1) {
		        patch = patch.substring(patch.lastIndexOf("-")+1);
		    }
		    echo "post strip2 ${patch}"
		  }
	      // Todo: Fetch latest major.minor.patch[-branchname-branchpatch] tag from git for non-master/stable branches
          currentBuild.displayName = version + "." + patch
          env.BUILD_VERSION_SUFFIX = patch
	    }
	  }
	}
	
	// Build and check if all tests pass before doing anything else 
	stage('Build and Test') {
	  steps {
		wrap([$class: 'Xvfb']) {
		  // todo: why build hangs with distzip and javadoc?
		  sh './gradlew -Pdist=publishdists clean :applications:micro:test :platform:base:test test -x javadoc -x processSchemas --continue'
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