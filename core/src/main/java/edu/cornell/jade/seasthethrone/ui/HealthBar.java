package edu.cornell.jade.seasthethrone.ui;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import edu.cornell.jade.seasthethrone.render.Renderable;
import edu.cornell.jade.seasthethrone.render.RenderingEngine;


public class HealthBar implements Renderable {

    /** Current texture for the healthbar */
    private TextureRegion texture;

    /** Textures for each health state, ordered from lowest to highest hp */
    private Array<TextureRegion> textures;

    /** Ratio of width/height for the health bar texture */
    private float aspectRatio;

    /** Scale of health bar on the screen */
    private final float SCALE = 100f;

    private float height;

    public HealthBar() {
        this.textures = new Array<>(6);
//        textures.add(new Texture("ui/playerhealth_0.png"));
//        textures.add(new Texture("ui/playerhealth_1_5.png"));
//        textures.add(new Texture("ui/playerhealth_2_5.png"));
//        textures.add(new Texture("ui/playerhealth_3_5.png"));
//        textures.add(new Texture("ui/playerhealth_4_5.png"));
//        textures.add(new Texture("ui/playerhealth_5_5.png"));
        textures.add(new TextureRegion(new Texture("ui/health_v3_0.png")));
        textures.add(new TextureRegion(new Texture("ui/health_v3_1.png")));
        textures.add(new TextureRegion(new Texture("ui/health_v3_2.png")));
        textures.add(new TextureRegion(new Texture("ui/health_v3_3.png")));
        textures.add(new TextureRegion(new Texture("ui/health_v3_4.png")));
        textures.add(new TextureRegion(new Texture("ui/health_v3_5.png")));

        this.texture = textures.get(textures.size - 1);
        this.aspectRatio = (float) texture.getRegionWidth() /texture.getRegionHeight();
        height = 0;
    }

    /** Updates the texture of this health bar to match player health */
    public void update(int currHealth) {
        if (currHealth < 0) {
            this.texture = textures.get(0);
            return;
        }
        this.texture = textures.get(currHealth);
    }

    @Override
    public void draw(RenderingEngine renderer) {
        if (height == 0) {height = renderer.getGameCanvas().getHeight();}

        renderer.getGameCanvas().drawUI(
                texture,
                10f,
                0.9f*height,
                SCALE*aspectRatio,
                SCALE);
    }
}
