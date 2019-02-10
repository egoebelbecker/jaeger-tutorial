#! /bin/sh


export LIBS=/app/JaegerTutorial/libs
export APP=com.egoebelbecker.jaegertutorial.TutorialApplication
export LOG_DIR=${APP_HOME}/logs
export CLASSPATH=${CLASSPATH}:${LIBS}/*
${JAVA_HOME}/bin/java -enableassertions -cp ${CLASSPATH} ${LOGBACKCFG} ${APP}

