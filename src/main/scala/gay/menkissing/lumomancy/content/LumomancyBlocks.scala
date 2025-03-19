/*
 * Copyright (c) BulbyVR/TheDrawingCoder-Gamer 2025.
 *
 * This file is part of Lumomancy.
 *
 * Lumomancy is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the license, or (at your option) any later version.
 *
 * Lumomancy is distributed in the hopes it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 *  PARTICULAR PURPOSE. See the Lesser GNU General Public License for more details.
 *
 * You should have received a copy of the Lesser GNU General Public License along with Lumomancy. If not,
 *  see <https://www.gnu.org/licenses/>
 */

package gay.menkissing.lumomancy.content

import com.google.common.collect.ImmutableMap
import gay.menkissing.lumomancy.Lumomancy
import gay.menkissing.lumomancy.content.block.{StasisCooler, StrippablePillarBlock}
import gay.menkissing.lumomancy.content.block.entity.StasisCoolerBlockEntity
import gay.menkissing.lumomancy.mixin.AxeItemAccessor
import gay.menkissing.lumomancy.registries.{LumoBlockFamilies, LumomancyLootTables, LumomancyTags}
import gay.menkissing.lumomancy.util.resources.{*, given}
import net.fabricmc.api.{EnvType, Environment}
import net.fabricmc.fabric.api.`object`.builder.v1.block.`type`.{BlockSetTypeBuilder, WoodTypeBuilder}
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents
import net.minecraft.client.renderer.RenderType
import net.minecraft.core.{Direction, Registry}
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.{BlockItem, HangingSignItem, Item, SignItem}
import net.minecraft.world.level.block.entity.{BlockEntity, BlockEntityType}
import net.minecraft.world.level.block.{Block, Blocks, CeilingHangingSignBlock, DoorBlock, FenceBlock, FenceGateBlock, PressurePlateBlock, RotatedPillarBlock, SignBlock, SlabBlock, SoundType, StairBlock, StandingSignBlock, TrapDoorBlock, WallHangingSignBlock, WallSignBlock}
import net.minecraft.world.level.block.state.{BlockBehaviour, BlockState}
import net.minecraft.world.level.block.state.properties.{BlockSetType, NoteBlockInstrument, WoodType}
import net.minecraft.world.level.material.{MapColor, PushReaction}
import BlockBehaviour.Properties as BlockProps
import gay.menkissing.lumomancy.util.registry.InfoCollector
import gay.menkissing.lumomancy.util.registry.builder.{BlockBuilder, ItemBuilder}
import net.minecraft.tags.{BlockTags, ItemTags}

import scala.collection.mutable

