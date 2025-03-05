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
package gay.menkissing.lumomancy.api.energy

import com.mojang.serialization.Codec
import gay.menkissing.lumomancy.registries.LumomancyRegistries
import gay.menkissing.lumomancy.util.ColorUtil
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.TagKey
import net.minecraft.world.item.DyeColor
import org.joml.Vector3f

import scala.jdk.OptionConverters.*

case class LumonColor(
                     dyeColor: Option[DyeColor],
                     colorInt: Int,
                     colorVec: Vector3f,
                     textColor: Int,
                     textColorVec: Vector3f
                     ):
  def id: ResourceLocation =
    LumomancyRegistries.lumonColors.getKey(this)

  def isIn(tagKey: TagKey[LumonColor]): Boolean =
    LumomancyRegistries.lumonColors.wrapAsHolder(this).is(tagKey)

object LumonColor:
  def ofID(loc: ResourceLocation): Option[LumonColor] =
    LumomancyRegistries.lumonColors.getOptional(loc).toScala

  val CODEC: Codec[LumonColor] =
    ResourceLocation.CODEC.xmap(it => LumonColor.ofID(it).get, _.id)

  def fromInts(dyeColor: Option[DyeColor], color: Int, textColor: Int): LumonColor =
    LumonColor(dyeColor, color, ColorUtil.colorIntToVec(color), textColor, ColorUtil.colorIntToVec(textColor))