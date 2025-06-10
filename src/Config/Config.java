package Config;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Properties;

public class Config {
    private final Properties config;
    public static final String CONFIG_FILE = "factory.properties";


     public Config() {
        config = new Properties();
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(CONFIG_FILE)) {
           if (is != null) {
               config.load(is);
           } else {
               throw new IllegalStateException("Не найден factory.properties в classpath");
           }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public int getInt(String key, int defaultValue){
         String value =  config.getProperty(key);
         if(value != null){
             return Integer.parseInt(value);

         }
         return defaultValue;
    }

    public boolean getBoolean(String key, boolean defaultValue){
        String value =  config.getProperty(key);
        if(value != null){
            return Boolean.parseBoolean(value);

        }
        return defaultValue;    }
}
