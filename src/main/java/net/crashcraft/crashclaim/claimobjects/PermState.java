package net.crashcraft.crashclaim.claimobjects;

public class PermState {
    public static final int DISABLE = 0;
    public static final int ENABLED = 1;
    public static final int NEUTRAL = 2;

    public static int of(String value) {
        return switch (value) {
            case "ENABLED" -> 1;
            case "NEUTRAL" -> 2;
            default -> 0;
        };
    }
}
