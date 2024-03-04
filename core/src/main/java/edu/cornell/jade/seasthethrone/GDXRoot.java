package edu.cornell.jade.seasthethrone;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class GDXRoot extends Game {
    private SpriteBatch batch;
    private Texture image;

    private GameplayController controller;

    @Override
    public void create() {
        batch = new SpriteBatch();
        image = new Texture("libgdx.png");

        controller = new GameplayController();
        setScreen(controller);
    }

//    @Override
//    public void render() {
//        Gdx.gl.glClearColor(0.15f, 0.15f, 0.2f, 1f);
//        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
//        batch.begin();
//        batch.draw(image, 140, 210);
//        batch.end();
//    }

    @Override
    public void dispose() {
        batch.dispose();
        image.dispose();
    }
}
