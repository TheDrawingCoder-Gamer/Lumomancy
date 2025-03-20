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
                   .addTag(ConventionalItemTags.TOOLS)
                   .addTag(ItemTags.HEAD_ARMOR)
                   .addTag(ItemTags.CHEST_ARMOR)
                   .addTag(ItemTags.LEG_ARMOR)
                   .addTag(ItemTags.FOOT_ARMOR)
                   .add(Items.SPYGLASS)
                   .addTag(ItemTags.COMPASSES)
                   .addTag(TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "wrenches")))
                   .register()

    val stillwoodLogsTag: TagKey[Item] = 
      InfoCollector.instance.tag(BuiltInRegistries.ITEM.key(), Lumomancy.locate("stillwood_logs"))
                   .tag(ItemTags.LOGS_THAT_BURN)
                   .register()
    val wiederLogsTag: TagKey[Item] = 
      InfoCollector.instance.tag(Registries.ITEM, Lumomancy.locate("wieder_logs"))
                   .tag(ItemTags.LOGS_THAT_BURN)
                   .register()
    val aftusLogsTag: TagKey[Item] = 
      InfoCollector.instance.tag(Registries.ITEM, Lumomancy.locate("aftus_logs"))
                   .tag(ItemTags.LOGS_THAT_BURN)
                   .register()
    val coloredLeavesTag: TagKey[Item] = 
      InfoCollector.instance.tag(Registries.ITEM, Lumomancy.locate("colored_leaves"))
                   .tag(ItemTags.LEAVES)
                   .register()
    val coloredSaplingsTag: TagKey[Item] = 
      InfoCollector.instance.tag(Registries.ITEM, Lumomancy.locate("colored_saplings"))
                   .tag(ItemTags.SAPLINGS)
                   .register()

  object block:
    val stillwoodLogsTag: TagKey[Block] = 
      InfoCollector.instance.tag(BuiltInRegistries.BLOCK.key(), Lumomancy.locate("stillwood_logs"))
                   .tag(BlockTags.LOGS_THAT_BURN)
                   .register()
    val wiederLogsTag: TagKey[Block] = 
      InfoCollector.instance.tag(Registries.BLOCK, Lumomancy.locate("wieder_logs"))
                   .tag(BlockTags.LOGS_THAT_BURN)
                   .register()
    val aftusLogsTag: TagKey[Block] = 
      InfoCollector.instance.tag(Registries.BLOCK, Lumomancy.locate("aftus_logs"))
                   .tag(BlockTags.LOGS_THAT_BURN)
                   .register()
    val coloredLeavesTag: TagKey[Block] = 
      InfoCollector.instance.tag(Registries.BLOCK, Lumomancy.locate("colored_leaves"))
                   .tag(BlockTags.LEAVES)
                   .tag(BlockTags.MINEABLE_WITH_HOE)
                   .tag(BlockTags.SWORD_EFFICIENT)
                   .register()
    val coloredSaplingsTag: TagKey[Block] = 
      InfoCollector.instance.tag(Registries.BLOCK, Lumomancy.locate("colored_saplings"))
                   .tag(BlockTags.SAPLINGS)
                   .register()

  def init(): Unit =
    val _ = block
    val _ = item