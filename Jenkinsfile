pipeline {
    agent any
    tools {
        jdk 'jdk17'
        maven 'maven-3'
    }

    environment {
        SPRING_PROFILES_ACTIVE = 'ci'
    }

    stages {
        stage('Checkout') {
            steps { checkout scm }
        }

        stage('Build') {
            steps {
                sh 'mvn clean compile -Pci'
            }
        }

        stage('Unit Tests') {
            steps {
                sh 'mvn test -Pci'
                junit 'target/surefire-reports/**/*.xml'
            }
        }

        stage('Integration Tests') {
            steps {
                sh 'mvn failsafe:integration-test failsafe:verify -Pci -DskipITs=false'
                junit 'target/failsafe-reports/**/*.xml'
            }
        }

        stage('Package') {
            steps {
                sh 'mvn package -DskipTests -Pci'
                archiveArtifacts 'target/*.jar'
            }
        }
    }

    post {
        always {
            echo "Build ${currentBuild.result}"
        }
    }
}
