pipeline {
  //agent { docker { image 'maven:3.3.3' } }
  //agent { docker { image 'node:14-alpine' } }  
  agent any
 
  environment {
   	DISABLE_AUTH = 'true'
   	DB_ENGINE    = 'sqlite'
  }
     
  stages {
	stage("Initialize") {
         steps {
            echo "*** Inside init step ***"
            echo "*************"
            echo ""
            echo "env.PATH: ${env.PATH}"
            echo "env.GIT_BRANCH: ${env.GIT_BRANCH}"
            echo "env.BRANCH_NAME: ${env.BRANCH_NAME}"
            echo "env.TAG_NAME: ${env.TAG_NAME}"
            echo "env.BUILD_ID: ${env.BUILD_ID}"

            // Some examples of single line and mult-line shell execution
            // sh 'printenv'
            /**
            sh '''
                pwd
                ls -lah
                printenv
            '''
            */
        }
        post {
            always {
                echo "Post-Init result: ${currentBuild.result}"
                echo "Post-Init currentResult: ${currentBuild.currentResult}"
            }
        }        
    }
    stage('Build') {
      	when {
         	expression { currentBuild.currentResult == 'SUCCESS' }
      	}
        steps {
            echo "*** Inside build step ***"
            echo "*************"
            echo ""

            //bat 'mvn clean compile'            
            sh '''
                mvn -Dmaven.test.skip=true package
            '''
        }   
    }
    stage('Test') {
      	when {
         	expression { currentBuild.currentResult == 'SUCCESS' }
      	}
        steps {
            echo "*** Inside test step ***"
            echo "*************"
            echo ""

            sh '''
				mvn -Dmaven.test.skip=false test
            '''
        }
    }
    stage('Build Dist') {
      	when {
         	expression { currentBuild.currentResult == 'SUCCESS' }
      	}
        steps {
            echo "*** Inside build-dist step ***"
            echo "*************"
            echo ""
            
            sh '''
               mvn assembly:single
            '''
        }
    }
  }
}