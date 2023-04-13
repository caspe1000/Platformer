package input;

import gamestates.Gamestate;
import main.GamePanel;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class MouseInputs implements MouseListener {

    private GamePanel gamePanel;

    public MouseInputs(GamePanel gamePanel){
        this.gamePanel = gamePanel;
    }


    @Override
    public void mouseClicked(MouseEvent e) {
        switch (Gamestate.state){

            case PLAYING:
                gamePanel.getGame().getPlaying().mouseClicked(e);
                break;

            case MENU:
                gamePanel.getGame().getMenu().mouseClicked(e);
                break;

            case PAUSE:
                gamePanel.getGame().getPausemenu().mouseClicked(e);
                break;

            case OPTIONS:
                gamePanel.getGame().getOptions().mouseClicked(e);
                break;

            default:
                break;
        }
    }

    public void mouseMoved(MouseEvent e){
        switch (Gamestate.state){

            case PLAYING:
                gamePanel.getGame().getPlaying().mouseMoved(e);
                break;

            case MENU:
                gamePanel.getGame().getMenu().mouseMoved(e);
                break;

            case PAUSE:
                gamePanel.getGame().getPausemenu().mouseMoved(e);
                break;

            default:
                break;
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        switch (Gamestate.state){

            case PLAYING:
                gamePanel.getGame().getPlaying().mousePressed(e);
                break;

            case MENU:
                gamePanel.getGame().getMenu().mousePressed(e);
                break;

            case PAUSE:
                gamePanel.getGame().getPausemenu().mousePressed(e);
                break;

            case OPTIONS:
                gamePanel.getGame().getOptions().mousePressed(e);
                break;

            default:
                break;
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        switch (Gamestate.state){

            case PLAYING:
                gamePanel.getGame().getPlaying().mouseReleased(e);
                break;

            case MENU:
                gamePanel.getGame().getMenu().mouseReleased(e);
                break;

            case PAUSE:
                gamePanel.getGame().getPausemenu().mouseReleased(e);
                break;

            case OPTIONS:
                gamePanel.getGame().getOptions().mouseReleased(e);
                break;

            default:
                break;
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
