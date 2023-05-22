package items;

//Imports from within project
import gamestates.Playing;
import utils.LoadSave;
//Imports from Javas library
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;
//Imports of static variables and methods
import static utils.Constants.GunManConstants.GUN_MAN_CHANGER;


/**
 * This class is responsible for storing and updating all items in the game
 * @author Linus Magnusson
 */
public class ItemManager {

    private Playing playing;
    private ArrayList<ClassChanger> classChangers;


    public ItemManager(Playing playing){
        this.playing = playing;
        classChangers = new ArrayList<>();
        classChangers.add(new ClassChanger(playing,GUN_MAN_CHANGER));
    }

    /**
     * Updates the ItemManager.
     */
    public void update(){
        for(Iterator<ClassChanger> iterator = classChangers.iterator(); iterator.hasNext();){
            ClassChanger classChanger = iterator.next();
            if(classChanger.checkIfPlayerCollides()){
                iterator.remove();
            }
        }
    }

    /**
     * Draws the items in ItemManager.
     * @param g
     * @param xLevelOffset
     */
    public void draw(Graphics g, int xLevelOffset){
        drawClassChanger(g, xLevelOffset);
    }


    /**
     * Draws the Class Changer item.
     * @param g
     * @param xLevelOffset
     */
    private void drawClassChanger(Graphics g, int xLevelOffset) {
        for(ClassChanger classChanger: classChangers){
            g.drawImage(classChanger.changerImage, (int) classChanger.hitbox.x - xLevelOffset, (int) classChanger.hitbox.y, (int)  classChanger.hitbox.width, (int) classChanger.hitbox.height, null);
        }
    }
}

