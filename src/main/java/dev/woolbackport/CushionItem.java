package dev.woolbackport;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/** Places a {@link CushionEntity} of a fixed color. No surface restriction: works on any clicked face, at any Y. */
public class CushionItem extends Item {
    private final String color;

    public CushionItem(String color, Properties properties) {
        super(properties);
        this.color = color;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos placeAt = context.getClickedPos().relative(context.getClickedFace());

        if (!level.isClientSide) {
            CushionEntity cushion = new CushionEntity(WoolBackport.CUSHION, level);
            cushion.setColor(color);
            Vec3 center = Vec3.atBottomCenterOf(placeAt);
            cushion.setPos(center.x, placeAt.getY(), center.z);
            level.addFreshEntity(cushion);
        }

        Player player = context.getPlayer();
        if (player != null && !player.getAbilities().instabuild) {
            context.getItemInHand().shrink(1);
        }
        return InteractionResult.SUCCESS;
    }
}
