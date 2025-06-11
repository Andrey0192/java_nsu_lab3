package Config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {
    public static final String CONFIG_FILE = "factory.properties";
    private final Properties config = new Properties();


     public Config() {
        try (InputStream is = getClass()
                .getClassLoader()
                .getResourceAsStream(CONFIG_FILE)) {
           if (is == null) {
               throw new IllegalStateException("not found " + CONFIG_FILE + " в classpath");
           }
           config.load(is);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public int getInt(String key, int defaultValue){
         String value =  config.getProperty(key);
         if(value == null){
             return defaultValue;
         }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Config key '" + key + "' value='" + value + "' is not a valid int",e);
        }
    }

    public boolean getBoolean(String key, boolean defaultValue){
        String value =  config.getProperty(key);
        if(value == null){
            return defaultValue;
        }
        try {
            return Boolean.parseBoolean(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Config key '" + key + "' value='" + value + "' is not a valid boolean",e);
        }
    }

}

