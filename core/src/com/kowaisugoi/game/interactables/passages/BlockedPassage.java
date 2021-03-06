package com.kowaisugoi.game.interactables.passages;

import com.badlogic.gdx.math.Rectangle;
import com.kowaisugoi.game.audio.AudioManager;
import com.kowaisugoi.game.audio.SoundId;
import com.kowaisugoi.game.control.flags.FlagId;
import com.kowaisugoi.game.interactables.objects.ItemId;
import com.kowaisugoi.game.player.Player;
import com.kowaisugoi.game.rooms.RoomId;
import com.kowaisugoi.game.screens.PlayGame;
import com.kowaisugoi.game.system.GameUtil;
import net.dermetfan.gdx.physics.box2d.PositionController;

public class BlockedPassage extends DirectionalPassage {
    private String _lockedText = "";
    private String _unlockText = "";
    private boolean _unlocked = false;
    private ItemId _interactionItemId;

    private SoundId _lockedSoundId;

    private FlagId _unlockFlag;

    public BlockedPassage(RoomId src,
                          RoomId dest,
                          Rectangle interactionBox,
                          GameUtil.Direction direction,
                          ItemId id,
                          String lockedText,
                          String unlockText,
                          SoundId soundId) {
        super(src, dest, interactionBox, direction);
        _lockedText = lockedText;
        _unlockText = unlockText;
        _interactionItemId = id;
        _lockedSoundId = soundId;
    }

    public BlockedPassage(RoomId src,
                          RoomId dest,
                          Rectangle interactionBox,
                          Rectangle silentInteractionBox,
                          GameUtil.Direction direction,
                          ItemId id,
                          String lockedText,
                          String unlockText,
                          SoundId soundId) {
        super(src, dest, interactionBox, silentInteractionBox, direction);
        _lockedText = lockedText;
        _unlockText = unlockText;
        _interactionItemId = id;
        _lockedSoundId = soundId;
    }

    public void setLockedSoundId(SoundId id) {
        _lockedSoundId = id;
    }

    public void setUnlockedToggleFlag(FlagId unlockFlag) {
        _unlockFlag = unlockFlag;
    }

    @Override
    public boolean click(float curX, float curY) {
        if (_interactionBox.contains(curX, curY)) {
            if (_unlocked) {
                return super.click(curX, curY);
            }

            AudioManager.playSound(_lockedSoundId);

            if (GameUtil.isNotNullOrEmpty(_lockedText)) {
                PlayGame.getPlayer().think(_lockedText);
            }
        }
        return false;
    }

    @Override
    public boolean isItemInteractable() {
        return true;
    }

    @Override
    public boolean itemInteract(ItemId id) {
        if (id == _interactionItemId) {
            _unlocked = true;
            if (GameUtil.isNotNullOrEmpty(_unlockText)) {
                PlayGame.getPlayer().think(_unlockText);
                PlayGame.getPlayer().getInventory().removeItem(id);
            }
            PlayGame.getFlagManager().toggleFlag(_unlockFlag);
            return true;
        }
        return false;
    }

    @Override
    public void beautifyCursor(float curX, float curY) {
        super.beautifyCursor(curX, curY);
        //TODO:Fix this mess
        if (PlayGame.getPlayer().getInteractionMode() == Player.InteractionMode.NORMAL
                && !_unlocked
                && getInteractionBox().contains(curX, curY)) {
            PlayGame.getPlayer().setCursor(Player.CursorType.PICKUP);
        }
        if (PlayGame.getPlayer().getInteractionMode() != Player.InteractionMode.ITEM_INTERACTION) {
            return;
        }
        if (getInteractionBox().contains(curX, curY)) {
            PlayGame.getPlayer().setCursor(Player.CursorType.PICKUP);
        }
    }

    @Override
    public boolean checkInteraction(float curX, float curY) {
        if (_interactionBox.contains(curX, curY) || _silentInteractionBox.contains(curX, curY)) {
            return true;
        } else {
            return false;
        }
    }
}
