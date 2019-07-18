pipeline {
  agent any
  stages {
    stage('build') {
      agent {
        docker {
          image 'maven:3.5.3-jdk-8-alpine'
          args '-v /root/.m2:/root/.m2'
        }
      }
      steps {
        echo 'Starting build the app.....'
        sh 'rm -rf ~/.m2/settings.xml'
        sh 'wget -P ~/.m2 https://aimeow.oss-cn-hangzhou.aliyuncs.com/settings.xml'
        sh 'mvn package -Dmaven.test.skip=true'
        sh 'ls target'
        sh 'sed -i "s/dl-cdn.alpinelinux.org/mirrors.ustc.edu.cn/g" /etc/apk/repositories'
        sh 'apk add --update --no-cache openssh sshpass'
        script {
            withCredentials(bindings: [usernamePassword(credentialsId: 'server-47.106.155.153', usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD')]) {
                  sh 'sshpass -p $PASSWORD ssh -v -o StrictHostKeyChecking=no $USERNAME@47.106.155.153 rm -rf /root/service/elpida-api'
                  sh 'sshpass -p $PASSWORD ssh -v -o StrictHostKeyChecking=no $USERNAME@47.106.155.153 mkdir /root/service/elpida-api'
                  sh 'sshpass -p $PASSWORD scp -v -o StrictHostKeyChecking=no Dockerfile $USERNAME@47.106.155.153:/root/service/elpida-api/Dockerfile'
                  sh 'sshpass -p $PASSWORD scp -v -o StrictHostKeyChecking=no docker-compose.yml $USERNAME@47.106.155.153:/root/service/elpida-api/docker-compose.yml'
                  sh 'sshpass -p $PASSWORD scp -v -o StrictHostKeyChecking=no -r ./target/* $USERNAME@47.106.155.153:/root/service/elpida-api/'

                  sh 'sshpass -p $PASSWORD ssh -v -o StrictHostKeyChecking=no $USERNAME@47.106.155.153 docker-compose -f /root/service/elpida-api/docker-compose.yml down'
                  sh 'sshpass -p $PASSWORD ssh -v -o StrictHostKeyChecking=no $USERNAME@47.106.155.153 docker build --no-cache -t elpida-api /root/service/elpida-api/.'
                  sh 'sshpass -p $PASSWORD ssh -v -o StrictHostKeyChecking=no $USERNAME@47.106.155.153 docker-compose -f /root/service/elpida-api/docker-compose.yml up -d'
                  bearychatSend 'elpida-api正式环境发布成功，api地址为https://elpida-api.aimeow.com/ ，开瓶红酒庆祝啊~'
                }
         }

      }
    }
  }
}
