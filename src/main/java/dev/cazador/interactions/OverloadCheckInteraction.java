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
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInteraction;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import dev.cazador.utils.ItemStackUtils;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

/**
 * Used in JSON to prevent ammo overload.
 */
public class OverloadCheckInteraction extends SimpleInteraction {

    @Nonnull
    public static final BuilderCodec<OverloadCheckInteraction> CODEC;
    private int maxAmmo = 0;

    @Nonnull
    @Override
    public WaitForDataFrom getWaitForDataFrom() {
        return WaitForDataFrom.Server;
    }

    @Override
    protected void tick0(boolean firstRun, float time,
                         @NotNull InteractionType type,
                         @NotNull InteractionContext context,
                         @NotNull CooldownHandler cooldownHandler) {
        CommandBuffer<EntityStore> commandBuffer = context.getCommandBuffer();
        Ref<EntityStore> ref = context.getEntity();
        boolean interactionStatus = true;

        if (commandBuffer == null) {
            return;
        }

        Player player = commandBuffer.getComponent(ref, Player.getComponentType());
        ItemStack heldItemStack = context.getHeldItem();

        if (heldItemStack == null) {
            interactionStatus = false;
        }

        maxAmmo = getMaxAmmo();
        Integer ammo = ItemStackUtils.getCustomInt(heldItemStack, "Ammo");

        if (ammo != null && ammo >= maxAmmo) {
            interactionStatus = false;
        }

        context.getState().state = interactionStatus ? InteractionState.Finished : InteractionState.Failed;

        super.tick0(firstRun, time, type, context, cooldownHandler);
    }

    static {
        CODEC = BuilderCodec.builder(OverloadCheckInteraction.class, OverloadCheckInteraction::new, SimpleInteraction.CODEC)
                // MaxAmmo
                .append(new KeyedCodec<>("MaxAmmo", Codec.INTEGER),
                        OverloadCheckInteraction::setMaxAmmo,
                        OverloadCheckInteraction::getMaxAmmo)
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
}
