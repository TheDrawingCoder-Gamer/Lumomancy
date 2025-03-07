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

package gay.menkissing.lumomancy.api.info

import net.minecraft.network.chat.Component

import scala.collection.mutable

/**
 * Add this to a [[net.minecraft.world.level.block.entity.BlockEntity]] to get hover information when wearing
 */
trait LumoTooltipInfo:
  def appendLumoTooltip(tooltip: mutable.ListBuffer[Component], isPlayerSneaking: Boolean): Boolean = false

