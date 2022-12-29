package xyz.jawbts.realpeace.mixin.goal;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.ai.goal.BowAttackGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(BowAttackGoal.class)
public abstract class BowAttackGoalMixin<T extends HostileEntity & RangedAttackMob> extends Goal {
    @Shadow
    private final T actor;
    @Shadow
    private final double speed;
    @Shadow
    private int attackInterval;
    @Shadow
    private final float squaredRange;
    @Shadow
    private int cooldown = -1;
    @Shadow
    private int targetSeeingTicker;
    @Shadow
    private boolean movingToLeft;
    @Shadow
    private boolean backward;
    @Shadow
    private int combatTicks = -1;

    private ItemStack itemStackB;

    private static final ItemStack itemStackA = new ItemStack(Items.ARROW);
    private boolean isReadyToGive = false;

    protected BowAttackGoalMixin(T actor, double speed, int attackInterval, float squaredRange) {
        this.actor = actor;
        this.speed = speed;
        this.attackInterval = attackInterval;
        this.squaredRange = squaredRange;
    }

    /**
     * @author Mojang AB
     * @reason 1
     */
    @Overwrite
    public void tick() {
        LivingEntity livingEntity = this.actor.getTarget();
        if(livingEntity == null) {
            return;
        }
        double d = this.actor.squaredDistanceTo(livingEntity.getX(), livingEntity.getY(), livingEntity.getZ());
        boolean bl = this.actor.getVisibilityCache().canSee(livingEntity);
        boolean bl2 = this.targetSeeingTicker > 0;
        if (bl != bl2) {
            this.targetSeeingTicker = 0;
        }

        if (bl) {
            ++this.targetSeeingTicker;
        } else {
            --this.targetSeeingTicker;
        }

        if (!(d > (double)this.squaredRange) && this.targetSeeingTicker >= 20) {
            this.actor.getNavigation().stop();
            ++this.combatTicks;
        } else {
            this.actor.getNavigation().startMovingTo(livingEntity, this.speed);
            this.combatTicks = -1;
        }

        if (this.combatTicks >= 20) {
            if ((double)this.actor.getRandom().nextFloat() < 0.3) {
                this.movingToLeft = !this.movingToLeft;
            }

            if ((double)this.actor.getRandom().nextFloat() < 0.3) {
                this.backward = !this.backward;
            }

            this.combatTicks = 0;
        }

        if (this.combatTicks > -1) {
            if (d > (double)(this.squaredRange * 0.75F)) {
                this.backward = false;
            } else if (d < (double)(this.squaredRange * 0.25F)) {
                this.backward = true;
            }

            this.actor.getMoveControl().strafeTo(this.backward ? -0.5F : 0.5F, this.movingToLeft ? 0.5F : -0.5F);
            this.actor.lookAtEntity(livingEntity, 30.0F, 30.0F);
        } else {
            this.actor.getLookControl().lookAt(livingEntity, 30.0F, 30.0F);
        }

        Hand hand = Hand.MAIN_HAND;

        if (isReadyToGive && d < 4F) {
            isReadyToGive = false;
            this.actor.clearActiveItem();
            this.actor.clearActiveItem();
            this.actor.attack(livingEntity, 0F);
            this.cooldown = this.attackInterval;
            actor.setStackInHand(hand, new ItemStack(Items.BOW));
        } else if (--this.cooldown <= 0 && this.targetSeeingTicker >= -60) {
            actor.setStackInHand(hand, itemStackA);
            isReadyToGive = true;
            this.actor.getNavigation().startMovingTo(livingEntity, this.speed);
            this.actor.setCurrentHand(hand);
        }
    }
}
