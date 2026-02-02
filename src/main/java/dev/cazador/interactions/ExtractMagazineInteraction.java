package dev.cazador.interactions;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.protocol.WaitForDataFrom;
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

import javax.annotation.Nonnull;

public class ExtractMagazineInteraction extends SimpleInstantInteraction {

    public static final BuilderCodec<ExtractMagazineInteraction> CODEC;
    private String unloadedGunName = null;
    private int ammo;
    private int maxAmmo;
    private String unloadMagaSound = null;

    public String getUnloadedGunName() {
        return unloadedGunName;
    }

    public void setUnloadedGunName(String unloadedGunName) {
        this.unloadedGunName = unloadedGunName;
    }

    public int getAmmo() {
        return ammo;
    }

    public void setAmmo(int ammo) {
        this.ammo = ammo;
    }

    public int getMaxAmmo() {
        return maxAmmo;
    }

    public void setMaxAmmo(int maxAmmo) {
        this.maxAmmo = maxAmmo;
    }

    public String getUnloadMagaSound() {
        return unloadMagaSound;
    }

    public void setUnloadMagaSound(String unloadMagaSound) {
        this.unloadMagaSound = unloadMagaSound;
    }

    @Nonnull
    @Override
    public WaitForDataFrom getWaitForDataFrom() {
        return WaitForDataFrom.Server;
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
        ItemStack loadedGunItemStack = interactionContext.getHeldItem();

        if (loadedGunItemStack == null) {
            return;
        }

        String magazineName = ItemStackUtils.getCustomString(loadedGunItemStack, "LoadedMagazine");
        Integer ammo = ItemStackUtils.getCustomInt(loadedGunItemStack, "Ammo");
        Integer maxAmmo = ItemStackUtils.getCustomInt(loadedGunItemStack, "MaxAmmo");
        String unloadedGunName = getUnloadedGunName();

        // Suppress warnings
        if (player == null || magazineName == null) {
            return;
        }

        ItemContainer hotbar = player.getInventory().getHotbar();

        if (!hasAvailableHotbarSlot(hotbar)) {
            return;
        }

        ItemStack unloadedGunItemStack = new ItemStack(unloadedGunName, 1,
                loadedGunItemStack.getDurability(), loadedGunItemStack.getMaxDurability(), null);
        ItemContainer itemContainer = interactionContext.getHeldItemContainer();

        if (itemContainer != null) {
            ItemContext itemContext = new ItemContext(
                    itemContainer,
                    interactionContext.getHeldItemSlot(),
                    loadedGunItemStack
            );
            itemContainer.replaceItemStackInSlot(itemContext.getSlot(), loadedGunItemStack, unloadedGunItemStack);
        }

        ItemStack unloadedMagazine = new ItemStack(magazineName);
        unloadedMagazine = ItemStackUtils.setCustomInt(unloadedMagazine, "Ammo", ammo);
        unloadedMagazine = ItemStackUtils.setCustomInt(unloadedMagazine, "MaxAmmo", maxAmmo);

        // custom sounds
        SoundUtils.playSound(player, getUnloadMagaSound());

        hotbar.addItemStack(unloadedMagazine);
    }

    private boolean hasAvailableHotbarSlot(ItemContainer hotbar) {
        int capacity = hotbar.getCapacity();

        for (short slot = 0; slot < capacity; slot++) {
            ItemStack stack = hotbar.getItemStack(slot);

            // пустой слот — подходит
            if (stack == null) {
                return true;
            }
        }

        return false;
    }

    /**
     * Тут переменные из интеракции.
     */
    static {
        CODEC = BuilderCodec.builder(ExtractMagazineInteraction.class, ExtractMagazineInteraction::new, SimpleInstantInteraction.CODEC)
                // LoadedGun
                .append(new KeyedCodec<>("UnloadedGunName", Codec.STRING),
                        ExtractMagazineInteraction::setUnloadedGunName,
                        ExtractMagazineInteraction::getUnloadedGunName)
                .add()
                .append(new KeyedCodec<>("UnloadSound", Codec.STRING),
                        ExtractMagazineInteraction::setUnloadMagaSound,
                        ExtractMagazineInteraction::getUnloadMagaSound)
                .add()
                .documentation("Extract magazine from gun.")
                .build();
    }
}
