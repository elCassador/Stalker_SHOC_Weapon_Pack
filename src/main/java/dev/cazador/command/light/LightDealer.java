package dev.cazador.command.light;

import dev.cazador.constants.StringConstants;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;

public class LightDealer extends AbstractCommandCollection {

    public LightDealer() {
        super(StringConstants.LIGHT_DEALER_COMMAND, StringConstants.LIGHT_DEALER_DESC);

        addSubCommand(new GiveLightRifle());
    }
}
