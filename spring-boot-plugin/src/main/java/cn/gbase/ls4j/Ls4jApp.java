package cn.gbase.ls4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@SpringBootApplication
//implements CommandLineRunner
public class Ls4jApp  {

    @Autowired
    Config config;

    public static void main(String[] args) {
        SpringApplication.run(Ls4jApp.class, args);
    }
//    @Override
//    public void run(String... args) throws Exception {
//
//        System.out.println(config.getString("ls4j.user"));
//    }


    @Bean
    public static PropertySourcesPlaceholderConfigurer placeholderConfigurer() {

        PropertySourcesPlaceholderConfigurer c = new PropertySourcesPlaceholderConfigurer();
        c.setIgnoreUnresolvablePlaceholders(true);
        return c;
    }


    /**
     @Bean
     MeterRegistryCustomizer<MeterRegistry> metricsCommonTags() {
     return registry -> registry.config().commonTags("application", "springboot-actuator-prometheus-demo");
     }
     */
    @ComponentScan
    @Configuration
    public static class CorsConfig {
        private CorsConfiguration buildConfig() {
            CorsConfiguration corsConfiguration = new CorsConfiguration();

            corsConfiguration.addAllowedOrigin("*");
            corsConfiguration.addAllowedHeader("*");
            corsConfiguration.addAllowedMethod("*");
            return corsConfiguration;
        }

        @Bean
        public CorsFilter corsFilter() {
            UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
            // 配置所有请求
            source.registerCorsConfiguration("/**", buildConfig());
            return new CorsFilter(source);
        }
    }
}
