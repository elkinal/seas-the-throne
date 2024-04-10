package edu.cornell.jade.seasthethrone.ui;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Array;
import edu.cornell.jade.seasthethrone.render.Renderable;
import edu.cornell.jade.seasthethrone.render.RenderingEngine;


public class HealthBar implements Renderable {

    /** Current texture for the healthbar */
    private Texture texture;

    /** Textures for each health state, ordered from lowest to highest hp */
    private Array<Texture> textures;

    /** Ratio of width/height for the health bar texture */
    private float aspectRatio;

    /** Scale of health bar on the screen */
    private final float SCALE = 400f;

    public HealthBar() {
        this.textures = new Array<>(6);
        textures.add(new Texture("ui/playerhealth_0.png"));
        textures.add(new Texture("ui/playerhealth_1_5.png"));
        textures.add(new Texture("ui/playerhealth_2_5.png"));
        textures.add(new Texture("ui/playerhealth_3_5.png"));
        textures.add(new Texture("ui/playerhealth_4_5.png"));
        textures.add(new Texture("ui/playerhealth_5_5.png"));
//        textures.insert(0,new Texture("ui/health_hori_0.png"));
//        textures.insert(1,new Texture("ui/health_hori_1.png"));
//        textures.insert(2,new Texture("ui/health_hori_2.png"));
//        textures.insert(3,new Texture("ui/health_hori_3.png"));
//        textures.insert(4,new Texture("ui/health_hori_4.png"));
//        textures.insert(5,new Texture("ui/health_hori_1.png"));
        this.texture = textures.get(5);

        this.aspectRatio = (float) texture.getWidth() /texture.getHeight();
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
        renderer.getGameCanvas().drawUI(
                texture,
                10f,
                10f,
                SCALE*aspectRatio, SCALE);
    }
}
