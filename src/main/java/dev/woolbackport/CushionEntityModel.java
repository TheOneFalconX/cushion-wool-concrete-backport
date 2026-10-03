package dev.woolbackport;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

/**
 * A single flat box: 16x16 footprint (a full block), 4 units tall (1/4 block), matching
 * the UV layout measured from the real cushion texture (two 16x16 squares for top/bottom,
 * a 4-tall strip of four 16-wide faces for the sides).
 */
public class CushionEntityModel extends EntityModel<CushionEntityRenderState> {
    private static final String BODY = "body";
    private final ModelPart body;

    public CushionEntityModel(ModelPart root) {
        super(root);
        this.body = root.getChild(BODY);
    }

    public static LayerDefinition getTexturedModelData() {
        MeshDefinition modelData = new MeshDefinition();
        PartDefinition root = modelData.getRoot();
        root.addOrReplaceChild(
            BODY,
            CubeListBuilder.create().texOffs(0, 0).addBox(-8, -4, -8, 16, 4, 16),
            PartPose.offset(0, 0, 0)
        );
        return LayerDefinition.create(modelData, 64, 64);
    }

    @Override
    public void setupAnim(CushionEntityRenderState state) {
        // Static decoration; nothing to animate.
    }
}
