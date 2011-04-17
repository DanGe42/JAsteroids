package asteroid;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.util.HashSet;
import java.util.Set;

/**
 * GameObject describes the basic framework for every moving object in the
 * gameplay. 
 * 
 * @author Daniel Ge
 */
public abstract class GameObject {
    // right and bottomBound describe the boundaries of the JPanel.
    protected static int rightBound, bottomBound;
    
    public static void setBounds (int right, int bottom) {
        rightBound = right;
        bottomBound = bottom;
    }
    
    // objList is the global list of undestroyed GameObjects currently in Space
    public static Set<GameObject> objList = new HashSet<GameObject>();
    
    /**
     * Add this GameObject to the global list.
     */
    public void addToGlobalList() {
        objList.add(this);
    }

    /**
     * Clear the global list.
     */
    public static void resetList() {
        objList = new HashSet<GameObject>();
    }
    
    
    // All fields and methods that describe the GameObject itself.
    protected int x, y;
    protected final int WIDTH, HEIGHT;
    protected double velocityX, velocityY;
    protected double theta;
    private boolean destroyed;
    
    /**
     * Creates a new GameObject.
     * 
     * @param x             X-coordinate
     * @param y             Y-coordinate
     * @param width         Width of the object (for bounding boxes) (x)
     * @param height        Height of the object (for bounding boxes) (y)
     * @param velocityX     Velocity in the x direction
     * @param velocityY     Velocity in the y direction
     * @param theta         Orientation of the GameObject in radians.
     */
    public GameObject(int x, int y, int width, int height, double velocityX, 
            double velocityY, double theta) {
        this.x = x;
        this.y = y;
        this.WIDTH = width;
        this.HEIGHT = height;
        this.velocityX = velocityX;
        this.velocityY = velocityY;
        this.theta = theta;
        this.destroyed = false;
    }

    /**
     * Update the location of the GameObject.
     */
    public void move() {
        x += velocityX;
        y += velocityY;

        // If the GameObject moves off-window, place it on the other side.
        if (x >= rightBound)
            x -= rightBound;
        if (x <= 0)
            x += rightBound;
        if (y >= bottomBound)
            y -= bottomBound;
        if (y <= 0)
            y += bottomBound;
    }

    /**
     * Destroy the object. Throws an IllegalStateException if the object is
     * already destroyed.
     */
    public void destroy() {
        if (this.destroyed)
            throw new IllegalStateException("object already destroyed");
        this.destroyed = true;
    }

    /**
     * Returns whether the GameObject is destroyed or not.
     *
     * @return  {@code true} if the GameObject is destroyed, {@code false} if
     *          it is not.
     */
    public boolean isDestroyed() {
        return this.destroyed;
    }

    /**
     * Returns the bounding shape for this GameObject.
     *
     * @return  The bounding Shape for this GameObject.
     */
    public abstract Shape boundingShape();

    /**
     * Determines whether two objects intersect. Currently uses bounding boxes.
     *
     * @param o The other GameObject
     * @return  {@code true} if the two GameObjects intersect, {@code false} if
     *          otherwise.
     */
    public boolean intersect(GameObject o) {
        Shape thisShape = this.boundingShape();
        Shape oShape = o.boundingShape();

        // GameObjects cannot collide with a SpaceShip in hyperspace.
        if (o instanceof SpaceShip)
            if (((SpaceShip) o).inHyperspace())
                return false;

        return thisShape.intersects((Rectangle2D) oShape);
    }

    /**
     * Draw the object to the screen.
     *
     * @param g The Graphics context.
     */
    public abstract void draw (Graphics g);

    @Override
    public String toString() {
        return getClass().toString() + " " + isDestroyed();
    }

    /**
     * Global method for generating angles in radians at random.
     *
     * @return  An angle such that [0, 2*PI]
     */
    public static double generateAngle() {
        return Math.random() * 2 * Math.PI;
    }
}
