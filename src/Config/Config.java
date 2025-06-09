package Config;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Properties;

public class Config {
    private final Properties config;

     public Config() {
        config = new Properties();
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("factory.properties")) {
           if (is != null) {
               config.load(is);
           } else {
               throw new IllegalStateException("Не найден factory.properties в classpath");
           }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    int getInt(String key, int defaultValue){
        return Integer.parseInt(config.getProperty(key));
    }

    boolean getBoolean(String key, boolean defaultValue){
        return Boolean.parseBoolean(config.getProperty(key));
    }
}
