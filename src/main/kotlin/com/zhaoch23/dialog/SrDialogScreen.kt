package com.zhaoch23.dialog

import com.germ.germplugin.api.dynamic.gui.GermGuiScreen
import ink.ptms.chemdah.core.conversation.PlayerReply
import ink.ptms.chemdah.core.conversation.Session
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Player
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
                    //Bukkit.getLogger().info("Selected reply: $this")
                    check(session).thenApply { b ->
                        //Bukkit.getLogger().info("Check result: $b")
                        if (b.cbool) select(session)
                    }
                }

            }
            it.map { it.build(session) }
        } ?: run {
            callbackMap.remove("reply")
            listOf()
        }
        // Load data to the client side
        val data = mapOf(
                "title" to ThemeSrDialog.getSessionTitle(session),
                "messages" to messages,
                "replies" to replyStrings
        )

        options.setData(data)
    }

    fun closeConversation() {
        // Send a message to the client side to info it to close the GUI
        options.setData(mapOf("close" to true))
    }

    override fun onReceivePost(path: String?,
                               contentMap: MutableMap<String, Any>?,
                               responseMap: MutableMap<String, Any>?) {
        callbackMap[path]?.accept(contentMap!!, responseMap!!)
        super.onReceivePost(path, contentMap, responseMap)
    }

    companion object {
        const val guiTitle = "sr-dialog"

        fun getTitle(player: Player): String = "sr-dialog-${player.uniqueId}"

    }

}
