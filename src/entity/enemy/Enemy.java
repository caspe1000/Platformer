/**
 * This abstract class is responsible for the general functions of the enemies
 * @author Linus Magnusson
 * @author Simon Sandén
 */

package entity.enemy;

import entity.player.Player;
import gamestates.Gamestate;
import utils.Constants;

import java.awt.*;
import java.awt.geom.Rectangle2D;

import static utils.AssistanceMethods.*;
import static utils.Constants.EnemyConstants.*;
import static utils.Constants.EnemyConstants.IDLE;
import static utils.Constants.EnemyConstants.RUNNING;
import static utils.Constants.EntityConstants.MAX_AIR_SPEED;
import static utils.Constants.GameConstants.SCALE;
import static utils.Constants.Directions.*;
import static utils.Constants.StartPlayerConstants.*;

public abstract class Enemy{
    protected float x;
    protected float y;
    protected int width;
    protected int height;
    protected Rectangle2D.Float hitbox;
    protected Rectangle2D.Float attackBox;
    protected float horizontalSpeed;
    protected int maxHealth;
    protected int currentHealth;
    protected int attackDamage;
    private EnemyManager enemyManager;
    public int enemyType;
    private final float patrolSpeed = RAT_PATROL_SPEED;
    private boolean firstUpdate = true;
    protected int walkDirection = LEFT;
    private boolean canAttack = true;
    private AttackCooldownThread attackCooldownTimer;
    protected int flipX = 0;
    protected int flipW = 1;
    protected int animationIndex;
    protected int animationTick;
    protected int animationSpeed = 30;
    protected int entityState = IDLE;
    protected boolean inAir;
    protected float airSpeed;
    protected float gravity = 0.03f * SCALE;
    protected boolean animatedDeathOnce;
    protected boolean isMoving;

    /**
     * Constructor for enemy
     * @param x
     * @param y
     * @param width
     * @param height
     * @param enemyType
     */
    public Enemy(float x, float y, int width, int height, int enemyType, int maxHealth, int attackDamage) {
        initialiseVariables(x,y,width,height,enemyType,maxHealth,attackDamage);
    }

    /**
     * Initialising the enemy with position, size, type of enemy, max health and attack damage.
     * @param x
     * @param y
     * @param width
     * @param height
     * @param enemyType
     * @param maxHealth
     * @param attackDamage
     */
    private void initialiseVariables(float x, float y, int width, int height, int enemyType, int maxHealth, int attackDamage){
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.enemyType = enemyType;
        this.maxHealth = maxHealth;
        this.attackDamage = attackDamage;
    }
    /**
     * This method initializes the hitbox of the entity
     * @param x
     * @param y
     * @param width
     * @param height
     */
    protected void initialiseHitbox(float x, float y, float width, float height) {
        hitbox = new Rectangle2D.Float(x, y,width,height);
    }

    /**
     * This method initialises the attackbox of the entity
     * @param x
     * @param y
     * @param width
     * @param height
     */
    protected void initialiseAttackBox(float x, float y, float width, float height){
        attackBox = new Rectangle2D.Float(x, y, width, height);
    }

    //For Debugging hitbox
    protected void drawHitbox(Graphics g, int levelOffset){
        g.setColor(Color.BLACK);
        g.drawRect((int) hitbox.x - levelOffset, (int) hitbox.y, (int) hitbox.width, (int) hitbox.height);
    }


    /**
     * This metod checks if the player is hit by the Enemy by checking if the player hitbox
     * and enemy attackbox intersects
     * @param enemy
     * @param player
     */
    protected void checkIfPlayerIsHit(Enemy enemy, Player player){
        if(enemy.attackBox.intersects(player.getHitbox()) == true){
            if(canAttack == true && entityState != DEAD){
                attackPlayer(enemy, player);
            }
            changePlayerToHit(enemy, player);
        }
    }

    /**
     * This method makes the enemy attack the player and calls for the cooldown to start
     * @param enemy
     * @param player
     */
    private void attackPlayer(Enemy enemy, Player player){
        if (entityState != DEAD) {
            player.entityTakeDamage(enemy.attackDamage);
            checkIfPlayerIsDead(player);
            canAttack = false;
            startAttackCooldown();
        }
    }

