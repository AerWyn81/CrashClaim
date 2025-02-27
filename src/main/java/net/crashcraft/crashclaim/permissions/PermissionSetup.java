package net.crashcraft.crashclaim.permissions;

import net.crashcraft.crashclaim.CrashClaim;
import net.crashcraft.crashclaim.claimobjects.PermState;
import net.crashcraft.crashclaim.claimobjects.permission.PlayerPermissionSet;
import org.bukkit.Material;
import org.bukkit.block.Container;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Flying;
import org.bukkit.entity.Monster;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

public class PermissionSetup {
    private final ArrayList<Material> trackedContainers;
    private final ArrayList<Material> untrackedBlocks;
    private final ArrayList<Material> extraInteractables;
    private final ArrayList<Material> heldItemInteraction;

    private final ArrayList<EntityType> trackedMonsters;

    private final PlayerPermissionSet ownerPermissionSet;

    public PermissionSetup(CrashClaim claim){
        Logger logger = claim.getLogger();

        trackedContainers = new ArrayList<>();
        untrackedBlocks = new ArrayList<>();
        extraInteractables = new ArrayList<>();
        heldItemInteraction = new ArrayList<>();
        trackedMonsters = new ArrayList<>();

        for (Material material : Material.values()){
            ItemStack stack = new ItemStack(material);

            if (stack.getItemMeta() instanceof BlockStateMeta) {
                BlockStateMeta meta = (BlockStateMeta) stack.getItemMeta();

                if (meta == null)
                    continue;

                if (meta.getBlockState() instanceof Container) {
                    trackedContainers.add(material);
                }
            }
        }

        for (EntityType entity : EntityType.values()){
            if (entity.getEntityClass() == null) {
                continue;
            }

            if (Monster.class.isAssignableFrom(entity.getEntityClass()) || entity.getEntityClass().isAssignableFrom(Flying.class)) {
                trackedMonsters.add(entity);
            }
        }

        FileConfiguration lookup = YamlConfiguration.loadConfiguration(new File(Paths.get(claim.getDataFolder().getAbsolutePath(), "lookup.yml").toUri()));

        for (String name : lookup.getStringList("additional-tracked-interactables")){
            if (name.equals(""))
                continue;

            Material material = Material.getMaterial(name);

            if (material != null) {
                extraInteractables.add(material);
            } else {
                logger.warning("Material was not found whole parsing lookup.yml -> additional-tracked-interactables: " + name +
                        "\n Make sure to be using the Bukkit Material names.");
            }
        }

        for (String name : lookup.getStringList("untracked-blocks")){
            if (name.equals("")) {
                continue;
            }

            Material material = Material.getMaterial(name);

            if (material != null) {
                untrackedBlocks.add(material);
                trackedContainers.remove(material);
                extraInteractables.remove(material);
            } else {
                logger.warning("Material was not found whole parsing lookup.yml -> untracked-blocks: " + name +
                        "\nMake sure to be using the Bukkit Material names.");
            }
        }

        for (String name : lookup.getStringList("tracked-heldItemInteraction")){
            if (name.equals("")) {
                continue;
            }

            Material material = Material.getMaterial(name);

            if (material != null) {
                heldItemInteraction.add(material);
            } else {
                logger.warning("Material was not found whole parsing lookup.yml -> tracked-heldItemInteraction: " + name +
                        "\n Make sure to be using the Bukkit Material names.");
            }
        }

        HashMap<Material, Integer> temp = new HashMap<>();

        for (Material material : trackedContainers){
            temp.put(material, PermState.ENABLED);
        }

        ownerPermissionSet = new PlayerPermissionSet(PermState.ENABLED,
                PermState.ENABLED,
                PermState.ENABLED,
                PermState.ENABLED,
                PermState.ENABLED,
                PermState.ENABLED,
                temp,
                PermState.ENABLED,
                PermState.ENABLED,
                PermState.ENABLED);
    }

    public ArrayList<Material> getTrackedContainers() {
        return trackedContainers;
    }

    public ArrayList<Material> getUntrackedBlocks() {
        return untrackedBlocks;
    }

    public ArrayList<Material> getExtraInteractables() {
        return extraInteractables;
    }

    public PlayerPermissionSet getOwnerPermissionSet() {
        return ownerPermissionSet;
    }

    public ArrayList<Material> getHeldItemInteraction() {
        return heldItemInteraction;
    }

    public ArrayList<EntityType> getTrackedMonsters() {
        return trackedMonsters;
    }
}