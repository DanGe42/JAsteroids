package asteroid;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

/**
 * The main method lives here.
 * @author Daniel Ge
 */
public class Game extends JFrame {

    public Game() {
        final Space space = new Space();
        final InstructionsDialog instDialog = new InstructionsDialog(this, true);
        instDialog.setLocation(100, 100);
        final AboutDialog aboutDialog = new AboutDialog(this, true);
        aboutDialog.setLocation(100, 100);

        // START Create the menu bar //
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem fileMenuExit = new JMenuItem("Exit");
        fileMenuExit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        JMenuItem fileMenuNewGame = new JMenuItem("New Game");
        fileMenuNewGame.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                space.play();
            }
        });
        fileMenu.add(fileMenuNewGame);
        fileMenu.add(fileMenuExit);
        menuBar.add(fileMenu);

        JMenu helpMenu = new JMenu("Help");
        JMenuItem helpMenuInstructions = new JMenuItem("Instructions");
        helpMenuInstructions.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                instDialog.setVisible(true);
            }
        });
        JMenuItem helpMenuAbout = new JMenuItem("About");
        helpMenuAbout.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                aboutDialog.setVisible(true);
            }
        });

        helpMenu.add(helpMenuInstructions);
        helpMenu.add(helpMenuAbout);
        menuBar.add(helpMenu);
        // END Create the menu bar //

        this.add(space);
        this.setJMenuBar(menuBar);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("Asteroids");
        this.setLocation(50, 50);
        this.pack();
        this.setResizable(false);
        this.setVisible(true);
    }

    /**
     * Creates a separate thread to run the GUI.
     * 
     * @param args the command line arguments that are disregarded
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new Game();
            }
        });
    }

    /*
     * Sets the look and feel of the Swing GUI globally. This Swing to use the
     * native window-drawing features of the OS, so it will look more natural.
     *
     * If any exception occurs here, we will just ignore it and let the game
     * run with the default Metal L&F.
     */
    static {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Look and Feel error occurred. Continuing...");
        }
    }
}
