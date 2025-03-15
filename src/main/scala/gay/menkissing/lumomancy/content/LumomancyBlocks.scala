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
import gay.menkissing.lumomancy.registries.{LumoBlockFamilies, LumomancyLootTables}
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

import scala.collection.mutable

object LumomancyBlocks:
  private val blockItems: mutable.ListBuffer[Item] = mutable.ListBuffer()
  
  def makeWithItem(rl: ResourceLocation, block: Block): Block =
    val blockItem = BlockItem(block, Item.Properties())
    Registry.register(BuiltInRegistries.BLOCK, rl, block)
    Registry.register(BuiltInRegistries.ITEM, rl, blockItem)
    blockItems.append(blockItem)
    block

  def makeNoItem(rl: ResourceLocation, block: Block): Block =
    Registry.register(BuiltInRegistries.BLOCK, rl, block)

  def makeItem(rl: ResourceLocation, item: Item): Item =
    blockItems.append(item)
    Registry.register(BuiltInRegistries.ITEM, rl, item)
    
  def makeEntity[T <: BlockEntity](name: String, factory: BlockEntityType.BlockEntitySupplier[T], blocks: Block*): BlockEntityType[T] =
    val id = Lumomancy.locate(name)
    Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, id, BlockEntityType.Builder.of[T](factory, blocks*).build())

  // strength copied from chiseled bookshelf
  val stasisCooler: Block = makeWithItem(Lumomancy.locate("stasis_cooler"), StasisCooler(BlockBehaviour.Properties.of().sound(SoundType.WOOD).strength(1.5f)))
  
  val stasisCoolerBlockEntity: BlockEntityType[StasisCoolerBlockEntity] =
    makeEntity("stasis_cooler",StasisCoolerBlockEntity.apply, stasisCooler)




  // STILL WOOD SET

  val stillwoodBlockSet = BlockSetTypeBuilder.copyOf(BlockSetType.OAK).register(Lumomancy.locate("stillwood"))
  val stillwoodWoodSet = WoodTypeBuilder.copyOf(WoodType.OAK).register(Lumomancy.locate("stillwood"), stillwoodBlockSet)




  val strippedStillwoodLog: Block = makeWithItem(Lumomancy.locate("stripped_stillwood_log"), Blocks.log(MapColor.COLOR_CYAN, MapColor.COLOR_CYAN))
  val strippedStillwoodWood: Block = makeWithItem(Lumomancy.locate("stripped_stillwood_wood"), RotatedPillarBlock(BlockBehaviour.Properties.of().sound(SoundType.WOOD).instrument(NoteBlockInstrument.BASS).strength(2.0f).ignitedByLava()))
  val stillwoodLog: Block =
    makeWithItem(Lumomancy.locate("stillwood_log"),
      StrippablePillarBlock(strippedStillwoodLog,
                            LumomancyLootTables.stripStillwood,
                            BlockBehaviour.Properties.of()
                                          .sound(SoundType.WOOD).instrument(NoteBlockInstrument.BASS)
                                          .strength(2.0f).ignitedByLava()))
  // wood is still a rotated pillar block
  val stillwoodWood: Block = makeWithItem(Lumomancy.locate("stillwood_wood"), StrippablePillarBlock(strippedStillwoodWood,
    LumomancyLootTables.stripStillwood,
    BlockBehaviour.Properties.of().sound(SoundType.WOOD).instrument(NoteBlockInstrument.BASS).strength(2.0f).ignitedByLava()))

  val stillwoodPlanks: Block = makeWithItem(Lumomancy.locate("stillwood_planks"), Block(BlockBehaviour.Properties.of().sound(SoundType.WOOD).instrument(NoteBlockInstrument.BASS).strength(2.0f, 3.0f).ignitedByLava()))
  val stillwoodSlab: Block = makeWithItem(Lumomancy.locate("stillwood_slab"), SlabBlock(BlockBehaviour.Properties.of().sound(SoundType.WOOD).instrument(NoteBlockInstrument.BASS).strength(2.0f, 3.0f).ignitedByLava()))

  val stillwoodButton: Block = makeWithItem(Lumomancy.locate("stillwood_button"), Blocks.woodenButton(stillwoodBlockSet))
  val stillwoodPressurePlate: Block = makeWithItem(Lumomancy.locate("stillwood_pressure_plate"), PressurePlateBlock(stillwoodBlockSet, BlockBehaviour.Properties.of().sound(SoundType.WOOD).forceSolidOn().mapColor(stillwoodPlanks.defaultMapColor()).noCollission().strength(0.5f).pushReaction(PushReaction.DESTROY)))
  val stillwoodFence: Block = makeWithItem(Lumomancy.locate("stillwood_fence"), FenceBlock(BlockBehaviour.Properties.of().mapColor(stillwoodPlanks.defaultMapColor()).forceSolidOn().instrument(NoteBlockInstrument.BASS).strength(2.0f, 3.0f).sound(SoundType.WOOD).ignitedByLava()))
  val stillwoodFenceGate: Block = makeWithItem(Lumomancy.locate("stillwood_fence_gate"), FenceGateBlock(stillwoodWoodSet, BlockBehaviour.Properties.of().mapColor(stillwoodPlanks.defaultMapColor()).forceSolidOn().instrument(NoteBlockInstrument.BASS).strength(2.0f, 3.0f).ignitedByLava()))
  val stillwoodStairs: Block = makeWithItem(Lumomancy.locate("stillwood_stairs"), StairBlock(stillwoodPlanks.defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(stillwoodPlanks)))
  val stillwoodSign: Block = makeNoItem(Lumomancy.locate("stillwood_sign"), StandingSignBlock(stillwoodWoodSet, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_SIGN).mapColor(MapColor.COLOR_CYAN)))
  val stillwoodWallSign: Block = makeNoItem(Lumomancy.locate("stillwood_wall_sign"), WallSignBlock(stillwoodWoodSet, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_WALL_SIGN).mapColor(MapColor.COLOR_CYAN).dropsLike(stillwoodSign)))

  val stillwoodSignItem: Item = makeItem(Lumomancy.locate("stillwood_sign"), SignItem(Item.Properties().stacksTo(16), stillwoodSign, stillwoodWallSign))

  val stillwoodHangingSign: Block = makeNoItem(Lumomancy.locate("stillwood_hanging_sign"), CeilingHangingSignBlock(stillwoodWoodSet, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_HANGING_SIGN).mapColor(MapColor.COLOR_CYAN)))
  val stillwoodWallHangingSign: Block = makeNoItem(Lumomancy.locate("stillwood_wall_hanging_sign"), WallHangingSignBlock(stillwoodWoodSet, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_WALL_HANGING_SIGN).mapColor(MapColor.COLOR_CYAN).dropsLike(stillwoodHangingSign)))

  val stillwoodHangingSignItem: Item = makeItem(Lumomancy.locate("stillwood_hanging_sign"), HangingSignItem(stillwoodHangingSign, stillwoodWallHangingSign, Item.Properties().stacksTo(16)))

  val stillwoodDoor: Block = makeWithItem(Lumomancy.locate("stillwood_door"), DoorBlock(stillwoodBlockSet, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_DOOR).mapColor(stillwoodPlanks.defaultMapColor())))
  val stillwoodTrapdoor: Block = makeWithItem(Lumomancy.locate("stillwood_trapdoor"), TrapDoorBlock(stillwoodBlockSet, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_TRAPDOOR).mapColor(stillwoodPlanks.defaultMapColor())))

  // wieder wood
  val wiederBlockSet = BlockSetTypeBuilder.copyOf(BlockSetType.OAK).register(Lumomancy.locate("wieder"))
  val wiederWoodType = WoodTypeBuilder.copyOf(WoodType.OAK).register(Lumomancy.locate("wieder"), wiederBlockSet)

  val strippedWiederLog = makeWithItem(Lumomancy.locate("stripped_wieder_log"), Blocks.log(MapColor.COLOR_MAGENTA, MapColor.COLOR_MAGENTA))
  val strippedWiederWood = makeWithItem(Lumomancy.locate("stripped_wieder_wood"), RotatedPillarBlock(BlockProps.ofFullCopy(Blocks.OAK_WOOD).mapColor(MapColor.COLOR_MAGENTA)))
  val wiederLog = makeWithItem(Lumomancy.locate("wieder_log"), StrippablePillarBlock(strippedWiederLog, LumomancyLootTables.stripWieder,
    BlockProps.ofFullCopy(Blocks.OAK_LOG).mapColor((state: BlockState) => if state.getValue(RotatedPillarBlock.AXIS) == Direction.Axis.Y then MapColor.COLOR_MAGENTA else MapColor.TERRACOTTA_MAGENTA )))
  val wiederWood = makeWithItem(Lumomancy.locate("wieder_wood"), StrippablePillarBlock(strippedWiederWood, LumomancyLootTables.stripWieder, BlockProps.ofFullCopy(Blocks.OAK_WOOD).mapColor(MapColor.TERRACOTTA_MAGENTA)))

  val wiederPlanks = makeWithItem(Lumomancy.locate("wieder_planks"), Block(BlockProps.ofFullCopy(Blocks.OAK_PLANKS).mapColor(MapColor.COLOR_MAGENTA)))
  val wiederSlab = makeWithItem(Lumomancy.locate("wieder_slab"), SlabBlock(BlockProps.ofFullCopy(Blocks.OAK_SLAB).mapColor(MapColor.COLOR_MAGENTA)))

  val wiederButton = makeWithItem(Lumomancy.locate("wieder_button"), Blocks.woodenButton(wiederBlockSet))
  val wiederPressurePlate = makeWithItem(Lumomancy.locate("wieder_pressure_plate"), PressurePlateBlock(wiederBlockSet, BlockProps.ofFullCopy(Blocks.OAK_PRESSURE_PLATE).mapColor(wiederPlanks.defaultMapColor())))
  val wiederFence = makeWithItem(Lumomancy.locate("wieder_fence"), FenceBlock(BlockProps.ofFullCopy(Blocks.OAK_FENCE).mapColor(wiederPlanks.defaultMapColor())))
  val wiederFenceGate = makeWithItem(Lumomancy.locate("wieder_fence_gate"), FenceGateBlock(wiederWoodType, BlockProps.ofFullCopy(Blocks.OAK_FENCE_GATE).mapColor(wiederPlanks.defaultMapColor())))
  val wiederStairs = makeWithItem(Lumomancy.locate("wieder_stairs"), StairBlock(wiederPlanks.defaultBlockState(), BlockProps.ofFullCopy(Blocks.OAK_STAIRS).mapColor(wiederPlanks.defaultMapColor())))
  val wiederSign = makeNoItem(Lumomancy.locate("wieder_sign"), StandingSignBlock(wiederWoodType, BlockProps.ofFullCopy(Blocks.OAK_SIGN).mapColor(wiederPlanks.defaultMapColor())))
  val wiederWallSign = makeNoItem(Lumomancy.locate("wieder_wall_sign"), WallSignBlock(wiederWoodType, BlockProps.ofFullCopy(Blocks.OAK_WALL_SIGN).mapColor(wiederPlanks.defaultMapColor()).dropsLike(wiederSign)))

  val wiederSignItem = makeItem(Lumomancy.locate("wieder_sign"), SignItem(Item.Properties().stacksTo(16), wiederSign, wiederWallSign))

  val wiederHangingSign = makeNoItem(Lumomancy.locate("wieder_hanging_sign"), CeilingHangingSignBlock(wiederWoodType, BlockProps.ofFullCopy(Blocks.OAK_HANGING_SIGN).mapColor(strippedWiederWood.defaultMapColor())))
  val wiederWallHangingSign = makeNoItem(Lumomancy.locate("wieder_wall_hanging_sign"), WallHangingSignBlock(wiederWoodType, BlockProps.ofFullCopy(Blocks.OAK_WALL_HANGING_SIGN).mapColor(strippedWiederWood.defaultMapColor()).dropsLike(wiederHangingSign)))

  val wiederHangingSignItem = makeItem(Lumomancy.locate("wieder_hanging_sign"), HangingSignItem(wiederHangingSign, wiederWallHangingSign, Item.Properties().stacksTo(16)))

  val wiederDoor = makeWithItem(Lumomancy.locate("wieder_door"), DoorBlock(wiederBlockSet, BlockProps.ofFullCopy(Blocks.OAK_DOOR).mapColor(wiederPlanks.defaultMapColor())))
  val wiederTrapdoor = makeWithItem(Lumomancy.locate("wieder_trapdoor"), TrapDoorBlock(wiederBlockSet, BlockProps.ofFullCopy(Blocks.OAK_TRAPDOOR).mapColor(wiederPlanks.defaultMapColor())))


  // aftus wood
  val aftusBlockSet = BlockSetTypeBuilder.copyOf(BlockSetType.OAK).register(Lumomancy.locate("aftus"))
  val aftusWoodType = WoodTypeBuilder.copyOf(WoodType.OAK).register(Lumomancy.locate("aftus"), wiederBlockSet)

  val strippedAftusLog = makeWithItem(Lumomancy.locate("stripped_aftus_log"), Blocks
    .log(MapColor.COLOR_YELLOW, MapColor.COLOR_YELLOW))
  val strippedAftusWood = makeWithItem(Lumomancy.locate("stripped_aftus_wood"), RotatedPillarBlock(BlockProps
    .ofFullCopy(Blocks.OAK_WOOD).mapColor(MapColor.COLOR_YELLOW)))
  val aftusLog = makeWithItem(Lumomancy
    .locate("aftus_log"), StrippablePillarBlock(strippedAftusLog, LumomancyLootTables.stripAftus,
    BlockProps.ofFullCopy(Blocks.OAK_LOG)
              .mapColor((state: BlockState) => if state.getValue(RotatedPillarBlock.AXIS) == Direction.Axis
                                                                                                      .Y then MapColor
                .COLOR_YELLOW else MapColor.TERRACOTTA_YELLOW)))
  val aftusWood = makeWithItem(Lumomancy
    .locate("aftus_wood"), StrippablePillarBlock(strippedAftusWood, LumomancyLootTables.stripAftus, BlockProps
    .ofFullCopy(Blocks.OAK_WOOD).mapColor(MapColor.TERRACOTTA_YELLOW)))

  val aftusPlanks = makeWithItem(Lumomancy.locate("aftus_planks"), Block(BlockProps.ofFullCopy(Blocks.OAK_PLANKS)
                                                                                     .mapColor(MapColor.COLOR_YELLOW)))
  val aftusSlab = makeWithItem(Lumomancy.locate("aftus_slab"), SlabBlock(BlockProps.ofFullCopy(Blocks.OAK_SLAB)
                                                                                     .mapColor(MapColor.COLOR_YELLOW)))

  val aftusButton = makeWithItem(Lumomancy.locate("aftus_button"), Blocks.woodenButton(aftusBlockSet))
  val aftusPressurePlate = makeWithItem(Lumomancy
    .locate("aftus_pressure_plate"), PressurePlateBlock(aftusBlockSet, BlockProps
    .ofFullCopy(Blocks.OAK_PRESSURE_PLATE).mapColor(aftusPlanks.defaultMapColor())))
  val aftusFence = makeWithItem(Lumomancy.locate("aftus_fence"), FenceBlock(BlockProps.ofFullCopy(Blocks.OAK_FENCE)
                                                                                        .mapColor(aftusPlanks
                                                                                          .defaultMapColor())))
  val aftusFenceGate = makeWithItem(Lumomancy.locate("aftus_fence_gate"), FenceGateBlock(aftusWoodType, BlockProps
    .ofFullCopy(Blocks.OAK_FENCE_GATE).mapColor(aftusPlanks.defaultMapColor())))
  val aftusStairs = makeWithItem(Lumomancy.locate("aftus_stairs"), StairBlock(aftusPlanks
    .defaultBlockState(), BlockProps.ofFullCopy(Blocks.OAK_STAIRS).mapColor(aftusPlanks.defaultMapColor())))
  val aftusSign = makeNoItem(Lumomancy.locate("aftus_sign"), StandingSignBlock(aftusWoodType, BlockProps
    .ofFullCopy(Blocks.OAK_SIGN).mapColor(aftusPlanks.defaultMapColor())))
  val aftusWallSign = makeNoItem(Lumomancy.locate("aftus_wall_sign"), WallSignBlock(aftusWoodType, BlockProps
    .ofFullCopy(Blocks.OAK_WALL_SIGN).mapColor(aftusPlanks.defaultMapColor()).dropsLike(aftusSign)))

  val aftusSignItem = makeItem(Lumomancy.locate("aftus_sign"), SignItem(Item.Properties()
                                                                              .stacksTo(16), aftusSign, aftusWallSign))

  val aftusHangingSign = makeNoItem(Lumomancy
    .locate("aftus_hanging_sign"), CeilingHangingSignBlock(aftusWoodType, BlockProps
    .ofFullCopy(Blocks.OAK_HANGING_SIGN).mapColor(strippedAftusWood.defaultMapColor())))
  val aftusWallHangingSign = makeNoItem(Lumomancy
    .locate("aftus_wall_hanging_sign"), WallHangingSignBlock(aftusWoodType, BlockProps
    .ofFullCopy(Blocks.OAK_WALL_HANGING_SIGN).mapColor(strippedAftusWood.defaultMapColor())
    .dropsLike(aftusHangingSign)))

  val aftusHangingSignItem = makeItem(Lumomancy
    .locate("aftus_hanging_sign"), HangingSignItem(aftusHangingSign, aftusWallHangingSign, Item.Properties()
                                                                                                  .stacksTo(16)))

  val aftusDoor = makeWithItem(Lumomancy.locate("aftus_door"), DoorBlock(aftusBlockSet, BlockProps
    .ofFullCopy(Blocks.OAK_DOOR).mapColor(aftusPlanks.defaultMapColor())))
  val aftusTrapdoor = makeWithItem(Lumomancy.locate("aftus_trapdoor"), TrapDoorBlock(aftusBlockSet, BlockProps
    .ofFullCopy(Blocks.OAK_TRAPDOOR).mapColor(aftusPlanks.defaultMapColor())))
  
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
    
