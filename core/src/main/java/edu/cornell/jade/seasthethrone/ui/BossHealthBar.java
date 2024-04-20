// package edu.cornell.jade.seasthethrone.ui;
//
// import com.badlogic.gdx.graphics.Color;
// import com.badlogic.gdx.graphics.Texture;
// import com.badlogic.gdx.graphics.g2d.TextureRegion;
// import com.badlogic.gdx.utils.Array;
// import edu.cornell.jade.seasthethrone.render.Renderable;
// import edu.cornell.jade.seasthethrone.render.RenderingEngine;
// import edu.cornell.jade.seasthethrone.util.FilmStrip;
//
// public class BossHealthBar implements Renderable {
//  // statusBar is a "texture atlas." Break it up into parts.
//  /** Left cap to the status background (grey region) */
//  private TextureRegion statusBkgLeft;
//
//  /** Middle portion of the status background (grey region) */
//  private TextureRegion statusBkgMiddle;
//
//  /** Right cap to the status background (grey region) */
//  private TextureRegion statusBkgRight;
//
//  /** Left cap to the status forground (colored region) */
//  private TextureRegion statusFrgLeft;
//
//  /** Middle portion of the status forground (colored region) */
//  private TextureRegion statusFrgMiddle;
//
//  /** Right cap to the status forground (colored region) */
//  private TextureRegion statusFrgRight;
//
//  /** Ratio of width/height for the health bar texture */
//  private float aspectRatio;
//
//  /** Scale of health bar on the screen */
//  private final float SCALE = 100f;
//
//  /** The width of the progress bar */
//  private int height;
//
//  /** The width of the progress bar */
//  private int width;
//
//  /** The y-coordinate of the center of the progress bar */
//  private int centerY;
//
//  /** The x-coordinate of the center of the progress bar */
//  private int centerX;
//
//  /** The height of the canvas window (necessary since sprite origin != screen origin) */
//  private int heightY;
//
//  /** Current boss HP */
//  private int bossHP;
//
//  public BossHealthBar() {
//    height = 0;
//    width = 0;
//  }
//
//  /** Does nothing except update the current boss HP, the hp bar is updated in draw. */
//  public void update(int currHealth) {
//    bossHP = currHealth;
//    drawProgress();
//  }
//
//  @Override
//  public void draw(RenderingEngine renderer) {
//    if (height == 0) {
//      height = renderer.getGameCanvas().getHeight();
//    }
//
//    renderer.getGameCanvas().drawUI(texture, width / 2f, 0.9f * height, SCALE * aspectRatio,
// SCALE);
//  }
//
//  private void drawProgress(RenderingEngine renderer) {
//    renderer
//        .getGameCanvas()
//        .draw(
//            statusBkgLeft,
//            Color.WHITE,
//            centerX - width / 2,
//            centerY,
//            scale * statusBkgLeft.getRegionWidth(),
//            scale * statusBkgLeft.getRegionHeight());
//    renderer
//        .getGameCanvas()
//        .draw(
//            statusBkgRight,
//            Color.WHITE,
//            centerX + width / 2 - scale * statusBkgRight.getRegionWidth(),
//            centerY,
//            scale * statusBkgRight.getRegionWidth(),
//            scale * statusBkgRight.getRegionHeight());
//    renderer
//        .getGameCanvas()
//        .draw(
//            statusBkgMiddle,
//            Color.WHITE,
//            centerX - width / 2 + scale * statusBkgLeft.getRegionWidth(),
//            centerY,
//            width - scale * (statusBkgRight.getRegionWidth() + statusBkgLeft.getRegionWidth()),
//            scale * statusBkgMiddle.getRegionHeight());
//
//    renderer
//        .getGameCanvas()
//        .draw(
//            statusFrgLeft,
//            Color.WHITE,
//            centerX - width / 2,
//            centerY,
//            scale * statusFrgLeft.getRegionWidth(),
//            scale * statusFrgLeft.getRegionHeight());
//    if (bossHP > 0) {
//      float span =
//          progress
//              * (width - scale * (statusFrgLeft.getRegionWidth() +
// statusFrgRight.getRegionWidth()))
//              / 2.0f;
//      renderer
//          .getGameCanvas()
//          .draw(
//              statusFrgRight,
//              Color.WHITE,
//              centerX - width / 2 + scale * statusFrgLeft.getRegionWidth() + span,
//              centerY,
//              scale * statusFrgRight.getRegionWidth(),
//              scale * statusFrgRight.getRegionHeight());
//      renderer
//          .getGameCanvas()
//          .draw(
//              statusFrgMiddle,
//              Color.WHITE,
//              centerX - width / 2 + scale * statusFrgLeft.getRegionWidth(),
//              centerY,
//              span,
//              scale * statusFrgMiddle.getRegionHeight());
//    } else {
//      renderer
//          .getGameCanvas()
//          .draw(
//              statusFrgRight,
//              Color.WHITE,
//              centerX - width / 2 + scale * statusFrgLeft.getRegionWidth(),
//              centerY,
//              scale * statusFrgRight.getRegionWidth(),
//              scale * statusFrgRight.getRegionHeight());
//    }
//  }
//
//  @Override
//  public FilmStrip progressFrame() {
//    return null;
//  }
//
//  @Override
//  public void alwaysUpdate() {}
//
//  @Override
//  public void neverUpdate() {}
//
//  @Override
//  public void setAlwaysAnimate(boolean animate) {}
//
//  @Override
//  public boolean alwaysAnimate() {
//    return false;
//  }
// }
