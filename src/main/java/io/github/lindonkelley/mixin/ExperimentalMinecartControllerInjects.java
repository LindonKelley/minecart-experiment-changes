package io.github.lindonkelley.mixin;

import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.ExperimentalMinecartController;
import net.minecraft.entity.vehicle.MinecartController;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import io.github.lindonkelley.MinecartExperimentChanges;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;

@Mixin(ExperimentalMinecartController.class)
public abstract class ExperimentalMinecartControllerInjects extends MinecartController {
	protected ExperimentalMinecartControllerInjects(AbstractMinecartEntity minecart) {
		super(minecart);
	}

	// mud bug reimplementation
	@Inject(
		method = "moveOnRail",
		require = 1,
		at = @At(
			value = "INVOKE_ASSIGN",
			target = "Lnet/minecraft/block/AbstractRailBlock;isRail(Lnet/minecraft/block/BlockState;)Z"
		),
		locals = LocalCapture.CAPTURE_FAILHARD
	)
	public void snapMinecartDown(ServerWorld world, CallbackInfo info, ExperimentalMinecartController.MoveIteration moveIteration, Vec3d velocity, @Local LocalRef<BlockPos> blockPos, @Local LocalRef<BlockState> blockState, @Local LocalBooleanRef blockStateIsRail) {
		if (!blockStateIsRail.get()) {
			BlockPos flooredBlockPos = BlockPos.ofFloored(this.minecart.getPos()).down();
			BlockState flooredBlockState = world.getBlockState(flooredBlockPos);
			if (AbstractRailBlock.isRail(flooredBlockState)) {
				blockPos.set(flooredBlockPos);
				blockState.set(flooredBlockState);
				blockStateIsRail.set(true);
				if (MinecartExperimentChanges.LOGGER.isDebugEnabled()) {
					MinecartExperimentChanges.LOGGER.debug("minecart at {} adjusted", this.minecart.getPos());
				}
			}
		}
	}
	
	// activator rail fix, implements the default controller's override
	@Override
	public Direction getHorizontalFacing() {
		return this.minecart.isYawFlipped()
			? this.minecart.getHorizontalFacing().getOpposite().rotateYClockwise()
			: this.minecart.getHorizontalFacing().rotateYClockwise();
	}
}