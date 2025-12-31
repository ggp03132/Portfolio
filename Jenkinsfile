pipeline {
    agent {
        kubernetes {
            yaml '''
apiVersion: v1
kind: Pod
metadata:
  labels:
    jenkins: agent
spec:
  serviceAccountName: jenkins
  containers:
  - name: gradle
    image: gradle:8.5-jdk17
    command:
    - cat
    tty: true
    volumeMounts:
    - name: gradle-cache
      mountPath: /home/gradle/.gradle
  - name: docker
    image: docker:24-dind
    command:
    - dockerd
    - --host=unix:///var/run/docker.sock
    - --host=tcp://0.0.0.0:2375
    securityContext:
      privileged: true
    env:
    - name: DOCKER_TLS_CERTDIR
      value: ""
    volumeMounts:
    - name: docker-graph-storage
      mountPath: /var/lib/docker
  - name: kubectl
    image: alpine/k8s:1.28.0
    command:
    - cat
    tty: true
  volumes:
  - name: gradle-cache
    emptyDir: {}
  - name: docker-graph-storage
    emptyDir: {}
'''
        }
    }
    
    environment {
        IMAGE_NAME = 'lms-backend'
        IMAGE_TAG = "${BUILD_NUMBER}"
        NAMESPACE = 'lms'
    }
    
    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        
        stage('Build') {
            steps {
                container('gradle') {
                    sh '''
                        echo "=== Building LMS Backend ==="
                        gradle clean bootJar --no-daemon
                        ls -lh build/libs/
                    '''
                }
            }
        }
        
        stage('Docker Build') {
            steps {
                container('docker') {
                    sh '''
                        echo "=== Building Docker Image ==="
                        
                        # Wait for Docker daemon to be ready
                        timeout 60 sh -c 'until docker info; do sleep 1; done'
                        
                        docker build -t ${IMAGE_NAME}:${IMAGE_TAG} .
                        docker tag ${IMAGE_NAME}:${IMAGE_TAG} ${IMAGE_NAME}:latest
                        docker images | grep ${IMAGE_NAME}
                    '''
                }
            }
        }
        
        stage('Deploy to Kubernetes') {
            steps {
                container('kubectl') {
                    sh '''
                        echo "=== Deploying to Kubernetes ==="
                        
                        # Create namespace if not exists
                        kubectl get namespace ${NAMESPACE} || kubectl create namespace ${NAMESPACE}
                        
                        # Apply ConfigMap and Secret
                        kubectl apply -f - <<EOFCONFIG
apiVersion: v1
kind: ConfigMap
metadata:
  name: lms-config
  namespace: ${NAMESPACE}
data:
  DB_URL: "jdbc:postgresql://172.26.9.71:5432/lmsdb"
  DB_USERNAME: "lms"
---
apiVersion: v1
kind: Secret
metadata:
  name: lms-secret
  namespace: ${NAMESPACE}
type: Opaque
stringData:
  DB_PASSWORD: "lms123456"
EOFCONFIG
                        
                        # Update deployment
                        kubectl apply -f - <<EOFDEPLOY
apiVersion: apps/v1
kind: Deployment
metadata:
  name: lms-backend
  namespace: ${NAMESPACE}
spec:
  replicas: 2
  selector:
    matchLabels:
      app: lms-backend
  template:
    metadata:
      labels:
        app: lms-backend
    spec:
      containers:
      - name: lms-backend
        image: ${IMAGE_NAME}:${IMAGE_TAG}
        imagePullPolicy: Never
        ports:
        - containerPort: 8080
          name: http
        env:
        - name: DB_URL
          valueFrom:
            configMapKeyRef:
              name: lms-config
              key: DB_URL
        - name: DB_USERNAME
          valueFrom:
            configMapKeyRef:
              name: lms-config
              key: DB_USERNAME
        - name: DB_PASSWORD
          valueFrom:
            secretKeyRef:
              name: lms-secret
              key: DB_PASSWORD
        - name: SPRING_PROFILES_ACTIVE
          value: "prod"
        resources:
          requests:
            memory: "512Mi"
            cpu: "250m"
          limits:
            memory: "1Gi"
            cpu: "500m"
---
apiVersion: v1
kind: Service
metadata:
  name: lms-backend
  namespace: ${NAMESPACE}
spec:
  type: NodePort
  ports:
  - port: 8080
    targetPort: 8080
    nodePort: 30800
    name: http
  selector:
    app: lms-backend
EOFDEPLOY
                        
                        echo "=== Deployment Status ==="
                        kubectl rollout status deployment/lms-backend -n ${NAMESPACE} --timeout=5m
                        kubectl get pods -n ${NAMESPACE}
                        kubectl get svc -n ${NAMESPACE}
                    '''
                }
            }
        }
    }
    
    post {
        success {
            echo '✅ Pipeline completed successfully!'
            echo 'Application URL: http://54.180.187.186:30800'
        }
        failure {
            echo '❌ Pipeline failed!'
        }
    }
}
