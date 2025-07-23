package gay.rooot.fairkeepinventory.mixin;

import gay.rooot.fairkeepinventory.FairKeepInv;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity {
    @Shadow @Final
    PlayerInventory inventory;

    @Shadow public int experienceLevel;

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Redirect(method = "dropInventory", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/GameRules;getBoolean(Lnet/minecraft/world/GameRules$Key;)Z"))
    public boolean doKeepInv(GameRules instance, GameRules.Key<GameRules.BooleanRule> rule) {
        System.out.println("PlayerEntityMixin.doKeepInv");
        if (rule == GameRules.KEEP_INVENTORY && FairKeepInv.canAffordToKeepInventory(this.experienceLevel, this.inventory)) {
            return true;
        }
        return instance.getBoolean(rule);
    }

    @Redirect(method = "getXpToDrop", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/GameRules;getBoolean(Lnet/minecraft/world/GameRules$Key;)Z"))
    public boolean doKeepXp(GameRules instance, GameRules.Key<GameRules.BooleanRule> rule) {
        System.out.println("PlayerEntityMixin.doKeepXp");
        if (rule == GameRules.KEEP_INVENTORY && FairKeepInv.canAffordToKeepInventory(this.experienceLevel, this.inventory)) {
            return true;
        }
        return instance.getBoolean(rule);
    }

    @Inject(at = @At("RETURN"), method = "getXpToDrop", cancellable = true)
    public void getXpToDrop(CallbackInfoReturnable<Integer> cir) {
        System.out.println("PlayerEntityMixin.getXpToDrop");
        if (FairKeepInv.canAffordToKeepInventory(this.experienceLevel, this.inventory)) {
            cir.setReturnValue(0);
            cir.cancel();
        }
    }
}
