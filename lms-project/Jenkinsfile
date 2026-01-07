pipeline {
    agent {
        kubernetes {
            yaml """
apiVersion: v1
kind: Pod
spec:
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
    - sh
    - -c
    - |
      mkdir -p /etc/docker
      cat > /etc/docker/daemon.json <<DAEMON_EOF
      {
        "insecure-registries": ["172.26.9.71:5000"]
      }
      DAEMON_EOF
      dockerd --host=unix:///var/run/docker.sock --host=tcp://0.0.0.0:2375
    env:
    - name: DOCKER_TLS_CERTDIR
      value: ""
    securityContext:
      privileged: true
    volumeMounts:
    - name: docker-graph-storage
      mountPath: /var/lib/docker
  - name: kubectl
    image: alpine/k8s:1.28.0
    command:
    - cat
    tty: true
  volumes:
  - name: docker-graph-storage
    emptyDir: {}
  - name: gradle-cache
    emptyDir: {}
  serviceAccountName: jenkins
"""
        }
    }

    environment {
        REGISTRY = '172.26.9.71:5000'
        IMAGE_NAME = 'lms-backend'
        BUILD_NUMBER = "${env.BUILD_NUMBER}"
        DB_HOST = '172.26.9.71'
        DB_PORT = '5432'
        DB_NAME = 'lmsdb'
        DB_USER = 'lms'
        DB_PASSWORD = 'lms123456'
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

        stage('Docker Build & Push') {
            steps {
                container('docker') {
                    sh '''
                        echo "=== Waiting for Docker daemon to be ready ==="
                        for i in $(seq 1 60); do
                            if docker info > /dev/null 2>&1; then
                                echo "Docker daemon is ready"
                                break
                            fi
                            echo "Waiting for Docker daemon... ($i/60)"
                            sleep 1
                        done
                        
                        echo "=== Docker daemon info ==="
                        docker info | grep -A 5 "Insecure"
                        
                        echo "=== Building Docker Image ==="
                        docker build -t ${IMAGE_NAME}:${BUILD_NUMBER} .
                        
                        echo "=== Tagging Image for Registry ==="
                        docker tag ${IMAGE_NAME}:${BUILD_NUMBER} ${REGISTRY}/${IMAGE_NAME}:${BUILD_NUMBER}
                        docker tag ${IMAGE_NAME}:${BUILD_NUMBER} ${REGISTRY}/${IMAGE_NAME}:latest
                        
                        echo "=== Pushing to Registry ==="
                        docker push ${REGISTRY}/${IMAGE_NAME}:${BUILD_NUMBER}
                        docker push ${REGISTRY}/${IMAGE_NAME}:latest
                        
                        echo "=== Verification ==="
                        docker images | grep ${IMAGE_NAME}
                    '''
                }
            }
        }

        stage('Deploy to Kubernetes') {
            steps {
                container('kubectl') {
                    sh """
                        echo "=== Deploying to Kubernetes ==="
                        
                        kubectl get namespace lms || kubectl create namespace lms
                        
                        kubectl delete configmap lms-config -n lms --ignore-not-found=true
                        kubectl delete secret lms-secret -n lms --ignore-not-found=true
                        
                        kubectl create configmap lms-config -n lms \\
                          --from-literal=DB_HOST=${DB_HOST} \\
                          --from-literal=DB_PORT=${DB_PORT} \\
                          --from-literal=DB_NAME=${DB_NAME}
                        
                        kubectl create secret generic lms-secret -n lms \\
                          --from-literal=DB_USER=${DB_USER} \\
                          --from-literal=DB_PASSWORD=${DB_PASSWORD}
                        
                        cat <<DEPLOY_EOF | kubectl apply -f -
apiVersion: apps/v1
kind: Deployment
metadata:
  name: lms-backend
  namespace: lms
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
        image: ${REGISTRY}/${IMAGE_NAME}:${BUILD_NUMBER}
        imagePullPolicy: Always
        ports:
        - containerPort: 8080
        envFrom:
        - configMapRef:
            name: lms-config
        - secretRef:
            name: lms-secret
        livenessProbe:
          httpGet:
            path: /
            port: 8080
          initialDelaySeconds: 60
          periodSeconds: 10
        readinessProbe:
          httpGet:
            path: /
            port: 8080
          initialDelaySeconds: 30
          periodSeconds: 5
---
apiVersion: v1
kind: Service
metadata:
  name: lms-backend
  namespace: lms
spec:
  type: NodePort
  selector:
    app: lms-backend
  ports:
  - port: 8080
    targetPort: 8080
    nodePort: 30800
DEPLOY_EOF
                        
                        kubectl rollout status deployment/lms-backend -n lms --timeout=5m
                        kubectl get pods -n lms
                        kubectl get svc -n lms
                        
                        echo ""
                        echo "✅ Application deployed successfully!"
                        echo "Access: http://13.124.50.47:30800"
                    """
                }
            }
        }
    }

    post {
        success {
            echo '✅ Pipeline completed successfully!'
        }
        failure {
            echo '❌ Pipeline failed!'
        }
    }
}
