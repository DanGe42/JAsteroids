package asteroid;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import javax.swing.Timer;

/**
 * Represents a space ship in Asteroids.
 *
 * @author Daniel Ge
 */
public class SpaceShip extends GameObject {
    private static final int WIDTH = 15, HEIGHT = 23;

    private int score;
    
    // hsTimer sets up the delay that between the time the SpaceShip disappears
    // and the time it reappears.
    private Timer hsTimer;
    private boolean hyperspace_mode;

    /**
     * Creates a new SpaceShip facing upwards at the specified coordinates.
     * 
     * @param x X-coordinate
     * @param y Y-coordinate
     */
    public SpaceShip (int x, int y) {
        super(x, y, WIDTH, HEIGHT, 0.0, 0.0, Math.PI);
        score = 0;
        
        hyperspace_mode = false;
        hsTimer = new Timer(500, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                hyperspace_helper();
            }
        });
    }

    /**
     * Update the location of the SpaceShip. Also slows down the SpaceShip
     * gradually.
     *
     * @see GameObject#move
     */
    @Override
    public void move() {
        super.move();

        final double DECELERATION = 0.99;
        velocityX *= DECELERATION;
        velocityY *= DECELERATION;
    }

    /**
     * Accelerates the SpaceShip in its current direction.
     */
    public void accelerate() {
        // TODO: Re-implement acceleration
        final double MAX_VELOCITY = 7.0;
        final double ACCELERATION = 0.2;

        // Only accelerate if the velocity is not at the max
        if (Math.sqrt(velocityX*velocityX + velocityY*velocityY)
                <= MAX_VELOCITY) {
            velocityX += ACCELERATION * Math.sin(super.theta) * -1;
            velocityY += ACCELERATION * Math.cos(super.theta);
        }
    }

    /**
     * Rotate the SpaceShip in the direction specified.
     *
     * @param angle {@code -1} to rotate CCW, {@code 1} to rotate CW.
     */
    public void rotate(double angle) {
        final double ROTATE = 2*Math.PI/45;
        super.theta += ((int) Math.signum(angle)) * ROTATE;
    }

    /**
     * Put the SpaceShip into hyperspace, which means warp the SpaceShip to
     * a random location.
     */
    public void hyperspace() {
        hsTimer.start();
        hyperspace_mode = true;
    }

    private void hyperspace_helper() {
        velocityX = velocityY = 0;
        x = (int) (Math.random() * rightBound);
        y = (int) (Math.random() * bottomBound);
        hyperspace_mode = false;
        hsTimer.stop();
    }

    /**
     * Returns whether the SpaceShip is in hyperspace.
     * 
     * @return  {@code true} if this SpaceShip is in hyperspace, {@code false}
     *          if otherwise.
     */
    public boolean inHyperspace() {
        return hyperspace_mode;
    }

    /**
     * Fires Bullets in the current direction in the same spot as the SpaceShip.
     *
     * @return  A Bullet moving the current direction
     */
    public Bullet fire() {
        if (!isDestroyed() && !hyperspace_mode) {
            int xBullet = (-1) * (int) Math.round(11 * Math.sin(theta));
            int yBullet = (int) Math.round(11 * Math.cos(theta));
            return Bullet.createFriendlyBullet(x + xBullet, y + yBullet, theta);
        }
        return null;
    }

    /**
     * Determines whether two objects intersect. Currently uses bounding boxes.
     *
     * @param o The other GameObject
     * @return  {@code true} if the two GameObjects intersect, {@code false} if
     *          otherwise or if the SpaceShip is in hyperspace.
     */
    @Override
    public boolean intersect (GameObject o) {
        if (this.inHyperspace())
            return false;
        return super.intersect(o);
    }

    /**
     * Returns the bounding shape for this GameObject.
     *
     * @return  The bounding Shape for this GameObject.
     */
    @Override
    public Shape boundingShape() {
        return new Rectangle2D.Float(x-7, y-11, WIDTH, HEIGHT);
    }
    
    /**
     * Draw the object to the screen.
     *
     * @param g The Graphics context.
     */
    @Override
    public void draw(Graphics g) {
        // Only draw the SpaceShip if it is not destroyed or in hyperspace.
        if (!isDestroyed() || !hyperspace_mode) {
            Graphics2D g2 = (Graphics2D) g;
            AffineTransform currentAt = g2.getTransform();
            g2.setColor(Color.white);

            // Draw the ship
            int xPoints[] = {x-7, x, x+8};
            int yPoints[] = {y-12, y+11, y-12};
            GeneralPath shipShape = new GeneralPath(GeneralPath.WIND_EVEN_ODD);

            shipShape.moveTo(xPoints[0], yPoints[0]);
            for (int i = 0; i < xPoints.length; i++) {
                shipShape.lineTo(xPoints[i], yPoints[i]);
            }
            shipShape.moveTo(x-5, y-7);
            shipShape.lineTo(x+6, y-7);

            // Rotate the ship
            g2.setTransform(AffineTransform.getRotateInstance(super.theta, super.x,
                    super.y));

            g2.draw(shipShape);
            g2.setTransform(currentAt);
        }
        
    }

    /**
     * Add to the current score.
     *
     * @param x Number to add
     */
    public void addScore (int x) {
        score += x;
    }

    /**
     * Set the score (probably for use once lives are implemented)
     *
     * @param x Number to set score at
     */
    public void setScore (int x) {
        score = x;
    }

    /**
     * Return the current score.
     *
     * @return  The current score.
     */
    public int getScore() {
        return score;
    }
}
