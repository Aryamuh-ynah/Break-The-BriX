import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class HighScoreManager {
    private final File highScoreFile;

    public HighScoreManager() {
        highScoreFile = new File(GameConfig.HIGH_SCORE_FILE);
    }

    public int loadHighScore() {
        if (!highScoreFile.exists()) {
            return 0;
        }

        try (Scanner scanner = new Scanner(highScoreFile)) {
            if (scanner.hasNextInt()) {
                return scanner.nextInt();
            }
        } catch (IOException exception) {
            System.out.println("Could not read high score file.");
        }

        return 0;
    }

    public void saveHighScore(int score) {
        try (FileWriter writer = new FileWriter(highScoreFile)) {
            writer.write(String.valueOf(score));
        } catch (IOException exception) {
            System.out.println("Could not save high score.");
        }
    }
}