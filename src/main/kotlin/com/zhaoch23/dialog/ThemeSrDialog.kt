package com.zhaoch23.dialog

import com.germ.germplugin.api.dynamic.gui.GuiManager
import ink.ptms.chemdah.api.ChemdahAPI.conversationSession
import ink.ptms.chemdah.api.event.collect.ConversationEvents
import ink.ptms.chemdah.core.conversation.Session
import ink.ptms.chemdah.core.conversation.theme.Theme
import ink.ptms.chemdah.taboolib.module.configuration.Configuration
import org.bukkit.event.EventHandler
import java.util.concurrent.CompletableFuture

object ThemeSrDialog : Theme<ThemeSrDialogSettings>() {

    override fun createConfig(): ThemeSrDialogSettings {
        val file = SrChemDialog.instance.dataFolder.resolve("style.yml")

        if (!file.exists()) {
            SrChemDialog.instance.saveResource("style.yml", false)
        }

        val config = Configuration.loadFromFile(file)

        return ThemeSrDialogSettings(config)
    }

    fun getSessionTitle(session: Session): String {
        return session.variables["title"]?.toString() ?: session.conversation.option.title
    }

    @EventHandler
    private fun onClosed(e: ConversationEvents.Closed) {
        val session = e.session
        val player = session.player
        val guiTitle = SrDialogScreen.getTitle(player)
        // Close the GUI if the session is closed
        if (GuiManager.isOpenedGui(player, guiTitle)) {
            GuiManager.getOpenedGui(player, guiTitle)?.close()
        }
    }

    override fun onDisplay(session: Session, message: List<String>, canReply: Boolean): CompletableFuture<Void> {
        return session.createDisplay { replies ->

            val player = session.player
            // Title of the GUI, NOT the title of the conversation
            val title = SrDialogScreen.getTitle(player)
            // Get the GuiScreen instance
            if (GuiManager.isOpenedGui(player, title)) {
                GuiManager.getOpenedGui(player, title) as? SrDialogScreen
            } else {
                // SrChemDialog.loadConfiguration(SrChemDialog.instance.dataFolder)
                // Create a new GUI if the player has not opened one
                SrDialogScreen(title, SrChemDialog.germConfig).also { gui ->
                    // Set the close handler: Interrupt the conversation session
                    // if the player closes the GUI
                    gui.setClosedHandler { player, _ ->
                        val currentSession = player.conversationSession ?: return@setClosedHandler
                        // Close the session if the player closes the GUI
                        if (getSessionTitle(currentSession).equals(getSessionTitle(session), true)) {
                            // Bukkit.getLogger().info("Closing session $currentSession")
                            currentSession.close(refuse = true)
                        }
                    }
                }
            }!!.apply {
                if (canReply) {
                    // If the player can reply, load the data into the GUI
                    loadData(session, message, replies)
                } else {
                    loadData(session, message, null)
                }
            }.also { gui ->
                if (!gui.isOpened) {
                    gui.openGui(player)
                }
            }
        }
    }


}