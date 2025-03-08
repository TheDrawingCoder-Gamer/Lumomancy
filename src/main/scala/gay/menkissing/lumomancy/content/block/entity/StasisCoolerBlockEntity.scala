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

package gay.menkissing.lumomancy.content.block.entity

import gay.menkissing.lumomancy.Lumomancy
import gay.menkissing.lumomancy.content.{LumomancyBlocks, LumomancyItems}
import gay.menkissing.lumomancy.content.block.StasisCooler
import net.minecraft.core.{BlockPos, HolderLookup, NonNullList}
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.entity.player.Player
import net.minecraft.world.{Container, ContainerHelper}
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState

import java.util.Objects

class StasisCoolerBlockEntity(pos: BlockPos, state: BlockState) extends BlockEntity(LumomancyBlocks.stasisCoolerBlockEntity, pos, state):
  private val items = NonNullList.withSize(6, ItemStack.EMPTY)
  private var lastInteractedSlot: Int = -1

  override def loadAdditional(tag: CompoundTag, registries: HolderLookup.Provider): Unit = {
    super.loadAdditional(tag, registries)
    ContainerHelper.loadAllItems(tag, items, registries)
    this.lastInteractedSlot = tag.getInt("last_interacted_slot")
  }

  override def saveAdditional(tag: CompoundTag, registries: HolderLookup.Provider): Unit = {
    super.saveAdditional(tag, registries)
    ContainerHelper.saveAllItems(tag, items, registries)
    tag.putInt("last_interacted_slot", lastInteractedSlot)
  }

  def clearContent(): Unit =
    items.clear()

  def isEmpty: Boolean =
    items.stream().allMatch(_.isEmpty)

  private def updateState(slot: Int): Unit =
    if slot >= 0 && slot < 6 then
      this.lastInteractedSlot = slot
      var blockState = this.getBlockState

      StasisCooler.COOLER_SLOT_OCCUPIED_PROPS.zipWithIndex.foreach { (prop, i) =>
        val kind =
          val item = items.get(i)
          if item.isEmpty then
            StasisCooler.CoolerSlotOccupiedBy.Empty
          else if item.is(LumomancyItems.stasisBottle) then
            StasisCooler.CoolerSlotOccupiedBy.Bottle
          else
            StasisCooler.CoolerSlotOccupiedBy.Tube
        blockState = blockState.setValue(prop, kind)
      }
    else
      Lumomancy.LOGGER.error("Expected slot to be 0-5 got {}", slot)

  
  // Implemented SOME fields from Container, but didn't implement container
  // so i can have a separate fabric storage transfer api impl
  def getContainerSize(): Int = 6

  def getItem(slot: Int): ItemStack = this.items.get(slot)

  def removeItem(slot: Int, amount: Int): ItemStack =
    val stack = Objects.requireNonNullElse(this.items.get(slot), ItemStack.EMPTY)
    this.items.set(slot, ItemStack.EMPTY)
    if !stack.isEmpty then
      this.updateState(slot)

    stack

  def removeItemNoUpdate(slot: Int): ItemStack = this.removeItem(slot, 1)

  def setItem(slot: Int, stack: ItemStack): Unit =
    if stack.is(LumomancyItems.stasisTube) || stack.is(LumomancyItems.stasisBottle) then
      this.items.set(slot, stack)
      this.updateState(slot)
    else if stack.isEmpty then
      this.removeItem(slot, 1)

  def stillValid(player: Player): Boolean = Container.stillValidBlockEntity(this, player)

  def getMaxStackSize(stack: ItemStack): Int = 1