    /**
     * This method checks if the player is dead and if the player
     * is dead the gamestate switches to DeathScreen
     * @param player
     */
    private void checkIfPlayerIsDead(Player player){
        if(player.isEntityDead() == true) {
            Gamestate.state = Gamestate.DEATHSCREEN;
        }
    }
    /**
     * This method starts the attack cooldown
     */
    private void startAttackCooldown(){
        attackCooldownTimer = new AttackCooldownThread();
        attackCooldownTimer.start();
    }

    /**
     * This method changes the players state to hit and calls for the playerHit method
     * @param enemy
     * @param player
     */
    private void changePlayerToHit(Enemy enemy, Player player){
        if (entityState != DEAD && player.isHit() == false) {
            player.playerHit(enemy);
            player.setEntityState(HIT);
        }
    }

    /**
     * This method updates the animationtick to keep track of the animation
     */
    protected void updateAnimationTick(){
        if (animatedDeathOnce == false) {
            animationTick++;
            if (animationTick >= animationSpeed) {
                animationTick = 0;
                animationIndex++;
                if (animationIndex >= GetSpriteAmount(enemyType, entityState)) {
                    if (entityState == DEAD){
                        animatedDeathOnce = true;
                    }
                    animationIndex = 0;
                }
            }
        }
        else {
            animationIndex = 3;
        }
    }

    /**
     * This method updates the enemies
     * @param levelData
     */
    public void update(int[][] levelData){
        updateEntityPosition(levelData);
        updateAttackBox(0, 0);
        updateAnimationTick();
        setEntityAnimation();
    }

    /**
     * This method updates the enemies attack box depending on which direction the enemy is facing.
     * @param xOffset
     * @param facingDirection
     */
    protected void updateAttackBox(int xOffset, int facingDirection){
        if(facingDirection == 0){
            attackBox.x = hitbox.x - xOffset;
            attackBox.y = hitbox.y;
        } else if (facingDirection == 1){
            attackBox.x = hitbox.x + xOffset;
            attackBox.y = hitbox.y;
        }
    }

    /**
     * This method sets the enemies animation depending on which state the enemy is in.
     */
    protected void setEntityAnimation() {
        int startAnimation = entityState;

        if (isMoving == true) {
            entityState = RUNNING;
        }
        else if (currentHealth <= 0) {
            entityState = DEAD;
        }
        else if (isMoving == false && entityState != DEAD) {
            entityState = IDLE;
        }

        if (startAnimation != entityState){
            resetAnimationTick();
        }
    }

    /**
     * Resets the animations tick back to 0
     */
    protected void resetAnimationTick(){
        animationTick = 0;
        animationIndex = 0;
    }

    /**
     * This method checks if the enemy is in air and changes the boolean to true or false
     * @param levelData
     */

    protected void isEnemyInAir(int[][] levelData){
        if(IsEntityOnFloor(hitbox, levelData) == false){
            inAir = true;
        }
    }


    /**
     * Checks that the enemy does not collide with walls or objects while in air.
     * @param levelData
     */
    private void checkIfEntityCanMoveInAir(int[][] levelData) {
        if (canMoveHere(hitbox.x, hitbox.y + airSpeed, hitbox.width, hitbox.height, levelData) == true) {
            hitbox.y += airSpeed;
            changeAirSpeed();
            updateEnemyXPosition(horizontalSpeed, levelData);
        } else {
            updateEnemyXPosition(horizontalSpeed, levelData);
        }
    }

    /**
     * Changes the speed while and enemy is in air.
     */
    private void changeAirSpeed(){
        if(airSpeed < MAX_AIR_SPEED){
            airSpeed += gravity;
        }
    }

    /**
     * This method is used to move the enemy with all the checks that is needed to be done
     * Makes the physics and collisions work
     * @param levelData
     */
    protected void moveEntity(int[][] levelData) {
        if (inAir == true) {
            checkIfEntityCanMoveInAir(levelData);
        } else {
            updateEnemyXPosition(horizontalSpeed, levelData);
        }
    }

