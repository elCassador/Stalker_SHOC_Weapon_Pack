package dev.cazador.command.light;

import dev.cazador.constants.StringConstants;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractAsyncCommand;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class GiveLightRifle extends AbstractAsyncCommand {

    public GiveLightRifle() {
        super(StringConstants.GIVE_LIGHT_RIFLE_COMMAND, StringConstants.GIVE_LIGHT_RIFLE_DESC);
    }

    @Override
    protected @NotNull CompletableFuture<Void> executeAsync(@NotNull CommandContext commandContext) {
        Player player = commandContext.senderAs(Player.class);
        ItemContainer hotbar = player.getInventory().getHotbar();

        hotbar.addItemStack(new ItemStack("Assault_Rifle", 1));

        return null;
    }
}
