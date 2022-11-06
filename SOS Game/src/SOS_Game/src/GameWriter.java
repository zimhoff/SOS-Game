package SOS_Game.src;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class GameWriter {
    private final String filename;
    private final StringBuilder buffer;
    private FileWriter writer;

    public GameWriter() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        this.filename = String.format("%s.sos", dtf.format(now));
        this.buffer = new StringBuilder();
    }

    public void writeMove(int row, int column, Player player) {
        this.buffer.append(String.format("%s: (%s,%s)\n", player, row, column));
    }

    public void writeMessage(String s) {
        this.buffer.append(s);
    }

    public void clearBuffer() {
        this.buffer.setLength(0);
    }

    public void writeToFile() {
        try {
            File path = new File(String.format("%s/recorded/", System.getProperty("user.dir")));
            File file = new File(path + "/" + filename);
            this.writer = new FileWriter(file);
            this.writer.write(String.valueOf(this.buffer));
            this.writer.flush();
            this.writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void finalize() throws IOException {
        this.writer.flush();
        this.writer.close();
    }
}