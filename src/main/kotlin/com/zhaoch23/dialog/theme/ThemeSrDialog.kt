package com.zhaoch23.dialog.theme

import com.germ.germplugin.api.dynamic.gui.GuiManager
import com.zhaoch23.dialog.SrChemDialog
import ink.ptms.chemdah.api.ChemdahAPI.conversationSession
import ink.ptms.chemdah.api.event.collect.ConversationEvents
import ink.ptms.chemdah.core.conversation.Session
import ink.ptms.chemdah.core.conversation.theme.Theme
import ink.ptms.chemdah.taboolib.library.configuration.ConfigurationSection
import ink.ptms.chemdah.taboolib.module.configuration.Configuration
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import taboolib.common.platform.event.SubscribeEvent
import java.util.concurrent.CompletableFuture

object ThemeSrDialog : Theme<ThemeSrDialogSettings>() {

    const val THEME_NAME = "sr-dialog"

    fun getScreenTitle(player: Player): String = "$THEME_NAME-${player.uniqueId}"

    fun getSessionTitle(session: Session): String {
        val title = session.variables["title"]?.toString() ?: session.conversation.option.title
        return title.replace("{name}", session.source.name)
    }

    @SubscribeEvent
    private fun onConversationClosed(e: ConversationEvents.Closed) {
        if (e.session.conversation.option.theme != THEME_NAME) return

        val session = e.session
        val player = session.player
        val guiTitle = getScreenTitle(player)
        // Close the GUI if the session is closed
        if (GuiManager.isOpenedGui(player, guiTitle)) {
            (GuiManager.getOpenedGui(player, guiTitle) as? SrDialogScreen)?.closeConversation()
        }
    }

    override fun createConfig(): ThemeSrDialogSettings {
        val file = SrChemDialog.instance.dataFolder.resolve("style.yml")

        if (!file.exists()) {
            SrChemDialog.instance.saveResource("style.yml", false)
        }

        val config = Configuration.loadFromFile(file)
            .getConfigurationSection("theme-sr-dialog")!!

        return ThemeSrDialogSettings(config)
    }

    override fun onDisplay(session: Session, message: List<String>, canReply: Boolean): CompletableFuture<Void> {
        return session.createDisplay { replies ->
            val player = session.player
            // Get the GuiScreen instance
            val title = getScreenTitle(player)
            if (GuiManager.isOpenedGui(player, title)) {
                GuiManager.getOpenedGui(player, title) as SrDialogScreen
            } else {
                SrChemDialog.logger.info("[SrChemDialog] Creating sr-dialog GUI for ${player.name}")
                // Set esc cancel
                SrChemDialog.germConfig.set("options.esc-cancel", settings.settings["esc-cancel"] as Boolean)
                SrDialogScreen(title, SrChemDialog.germConfig).apply {
                    // Set the close handler: Interrupt the conversation session
                    // if the player closes the GUI
                    setClosedHandler { player2, _ ->
                        val currentSession = player2.conversationSession ?: return@setClosedHandler
                        // Close the session if the player closes the GUI
                        if (currentSession.conversation.id == session.conversation.id) {
                            currentSession.close(refuse = true)
                        }
                    }
                }
            }.apply {
                if (!isOpened) {
                    openGui(player)
                    loadSettings(settings)
                }
                if (canReply)
                // If the player can reply, load the data into the GUI
                    loadData(session, message, replies)
                else
                    loadData(session, message, null)
            }
        }
    }


}