package dev.cazador.utils.hud;

import dev.cazador.utils.ItemStackUtils;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.player.hud.CustomUIHud;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import org.jetbrains.annotations.NotNull;

public class AmmoHud extends CustomUIHud {

    private final ItemStack itemStack;

    public AmmoHud(@NotNull PlayerRef playerRef, ItemStack itemStack) {
        super(playerRef);
        this.itemStack = itemStack;
    }

    @Override
    protected void build(@NotNull UICommandBuilder uiCommandBuilder) {
        uiCommandBuilder.append("MyHUD.ui");

        if (itemStack != null) {
            checkAndSetAmmoMetadata(uiCommandBuilder);
        }
    }

    private void checkAndSetAmmoMetadata(UICommandBuilder uiCommandBuilder) {
        Integer ammo = ItemStackUtils.getCustomInt(itemStack, "Ammo");
        Integer maxAmmo = ItemStackUtils.getCustomInt(itemStack, "MaxAmmo");

        uiCommandBuilder.set("#Icon.ItemId", itemStack.getItemId());

        if (ammo != null && maxAmmo != null) {
            uiCommandBuilder.set("#MyLabel.TextSpans", Message.raw(String.format(
                    "%s / %s", ammo, maxAmmo
            )));
        }
    }
}
