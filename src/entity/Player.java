/**
 * This class is responsible for the players attributes and functions
 * @author Linus Magnusson
 */

package entity;

import main.Game;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import static utils.AssistanceMethods.IsEntityOnFloor;
import static utils.AssistanceMethods.canMoveHere;
import static utils.Constants.Directions.LEFT;
import static utils.Constants.Directions.RIGHT;
import static utils.Constants.EnemyConstants.HIT;

public class Player extends Entity {
    private BufferedImage[][] playerAnimations;
    private EnemyManager enemyManager;
    private AttackTimer attackTimer;
    private float xDrawOffset = 24 * Game.SCALE;
    private float yDrawOffset = 14 * Game.SCALE;
    private int[][] levelData;
    private float playerSpeed = 1.2f;
    private int flipX = 0;
    private int flipW = 1;
    private boolean jumpOnce;
    private boolean canAttack;
    private int facing;
    private Enemy attackingEnemy;


    /**
     * Constructor for Player
     * @param x
     * @param y
     * @param width
     * @param heigth
     */
    public Player(float x, float y, int width, int heigth, int maxHealth, int attackDamage, EnemyManager enemyManager) {
        super(x, y, width, heigth, maxHealth, attackDamage);
        loadPlayerAnimations();
        initialiseHitbox(x,y, 22 * Game.SCALE, 30 * Game.SCALE);
        initialiseAttackBox(x,y,20 * Game.SCALE, 27 * Game.SCALE);
        this.enemyManager = enemyManager;
        jumpOnce = true;
        canAttack = true;
        isHit = false;
        facing = 1;
    }

    public void setSpawn(Point spawn){
        this.x = spawn.x;
        this.y = spawn.y;
        hitbox.x = x;
        hitbox.y = y;
    }

    /**
     * This method is responsible for updating the player
     */
    public void updatePlayer() {
        updateEntityPos(levelData);
        updateAnimationTick();
        updateAttackBox(30, facing);
        setEntityAnimation();
    }

    protected void jump() {
        if(inAir){
            return;
        } else if (jumpOnce){
            inAir = true;
            airSpeed = jumpSpeed;
            jumpOnce = false;
        }
    }

    public void playerHit(Enemy attackingEnemy){
        this.attackingEnemy = attackingEnemy;
        isHit = true;
        HitTimer ht = new HitTimer();
        ht.start();
    }

    public void attack(){
        //TODO add animation for attacking
        if(canAttack){
            enemyManager.checkIfEnemyIsHit(attackBox);
            canAttack = false;
            attackTimer = new AttackTimer();
            attackTimer.start();
            System.out.println("Attacked");
        }
    }

    public void resetJumpOnce(){
        this.jumpOnce = true;
    }
    /**
     * This method renders the player
     * @param g
     */
    public void renderPlayer(Graphics g, int levelOffset) {

        g.drawImage(playerAnimations[entityState][animationIndex],
                (int) (hitbox.x - xDrawOffset) + flipX - levelOffset,
                (int) (hitbox.y - yDrawOffset),
                width * flipW,
                height, null);

        drawHitbox(g,levelOffset);
        //drawAttackBox(g, levelOffset); //TODO Remove later just for debugging
    }

    //TODO få in spelarfacing och gör if-satser eller intersectsLine och gör ett entitystate hit som gör allt mer smooth
    public void knockbackPlayer(Enemy enemy){
        xSpeed = 0;
        if(hitbox.x > enemy.hitbox.x){
            xSpeed = 1.2f * 1.2f;
        } else if (hitbox.x < enemy.hitbox.x){
            xSpeed = -1.2f * 1.2f;
        }
        if(canMoveHere(hitbox.x + xSpeed, hitbox.y, hitbox.width, hitbox.height, levelData)){
            hitbox.x += xSpeed;
        }
    }

    /**
     *  This method updates the players position and checks for collisions
     */
    protected void updateEntityPos(int [][] lvlData) {
        isMoving = false;

        if(jumping){
            jump();
        }

        if(!movingLeft && !movingRight && !inAir && !isHit){
            return;
        }

        xSpeed = 0;
        if(movingLeft){
            xSpeed -=playerSpeed;
            flipX = width;
            flipW = -1;
            facing = 0;
        } else if (movingRight){
            xSpeed += playerSpeed;
            flipX = 0;
            flipW = 1;
            facing = 1;
        }

        if(isHit){
            knockbackPlayer(attackingEnemy);
        }

        if(!inAir){
            isEntityInAir(lvlData);
        }



        moveEntity(lvlData);
        isMoving = true;
    }

    /**
     * This method loads the players animations into a 2d array of bufferedimages
     */
    public void loadPlayerAnimations() {

        InputStream is = getClass().getResourceAsStream("/Gubbe_1_Test.png");
        try {
            BufferedImage player = ImageIO.read(is);
            playerAnimations = new BufferedImage[5][8];
            for (int i = 0; i < playerAnimations.length; i++) {
                for (int j = 0; j < playerAnimations[i].length; j++) {
                    playerAnimations[i][j] = player.getSubimage(j * 64, i * 64, 64,64 );
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * This method loads the leveldata into the player
     * @param lvlData
     */
    public void loadLvlData(int [][] lvlData){
        this.levelData = lvlData;
        if(!IsEntityOnFloor(hitbox,lvlData)){
            inAir = true;
        }
    }

    private class AttackTimer extends Thread{
        @Override
        public void run() {
            try {
                Thread.sleep(1000);
                canAttack = true;
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private class HitTimer extends Thread{
        @Override
        public void run() {
            try {
                Thread.sleep(500);
                isHit = false;
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
