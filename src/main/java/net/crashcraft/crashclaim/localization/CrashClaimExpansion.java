package net.crashcraft.crashclaim.localization;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.crashcraft.crashclaim.CrashClaim;
import net.crashcraft.crashclaim.claimobjects.Claim;
import net.crashcraft.crashclaim.config.GroupSettings;
import net.crashcraft.crashclaim.data.ContributionManager;
import net.crashcraft.crashclaim.visualize.api.BaseVisual;
import net.crashcraft.crashclaim.visualize.api.VisualGroup;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CrashClaimExpansion extends PlaceholderExpansion {
    private final CrashClaim crashClaim;

    public CrashClaimExpansion(CrashClaim crashClaim){
        this.crashClaim = crashClaim;
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        if (player != null){
            switch (params.toLowerCase()) {
                case "total_owned_claims" -> {
                    return Integer.toString(crashClaim.getDataManager().getNumberOwnedClaims(player.getUniqueId()));
                }
                case "total_owned_claims_blocks" -> {
                    return Integer.toString(crashClaim.getDataManager().getOwnedParentClaims(player.getUniqueId()).stream()
                            .filter(c -> c.getOwner().equals(player.getUniqueId()))
                            .map(c -> ContributionManager.getArea(c.getMinX(), c.getMinZ(), c.getMaxX(), c.getMaxZ()))
                            .mapToInt(i -> i).sum());
                }
                case "total_owned_parent_claims" -> {
                    return Integer.toString(crashClaim.getDataManager().getNumberOwnedParentClaims(player.getUniqueId()));
                }
                case "current_claim_owner" -> {
                    if (!player.isOnline()){
                        return null;
                    }

                    Player onlinePlayer = player.getPlayer();

                    if (onlinePlayer == null){
                        return null;
                    }

                    Claim claim = crashClaim.getDataManager().getClaim(onlinePlayer.getLocation());

                    if (claim == null){
                        return "";
                    }

                    OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(claim.getOwner());
                    return offlinePlayer.getName();
                }
                case "current_claim_blocks" -> {
                    if (!player.isOnline()){
                        return null;
                    }

                    Player onlinePlayer = player.getPlayer();

                    if (onlinePlayer == null){
                        return null;
                    }

                    Claim claim = crashClaim.getDataManager().getClaim(onlinePlayer.getLocation());

                    if (claim == null){
                        return "-1";
                    }

                    return String.valueOf(ContributionManager.getArea(claim.getMinX(), claim.getMinZ(), claim.getMaxX(), claim.getMaxZ()));
                }
                case "visual_status" -> {
                    if (!player.isOnline()){
                        return null;
                    }

                    Player onlinePlayer = player.getPlayer();

                    if (onlinePlayer == null){
                        return null;
                    }

                    VisualGroup group = CrashClaim.getPlugin().getVisualizationManager().fetchVisualGroup(onlinePlayer, false);

                    if (group == null){
                        return Localization.PLACEHOLDERAPI__VISUAL_STATUS_HIDDEN.getRawMessage();
                    }

                    for (BaseVisual visual : group.getActiveVisuals()){
                        if (visual.getClaim() != null){
                            return Localization.PLACEHOLDERAPI__VISUAL_STATUS_SHOWN.getRawMessage();
                        }
                    }

                    return Localization.PLACEHOLDERAPI__VISUAL_STATUS_HIDDEN.getRawMessage();
                }
                case "max_allowed_claims" -> {
                    if (!player.isOnline()){
                        return null;
                    }

                    Player onlinePlayer = player.getPlayer();

                    if (onlinePlayer == null){
                        return null;
                    }

                    GroupSettings groupSettings = CrashClaim.getPlugin().getPluginSupport().getPlayerGroupSettings(onlinePlayer);
                    return String.valueOf(groupSettings.getMaxClaims());
                }
                case "max_allowed_claims_blocks" -> {
                    if (!player.isOnline()){
                        return null;
                    }

                    Player onlinePlayer = player.getPlayer();

                    if (onlinePlayer == null){
                        return null;
                    }

                    GroupSettings groupSettings = CrashClaim.getPlugin().getPluginSupport().getPlayerGroupSettings(onlinePlayer);
                    return String.valueOf(groupSettings.getMaxClaimsArea());
                }
            }
        }
        return null;
    }

    @Override
    public @NotNull String getIdentifier() {
        return crashClaim.getName();
    }

    @Override
    public @NotNull String getAuthor() {
        return crashClaim.getDescription().getAuthors().get(0);
    }

    @Override
    public @NotNull String getVersion() {
        return crashClaim.getDescription().getVersion();
    }

    @Override
    public boolean persist(){
        return true;
    }
}
