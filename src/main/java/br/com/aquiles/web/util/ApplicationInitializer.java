package br.com.aquiles.web.util;

import br.com.aquiles.core.exception.PropertiesLoaderException;
import br.com.aquiles.core.util.PropertiesUtilities;
import org.apache.log4j.Logger;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.util.Properties;
import java.util.Set;

/**
 * Created by rlanhellas on 08/05/2017.
 */
public class ApplicationInitializer implements ServletContainerInitializer {

    private Logger logger = Logger.getLogger(ApplicationInitializer.class);

    @Override
    public void onStartup(Set<Class<?>> set, ServletContext servletContext) throws ServletException {
        try {
            Properties properties = PropertiesUtilities.loadProperties("META-INF/aquiles-web.properties");
            String version = properties.getProperty("version");
            logger.info("Using Aquiles Web ver " + version);
        } catch (PropertiesLoaderException e) {
            logger.error("Error loading Arquiles Web properties. " + e.getMessage());
        }
    }
}
