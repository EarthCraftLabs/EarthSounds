package de.mecrytv.earthSounds

import de.mecrytv.earthcore.config.ConfigDefaults
import de.mecrytv.earthcore.config.ConfigService
import de.mecrytv.earthcore.config.JsonConfigService
import de.mecrytv.earthcore.config.getOrDefault
import de.mecrytv.earthcore.database.api.DatabaseProvider
import de.mecrytv.earthcore.database.api.DatabaseService
import de.mecrytv.earthcore.registry.api.AutoRegistrar
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

class EarthSounds : JavaPlugin() {

    companion object {
        lateinit var instance: EarthSounds
            private set
    }

    lateinit var configService: ConfigService
        private set

    lateinit var database: DatabaseService
        private set

    override fun onEnable() {
        instance = this

        configService = JsonConfigService(
            file = File(dataFolder, "config.json"),
            defaults = ConfigDefaults.resource("config.json", javaClass.classLoader),
            logger = logger,
        )

        val databases = server.servicesManager.load(DatabaseProvider::class.java)
            ?: error("EarthCore ist nicht geladen - fehlt depend: [EarthCore] in der plugin.yml?")
        database = databases.of(configService.getOrDefault("database", "earthsounds"))

        val registrar = server.servicesManager.load(AutoRegistrar::class.java)!!
        val summary = registrar.register(
            this,
            database,
            "de.mecrytv.earthSounds.models",
            "de.mecrytv.earthSounds.commands",
            "de.mecrytv.earthSounds.listeners",
        )

        logger.info("EarthSounds aktiv: $summary")
    }

    override fun onDisable() {
        if (::configService.isInitialized) configService.save()
    }
}
