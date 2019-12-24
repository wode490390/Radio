package cn.wode490390.nukkit.radio;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.network.protocol.PlaySoundPacket;
import cn.nukkit.network.protocol.StopSoundPacket;
import cn.nukkit.utils.TextFormat;
import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class Radio implements IRadio {

    private byte mode = MODE_ORDER;
    private final List<IMusic> playlist = new ObjectArrayList<>();

    private final Set<Player> listeners = Sets.newHashSet();
    private IMusic playing;
    private int index = 0;
    private static final Timer timer = new Timer("Radio Timer", true);
    private TimerTask updater;

    @Override
    public byte getMode() {
        return this.mode;
    }

    @Override
    public void setMode(byte mode) {
        this.mode = mode;
    }

    @Override
    public List<IMusic> getPlaylist() {
        return this.playlist;
    }

    @Override
    public void addMusic(IMusic music) {
        this.playlist.add(music);
    }

    @Override
    public void next() {
        this.next(false);
    }

    public synchronized void next(boolean force) {
        if (this.listeners.isEmpty() && !force) {
            this.playing = null;
            return;
        }
        Player[] players = this.listeners.toArray(new Player[0]);

        switch (this.mode) {
            case MODE_RANDOM:
                this.index = RadioPlugin.getRNG().nextInt(this.playlist.size());
                break;
            case MODE_ORDER:
            default:
                if (++this.index >= this.playlist.size()) {
                    this.index = 0;
                }
                break;
        }
        this.playing = this.playlist.get(this.index);

        stop(null, players);
        play(this.playing, players);

        this.updater = new TimerTask() {
            @Override
            public void run() {
                Radio.this.next();
            }
        };
        timer.schedule(this.updater, this.playing.getDuration());
    }

    @Override
    public void addListener(Player player) {
        if (this.listeners.add(player)) {
            if (this.playing == null) {
                this.next();
            } else {
                stop(null, player);
                play(this.playing, player);
            }
        }
    }

    @Override
    public void removeListener(Player player) {
        if (this.listeners.remove(player)) {
            stop(null, player);
        }
    }

    @Override
    public String toString() {
        return "Radio(" + this.playlist + ")";
    }

    public static void play(IMusic music, Player... players) {
        if (players == null || players.length == 0) {
            return;
        }
        Preconditions.checkNotNull(music, "music");

        String message = TextFormat.YELLOW + "Now playing: " + TextFormat.BOLD + music.getName();
        for (Player player : players) {
            PlaySoundPacket pk = new PlaySoundPacket();
            pk.name = music.getIdentifier();
            pk.pitch = 1;
            pk.volume = 1;
            pk.x = player.getFloorX();
            pk.y = player.getFloorY();
            pk.z = player.getFloorZ();
            player.dataPacket(pk);
            player.sendActionBar(message, 20, 60, 20);
        }
    }

    public static void stop(String identifier, Player... players) {
        if (players == null || players.length == 0) {
            return;
        }

        StopSoundPacket pk = new StopSoundPacket();
        if (identifier == null) {
            pk.stopAll = true;
            pk.name = "";
        } else {
            pk.name = identifier;
        }

        if (players.length == 1) {
            players[0].dataPacket(pk);
        } else {
            Server.broadcastPacket(players, pk);
        }
    }
}
