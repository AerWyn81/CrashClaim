package net.crashcraft.crashclaim.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import io.papermc.lib.PaperLib;
import net.crashcraft.crashclaim.claimobjects.Claim;
import net.crashcraft.crashclaim.config.GlobalConfig;
import net.crashcraft.crashclaim.data.ClaimDataManager;
import net.crashcraft.crashclaim.data.TimedHashSet;
import net.crashcraft.crashclaim.localization.Localization;
import net.crashcraft.crashclaim.permissions.PermissionHelper;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.util.UUID;

@CommandAlias("claimunstuck|unstuck")
@CommandPermission("crashclaim.user.claimunstuck")
public class UnstuckCommand extends BaseCommand {
    private final ClaimDataManager manager;

    private final TimedHashSet<UUID> playersCooldown;

    public UnstuckCommand(ClaimDataManager manager){
        this.manager = manager;
        this.playersCooldown = new TimedHashSet<>();
    }

    @Default
    @CommandCompletion("@players")
    public void onDefault(Player player){
        Location location = player.getLocation();

        Claim claim = manager.getClaim(location.getBlockX(), location.getBlockZ(), location.getWorld().getUID());
        if (claim == null) {
            player.spigot().sendMessage(Localization.UNSTUCK__NO_CLAIM.getMessage(player));
            return;
        }

        if (claim.getOwner().equals(player.getUniqueId())) {
            player.spigot().sendMessage(Localization.UNSTUCK__OWN_CLAIM.getMessage(player));
            return;
        }

        var isPlayerBypass = PermissionHelper.getPermissionHelper().getBypassManager().isBypass(player.getUniqueId());

        var playerInCooldown = playersCooldown.get(player.getUniqueId());
        if (!isPlayerBypass && playerInCooldown.isPresent()) {
            player.spigot().sendMessage(Localization.UNSTUCK__IN_COOLDOWN.getMessage(player, "time", formatTime(playerInCooldown.get().getLeftTime())));
            return;
        }

        player.setNoDamageTicks(100);

        int distMax = Math.abs(location.getBlockX() - claim.getMaxX());
        int distMin = Math.abs(location.getBlockX() - claim.getMinX());

        World world = location.getWorld();
        if (distMax > distMin) {
            PaperLib.teleportAsync(player, new Location(world, claim.getMinX() - 1,
                    world.getHighestBlockYAt(claim.getMinX(),
                            location.getBlockZ()) + 1, location.getBlockZ()));
        } else {
            PaperLib.teleportAsync(player, new Location(world, claim.getMaxX() + 1,
                    world.getHighestBlockYAt(claim.getMaxX(),
                            location.getBlockZ()) + 1, location.getBlockZ()));
        }

        if (!isPlayerBypass) {
            playersCooldown.add(player.getUniqueId(), GlobalConfig.cooldownUnstuckCommand * 1000L);
        }

        player.spigot().sendMessage(Localization.UNSTUCK__SUCCESS.getMessage(player));
    }

    private String formatTime(long milliseconds) {
        var timeToShow = "";

        var duration = Duration.ofMillis(milliseconds);

        var min = duration.toMinutesPart();
        if (min > 0)
            timeToShow += duration.toMinutesPart() + "min";

        timeToShow += duration.toSecondsPart() + "s";
        return timeToShow;
    }
}
