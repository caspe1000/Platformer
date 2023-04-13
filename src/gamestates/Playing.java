package gamestates;

import entity.EnemyManager;
import entity.Player;
import items.ItemManager;
import levels.LevelManager;
import main.Game;

import utils.LoadSave;


import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class Playing extends State implements StateMethods {

    private Player player;
    private LevelManager levelManager;
    private EnemyManager enemyManager;
    private ItemManager itemManager;
    private boolean paused;
    private int currentLevelOffsetX;
    private int cameraLeftBorder = (int) (0.3 * Game.GAME_WIDTH);
    private int cameraRightBorder = (int) (0.7 * Game.GAME_WIDTH);
    private int maxLevelOffsetX;

    public Playing(Game game) {
        super(game);
        initClasses();

        calculatingLevelOffset();
        loadStartLevel();
    }

    public void loadNextLevel(){
        levelManager.loadNextLevel();
        player.setSpawn(levelManager.getCurrentLevel().getPlayerSpawn());
    }

    private void loadStartLevel() {
        enemyManager.loadEnemies(levelManager.getCurrentLevel());
    }

    private void calculatingLevelOffset() {
        maxLevelOffsetX = levelManager.getCurrentLevel().getMaxLevelOffsetX();
    }


    public void initClasses() {
        levelManager = new LevelManager(game);
        enemyManager = new EnemyManager(this);
        itemManager = new ItemManager(this);
        loadStartLevel();

        player = new Player(200,200, (int) (64 * Game.SCALE),(int)(40 * Game.SCALE), 10, 2, enemyManager);
        player.loadLvlData(levelManager.getCurrentLevel().getLevelData());
        player.setSpawn(levelManager.getCurrentLevel().getPlayerSpawn());
    }


    @Override
    public void update() {
        if (paused == false) {
            levelManager.updateLevel();
            itemManager.update();
            player.updatePlayer();
            checkIfPlayerIsCloseToCameraBorder();
            enemyManager.update(levelManager.getCurrentLevel().getLevelData());
        }
    }

    /**
     * This method checks if the player is close to the edge of the screen.
     */
    private void checkIfPlayerIsCloseToCameraBorder() {
        int playerPositionX = (int) player.getHitbox().x;
        int currentPlayerPositionX = playerPositionX - currentLevelOffsetX;

        if (currentPlayerPositionX > cameraRightBorder) {
            currentLevelOffsetX += currentPlayerPositionX - cameraRightBorder;
        } else if (currentPlayerPositionX < cameraLeftBorder) {
            currentLevelOffsetX += currentPlayerPositionX - cameraLeftBorder;
        }

        if (currentLevelOffsetX > maxLevelOffsetX) {
            currentLevelOffsetX = maxLevelOffsetX;
        } else if (currentLevelOffsetX < 0) {
            currentLevelOffsetX = 0;
        }
    }

    @Override
    public void draw(Graphics g) {
        levelManager.drawLevel(g, currentLevelOffsetX);
        player.renderPlayer(g, currentLevelOffsetX);
        enemyManager.draw(g, currentLevelOffsetX);
        itemManager.draw(g, currentLevelOffsetX);

    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {

            case KeyEvent.VK_A:
                player.setMovingLeft(true);
                break;
            case KeyEvent.VK_D:
                player.setMovingRight(true);
                break;

            case KeyEvent.VK_SPACE:
                player.setJumping(true);
                break;
            case KeyEvent.VK_F:
                player.attack();
                break;
            case KeyEvent.VK_R:
                initClasses();
                break;
            case KeyEvent.VK_ESCAPE:
                paused = true;
                player.setJumping(false);
                Gamestate.state = Gamestate.PAUSE;
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_A:
                player.setMovingLeft(false);
                break;
            case KeyEvent.VK_D:
                player.setMovingRight(false);
                break;
            case KeyEvent.VK_SPACE:
                player.setJumping(false);
                player.resetJumpOnce();
                break;
        }
    }

    public boolean isPaused() {
        return paused;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    public void windowFocusLost() {
        player.allMovingBooleansFalse();
    }

    public Player getPlayer() {
        return player;
    }

    public ItemManager getItemManager() {
        return itemManager;
    }

    public EnemyManager getEnemyManager() {
        return enemyManager;
    }

    public void setMaxLevelOffsetX(int maxLevelOffsetX) {
        this.maxLevelOffsetX = maxLevelOffsetX;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {}
    @Override
    public void mouseReleased(MouseEvent e) {}
    @Override
    public void mouseMoved(MouseEvent e) {}
}
