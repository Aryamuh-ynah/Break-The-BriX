/*
 * @author Humayra
 */
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        String name = JOptionPane.showInputDialog(null, "Enter your name:");

        if (name != null && !name.trim().isEmpty()) {
            int selectedLevel = JOptionPane.showOptionDialog(
                    null,
                    "Select the level:",
                    "Level Selection",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    new String[]{"Easy", "Medium", "Hard"},
                    "Easy"
            );

            if (selectedLevel != JOptionPane.CLOSED_OPTION) {
                final String playerName = name.trim();
                final int level = selectedLevel + 1;
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        createAndShowGameFrame(playerName, level);
                    }
                });
            } else {
                JOptionPane.showMessageDialog(
                        null,
                        "Invalid level selection. Game cannot start.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        } else {
            JOptionPane.showMessageDialog(
                    null,
                    "Invalid player name. Game cannot start.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private static void createAndShowGameFrame(String name, int selectedLevel) {
        JFrame frame = new JFrame();
        GamePanel gamePanel = new GamePanel(name, selectedLevel);

        frame.setBounds(
                GameConfig.WINDOW_X,
                GameConfig.WINDOW_Y,
                GameConfig.WINDOW_WIDTH,
                GameConfig.WINDOW_HEIGHT
        );
        frame.setTitle("Break The BriX");
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(gamePanel);
        frame.setVisible(true);
    }
}
