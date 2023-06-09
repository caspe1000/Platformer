/**
 * This class is responsible for keeping track of the interactable objects
 * @author Linus Magnusson
 */

package entity.interactable;
//Imports from within project
import entity.enemy.EnemyManager;
import entity.player.Start_Player;
import gamestates.Playing;
import levels.Level;
//Imports from Javas library
import java.awt.*;
import java.util.ArrayList;


public class InteractablesManager {
    private ArrayList<Box> interactableBoxes;
    private Playing playing;
    private EnemyManager enemyManager;

    /**
     * Constructor for interactablesManager
     * @param playing
     */
    public InteractablesManager(Playing playing, EnemyManager enemyManager){
        this.playing = playing;
        this.enemyManager = enemyManager;
        initialiseVariables();
    }


    /**
     * This method initialises the variables
     */
    private void initialiseVariables(){
        interactableBoxes = new ArrayList<>();
    }

    /**
     * This method draws the interactables
     * @param g
     * @param xOffset
     */
    public void draw(Graphics g, int xOffset){
        for(Box box : interactableBoxes){
            box.draw(g, xOffset);
        }
    }

    /**
     * This method updates all the interactables in the arraylists
     */
    public void update(){
        int counter = 0;
        for(Box box : interactableBoxes){
            box.update(playing.getLevelManager().getCurrentLevel().getLevelData(), box, playing);
            if(box.touchingBox()== false){
                counter++;
            }
            if(counter == interactableBoxes.size()){
                resetChangedPlayerVariables();
            }
        }
    }

    /**
     * This method resets the variables changed in player from this class
     */
    private void resetChangedPlayerVariables(){
        playing.getPlayer().setHorizontalSpeed(0);
        playing.getPlayer().setStandingOnInteractable(false);
        playing.getPlayer().setPushing(false);
    }


    /**
     * This method loads the boxes and initalises player in to the boxes
     * @param level
     */
    public void loadBoxes(Level level){
        interactableBoxes = level.getBoxes();
        for(Box b : interactableBoxes){
            b.initialisePlayerToBox(playing.getPlayer());
            b.setEnemyManager(enemyManager);
        }
    }
}
