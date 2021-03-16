package cn.wode490390.nukkit.radio.command;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.PluginIdentifiableCommand;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.plugin.Plugin;
import cn.wode490390.nukkit.radio.IRadio;
import cn.wode490390.nukkit.radio.RadioPlugin;

public class RadioCommand extends Command implements PluginIdentifiableCommand {

    private final RadioPlugin plugin;

    public RadioCommand(RadioPlugin plugin) {
        super("radio", "Controls the global radio", "/radio [play|stop]", new String[]{"fm", "music"});
        this.setPermission("radio.command");
        this.getCommandParameters().clear();
        this.addCommandParameters("default", new CommandParameter[]{
                CommandParameter.newEnum("operate", new String[]{"play", "stop"}),
        });
        this.plugin = plugin;
    }

    @Override
    public Plugin getPlugin() {
        return this.plugin;
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!this.plugin.isEnabled() || !this.testPermission(sender)) {
            return false;
        }

        IRadio global = this.plugin.getGlobal();

        if (global.getPlaylist().isEmpty()) {
            sender.sendMessage("Playlist is empty");
            return true;
        }

        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (args.length < 1) {
                this.plugin.showUI(player);
                return true;
            }

            switch (args[0].toLowerCase()) {
                case "play":
                    global.addListener(player);
                    break;
                case "stop":
                    global.removeListener(player);
                    break;
                default:
                    sender.sendMessage(new TranslationContainer("commands.generic.usage", this.usageMessage));
                    return false;
            }
        } else {
            sender.sendMessage(new TranslationContainer("%commands.generic.ingame"));
        }

        return true;
    }
}
