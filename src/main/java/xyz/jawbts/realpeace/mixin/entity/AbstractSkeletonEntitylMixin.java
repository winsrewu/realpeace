package xyz.jawbts.realpeace.mixin.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.mob.AbstractSkeletonEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractSkeletonEntity.class)
public abstract class AbstractSkeletonEntitylMixin extends HostileEntity {
    protected AbstractSkeletonEntitylMixin(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    @Shadow
    private final BowAttackGoal<AbstractSkeletonEntity> bowAttackGoal = new BowAttackGoal(this, 1.0, 20, 15.0F);
    @Shadow
    private final MeleeAttackGoal meleeAttackGoal = new MeleeAttackGoal(this, 1.2, false);

    private final ActiveTargetGoal<AbstractSkeletonEntity> playerTargetGoal = new ActiveTargetGoal(this, PlayerEntity.class, true);

    /**
     * @author Mojang AB
     * @reason 1
     */
    @Overwrite
    public void initGoals() {
        this.goalSelector.add(2, new AvoidSunlightGoal(this));
        this.goalSelector.add(3, new EscapeSunlightGoal(this, 1.0));
        this.goalSelector.add(3, new FleeEntityGoal(this, WolfEntity.class, 6.0F, 1.0, 1.2));
        this.goalSelector.add(5, new WanderAroundFarGoal(this, 1.0));
        this.goalSelector.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.add(6, new LookAroundGoal(this));
        this.targetSelector.add(1, new RevengeGoal(this, new Class[0]));
    }

    /**
     * @author MojangAB
     * @reason 1
     */
    @Overwrite
    public void attack(LivingEntity target, float pullProgress) {
        ItemStack itemStack = new ItemStack(Items.ARROW, (int)(Math.random() * 100) % 5 + 1);
        this.dropStack(itemStack);
        this.targetSelector.remove(playerTargetGoal);
    }

    @Inject(method = "tickMovement()V", at = @At("HEAD"))
    public void tickMovement(CallbackInfo ci) {
        if(age % 400 == 0 && Math.random() < 0.1f) {
            this.targetSelector.add(2, playerTargetGoal);
        }
    }

    /**
     * @author Mojang AB
     * @reason 1
     */
    @Overwrite
    public void updateAttackType() {
        if (this.world != null && !this.world.isClient) {
            this.goalSelector.remove(this.meleeAttackGoal);
            this.goalSelector.add(4, this.bowAttackGoal);
        }
    }
}
