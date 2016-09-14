package com.kowaisugoi.game.rooms;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.kowaisugoi.game.graphics.SnowAnimation;
import com.kowaisugoi.game.interactables.passages.DirectionalPassage;
import com.kowaisugoi.game.interactables.passages.Passage;
import com.kowaisugoi.game.messages.MessageProperties;
import com.kowaisugoi.game.screens.PlayGame;
import com.kowaisugoi.game.system.GameUtil;

public class RoomForestPath extends StandardRoom {

    private static final String ROOM_URL = "rooms/path/path.png";
    private boolean _firstTime = true; //S-s-senpai (≧◡≦) ♡

    private SnowAnimation _snowAnimation;

    public RoomForestPath() {
        super(new Sprite(new Texture(ROOM_URL)));

        _snowAnimation = new SnowAnimation(50, 6);

        Passage forward = new DirectionalPassage(RoomId.ROAD, RoomId.FRONTYARD, new Rectangle(60, 20, 30, 60), GameUtil.Direction.UP);
        Passage backward = new DirectionalPassage(RoomId.ROAD, RoomId.CAR, new Rectangle(55, 0, 50, 10), GameUtil.Direction.DOWN);

        addPassage(forward);
        addPassage(backward);
    }

    @Override
    public void update(float delta) {
        _snowAnimation.updateSnow(delta);
        super.update(delta);
    }

    @Override
    public void draw(ShapeRenderer renderer) {
        _snowAnimation.draw(renderer);
        super.draw(renderer);
    }

    @Override
    public void enter() {
        super.enter();

        if (_firstTime) {
            PlayGame.getPlayer().think(MessageProperties.getProperties().getProperty("thought.outside"), 2.0f);
            _firstTime = false;
        }
    }
}