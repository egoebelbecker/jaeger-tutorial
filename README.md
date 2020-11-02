# Jaeger Tutorial

Quick and dirty Spring Boot project with some basic Jaeger tracing enabled.


Use gradle build copyLocal to run from local subdirectory.

Use gradele build copyDeps to run in container. Build image in docker subdirectory.


## Starting Jaeger

```
docker run -d --name jaeger \
  -e COLLECTOR_ZIPKIN_HTTP_PORT=9411 \
  -p 5775:5775/udp \
  -p 6831:6831/udp \
  -p 6832:6832/udp \
  -p 5778:5778 \
  -p 16686:16686 \
  -p 14268:14268 \
  -p 14250:14250 \
  -p 9411:9411 \
  jaegertracing/all-in-one:1.18
```

## Run with Docker on Mac

*Prerequisites*:
- Starting Jaeger (above)

```
$ gradle wrapper
$ gradle build copyDeps
$ docker build -t springboot_jaeger_tutorial .
$ docker run --rm \
    --name springboot_jaeger_tutorial \
    -p 8080:8080 \
    -e JAEGER_AGENT_HOST="host.docker.internal" \
    -e JAEGER_AGENT_PORT=6831 \
    springboot_jaeger_tutorial
```

Navigate to: http://localhost:8080/swagger-ui.html#!/tutorial-controller/getAllEmployeesUsingGET

Click on "Try it out!"

Navigate to: http://localhost:16686

Select service: "jaeger tutorial"

Click on "Find Traces"

