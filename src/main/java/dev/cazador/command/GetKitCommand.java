package dev.cazador.command;

import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.Inventory;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import org.jetbrains.annotations.NotNull;

public class GetKitCommand extends CommandBase {

    public GetKitCommand(@NotNull String name, @NotNull String description) {
        super(name, description);
    }

    @Override
    protected void executeSync(@NotNull CommandContext commandContext) {
        Player player = commandContext.senderAs(Player.class);
        changePlayerInventory(player);

        // Inventory Changed
    }

    private void changePlayerInventory(Player player) {
        Inventory inventory = player.getInventory();
        ItemContainer itemContainer = inventory.getStorage();

        if (isItemContainerHasFreeSpace(itemContainer)) {
            itemContainer.addItemStack(new ItemStack("Ore_Adamantite", 10));
        }
    }

    private boolean isItemContainerHasFreeSpace(ItemContainer itemContainer) {
        for (short i = 0; i < itemContainer.getCapacity(); i++) {
            if (itemContainer.getItemStack(i) == null) {
                return true;
            }
        }

        return false;
    }
}
