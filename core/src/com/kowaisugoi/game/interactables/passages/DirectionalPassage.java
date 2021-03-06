package com.kowaisugoi.game.interactables.passages;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Timer;
import com.kowaisugoi.game.audio.AudioManager;
import com.kowaisugoi.game.audio.SoundId;
import com.kowaisugoi.game.control.flags.FlagId;
import com.kowaisugoi.game.graphics.SlideTransition;
import com.kowaisugoi.game.interactables.InteractionListener;
import com.kowaisugoi.game.interactables.objects.ItemId;
import com.kowaisugoi.game.player.Player;
import com.kowaisugoi.game.rooms.RoomId;
import com.kowaisugoi.game.screens.PlayGame;
import com.kowaisugoi.game.system.GameUtil.Direction;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class DirectionalPassage implements Passage {
    private LinkedList<InteractionListener> _listeners = new LinkedList<InteractionListener>();
    protected Rectangle _interactionBox;
    protected Rectangle _silentInteractionBox;
    private RoomId _source, _destination;
    protected Direction _direction;
    private boolean _release = true;

    private FlagId _travelFlag;

    private SoundId _soundId;

    private Sprite _transitionSprite;

    private float _travelSpeed = SlideTransition.DEFAULT_SPEED;

    private Map<ItemId, String> _itemInteractionMessages;

    public DirectionalPassage(RoomId src, RoomId dest, Rectangle interactionBox, Direction direction) {
        _source = src;
        _destination = dest;
        _interactionBox = interactionBox;
        _silentInteractionBox = interactionBox;
        _direction = direction;

        _itemInteractionMessages = new HashMap<ItemId, String>();
    }

    public DirectionalPassage(RoomId src, RoomId dest, Rectangle interactionBox, Rectangle silentInteractionBox, Direction direction) {
        _source = src;
        _destination = dest;
        _interactionBox = interactionBox;
        _silentInteractionBox = silentInteractionBox;
        _direction = direction;

        _itemInteractionMessages = new HashMap<ItemId, String>();
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
        PlayGame.getPlayer().enterRoom(_destination);
    }

    @Override
    public void transitionComplete() {
        if (PlayGame.getFlagManager().getFlag(FlagId.FLAG_KEYS_MISSING).getState()) {
            Timer.schedule(new Timer.Task() {
                               @Override
                               public void run() {
                                   if (_release) {
                                       PlayGame.getPlayer().setInteractionMode(Player.InteractionMode.NORMAL);
                                   }
                               }
                           }
                    , 0.65f // Initial delay
                    , 0 // Fire every X seconds
                    , 1 // Number of times to fire
            );
        } else {
            Timer.schedule(new Timer.Task() {
                               @Override
                               public void run() {
                                   if (_release) {
                                       PlayGame.getPlayer().setInteractionMode(Player.InteractionMode.NORMAL);
                                   }
                               }
                           }
                    , 0.4f // Initial delay
                    , 0 // Fire every X seconds
                    , 1 // Number of times to fire
            );
        }
    }

    @Override
    public void setTransitionImage(Sprite sprite) {
        _transitionSprite = sprite;
    }

    @Override
    public void setTravelFlag(FlagId flag) {
        _travelFlag = flag;
    }

    @Override
    public void setTravelSpeed(float speed) {
        _travelSpeed = speed;
    }

    @Override
    public void draw(SpriteBatch batch) {
    }

    public void setAutoRelease(boolean release) {
        _release = release;
    }

    @Override
    public void draw(ShapeRenderer renderer) {
        if (PlayGame.getDebug()) {
            if (PlayGame.getPlayer().getInteractionMode() == Player.InteractionMode.NORMAL) {
                renderer.setColor(0, 1, 0, 0.25f);
            } else if (PlayGame.getPlayer().getInteractionMode() == Player.InteractionMode.ITEM_INTERACTION) {
                renderer.setColor(0, 0, 1, 0.25f);
            } else {
                renderer.setColor(1, 0, 0, 0.25f);
            }
            renderer.rect(_interactionBox.x, _interactionBox.y, _interactionBox.width, _interactionBox.height);
        }
    }

    @Override
    public boolean click(float curX, float curY) {
        if (_interactionBox.contains(curX, curY) || _silentInteractionBox.contains(curX, curY)) {
            notifyListeners();

            PlayGame.getPlayer().setInteractionMode(Player.InteractionMode.NONE);
            PlayGame.playTransition(_transitionSprite == null ?
                    new SlideTransition(this, _direction) :
                    new SlideTransition(this, _direction, _transitionSprite, _travelSpeed));

            AudioManager.playSound(_soundId);

            if (_travelFlag != null && !PlayGame.getFlagManager().getFlag(_travelFlag).getState()) {
                PlayGame.getFlagManager().toggleFlag(_travelFlag);
            }

            return true;
        }
        return false;
    }

    @Override
    public void beautifyCursor(float curX, float curY) {
        if (_interactionBox.contains(curX, curY)) {
            switch (_direction) {
                case UP:
                    PlayGame.getPlayer().setCursor(Player.CursorType.UP_ARROW);
                    break;
                case DOWN:
                    PlayGame.getPlayer().setCursor(Player.CursorType.DOWN_ARROW);
                    break;
                case LEFT:
                    PlayGame.getPlayer().setCursor(Player.CursorType.LEFT_ARROW);
                    break;
                case RIGHT:
                    PlayGame.getPlayer().setCursor(Player.CursorType.RIGHT_ARROW);
                    break;
            }
        }
    }

    @Override
    public void update(float delta) {
    }

    @Override
    public void registerListener(InteractionListener lis) {
        _listeners.push(lis);
    }

    @Override
    public void setSoundEffect(SoundId soundId) {
        _soundId = soundId;
    }

    @Override
    public String getItemInteractionMessage(ItemId id) {
        if (_itemInteractionMessages.containsKey(id)) {
            return _itemInteractionMessages.get(id);
        }
        return "";
    }

    @Override
    public void setItemInteractionMessage(ItemId id, String message) {
        _itemInteractionMessages.put(id, message);
    }

    @Override
    public boolean isItemInteractable() {
        return false;
    }

    @Override
    public boolean itemInteract(ItemId id) {
        return false;
    }

    private void notifyListeners() {
        for (InteractionListener listener : _listeners) {
            listener.notifyListener();
        }
    }

    @Override
    public boolean checkInteraction(float curX, float curY) {
        return _interactionBox.contains(curX, curY);
    }
}
