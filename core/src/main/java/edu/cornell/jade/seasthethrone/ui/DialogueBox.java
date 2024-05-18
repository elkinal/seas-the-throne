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
  private final TextureRegion boxTextureRegion;

  // Text rendering
  private BitmapFont menuFont;
  private float textSpacingY;
  private final float fontSize = 2.0f;
  private ArrayList<String> texts;
  private int currentText;

  // private String text = "default text \nline 2\nline3";

  /** Constructor for the Pause Menu */
  public DialogueBox() {

    // Loading scroll texture
    boxTextureRegion = new TextureRegion(new Texture("ui/dialoguebox.png"));

    texts = new ArrayList<>();
  }

  /**
   * Ensures the dialogue box stays the same size and remains in the center of the screen when the
   * window is resized.
   */
  public void resize(int screenWidth, int screenHeight) {

    // Initial scale
    if (this.screenWidth == 0) this.screenWidth = screenWidth;
    if (this.screenHeight == 0) this.screenHeight = screenHeight;

    // New position of dialogue box after resizing
    x = ((float) screenWidth / 2) - width / 2;
    y = 25;
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
    // Setting scaling
    float scale = 0.7f * ((float)renderer.getGameCanvas().getWidth() / boxTextureRegion.getRegionWidth());
    width = boxTextureRegion.getRegionWidth() * scale;
    height = boxTextureRegion.getRegionHeight() * scale;

    if (display) {
      x = (renderer.getGameCanvas().getWidth() - width)/2f;
      y = 0.08f * renderer.getGameCanvas().getHeight();
      renderer.getGameCanvas().drawUI(boxTextureRegion, x, y, width, height);
      drawText(renderer);
    }
  }

  /** Draws text to the scroll image */
  private void drawText(RenderingEngine renderer) {
    if (display) {
      // Drawing the main text
      if (this.menuFont != null) menuFont.dispose();
      FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Alagard.ttf"));
      FreeTypeFontGenerator.FreeTypeFontParameter parameter =
              new FreeTypeFontGenerator.FreeTypeFontParameter();

      // NOTE: this is just a hardcoded magic number to get text scaling right
      float fontScale = (float) renderer.getGameCanvas().getWidth() / 1500;

      menuFont = generator.generateFont(parameter);
      menuFont.setUseIntegerPositions(false);
      menuFont.getData().setScale(fontScale*fontSize);
      menuFont.setColor(Color.BLACK);
      generator.dispose();

      if (!texts.isEmpty()) {
        float ox = x + 0.06f*width;
        float oy = y + 0.8f*height;

        renderer.getGameCanvas().drawTextUI(texts.get(currentText), menuFont, ox, oy, false);
      }

      // Drawing info messages
//      float xTextOffset = 120;
//      renderer
//          .getGameCanvas()
//          .drawTextUI(
//              "Press [primary1] to hide",
//              menuShadowFont,
//              width - xTextOffset + 2,
//              getTextY() + textSpacingY * 1.5f - 2,
//              false);
//      renderer
//          .getGameCanvas()
//          .drawTextUI(
//              "Page " + (currentText + 1) + "/" + (texts.size()),
//              menuFont,
//              x + width / 2,
//              110,
//              true);
    }
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
