package gay.rooot.fairkeepinventory;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Items;

public class FairKeepInv {
    public static boolean canAffordToKeepInventory(int xp, PlayerInventory inv) {
        int netheriteCount = inv.count(Items.NETHERITE_INGOT);
        int diamondCount = inv.count(Items.DIAMOND);
        boolean hasTotem = inv.contains(Items.TOTEM_OF_UNDYING.getDefaultStack());
        System.out.println(
                "---- DEBUG ----\n"+
                        "experienceLevel: " + xp+"\n"+
                        "hasTotem: " + hasTotem + "\n"+
                        "netheriteCount: " + netheriteCount + "\n"+
                        "diamondCount: " + diamondCount+"\n");

        if (xp >= 10) return true;
        if (netheriteCount >= 1) return true;
        if (diamondCount >= 8) return true;

        System.out.println("skill issue unfortunately");
        return false;
    }
}
