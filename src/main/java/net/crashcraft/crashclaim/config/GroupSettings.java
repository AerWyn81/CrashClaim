package net.crashcraft.crashclaim.config;

public class GroupSettings {
    private final int maxClaims;
    private final int maxClaimsArea;

    public GroupSettings(int maxClaims, int maxClaimsArea) {
        this.maxClaims = maxClaims;
        this.maxClaimsArea = maxClaimsArea;
    }

    public int getMaxClaims() {
        return maxClaims;
    }

    public int getMaxClaimsArea() { return maxClaimsArea; }
}
