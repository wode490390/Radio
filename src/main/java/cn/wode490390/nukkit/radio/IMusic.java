package cn.wode490390.nukkit.radio;

public interface IMusic {

    String getMD5();

    long getDuration();

    String getName();

    default String getIdentifier() {
        return "radio." + this.getMD5();
    }
}
