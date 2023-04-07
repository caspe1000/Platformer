package input;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import gamestates.Gamestate;
import main.GamePanel;

import static utils.Constants.Directions.*;


public class KeyBoardInputs implements KeyListener {
    private GamePanel gamePanel;
    private final int PLAYER_SPEED = 5;

    public KeyBoardInputs(GamePanel gamePanel){
        this.gamePanel = gamePanel;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (Gamestate.state){

            case PLAYING:
                gamePanel.getGame().getPlaying().keyPressed(e);
                break;

            case MENU:
                gamePanel.getGame().getMenu().keyPressed(e);
                break;

            default:
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch (Gamestate.state){

            case PLAYING:
                gamePanel.getGame().getPlaying().keyReleased(e);
                break;

            case MENU:
                gamePanel.getGame().getMenu().keyReleased(e);
                break;

            default:
                break;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}
}
