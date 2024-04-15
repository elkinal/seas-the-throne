package edu.cornell.jade.seasthethrone.level;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import edu.cornell.jade.seasthethrone.gamemodel.ObstacleModel;
import edu.cornell.jade.seasthethrone.model.BoxModel;
import edu.cornell.jade.seasthethrone.render.Renderable;
import edu.cornell.jade.seasthethrone.render.RenderingEngine;
import edu.cornell.jade.seasthethrone.util.FilmStrip;

public class LevelObject implements Renderable {

  public float x;
  public float y;
  public float width;
  public float height;
  public TextureRegion texture;
  public String target;
  public String bossName;

  public Vector2 playerLoc;

  /** Vertices for the polygonModel if this is representing a wall */
  public Array<Float> vertices = new Array<>();

  /** Default to transparent texture if none is specified */
  private final TextureRegion DEFAULT_TEXTURE = new TextureRegion(new Texture("empty.png"));

  public Vector2 pos;

  // GateModel state
  public Array<LevelObject> sensors = new Array<>();
  public Array<LevelObject> walls = new Array<>();

  public int id;

  public LevelObject() {}

  public LevelObject(float x, float y) {
    this.x = x;
    this.y = y;
    this.pos = new Vector2(x, y);
    this.texture = DEFAULT_TEXTURE;
  }

  public LevelObject(float x, float y, TextureRegion texture) {
    this(x, y);
    this.texture = texture;
  }

  public LevelObject(float x, float y, Array<Float> vertices) {
    this(x, y);
    this.vertices = vertices;
  }

  public LevelObject(float x, float y, float width, float height) {
    this(x, y);
    this.width = width;
    this.height = height;
  }

  public LevelObject(float x, float y, float width, float height, Vector2 playerLoc) {
    this(x, y, width, height);
    this.playerLoc = playerLoc;
  }

  public LevelObject(float x, float y, float width, float height, TextureRegion texture) {
    this(x, y, width, height);
    this.texture = texture;
  }

  public void setTarget(String target) {
    this.target = target;
  }

  public void setBossName(String name) {
    this.bossName = name;
  }

  public String getBossName() {
    return this.bossName;
  }

  public Vector2 getPosition() {
    return pos;
  }

  @Override
  public void draw(RenderingEngine renderer) {
    renderer.draw(texture, getPosition().x, getPosition().y);
  }

  @Override
  public FilmStrip progressFrame() {
    return null;
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

  public void setTexture(TextureRegion texture) {
    this.texture = texture;
  }

  public int getFrameNumber() {
    return 0;
  }

  public void setFrameNumber(int frameNumber) {}

  public int getFramesInAnimation() {
    return 0;
  }

  public void addVertex(float vertex) {
    vertices.add(vertex);
  }

  /** Converts the array of vertices to a list, used to initialize a polygonModel */
  public float[] toList() {
    float[] list = new float[vertices.size];
    for (int i = 0; i < vertices.size; i++) {
      list[i] = vertices.get(i);
    }
    return list;
  }
}
