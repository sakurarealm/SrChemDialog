package com.zhaoch23.dialog

import org.bukkit.Bukkit
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.YamlConfiguration
import taboolib.common.platform.Plugin
import taboolib.platform.BukkitPlugin
import taboolib.platform.util.bukkitPlugin
import java.io.File

object SrChemDialog : Plugin() {

    val instance by lazy {
        BukkitPlugin.getInstance()
    }
    lateinit var germConfig: ConfigurationSection
        private set


    override fun onEnable() {

        val dataFolder = bukkitPlugin.dataFolder
        // Ensure the data folder exists
        dataFolder.mkdirs()

        // Load the configuration
        loadConfiguration(dataFolder)

        // Register the theme
        ThemeSrDialog.register(ThemeSrDialog.name)
    }

    fun loadConfiguration(dataFolder: File) {
        val file = dataFolder.resolve("default.yml")
        // If default.yml does not exist, create it from class resources
        if (!file.exists()) {
            javaClass.getResourceAsStream("/default.yml").use { inputStream ->
                if (inputStream == null) {
                    Bukkit.getLogger().warning("Could not find default.yml in jar file")
                } else {
                    file.outputStream().use { outputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }
            }
        }

        // Load configuration (assuming you have a method to do so)
        val yamlConfiguration = YamlConfiguration.loadConfiguration(file)
        germConfig = yamlConfiguration.getConfigurationSection(SrDialogScreen.guiTitle)!!
    }


}
