package edu.cornell.jade.seasthethrone.render;

public class PlayerTest implements PlayerRenderable{
    @Override
    public boolean spearExtended() {
        return false;
    }

    @Override
    public float getX() {
        return 80f;
    }

    @Override
    public float getY() {
        return 80f;
    }
}
