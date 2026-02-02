package dev.cazador;

import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.Interaction;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import dev.cazador.command.GetKitCommand;
import dev.cazador.command.WeaponDealer;
import dev.cazador.constants.StringConstants;
import dev.cazador.interactions.ExtractMagazineInteraction;
import dev.cazador.interactions.InsertMagazineInteraction;
import dev.cazador.interactions.LoadMagazineInteraction;
import dev.cazador.interactions.OverloadCheckInteraction;
import dev.cazador.interactions.ShootInteraction;
import dev.cazador.interactions.UnloadMagazineInteraction;
import dev.cazador.utils.tick.PlayerTickSystem;

public class StalkerWeaponPlugin extends JavaPlugin {
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    public StalkerWeaponPlugin(JavaPluginInit init) {
        super(init);
        LOGGER.atInfo().log("Hello from %s version %s", this.getName(), this.getManifest().getVersion().toString());
    }

    @Override
    protected void setup() {
        this.getCommandRegistry().registerCommand(new GetKitCommand(StringConstants.KIT_COMMAND, StringConstants.KIT_DESC));
        this.getCommandRegistry().registerCommand(new WeaponDealer());
        // interactions
        this.getCodecRegistry(Interaction.CODEC).register("Shoot", ShootInteraction.class, ShootInteraction.CODEC);
        this.getCodecRegistry(Interaction.CODEC).register("Insert_Magazine", InsertMagazineInteraction.class, InsertMagazineInteraction.CODEC);
        this.getCodecRegistry(Interaction.CODEC).register("Extract_Magazine", ExtractMagazineInteraction.class, ExtractMagazineInteraction.CODEC);
        this.getCodecRegistry(Interaction.CODEC).register("Load_Magazine", LoadMagazineInteraction.class, LoadMagazineInteraction.CODEC);
        this.getCodecRegistry(Interaction.CODEC).register("Unload_Magazine", UnloadMagazineInteraction.class, UnloadMagazineInteraction.CODEC);
        this.getCodecRegistry(Interaction.CODEC).register("Overload_Check", OverloadCheckInteraction.class, OverloadCheckInteraction.CODEC);
        // custom held item detection system
        this.getEntityStoreRegistry().registerSystem(new PlayerTickSystem());
    }
}
