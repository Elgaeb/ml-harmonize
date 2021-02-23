package com.marklogic;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
//@EnableWebMvc
public class SpringConfiguration implements WebMvcConfigurer {

    @Override
    @Description("Custom Conversion Service")
    public void addFormatters(FormatterRegistry registry) {
        registry.addFormatter(new DurationFormatter());
    }
}