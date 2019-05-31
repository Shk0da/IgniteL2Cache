package com.github.shk0da.demo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Arrays;

@Configuration
@EnableSwagger2
@Profile(value = {ProfileConfigConstants.SPRING_PROFILE_SWAGGER})
public class SwaggerConfiguration implements WebMvcConfigurer {

    @Bean
    public Docket api(@Value("${spring.application.name}") String applicationName) {
        return new Docket(DocumentationType.SWAGGER_2)
                .globalOperationParameters(Arrays.asList(
                        new ParameterBuilder()
                                .name("Header 1")
                                .modelRef(new ModelRef("string"))
                                .parameterType("header")
                                .required(false)
                                .defaultValue("1")
                                .build(),
                        new ParameterBuilder()
                                .name("Header 2")
                                .modelRef(new ModelRef("string"))
                                .parameterType("header")
                                .required(true)
                                .defaultValue("1")
                                .build(),
                        new ParameterBuilder()
                                .name("Header 3")
                                .modelRef(new ModelRef("string"))
                                .parameterType("header")
                                .required(true)
                                .defaultValue("1")
                                .build()
                ))
                .apiInfo(new ApiInfo(
                        applicationName,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        ApiInfo.DEFAULT.getVendorExtensions()
                ))
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.github.shk0da.demo.controller"))
                .build();
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addRedirectViewController("/", "/swagger-ui.html");
    }
}
