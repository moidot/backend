# run_new_was.sh

#!/bin/bash

# JAVA_HOME과 PATH 설정을 명시적으로 로드
if [ -f /etc/profile.d/jdk.sh ]; then
    source /etc/profile.d/jdk.sh
fi

echo "start run_new_was"
echo "current user: $USER"
echo "JAVA_HOME: $JAVA_HOME"
echo "PATH: $PATH"
shopt -q login_shell && echo "Login shell" || echo "Non-login shell"
PROJECT_ROOT="/home/ec2-user/backend"
JAR_FILE="$PROJECT_ROOT/build/libs/backend-0.0.1-SNAPSHOT.jar"

TARGET_PORT=8080

nohup java -jar -Dspring.profiles.active=prod -Dserver.port=${TARGET_PORT} ${JAR_FILE} > /home/ec2-user/nohup.out 2>&1 &
echo "> Now new WAS runs at ${TARGET_PORT}."
exit 0