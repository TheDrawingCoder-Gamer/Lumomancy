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

package gay.menkissing.lumomancy.item

import gay.menkissing.lumomancy.registries.LumomancyDataComponents
import net.minecraft.core.component.DataComponents
import net.minecraft.world.SimpleContainer
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.component.ItemContainerContents

import scala.jdk.CollectionConverters.*
import scala.jdk.StreamConverters.*

class ItemBackedInventory(val stack: ItemStack, expectedSize: Int) extends SimpleContainer(expectedSize):
  locally:
    val contents = stack.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY)
    var i = 0
    contents.stream().forEachOrdered { item =>
      setItem(i, item)
      i = i + 1
    }

  override def stillValid(player: Player): Boolean =
    !stack.isEmpty

  override def setChanged(): Unit =
    super.setChanged()
    stack.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(getItems))
      
  