    /**
     * Updates the enemy's position on the map.
     * @param horizontalSpeed
     * @param levelData
     */
    protected void updateEnemyXPosition(float horizontalSpeed, int [][] levelData) {
        if(canMoveHere(hitbox.x + horizontalSpeed, hitbox.y, hitbox.width, hitbox.height, levelData) == true){
            hitbox.x += horizontalSpeed;
        } else {
            if (walkDirection == LEFT) {
                hitbox.x = GetEntityXPosNextToWall(hitbox) + 2;
            }
            else {
                hitbox.x = GetEntityXPosNextToWall(hitbox) + 15;
            }
        }
    }

    /**
     * This method updates the enemy movement and checks for collisions and invalid moves
     * @param levelData
     */
    protected void updateEntityPosition(int[][] levelData) {
        isEnemyInAir(levelData);
        moveEntity(levelData);

        switch (entityState){
            case IDLE:
                entityState = RUNNING;
                break;
            case RUNNING:
                horizontalSpeed = 0;
                setEnemyToPatrol();
                checkIfWalkDirectionShouldChange(levelData);
                break;

            case DEAD:
                canAttack = false;
                horizontalSpeed = 0;
                break;
        }
    }

    /**
     * This method checks if the walkdirection of the enemy should change
     * @param levelData
     */
    private void checkIfWalkDirectionShouldChange(int[][] levelData){
        if(canMoveHere(hitbox.x + horizontalSpeed, hitbox.y, hitbox.width, hitbox.height, levelData) == true){
            if(IsFloor(hitbox, horizontalSpeed, levelData) == true){
                return;
            }
        }
        changeFacingDirection();
    }

    /**
     * This method changes the enemy walkdirektion and sets an offset that helps the enemy not getting
     * stuck
     *
     */
    public void changeEnemyWalkDirection(){
        if(getWalkDirection() == LEFT) {
            hitbox.x += 3;
        } else {
            hitbox.x -= 3;
        }
        changeFacingDirection();
    }

    /**
     * This method changes the facing direction for the enemy
     */
    public void changeFacingDirection() {
        if (entityState != DEAD){
             if (walkDirection == LEFT) {
                 walkDirection = RIGHT;
                 flipX = 0;
                 flipW = 1;

        }    else {
                 walkDirection = LEFT;
                 flipX = width + 20;
                 flipW = -1;
         }
      }
    }


    /**
     * This method makes the enemy start patrolling
     */
    public void setEnemyToPatrol(){
        if(walkDirection == LEFT){
            horizontalSpeed = -patrolSpeed;
        } else {
            horizontalSpeed = patrolSpeed;
        }
    }

    /**
     * This method makes the enemy take damage
     * @param damage
     */
    public void enemyTakeDamage(int damage){
        currentHealth -= damage;
    }


    /**
     * This method checks if enemys current health is or less than 0. Returns value accordingly.
     * @return true if health is 0 or below, false if not.
     */
    public boolean isEntityDead(){
        if(currentHealth <= 0){
            return true;
        }
        return false;
    }

    public int getWalkDirection(){
       return walkDirection;
    }

    public int getAnimationIndex(){
        return animationIndex;
    }

    public int getEnemyState(){
        return entityState;
    }

    public int getFlipX() {
        return flipX;
    }

    public int getFlipW() {
        return flipW;
    }

    public Rectangle2D.Float getHitbox(){
        return hitbox;
    }

    public EnemyManager getEnemyManager() {
        return enemyManager;
    }

    public void setEnemyManager(EnemyManager enemyManager) {
        this.enemyManager = enemyManager;
    }

    public int getCurrentHealth() {
        return currentHealth;
    }

    public void setCurrentHealth(int currentHealth) {
        this.currentHealth = currentHealth;
    }

    public int getEntityState() {return entityState;}
    public void setEntityState(int entityState) {this.entityState = entityState;}

    public void setAnimationIndex(int animationIndex) {
        this.animationIndex = animationIndex;
    }

    public boolean isMoving() {
        return isMoving;
    }

    public void setMoving(boolean moving) {
        isMoving = moving;
    }

    private class AttackCooldownThread extends Thread{
        /**
         * This thread is responsible for the attackCooldown
         * @author Linus Magnusson
         */
        @Override
        public void run() {
            try {
                Thread.sleep(800);
                canAttack = true;
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
