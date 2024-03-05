package edu.cornell.jade.seasthethrone.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import edu.cornell.jade.seasthethrone.util.FilmStrip;

public class RenderingEngine {
  /**
   * The Renderable objects for rendering
   */
  Array<Renderable> renderables;

  /**
   * Internal game canvas to handle rendering
   * TODO: REPLACE WITH HOMEBREWED THING
   */
  GameCanvas canvas;

  /** Font for display text */
  BitmapFont textFont;

  /**
   * FIXME: stop hardcoding texture regions
   */
  private static final Texture PLAYER_TEXTURE_UP = new Texture("playerspriterunfilmstrip_up.png");
  private static final Texture PLAYER_TEXTURE_DOWN = new Texture("playerspriterunfilmstrip_down.png");
  private static final Texture SPEAR_TEXTURE_DOWN = new Texture("spear_up_down.png");
  private static final Texture SPEAR_TEXTURE_LEFT = new Texture("spear_left_right.png");
  FilmStrip filmStrip = new FilmStrip(PLAYER_TEXTURE_DOWN, 1, 7, 6);

  /**
   * FIXME: stop hardcoding texture regions
   */
  private static final Texture FISH_TEXTURE = new Texture("bullet_test.png");

  /**
   * Creates a new RenderingEngine
   */
  public RenderingEngine() {
    renderables = new Array<>();
    canvas = new GameCanvas();

    /** LOADING IN FONT, might be better to have an AssetDirectory later */
    FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("EBGaramond.ttf"));
    FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
    parameter.size = 30; //font size
    textFont = generator.generateFont(parameter);
  }

  /**
   * Adds a renderable object to the list to be rendered
   *
   * @param r The renderable to be added
   */
  public void addRenderable(Renderable r) {
    renderables.add(r);
  }

  /**
   * Draws all the renderable objects in the list to be rendered
   */
  public void drawRenderables() {
    canvas.clear();
    canvas.begin();
    for (Renderable r : renderables) {
      Vector2 position = new Vector2(r.getX(),r.getY());
      float x = toScreenCoord(position).x;
      float y = toScreenCoord(position).y;
      if (r instanceof PlayerRenderable) {
        Texture texture;
        Texture speartexture;
        float spearx = 0;
        float speary = 0;
        if (((PlayerRenderable) r).direction() == 0) {
          texture = PLAYER_TEXTURE_UP;
          speartexture = SPEAR_TEXTURE_DOWN;
          spearx = x + 10;
          speary = y;
        } else if (((PlayerRenderable) r).direction() == 1) {
          texture = PLAYER_TEXTURE_DOWN;
          speartexture = SPEAR_TEXTURE_DOWN;
          spearx = x + 10;
          speary = y;
        } else if (((PlayerRenderable) r).direction() == 2) {
          // FIXME: Replace with Texture Left and Right when we have those
          texture = PLAYER_TEXTURE_DOWN;
          speartexture = SPEAR_TEXTURE_LEFT;
          spearx = x + 6;
          speary = y + 20;
        } else {
          texture = PLAYER_TEXTURE_UP;
          speartexture = SPEAR_TEXTURE_LEFT;
          spearx = x + 6;
          speary = y + 20;
        }
        filmStrip.setTexture(texture);
        filmStrip.setFrame(((PlayerRenderable) r).frameNumber());
        canvas.draw(filmStrip, x, y);
        canvas.draw(speartexture, spearx, speary);
        ((PlayerRenderable) r).updateAnimationFrame();
      } else if (r instanceof FishRenderable) {
        canvas.draw(FISH_TEXTURE, x, y);
      }
    }
    canvas.end();
  }

  public void drawGameOver(){
      canvas.begin();
      canvas.drawTextCentered("Game Over!",textFont, 40f);
      canvas.drawTextCentered("Press R to Restart", textFont, 0);
      canvas.end();
  }

  /**
   * Converts coordinate from world to screen
   *
   * @return screen coordinate
   *
   * @param pos the coordinate to be converted
   */
  //TODO: fix this to utilize the camera, right now collisioin looks sketchy with this transformation. 
  public Vector2 toScreenCoord(Vector2 pos) {
    return pos.scl(10f, 4f).sub(-300f, -45f);
  }
  public void clear(){
    renderables.clear();
  }

  @Override
  public String toString() {
    return "RenderingEngine [renderables=" + renderables + "]";
  }

}
