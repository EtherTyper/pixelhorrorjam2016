package com.kowaisugoi.game.rooms;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Disposable;

public interface Room extends Disposable {
    public void draw(SpriteBatch batch);

    public void draw(ShapeRenderer batch);

    public boolean click(float curX, float curY);

}