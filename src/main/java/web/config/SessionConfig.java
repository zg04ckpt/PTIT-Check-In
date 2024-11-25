package web.config;

import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SessionConfig {
    @Bean
    public ServletContextInitializer initializer() {
        return servletContext -> servletContext.setSessionTimeout(120);
    }
}
