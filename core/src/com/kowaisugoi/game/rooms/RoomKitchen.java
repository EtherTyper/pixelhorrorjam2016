package com.kowaisugoi.game.rooms;


import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.kowaisugoi.game.interactables.passages.DirectionalPassage;
import com.kowaisugoi.game.interactables.passages.Passage;
import com.kowaisugoi.game.interactables.scenic.Describable;
import com.kowaisugoi.game.interactables.scenic.GeneralDescribable;
import com.kowaisugoi.game.messages.Messages;
import com.kowaisugoi.game.system.GameUtil;

public class RoomKitchen extends StandardRoom {

    private static final String ROOM_URL = "rooms/kitchen/kitchen_draft.png";

    public RoomKitchen() {
        super(new Sprite(new Texture(ROOM_URL)));

        Passage passageHall = new DirectionalPassage(RoomId.BEDROOM,
                RoomId.HALLWAY,
                new Rectangle(50, 0, 50, 15),
                GameUtil.Direction.DOWN);

        addPassage(passageHall);

        Describable sink = new GeneralDescribable(Messages.getText("kitchen.sink.thought"),
                new Rectangle(103, 31, 25, 18));

        Describable toaster = new GeneralDescribable(Messages.getText("kitchen.toaster.thought"),
                new Rectangle(76, 34, 13, 14));

        Describable outlet = new GeneralDescribable(Messages.getText("kitchen.outlet.thought"),
                new Rectangle(80, 56, 4, 7));

        Describable window = new GeneralDescribable(Messages.getText("kitchen.window.thought"),
                new Rectangle(95, 49, 41, 17));

        Describable fridge = new GeneralDescribable(Messages.getText("kitchen.fridge.thought"),
                new Rectangle(36, 12, 37, 58));

        addDescribable(sink);
        addDescribable(toaster);
        addDescribable(outlet);
        addDescribable(window);
        addDescribable(fridge);
    }
}