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
import net.fabricmc.fabric.api.event.registry.{FabricRegistryBuilder, RegistryAttribute}
import net.minecraft.core.Registry
import net.minecraft.resources.{ResourceKey, ResourceLocation}

object LumomancyRegistries:
  val lumonColorsID: ResourceLocation = Lumomancy.locate("lumon_color")
  val lumonColorsKey: ResourceKey[Registry[LumonColor]] = ResourceKey.createRegistryKey(lumonColorsID)
  val lumonColors: Registry[LumonColor] = FabricRegistryBuilder.createSimple(lumonColorsKey)
                                                               .attribute(RegistryAttribute.SYNCED).buildAndRegister()
  
  
  def init(): Unit = ()
