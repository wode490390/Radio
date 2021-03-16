package cn.wode490390.nukkit.radio;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.network.protocol.PlaySoundPacket;
import cn.nukkit.network.protocol.StopSoundPacket;
import cn.nukkit.utils.TextFormat;
import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class Radio implements IRadio {

    private byte mode = MODE_ORDER;
    private final List<IMusic> playlist = new ObjectArrayList<>();

    private final Set<Player> listeners = new HashSet<>();
    private IMusic playing;
    private int index = 0;
    private final List<IMusic> randomPlaylist = new ObjectArrayList<>();

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(task -> {
        Thread thread = new Thread(task, "Radio");
        thread.setDaemon(true);
        return thread;
    });

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
    public IMusic getPlaying() {
        return this.playing;
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
                if (this.randomPlaylist.isEmpty()) {
                    this.randomPlaylist.addAll(this.playlist);
                    Collections.shuffle(this.randomPlaylist, ThreadLocalRandom.current());
                }
                this.playing = this.randomPlaylist.remove(this.randomPlaylist.size() - 1);
                break;
            case MODE_ORDER:
            default:
                if (++this.index >= this.playlist.size()) {
                    this.index = 0;
                }
                this.playing = this.playlist.get(this.index);
                break;
        }

        stop(null, players);
        play(this.playing, players);

        this.scheduler.schedule((Runnable) this::next, this.playing.getDuration(), TimeUnit.MILLISECONDS);
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
    public boolean isListened(Player player) {
        return this.listeners.contains(player);
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
