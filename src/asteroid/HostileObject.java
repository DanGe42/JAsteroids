package asteroid;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents a GameObject set out to destroy the SpaceShip
 *
 * @author Daniel Ge
 */
public abstract class HostileObject extends GameObject {

    // Global list of HostileObjects. Used primarily to determine how many
    // HostileObjects are left on the screen.
    private static Set<HostileObject> hostileList =
            new HashSet<HostileObject>();

    /**
     * Creates a new HostileObject with the specified parameters. Also adds
     * the HostileObject to the global list of HostileObjects.
     *
     * @param x         X-coordinate
     * @param y         Y-coordinate
     * @param width     Width of the HostileObject
     * @param height    Height of the HostileObject
     * @param velocityX X-velocity of the HostileObject
     * @param velocityY Y-velocity of the HostileObject
     * @param theta     Orientation of the HostileObject in radians
     * @see GameObject#GameObject
     */
    public HostileObject(int x, int y, int width, int height, double velocityX,
            double velocityY, double theta) {
        super(x, y, width, height, velocityX, velocityY, theta);
        addToHostileList();
    }

    /**
     * Add this object to the global list.
     */
    private void addToHostileList() {
        hostileList.add(this);
    }

    /**
     * Returns the size of the HostileObject global list.
     *
     * @return  The size of the HostileObject global list
     */
    public static int hostileSize() {
        return hostileList.size();
    }

    /**
     * Determines whether the global list is empty.
     *
     * @return  {@code true} if the list is empty, {@code false} if not
     */
    public static boolean listEmpty() {
        return hostileSize() == 0;
    }

    /**
     * Clear the HostileObject global list.
     */
    public static void clearList() {
        hostileList = new HashSet<HostileObject>();
    }

    /**
     * Returns the score that would be gained for destroying this object.
     *
     * @return  The score for this HostileObject.
     */
    public abstract int getScore();

    /**
     * Destroy the object. Throws an IllegalStateException if the object is
     * already destroyed. Also removes this HostileObject from the global list.
     *
     * @see GameObject#destroy
     */
    @Override
    public void destroy() {
        super.destroy();
        hostileList.remove(this);
    }
}
