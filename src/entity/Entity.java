package entity;

import main.Game;

import java.awt.*;
import java.awt.geom.Rectangle2D;

import static utils.AssistanceMethods.*;
import static utils.Constants.PlayerConstants.*;

public abstract class Entity {
    protected float x;
    protected float y;
    protected int width;
    protected int height;
    protected Rectangle2D.Float hitbox;
    protected int animationIndex;
    protected int animationTick;
    protected int animationSpeed = 30;
    protected int entityState = IDLE;
    protected float airSpeed; // the airspeed of the entity
    protected float gravity; // the gravity of the entity, this is multiplied with game.Scale
    protected float jumpSpeed; // The speed of the entity jump, this is multiplied with game.Scale
    protected float fallSpeedAfterCollision; // The speed the enity is falling in after a collision, this is multiplied with game.Scale
    protected boolean inAir = false; // check if in air
    protected boolean jumping = false; // check if jumping
    protected boolean isMoving = false; // check if moving
    protected float xSpeed; // speed of the entity to the left or right
    protected boolean movingLeft; // check if entity moving left
    protected boolean movingRight; // check if entity moving right

    public Entity(float x, float y,int width, int height){
        this.x = x;
        this.y = y;
        this.height = height;
        this.width = width;
        airSpeed = 0f;
        gravity = 0.03f * Game.SCALE;
        jumpSpeed = -2.25f * Game.SCALE;
        fallSpeedAfterCollision = 0.5f * Game.SCALE;
    }

    protected void initialiseHitbox(float x, float y, float width, float heigth) {
        hitbox = new Rectangle2D.Float(x, y,width,heigth);
    }

    //For Debugging hitbox
    protected void drawHitbox(Graphics g){
        g.setColor(Color.BLACK);
        g.drawRect((int) hitbox.x, (int) hitbox.y, (int) hitbox.width, (int) hitbox.height);
    }

    // the movement of the entity, varies for each subclass
    protected abstract void updateEntityPos(int[][] lvldata);

    // used to set jump to true
    protected void jump() {
        if(inAir){
            return;
        }
        inAir = true;
        airSpeed = jumpSpeed;
    }

    //Updates the Xpos of the entity by taking in the speed
    protected void updateXPosition(float xSpeed, int [][] lvlData) {
        if(canMoveHere(hitbox.x + xSpeed, hitbox.y, hitbox.width, hitbox.height, lvlData)){
            hitbox.x += xSpeed;
        } else {
            hitbox.x = GetEntityXPosNextToWall(hitbox, xSpeed);
        }
    }

    // used when entity no longer is in air
    protected void resetInAir() {
        inAir = false;
        airSpeed = 0;
    }

    // Checks if the entity is in air and changes the boolean to true or false
    protected void isEntityInAir(int[][] lvlData){
        if(!IsEntityOnFloor(hitbox, lvlData)){
            inAir = true;
        }
    }

    // moves the entity and makes all the nessecary checks
    protected void moveEntity(int[][] lvlData){
        if(inAir){
            if(canMoveHere(hitbox.x, hitbox.y + airSpeed, hitbox.width, hitbox.height, lvlData )){
                hitbox.y += airSpeed;
                airSpeed += gravity;
                updateXPosition(xSpeed, lvlData);
            } else {
                hitbox.y = GetEntityYPosUnderOrAboveTile(hitbox, airSpeed);
                if(airSpeed > 0){
                    resetInAir();
                } else {
                    airSpeed = fallSpeedAfterCollision;
                }
                updateXPosition(xSpeed, lvlData);
            }
        } else {
            updateXPosition(xSpeed, lvlData);
        }
    }

    protected void resetAnimationTick(){
        animationTick = 0;
        animationIndex = 0;
    }

    protected void updateAnimationTick() {
        animationTick++;
        if (animationTick >= animationSpeed) {
            animationTick = 0;
            animationIndex++;
            if (animationIndex >= GetSpriteAmount(entityState)) {
                animationIndex = 0;
            }
        }
    }
    // Sets the player animation based on current state
    protected void setEntityAnimation() {

        int startAnimation = entityState;

        if (isMoving) {
            entityState = RUNNING;
        } else {
            entityState = IDLE;
        }
        if(inAir) {
            entityState = JUMP;
        }

        if (startAnimation != entityState){
            resetAnimationTick();
        }
    }

    public Rectangle2D.Float getHitbox(){
        return hitbox;
    }


    public boolean isMovingLeft() {
        return movingLeft;
    }

    public void setMovingLeft(boolean movingLeft) {
        this.movingLeft = movingLeft;
    }

    public boolean isMovingRight() {
        return movingRight;
    }

    public void setMovingRight(boolean movingRight) {
        this.movingRight = movingRight;
    }

    public boolean isJumping() {
        return jumping;
    }

    public void setJumping(boolean jumping) {
        this.jumping = jumping;
    }

    public void allMovingBooleansFalse() {
        setMovingRight(false);
        setMovingLeft(false);
        isMoving = false;
    }

}
