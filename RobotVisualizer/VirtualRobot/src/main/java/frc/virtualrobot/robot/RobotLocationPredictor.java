package frc.virtualrobot.robot;

/**
 * WARNING: This class is not accurate! Because one wheel's rotation is
 * applied first, the prediction will slowly drift to one side, even if
 * it's actually going perfectly straight.
 * Not going to fix because competition robot will use swivel drive
 *
 * A class that predicts the location and rotation of a robot based on
 * the rotations of the wheels.
 * <p>
 * The position and rotation are relative to the starting position of
 * the robot.
 * <p>
 * All measurements of distance are in centimeters, and all measurements
 * of rotation are in radians.
 * <p>
 * <b>IMPORTANT: The robot rotation is clockwise!</b>
 *
 * @author Ryan Heuer
 */
public class RobotLocationPredictor {
    private static final double TWO_PI = 2 * Math.PI;

    private final double halfWheelSpacing;
    private final double wheelRadius;
    private final double wheelSpacingCirc;

    private double x;
    private double y;
    private double rotation;

    private double lastLeft;
    private double lastRight;

    /**
     * Creates a new RobotLocationPredictor based on the measurements of
     * the robot.
     *
     * @param wheelSpacing the spacing between the centers of the wheels
     * @param wheelRadius  the radius of the wheels
     */
    public RobotLocationPredictor(double wheelSpacing, double wheelRadius) {
        this.halfWheelSpacing = wheelSpacing / 2.0;
        this.wheelRadius = wheelRadius;
        this.wheelSpacingCirc = wheelSpacing * TWO_PI;

        this.x = 0;
        this.y = 0;
        this.rotation = 0;

        this.lastLeft = 0;
        this.lastRight = 0;
    }

    /**
     * Updates the predicted location of the robot using the rotations
     * of the wheels.
     * <p>
     * This method should be called as frequently as possible to ensure
     * the prediction is as accurate as possible.
     *
     * @param left  the rotation of the left wheel
     * @param right the rotation of the right wheel
     */
    public void updateRotations(double left, double right) {
        double leftDelta = left - lastLeft;
        double rightDelta = right - lastRight;
        lastLeft = left;
        lastRight = right;

        if (leftDelta == 0 && rightDelta == 0) {
            return; // If nothing has changed, no reason to continue
        }

        double sin = Math.sin(rotation);
        double cos = Math.cos(rotation);

        double lwX = cos * halfWheelSpacing;
        double lwY = sin * halfWheelSpacing;
        double rwX = cos * -halfWheelSpacing;
        double rwY = sin * -halfWheelSpacing;

        rotateAround(lwX, lwY, getWheelRotationAngle(leftDelta));
        rotateAround(rwX, rwY, getWheelRotationAngle(rightDelta));
    }

    /**
     * Gets the predicted X position of the robot.
     *
     * @return x position
     */
    public double getX() {
        return x;
    }

    /**
     * Gets the predicted Y position of the robot.
     *
     * @return y position
     */
    public double getY() {
        return y;
    }

    /**
     * Gets the predicted rotation of the robot.
     *
     * @return rotation angle
     */
    public double getRotation() {
        return rotation;
    }

    /**
     * Sets the position, useful for resetting.
     *
     * @param x new X position
     * @param y new Y position
     */
    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Sets the rotation, useful for resetting.
     *
     * @param rotation new rotation angle
     */
    public void setRotation(double rotation) {
        this.rotation = rotation;
    }

    // Gets the rotation angle around the opposite wheel based on the wheel delta
    private double getWheelRotationAngle(double delta) {
        // Radians are the "amount of radii around the circle", so this is pretty easy
        double edgeMovement = delta * wheelRadius;

        double fraction = edgeMovement / wheelSpacingCirc;
        return fraction * TWO_PI;
    }

    // Rotates the simulated position about a given point
    private void rotateAround(double x, double y, double angle) {
        x += this.x;
        y += this.y;

        this.x -= x;
        this.y -= y;

        double c = Math.cos(angle);
        double s = Math.sin(angle);

        rotation += angle;
        double newX = c * this.x - s * this.y;
        double newY = s * this.x + c * this.y;

        this.x = newX + x;
        this.y = newY + y;
    }
}
