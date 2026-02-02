package dev.cazador.utils.sound;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.protocol.SoundCategory;
import com.hypixel.hytale.server.core.asset.type.soundevent.config.SoundEvent;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entity.EntityModule;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.world.SoundUtil;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

public class SoundUtils {

    public static void playSound(Player player, String sound) {
        int index = SoundEvent.getAssetMap().getIndex(sound);
        World world = player.getWorld();

        if (world == null) {
            return;
        }

        EntityStore store = world.getEntityStore();
        Ref<EntityStore> playerRef = player.getReference();

        if (playerRef == null) {
            return;
        }

        world.execute(() -> {
            TransformComponent transform = store.getStore().getComponent(playerRef, EntityModule.get().getTransformComponentType());

            if (transform == null) {
                return;
            }

            SoundUtil.playSoundEvent3d(index, SoundCategory.UI, transform.getPosition(), store.getStore());
        });
    }
}
