package view.client.game_frame;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GamePanel extends JPanel {
    private PlatformView[] platforms;
    private CharacterView characterView;
    private final List<CharacterView> otherPlayersViews = new ArrayList<>();

    GamePanel() {
        super();
        this.setBackground(Color.lightGray);
        this.setFocusable(true);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(Color.magenta);
        g.fillRect((int)(HomeView.getRelativeX()*this.getWidth()),
                (int)(HomeView.getRelativeY()*this.getHeight()),
                (int)(HomeView.getRelativeWidth()*this.getWidth()),
                (int)(HomeView.getRelativeHeight()*this.getHeight()));

        for (PlatformView platform : platforms) {
            if (platform != null) {
                g.setColor(Color.blue);
                //Platforms
                g.fillRect((int)(platform.getRelativeX()*this.getWidth()),
                        (int)(platform.getRelativeY()*this.getHeight()),
                        (int)(platform.getRelativeWidth()*this.getWidth()),
                        (int)(platform.getRelativeHeight()*this.getHeight()));
            }
        }

        for (CharacterView otherPlayerView : otherPlayersViews) {
            if (otherPlayerView != null) {
                g.setColor(Color.red);
                g.fillRect((int)(otherPlayerView.getRelativeX()*this.getWidth()),
                        (int)(otherPlayerView.getRelativeY()*this.getHeight()),
                        (int)(otherPlayerView.getRelativeWidth()*this.getWidth()),
                        (int)(otherPlayerView.getRelativeHeight()*this.getHeight()));

                if (otherPlayerView.getNameLabel().getParent() == null) {
                    this.add(otherPlayerView.getNameLabel());
                    this.revalidate();
                }

                otherPlayerView.getNameLabel().setLocation((int)((otherPlayerView.getRelativeX()+otherPlayerView.getRelativeWidth()/2)*this.getWidth()-otherPlayerView.getNameLabel().getWidth()/2), (int)((otherPlayerView.getRelativeY()+otherPlayerView.getRelativeHeight()/2)*this.getHeight())-otherPlayerView.getNameLabel().getHeight()/2);
            }
        }

        if (characterView != null) {
            if (characterView.getCharacterLabel().getParent() == null) {
                this.add(characterView.getCharacterLabel());
            }

            if (characterView.getNameLabel().getParent() == null)
                this.add(characterView.getNameLabel());

            if(characterView.getHorizontal_direction() == -1) {
                characterView.getCharacterLabel().setLocation((int)((characterView.getRelativeX() - characterView.getRelativeWidth() / characterView.scaleWidthCharacter + characterView.getRelativeWidth()) * this.getWidth()), (int) ((characterView.getRelativeY() + 0.01) * this.getHeight()));
            }
            else
                characterView.getCharacterLabel().setLocation((int)(characterView.getRelativeX()*this.getWidth()),(int)((characterView.getRelativeY()+0.01)*this.getHeight()));

            characterView.setScaleWidthCharacter(characterView.getRelativeWidth()*this.getWidth()/200);
            characterView.setScaleHeightCharacter(characterView.getRelativeHeight()*this.getHeight()/200);
            characterView.getNameLabel().setLocation((int)((characterView.getRelativeX()+characterView.getRelativeWidth()/2)*this.getWidth()-characterView.getNameLabel().getWidth()/2), (int)((characterView.getRelativeY()+characterView.getRelativeHeight()/2)*this.getHeight())-characterView.getNameLabel().getHeight()/2);
        }
    }

        public void setPlatformsView(PlatformView[] platforms) {
        this.platforms = platforms;
    }

    public void setEachPlatformView(int i, PlatformView platformView) {
        this.platforms[i] = platformView;
    }

    public void setCharacterView(CharacterView characterView) {
        this.characterView = characterView;
    }

    public List<CharacterView> getOtherPlayersViews() {
        return otherPlayersViews;
    }

    public void addOtherPlayerViewToArray(CharacterView characterView) {
        otherPlayersViews.add(characterView);
    }
}