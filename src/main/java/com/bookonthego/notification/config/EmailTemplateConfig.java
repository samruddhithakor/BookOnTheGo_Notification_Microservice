// package com.bookonthego.notification.config;

// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.thymeleaf.TemplateEngine;
// import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

// @Configuration
// public class EmailTemplateConfig {
//     @Bean
//     public TemplateEngine templateEngine() {
//         ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
//         resolver.setPrefix("templates/");
//         resolver.setSuffix(".html");
//         resolver.setTemplateMode("HTML");
//         resolver.setCharacterEncoding("UTF-8");
//         TemplateEngine engine = new TemplateEngine();
//         engine.setTemplateResolver(resolver);
//         return engine;
//     }
// }