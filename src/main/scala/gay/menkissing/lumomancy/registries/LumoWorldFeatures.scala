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
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature

object LumoWorldFeatures:

  val aftusTree: ResourceKey[ConfiguredFeature[?, ?]] = ResourceKey.create(Registries.CONFIGURED_FEATURE, Lumomancy.locate("aftus_tree"))
  val stillwoodTree: ResourceKey[ConfiguredFeature[?, ?]] = ResourceKey.create(Registries.CONFIGURED_FEATURE, Lumomancy.locate("stillwood_tree"))
  val wiederTree: ResourceKey[ConfiguredFeature[?, ?]] = ResourceKey.create(Registries.CONFIGURED_FEATURE, Lumomancy.locate("wieder_tree"))
