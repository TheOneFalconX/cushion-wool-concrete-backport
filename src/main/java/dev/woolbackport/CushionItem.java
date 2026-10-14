package dev.woolbackport;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.phys.Vec3;

/** Places a CushionEntity grid-aligned horizontally, adapting to the surface Y-level of full blocks, slabs, and stairs. */
public class CushionItem extends Item {
    private final String color;

    public CushionItem(String color, Properties properties) {
        super(properties);
        this.color = color;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos clickedPos = context.getClickedPos();
        Direction face = context.getClickedFace();
        
        // Determine the actual support block position
        BlockPos supportPos = clickedPos;
        if (face == Direction.DOWN) {
            supportPos = clickedPos.below();
        }

        BlockState state = level.getBlockState(supportPos);
        double yCoord = supportPos.getY() + 1.0;

        // Calculate surface height of the support block dynamically
        Block block = state.getBlock();
        if (block instanceof SlabBlock) {
            SlabType type = state.getValue(SlabBlock.TYPE);
            if (type == SlabType.BOTTOM) {
                yCoord = supportPos.getY() + 0.5;
            } else {
                yCoord = supportPos.getY() + 1.0;
            }
        } else if (block instanceof StairBlock) {
            Half half = state.getValue(StairBlock.HALF);
            if (half == Half.BOTTOM) {
                yCoord = supportPos.getY() + 0.5;
            } else {
                yCoord = supportPos.getY() + 1.0;
            }
        }

        if (!level.isClientSide()) {
            CushionEntity cushion = new CushionEntity(WoolBackport.CUSHION, level);
            cushion.setColor(color);
            
            // Snap horizontally to the center of the block grid, but use the dynamic surface Y level
            Vec3 center = Vec3.atBottomCenterOf(supportPos);
            cushion.setPos(center.x, yCoord, center.z);
            
            level.addFreshEntity(cushion);
        }

        Player player = context.getPlayer();
        if (player != null && !player.getAbilities().instabuild) {
            context.getItemInHand().shrink(1);
        }
        return InteractionResult.SUCCESS;
    }
}
