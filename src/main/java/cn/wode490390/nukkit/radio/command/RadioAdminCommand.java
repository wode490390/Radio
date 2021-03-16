package cn.wode490390.nukkit.radio.command;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.PluginIdentifiableCommand;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.resourcepacks.ResourcePack;
import cn.nukkit.scheduler.AsyncTask;
import cn.wode490390.nukkit.radio.RadioPlugin;
import cn.wode490390.nukkit.radio.resourcepack.MusicResourcePack;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class RadioAdminCommand extends Command implements PluginIdentifiableCommand {

    private final RadioPlugin plugin;

    public RadioAdminCommand(RadioPlugin plugin) {
        super("radioadmin", "Manages the global radio", "/radioadmin <dump>");
        this.setPermission("radio.admin.command");
        this.getCommandParameters().clear();
        this.addCommandParameters("default", new CommandParameter[]{
                CommandParameter.newEnum("operate", new String[]{"dump"}),
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

        if (args.length < 1) {
            sender.sendMessage(new TranslationContainer("commands.generic.usage", this.usageMessage));
            return false;
        }

        switch (args[0].toLowerCase()) {
            case "dump":
                sender.sendMessage("Dumping...");
                this.plugin.getServer().getScheduler().scheduleAsyncTask(this.plugin, new AsyncTask(){
                    @Override
                    public void onRun() {
                        Path path = plugin.getDataFolder().toPath().resolve("dump");
                        try {
                            if (!Files.isDirectory(path, LinkOption.NOFOLLOW_LINKS)) {
                                Files.deleteIfExists(path);
                                Files.createDirectory(path);
                            }
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        int num = 0;
                        for (ResourcePack pack : plugin.getServer().getResourcePackManager().getResourceStack()) {
                            if (pack instanceof MusicResourcePack) {
                                MusicResourcePack music = (MusicResourcePack) pack;
                                try (OutputStream outputStream = Files.newOutputStream(path.resolve(music.getPackId().toString() + ".mcpack"), StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING)) {
                                    outputStream.write(music.getData());
                                } catch (IOException e) {
                                    plugin.getLogger().alert("Unable to dump resource pack", e);
                                    continue;
                                }
                                ++num;
                            }
                        }
                        sender.sendMessage("Dumped " + num + " music resource packs");
                    }
                });
                break;
            default:
                sender.sendMessage(new TranslationContainer("commands.generic.usage", this.usageMessage));
                return false;
        }

        return true;
    }
}
