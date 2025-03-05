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

package gay.menkissing.lumomancy.content.item

import gay.menkissing.lumomancy.item.ItemBackedInventory
import gay.menkissing.lumomancy.registries.{LumomancyScreens, LumomancyTags}
import gay.menkissing.lumomancy.screen.ToolContainerMenu
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory
import net.minecraft.core.component.DataComponents
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.SlotAccess
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.{InteractionHand, InteractionResultHolder, SimpleContainer}
import net.minecraft.world.entity.player.{Inventory, Player}
import net.minecraft.world.inventory.{AbstractContainerMenu, ClickAction, Slot}
import net.minecraft.world.item.component.ItemContainerContents
import net.minecraft.world.item.{Item, ItemStack, ItemUtils}
import net.minecraft.world.level.Level

import java.util.stream.IntStream

class ToolContainer(props: Item.Properties) extends Item(props):
  override def use(level: Level, player: Player, interactionHand: InteractionHand): InteractionResultHolder[ItemStack] =
    if !level.isClientSide then
      val stack = player.getItemInHand(interactionHand)
      val provider = new ExtendedScreenHandlerFactory[Boolean] {
        override def createMenu(i: Int, inventory: Inventory, player: Player): AbstractContainerMenu =
          LumomancyScreens.toolContainer.create(i, inventory, interactionHand == InteractionHand.MAIN_HAND)

        override def getDisplayName: Component = stack.getHoverName

        override def getScreenOpeningData(splayer: ServerPlayer): Boolean =
          interactionHand == InteractionHand.MAIN_HAND
      }
      player.openMenu(provider)
    InteractionResultHolder.sidedSuccess(player.getItemInHand(interactionHand), level.isClientSide)

  override def onDestroyed(itemEntity: ItemEntity): Unit =
    ItemUtils.onContainerDestroyed(itemEntity, itemEntity.getItem.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY).nonEmptyItems())
    itemEntity.getItem.remove(DataComponents.CONTAINER)

  override def overrideOtherStackedOnMe(thisStack: ItemStack, thatStack: ItemStack, slot: Slot, clickAction: ClickAction, player: Player, slotAccess: SlotAccess): Boolean =
    if clickAction == ClickAction.SECONDARY && slot.allowModification(player) && !thatStack.isEmpty && thatStack.is(LumomancyTags.validToolTag) then
      val container = ToolContainer.getRawInventory(thisStack)
      if container.canAddItem(thatStack) then
        val res = container.addItem(thatStack)
        slotAccess.set(res)
        return true
    false

  override def overrideStackedOnOther(thisStack: ItemStack, slot: Slot, clickAction: ClickAction, player: Player): Boolean = {
    if clickAction != ClickAction.SECONDARY then
      false
    else
      val container = ToolContainer.getRawInventory(thisStack)
      val thatStack = slot.getItem
      if !thatStack.isEmpty && thatStack.is(LumomancyTags.validToolTag) then
          if container.canAddItem(thatStack) then
            val res = container.addItem(thatStack)
            slot.set(res)
            true
          else
            false
      else
        false
  }

object ToolContainer:
  def getRawInventory(stack: ItemStack): SimpleContainer =
    new ItemBackedInventory(stack, ToolContainerMenu.containerSize)
