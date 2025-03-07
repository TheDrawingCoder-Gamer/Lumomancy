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

package gay.menkissing.lumomancy.util

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants

object LumoNumberFormatting:
  def formatMB(amount: Long): String =
    val mb = amount.toFloat / 81f
    if mb < 1000 then
      mb.toLong.toString
    else if mb < 1000000 then
      String.format("%1$.2fK", mb / 1000f)
    else
      String.format("%1$.2fM", mb / 1000000f)

  def formatFluidMax(amount: Long): String =
    val buckets = math.round(amount.toFloat / FluidConstants.BUCKET)
    buckets.toString
