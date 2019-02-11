package com.egoebelbecker.jaegertutorial;

import com.egoebelbecker.jaegertutorial.controller.TutorialController;
import io.jaegertracing.internal.JaegerTracer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@SpringBootApplication
@Configuration
public class TutorialApplication {

    public static void main(String[] args) {

        ApplicationContext context = SpringApplication.run(TutorialApplication.class, args);
        log.info("TutorialApplication started");

        TutorialController commandController = context.getBean(TutorialController.class);
        commandController.init();
    }

    @Bean
    public static JaegerTracer getTracer() {
        io.jaegertracing.Configuration.SamplerConfiguration samplerConfig = io.jaegertracing.Configuration.SamplerConfiguration.fromEnv().withType("const").withParam(1);
        io.jaegertracing.Configuration.ReporterConfiguration reporterConfig = io.jaegertracing.Configuration.ReporterConfiguration.fromEnv().withLogSpans(true);
        io.jaegertracing.Configuration config = new io.jaegertracing.Configuration("jaeger tutorial").withSampler(samplerConfig).withReporter(reporterConfig);
        return config.getTracer();
    }



}
