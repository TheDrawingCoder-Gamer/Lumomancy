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

package gay.menkissing.lumomancy.registries

import gay.menkissing.lumomancy.Lumomancy
import gay.menkissing.lumomancy.api.energy.LumonColor
import gay.menkissing.lumomancy.content.item.StasisBottle.StasisBottleContents
import gay.menkissing.lumomancy.content.item.StasisTube.StasisTubeContents
import gay.menkissing.lumomancy.util.codec.LumoCodecs
import net.minecraft.core.Registry
import net.minecraft.core.component.DataComponentType
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.item.ItemStack

object LumomancyDataComponents:
  val lumonColorComponent: DataComponentType[LumonColor] = Registry.register(
    BuiltInRegistries.DATA_COMPONENT_TYPE,
    Lumomancy.locate("lumon_color"),
    DataComponentType.builder[LumonColor]().persistent(LumonColor.CODEC).build()
  )
  
  val itemBackedInventoryComponent: DataComponentType[List[ItemStack]] = Registry.register(
    BuiltInRegistries.DATA_COMPONENT_TYPE,
    Lumomancy.locate("item_backed_inventory"),
    DataComponentType.builder[List[ItemStack]]().persistent(LumoCodecs.scalaListCodec[ItemStack](ItemStack.CODEC)).build()
  )



  val stasisTubeContents: DataComponentType[StasisTubeContents] = Registry.register(
    BuiltInRegistries.DATA_COMPONENT_TYPE,
    Lumomancy.locate("stasis_tube_contents"),
    DataComponentType.builder[StasisTubeContents]().persistent(StasisTubeContents.CODEC).networkSynchronized(StasisTubeContents.STREAM_CODEC).build()
  )
  
  val stasisBottleContents: DataComponentType[StasisBottleContents] = Registry.register(
    BuiltInRegistries.DATA_COMPONENT_TYPE,
    Lumomancy.locate("stasis_bottle_contents"),
    DataComponentType.builder[StasisBottleContents]().persistent(StasisBottleContents.CODEC).networkSynchronized(StasisBottleContents.STREAM_CODEC).build()
  )
  
  def init(): Unit = ()