package asteroid;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import javax.swing.Timer;

/**
 * Represents a Bullet.
 *
 * @author Daniel Ge
 */
public class Bullet extends GameObject {
    private static final int SIZE = 3;
    private static final double VELOCITY = 10;

    private static int maxBullets = 4;
    private static int numBullets = 0;
    private Timer timer;

    private boolean isOwn;
    
    /**
     * Creates a new Bullet. This constructor can only be invoked using the
     * static methods.
     * 
     * @param x     X-coordinate
     * @param y     Y-coordinate
     * @param theta Direction of the Bullet in radians
     * @param isOwn {@code true} if this Bullet was fired by a SpaceShip, 
     *              {@code false} if it was fired by a HostileObject
     */
    private Bullet (int x, int y, double theta, boolean isOwn) {
        super(x, y, SIZE, SIZE, -1 * VELOCITY * Math.sin(theta),
                VELOCITY * Math.cos(theta), theta);
        this.isOwn = isOwn;

        // Call destroy after the bullet has been active for 700 ms
        timer = new Timer(700, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                destroy();
            }
        });

        numBullets++;
        timer.start();
    }

    /**
     * Create a Bullet (fired by a SpaceShip) at the specified coordinates
     * and direction. If the number of friendly bullets on the screen is equal
     * to the maximum number of bullets, this method returns {@code null}
     * 
     * @param x     X-coordinate
     * @param y     Y-coordinate
     * @param theta Direction of the Bullet in radians
     * @return      A Bullet moving in the direction of {@code theta} at 
     *              {@code (x,y)}, or {@code null} if the number of friendly
     *              bullets already on the screen is at least the maximum number
     *              of friendly Bullets.
     */
    public static Bullet createFriendlyBullet (int x, int y, double theta) {
        if (numBullets >= maxBullets)
            return null;
        return new Bullet (x, y, theta, true);
    }

    /**
     * Create a Bullet (fired by a HostileObject) at the specified coordinates
     * and direction.
     * 
     * @param x     X-coordinate
     * @param y     Y-coordinate
     * @param theta Direction of the Bullet in radians
     * @return      A Bullet moving in the direction of {@code theta} at 
     *              {@code (x,y)}
     */
    public static Bullet createEnemyBullet (int x, int y, double theta) {
        return new Bullet (x, y, theta, false);
    }

    /**
     * Set the maximum number of friendly bullets allowed on the screen.
     * 
     * @param max   The new maximum number.
     */
    public static void setMaxBullets (int max) {
        maxBullets = max;
    }

    /**
     * Determine whether this Bullet was fired by the SpaceShip or HostileObject.
     * 
     * @return  {@code true} if this is a friendly Bullet, {@code false} if not.
     */
    public boolean isFriendly() {
        return isOwn;
    }

    /**
     * Update the location of the GameObject.
     * 
     * @see GameObject#move
     */
    @Override
    public void move() {
        if (!isDestroyed())
            super.move();
    }

    /**
     * Destroy the object. Throws an IllegalStateException if the object is
     * already destroyed.
     *
     * @see GameObject#destroy
     */
    @Override
    public void destroy() {
        if (isFriendly())
            numBullets--;
        timer.stop();
        super.destroy();
    }

    /**
     * Returns the bounding shape for this GameObject.
     *
     * @return  The bounding Shape for this GameObject.
     * @see GameObject#boundingShape
     */
    @Override
    public Shape boundingShape() {
        return new Rectangle2D.Float(x-1, y-1, SIZE, SIZE);
    }

    /**
     * Draw the object to the screen.
     *
     * @param g The Graphics context.
     * @see GameObject#draw
     */
    @Override
    public void draw(Graphics g) {
        if (!isDestroyed()) {
            g.setColor(Color.WHITE);
            g.fillRect(x-1, y-1, SIZE, SIZE);
        }
    }

}
