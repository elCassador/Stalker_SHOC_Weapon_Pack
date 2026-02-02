package dev.cazador.command.ammo;

import dev.cazador.constants.StringConstants;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractAsyncCommand;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class GiveLightAmmo extends AbstractAsyncCommand {

    public GiveLightAmmo() {
        super(StringConstants.GIVE_LIGHT_AMMO_COMMAND, StringConstants.GIVE_LIGHT_AMMO_DESC);
    }

    @Override
    protected @NotNull CompletableFuture<Void> executeAsync(@NotNull CommandContext commandContext) {
        Player player = commandContext.senderAs(Player.class);
        ItemContainer hotbar = player.getInventory().getHotbar();

        hotbar.addItemStack(new ItemStack("Light_Ammo", 15));
        return null;
    }
}
