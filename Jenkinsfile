pipeline {
  agent { label 'jadex-jenkins-agent' }
  stages {
  
	// Build and check if all tests pass before doing anything else 
	stage('Build and Test') {
	  steps {
	    // not required?
	    //sh './gradlew -Pdist=addongradleplugin clean :android:gradle-plugin:test publishToMavenLocal -x javadoc -x processSchemas'
		wrap([$class: 'Xvfb']) {
		  // todo: why build hangs with distzip and javadoc?
		  // No 'clean' when already done for android-gradle-plugin
		  sh './gradlew -Pdist=publishdists clean :applications:micro:test :platform:base:test test -x javadoc --continue'
		  // Fetch build version and set it
		  def props = readProperties  file:'build/jadexversion.properties'
		  script {
		      currentBuild.displayName = props['jadex_build_version']
		  }
		}
	  }
	}
	
	// Build all kinds of docs/dist files as parallel as possible
	stage('Dist and Docs') {
	  parallel {
		stage('Dist') {
		  steps {
			sh './gradlew -Pdist=publishdists checkDist'
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
	
	// Upload to all kinds of targets
	stage('Publish and Deploy') {
	  parallel {
		stage('Publish') {
		  steps {
		  	// TODO: move credentials to environment
			sh './gradlew -Pdist=publishdists publish -x test -P repo_noncommercial=https://oss.sonatype.org/service/local/staging/deploy/maven2 -P repo_commercial= -Prepouser=Lars -Prepopassword=lax'
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