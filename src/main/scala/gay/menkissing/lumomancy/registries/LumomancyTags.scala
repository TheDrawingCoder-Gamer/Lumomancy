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
import gay.menkissing.lumomancy.util.registry.InfoCollector
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags
import net.minecraft.core.registries.{BuiltInRegistries, Registries}
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.{BlockTags, ItemTags, TagKey}
import net.minecraft.world.item.{Item, Items}
import net.minecraft.world.level.block.Block

object LumomancyTags:
  object item:
    val validToolTag: TagKey[Item] =
      InfoCollector.instance.tag(Registries.ITEM, "valid_tools")
                   .subtag(ConventionalItemTags.TOOLS)
                   .subtag(ItemTags.HEAD_ARMOR)
                   .subtag(ItemTags.CHEST_ARMOR)
                   .subtag(ItemTags.LEG_ARMOR)
                   .subtag(ItemTags.FOOT_ARMOR)
                   .add(Items.SPYGLASS)
                   .subtag(ItemTags.COMPASSES)
                   .subtag(TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "wrenches")))
                   .register()

    val stillwoodLogsTag: TagKey[Item] = TagKey.create(BuiltInRegistries.ITEM.key(), Lumomancy.locate("stillwood_logs"))
    val wiederLogsTag: TagKey[Item] = TagKey.create(Registries.ITEM, Lumomancy.locate("wieder_logs"))
    val aftusLogsTag: TagKey[Item] = TagKey.create(Registries.ITEM, Lumomancy.locate("aftus_logs"))
    val coloredLeavesTag: TagKey[Item] = TagKey.create(Registries.ITEM, Lumomancy.locate("colored_leaves"))
    val coloredSaplingsTag: TagKey[Item] = TagKey.create(Registries.ITEM, Lumomancy.locate("colored_saplings"))

  object block:
    val stillwoodLogsTag: TagKey[Block] = TagKey.create(BuiltInRegistries.BLOCK.key(), Lumomancy.locate("stillwood_logs"))
    val wiederLogsTag: TagKey[Block] = TagKey.create(Registries.BLOCK, Lumomancy.locate("wieder_logs"))
    val aftusLogsTag: TagKey[Block] = TagKey.create(Registries.BLOCK, Lumomancy.locate("aftus_logs"))
    val coloredLeavesTag: TagKey[Block] = TagKey.create(Registries.BLOCK, Lumomancy.locate("colored_leaves"))
    val coloredSaplingsTag: TagKey[Block] = TagKey.create(Registries.BLOCK, Lumomancy.locate("colored_saplings"))

  def init(): Unit =
    val _ = block
    val _ = item
    InfoCollector.instance.appendTag(ItemTags.LOGS_THAT_BURN)
                 .subtag(item.stillwoodLogsTag)
                 .subtag(item.wiederLogsTag)
                 .subtag(item.aftusLogsTag)
                 .build()
    InfoCollector.instance.appendTag(BlockTags.LOGS_THAT_BURN)
                 .subtag(block.stillwoodLogsTag)
                 .subtag(block.wiederLogsTag)
                 .subtag(block.aftusLogsTag)
                 .build()
    InfoCollector.instance.appendTag(ItemTags.SAPLINGS)
                 .subtag(item.coloredSaplingsTag)
                 .build()
    InfoCollector.instance.appendTag(BlockTags.SAPLINGS)
                 .subtag(block.coloredSaplingsTag)
                 .build()
    InfoCollector.instance.appendTag(ItemTags.LEAVES)
                 .subtag(item.coloredLeavesTag)
                 .build()
    InfoCollector.instance.appendTag(BlockTags.LEAVES)
                 .subtag(block.coloredLeavesTag)
                 .build()