import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import model.CollisionDetection;
import model.PlayerCollisionSide;
import model.Terrain;
import model.characters.Direction;
import model.characters.PlayableCharacter;
import model.platforms.Platform;
import network.GameClient;
import network.Network;
import view.client.connection.AskClientName;
import view.client.connection.AskIPHost;
import view.client.connection.ServerFullError;
import view.client.game_frame.GameFrame;
import view.client.game_frame.HomeView;
import view.client.keyboard_actions.PressAction;
import view.client.keyboard_actions.ReleaseAction;
import view.client.game_frame.CharacterView;
import view.client.game_frame.PlatformView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

class MainClient {
    private static String clientName;
    private static GameClient gameClient;
    private static final List<Object> allSolidObjects = new ArrayList<>();
    private static float relativeMovementX = 0f;
    private static float relativeMovementY = 0f;
    private static boolean collisionOnRight = false, collisionOnLeft = false, collisionOnTop = false, collisionOnBottom = false;
    private static boolean jumpKeyJustPressed = false;
    private static GameFrame gameFrame;
    private static PlayableCharacter playableCharacter;
    private static CharacterView characterView;
    private static final String RELEASE_LEFT = "Release.left", RELEASE_RIGHT = "Release.right", PRESS_LEFT = "Press.left", PRESS_RIGHT = "Press.right";
    private static final Set<Direction> directions = new TreeSet<>();
    private static final String OS = System.getProperty("os.name").toLowerCase();
    private static final boolean IS_UNIX_OS = OS.contains("nix") || OS.contains("nux") || OS.contains("aix");
    private static boolean gameServerFull = false;
    private static String serverIP;
    private static float totalDirection = 0;
    private static volatile boolean readyToLaunchGameLoop = false;
    private static final ReleaseAction releaseActionLeft = new ReleaseAction(directions, Direction.LEFT);
    private static final ReleaseAction releaseActionRight = new ReleaseAction(directions, Direction.RIGHT);
    private static boolean readyToFire = false;
    private static long lastShot = 0;

    public static void main(String[] args) {
        launchGameClient();
        if (!gameServerFull) {
            SwingUtilities.invokeLater(MainClient::launchGameFrame);

            while (true)
                if (readyToLaunchGameLoop) break;

            launchGameLoop();
        }
        else
            new ServerFullError();
    }

    private static void launchGameClient() {
        while(true) {
            try {
                serverIP = AskIPHost.getIPHost();
                gameClient = new GameClient(serverIP);
                break;
            } catch (IOException e) {
                System.out.println("No game server found with this IP on the network.");
                AskIPHost.setGoBack(true);
            }
        }

        gameClient.addListener(new Listener() {
            @Override
            public void received(Connection connection, Object object) {
                if (object instanceof Network.RemoveName) {
                    Network.RemoveName removeName = (Network.RemoveName) object;
                    System.out.println("\"" + removeName.name + "\" is disconnected !");

                    if (gameFrame.getGamePanel().getOtherPlayersViews().get(gameClient.registerNameList.getList().indexOf(removeName.name)).getNameLabel().getParent() != null)
                        gameFrame.getGamePanel().remove(gameFrame.getGamePanel().getOtherPlayersViews().get(gameClient.registerNameList.getList().indexOf(removeName.name)).getNameLabel());

                    gameFrame.getGamePanel().getOtherPlayersViews().remove(gameClient.registerNameList.getList().indexOf(removeName.name));
                }
                gameClient.receivedListener(object);
            }

            @Override
            public void disconnected (Connection connection) {
                System.out.println("You are disconnected !\nServer closed.");
                System.exit(1);
            }
        });

        while (true) {
            if (gameClient.getRegisterNameList() != null) {
                if (gameClient.getRegisterNameList().getList().size() == 4) {
                    gameServerFull = true;
                }
                else {
                    AskClientName.setRegisterNameList(gameClient.getRegisterNameList().getList());
                    clientName = AskClientName.getClientName();

                    gameClient.connectedListener(clientName);
                }
                break;
            }
        }
    }