object LumomancyBlocks:
  private val blockItems: mutable.ListBuffer[Item] = mutable.ListBuffer()

  extension (builder: BlockBuilder[?])
    def registerItem(): Block =
      val i = builder.register()
      blockItems.append(i.asItem())
      i

  extension (builder: ItemBuilder[?])
    def registerItem(): Item =
      val i = builder.register()
      blockItems.append(i.asItem())
      i

  def makeItem(rl: ResourceLocation, item: Item): Item =
    blockItems.append(item)
    Registry.register(BuiltInRegistries.ITEM, rl, item)
    
  def makeEntity[T <: BlockEntity](name: String, factory: BlockEntityType.BlockEntitySupplier[T], blocks: Block*): BlockEntityType[T] =
    val id = Lumomancy.locate(name)
    Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, id, BlockEntityType.Builder.of[T](factory, blocks*).build())

  // strength copied from chiseled bookshelf
  val stasisCooler: Block =
    InfoCollector.instance.block(Lumomancy.locate("stasis_cooler"), StasisCooler(BlockBehaviour.Properties.of().sound(SoundType.WOOD).strength(1.5f)))
                 .lang("Stasis Cooler")
                 .item()
                 .model(gen => item => gen.withExistingParent(item, stasisCooler.modelLoc.withSuffix("_inventory"))).build()
                 .tag(BlockTags.MINEABLE_WITH_AXE)
                 .dropSelf()
                 .registerItem()
  
  val stasisCoolerBlockEntity: BlockEntityType[StasisCoolerBlockEntity] =
    makeEntity("stasis_cooler",StasisCoolerBlockEntity.apply, stasisCooler)




  // STILL WOOD SET

  val stillwoodBlockSet = BlockSetTypeBuilder.copyOf(BlockSetType.OAK).register(Lumomancy.locate("stillwood"))
  val stillwoodWoodSet = WoodTypeBuilder.copyOf(WoodType.OAK).register(Lumomancy.locate("stillwood"), stillwoodBlockSet)




  val strippedStillwoodLog: Block =
    InfoCollector.instance.block("stripped_stillwood_log", Blocks.log(MapColor.COLOR_CYAN, MapColor.COLOR_CYAN))
                 .lang("Stripped Stillwood Log")
                 .blockItem().tag(LumomancyTags.item.stillwoodLogsTag).build()
                 .blockstate(gen => block => gen.logBlock(block))
                 .tag(LumomancyTags.block.stillwoodLogsTag)
                 .dropSelf()
                 .registerItem()
  val strippedStillwoodWood: Block =
    InfoCollector.instance.block("stripped_stillwood_wood", RotatedPillarBlock(BlockBehaviour.Properties.of().sound(SoundType.WOOD).instrument(NoteBlockInstrument.BASS).strength(2.0f).ignitedByLava()))
                      .lang("Stripped Stillwood Wood")
                      .blockItem().tag(LumomancyTags.item.stillwoodLogsTag).build()
                      .blockstate(gen => block => gen.woodBlock(block, strippedStillwoodLog.modelLoc))
                      .tag(LumomancyTags.block.stillwoodLogsTag)
                      .dropSelf()
                      .registerItem()
  val stillwoodLog: Block =
    InfoCollector.instance.block("stillwood_log",
      StrippablePillarBlock(strippedStillwoodLog,
                            LumomancyLootTables.stripStillwood,
                            BlockBehaviour.Properties.of()
                                          .sound(SoundType.WOOD).instrument(NoteBlockInstrument.BASS)
                                          .strength(2.0f).ignitedByLava()))
                      .lang("Stillwood Log")
                      .blockItem().tag(LumomancyTags.item.stillwoodLogsTag).build()
                      .tag(LumomancyTags.block.stillwoodLogsTag)
                      .blockstate(_.logBlock)
                      .dropSelf()
                      .registerItem()
  // wood is still a rotated pillar block
  val stillwoodWood: Block =
    InfoCollector.instance.block("stillwood_wood", StrippablePillarBlock(strippedStillwoodWood,
      LumomancyLootTables.stripStillwood,
      BlockBehaviour.Properties.of().sound(SoundType.WOOD).instrument(NoteBlockInstrument.BASS).strength(2.0f).ignitedByLava()))
                      .lang("Stillwood Wood")
                      .blockItem().tag(LumomancyTags.item.stillwoodLogsTag).build()
                      .tag(LumomancyTags.block.stillwoodLogsTag)
                      .dropSelf()
                      .blockstate(gen => block => gen.woodBlock(block, stillwoodLog.modelLoc))
                      .registerItem()

  val stillwoodPlanks: Block =
    InfoCollector.instance.block(Lumomancy.locate("stillwood_planks"),
      Block(BlockBehaviour.Properties.of().sound(SoundType.WOOD).instrument(NoteBlockInstrument.BASS).mapColor(MapColor.COLOR_CYAN).strength(2.0f, 3.0f).ignitedByLava()))
                      .lang("Stillwood Planks")
                      .blockItem().tag(ItemTags.PLANKS).build()
                      .tag(BlockTags.PLANKS)
                      .blockstate(_.simpleBlock)
                      .dropSelf()
                      .registerItem()

  val stillwoodSlab: Block =
    InfoCollector.instance.block(Lumomancy.locate("stillwood_slab"),
      SlabBlock(BlockBehaviour.Properties.of().sound(SoundType.WOOD).instrument(NoteBlockInstrument.BASS).strength(2.0f, 3.0f).ignitedByLava()))
                      .lang("Stillwood Slab")
                      .blockItem().tag(ItemTags.WOODEN_SLABS).build()
                      .tag(BlockTags.WOODEN_SLABS)
                      .lootTable(_.createSlabItemTable(stillwoodSlab))
                      .blockstate(gen => block => gen.slabBlock(block, stillwoodPlanks.modelLoc, stillwoodPlanks.modelLoc))
                      .registerItem()

  val stillwoodButton: Block =
    InfoCollector.instance.block(Lumomancy.locate("stillwood_button"), Blocks.woodenButton(stillwoodBlockSet))
                      .lang("Stillwood Button")
                      .item()
                      .tag(ItemTags.WOODEN_BUTTONS)
                      .model(gen => item =>
                        gen.buttonInventory(stillwoodButton.modelLoc.withSuffix("_inventory"), stillwoodPlanks.modelLoc)
                        gen.withExistingParent(item, stillwoodButton.modelLoc.withSuffix("_inventory"))
                      )
                      .build()
                      .tag(BlockTags.WOODEN_BUTTONS)
                      .blockstate(gen => block => gen.buttonBlock(block, stillwoodPlanks.modelLoc))
                      .dropSelf()
                      .registerItem()
  val stillwoodPressurePlate: Block =
    InfoCollector.instance.block(Lumomancy.locate("stillwood_pressure_plate"), PressurePlateBlock(stillwoodBlockSet, BlockBehaviour.Properties.of().sound(SoundType.WOOD).forceSolidOn().mapColor(stillwoodPlanks.defaultMapColor()).noCollission().strength(0.5f).pushReaction(PushReaction.DESTROY)))
                      .lang("Stillwood Pressure Plate")
                      .blockItem().tag(ItemTags.WOODEN_PRESSURE_PLATES).build()
                      .tag(BlockTags.WOODEN_PRESSURE_PLATES)
                      .blockstate(gen => block => gen.pressurePlateBlock(block, stillwoodPlanks.modelLoc))
                      .dropSelf()
                      .registerItem()
  val stillwoodFence: Block =
    InfoCollector.instance.block(Lumomancy.locate("stillwood_fence"), FenceBlock(BlockBehaviour.Properties.of().mapColor(stillwoodPlanks.defaultMapColor()).forceSolidOn().instrument(NoteBlockInstrument.BASS).strength(2.0f, 3.0f).sound(SoundType.WOOD).ignitedByLava()))
                      .lang("Stillwood Fence")
                      .item().model(gen => item =>
                        gen.fenceInventory(stillwoodFence.modelLoc.withSuffix("_inventory"), stillwoodPlanks.modelLoc)
                        gen.withExistingParent(item, stillwoodFence.modelLoc.withSuffix("_inventory"))
                      )
                      .tag(ItemTags.WOODEN_FENCES)
                      .build()
                      .tag(BlockTags.WOODEN_FENCES)
                      .blockstate(gen => block => gen.fenceBlock(block, stillwoodPlanks.modelLoc))
                      .dropSelf()
                      .registerItem()
  val stillwoodFenceGate: Block =
    InfoCollector.instance.block("stillwood_fence_gate", FenceGateBlock(stillwoodWoodSet, BlockBehaviour.Properties.of().mapColor(stillwoodPlanks.defaultMapColor()).forceSolidOn().instrument(NoteBlockInstrument.BASS).strength(2.0f, 3.0f).ignitedByLava()))
                      .lang("Stillwood Fence Gate")
                      .blockItem().tag(ItemTags.FENCE_GATES).build()
                      .tag(BlockTags.FENCE_GATES)
                      .blockstate(gen => block => gen.fenceGateBlock(block, stillwoodPlanks.modelLoc))
                      .dropSelf()
                      .registerItem()
  val stillwoodStairs: Block =
    InfoCollector.instance.block(Lumomancy.locate("stillwood_stairs"), StairBlock(stillwoodPlanks.defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(stillwoodPlanks)))
                      .lang("Stillwood Stairs")
                      .blockItem().tag(ItemTags.WOODEN_STAIRS).build()
                      .tag(BlockTags.WOODEN_STAIRS)
                      .blockstate(gen => block => gen.stairsBlock(block, stillwoodPlanks.modelLoc))
                      .dropSelf()
                      .registerItem()
  val stillwoodSign: Block =
    InfoCollector.instance.block(Lumomancy.locate("stillwood_sign"), StandingSignBlock(stillwoodWoodSet, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_SIGN).mapColor(MapColor.COLOR_CYAN)))
                      .lang("Stillwood Sign")
                      .tag(BlockTags.STANDING_SIGNS)
                      // hopefully the forward reference is tolerable here
                      .lootTable(_.createSingleItemTable(LumomancyBlocks.stillwoodSignItem))
                      .register()
  val stillwoodWallSign: Block =
    InfoCollector.instance.block(Lumomancy.locate("stillwood_wall_sign"), WallSignBlock(stillwoodWoodSet, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_WALL_SIGN).mapColor(MapColor.COLOR_CYAN).dropsLike(stillwoodSign)))
                      .blockstate(gen => block => gen.signBlock(stillwoodSign, block, stillwoodPlanks.modelLoc))
                      .tag(BlockTags.WALL_SIGNS)
                      .register()

  val stillwoodSignItem: Item =
    InfoCollector.instance.item(Lumomancy.locate("stillwood_sign"), SignItem(Item.Properties().stacksTo(16), stillwoodSign, stillwoodWallSign))
                 .lang("Stillwood Sign")
                 .tag(ItemTags.SIGNS)
                 .defaultModel()
                 .registerItem()

  val stillwoodHangingSign: Block =
    InfoCollector.instance.block(Lumomancy.locate("stillwood_hanging_sign"), CeilingHangingSignBlock(stillwoodWoodSet, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_HANGING_SIGN).mapColor(MapColor.COLOR_CYAN)))
                      .lang("Stillwood Hanging Sign")
                      .tag(BlockTags.CEILING_HANGING_SIGNS)
                      .lootTable(_.createSingleItemTable(LumomancyBlocks.stillwoodHangingSignItem))
                      .register()
  val stillwoodWallHangingSign: Block =
    InfoCollector.instance.block(Lumomancy.locate("stillwood_wall_hanging_sign"), WallHangingSignBlock(stillwoodWoodSet, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_WALL_HANGING_SIGN).mapColor(MapColor.COLOR_CYAN).dropsLike(stillwoodHangingSign)))
                      .tag(BlockTags.WALL_HANGING_SIGNS)
                      .blockstate(gen => block => gen.signBlock(stillwoodHangingSign, block, strippedStillwoodLog.modelLoc))
                      .register()

  val stillwoodHangingSignItem: Item =
    InfoCollector.instance.item(Lumomancy.locate("stillwood_hanging_sign"), HangingSignItem(stillwoodHangingSign, stillwoodWallHangingSign, Item.Properties().stacksTo(16)))
                 .lang("Stillwood Hanging Sign")
                 .tag(ItemTags.HANGING_SIGNS)
                 .defaultModel()
                 .registerItem()

  val stillwoodDoor: Block =
    InfoCollector.instance.block("stillwood_door", DoorBlock(stillwoodBlockSet, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_DOOR).mapColor(stillwoodPlanks.defaultMapColor())))
                      .lang("Stillwood Door")
                      .item().defaultModel()
                      .tag(ItemTags.WOODEN_DOORS)
                      .build()
                      .tag(BlockTags.WOODEN_DOORS)
                      .lootTable(_.createDoorTable(stillwoodDoor))
                      .blockstate(_.doorBlock)
                      .registerItem()
  val stillwoodTrapdoor: Block =
    InfoCollector.instance.block("stillwood_trapdoor", TrapDoorBlock(stillwoodBlockSet, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_TRAPDOOR).mapColor(stillwoodPlanks.defaultMapColor())))
                      .lang("Stillwood Trapdoor")
                      .item()
                      .tag(ItemTags.WOODEN_TRAPDOORS)
                      .model(gen => item => gen.withExistingParent(item, stillwoodTrapdoor.modelLoc.extend("_bottom"))).build()
                      .blockstate(gen => block => gen.trapdoorBlock(block))
                      .tag(BlockTags.WOODEN_TRAPDOORS)
                      .dropSelf()
                      .registerItem()

  // wieder wood
  val wiederBlockSet = BlockSetTypeBuilder.copyOf(BlockSetType.OAK).register(Lumomancy.locate("wieder"))
  val wiederWoodSet = WoodTypeBuilder.copyOf(WoodType.OAK).register(Lumomancy.locate("wieder"), wiederBlockSet)

  val strippedWiederLog: Block =
    InfoCollector.instance.block("stripped_wieder_log", Blocks.log(MapColor.COLOR_MAGENTA, MapColor.COLOR_MAGENTA))
                 .lang("Stripped Wieder Log")
                 .blockItem().tag(LumomancyTags.item.wiederLogsTag).build()
                 .tag(LumomancyTags.block.wiederLogsTag)
                 .blockstate(gen => block => gen.logBlock(block))
                 .dropSelf()
                 .registerItem()
  val strippedWiederWood: Block =
    InfoCollector.instance.block("stripped_wieder_wood", RotatedPillarBlock(BlockBehaviour.Properties.of().sound(SoundType.WOOD).instrument(NoteBlockInstrument.BASS).strength(2.0f).ignitedByLava()))
                      .lang("Stripped Wieder Wood")
                      .blockItem().tag(LumomancyTags.item.wiederLogsTag).build()
                      .tag(LumomancyTags.block.wiederLogsTag)
                      .blockstate(gen => block => gen.woodBlock(block, strippedWiederLog.modelLoc))
                      .dropSelf()
                      .registerItem()
  val wiederLog: Block =
    InfoCollector.instance.block("wieder_log",
      StrippablePillarBlock(strippedWiederLog,
                            LumomancyLootTables.stripWieder,
                            BlockBehaviour.Properties.of()
                                          .sound(SoundType.WOOD).instrument(NoteBlockInstrument.BASS)
                                          .strength(2.0f).ignitedByLava()))
                      .lang("Wieder Log")
                      .blockItem().tag(LumomancyTags.item.wiederLogsTag).build()
                      .tag(LumomancyTags.block.wiederLogsTag)
                      .blockstate(_.logBlock)
                      .dropSelf()
                      .registerItem()
  // wood is still a rotated pillar block
  val wiederWood: Block =
    InfoCollector.instance.block("wieder_wood", StrippablePillarBlock(strippedWiederWood,
      LumomancyLootTables.stripWieder,
      BlockBehaviour.Properties.of().sound(SoundType.WOOD).instrument(NoteBlockInstrument.BASS).strength(2.0f).ignitedByLava()))
                      .lang("Wieder Wood")
                      .blockItem().tag(LumomancyTags.item.wiederLogsTag).build()
                      .tag(LumomancyTags.block.wiederLogsTag)
                      .blockstate(gen => block => gen.woodBlock(block, wiederLog.modelLoc))
                      .dropSelf()
                      .registerItem()

  val wiederPlanks: Block =
    InfoCollector.instance.block(Lumomancy.locate("wieder_planks"),
      Block(BlockBehaviour.Properties.of().sound(SoundType.WOOD).instrument(NoteBlockInstrument.BASS).mapColor(MapColor.COLOR_MAGENTA).strength(2.0f, 3.0f).ignitedByLava()))
                      .lang("Wieder Planks")
                      .blockItem().tag(ItemTags.PLANKS).build()
                      .tag(BlockTags.PLANKS)
                      .blockstate(_.simpleBlock)
                      .dropSelf()
                      .registerItem()

  val wiederSlab: Block =
    InfoCollector.instance.block(Lumomancy.locate("wieder_slab"),
      SlabBlock(BlockBehaviour.Properties.of().sound(SoundType.WOOD).instrument(NoteBlockInstrument.BASS).strength(2.0f, 3.0f).ignitedByLava()))
                      .lang("Wieder Slab")
                      .blockItem().tag(ItemTags.WOODEN_SLABS).build()
                      .tag(BlockTags.WOODEN_SLABS)
                      .blockstate(gen => block => gen.slabBlock(block, wiederPlanks.modelLoc, wiederPlanks.modelLoc))
                      .lootTable(_.createSlabItemTable(wiederSlab))
                      .registerItem()

  val wiederButton: Block =
    InfoCollector.instance.block(Lumomancy.locate("wieder_button"), Blocks.woodenButton(wiederBlockSet))
                      .lang("Wieder Button")
                      .item().model(gen => item =>
                        gen.buttonInventory(wiederButton.modelLoc.withSuffix("_inventory"), wiederPlanks.modelLoc)
                        gen.withExistingParent(item, wiederButton.modelLoc.withSuffix("_inventory"))
                      )
                      .tag(ItemTags.WOODEN_BUTTONS)
                      .build()
                      .tag(BlockTags.WOODEN_BUTTONS)
                      .blockstate(gen => block => gen.buttonBlock(block, wiederPlanks.modelLoc))
                      .dropSelf()
                      .registerItem()
  val wiederPressurePlate: Block =
    InfoCollector.instance.block(Lumomancy.locate("wieder_pressure_plate"), PressurePlateBlock(wiederBlockSet, BlockBehaviour.Properties.of().sound(SoundType.WOOD).forceSolidOn().mapColor(wiederPlanks.defaultMapColor()).noCollission().strength(0.5f).pushReaction(PushReaction.DESTROY)))
                      .lang("Wieder Pressure Plate")
                      .blockItem().tag(ItemTags.WOODEN_PRESSURE_PLATES).build()
                      .tag(BlockTags.WOODEN_PRESSURE_PLATES)
                      .blockstate(gen => block => gen.pressurePlateBlock(block, wiederPlanks.modelLoc))
                      .dropSelf()
                      .registerItem()
  val wiederFence: Block =
    InfoCollector.instance.block(Lumomancy.locate("wieder_fence"), FenceBlock(BlockBehaviour.Properties.of().mapColor(wiederPlanks.defaultMapColor()).forceSolidOn().instrument(NoteBlockInstrument.BASS).strength(2.0f, 3.0f).sound(SoundType.WOOD).ignitedByLava()))
                      .lang("Wieder Fence")
                      .item().model(gen => item =>
                        gen.fenceInventory(wiederFence.modelLoc.withSuffix("_inventory"), wiederPlanks.modelLoc)
                        gen.withExistingParent(item, wiederFence.modelLoc.withSuffix("_inventory"))
                      )
                      .tag(ItemTags.WOODEN_FENCES)
                      .build()
                      .tag(BlockTags.WOODEN_FENCES)
                      .blockstate(gen => block => gen.fenceBlock(block, wiederPlanks.modelLoc))
                      .dropSelf()
                      .registerItem()
  val wiederFenceGate: Block =
    InfoCollector.instance.block("wieder_fence_gate", FenceGateBlock(wiederWoodSet, BlockBehaviour.Properties.of().mapColor(wiederPlanks.defaultMapColor()).forceSolidOn().instrument(NoteBlockInstrument.BASS).strength(2.0f, 3.0f).ignitedByLava()))
                      .lang("Wieder Fence Gate")
                      .blockstate(gen => block => gen.fenceGateBlock(block, wiederPlanks.modelLoc))
                      .blockItem().tag(ItemTags.FENCE_GATES).build()
                      .tag(BlockTags.FENCE_GATES)
                      .dropSelf()
                      .registerItem()
  val wiederStairs: Block =
    InfoCollector.instance.block(Lumomancy.locate("wieder_stairs"), StairBlock(wiederPlanks.defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(wiederPlanks)))
                      .lang("Wieder Stairs")
                      .blockItem().tag(ItemTags.WOODEN_STAIRS).build()
                      .tag(BlockTags.WOODEN_STAIRS)
                      .blockstate(gen => block => gen.stairsBlock(block, wiederPlanks.modelLoc))
                      .dropSelf()
                      .registerItem()
  val wiederSign: Block =
    InfoCollector.instance.block(Lumomancy.locate("wieder_sign"), StandingSignBlock(wiederWoodSet, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_SIGN).mapColor(wiederPlanks.defaultMapColor())))
                      .lang("Wieder Sign")
                      .tag(BlockTags.STANDING_SIGNS)
                      // hopefully the forward reference is tolerable here
                      .lootTable(_.createSingleItemTable(LumomancyBlocks.wiederSignItem))
                      .register()
  val wiederWallSign: Block =
    InfoCollector.instance.block(Lumomancy.locate("wieder_wall_sign"), WallSignBlock(wiederWoodSet, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_WALL_SIGN).mapColor(wiederPlanks.defaultMapColor()).dropsLike(wiederSign)))
                      .blockstate(gen => block => gen.signBlock(wiederSign, block, wiederPlanks.modelLoc))
                      .tag(BlockTags.WALL_SIGNS)
                      .register()

  val wiederSignItem: Item =
    InfoCollector.instance.item(Lumomancy.locate("wieder_sign"), SignItem(Item.Properties().stacksTo(16), wiederSign, wiederWallSign))
                 .lang("Wieder Sign")
                 .defaultModel()
                 .tag(ItemTags.SIGNS)
                 .registerItem()

  val wiederHangingSign: Block =
    InfoCollector.instance.block(Lumomancy.locate("wieder_hanging_sign"), CeilingHangingSignBlock(wiederWoodSet, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_HANGING_SIGN).mapColor(wiederPlanks.defaultMapColor())))
                      .lang("Wieder Hanging Sign")
                      .lootTable(_.createSingleItemTable(LumomancyBlocks.wiederHangingSignItem))
                      .tag(BlockTags.CEILING_HANGING_SIGNS)
                      .register()
  val wiederWallHangingSign: Block =
    InfoCollector.instance.block(Lumomancy.locate("wieder_wall_hanging_sign"), WallHangingSignBlock(wiederWoodSet, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_WALL_HANGING_SIGN).mapColor(wiederPlanks.defaultMapColor()).dropsLike(wiederHangingSign)))
                 .blockstate(gen => block => gen.signBlock(wiederHangingSign, block, strippedWiederLog.modelLoc))
                 .tag(BlockTags.WALL_HANGING_SIGNS)
                 .register()

  val wiederHangingSignItem: Item =
    InfoCollector.instance.item(Lumomancy.locate("wieder_hanging_sign"), HangingSignItem(wiederHangingSign, wiederWallHangingSign, Item.Properties().stacksTo(16)))
                 .lang("Wieder Hanging Sign")
                 .defaultModel()
                 .tag(ItemTags.HANGING_SIGNS)
                 .registerItem()

  val wiederDoor: Block =
    InfoCollector.instance.block("wieder_door", DoorBlock(wiederBlockSet, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_DOOR).mapColor(wiederPlanks.defaultMapColor())))
                      .lang("Wieder Door")
                      .item().defaultModel().tag(ItemTags.WOODEN_DOORS).build()
                      .tag(BlockTags.WOODEN_DOORS)
                      .lootTable(_.createDoorTable(wiederDoor))
                      .blockstate(_.doorBlock)
                      .registerItem()
  val wiederTrapdoor: Block =
    InfoCollector.instance.block("wieder_trapdoor", TrapDoorBlock(wiederBlockSet, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_TRAPDOOR).mapColor(wiederPlanks.defaultMapColor())))
                      .lang("Wieder Trapdoor")
                      .item()
                      .tag(ItemTags.WOODEN_TRAPDOORS)
                      .model(gen => item => gen.withExistingParent(item, wiederTrapdoor.modelLoc.withSuffix("_bottom"))).build()
                      .blockstate(gen => block => gen.trapdoorBlock(block))
                      .tag(BlockTags.WOODEN_TRAPDOORS)
                      .dropSelf()
                      .registerItem()


  // aftus wood
  val aftusBlockSet = BlockSetTypeBuilder.copyOf(BlockSetType.OAK).register(Lumomancy.locate("aftus"))
  val aftusWoodSet = WoodTypeBuilder.copyOf(WoodType.OAK).register(Lumomancy.locate("aftus"), wiederBlockSet)

  val strippedAftusLog: Block =
    InfoCollector.instance.block("stripped_aftus_log", Blocks.log(MapColor.COLOR_YELLOW, MapColor.COLOR_YELLOW))
                 .lang("Stripped Aftus Log")
                 .blockItem().tag(LumomancyTags.item.aftusLogsTag).build()
                 .tag(LumomancyTags.block.aftusLogsTag)
                 .blockstate(_.logBlock)
                 .dropSelf()
                 .registerItem()
  val strippedAftusWood: Block =
    InfoCollector.instance.block("stripped_aftus_wood", RotatedPillarBlock(BlockBehaviour.Properties.of().sound(SoundType.WOOD).instrument(NoteBlockInstrument.BASS).strength(2.0f).ignitedByLava()))
                      .lang("Stripped Aftus Wood")
                      .blockItem().tag(LumomancyTags.item.aftusLogsTag).build()
                      .tag(LumomancyTags.block.aftusLogsTag)
                      .blockstate(gen => block => gen.woodBlock(block, strippedAftusLog.modelLoc))
                      .dropSelf()
                      .registerItem()
  val aftusLog: Block =
    InfoCollector.instance.block("aftus_log",
      StrippablePillarBlock(strippedAftusLog,
                            LumomancyLootTables.stripAftus,
                            BlockBehaviour.Properties.of()
                                          .sound(SoundType.WOOD).instrument(NoteBlockInstrument.BASS)
                                          .strength(2.0f).ignitedByLava()))
                      .lang("Aftus Log")
                      .blockItem().tag(LumomancyTags.item.aftusLogsTag).build()
                      .tag(LumomancyTags.block.aftusLogsTag)
                      .blockstate(_.logBlock)
                      .dropSelf()
                      .registerItem()
  // wood is still a rotated pillar block
  val aftusWood: Block =
    InfoCollector.instance.block("aftus_wood", StrippablePillarBlock(strippedAftusWood,
      LumomancyLootTables.stripAftus,
      BlockBehaviour.Properties.of().sound(SoundType.WOOD).instrument(NoteBlockInstrument.BASS).strength(2.0f).ignitedByLava()))
                      .lang("Aftus Wood")
                      .blockItem().tag(LumomancyTags.item.aftusLogsTag).build()
                      .tag(LumomancyTags.block.aftusLogsTag)
                      .blockstate(gen => block => gen.woodBlock(block, aftusLog.modelLoc))
                      .dropSelf()
                      .registerItem()

  val aftusPlanks: Block =
    InfoCollector.instance.block(Lumomancy.locate("aftus_planks"),
      Block(BlockBehaviour.Properties.of().sound(SoundType.WOOD).instrument(NoteBlockInstrument.BASS).mapColor(MapColor.COLOR_YELLOW).strength(2.0f, 3.0f).ignitedByLava()))
                      .lang("Aftus Planks")
                      .blockItem().tag(ItemTags.PLANKS).build()
                      .tag(BlockTags.PLANKS)
                      .blockstate(_.simpleBlock)
                      .dropSelf()
                      .registerItem()

  val aftusSlab: Block =
    InfoCollector.instance.block(Lumomancy.locate("aftus_slab"),
      SlabBlock(BlockBehaviour.Properties.of().sound(SoundType.WOOD).instrument(NoteBlockInstrument.BASS).strength(2.0f, 3.0f).ignitedByLava()))
                      .lang("Aftus Slab")
                      .blockItem().tag(ItemTags.WOODEN_SLABS).build()
                      .tag(BlockTags.WOODEN_SLABS)
                      .blockstate(gen => block => gen.slabBlock(block, aftusPlanks.modelLoc, aftusPlanks.modelLoc))
                      .lootTable(_.createSlabItemTable(aftusSlab))
                      .registerItem()

  val aftusButton: Block =
    InfoCollector.instance.block(Lumomancy.locate("aftus_button"), Blocks.woodenButton(aftusBlockSet))
                    .lang("Aftus Button")
                    .item().model(gen => item =>
                        gen.buttonInventory(aftusButton.modelLoc.withSuffix("_inventory"), aftusPlanks.modelLoc)
                        gen.withExistingParent(item, aftusButton.modelLoc.withSuffix("_inventory"))
                    )
                    .tag(ItemTags.WOODEN_BUTTONS)
                    .build()
                    .tag(BlockTags.WOODEN_BUTTONS)
                    .blockstate(gen => block => gen.buttonBlock(block, aftusPlanks.modelLoc))
                    .dropSelf()
                    .registerItem()
  val aftusPressurePlate: Block =
    InfoCollector.instance.block(Lumomancy.locate("aftus_pressure_plate"), PressurePlateBlock(aftusBlockSet, BlockBehaviour.Properties.of().sound(SoundType.WOOD).forceSolidOn().mapColor(aftusPlanks.defaultMapColor()).noCollission().strength(0.5f).pushReaction(PushReaction.DESTROY)))
                      .lang("Aftus Pressure Plate")
                      .blockItem().tag(ItemTags.WOODEN_PRESSURE_PLATES).build()
                      .tag(BlockTags.WOODEN_PRESSURE_PLATES)
                      .dropSelf()
                      .blockstate(gen => block => gen.pressurePlateBlock(block, aftusPlanks.modelLoc))
                      .registerItem()
  val aftusFence: Block =
    InfoCollector.instance.block(Lumomancy.locate("aftus_fence"), FenceBlock(BlockBehaviour.Properties.of().mapColor(aftusPlanks.defaultMapColor()).forceSolidOn().instrument(NoteBlockInstrument.BASS).strength(2.0f, 3.0f).sound(SoundType.WOOD).ignitedByLava()))
                      .lang("Aftus Fence")
                      .item().model(gen => item =>
                        gen.fenceInventory(aftusFence.modelLoc.withSuffix("_inventory"), aftusPlanks.modelLoc)
                        gen.withExistingParent(item, aftusFence.modelLoc.withSuffix("_inventory"))
                      ).tag(ItemTags.WOODEN_FENCES).build()
                      .tag(BlockTags.WOODEN_FENCES)
                      .blockstate(gen => block => gen.fenceBlock(block, aftusPlanks.modelLoc))
                      .dropSelf()
                      .registerItem()
  val aftusFenceGate: Block =
    InfoCollector.instance.block("aftus_fence_gate", FenceGateBlock(aftusWoodSet, BlockBehaviour.Properties.of().mapColor(aftusPlanks.defaultMapColor()).forceSolidOn().instrument(NoteBlockInstrument.BASS).strength(2.0f, 3.0f).ignitedByLava()))
                      .lang("Aftus Fence Gate")
                      .blockItem().tag(ItemTags.FENCE_GATES).build()
                      .tag(BlockTags.FENCE_GATES)
                      .dropSelf()
                      .blockstate(gen => block => gen.fenceGateBlock(block, aftusPlanks.modelLoc))
                      .registerItem()
  val aftusStairs: Block =
    InfoCollector.instance.block(Lumomancy.locate("aftus_stairs"), StairBlock(aftusPlanks.defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(aftusPlanks)))
                      .lang("Aftus Stairs")
                      .blockItem().tag(ItemTags.WOODEN_STAIRS).build()
                      .tag(BlockTags.WOODEN_STAIRS)
                      .blockstate(gen => block => gen.stairsBlock(block, aftusPlanks.modelLoc))
                      .dropSelf()
                      .registerItem()
  val aftusSign: Block =
    InfoCollector.instance.block(Lumomancy.locate("aftus_sign"), StandingSignBlock(aftusWoodSet, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_SIGN).mapColor(aftusPlanks.defaultMapColor())))
                      .lang("Aftus Sign")
                      .tag(BlockTags.STANDING_SIGNS)
                      // hopefully the forward reference is tolerable here
                      .lootTable(_.createSingleItemTable(LumomancyBlocks.aftusSignItem))
                      .register()
  val aftusWallSign: Block =
    InfoCollector.instance.block(Lumomancy.locate("aftus_wall_sign"), WallSignBlock(aftusWoodSet, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_WALL_SIGN).mapColor(aftusPlanks.defaultMapColor()).dropsLike(aftusSign)))
                      .blockstate(gen => block => gen.signBlock(aftusSign, block, aftusPlanks.modelLoc))
                      .tag(BlockTags.WALL_SIGNS)
                      .register()

  val aftusSignItem: Item =
    InfoCollector.instance.item(Lumomancy.locate("aftus_sign"), SignItem(Item.Properties().stacksTo(16), aftusSign, aftusWallSign))
                 .lang("Aftus Sign")
                 .defaultModel()
                 .tag(ItemTags.SIGNS)
                 .registerItem()

  val aftusHangingSign: Block =
    InfoCollector.instance.block(Lumomancy.locate("aftus_hanging_sign"), CeilingHangingSignBlock(aftusWoodSet, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_HANGING_SIGN).mapColor(aftusPlanks.defaultMapColor())))
                      .lang("Aftus Hanging Sign")
                      .lootTable(_.createSingleItemTable(LumomancyBlocks.aftusHangingSignItem))
                      .tag(BlockTags.CEILING_HANGING_SIGNS)
                      .register()
  val aftusWallHangingSign: Block =
    InfoCollector.instance.block(Lumomancy.locate("aftus_wall_hanging_sign"), WallHangingSignBlock(aftusWoodSet, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_WALL_HANGING_SIGN).mapColor(aftusPlanks.defaultMapColor()).dropsLike(aftusHangingSign)))
                      .blockstate(gen => block => gen.signBlock(aftusHangingSign, block, strippedAftusLog.modelLoc))
                      .tag(BlockTags.WALL_HANGING_SIGNS)
                      .register()

  val aftusHangingSignItem: Item =
    InfoCollector.instance.item(Lumomancy.locate("aftus_hanging_sign"), HangingSignItem(aftusHangingSign, aftusWallHangingSign, Item.Properties().stacksTo(16)))
                 .lang("Aftus Hanging Sign")
                 .defaultModel()
                 .tag(ItemTags.HANGING_SIGNS)
                 .registerItem()

  val aftusDoor: Block =
    InfoCollector.instance.block("aftus_door", DoorBlock(aftusBlockSet, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_DOOR).mapColor(aftusPlanks.defaultMapColor())))
                      .lang("Aftus Door")
                      .item().defaultModel().tag(ItemTags.WOODEN_DOORS).build()
                      .tag(BlockTags.WOODEN_DOORS)
                      .lootTable(_.createDoorTable(aftusDoor))
                      .blockstate(_.doorBlock)
                      .registerItem()
  val aftusTrapdoor: Block =
    InfoCollector.instance.block("aftus_trapdoor", TrapDoorBlock(aftusBlockSet, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_TRAPDOOR).mapColor(aftusPlanks.defaultMapColor())))
                      .lang("Aftus Trapdoor")
                      .item()
                      .model(gen => item => gen.withExistingParent(item, aftusTrapdoor.modelLoc.withSuffix("_bottom")))
                      .tag(ItemTags.WOODEN_TRAPDOORS)
                      .build()
                      .tag(BlockTags.WOODEN_TRAPDOORS)
                      .blockstate(gen => block => gen.trapdoorBlock(block))
                      .dropSelf()
                      .registerItem()
  
  @Environment(EnvType.CLIENT)
  def registerClient(): Unit =
    BlockRenderLayerMap.INSTANCE.putBlock(stillwoodDoor, RenderType.cutout())
    BlockRenderLayerMap.INSTANCE.putBlock(stillwoodTrapdoor, RenderType.cutout())
    BlockRenderLayerMap.INSTANCE.putBlock(wiederDoor, RenderType.cutout())
    BlockRenderLayerMap.INSTANCE.putBlock(wiederTrapdoor, RenderType.cutout())
    BlockRenderLayerMap.INSTANCE.putBlock(aftusDoor, RenderType.cutout())
    BlockRenderLayerMap.INSTANCE.putBlock(aftusTrapdoor, RenderType.cutout())


  def init(): Unit =
    StasisCoolerBlockEntity.registerStorages()
    LumoBlockFamilies.register()
    ItemGroupEvents.modifyEntriesEvent(LumomancyItems.itemGroupKey).register { group =>
      blockItems.foreach(group.accept)
    }
    val updatedMap = java.util.HashMap(AxeItemAccessor.getStrippables)

    updatedMap.put(stillwoodLog, strippedStillwoodLog)
    updatedMap.put(stillwoodWood, strippedStillwoodWood)
    updatedMap.put(wiederLog, strippedWiederLog)
    updatedMap.put(wiederWood, strippedWiederWood)
    updatedMap.put(aftusLog, strippedAftusLog)
    updatedMap.put(aftusWood, strippedAftusWood)

    AxeItemAccessor.setStrippables(ImmutableMap.copyOf(updatedMap))

    BlockEntityType.SIGN.addSupportedBlock(stillwoodSign)
    BlockEntityType.SIGN.addSupportedBlock(stillwoodWallSign)
    BlockEntityType.SIGN.addSupportedBlock(wiederSign)
    BlockEntityType.SIGN.addSupportedBlock(wiederWallSign)
    BlockEntityType.SIGN.addSupportedBlock(aftusSign)
    BlockEntityType.SIGN.addSupportedBlock(aftusWallSign)
    BlockEntityType.HANGING_SIGN.addSupportedBlock(stillwoodHangingSign)
    BlockEntityType.HANGING_SIGN.addSupportedBlock(stillwoodWallHangingSign)
    BlockEntityType.HANGING_SIGN.addSupportedBlock(wiederHangingSign)
    BlockEntityType.HANGING_SIGN.addSupportedBlock(wiederWallHangingSign)
    BlockEntityType.HANGING_SIGN.addSupportedBlock(aftusHangingSign)
    BlockEntityType.HANGING_SIGN.addSupportedBlock(aftusWallHangingSign)
    
