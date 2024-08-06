package ru.step.smartcontrol.telegram.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;


/**
 * Настройка сваггера.
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Value("${swagger.server-port}")
    private int serverPort;

    @Value("${swagger.server-host}")
    private String serverHost;

    /**
     * Задает настройки запуска свагера
     *
     * @return инстанс настройки свагера
     */
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .host(serverHost + ":" + serverPort)
                .select()
                .apis(RequestHandlerSelectors.basePackage("ru.step.smartcontrol.telegram"))
                .paths(PathSelectors.any())
                .build();
    }




}
