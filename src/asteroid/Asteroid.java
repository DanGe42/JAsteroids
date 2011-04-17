package asteroid;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Daniel Ge
 */
public class Asteroid extends HostileObject {
    private static final int BIG_SIZE = 57, MED_SIZE = 29, SMALL_SIZE = 15;
    private static final int BIG_SCORE = 20, MED_SCORE = 50, SMALL_SCORE = 100;
    private static final double BIG_VELOCITY = 1.5,
                                MED_VELOCITY = 2.5,
                                SMALL_VELOCITY = 4.0;

    /**
     * Private constructor for the Asteroid. Can only be invoked using the
     * static methods.
     * 
     * @param x         X-coordinate
     * @param y         Y-coordinate
     * @param width     Width of this Asteroid
     * @param height    Height of this Asteroid
     * @param velocityX X-velocity of this Asteroid
     * @param velocityY Y-velocity of this Asteroid
     * @param theta     Orientation of this Asteroid in radians. Used by the
     *                  static methods to determine the velocity vector.
     */
    private Asteroid(int x, int y, int width, int height, double velocityX,
            double velocityY, double theta) {
        super(x, y, width, height, velocityX, velocityY, 0.0);
    }

    /**
     * Creates a big Asteroid of size 57x57 pixels at the specified coordinates
     * and orientation. Velocity angle is determined by the angle.
     * 
     * @param x     X-coordinate
     * @param y     Y-coordinate
     * @param angle Direction of the Asteroid.
     * @return      A big Asteroid.
     */
    public static Asteroid createBigAsteroid
            (int x, int y, double angle) {
        
        return new Asteroid(x, y, BIG_SIZE, BIG_SIZE, 
                BIG_VELOCITY * Math.sin(angle) + 0.5,
                BIG_VELOCITY * Math.cos(angle) + 0.5, angle);
    }

    /**
     * Creates a medium Asteroid of size 29x29 pixels at the specified 
     * coordinates and orientation. Velocity angle is determined by the angle.
     * 
     * @param x     X-coordinate
     * @param y     Y-coordinate
     * @param angle Direction of the Asteroid.
     * @return      A medium Asteroid.
     */
    public static Asteroid createMediumAsteroid
            (int x, int y, double angle) {

        double velocity = (Math.random() * (MED_VELOCITY - 1.0)) + 1.0;
        return new Asteroid(x, y, MED_SIZE, MED_SIZE,
                velocity * Math.sin(angle),
                velocity * Math.cos(angle), angle);
    }

    /**
     * Creates a small Asteroid of size 15x15 pixels at the specified 
     * coordinates and orientation. Velocity angle is determined by the angle.
     * 
     * @param x     X-coordinate
     * @param y     Y-coordinate
     * @param angle Direction of the Asteroid.
     * @return      A small Asteroid.
     */
    public static Asteroid createSmallAsteroid
            (int x, int y, double angle) {

        double velocity = (Math.random() * (SMALL_VELOCITY - 1.0)) + 1.0;
        return new Asteroid(x, y, SMALL_SIZE, SMALL_SIZE,
                velocity * Math.sin(angle),
                velocity * Math.cos(angle), angle);
    }

    /**
     * Return the smaller asteroids that result from destroying this Asteroid.
     * If this is small Asteroid, this method returns {@code null}
     *
     * @return  Smaller asteroids resulting from destroying this Asteroid. If
     *          this is a small Asteroid, return {@code null}
     * @throws IllegalStateException    if this Asteroid is not destroyed yet
     */
    public Set<Asteroid> breakAsteroid() {
        if (!isDestroyed())
            throw new IllegalStateException("Asteroid must be destroyed first");

        Set<Asteroid> aList = new HashSet<Asteroid>(2);
        // Create two medium Asteroids upon destruction of a big one
        if (WIDTH == BIG_SIZE) {
            aList.add(Asteroid.createMediumAsteroid(x, y, GameObject.generateAngle()));
            aList.add(Asteroid.createMediumAsteroid(x, y, GameObject.generateAngle()));
            return aList;
        }

        // Create two small Asteroids upon destruction of a medium one
        if (WIDTH == MED_SIZE) {
            aList.add(Asteroid.createSmallAsteroid(x, y, generateAngle()));
            aList.add(Asteroid.createSmallAsteroid(x, y, generateAngle()));
            return aList;
        }

        // Destroy the Asteroid completely if it's small.
        return null;
    }

    /**
     * Returns the score that would be gained for destroying this object.
     *
     * @return  The score for this HostileObject.
     */
    public int getScore() {
        switch (getSize()){
            case BIG_SIZE: return BIG_SCORE;
            case MED_SIZE: return MED_SCORE;
            case SMALL_SIZE: return SMALL_SCORE;
            default: throw new IllegalStateException("asteroid is not of "
                    + "standard size");
        }
    }

    /**
     * Return the width of this Asteroid.
     *
     * @return  The width (or height) of this Asteroid.
     */
    private int getSize() {
        switch (super.WIDTH) {
            case BIG_SIZE: return BIG_SIZE;
            case MED_SIZE: return MED_SIZE;
            case SMALL_SIZE: return SMALL_SIZE;
            default: throw new IllegalStateException("asteroid is not of "
                    + "standard size");
        }
    }

    /**
     * Returns the bounding shape for this GameObject.
     *
     * @return  The bounding Shape for this GameObject.
     */
    @Override
    public Shape boundingShape() {
        return new Rectangle2D.Float (x-WIDTH/2, y-WIDTH/2, WIDTH, WIDTH);
    }

    /**
     * Draw the object to the screen.
     *
     * @param g The Graphics context.
     */
    @Override
    public void draw(Graphics g) {
        if (!isDestroyed()) {
            g.setColor(Color.WHITE);
            g.drawRect(x-WIDTH/2, y-WIDTH/2, WIDTH, WIDTH);
        }
    }

}
