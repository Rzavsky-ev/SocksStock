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

        stage('Build & Test') {
            steps {
                sh 'mvn clean test -Pci'
                junit 'target/surefire-reports/**/*.xml'
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