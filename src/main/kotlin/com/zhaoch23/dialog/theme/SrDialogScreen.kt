package com.zhaoch23.dialog.theme

import com.germ.germplugin.api.dynamic.gui.GermGuiScreen
import com.zhaoch23.dialog.SrChemDialog
import ink.ptms.chemdah.core.conversation.PlayerReply
import ink.ptms.chemdah.core.conversation.Session
import me.clip.placeholderapi.PlaceholderAPI
import org.bukkit.configuration.ConfigurationSection
import taboolib.common5.cbool
import java.util.function.BiConsumer

class SrDialogScreen(title: String, configuration: ConfigurationSection) : GermGuiScreen(title, configuration) {

    private val callbackMap = mutableMapOf<String, BiConsumer<Map<String, Any>, Map<String, Any>>>()

    /**
     * Load data into the GUI and send it to the client side
     *
     * @param session The session of the conversation
     * @param messages The messages to be displayed
     * @param replies The replies to be displayed
     */
    fun loadData(session: Session, messages: List<String>, replies: List<PlayerReply>? = null) {
        // If the player can reply, load the replies into the GUI
        val replyStrings = replies?.let { it ->
            // Initialize the callback
            callbackMap["reply"] = BiConsumer { contentMap, _ ->
                val selected = contentMap["selected"]?.toString()?.toInt()!!
                // Kotlin isn't fun :(
                replies.getOrNull(selected)?.run {
                    check(session).thenApply { b ->
                        if (b.cbool) select(session)
                    }
                }

            }
            it.map { it.build(session) }
        } ?: run {
            callbackMap.remove("reply")
            listOf()
        }
        if (messages.isEmpty() && replyStrings.isEmpty()) {
            // If there is no message and reply, close the GUI
            closeConversation()
            return
        }

        val parsePlaceholders = ThemeSrDialog.settings.settings["parse-placeholders"] as Boolean

        // Load data to the client side
        val data = mapOf(
            "title" to ThemeSrDialog.getSessionTitle(session),
            "messages" to if (parsePlaceholders) messages.map { PlaceholderAPI.setPlaceholders(session.player, it) } else messages,
            "replies" to if (parsePlaceholders) replyStrings.map { PlaceholderAPI.setPlaceholders(session.player, it) } else replyStrings
        )

        options.setData(data)
    }

    fun loadSettings(settings: ThemeSrDialogSettings) {
        options.setData(mapOf("settings" to settings.settings))
    }

    fun closeConversation() {
        // Send a message to the client side to info it to close the GUI
        options.setData(mapOf("close" to true))
    }

    override fun onReceivePost(
        path: String?,
        contentMap: MutableMap<String, Any>?,
        responseMap: MutableMap<String, Any>?
    ) {
        callbackMap[path]?.accept(contentMap!!, responseMap!!)
        super.onReceivePost(path, contentMap, responseMap)
    }

    override fun onOpened() {
        super.onOpened()
        if(ThemeSrDialog.settings.settings["hide-hud"] as Boolean) {
            //hide HUD before start dialog
            SrChemDialog.instance.server.run {
//                SrChemDialog.logger.info("Hiding HUD for ${player.name}")
                dispatchCommand(
                    consoleSender,
                    "gp hud hide ${player.name} true"
                )
            }
        }
    }

    override fun onClosed() {
        super.onClosed()
        if(ThemeSrDialog.settings.settings["hide-hud"] as Boolean){
            //recover HUD to the time before the dialog
            SrChemDialog.instance.server.run {
//                SrChemDialog.logger.info("ReShowing HUD for ${player.name}")
                dispatchCommand(
                    consoleSender,
                    "gp hud hide ${player.name} false"
                )
            }
        }
    }
}
