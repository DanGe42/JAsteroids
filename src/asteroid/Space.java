package asteroid;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.Timer;

/**
 * The Space class is a JPanel that holds the gameplay environment. In addition,
 * at this time, all keystrokes will pass through Space.
 * 
 * @author Daniel Ge
 */
public class Space extends JPanel {
    private static final int WIDTH = 640;
    private static final int HEIGHT = 480;

    private SpaceShip spaceShip;

    // timer and INTERVAL are used together to advance the frame.
    private Timer timer;
    private final int INTERVAL = 25;

    /* These variables are used to determine which keys are pushed. These are
     * used so we can allow for simultaneous keystrokes. In addition, these
     * are used to keep the user from holding down the space bar to fire the
     * maximum number of asteroids at once.
     */
    private boolean leftPressed = false,
                    rightPressed = false,
                    spacePressed = false,
                    upPressed = false;
    private boolean spaceFired = false;

    /* Variables that do not affect the mechanics of the game. These are used
     * to create delays, advance levels, etc.
     */
    private boolean gameStarted, isPaused;
    private int level;
    private boolean generatingLevel;
    private Timer genLevelTimer, shipDestroyedTimer;
    private boolean shipDestroyed;
    private final int MAX_ASTEROIDS = 12;

    /**
     * Create a new Space environment.
     */
    public Space() {
        // Set up environment
        super();
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        // Keeps JFrame.pack() from over-packing
        setBorder(BorderFactory.createEmptyBorder());
        // Allows the JPanel to accept keystrokes.
        this.setFocusable(true);

        // Creates the timer that advances the frame at INTERVAL
        timer = new Timer(INTERVAL, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                tick();
            }
        });

        gameStarted = false;
        shipDestroyed = false;

        // Set up action listeners
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                // New game
                if (e.getKeyCode() == KeyEvent.VK_F2) {
                    play();
                }
                // Pause
                else if (e.getKeyCode() == KeyEvent.VK_F3) {
                    if (gameStarted)
                        pause();
                }
            }
        });
        
        /* If user clicks on JMenuBar or outside the GUI, pause the game
         * automatically
         */
        addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                if (gameStarted && !isPaused)
                    pause();
            }
        });
    }

    /**
     * Sets up the initial interactive gameplay environment in Space and begins
     * the game.
     */
    public void play() {
        gameStarted = true;
        isPaused = false;
        shipDestroyed = false;
        level = 1;
        GameObject.resetList();
        spaceShip = new SpaceShip(WIDTH/2, HEIGHT/2);
        spaceShip.addToGlobalList();
        GameObject.setBounds(WIDTH, HEIGHT);
        HostileObject.clearList();

        // Key listeners to control the SpaceShip.
        addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                // Rotate CCW
                if (e.getKeyCode() == KeyEvent.VK_LEFT)
                    leftPressed = true;
                // Rotate CW
                else if (e.getKeyCode() == KeyEvent.VK_RIGHT)
                    rightPressed = true;
                // Accelerate forwards
                else if (e.getKeyCode() == KeyEvent.VK_UP)
                    upPressed = true;
                // Fire bullets
                else if (e.getKeyCode() == KeyEvent.VK_SPACE)
                    spacePressed = true;
                // Go hyperspace!
                else if (e.getKeyCode() == KeyEvent.VK_SHIFT)
                    spaceShip.hyperspace();
            }

            public void keyReleased(KeyEvent e) {
                // Disallows the user from firing multiple bullets by holding
                // down the spacebar.
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    spacePressed = false;
                    spaceFired = false;
                }
                else if (e.getKeyCode() == KeyEvent.VK_UP)
                    upPressed = false;
                else if (e.getKeyCode() == KeyEvent.VK_LEFT)
                    leftPressed = false;
                else if (e.getKeyCode() == KeyEvent.VK_RIGHT)
                    rightPressed = false;
            }
        });

        /* Creates a timer that delays the creation of new asteroids (advancing
         * the level) for a second after all asteroids are destroyed.
         */
        genLevelTimer = new Timer(1000, new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    nextLevel();
                }
        });

        /* Creates a timer that delays the display of the GAME OVER message
         * after the SpaceShip is destroyed.
         */
        shipDestroyedTimer = new Timer(1000, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                shipDestroyed = true;
                shipDestroyedTimer.stop();
            }
        });

        timer.start();
        generateAsteroids(level);
        generatingLevel = false;
    }

    /**
     * Sets up the next frame and also watches out for non-movement changes
     * for certain objects.
     */
    private void tick() {
        performKeyAction();
        globalUpdate();

        // Delays the advancement into the next level.
        if (HostileObject.listEmpty() && !spaceShip.isDestroyed() &&
                !generatingLevel) {
            System.out.println("Advancing level");
            generatingLevel = true;
            genLevelTimer.start();
        }

        // Delays display of GAME OVER message
        if (spaceShip.isDestroyed() && !shipDestroyedTimer.isRunning() &&
                !shipDestroyed) {
            shipDestroyedTimer.start();
        }
        
        repaint();
    }

    /**
     * Performs actions based on which keys are pushed down. Allows for
     * simultaneous key-presses.
     */
    private void performKeyAction() {
        if (leftPressed)
            spaceShip.rotate(-1);
        if (rightPressed)
            spaceShip.rotate(1);
        if (upPressed)
            spaceShip.accelerate();
        if (spacePressed) {
            if (!spaceFired) {
                Bullet b = spaceShip.fire();
                if (b != null)
                    b.addToGlobalList();
            }
            spaceFired = true;
        }
        repaint();
    }
    
    /**
     * For each GameObject, update its position and then destroy them as
     * necessary.
     */
    private void globalUpdate() {
        Iterator<GameObject> iter = GameObject.objList.iterator();
        Set<Asteroid> brokenAsteroids = new HashSet<Asteroid>();
        
        // Look for collisions and set GameObjects for destruction as necessary.
        while (iter.hasNext()) {
            GameObject element = iter.next();

            // Collisions between a target and a Bullet destroys both depending
            // on the circumstances.
            if (element instanceof Bullet && !element.isDestroyed()) {
                Bullet bul = (Bullet) element; // This renaming is for clarity
                                               // so I that I know the current
                                               // element is a bullet.

                /* If a bullet collides with an Asteroid or SpaceShip, depending
                 * on the circumstances, mark it for destruction if it has not
                 * already been done.
                 */
                for (GameObject target : GameObject.objList) {
                    if (!target.isDestroyed() && bul.intersect(target)) {
                        if (target instanceof Asteroid) {
                            target.destroy();
                            bul.destroy();

                            // Broken Asteroids do not automatically get added
                            // to the global list, so we collect them here.
                            Set<Asteroid> temp = 
                                    ((Asteroid) target).breakAsteroid();
                            if (temp != null)
                                brokenAsteroids.addAll(temp);

                            /* In the future, after saucers will be implemented,
                             * only collisions from bullets fired by the
                             * SpaceShip will add to the score.
                             */
                            if (bul.isFriendly())
                                spaceShip.addScore(((Asteroid) target).getScore());

                            break;
                        }

                        // Destroy the SpaceShip if a bullet was fired by an
                        // enemy.
                        if (target instanceof SpaceShip && !bul.isFriendly()) {
                            target.destroy();
                            bul.destroy();

                            break;
                        }
                    }
                }
            }

            /*
             * Any collisions with the SpaceShip will destroy the SpaceShip.
             */
            if (element instanceof SpaceShip && !element.isDestroyed()) {
                SpaceShip ship = (SpaceShip) element; // Again, this is for clarity
                for (GameObject hostile : GameObject.objList) {
                    if (hostile instanceof HostileObject && 
                            !hostile.isDestroyed() && hostile.intersect(ship)) {
                        hostile.destroy();
                        ship.destroy();

                        if (hostile instanceof Asteroid) {
                            Set<Asteroid> temp = 
                                    ((Asteroid) hostile).breakAsteroid();
                            if (temp != null)
                                brokenAsteroids.addAll(temp);
                        }

                        // Since collisions with SpaceShips destroy the
                        // HostileObject, we will add the score.
                        ship.addScore(((HostileObject) hostile).getScore());
                        break;
                    }
                }
            }
            
        }

        // Add the new broken asteroids to the global set of objects
        GameObject.objList.addAll(brokenAsteroids);

        // Remove destroyed objects and update the movements of the rest
        iter = GameObject.objList.iterator();
        while (iter.hasNext()) {
            GameObject g = iter.next();
            if (g.isDestroyed()) {
                iter.remove();
            }
            else {
                g.move();
            }
        }
    }

    /**
     * Create new Asteroids in a fixed radius around the SpaceShip. The number
     * of asteroids is based on what level it is.
     *
     * @param lvl   the next level
     */
    private void generateAsteroids (int lvl) {
        int numAsteroids = lvl + 3;
        if (numAsteroids > MAX_ASTEROIDS)
            numAsteroids = MAX_ASTEROIDS;

        final int DIAMETER = WIDTH;

        // Messy logic for generating randomly the positions for the asteroids
        // in a circle around the SpaceShip. I think I might have been high when
        // I wrote this.
        for (int n = 0; n < numAsteroids; n++) {
            int x_init = ((int)(Math.random() * DIAMETER) - 200) + spaceShip.x;
            if (x_init < 0)
                x_init += WIDTH;
            if (x_init > WIDTH)
                x_init -= WIDTH;
            
            int y_sign = (int) (Math.random() * 2);
            if (y_sign == 0)
                y_sign = -1;
            else
                y_sign = 1;

            int y_init = spaceShip.y + y_sign * 
                    (int)(Math.sqrt((DIAMETER * DIAMETER)/4 -
                    (x_init - spaceShip.x)*(x_init - spaceShip.x)));
            if (y_init < 0)
                y_init += HEIGHT;
            if (y_init > HEIGHT)
                y_init -= HEIGHT;

            Asteroid.createBigAsteroid(x_init, y_init,
                    GameObject.generateAngle()).addToGlobalList();
        }
    }

    /**
     * Pause the game.
     */
    private void pause() {
        if (!isPaused) {
            isPaused = true;
            timer.stop();
            repaint();
        }
        else {
            isPaused = false;
            timer.start();
        }
    }

    /**
     * Advance the level and generate a new environment for it.
     * This happens at the end of the level delay.
     */
    private void nextLevel() {
        System.out.println("Level advance");
        level++;
        generateAsteroids(level);
        generatingLevel = false;
        genLevelTimer.stop();
    }

    /**
     * See documentation in Java 6 SE API reference. (probably in java.awt)
     * @return  The preferred size of this JPanel
     */
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(WIDTH, HEIGHT);
    }

    /**
     * Repaint the JPanel.
     *
     * @param g The Graphics context.
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        this.setBackground(Color.black);
        Graphics2D g2d = (Graphics2D) g;

        // Only paint stuff if the game has been started before.
        if (gameStarted) {
            for (GameObject go : GameObject.objList) {
                // A SpaceShip in hyperspace disappears from the screen for a
                // bit before it reappears at another location.
                if (!(go instanceof SpaceShip && ((SpaceShip) go).inHyperspace()))
                    go.draw(g);
            }

            g2d.setColor(Color.WHITE);

            // Draw the score
            String scoreText = String.valueOf(spaceShip.getScore());
            Font original = g2d.getFont();
            g2d.setFont(new Font("Dialog", Font.BOLD, 20));
            FontMetrics scoreMetrics = g2d.getFontMetrics();
            int scoreHeight = scoreMetrics.getHeight();
            g2d.drawString(scoreText, 100, scoreHeight);

            // Draw PAUSED
            if (isPaused) {
                g2d.setFont(new Font("Dialog", Font.PLAIN, 30));
                g2d.setColor(Color.RED);
                String pauseText = "PAUSED";

                // get metrics from the graphics
                FontMetrics pauseMetrics = g2d.getFontMetrics();
                // get the height of a line of text in this font and render context
                int pauseHeight = pauseMetrics.getHeight();
                // get the advance of my text in this font and render context
                int pauseAdv = pauseMetrics.stringWidth(pauseText);

                // Draw the string in the center
                g2d.drawString(pauseText, WIDTH/2-pauseAdv/2, HEIGHT/2-pauseHeight/2);
                g2d.setFont(original);
            }

            // Draw GAME OVER
            if (shipDestroyed) {
                g2d.setFont(new Font("Dialog", Font.PLAIN, 30));
                String loseText = "GAME OVER";

                FontMetrics loseMetrics = g2d.getFontMetrics();
                int loseAdv = loseMetrics.stringWidth(loseText);

                g2d.drawString(loseText, WIDTH/2 - loseAdv/2, 150);
                g2d.setFont(original);
            }
        }
        else {
            String instr1 = "PRESS F2 TO START";
            String instr2 = "Help > Instructions FOR INSTRUCTIONS";

            g2d.setColor(Color.red);
            Font original = g2d.getFont();
            g2d.setFont(new Font("Dialog", Font.PLAIN, 30));

            FontMetrics metrics = g2d.getFontMetrics();
            int instr1_Adv = metrics.stringWidth(instr1);
            int instr2_Adv = metrics.stringWidth(instr2);

            g2d.drawString(instr1, WIDTH/2 - instr1_Adv/2, 150);
            g2d.drawString(instr2, WIDTH/2 - instr2_Adv/2, 300);

            g2d.setFont(original);
        }
    }
}
