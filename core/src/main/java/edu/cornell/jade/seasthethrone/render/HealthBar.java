package edu.cornell.jade.seasthethrone.render;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Array;


public class HealthBar implements Renderable{

    /** Current texture for the healthbar */
    private Texture texture;

    /** Textures for each health state, ordered from lowest to highest hp */
    private Array<Texture> textures;

    /** Ratio of width/height for the health bar texture */
    private float aspectRatio;

    /** Scale of health bar on the screen */
    private final float SCALE = 400f;

    public HealthBar() {
        this.textures = new Array<>();
        textures.add(new Texture("ui/playerhealth_0.png"));
        textures.add(new Texture("ui/playerhealth_1_5.png"));
        textures.add(new Texture("ui/playerhealth_2_5.png"));
        textures.add(new Texture("ui/playerhealth_3_5.png"));
//        textures.add(new Texture("ui/playerhealth_4_5.png"));
//        textures.add(new Texture("ui/playerhealth_5_5.png"));
        this.texture = textures.get(textures.size - 1);

        this.aspectRatio = (float) texture.getWidth() /texture.getHeight();
    }

    /** Updates the texture of this health bar to match player health */
    public void update(int currHealth) {
        if (currHealth > textures.size || currHealth < 1) {
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
