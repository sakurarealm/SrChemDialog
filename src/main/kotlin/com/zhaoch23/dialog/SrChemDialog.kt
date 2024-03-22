package com.zhaoch23.dialog

import com.zhaoch23.dialog.theme.ThemeSrDialog
import ink.ptms.chemdah.taboolib.module.configuration.Configuration
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.YamlConfiguration
import taboolib.common.platform.Plugin
import taboolib.platform.BukkitPlugin
import taboolib.platform.util.bukkitPlugin
import java.io.File
import java.io.InputStreamReader

typealias TaboolibConfigurationSection = ink.ptms.chemdah.taboolib.library.configuration.ConfigurationSection

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
        ThemeSrDialog.register(ThemeSrDialog.THEME_NAME)
    }

    fun loadConfiguration(dataFolder: File, fromResource: Boolean=true) {
        val file = dataFolder.resolve("default.yml")

        val reader = InputStreamReader(javaClass.getResourceAsStream("/default.yml")!!)
        val defaultConfig = YamlConfiguration.loadConfiguration(reader)
        val defaultTitle = defaultConfig.getKeys(false).first()

        germConfig = if (!file.exists()) {
            defaultConfig.save(file)
            defaultConfig.getConfigurationSection(defaultTitle)!!
        } else {
            // Replace the path sr-dialog.options.script.methods for update
            val yamlConfiguration = YamlConfiguration.loadConfiguration(file)
            val title = yamlConfiguration.getKeys(false).first()!!
            if (fromResource) {
                val methods = defaultConfig.get("$defaultTitle.options.script.methods")!!
                yamlConfiguration.set("$title.options.script.methods", methods)
                yamlConfiguration.save(file)
            }
            yamlConfiguration.getConfigurationSection(title)!!
        }

        // Load style.yml
        val styleFile = dataFolder.resolve("style.yml")
        if (!styleFile.exists()) {
            instance.saveResource("style.yml", false)
            Configuration.loadFromFile(styleFile).getConfigurationSection("theme-sr-dialog")!!
        } else {
            val config = Configuration.loadFromFile(styleFile)
            val configSection = config.getConfigurationSection("theme-sr-dialog")!!

            if (fromResource) {
                val defaultStyleConfigSection = Configuration
                    .loadFromInputStream(javaClass.getResourceAsStream("/style.yml")!!)
                    .getConfigurationSection("theme-sr-dialog")!!
                // Migrate any missing keys
                defaultStyleConfigSection.getKeys(true).forEach { key ->
                    if (!configSection.contains(key)) {
                        configSection[key] = defaultStyleConfigSection[key]
                    }
                }
                config.saveToFile(styleFile)
            }
        }

        ThemeSrDialog.settings = ThemeSrDialog.createConfig()
    }


}
