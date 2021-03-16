package cn.wode490390.nukkit.radio;

import com.google.common.base.Preconditions;

public class Music implements IMusic {

    private final String md5;
    private final long duration;
    private final String name;

    public Music(String md5, long duration, String name) {
        Preconditions.checkNotNull(md5, "md5");
        md5 = md5.trim();
        Preconditions.checkArgument(md5.length() == 32, "Invalid MD5");
        Preconditions.checkArgument(duration > 0, "Invalid duration");
        if (name == null || name.trim().isEmpty()) {
            name = md5;
        }
        this.md5 = md5.toLowerCase();
        this.duration = duration;
        this.name = name;
    }

    @Override
    public String getMD5() {
        return this.md5;
    }

    @Override
    public long getDuration() {
        return this.duration;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public boolean equals(Object obj) {
        if (super.equals(obj)) {
            return true;
        }
        if (obj instanceof IMusic) {
            return this.md5.equals(((IMusic) obj).getMD5());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.md5.hashCode();
    }

    @Override
    public String toString() {
        return this.name + "(" + this.md5 + "/" + this.duration + "ms)";
    }
}
