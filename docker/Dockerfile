FROM anapsix/alpine-java:8_server-jre
MAINTAINER Eric Goebelbecker "eric@ericgoebelbecker.com"
ENV JAVA_VERSION 8u31
ENV PACKAGE_ROOT /app
ADD app /app/
ENTRYPOINT ["/app/JaegerTutorial/bin/start.sh"]
CMD []