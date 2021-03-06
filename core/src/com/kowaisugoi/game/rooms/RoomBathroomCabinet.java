package com.kowaisugoi.game.rooms;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.kowaisugoi.game.audio.SoundId;
import com.kowaisugoi.game.control.flags.FlagId;
import com.kowaisugoi.game.interactables.objects.ItemId;
import com.kowaisugoi.game.interactables.objects.PickupableItem;
import com.kowaisugoi.game.interactables.passages.DirectionalPassage;
import com.kowaisugoi.game.interactables.passages.Passage;
import com.kowaisugoi.game.interactables.scenic.Describable;
import com.kowaisugoi.game.interactables.scenic.GeneralDescribable;
import com.kowaisugoi.game.interactables.scenic.ItemInteractableScenic;
import com.kowaisugoi.game.messages.Messages;
import com.kowaisugoi.game.screens.PlayGame;
import com.kowaisugoi.game.system.GameUtil;

import java.util.LinkedList;
import java.util.List;

import static com.kowaisugoi.game.control.flags.FlagId.FLAG_ENTERED_BATHROOM;
import static com.kowaisugoi.game.control.flags.FlagId.FLAG_KEYS_APPEARED;

public class RoomBathroomCabinet extends StandardRoom {

    private static final String ROOM_URL = "rooms/bathroomCabinet/bathroomCabinet.png";
    private static final String TRANSITION_URL = "rooms/bathroomCabinet/uncle_transition.png";
    private List<Passage> _passageList2;
    private List<Describable> _descriptionList1;
    private List<Describable> _descriptionList2;

    public RoomBathroomCabinet() {
        super(new Sprite(new Texture(ROOM_URL)));

        //TODO: Have dead uncle swing by on transition screen?
        Passage passageBack = new DirectionalPassage(RoomId.BATHROOM_CABINET,
                RoomId.BATHROOM,
                new Rectangle(135, 2, 23, 77),
                GameUtil.Direction.RIGHT);

        Passage passageBackScare = new DirectionalPassage(RoomId.BATHROOM_CABINET,
                RoomId.BATHROOM,
                new Rectangle(2, 3, 23, 82),
                GameUtil.Direction.LEFT);
        passageBackScare.setTransitionImage(new Sprite(new Texture(TRANSITION_URL)));
        passageBackScare.setTravelFlag(FlagId.FLAG_ENTERED_BATHROOM);
        passageBackScare.setSoundEffect(SoundId.UNCLE_GASP);
        passageBackScare.setTravelSpeed(270f);

        _passageList2 = new LinkedList<Passage>();
        _passageList2.add(passageBack);

        Describable pills = new GeneralDescribable(Messages.getText("bathroomcabinet.pills.thought"),
                new Rectangle(46, 30, 21, 26));
        Describable scissors = new GeneralDescribable(Messages.getText("bathroomcabinet.scissors.thought"),
                new Rectangle(82, 31, 30, 16));
        Describable towardsBody = new GeneralDescribable(Messages.getText("bathroomcabinet.towardsbody.thought"),
                new Rectangle(2, 3, 23, 82));

        PickupableItem stickWrapped = new PickupableItem(new Sprite(new Texture("items/stickicon_wrapped.png")),
                new Rectangle(0, 0, 0, 0),
                ItemId.STICK_RAGS);

        ItemInteractableScenic bandages = new ItemInteractableScenic(Messages.getText("bathroomcabinet.bandage.thought"),
                Messages.getText("bathroomcabinet.interaction.stick.bandage"),
                new Rectangle(88, 1, 33, 19),
                ItemId.STICK, stickWrapped);

        _descriptionList2 = new LinkedList<Describable>();
        _descriptionList2.add(pills);
        _descriptionList2.add(towardsBody);
        _descriptionList2.add(bandages);

        _descriptionList1 = new LinkedList<Describable>();
        _descriptionList1.add(pills);
        _descriptionList1.add(bandages);

        addPassage(passageBackScare);
        addDescribable(pills);
        addDescribable(bandages);
        addDescribable(scissors);
    }

    @Override
    public void flagUpdate() {
        if (PlayGame.getFlagManager().getFlag(FLAG_ENTERED_BATHROOM).getState() &&
                !PlayGame.getFlagManager().getFlag(FLAG_KEYS_APPEARED).getState()) {
            setPassageList(_passageList2);
            setDescriptionList(_descriptionList2);
        } else if (PlayGame.getFlagManager().getFlag(FLAG_KEYS_APPEARED).getState()) {
            setPassageList(_passageList2);
            setDescriptionList(_descriptionList1);
        }
    }


}
