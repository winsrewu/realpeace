package xyz.jawbts.realpeace.mixin.goal;

import net.minecraft.entity.ai.goal.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(MeleeAttackGoal.class)
public abstract class MeleeAttackGoalMixin {
    /**
     * @author Mojang AB
     * @reason 1
     */
    @Overwrite
    public boolean canStart() {
        return false;
    }
}
