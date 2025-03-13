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
import gay.menkissing.lumomancy.content.block.{LumoBlockFamilies, StasisCooler}
import gay.menkissing.lumomancy.content.block.entity.StasisCoolerBlockEntity
import gay.menkissing.lumomancy.mixin.AxeItemAccessor
import net.fabricmc.fabric.api.`object`.builder.v1.block.`type`.{BlockSetTypeBuilder, WoodTypeBuilder}
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.{BlockItem, HangingSignItem, Item, SignItem}
import net.minecraft.world.level.block.entity.{BlockEntity, BlockEntityType}
import net.minecraft.world.level.block.{Block, Blocks, CeilingHangingSignBlock, FenceBlock, FenceGateBlock, PressurePlateBlock, RotatedPillarBlock, SignBlock, SlabBlock, SoundType, StairBlock, StandingSignBlock, WallHangingSignBlock, WallSignBlock}
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.block.state.properties.{BlockSetType, NoteBlockInstrument, WoodType}
import net.minecraft.world.level.material.{MapColor, PushReaction}

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


  val stillwoodLog: Block = makeWithItem(Lumomancy.locate("stillwood_log"), Blocks.log(MapColor.COLOR_CYAN, MapColor.WARPED_STEM))
  // wood is still a rotated pillar block
  val stillwoodWood: Block = makeWithItem(Lumomancy.locate("stillwood_wood"), RotatedPillarBlock(BlockBehaviour.Properties.of().sound(SoundType.WOOD).instrument(NoteBlockInstrument.BASS).strength(2.0f).ignitedByLava()))
  val strippedStillwoodLog: Block = makeWithItem(Lumomancy.locate("stripped_stillwood_log"), Blocks.log(MapColor.COLOR_CYAN, MapColor.COLOR_CYAN))
  val strippedStillwoodWood: Block = makeWithItem(Lumomancy.locate("stripped_stillwood_wood"), RotatedPillarBlock(BlockBehaviour.Properties.of().sound(SoundType.WOOD).instrument(NoteBlockInstrument.BASS).strength(2.0f).ignitedByLava()))

  val stillwoodPlanks: Block = makeWithItem(Lumomancy.locate("stillwood_planks"), Block(BlockBehaviour.Properties.of().sound(SoundType.WOOD).instrument(NoteBlockInstrument.BASS).strength(2.0f, 3.0f).ignitedByLava()))
  val stillwoodSlab: Block = makeWithItem(Lumomancy.locate("stillwood_slab"), SlabBlock(BlockBehaviour.Properties.of().sound(SoundType.WOOD).instrument(NoteBlockInstrument.BASS).strength(2.0f, 3.0f).ignitedByLava()))
  // todo: door, hanging sign, trapdoor
  // these all require unique textures
  val stillwoodButton: Block = makeWithItem(Lumomancy.locate("stillwood_button"), Blocks.woodenButton(stillwoodBlockSet))
  val stillwoodPressurePlate: Block = makeWithItem(Lumomancy.locate("stillwood_pressure_plate"), PressurePlateBlock(stillwoodBlockSet, BlockBehaviour.Properties.of().sound(SoundType.WOOD).forceSolidOn().mapColor(stillwoodPlanks.defaultMapColor()).noCollission().strength(0.5f).pushReaction(PushReaction.DESTROY)))
  val stillwoodFence: Block = makeWithItem(Lumomancy.locate("stillwood_fence"), FenceBlock(BlockBehaviour.Properties.of().mapColor(stillwoodPlanks.defaultMapColor()).forceSolidOn().instrument(NoteBlockInstrument.BASS).strength(2.0f, 3.0f).sound(SoundType.WOOD).ignitedByLava()))
  val stillwoodFenceGate: Block = makeWithItem(Lumomancy.locate("stillwood_fence_gate"), FenceGateBlock(stillwoodWoodSet, BlockBehaviour.Properties.of().mapColor(stillwoodPlanks.defaultMapColor()).forceSolidOn().instrument(NoteBlockInstrument.BASS).strength(2.0f, 3.0f).ignitedByLava()))
  val stillwoodStairs: Block = makeWithItem(Lumomancy.locate("stillwood_stairs"), StairBlock(stillwoodPlanks.defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(stillwoodPlanks)))
  val stillwoodSign: Block = makeNoItem(Lumomancy.locate("stillwood_sign"), StandingSignBlock(stillwoodWoodSet, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_SIGN).mapColor(MapColor.COLOR_CYAN)))
  val stillwoodWallSign: Block = makeNoItem(Lumomancy.locate("stillwood_wall_sign"), WallSignBlock(stillwoodWoodSet, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_WALL_SIGN).mapColor(MapColor.COLOR_CYAN).dropsLike(stillwoodSign)))

  val stillwoodSignItem: Item = makeItem(Lumomancy.locate("stillwood_sign"), SignItem(Item.Properties(), stillwoodSign, stillwoodWallSign))

  val stillwoodHangingSign: Block = makeNoItem(Lumomancy.locate("stillwood_hanging_sign"), CeilingHangingSignBlock(stillwoodWoodSet, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_HANGING_SIGN).mapColor(MapColor.COLOR_CYAN)))
  val stillwoodWallHangingSign: Block = makeNoItem(Lumomancy.locate("stillwood_wall_hanging_sign"), WallHangingSignBlock(stillwoodWoodSet, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_WALL_HANGING_SIGN).mapColor(MapColor.COLOR_CYAN).dropsLike(stillwoodHangingSign)))

  val stillwoodHangingSignItem: Item = makeItem(Lumomancy.locate("stillwood_hanging_sign"), HangingSignItem(stillwoodHangingSign, stillwoodWallHangingSign, Item.Properties()))


  def init(): Unit =
    StasisCoolerBlockEntity.registerStorages()
    LumoBlockFamilies.register()
    ItemGroupEvents.modifyEntriesEvent(LumomancyItems.itemGroupKey).register { group =>
      blockItems.foreach(group.accept)
    }
    val updatedMap = java.util.HashMap(AxeItemAccessor.getStrippables)

    updatedMap.put(stillwoodLog, strippedStillwoodLog)
    updatedMap.put(stillwoodWood, strippedStillwoodWood)

    AxeItemAccessor.setStrippables(ImmutableMap.copyOf(updatedMap))

    BlockEntityType.SIGN.addSupportedBlock(stillwoodSign)
    BlockEntityType.SIGN.addSupportedBlock(stillwoodWallSign)
    BlockEntityType.HANGING_SIGN.addSupportedBlock(stillwoodHangingSign)
    BlockEntityType.HANGING_SIGN.addSupportedBlock(stillwoodWallHangingSign)
    
