package dev.cazador.events;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.EntityEventSystem;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.ecs.SwitchActiveSlotEvent;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlayerSwitchFastSlotEvent extends EntityEventSystem<EntityStore, SwitchActiveSlotEvent> {

    public PlayerSwitchFastSlotEvent() {
        super(SwitchActiveSlotEvent.class);
    }

    @Override
    public void handle(int i, @NotNull ArchetypeChunk<EntityStore> archetypeChunk,
                       @NotNull Store<EntityStore> store,
                       @NotNull CommandBuffer<EntityStore> commandBuffer,
                       @NotNull SwitchActiveSlotEvent switchActiveSlotEvent) {
        // Get the player
        Ref<EntityStore> ref = archetypeChunk.getReferenceTo(i);
        Player player = store.getComponent(ref, Player.getComponentType());

        // Display info about item
        player.sendMessage(Message.raw("You cannot break blocks!"));
        switchActiveSlotEvent.setCancelled(true);
    }

    @Override
    public @Nullable Query<EntityStore> getQuery() {
        // Only run for entities with the Player component (only players!)
        return Query.and(
                Player.getComponentType()
        );
    }
}
