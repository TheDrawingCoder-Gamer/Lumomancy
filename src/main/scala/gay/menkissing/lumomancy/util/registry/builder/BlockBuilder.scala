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

package gay.menkissing.lumomancy.util.registry.builder

import gay.menkissing.lumomancy.Lumomancy
import gay.menkissing.lumomancy.util.registry.InfoCollector
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.{BlockItem, Item}
import net.minecraft.world.level.ItemLike
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.storage.loot.LootTable

class BlockBuilder(val owner: InfoCollector, val block: Block, val rl: ResourceLocation) extends Builder[Block, BlockBuilder]:
  private var makeItem = false
  
  def item(): this.type =
    makeItem = true
    this
  
  override protected def registered(): Block =
    Registry.register(BuiltInRegistries.BLOCK, rl, block)
    if makeItem then
      val bi = BlockItem(block, Item.Properties())
      Registry.register(BuiltInRegistries.ITEM, rl, bi)
    block
    
  def lang(value: String): this.type =
    lang((block: Block) => block.getDescriptionId, value)
    this
    
  def lootTable(table: FabricBlockLootTableProvider => LootTable.Builder): this.type =
    owner.addBlockLootTable(block, table)
    this
    
  def dropOther(item: ItemLike): this.type =
    lootTable(_.createSingleItemTable(item))
    this
    
  def dropSelf(): this.type =
    lootTable(_.createSingleItemTable(block))
    this

