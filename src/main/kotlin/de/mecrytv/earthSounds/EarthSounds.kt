package de.mecrytv.earthSounds

import de.mecrytv.earthSounds.config.ConfigDefaults
import de.mecrytv.earthSounds.config.ConfigService
import de.mecrytv.earthSounds.config.JsonConfigService
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

class EarthSounds : JavaPlugin() {

    companion object {
        lateinit var instance: EarthSounds
            private set
    }

    lateinit var configService: ConfigService
        private set

    override fun onEnable() {
        instance = this

        configService = JsonConfigService(
            file = File(dataFolder, "config.json"),
            defaults = ConfigDefaults.resource("config.json"),
            logger = logger,
        )
    }

    override fun onDisable() {
        if (::configService.isInitialized) configService.save()
    }
}
