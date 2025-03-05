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
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.DyeColor

object LumonColors:
  def make(id: ResourceLocation, color: LumonColor): LumonColor =
    Registry.register(LumomancyRegistries.lumonColors, id, color)

  // primary colors
  val red: LumonColor = make(Lumomancy.locate("red"), LumonColor.fromInts(Some(DyeColor.RED), 0xffff0000, 0xffff0000))
  val green: LumonColor = make(Lumomancy.locate("green"), LumonColor.fromInts(Some(DyeColor.GREEN), 0xff00ff00, 0xff00ff00))
  val blue: LumonColor = make(Lumomancy.locate("blue"), LumonColor.fromInts(Some(DyeColor.BLUE), 0xff0000ff, 0xff0000ff))
  val white: LumonColor = make(Lumomancy.locate("white"), LumonColor.fromInts(Some(DyeColor.WHITE), 0xffffffff, 0xffffffff))
  val black: LumonColor = make(Lumomancy.locate("black"), LumonColor.fromInts(Some(DyeColor.BLACK), 0xff000000, 0xff808080))

  // blue-red mixes
  val purple: LumonColor = make(Lumomancy.locate("purple"), LumonColor.fromInts(Some(DyeColor.PURPLE), 0xff8000ff, 0xff8000ff))
  val magenta: LumonColor = make(Lumomancy.locate("magenta"), LumonColor.fromInts(Some(DyeColor.MAGENTA), 0xffff00ff, 0xffff00ff))
  // This is a bit fudgey with the colors but spectrum fudges its color in the same way
  val rose: LumonColor = make(Lumomancy.locate("rose"), LumonColor.fromInts(Some(DyeColor.PINK), 0xffff007f, 0xffff007f))

  // red-green mixes
  val orange: LumonColor = make(Lumomancy.locate("orange"), LumonColor.fromInts(Some(DyeColor.ORANGE), 0xffff8000, 0xffff8000))
  val yellow: LumonColor = make(Lumomancy.locate("yellow"), LumonColor.fromInts(Some(DyeColor.YELLOW), 0xffffff00, 0xffffff00))
  val lime: LumonColor = make(Lumomancy.locate("lime"), LumonColor.fromInts(Some(DyeColor.LIME), 0xff7fff00, 0xff7fff00))

  // blue-green mixes
  // this is also fudgey
  val azure: LumonColor = make(Lumomancy.locate("azure"), LumonColor.fromInts(Some(DyeColor.LIGHT_BLUE), 0xff0080ff, 0xff0080ff))
  val cyan: LumonColor = make(Lumomancy.locate("cyan"), LumonColor.fromInts(Some(DyeColor.CYAN),0xff00ffff, 0xff00ffff))
  // No dye color associated with it
  val seafoam: LumonColor = make(Lumomancy.locate("seafoam"), LumonColor.fromInts(None, 0xff00ff7f, 0xff00ff7f))

  // black-white mixes
  val light_gray: LumonColor = make(Lumomancy.locate("light_gray"), LumonColor.fromInts(Some(DyeColor.LIGHT_GRAY), 0xff9d9d9d, 0xff9d9d9d))
  val gray: LumonColor = make(Lumomancy.locate("gray"), LumonColor.fromInts(Some(DyeColor.GRAY), 0xff474747, 0xff474747))

  // extras
  val brown: LumonColor = make(Lumomancy.locate("brown"), LumonColor.fromInts(Some(DyeColor.BROWN), 0xff835432, 0xff835432))

  def init(): Unit = ()

