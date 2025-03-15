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
import net.minecraft.core.registries.{BuiltInRegistries, Registries}
import net.minecraft.tags.TagKey
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block

object LumomancyTags:
  object item:
    val validToolTag: TagKey[Item] = TagKey.create(BuiltInRegistries.ITEM.key(), Lumomancy.locate("valid_tools"))
    val stillwoodLogsTag: TagKey[Item] = TagKey.create(BuiltInRegistries.ITEM.key(), Lumomancy.locate("stillwood_logs"))
    val wiederLogsTag: TagKey[Item] = TagKey.create(Registries.ITEM, Lumomancy.locate("wieder_logs"))
    val aftusLogsTag: TagKey[Item] = TagKey.create(Registries.ITEM, Lumomancy.locate("aftus_logs"))

  object block:
    val stillwoodLogsTag: TagKey[Block] = TagKey.create(BuiltInRegistries.BLOCK.key(), Lumomancy.locate("stillwood_logs"))
    val wiederLogsTag: TagKey[Block] = TagKey.create(Registries.BLOCK, Lumomancy.locate("wieder_logs"))
    val aftusLogsTag: TagKey[Block] = TagKey.create(Registries.BLOCK, Lumomancy.locate("aftus_logs"))

  def init(): Unit =
    val _ = block
    val _ = item