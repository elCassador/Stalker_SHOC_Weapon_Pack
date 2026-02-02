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
import dev.cazador.constants.GunConstants;
import dev.cazador.utils.ItemStackUtils;
import dev.cazador.utils.ProjectileUtil;
import dev.cazador.utils.sound.SoundUtils;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

public class ShootInteraction extends SimpleInstantInteraction {

    public static final BuilderCodec<ShootInteraction> CODEC;

    private int maxAmmo = 0;
    private int pellets = 10;
    private double spread = 0.075;
    private int damage = 0;
    private String shootSound = null;

    /**
     * ХЗ чё делает, но благодаря этой штуке партиклы не проигрываются если Failed
     *
     * @return
     */
    @Nonnull
    @Override
    public WaitForDataFrom getWaitForDataFrom() {
        return WaitForDataFrom.Server;
    }

    /**
     * Отвечает за логику выстрела:
     * проверка есть ли патроны, замена предмета в руке после выстрела с изменением ammo.
     * За выстрел проджектайлом отвечает {@link ProjectileUtil}.
     *
     * @param interactionType
     * @param interactionContext
     * @param cooldownHandler
     */
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
        ItemStack initialGunItemStack = interactionContext.getHeldItem();

        // Базовая проверка. ХЗ нахуя, но пусть будет.
        if (player != null && initialGunItemStack != null) {
            double durability = initialGunItemStack.getDurability();

            if (durability == 0.0) {
                interactionContext.getState().state = InteractionState.Failed;
                return;
            }

            // Далле будем работать с этим ItemStack. В него потом добавим поле Ammo
            // и потом этот обновленный ItemStack вместе с Ammo поменяем в руке.
            initialGunItemStack.withDurability(durability - 1.0);
            ItemStack gunItemStack = initialGunItemStack.withDurability(durability - 1.0);
            Integer ammo = ItemStackUtils.getCustomInt(initialGunItemStack, "Ammo");
            Integer maxAmmo = ItemStackUtils.getCustomInt(initialGunItemStack, "MaxAmmo");

            // Срабатывает прервый раз для initialGunItemStack т.к. по умолчанию при первом запуске поле ammo
            // у него null.
            if (ammo == null && maxAmmo == null) {
                gunItemStack = ItemStackUtils.setCustomInt(gunItemStack, "Ammo", 0);
                ammo = ItemStackUtils.getCustomInt(gunItemStack, "Ammo");
            }

            // suppress warnings
            if (ammo == null) {
                return;
            }

            if (ammo == 0) {
                interactionContext.getState().state = InteractionState.Failed;
                return;
            }

            ammo = ammo - 1;
            gunItemStack = ItemStackUtils.setCustomInt(gunItemStack, "Ammo", ammo);
            ItemContainer itemContainer = interactionContext.getHeldItemContainer();

            // Меняем предмет игрока в руке после выстрела
            if (itemContainer != null) {
                ItemContext itemContext = new ItemContext(
                        itemContainer,
                        interactionContext.getHeldItemSlot(),
                        gunItemStack
                );
                itemContainer.replaceItemStackInSlot(itemContext.getSlot(), initialGunItemStack, gunItemStack);
            }

            // делает проджектайл
            ProjectileUtil.shootProjectile(this.getPellets(),
                    this.getDamage(),
                    this.spread,
                    GunConstants.PROJECTILE_CONFIG_ID,
                    ref,
                    commandBuffer);

            SoundUtils.playSound(player, getShootSound());
        } else {
            interactionContext.getState().state = InteractionState.Failed;
        }
    }

    static {
        CODEC = BuilderCodec.builder(ShootInteraction.class, ShootInteraction::new, SimpleInstantInteraction.CODEC)
                // Damage
                .append(new KeyedCodec<>("Damage", Codec.INTEGER),
                        ShootInteraction::setDamage,
                        ShootInteraction::getDamage)
                .add()
                // MaxAmmo
                .append(new KeyedCodec<>("MaxAmmo", Codec.INTEGER),
                        ShootInteraction::setMaxAmmo,
                        ShootInteraction::getMaxAmmo)
                .add()
                // Pallets
                .append(new KeyedCodec<>("Pellets", Codec.INTEGER),
                        ShootInteraction::setPellets,
                        ShootInteraction::getPellets)
                .add()
                // Spread
                .append(new KeyedCodec<>("Spread", Codec.DOUBLE),
                        ShootInteraction::setSpread,
                        ShootInteraction::getSpread)
                .add()
                // ShootSound
                .append(new KeyedCodec<>("ShootSound", Codec.STRING),
                        ShootInteraction::setShootSound,
                        ShootInteraction::getShootSound)
                .add()
                .documentation("Shoots the gun and consumes one bullet.")
                .build();
    }

    public int getMaxAmmo() {
        return maxAmmo;
    }

    public void setMaxAmmo(int maxAmmo) {
        this.maxAmmo = maxAmmo;
    }

    public int getPellets() {
        return pellets;
    }

    public void setPellets(int pellets) {
        this.pellets = pellets;
    }

    public double getSpread() {
        return spread;
    }

    public void setSpread(double spread) {
        this.spread = spread;
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public String getShootSound() {
        return shootSound;
    }

    public void setShootSound(String shootSound) {
        this.shootSound = shootSound;
    }
}
