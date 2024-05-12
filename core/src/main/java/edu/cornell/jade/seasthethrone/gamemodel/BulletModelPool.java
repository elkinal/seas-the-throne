package edu.cornell.jade.seasthethrone.gamemodel;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.utils.Pool;

import edu.cornell.jade.seasthethrone.util.FilmStrip;

public class BulletModelPool extends Pool<BulletModel> {

  /** dummy texture region */
  private static final Texture DUMMY = new Texture(new Pixmap(8, 8, Format.RGBA8888));

  /**
   * Constructs a bullet model pool
   * 
   * @param initialCapacity initial capacity of pool
   * @param max             max size of pool
   */
  public BulletModelPool(int initialCapacity, int max) {
    super(initialCapacity, max);
  }

  @Override
  protected BulletModel newObject() {
    BulletModel out = new BulletModel();
    // hardcoded the filmstip parameters because they need to exist
    out.filmStrip = new FilmStrip(DUMMY, 1, 1);
    out.shape = new CircleShape();
    return out;
  }
}
