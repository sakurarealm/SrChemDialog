package com.zhaoch23.dialog

import ink.ptms.chemdah.core.conversation.theme.ThemeSettings
import ink.ptms.chemdah.taboolib.library.configuration.ConfigurationSection

class ThemeSrDialogSettings(root: ConfigurationSection) : ThemeSettings(root) {

    val settings = mapOf (
        "default-color" to root.getString("default-color", "&f")!!,
        "autoplay" to root.getBoolean("autoplay", true),
        "tick-speed" to root.getLong("tick-speed", 100),
        "repeat-button-text" to root.getBoolean("repeat-button-text", true)
    )
}