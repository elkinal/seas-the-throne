package edu.cornell.jade.seasthethrone.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.utils.viewport.Viewport;
import edu.cornell.jade.seasthethrone.render.Renderable;
import edu.cornell.jade.seasthethrone.render.RenderingEngine;

import java.util.ArrayList;
import java.util.Arrays;

public class PauseMenu implements Renderable {

    private boolean paused;

    // Location and scaling
    private float x, y;
    private float width, height;
    private float screenWidth, screenHeight;

    // Textures
    private final Texture scrollTexture = new Texture("ui/pausescreen.png");
    private final Texture backgroundTexture = new Texture("ui/darkscreen.png");
    private final TextureRegion scrollTextureRegion, backgroundTextureRegion;

    // Menu option enum
    private MenuSelection selection = MenuSelection.RESUME;

    // Text rendering
    private BitmapFont menuFont, menuShadowFont;
    private float textSpacingY;
    private final float fontSize = 3.0f;

    // Dialogue box for the help menu
    private DialogueBoxController dialogueBoxController;
    private DialogueBox dialogueBox;

    public enum MenuSelection {
        RESUME(0, "Resume"),
        RESTART(1, "Restart"),
        HELP(2, "Help"),
        QUIT(3, "Quit");

        public final String optionName;
        public final int optionValue;

        private MenuSelection(int value, String name) {
            this.optionName = name;
            this.optionValue = value;
        }

        public MenuSelection cycleUp() {
            return values()[(optionValue > 0 ? optionValue - 1 : optionValue)];
        }

        public MenuSelection cycleDown() {
            return values()[(optionValue < 3 ? optionValue + 1 : optionValue)];
        }
    }


    /** Constructor for the Pause Menu */
    public PauseMenu(Viewport viewport) {

        // Loading scroll texture
        scrollTextureRegion = new TextureRegion(scrollTexture);

        // Creating dark background texture
        Pixmap pixmap = new Pixmap(viewport.getScreenWidth(),
                viewport.getScreenHeight(),
                Pixmap.Format.RGBA8888);

        Color backgroundColor = new Color(0, 0, 0, 0.55f);
        pixmap.setColor(backgroundColor);
        pixmap.fill();
        //TODO: fix opacity bug
//        Texture backgroundTexture = new Texture(pixmap);
//        backgroundTextureRegion = new TextureRegion(backgroundTexture);
        backgroundTextureRegion = new TextureRegion(backgroundTexture);

        // Setting up text
        FreeTypeFontGenerator generator =
                new FreeTypeFontGenerator(Gdx.files.internal("Alagard.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter =
                new FreeTypeFontGenerator.FreeTypeFontParameter();

        menuFont = generator.generateFont(parameter);
        menuFont.setUseIntegerPositions(false);
        menuFont.getData().setScale(fontSize);
        menuFont.setColor(Color.BLACK);

        menuShadowFont = generator.generateFont(parameter);
        menuShadowFont.setUseIntegerPositions(false);
        menuShadowFont.getData().setScale(fontSize);
        menuShadowFont.setColor(Color.WHITE);

        generator.dispose();

        // Calculating spacings between menu options
        GlyphLayout layout = new GlyphLayout(menuFont, "Sample");
        textSpacingY = layout.height + 25;

        // Setting scaling
        width = 500;
        height = 520;

        // Creating the dialogue box
        dialogueBox = new DialogueBox(viewport);
        dialogueBox.setTexts("Line1\nLine2\nLine3", "Line4\nLine5\nLine6", "Line7\nLine8\nLine9");
        dialogueBoxController = new DialogueBoxController(dialogueBox);
    }

    /** Returns true if the menu is paused */
    public boolean isPaused() {
        return paused;
    }

    /** Displays the menu when the game is paused */
    public void setPaused(boolean paused) {
        this.paused = paused;
        if (!paused) {
            selection = MenuSelection.RESUME;
        }
    }

    /** Switches to a lower menu item */
    public void cycleDown() {
        if (paused && selection.optionValue < 3) {
            selection = selection.cycleDown();
        }
    }

    /** Switches to a higher menu item */
    public void cycleUp() {
        if (paused && selection.optionValue > 0)
            selection = selection.cycleUp();
    }

    public MenuSelection getSelection() {return this.selection;}

    /** Ensures the menu stays the same size and remains in the center
     * of the screen when the window is resized. */
    public void resize(int screenWidth, int screenHeight) {

        // Initial scale
        if (this.screenWidth == 0) this.screenWidth = screenWidth;
        if (this.screenHeight == 0) this.screenHeight = screenHeight;

        // New position of menu after resizing
        x = ((float)screenWidth / 2) - width/2;
        y = ((float)screenHeight / 2) - height/2;

        dialogueBox.resize(screenWidth, screenHeight);
    }

    /** Returns the X position of the text */
    private float getTextX() {
        return x + width/2;
    }

    /** Returns the Y position of the text */
    private float getTextY() {
        return y + height/2 + 30;
    }

    @Override
    public void draw(RenderingEngine renderer) {
        if (paused) {
            renderer.getGameCanvas().drawUI(
                    backgroundTextureRegion, 0, 0, screenWidth*4, screenHeight*4
            );
            renderer.getGameCanvas().drawUI(
                    scrollTextureRegion, x, y, width, height
            );
            dialogueBox.draw(renderer);
            drawText(renderer);
        }
    }

    /** Draws text to the scroll image */
    private void drawText(RenderingEngine renderer) {
        if (paused) {
            // Drawing the shadow for the selected option
            float textX = getTextX();
            float textY = getTextY();

            float shadowY = textSpacingY * (1.5f - selection.optionValue);
            renderer.getGameCanvas().drawTextUI(selection.optionName, menuShadowFont, textX-2, textY+2 + shadowY, true);

            // Drawing the default menu options
            renderer.getGameCanvas().drawTextUI("Resume", menuFont, textX, textY + textSpacingY * 1.5f, true);
            renderer.getGameCanvas().drawTextUI("Restart", menuFont, textX, textY + textSpacingY / 2, true);
            renderer.getGameCanvas().drawTextUI("Help", menuFont, textX, textY - textSpacingY / 2, true);
            renderer.getGameCanvas().drawTextUI("Quit", menuFont, textX, textY - textSpacingY * 1.5f, true);

        }
    }

    /** Sets the text that appears when this object's dialogue is activated */
    public void setDialogueTexts(String ... texts) {
        dialogueBox.setTexts(texts);
    }

    /** Returns the pause menu's help dialogue box */
    public DialogueBox getDialogueBox() {
        return dialogueBox;
    }

    /** Returns the pause menu's help dialogue box controller */
    public DialogueBoxController getDialogueBoxController() {
        return dialogueBoxController;
    }

    @Override
    public void alwaysUpdate() {}

    @Override
    public void neverUpdate() {}

    @Override
    public void setAlwaysAnimate(boolean animate) {}

    @Override
    public boolean alwaysAnimate() {
        return false;
    }

    @Override
    public void progressFrame() {}
}
