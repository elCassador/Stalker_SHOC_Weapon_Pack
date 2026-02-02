package dev.cazador.interactions;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.ItemContext;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInstantInteraction;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import dev.cazador.utils.ItemStackUtils;
import dev.cazador.utils.sound.SoundUtils;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class InsertMagazineInteraction extends SimpleInstantInteraction {

    public static final BuilderCodec<InsertMagazineInteraction> CODEC;
    private String loadedGunName;
    private String[] magazineTypes;
    private String insertMagSound = null;

    public String getInsertMagSound() {
        return insertMagSound;
    }

    public void setInsertMagSound(String insertMagSound) {
        this.insertMagSound = insertMagSound;
    }

    public String getLoadedGunName() {
        return loadedGunName;
    }

    public void setLoadedGunName(String loadedGunName) {
        this.loadedGunName = loadedGunName;
    }

    public String[] getMagazineTypes() {
        return magazineTypes;
    }

    public void setMagazineTypes(String[] magazineTypes) {
        this.magazineTypes = magazineTypes;
    }

    @Override
    protected void firstRun(@NotNull InteractionType interactionType,
                            @NotNull InteractionContext interactionContext,
                            @NotNull CooldownHandler cooldownHandler) {
        CommandBuffer<EntityStore> commandBuffer = interactionContext.getCommandBuffer();
        Ref<EntityStore> ref = interactionContext.getEntity();

        if (commandBuffer == null) {
            return;
        }

        Player player = commandBuffer.getComponent(ref, Player.getComponentType());

        if (player == null) {
            return;
        }

        ItemStack unloadedGunItemStack = interactionContext.getHeldItem();
        ItemStack magazineItemStack = player.getInventory().getUtilityItem();

        if (unloadedGunItemStack != null && magazineItemStack != null) {

            String magazineInUtilsName = magazineItemStack.getItem().getId();
            String loadedGunName = getLoadedGunName();
            List<String> magazineTypes = List.of(getMagazineTypes());
            Integer ammo = ItemStackUtils.getCustomInt(magazineItemStack, "Ammo");
            Integer maxAmmo = ItemStackUtils.getCustomInt(magazineItemStack, "MaxAmmo");

            if (!magazineTypes.contains(magazineInUtilsName)) {
                return;
            }

            if (ammo == null) {
                ammo = 0;
            }

            ItemStack loadedGun = new ItemStack(loadedGunName, 1,
                    unloadedGunItemStack.getDurability(), unloadedGunItemStack.getMaxDurability(), null);
            loadedGun = ItemStackUtils.setCustomInt(loadedGun, "Ammo", ammo);
            loadedGun = ItemStackUtils.setCustomInt(loadedGun, "MaxAmmo", maxAmmo);
            loadedGun = ItemStackUtils.setCustomString(loadedGun, "LoadedMagazine", magazineInUtilsName);
            ItemContainer itemContainer = interactionContext.getHeldItemContainer();

            if (itemContainer != null) {
                ItemContext itemContext = new ItemContext(
                        itemContainer,
                        interactionContext.getHeldItemSlot(),
                        unloadedGunItemStack
                );
                itemContainer.replaceItemStackInSlot(itemContext.getSlot(), unloadedGunItemStack, loadedGun);
            }

            ItemContainer utility = player.getInventory().getUtility();

            if (utility != null) {
                utility.removeItemStack(magazineItemStack);
            }

            // sound
            SoundUtils.playSound(player, getInsertMagSound());
        }
    }

    static {
        CODEC = BuilderCodec.builder(InsertMagazineInteraction.class, InsertMagazineInteraction::new, InsertMagazineInteraction.CODEC)
                // AmmoItemType
                .append(new KeyedCodec<>("MagazineTypes", Codec.STRING_ARRAY),
                        InsertMagazineInteraction::setMagazineTypes,
                        InsertMagazineInteraction::getMagazineTypes)
                .add()
                // LoadedGun
                .append(new KeyedCodec<>("LoadedGunName", Codec.STRING),
                        InsertMagazineInteraction::setLoadedGunName,
                        InsertMagazineInteraction::getLoadedGunName)
                .add()
                .append(new KeyedCodec<>("LoadSound", Codec.STRING),
                        InsertMagazineInteraction::setInsertMagSound,
                        InsertMagazineInteraction::getInsertMagSound)
                .add()
                .documentation("Reload gun.")
                .build();
    }
}
