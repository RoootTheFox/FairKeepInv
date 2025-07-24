package gay.rooot.fairkeepinventory.mixin;

import com.mojang.authlib.GameProfile;
import gay.rooot.fairkeepinventory.FairKeepInv;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity {
    public ServerPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile) {
        super(world, pos, yaw, gameProfile);
    }

    @Unique
    private ServerPlayerEntity leOldPlayer;

    @Inject(at = @At("HEAD"), method = "copyFrom")
    private void copyFrom(ServerPlayerEntity oldPlayer, boolean alive, CallbackInfo ci) {
        leOldPlayer = oldPlayer;
    }

    @Unique
    private static void removeItemFromInv(Item item, int count, PlayerInventory inv) {
        int removed = 0;

        for(int j = 0; j < inv.size(); ++j) {
            ItemStack itemStack = inv.getStack(j);
            if (itemStack.getItem().equals(item) && removed < count) {
                int willRemove = Math.min(itemStack.getCount(), count);
                itemStack.decrement(willRemove);
                removed += willRemove;
            }
        }
    }

    @Redirect(method = "copyFrom", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/GameRules;getBoolean(Lnet/minecraft/world/GameRules$Key;)Z"))
    public boolean doKeepInv(GameRules instance, GameRules.Key<GameRules.BooleanRule> rule) {
        if (rule == GameRules.KEEP_INVENTORY && FairKeepInv.canAffordToKeepInventory(leOldPlayer.experienceLevel, leOldPlayer.getInventory())) {
            // this runs when the player respawns - do the social credit payment here
            PlayerInventory inv = leOldPlayer.getInventory();
            int netheriteCount = inv.count(Items.NETHERITE_INGOT);
            int diamondCount = inv.count(Items.DIAMOND);

            if (leOldPlayer.experienceLevel >= 10) {
                leOldPlayer.experienceLevel = Math.round(leOldPlayer.experienceLevel * 0.2f);
            } else if (netheriteCount >= 1) {
                removeItemFromInv(Items.NETHERITE_INGOT, 1, inv);
            } else if (diamondCount >= 8) {
                removeItemFromInv(Items.DIAMOND, 8, inv);
            } else {
                return false;
            }

            return true;
        }

        return instance.getBoolean(rule);
    }
}
