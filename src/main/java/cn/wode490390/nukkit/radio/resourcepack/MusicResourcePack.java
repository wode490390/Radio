package cn.wode490390.nukkit.radio.resourcepack;

import cn.nukkit.resourcepacks.ResourcePack;
import com.google.common.base.Preconditions;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class MusicResourcePack implements ResourcePack {

    private static final MessageDigest HASHER;

    static {
        try {
            HASHER = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private final UUID uuid;
    private final byte[] data;
    private final byte[] sha256;

    public MusicResourcePack(String md5, byte[] data) throws Exception {
        Preconditions.checkNotNull(md5, "md5");
        md5 = md5.trim();
        Preconditions.checkArgument(md5.length() == 32, "Invalid MD5");
        md5 = md5.toLowerCase();
        Preconditions.checkNotNull(data, "data");
        Preconditions.checkArgument(data.length > 0, "Invalid data");

        StringBuilder builder = new StringBuilder(36);
        builder.append(md5.substring(0, 8));
        builder.append("-");
        builder.append(md5.substring(8, 12));
        builder.append("-");
        builder.append(md5.substring(12, 16));
        builder.append("-");
        builder.append(md5.substring(16, 20));
        builder.append("-");
        builder.append(md5.substring(20));
        String uuid = builder.toString();
        this.uuid = UUID.fromString(uuid);

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream(); ZipOutputStream zos = new ZipOutputStream(baos)) {
            zos.setLevel(Deflater.BEST_COMPRESSION);
            byte[] buffer;

            zos.putNextEntry(new ZipEntry("contents.json"));
            builder = new StringBuilder(140);
            builder.append("{\"content\":[{\"path\":\"manifest.json\"},{\"path\":\"sounds/sound_definitions.json\"},{\"path\":\"sounds/radio/");
            builder.append(md5);
            builder.append(".ogg\"}]}");
            buffer = builder.toString().getBytes();
            zos.write(buffer, 0, buffer.length);

            zos.putNextEntry(new ZipEntry("manifest.json"));
            builder = new StringBuilder(228);
            builder.append("{\"format_version\":1,\"header\":{\"uuid\":\"");
            builder.append(uuid);
            builder.append("\",\"name\":\"\",\"version\":[0,0,1],\"description\":\"\"},\"modules\":[{\"description\":\"\",\"version\":[0,0,1],\"uuid\":\"");
            builder.append(UUID.nameUUIDFromBytes(uuid.getBytes(StandardCharsets.US_ASCII)));
            builder.append("\",\"type\":\"resources\"}]}");
            buffer = builder.toString().getBytes();
            zos.write(buffer, 0, buffer.length);

            zos.putNextEntry(new ZipEntry("sounds/sound_definitions.json"));
            builder = new StringBuilder(122);
            builder.append("{\"radio.");
            builder.append(md5);
            builder.append("\":{\"category\":\"music\",\"sounds\":[\"sounds/radio/");
            builder.append(md5);
            builder.append("\"]}}");
            buffer = builder.toString().getBytes();
            zos.write(buffer, 0, buffer.length);

            zos.putNextEntry(new ZipEntry("sounds/radio/" + md5 + ".ogg"));
            zos.write(data, 0, data.length);

            zos.finish();
            this.data = baos.toByteArray();
        }

        this.sha256 = HASHER.digest(this.data);
    }

    public byte[] getData() {
        return this.data;
    }

    @Override
    public String getPackName() {
        return "";
    }

    @Override
    public UUID getPackId() {
        return this.uuid;
    }

    @Override
    public String getPackVersion() {
        return "0.0.1";
    }

    @Override
    public int getPackSize() {
        return this.data.length;
    }

    @Override
    public byte[] getSha256() {
        return this.sha256;
    }

    @Override
    public byte[] getPackChunk(int off, int len) {
        return Arrays.copyOfRange(this.data, off, off + ((this.data.length - off > len) ? len : (this.data.length - off)));
    }

    @Override
    public String toString() {
        return "MusicResourcePack(" + this.uuid + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (super.equals(obj)) {
            return true;
        }
        if (obj instanceof MusicResourcePack) {
            return this.uuid.equals(((MusicResourcePack) obj).uuid);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return 259 + Objects.hashCode(this.uuid);
    }
}
