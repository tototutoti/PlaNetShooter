package model.characters;

public class PlayableCharacter {
    private static final float RELATIVE_MAX_SPEED = 0.0025f;
    private static final float RELATIVE_SPEED_GROWTH = RELATIVE_MAX_SPEED/20;
    private static final float RELATIVE_JUMP_STRENGTH = 0.0090f;
    private static final float RELATIVE_WIDTH = 0.05f;
    private static final float RELATIVE_HEIGHT = 0.05f*768f/372f;
    private float relativeX = 0.45f;
    private float relativeY = 0.1f;
    private String name;

    // Default constructor used for reflection (by Kryo serialization)
    private PlayableCharacter() {
    }

    public PlayableCharacter(String name) {
        this();
        this.name = name;
    }

    @Override
    public String toString() {
        return "PlayableCharacter (" +relativeX+ ", " +relativeY+ ", " +RELATIVE_WIDTH+ ", " +RELATIVE_HEIGHT+ ", " +RELATIVE_MAX_SPEED+ ", " +name+ ")";
    }

    public float getRelativeX() {
        return relativeX;
    }

    public float getRelativeY() {
        return relativeY;
    }

    public static float getRelativeWidth() {
        return RELATIVE_WIDTH;
    }

    public static float getRelativeHeight() {
        return RELATIVE_HEIGHT;
    }

    public void setRelativeX(float relativeX) {
        this.relativeX = relativeX;
    }

    public static float getRelativeMaxSpeed() {
        return RELATIVE_MAX_SPEED;
    }

    public void setRelativeY(float relativeY) {
        this.relativeY = relativeY;
    }

    public static float getRelativeSpeedGrowth() {
        return RELATIVE_SPEED_GROWTH;
    }

    public static float getRelativeJumpStrength() {
        return RELATIVE_JUMP_STRENGTH;
    }

    public String getName() {
        return name;
    }
}