package com.kowaisugoi.game.interactables.passages;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.kowaisugoi.game.graphics.SlideTransition;
import com.kowaisugoi.game.interactables.InteractionListener;
import com.kowaisugoi.game.interactables.objects.ItemId;
import com.kowaisugoi.game.player.Player;
import com.kowaisugoi.game.rooms.RoomId;
import com.kowaisugoi.game.system.GameUtil.Direction;

import java.util.LinkedList;

public class StandardPassage implements Passage {
    private LinkedList<InteractionListener> _listeners = new LinkedList<InteractionListener>();
    protected Rectangle _interactionBox;
    private RoomId _source, _destination;
    protected SlideTransition _transition;
    private Direction _direction;

    public StandardPassage(RoomId src, RoomId dest, Rectangle interactionBox, Direction direction) {
        _source = src;
        _destination = dest;
        _interactionBox = interactionBox;
        _transition = new SlideTransition(this, src, dest);
        _direction = direction;
    }

    @Override
    public RoomId getDestination() {
        return _destination;
    }

    @Override
    public Rectangle getInteractionBox() {
        return _interactionBox;
    }

    @Override
    public void roomTransition() {
        Player.setCurrentRoom(_destination);
    }

    @Override
    public Direction getDirection() {
        return _direction;
    }

    @Override
    public void draw(SpriteBatch batch) {
        _transition.draw(batch);
    }

    @Override
    public void draw(ShapeRenderer renderer) {
        _transition.draw(renderer);
        if (Player.getDebug()) {
            if (Player.getCanInteract()) {
                renderer.setColor(0, 1, 0, 0.25f);
            } else {
                renderer.setColor(1, 0, 0, 0.25f);
            }
            renderer.rect(_interactionBox.x, _interactionBox.y, _interactionBox.width, _interactionBox.height);
        }
    }

    @Override
    public boolean click(float curX, float curY) {
        if (_interactionBox.contains(curX, curY)) {
            notifyListeners();

            Player.setCanInteract(false);
            _transition.startAnimation(_direction);
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseMoved(float curX, float curY) {
        if (_interactionBox.contains(curX, curY)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void update(float delta) {
        _transition.update(delta);
    }

    @Override
    public void registerListener(InteractionListener lis) {
        _listeners.push(lis);
    }

    @Override
    public boolean itemInteractable() {
        return false;
    }

    @Override
    public boolean itemIteracts(ItemId id) {
        return false;
    }

    private void notifyListeners() {
        for (InteractionListener listener : _listeners) {
            listener.notifyListener();
        }
    }
}
