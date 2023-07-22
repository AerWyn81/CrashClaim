package net.crashcraft.crashclaim.claimobjects.permission.parent;

import net.crashcraft.crashclaim.claimobjects.BaseClaim;
import net.crashcraft.crashclaim.claimobjects.PermState;
import net.crashcraft.crashclaim.claimobjects.PermissionGroup;
import net.crashcraft.crashclaim.claimobjects.permission.GlobalPermissionSet;
import net.crashcraft.crashclaim.claimobjects.permission.PlayerPermissionSet;
import net.crashcraft.crashclaim.config.GlobalConfig;
import net.crashcraft.crashclaim.permissions.PermissionRoute;

import java.util.HashMap;
import java.util.UUID;

public class ParentPermissionGroup extends PermissionGroup {
    public ParentPermissionGroup() {

    }

    public ParentPermissionGroup(BaseClaim owner, GlobalPermissionSet globalPermissionSet, HashMap<UUID, PlayerPermissionSet> playerPermissions) {
        super(owner, globalPermissionSet, playerPermissions);
    }

    @Override
    public PlayerPermissionSet createPlayerPermissionSet() {
        return new PlayerPermissionSet(PermState.NEUTRAL, PermState.NEUTRAL, PermState.NEUTRAL, PermState.NEUTRAL, PermState.NEUTRAL, PermState.NEUTRAL,
                new HashMap<>(), PermState.NEUTRAL, PermState.DISABLE, PermState.DISABLE);
    }

    @Override
    public GlobalPermissionSet createGlobalPermissionSet() {
        return new GlobalPermissionSet(GlobalConfig.global_perm_build, GlobalConfig.global_perm_interactions, GlobalConfig.global_perm_entities, GlobalConfig.global_perm_explosions, GlobalConfig.global_perm_mob_griefing, GlobalConfig.global_perm_teleportation, GlobalConfig.global_perm_view_subclaims,
                new HashMap<>(), GlobalConfig.global_perm_containers, GlobalConfig.global_perm_pistons, GlobalConfig.global_perm_fluids);
    }

    @Override
    public int checkGlobalValue(int value, PermissionRoute route) {
        if (value == PermState.NEUTRAL) {
            return PermState.DISABLE;
        } else {
            return value;
        }
    }

    @Override
    public int checkPlayerValue(int value, PermissionRoute route) {
        switch (route){
            case MODIFY_CLAIM:
            case MODIFY_PERMISSIONS:
                if (value == PermState.NEUTRAL) {
                    return PermState.DISABLE;
                } else {
                    return value;
                }
            default:
                return value;
        }
    }
}
