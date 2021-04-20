package at.timeguess.backend.configs;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Configuration;

/**
 * Spring configuration for servlet context.
 */
@Configuration
public class CustomServletContextInitializer implements ServletContextInitializer {

    @Override
    public void onStartup(ServletContext sc) throws ServletException {
        sc.setInitParameter("com.sun.faces.forceLoadConfiguration", "true");
        sc.setInitParameter("javax.faces.DEFAULT_SUFFIX", ".xhtml");
        sc.setInitParameter("javax.faces.PROJECT_STAGE", "Development");
        sc.setInitParameter("javax.faces.STATE_SAVING_METHOD", "server");
        sc.setInitParameter("javax.faces.FACELETS_SKIP_COMMENTS", "true");
        // websockets configuration
        sc.setInitParameter("javax.faces.ENABLE_CDI_RESOLVER_CHAIN", "true");
        sc.setInitParameter("org.omnifaces.SOCKET_ENDPOINT_ENABLED", "true");
        // set timezone for all components to system default
        sc.setInitParameter("javax.faces.DATETIMECONVERTER_DEFAULT_TIMEZONE_IS_SYSTEM_TIMEZONE", "true");
        // don't get empty string for null values from ui to save in db
        sc.setInitParameter("javax.faces.INTERPRET_EMPTY_STRING_SUBMITTED_VALUES_AS_NULL", "true");
        // enable gui features
        sc.setInitParameter("primefaces.FONT_AWESOME", "true");
    }
}
