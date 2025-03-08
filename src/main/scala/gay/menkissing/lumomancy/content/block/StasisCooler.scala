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

package gay.menkissing.lumomancy.content.block

import com.mojang.serialization.MapCodec
import gay.menkissing.lumomancy.content.LumomancyItems
import gay.menkissing.lumomancy.content.block.entity.StasisCoolerBlockEntity
import gay.menkissing.lumomancy.registries.LumomancyScreens
import net.minecraft.core.{BlockPos, Direction}
import net.minecraft.util.StringRepresentable
import net.minecraft.world.{InteractionHand, InteractionResult, ItemInteractionResult}
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.{BaseEntityBlock, Block, HorizontalDirectionalBlock, RenderShape}
import net.minecraft.world.level.block.state.{BlockBehaviour, BlockState, StateDefinition}
import net.minecraft.world.level.block.state.properties.EnumProperty
import net.minecraft.world.level.gameevent.GameEvent
import net.minecraft.world.phys.{BlockHitResult, Vec2}

class StasisCooler(props: BlockBehaviour.Properties) extends BaseEntityBlock(props):
  locally:
    var blockState = this.stateDefinition.any().setValue(HorizontalDirectionalBlock.FACING, Direction.NORTH)
    StasisCooler.COOLER_SLOT_OCCUPIED_PROPS.foreach { prop =>
      blockState = blockState.setValue(prop, StasisCooler.CoolerSlotOccupiedBy.Empty)
    }
    this.registerDefaultState(blockState)

  def getHitSlot(hitResult: BlockHitResult, state: BlockState): Option[Int] =
    StasisCooler.getRelativeHitCoordsForFace(hitResult, state.getValue(HorizontalDirectionalBlock.FACING)).map { vec2 =>
      val i = if vec2.y > 0.5f then 0 else 1
      val j = StasisCooler.getHorzSection(vec2.x)
      j + i * 3
    }

  override def createBlockStateDefinition(builder: StateDefinition.Builder[Block, BlockState]): Unit = {
    super
      .createBlockStateDefinition(builder)
    builder.add(HorizontalDirectionalBlock.FACING)
    StasisCooler.COOLER_SLOT_OCCUPIED_PROPS.foreach(it => builder.add(it))
  }

  override def newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity = StasisCoolerBlockEntity(pos, state)

  override def codec(): MapCodec[_ <: BaseEntityBlock] = BlockBehaviour.simpleCodec(StasisCooler.apply)

  override def useItemOn(stack: ItemStack, state: BlockState, level: Level, pos: BlockPos, player: Player, hand: InteractionHand, hitResult: BlockHitResult): ItemInteractionResult = {
    level.getBlockEntity(pos) match
      case coolerBlockEntity: StasisCoolerBlockEntity =>
        if !stack.is(LumomancyItems.stasisTube) && !stack.is(LumomancyItems.stasisBottle) then
          ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION
        else
          this.getHitSlot(hitResult, state) match
            case None => 
              ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION
            case Some(slot) =>
              if !state.getValue(StasisCooler.COOLER_SLOT_OCCUPIED_PROPS(slot)).isEmpty then
                ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION
              else
                StasisCooler.insertContainer(level, pos, player, coolerBlockEntity, stack, slot)
                ItemInteractionResult.sidedSuccess(level.isClientSide)
      case _ =>
        ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION
  }

  override def useWithoutItem(state: BlockState, level: Level, pos: BlockPos, player: Player, hitResult: BlockHitResult): InteractionResult = {
    level.getBlockEntity(pos) match
      case coolerBlockEntity: StasisCoolerBlockEntity =>
        this.getHitSlot(hitResult, state) match
          case None =>
            InteractionResult.PASS
          case Some(slot) =>
            if state.getValue(StasisCooler.COOLER_SLOT_OCCUPIED_PROPS(slot)).isEmpty then
              InteractionResult.CONSUME
            else
              StasisCooler.removeContainer(level, pos, player, coolerBlockEntity, slot)
              InteractionResult.sidedSuccess(level.isClientSide)
      case _ =>
        InteractionResult.PASS
  }

  override def getRenderShape(state: BlockState): RenderShape = RenderShape.MODEL

