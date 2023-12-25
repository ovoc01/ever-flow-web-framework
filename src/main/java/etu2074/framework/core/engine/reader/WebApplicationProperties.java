package etu2074.framework.core.engine.reader;

import etu2074.framework.utilities.Utilities;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Properties;

public class WebApplicationProperties {
    public static Properties getWebApplicationProperties() throws Exception {
        Properties properties = new Properties();
        InputStream inputStream = new FileInputStream(Utilities.GET_CURRENT_LOCATION()+"/settings/application.properties");
        properties.load(inputStream);
        return properties;
    }
}
