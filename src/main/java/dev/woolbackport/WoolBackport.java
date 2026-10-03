package dev.woolbackport;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

/** Backports 26.3's wool/concrete stairs and slabs, and cushions (as a real entity), to 26.2. */
public class WoolBackport implements ModInitializer {
    public static final String MOD_ID = "wccbp";

    // Vanilla creative-tab order.
    private static final String[] COLORS = {
        "white", "orange", "magenta", "light_blue", "yellow", "lime", "pink", "gray",
        "light_gray", "cyan", "purple", "blue", "brown", "green", "red", "black"
    };

    public static EntityType<CushionEntity> CUSHION;
    private static final Map<String, Item> CUSHION_ITEMS = new HashMap<>();

    public static Item cushionItem(String color) {
        return CUSHION_ITEMS.get(color);
    }

    @Override
    public void onInitialize() {
        registerCushionEntityType();

        for (String color : COLORS) {
            registerPair(color + "_wool", true);
            registerPair(color + "_concrete", false);
            registerCushionItem(color);
        }
    }

    private static void registerCushionEntityType() {
        Identifier id = Identifier.fromNamespaceAndPath(MOD_ID, "cushion");
        ResourceKey<EntityType<?>> key = ResourceKey.create(Registries.ENTITY_TYPE, id);
        CUSHION = Registry.register(BuiltInRegistries.ENTITY_TYPE, key,
            EntityType.Builder.<CushionEntity>of(CushionEntity::new, MobCategory.MISC)
                .sized(1.0f, 0.25f)
                .build(key));
    }

    /** Registers the "{color}_cushion" item, which places a CushionEntity of that color. */
    private static void registerCushionItem(String color) {
        Identifier id = Identifier.fromNamespaceAndPath(MOD_ID, color + "_cushion");
        ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM, id);
        Item item = new CushionItem(color, new Item.Properties().setId(itemKey));
        Registry.register(BuiltInRegistries.ITEM, itemKey, item);
        CUSHION_ITEMS.put(color, item);

        Block woolSlab = BuiltInRegistries.BLOCK.getValue(Identifier.fromNamespaceAndPath(MOD_ID, color + "_wool_slab"));
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.BUILDING_BLOCKS)
            .register(output -> output.insertAfter(woolSlab, item));
    }

    /** Registers a stairs + slab pair copied from an existing vanilla block, e.g. "red_wool" or "red_concrete". */
    private static void registerPair(String baseName, boolean flammable) {
        Block source = BuiltInRegistries.BLOCK.getValue(Identifier.withDefaultNamespace(baseName));

        Block stairs = register(baseName + "_stairs", source, flammable,
            props -> new StairBlock(source.defaultBlockState(), props));
        Block slab = register(baseName + "_slab", source, flammable, SlabBlock::new);

        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.BUILDING_BLOCKS)
            .register(output -> output.insertAfter(source, stairs, slab));
    }

    private static Block register(String name, Block copyFrom, boolean flammable,
            Function<BlockBehaviour.Properties, Block> factory) {
        Identifier id = Identifier.fromNamespaceAndPath(MOD_ID, name);

        ResourceKey<Block> blockKey = ResourceKey.create(Registries.BLOCK, id);
        // ofFullCopy keeps the source block's sound, hardness, map color, instrument and lava behaviour.
        Block block = factory.apply(BlockBehaviour.Properties.ofFullCopy(copyFrom).setId(blockKey));
        Registry.register(BuiltInRegistries.BLOCK, blockKey, block);

        ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM, id);
        Registry.register(BuiltInRegistries.ITEM, itemKey,
            new BlockItem(block, new Item.Properties().setId(itemKey).useBlockDescriptionPrefix()));

        if (flammable) {
            // Same fire values as vanilla wool. Concrete is skipped.
            FlammableBlockRegistry.getDefaultInstance().add(block, 30, 60);
        }
        return block;
    }
}
