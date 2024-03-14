package com.zhaoch23.dialog

import org.bukkit.command.CommandSender
import taboolib.common.platform.command.CommandBody
import taboolib.common.platform.command.CommandHeader
import taboolib.common.platform.command.mainCommand
import taboolib.common.platform.command.subCommand
import taboolib.expansion.createHelper


@CommandHeader(name = "srdialog", aliases = ["srd"], permission = "srdialog.command")
object CommandSrDialog {

    @CommandBody
    val main = mainCommand {
        createHelper()
    }

    @CommandBody()
    val reload = subCommand {
        dynamic("from-resource") {
            execute<CommandSender> { sender, context, _ ->
                val fromResource = context["from-resource"].toBoolean()
                SrChemDialog.loadConfiguration(SrChemDialog.instance.dataFolder, fromResource)
                sender.sendMessage("Reloaded")
            }
        }
        execute<CommandSender> { sender, _, _ ->
            SrChemDialog.loadConfiguration(SrChemDialog.instance.dataFolder)
            sender.sendMessage("Reloaded")
        }
    }

}