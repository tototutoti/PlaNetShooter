package view.client.game_frame;

public class PlatformView {
    private final float relativeX;
    private final float relativeY;
    private final float relativeWidth;
    private final float relativeHeight;
    public PlatformView(float relativeX, float relativeY, float relativeWidth, float relativeHeight) {
        this.relativeX = relativeX;
        this.relativeY = relativeY;
        this.relativeWidth = relativeWidth;
        this.relativeHeight = relativeHeight;
    }

    float getRelativeX() {
        return relativeX;
    }

    float getRelativeY() {
        return relativeY;
    }

    float getRelativeWidth() {
        return relativeWidth;
    }

    float getRelativeHeight() {
        return relativeHeight;
    }
}