package com.zhaoch23.dialog.theme

import ink.ptms.chemdah.core.conversation.theme.ThemeSettings
import ink.ptms.chemdah.taboolib.library.configuration.ConfigurationSection

class ThemeSrDialogSettings(root: ConfigurationSection) : ThemeSettings(root) {

    val settings = mapOf(
        "default-color" to root.getString("default-color", "&f")!!,
        "autoplay" to root.getBoolean("autoplay", false),
        "tick-speed" to root.getLong("tick-speed", 100),
        "repeat-button-text" to root.getBoolean("repeat-button-text", true),
        "close-player-dialog-when-show-button" to root.getBoolean("close-player-dialog-when-show-button", true),
        "esc-cancel" to root.getBoolean("esc-cancel", true),
    )
}