package dev.cazador.interactions;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.protocol.InteractionState;
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

public class UnloadMagazineInteraction extends SimpleInstantInteraction {

    public static final BuilderCodec<UnloadMagazineInteraction> CODEC;
    private int ammo = 0;
    private String ammoItemType = "";
    private String loadAmmoSound = null;

    public String getLoadAmmoSound() {
        return loadAmmoSound;
    }

    public void setLoadAmmoSound(String loadAmmoSound) {
        this.loadAmmoSound = loadAmmoSound;
    }

    public int getAmmo() {
        return ammo;
    }

    public void setAmmo(int ammo) {
        this.ammo = ammo;
    }

    public String getAmmoItemType() {
        return ammoItemType;
    }

    public void setAmmoItemType(String ammoItemType) {
        this.ammoItemType = ammoItemType;
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
        ItemStack magazineItemStack = interactionContext.getHeldItem();

        if (player != null && magazineItemStack != null) {
            Integer ammo = ItemStackUtils.getCustomInt(magazineItemStack, "Ammo");
            String ammoItemType = ItemStackUtils.getCustomString(magazineItemStack, "AmmoItemType");
            ItemStack updatedMagazineItemStack = magazineItemStack.withQuantity(1);
            ItemContainer hotbar = player.getInventory().getHotbar();

            if (ammo == null) {
                updatedMagazineItemStack =
                        ItemStackUtils.setCustomInt(updatedMagazineItemStack, "Ammo", getAmmo());
                ammo = getAmmo();
            }

            if (ammoItemType == null) {
                updatedMagazineItemStack =
                        ItemStackUtils.setCustomString(updatedMagazineItemStack, "AmmoItemType", getAmmoItemType());
                ammoItemType = getAmmoItemType();
            }

            if (ammo <= 0) {
                interactionContext.getState().state = InteractionState.Failed;
                return;
            }

            if (!hasAvailableHotbarSlot(hotbar, ammoItemType)) {
                interactionContext.getState().state = InteractionState.Failed;
                return;
            }

            ammo -= 1;
            updatedMagazineItemStack = ItemStackUtils.setCustomInt(updatedMagazineItemStack, "Ammo", ammo);
            ItemContainer itemContainer = interactionContext.getHeldItemContainer();

            if (updatedMagazineItemStack == null) {
                return;
            }

            // Меняем предмет игрока в руке после перезарядки
            if (itemContainer != null) {
                ItemContext itemContext = new ItemContext(
                        itemContainer,
                        interactionContext.getHeldItemSlot(),
                        updatedMagazineItemStack
                );
                itemContainer.replaceItemStackInSlot(itemContext.getSlot(), magazineItemStack, updatedMagazineItemStack);
                hotbar.addItemStack(new ItemStack(ammoItemType, 1));
            }

            SoundUtils.playSound(player, getLoadAmmoSound());

            interactionContext.getState().state = InteractionState.Finished;
        }
    }

    /**
     * Проверяет досутпные слоты в хотбаре. Если слот не нулл, слот занят не текущими патронами или этих
     * патронов уже макс стак то true.
     * @param hotbar
     * @param ammoItemType
     * @return
     */
    private boolean hasAvailableHotbarSlot(ItemContainer hotbar, String ammoItemType) {
        int capacity = hotbar.getCapacity();

        for (short slot = 0; slot < capacity; slot++) {
            ItemStack stack = hotbar.getItemStack(slot);

            // пустой слот — подходит
            if (stack == null) {
                return true;
            }

            // тот же ammo и стак не полный — подходит
            if (stack.getItemId().equals(ammoItemType)
                    && stack.getQuantity() < stack.getItem().getMaxStack()) {
                return true;
            }
        }

        return false;
    }

    static {
        CODEC = BuilderCodec.builder(UnloadMagazineInteraction.class, UnloadMagazineInteraction::new, SimpleInstantInteraction.CODEC)
                // Ammo
                .append(new KeyedCodec<>("Ammo", Codec.INTEGER),
                        UnloadMagazineInteraction::setAmmo,
                        UnloadMagazineInteraction::getAmmo)
                .add()
                // AmmoItemType
                .append(new KeyedCodec<>("AmmoItemType", Codec.STRING),
                        UnloadMagazineInteraction::setAmmoItemType,
                        UnloadMagazineInteraction::getAmmoItemType)
                .add()
                // AmmoItemType
                .append(new KeyedCodec<>("LoadAmmoSound", Codec.STRING),
                        UnloadMagazineInteraction::setLoadAmmoSound,
                        UnloadMagazineInteraction::getLoadAmmoSound)
                .add()
                .documentation("Load ammo in magazine.")
                .build();
    }
}
