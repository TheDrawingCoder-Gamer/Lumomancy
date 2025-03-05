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

package gay.menkissing.lumomancy

import gay.menkissing.lumomancy.content.LumomancyItems
import gay.menkissing.lumomancy.registries.{LumomancyDataComponents, LumomancyRegistries, LumomancyScreens, LumomancyTags, LumonColors}
import net.fabricmc.api.ModInitializer
import net.minecraft.resources.ResourceLocation

object Lumomancy extends ModInitializer:
  val MOD_ID: String = "lumomancy"

  def locate(id: String): ResourceLocation = ResourceLocation.fromNamespaceAndPath(MOD_ID, id)
  
  override def onInitialize(): Unit =
    LumomancyItems.init()
    LumomancyRegistries.init()
    LumomancyDataComponents.init()
    LumomancyTags.init()
    LumomancyScreens.init()
    LumonColors.init()