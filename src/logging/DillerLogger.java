package logging;

import model.Auto;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.time.LocalDateTime;


public class DillerLogger  {
    public enum Level { DEBUG, INFO, WARN, ERROR }

    private final String name;
    private final FileWriter writer;

    public DillerLogger(String name, String filePath) throws IOException {
        this.name = name;
        this.writer = new FileWriter(filePath, true);
    }


    public void log(Auto auto , Level level , String message) {
        String date = LocalDateTime.now().toString();
        String line ="DillerLogger: " +  date +  " " + auto.toString() + " " + level + " " + message;
        try {
            writer.write(line);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
    void close() {
        try {
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