object StasisCooler:
  enum CoolerSlotOccupiedBy extends Enum[CoolerSlotOccupiedBy], StringRepresentable:
    case Empty, Bottle, Tube
    
    def isEmpty: Boolean = this == Empty

    override def getSerializedName: String = {
      this match
        case CoolerSlotOccupiedBy.Empty => "empty"
        case CoolerSlotOccupiedBy.Bottle => "bottle"
        case CoolerSlotOccupiedBy.Tube => "tube"
    }

  val COOLER_SLOT_0_OCCUPIED_BY: EnumProperty[CoolerSlotOccupiedBy] = EnumProperty.create("cooler_slot_0_occupied_by", classOf[CoolerSlotOccupiedBy])
  val COOLER_SLOT_1_OCCUPIED_BY: EnumProperty[CoolerSlotOccupiedBy] = EnumProperty.create("cooler_slot_1_occupied_by", classOf[CoolerSlotOccupiedBy])
  val COOLER_SLOT_2_OCCUPIED_BY: EnumProperty[CoolerSlotOccupiedBy] = EnumProperty.create("cooler_slot_2_occupied_by", classOf[CoolerSlotOccupiedBy])
  val COOLER_SLOT_3_OCCUPIED_BY: EnumProperty[CoolerSlotOccupiedBy] = EnumProperty.create("cooler_slot_3_occupied_by", classOf[CoolerSlotOccupiedBy])
  val COOLER_SLOT_4_OCCUPIED_BY: EnumProperty[CoolerSlotOccupiedBy] = EnumProperty.create("cooler_slot_4_occupied_by", classOf[CoolerSlotOccupiedBy])
  val COOLER_SLOT_5_OCCUPIED_BY: EnumProperty[CoolerSlotOccupiedBy] = EnumProperty.create("cooler_slot_5_occupied_by", classOf[CoolerSlotOccupiedBy])

  val COOLER_SLOT_OCCUPIED_PROPS: List[EnumProperty[CoolerSlotOccupiedBy]] = List(
    COOLER_SLOT_0_OCCUPIED_BY,
    COOLER_SLOT_1_OCCUPIED_BY,
    COOLER_SLOT_2_OCCUPIED_BY,
    COOLER_SLOT_3_OCCUPIED_BY,
    COOLER_SLOT_4_OCCUPIED_BY,
    COOLER_SLOT_5_OCCUPIED_BY
  )

  def insertContainer(level: Level, pos: BlockPos, player: Player, blockEntity: StasisCoolerBlockEntity, stack: ItemStack, slot: Int): Unit =
    if !level.isClientSide then
      blockEntity.setItem(slot, stack.consumeAndReturn(1, player))
      
  def removeContainer(level: Level, pos: BlockPos, player: Player, blockEntity: StasisCoolerBlockEntity, slot: Int): Unit =
    if !level.isClientSide then
      val stack = blockEntity.removeItem(slot, 1)
      if !player.getInventory.add(stack) then
        player.drop(stack, false)
      
      level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos)
      
  
  
  def getHorzSection(x: Float): Int =
    val g = 0.375f
    val h = 0.6875f
    if x < g then
      0
    else if x < h then
      1
    else
      2

  def getRelativeHitCoordsForFace(hitResult: BlockHitResult, face: Direction): Option[Vec2] =
    val dir = hitResult.getDirection
    if face != dir then
      None
    else
      val pos = hitResult.getBlockPos.relative(dir)
      val vec3 = hitResult.getLocation.subtract(pos.getX, pos.getY, pos.getZ)
      val d = vec3.x()
      val e = vec3.y()
      val f = vec3.z()

      dir match
        case Direction.NORTH => Some(Vec2((1.0 - d).toFloat, e.toFloat))
        case Direction.SOUTH => Some(Vec2(d.toFloat, e.toFloat))
        case Direction.WEST => Some(Vec2(f.toFloat, e.toFloat))
        case Direction.EAST => Some(Vec2((1.0 - f).toFloat, e.toFloat))
        case _ => None
