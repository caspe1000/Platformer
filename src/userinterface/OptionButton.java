package userinterface;

import gamestates.Gamestate;
import utils.LoadSave;

import java.awt.*;
import java.awt.image.BufferedImage;

import static utils.Constants.UserInterface.OptionButtons.*;

public class OptionButton {

    private int xPos, yPos, btnWidth, btnHeight, rowIndex, index;
    private boolean mouseOver, mousePressed;
    private BufferedImage[] images;
    private Rectangle btnBounds;

    public OptionButton(int xPos, int yPos, int btnWidth, int btnHeight, int rowIndex){
        this.xPos = xPos;
        this.yPos = yPos;
        this.btnWidth = btnWidth;
        this.btnHeight = btnHeight;
        this.rowIndex = rowIndex;
        loadImages();
        initBounds();
    }

    private void initBounds(){
        btnBounds = new Rectangle(xPos, yPos, OPTIONBTN_SIZE, OPTIONBTN_SIZE);
    }

    private void loadImages(){
        images = new BufferedImage[2];
        BufferedImage temp = LoadSave.GetSpriteAtlas(LoadSave.OPTIONS_BUTTONS);

        for (int i = 0; i < images.length; i++){
            images[i] = temp.getSubimage(
                    i * OPTIONBTN_DEFAULT_SIZE,
                    rowIndex * OPTIONBTN_DEFAULT_SIZE,
                    OPTIONBTN_DEFAULT_SIZE,
                    OPTIONBTN_DEFAULT_SIZE);
        }
    }

    public void drawButtons(Graphics g){
        g.drawImage(images[index], xPos, yPos, OPTIONBTN_SIZE, OPTIONBTN_SIZE, null);
    }

    public void updateButtons(){
        index = 0;
        if (mouseOver){
            index = 1;
        }
        if (mousePressed){
            index = 2;
        }
    }

    public void resetBtnBooleans(){
        mouseOver = false;
        mousePressed = false;
    }

    public boolean isMouseOver() {return mouseOver;}
    public void setMouseOver(boolean mouseOver) {this.mouseOver = mouseOver;}
    public boolean isMousePressed() {return mousePressed;}
    public void setMousePressed(boolean mousePressed) {this.mousePressed = mousePressed;}
    public Rectangle getBtnBounds(){
        return btnBounds;
    }
    public int getxPos() {return xPos;}
    public void setxPos(int xPos) {this.xPos = xPos;}
    public int getyPos() {return yPos;}
    public void setyPos(int yPos) {this.yPos = yPos;}
    public int getBtnWidth() {return btnWidth;}
    public void setBtnWidth(int btnWidth) {this.btnWidth = btnWidth;}
    public int getBtnHeight() {return btnHeight;}
    public void setBtnHeight(int btnHeight) {this.btnHeight = btnHeight;
    }public void setBtnBounds(Rectangle btnBounds) {this.btnBounds = btnBounds;}
}
