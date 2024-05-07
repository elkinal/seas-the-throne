package edu.cornell.jade.seasthethrone.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
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
import java.util.List;

public class DialogueBox implements Renderable, Dialogueable {
  private boolean display;

  // Location and scaling
  private float x, y;
  private float width, height;
  private float screenWidth, screenHeight;

  // Textures
  private final Texture boxTexture = new Texture("ui/dialoguebox.png");
  private final TextureRegion boxTextureRegion;

  // Text rendering
  private BitmapFont menuFont, menuShadowFont;
  private float textSpacingY;
  private final float fontSize = 2.0f;
  private ArrayList<String> texts;
  private int currentText;
  // private String text = "default text \nline 2\nline3";

  /** Constructor for the Pause Menu */
  public DialogueBox(Viewport viewport) {

    // Loading scroll texture
    boxTextureRegion = new TextureRegion(boxTexture);

    // Setting up text
    FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Alagard.ttf"));
    FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();

    menuFont = generator.generateFont(parameter);
    menuFont.setUseIntegerPositions(false);
    menuFont.getData().setScale(fontSize);
    menuFont.setColor(Color.BLACK);

    menuShadowFont = generator.generateFont(parameter);
    menuShadowFont.setUseIntegerPositions(false);
    menuShadowFont.getData().setScale(fontSize);
    menuShadowFont.setColor(Color.WHITE);

    texts = new ArrayList<>();

    generator.dispose();

    // Calculating spacings between menu options
    GlyphLayout layout = new GlyphLayout(menuFont, "Sample");
    textSpacingY = layout.height + 25;

    // Setting scaling
    width = 1000;
    height = 300;
  }

  /**
   * Ensures the dialogue box stays the same size and remains in the center
   * of the screen when the window is resized.
   */
  public void resize(int screenWidth, int screenHeight) {

    // Initial scale
    if (this.screenWidth == 0)
      this.screenWidth = screenWidth;
    if (this.screenHeight == 0)
      this.screenHeight = screenHeight;

    // New position of dialogue box after resizing
    x = ((float) screenWidth / 2) - width / 2;
    y = 25;
  }

  /** Returns the X position of the text */
  private float getTextX() {
    return x + 65;
  }

  /** Returns the Y position of the text */
  private float getTextY() {
    return y + height / 2 + 30;
  }

  /** Switches to the next text page */
  public void cycleLeft() {
    if (currentText < texts.size() - 1) {
      currentText++;
    }
  }

  /** Switches to the previous text page */
  public void cycleRight() {
    if (currentText > 0) {
      currentText--;
    }
  }

  // Dialogue box methods
  @Override
  public void show() {
    this.display = true;
  }

  @Override
  public void hide() {
    this.display = false;
  }

  @Override
  public void setTexts(String... texts) {
    this.texts = new ArrayList<>(Arrays.asList(texts));
  }

  @Override
  public void draw(RenderingEngine renderer) {
    if (display) {
      renderer.getGameCanvas().drawUI(
          boxTextureRegion, x, y, width, height);
      drawText(renderer);
    }
  }

  /** Draws text to the scroll image */
  private void drawText(RenderingEngine renderer) {
    if (display) {
      // Drawing the main text
      renderer.getGameCanvas().drawTextUI(texts.get(currentText), menuFont, getTextX(),
          getTextY() + textSpacingY * 1.5f, false);

      // Drawing info messages
      float xTextOffset = 120;
      renderer.getGameCanvas().drawTextUI("Press [primary1] to hide", menuShadowFont, width - xTextOffset + 2,
          getTextY() + textSpacingY * 1.5f - 2, false);
      renderer.getGameCanvas().drawTextUI("Press [primary1] to hide", menuFont, width - xTextOffset,
          getTextY() + textSpacingY * 1.5f, false);
      renderer.getGameCanvas().drawTextUI(
          "Page " + (currentText + 1) + "/" + (texts.size()),
          menuFont,
          x + width / 2,
          110,
          true);
    }
  }

  @Override
  public void alwaysUpdate() {
  }

  @Override
  public void neverUpdate() {
  }

  @Override
  public void setAlwaysAnimate(boolean animate) {
  }

  @Override
  public boolean alwaysAnimate() {
    return false;
  }

  @Override
  public void progressFrame() {
  }
}
