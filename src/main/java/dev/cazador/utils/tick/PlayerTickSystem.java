package dev.cazador.utils.tick;

import com.buuz135.mhud.MultipleHUD;
import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.server.core.entity.EntityUtils;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import dev.cazador.utils.hud.AmmoHud;

import javax.annotation.Nonnull;
import java.util.concurrent.CompletableFuture;

/**
 * Отвечает за выдачу того, что держит пресонаж в руке каждый тик
 */
public class PlayerTickSystem extends EntityTickingSystem<EntityStore> {

    @Nonnull
    private final Query<EntityStore> query = Query.and(new Query[]{Player.getComponentType()});

    public PlayerTickSystem() {
    }

    public void tick(float dt, int index,
                     @Nonnull ArchetypeChunk<EntityStore> archetypeChunk,
                     @Nonnull Store<EntityStore> store,
                     @Nonnull CommandBuffer<EntityStore> commandBuffer) {
        Holder<EntityStore> holder = EntityUtils.toHolder(index, archetypeChunk);
        Player player = holder.getComponent(Player.getComponentType());
        PlayerRef playerRef = holder.getComponent(PlayerRef.getComponentType());
        if (player != null && playerRef != null) {

            ItemStack heldItem = player.getInventory().getItemInHand();
            AmmoHud ammoHud = new AmmoHud(playerRef, heldItem);
//            player.getHudManager().setCustomHud(playerRef, ammoHud);
//            MultipleHUD.getInstance().setCustomHud(player, playerRef, "StalkerWeaponHud", ammoHud);
            CompletableFuture.runAsync(() -> {
                MultipleHUD.getInstance().setCustomHud(player, playerRef, "StalkerWeaponHud", ammoHud);
            }, player.getWorld());
        }
    }

    @Nonnull
    public Query<EntityStore> getQuery() {
        return this.query;
    }
}
