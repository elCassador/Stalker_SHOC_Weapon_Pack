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
import com.hypixel.hytale.server.core.inventory.container.CombinedItemContainer;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInstantInteraction;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import dev.cazador.utils.ItemStackUtils;
import dev.cazador.utils.sound.SoundUtils;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

public class LoadMagazineInteraction extends SimpleInstantInteraction {

    public static final BuilderCodec<LoadMagazineInteraction> CODEC;
    private int maxAmmo;
    private int ammo = 0;
    private String ammoItemType = null;
    private String loadAmmoSound = null;
    private int ammoPerLoad = 0;

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
            Integer maxAmmo = ItemStackUtils.getCustomInt(magazineItemStack, "MaxAmmo");
            Integer ammo = ItemStackUtils.getCustomInt(magazineItemStack, "Ammo");
            String ammoItemType = ItemStackUtils.getCustomString(magazineItemStack, "AmmoItemType");
            ItemStack updatedMagazineItemStack = magazineItemStack.withQuantity(1);

            if (updatedMagazineItemStack == null) {
                return;
            }

            if (ammo == null) {
                updatedMagazineItemStack = ItemStackUtils.setCustomInt(updatedMagazineItemStack, "Ammo", getAmmo());
                ammo = ItemStackUtils.getCustomInt(updatedMagazineItemStack, "Ammo");
            }

            if (maxAmmo == null) {
                updatedMagazineItemStack = ItemStackUtils.setCustomInt(updatedMagazineItemStack, "MaxAmmo", getMaxAmmo());
                maxAmmo = ItemStackUtils.getCustomInt(updatedMagazineItemStack, "MaxAmmo");
            }

            // suppress warnings
            if (ammo == null || maxAmmo == null) {
                return;
            }

            if (ammoItemType == null) {
                updatedMagazineItemStack = ItemStackUtils.setCustomString(updatedMagazineItemStack, "AmmoItemType", getAmmoItemType());
                ammoItemType = ItemStackUtils.getCustomString(updatedMagazineItemStack, "AmmoItemType");
            }

            if (ammo >= maxAmmo) {
                interactionContext.getState().state = InteractionState.Failed;
                return;
            }

            int ammoPerLoad = (getAmmoPerLoad() == 0) ? 1 : getAmmoPerLoad();

            // in case when ammo less than ammoPerLoad
            ammoPerLoad = howManyAmmoLeft(player, ammoItemType, ammoPerLoad);

            // Prevent ammo overflow
            ammo += ammoPerLoad;

            if (ammo > maxAmmo) {
                int diff = ammo - maxAmmo;
                ammoPerLoad -= diff;
                ammo = maxAmmo;
            }

            if (!consumeAmmo(player, ammoItemType, ammoPerLoad)) {
                interactionContext.getState().state = InteractionState.Failed;
                return;
            }

            updatedMagazineItemStack = ItemStackUtils.setCustomInt(updatedMagazineItemStack, "Ammo", ammo);
            ItemContainer itemContainer = interactionContext.getHeldItemContainer();

            // Меняем предмет игрока в руке после перезарядки
            if (itemContainer != null) {
                ItemContext itemContext = new ItemContext(
                        itemContainer,
                        interactionContext.getHeldItemSlot(),
                        updatedMagazineItemStack
                );
                itemContainer.replaceItemStackInSlot(itemContext.getSlot(), magazineItemStack, updatedMagazineItemStack);
            }

            SoundUtils.playSound(player, getLoadAmmoSound());

            interactionContext.getState().state = InteractionState.Finished;
        }
    }

    private boolean consumeAmmo(Player player, String ammoItemType, int ammoPerLoad) {
        if (ammoItemType == null || ammoItemType.isEmpty()) {
            return false;
        }

        CombinedItemContainer container =
                player.getInventory().getCombinedHotbarFirst();

        ItemStack ammoStack = new ItemStack(ammoItemType, ammoPerLoad);

        if (!container.canRemoveItemStack(ammoStack)) {
            return false;
        }

        container.removeItemStack(ammoStack);
        return true;
    }

    /**
     * Searches for an available number of ammo that is less than ammoPerLoad.
     * @param player
     * @param ammoItemType
     * @param ammoPerLoad
     * @return
     */
    private int howManyAmmoLeft(Player player, String ammoItemType, int ammoPerLoad) {
        CombinedItemContainer container =
                player.getInventory().getCombinedHotbarFirst();

        ItemStack ammoStack;

        for (int i = ammoPerLoad; i > 0; i--) {
            ammoStack = new ItemStack(ammoItemType, i);

            if (container.canRemoveItemStack(ammoStack)) {
                return i;
            }
        }

        return ammoPerLoad;
    }

    static {
        CODEC = BuilderCodec.builder(LoadMagazineInteraction.class, LoadMagazineInteraction::new, SimpleInstantInteraction.CODEC)
                // MaxAmmo
                .append(new KeyedCodec<>("MaxAmmo", Codec.INTEGER),
                        LoadMagazineInteraction::setMaxAmmo,
                        LoadMagazineInteraction::getMaxAmmo)
                .add()
                // Ammo
                .append(new KeyedCodec<>("Ammo", Codec.INTEGER),
                        LoadMagazineInteraction::setAmmo,
                        LoadMagazineInteraction::getAmmo)
                .add()
                .append(new KeyedCodec<>("AmmoItemType", Codec.STRING),
                        LoadMagazineInteraction::setAmmoItemType,
                        LoadMagazineInteraction::getAmmoItemType)
                .add()
                .append(new KeyedCodec<>("LoadAmmoSound", Codec.STRING),
                        LoadMagazineInteraction::setLoadAmmoSound,
                        LoadMagazineInteraction::getLoadAmmoSound)
                .add()
                .append(new KeyedCodec<>("AmmoPerLoad", Codec.INTEGER),
                        LoadMagazineInteraction::setAmmoPerLoad,
                        LoadMagazineInteraction::getAmmoPerLoad)
                .add()
                .documentation("Load ammo in magazine.")
                .build();
    }

    public int getMaxAmmo() {
        return maxAmmo;
    }

    public void setMaxAmmo(int maxAmmo) {
        this.maxAmmo = maxAmmo;
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

    public String getLoadAmmoSound() {
        return loadAmmoSound;
    }

    public void setLoadAmmoSound(String loadAmmoSound) {
        this.loadAmmoSound = loadAmmoSound;
    }

    public int getAmmoPerLoad() {
        return ammoPerLoad;
    }

    public void setAmmoPerLoad(int ammoPerLoad) {
        this.ammoPerLoad = ammoPerLoad;
    }
}
