package dev.cazador.command.ammo;

import dev.cazador.constants.StringConstants;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;

public class AmmoDealer extends AbstractCommandCollection {

    public AmmoDealer() {
        super(StringConstants.AMMO_DEALER_COMMAND, StringConstants.AMMO_DEALER_DESC);

        addSubCommand(new GiveLightAmmo());
    }
}
