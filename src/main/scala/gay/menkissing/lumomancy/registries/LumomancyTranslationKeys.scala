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

import gay.menkissing.lumomancy.util.LumoNumberFormatting
import net.minecraft.network.chat.Component

object LumomancyTranslationKeys:
  object stasisBottle:
    object tooltip:
      val usagePickup: Component = Component.translatable("item.lumomancy.stasis_bottle.tooltip.usage_pickup")
      val usagePlace: Component = Component.translatable("item.lumomancy.stasis_bottle.tooltip.usage_place")
      // These are different because its possible the word for "Empty of fluid" and "Empty of items" is different
      // in some languages
      val empty: Component = Component.translatable("item.lumomancy.stasis_bottle.tooltip.empty")
      def countMB(amount: Long, max: Long): Component = 
        Component.translatable("item.lumomancy.stasis_bottle.tooltip.count_mb", LumoNumberFormatting.formatMB(amount), LumoNumberFormatting.formatFluidMax(max))
  
  object stasisTube:
    object tooltip:
      val empty: Component = Component.translatable("item.lumomancy.stasis_tube.tooltip.empty")
      def count(amount: Long, max: Long, stacksSize: String): Component =
        Component.translatable("item.lumomancy.stasis_tube.tooltip.count", amount, max, stacksSize)
