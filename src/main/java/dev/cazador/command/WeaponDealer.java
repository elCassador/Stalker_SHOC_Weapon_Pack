package dev.cazador.command;

import dev.cazador.command.ammo.AmmoDealer;
import dev.cazador.command.light.LightDealer;
import dev.cazador.constants.StringConstants;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;

public class WeaponDealer extends AbstractCommandCollection {

    public WeaponDealer() {
        super(StringConstants.WEAPON_DEALER_COMMAND, StringConstants.WEAPON_DEALER_DESC);

        addSubCommand(new LightDealer());
        addSubCommand(new AmmoDealer());
    }
}
