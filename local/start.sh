#! /bin/sh


export LIBS=./libs
export APP=com.egoebelbecker.jaegertutorial.TutorialApplication
export LOG_DIR=${APP_HOME}/logs
export CLASSPATH=${CLASSPATH}:${LIBS}/*
java -enableassertions -cp ${CLASSPATH} ${LOGBACKCFG} ${APP}

