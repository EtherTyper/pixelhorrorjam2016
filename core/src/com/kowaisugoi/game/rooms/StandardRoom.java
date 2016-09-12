package com.kowaisugoi.game.rooms;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.particles.renderers.PointSpriteRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.kowaisugoi.game.graphics.Transition;
import com.kowaisugoi.game.interactables.Interactable;
import com.kowaisugoi.game.interactables.objects.ItemId;
import com.kowaisugoi.game.interactables.objects.PickupableItem;
import com.kowaisugoi.game.interactables.passages.Passage;
import com.kowaisugoi.game.player.Player;
import com.kowaisugoi.game.screens.World;

import java.util.LinkedList;
import java.util.List;

import static com.kowaisugoi.game.screens.World.GAME_HEIGHT;
import static com.kowaisugoi.game.screens.World.GAME_WIDTH;

/**
 * Use this class as a class to build other rooms off of to avoid
 * code duplication
 */
public abstract class StandardRoom implements Room {

    private Sprite _roomSprite;
    protected List<PickupableItem> _pickupableItemList = new LinkedList<PickupableItem>();
    protected List<Passage> _passageList = new LinkedList<Passage>();
    private Transition _transition = null;

    public StandardRoom(Sprite image) {
        _roomSprite = image;
        _roomSprite.setSize(GAME_WIDTH, GAME_HEIGHT);
    }

    public void enter() {
        // On entering the typical room, allow the player to interact
        World.getPlayer().setInteractionMode(Player.InteractionMode.NORMAL);
    }

    public void enter(Transition t) {
        enter();
        _transition = t;
    }

    public void addPassage(Passage interactable) {
        _passageList.add(interactable);
    }

    public void addPickupableItem(PickupableItem interactable) {
        _pickupableItemList.add(interactable);
    }

    @Override
    public void draw(SpriteBatch batch) {
        _roomSprite.draw(batch);
        for (Interactable interactable : _pickupableItemList) {
            interactable.draw(batch);
        }
    }

    @Override
    public void draw(ShapeRenderer renderer) {
        for (Interactable interactable : _pickupableItemList) {
            interactable.draw(renderer);
        }
    }

    @Override
    public void drawFx(SpriteBatch batch) {
    }

    @Override
    public void beautifyCursor(float curX, float curY) {
        for (Passage passage : _passageList) {
            passage.beautifyCursor(curX, curY);
        }
        for (PickupableItem pickupableItem : _pickupableItemList) {
            pickupableItem.beautifyCursor(curX, curY);
            return;
        }
    }

    @Override
    public boolean click(float curX, float curY) {
        for (Interactable interactable : _passageList) {
            if (interactable.click(curX, curY)) {
                return true;
            }
        }

        for (PickupableItem pickupableItem : _pickupableItemList) {
            if (pickupableItem.click(curX, curY)) {
                World.getPlayer().getInventory().addItem(pickupableItem);
                _pickupableItemList.remove(pickupableItem);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean click(float curX, float curY, ItemId itemId) {
        for (Interactable interactable : _passageList) {

            if (interactable.getInteractionBox().contains(curX, curY)) {
                if (interactable.isItemInteractable()) {
                    if (interactable.itemIteract(itemId)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public void update(float delta) {
        if (_transition != null) {
            _transition.update(delta);
        }
        for (Interactable interactable : _passageList) {
            interactable.update(delta);
        }
    }

    @Override
    public void dispose() {
        _roomSprite.getTexture().dispose();
    }
}
