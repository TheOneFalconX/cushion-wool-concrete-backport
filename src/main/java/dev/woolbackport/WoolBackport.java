package dev.woolbackport;

import java.util.function.Function;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

/** Backports 26.3's wool stairs and slabs (all 16 colors) to 26.2. */
public class WoolBackport implements ModInitializer {
    public static final String MOD_ID = "woolbackport";

    // Vanilla creative-tab order.
    private static final String[] COLORS = {
        "white", "orange", "magenta", "light_blue", "yellow", "lime", "pink", "gray",
        "light_gray", "cyan", "purple", "blue", "brown", "green", "red", "black"
    };

    @Override
    public void onInitialize() {
        for (String color : COLORS) {
            Block wool = BuiltInRegistries.BLOCK.getValue(Identifier.withDefaultNamespace(color + "_wool"));

            Block stairs = register(color + "_wool_stairs", wool,
                props -> new StairBlock(wool.defaultBlockState(), props));
            Block slab = register(color + "_wool_slab", wool, SlabBlock::new);

            ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.COLORED_BLOCKS)
                .register(entries -> entries.addAfter(wool, stairs, slab));
        }
    }

    private static Block register(String name, Block copyFrom, Function<BlockBehaviour.Properties, Block> factory) {
        Identifier id = Identifier.fromNamespaceAndPath(MOD_ID, name);

        ResourceKey<Block> blockKey = ResourceKey.create(Registries.BLOCK, id);
        // ofFullCopy keeps wool's sound, hardness, map color, instrument and lava behaviour.
        Block block = factory.apply(BlockBehaviour.Properties.ofFullCopy(copyFrom).setId(blockKey));
        Registry.register(BuiltInRegistries.BLOCK, blockKey, block);

        ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM, id);
        Registry.register(BuiltInRegistries.ITEM, itemKey,
            new BlockItem(block, new Item.Properties().setId(itemKey).useBlockDescriptionPrefix()));

        // Same fire values as vanilla wool.
        FlammableBlockRegistry.getDefaultInstance().add(block, 30, 60);
        return block;
    }
}
