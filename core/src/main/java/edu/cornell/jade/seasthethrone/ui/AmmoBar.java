package edu.cornell.jade.seasthethrone.ui;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Array;
import edu.cornell.jade.seasthethrone.render.Renderable;
import edu.cornell.jade.seasthethrone.render.RenderingEngine;
import edu.cornell.jade.seasthethrone.util.FilmStrip;

public class AmmoBar implements Renderable {

    /** Current texture for the ammo */
    private Texture texture;

    /** Textures for each ammo state, ordered from lowest to highest ammo */
    private Array<Texture> textures;

    private Vector2 playerPos;
    public AmmoBar() {
        this.textures = new Array<>();
        this.playerPos = new Vector2();
        textures.add(new Texture("empty.png"));
        textures.add(new Texture("ui/darkammo_1_5.png"));
        textures.add(new Texture("ui/darkammo_2_5.png"));
        textures.add(new Texture("ui/darkammo_3_5.png"));
        textures.add(new Texture("ui/darkammo_4_5.png"));
        textures.add(new Texture("ui/darkammo_5_5.png"));
        this.texture = textures.get(textures.size - 1);

    }

    /** Updates the texture of this health bar to match player health */
    public void update(int currAmmo, Vector2 pos) {
        if (currAmmo > textures.size || currAmmo < 1) {
            this.texture = textures.get(0);
            return;
        }

        this.texture = textures.get(currAmmo);
        this.playerPos = pos;
    }

    @Override
    public void draw(RenderingEngine renderer) {
        renderer.draw(new TextureRegion(texture), playerPos.x, playerPos.y - 2.5f);
    }

    @Override
    public FilmStrip progressFrame() {
        return null;
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
}