    private static void launchGameFrame() {
        GameFrame.setIsClientAdmin(serverIP.equals("localhost"));

        gameFrame = new GameFrame(clientName);

        gameFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent evt) {
                System.out.println("You are disconnected !");
                System.exit(0);
            }
        });

        defineObjects();

        createKeyMap();

        readyToLaunchGameLoop = true;
    }

    private static void defineObjects() {
        Platform[] platforms = new Platform[Platform.getPlatformNumber()];
        gameFrame.getGamePanel().setPlatformsView(new PlatformView[Platform.getPlatformNumber()]);
        for (int i = 0; i < Platform.getPlatformNumber(); i++) {
            platforms[i] = new Platform();
            gameFrame.getGamePanel().setEachPlatformView(i, new PlatformView(
                    platforms[i].getRelativeX(),
                    platforms[i].getRelativeY(),
                    platforms[i].getRelativeWidth(),
                    platforms[i].getRelativeHeight()));

            allSolidObjects.add(platforms[i]);
        }

        playableCharacter = new PlayableCharacter(clientName);
        characterView = new CharacterView(
                playableCharacter.getRelativeX(),
                playableCharacter.getRelativeY(),
                PlayableCharacter.getRelativeWidth(),
                PlayableCharacter.getRelativeHeight(),
                playableCharacter.getName());

        gameFrame.getGamePanel().setCharacterView(characterView);
    }

    private static void createKeyMap() {
        final InputMap IM = gameFrame.getGamePanel().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        final ActionMap AM = gameFrame.getGamePanel().getActionMap();

        IM.put(KeyStroke.getKeyStroke(KeyEvent.VK_Q, 0, true), RELEASE_LEFT);
        IM.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0, true), RELEASE_LEFT);

        IM.put(KeyStroke.getKeyStroke(KeyEvent.VK_D, 0, true), RELEASE_RIGHT);
        IM.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0, true), RELEASE_RIGHT);

        AM.put(RELEASE_LEFT, releaseActionLeft);
        AM.put(RELEASE_RIGHT, releaseActionRight);

        IM.put(KeyStroke.getKeyStroke(KeyEvent.VK_D, 0, false), PRESS_RIGHT);
        IM.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0, false), PRESS_RIGHT);

        IM.put(KeyStroke.getKeyStroke(KeyEvent.VK_Q, 0, false), PRESS_LEFT);
        IM.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0, false), PRESS_LEFT);

        AM.put(PRESS_LEFT, new PressAction(directions, Direction.LEFT));
        AM.put(PRESS_RIGHT, new PressAction(directions, Direction.RIGHT));

        gameFrame.getGamePanel().addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE || e.getKeyCode() == KeyEvent.VK_UP) {
                    if (collisionOnBottom)
                        jumpKeyJustPressed = true;
                }
                else if (e.getKeyCode() == KeyEvent.VK_E && !(CollisionDetection.isCollisionBetween(playableCharacter, new HomeView()).equals(PlayerCollisionSide.NONE))) {
                    gameFrame.getCardLayout().next(gameFrame.getContentPane());
                }
            }
        });

        gameFrame.getGamePanel().addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                readyToFire = true;
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                readyToFire = false;
            }
        });

        gameFrame.getHomePanel().getBackToGameButton().addActionListener(e -> {
            gameFrame.getCardLayout().next(gameFrame.getContentPane());
            gameFrame.getGamePanel().requestFocus();
        });
    }

    private static void launchGameLoop() {
        int[] fpsRecord = new int[1];
        fpsRecord[0] = -1;
        String gameFrameTitleWithoutFPS = gameFrame.getTitle();
        final long[] a = {System.currentTimeMillis()};

        Thread gameLoopThread = new Thread(() -> {
            long lastTime = System.nanoTime();
            while (true) {
                if (System.nanoTime() - lastTime > 1_000_000_000L/120L) {
                    lastTime = System.nanoTime();
                    fpsRecord[0]++;
                    if (System.currentTimeMillis() - a[0] > 250) {
                        gameFrame.setTitle(gameFrameTitleWithoutFPS+ " | FPS : " +fpsRecord[0]*4);
                        fpsRecord[0] = -1;
                        a[0] = System.currentTimeMillis();
                    }

                    totalDirection = 0;
                    for (Direction direction : directions) {
                        totalDirection += direction.getDelta();
                    }

                    if(!gameFrame.getGamePanel().hasFocus()) {
                        releaseActionLeft.removeMovements();
                        releaseActionRight.removeMovements();
                    }

                    characterView.setHorizontal_direction(-totalDirection);

                    gameClient.sendPlayerInformation(playableCharacter);
                    collisionOnTop = false;
                    collisionOnBottom = false;
                    collisionOnRight = false;
                    collisionOnLeft = false;

                    for (Object object : allSolidObjects) {
                        if (CollisionDetection.isCollisionBetween(playableCharacter, object).equals(PlayerCollisionSide.TOP))
                            collisionOnTop = true;
                        if (CollisionDetection.isCollisionBetween(playableCharacter, object).equals(PlayerCollisionSide.BOTTOM))
                            collisionOnBottom = true;
                        if (CollisionDetection.isCollisionBetween(playableCharacter, object).equals(PlayerCollisionSide.RIGHT))
                            collisionOnRight = true;
                        if (CollisionDetection.isCollisionBetween(playableCharacter, object).equals(PlayerCollisionSide.LEFT))
                            collisionOnLeft = true;
                    }

                    if ((collisionOnRight && relativeMovementX > 0) || (collisionOnLeft && relativeMovementX < 0))
                        relativeMovementX = 0;
                    else if (collisionOnBottom) {
                        if (totalDirection == 1 && relativeMovementX < PlayableCharacter.getRelativeMaxSpeed())
                            relativeMovementX += PlayableCharacter.getRelativeSpeedGrowth();
                        else if (totalDirection == -1 && relativeMovementX > -PlayableCharacter.getRelativeMaxSpeed())
                            relativeMovementX -= PlayableCharacter.getRelativeSpeedGrowth();
                        else {
                            if (Math.abs(relativeMovementX) < Terrain.getRelativeFriction())
                                relativeMovementX = 0;
                            else if (relativeMovementX > 0)
                                relativeMovementX -= Terrain.getRelativeFriction();
                            else if (relativeMovementX < 0)
                                relativeMovementX += Terrain.getRelativeFriction();
                        }
                    }
                    else {
                        if (totalDirection == 1 && relativeMovementX < PlayableCharacter.getRelativeMaxSpeed())
                            relativeMovementX += PlayableCharacter.getRelativeSpeedGrowth()/2;
                        else if (totalDirection == -1 && relativeMovementX > -PlayableCharacter.getRelativeMaxSpeed())
                            relativeMovementX -= PlayableCharacter.getRelativeSpeedGrowth()/2;
                        else {
                            if (Math.abs(relativeMovementX) < Terrain.getRelativeFriction()/10)
                                relativeMovementX = 0;
                            else if (relativeMovementX > 0)
                                relativeMovementX -= Terrain.getRelativeFriction()/10;
                            else if (relativeMovementX < 0)
                                relativeMovementX += Terrain.getRelativeFriction()/10;
                        }
                    }

                    if (collisionOnBottom) {
                        if (jumpKeyJustPressed) {
                            while (collisionOnBottom) {
                                playableCharacter.setRelativeY(playableCharacter.getRelativeY()-PlayableCharacter.getRelativeJumpStrength());

                                for (Object object : allSolidObjects) {
                                    collisionOnBottom = CollisionDetection.isCollisionBetween(playableCharacter, object).equals(PlayerCollisionSide.BOTTOM);
                                    if (collisionOnBottom) {
                                        break;
                                    }
                                }
                            }
                            relativeMovementY -= PlayableCharacter.getRelativeJumpStrength();
                            playableCharacter.setRelativeY(playableCharacter.getRelativeY()+PlayableCharacter.getRelativeJumpStrength());

                            jumpKeyJustPressed = false;
                        }
                        else if (relativeMovementY > 0)
                            relativeMovementY = 0;
                    }
                    else if (collisionOnTop && relativeMovementY < 0)
                        relativeMovementY = Terrain.getRelativeGravityGrowth();
                    else if (relativeMovementY < Terrain.getRelativeMaxGravity())
                        relativeMovementY += Terrain.getRelativeGravityGrowth();

                    if (playableCharacter.getRelativeY() >= 1) {
                        playableCharacter.setRelativeX(0.45f);
                        playableCharacter.setRelativeY(0.1f);
                    }

                    playableCharacter.setRelativeX(playableCharacter.getRelativeX() + relativeMovementX);
                    playableCharacter.setRelativeY(playableCharacter.getRelativeY() + relativeMovementY);

                    if(readyToFire) {
                        if(System.currentTimeMillis() - lastShot > 500) {
                            System.out.println(MouseInfo.getPointerInfo().getLocation());
                            //shot();
                            lastShot = System.currentTimeMillis();
                        }
                    }

                    characterView.setRelativeX(playableCharacter.getRelativeX());
                    characterView.setRelativeY(playableCharacter.getRelativeY());

                    SwingUtilities.invokeLater(() -> {
                        otherPlayersPainting();
                        gameFrame.getGamePanel().repaint();
                    });

                    if (IS_UNIX_OS)
                        Toolkit.getDefaultToolkit().sync();
                }
            }
        });
        gameLoopThread.start();
    }

    private static void otherPlayersPainting() {
        for (int i = 0; i < gameClient.getOtherPlayers().size(); i++) {
            if (gameFrame.getGamePanel().getOtherPlayersViews().size() > i) {

                gameFrame.getGamePanel().getOtherPlayersViews().get(i).setRelativeX(gameClient.getOtherPlayers().get(i).getRelativeX());
                gameFrame.getGamePanel().getOtherPlayersViews().get(i).setRelativeY(gameClient.getOtherPlayers().get(i).getRelativeY());
            }
            else {
                gameFrame.getGamePanel().addOtherPlayerViewToArray(new CharacterView(
                        gameClient.getOtherPlayers().get(i).getRelativeX(),
                        gameClient.getOtherPlayers().get(i).getRelativeY(),
                        PlayableCharacter.getRelativeWidth(),
                        PlayableCharacter.getRelativeHeight(),
                        gameClient.getOtherPlayers().get(i).getName()));
            }
        }
    }
}