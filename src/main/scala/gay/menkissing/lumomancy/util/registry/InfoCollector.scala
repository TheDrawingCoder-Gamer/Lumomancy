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

package gay.menkissing.lumomancy.util.registry

import gay.menkissing.lumomancy.Lumomancy
import gay.menkissing.lumomancy.util.registry.builder.{BlockBuilder, ItemBuilder}
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator
import net.fabricmc.fabric.api.datagen.v1.provider.{FabricBlockLootTableProvider, FabricLanguageProvider}
import net.minecraft.core.HolderLookup
import net.minecraft.data.{CachedOutput, DataProvider}
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.storage.loot.LootTable

import java.util.concurrent.CompletableFuture
import scala.collection.mutable

/**
 * collects info for datagen
 * sort of inspired by registrate but registrate had too many things when I like just registering that thang
 */
class InfoCollector:

  private val lang = mutable.HashMap[String, String]()

  private val blockLootTables = mutable.HashMap[Block, FabricBlockLootTableProvider => LootTable.Builder]()

  private lazy val doDatagen = System.getProperty("fabric-api.datagen") != null

  def addRawLang(key: String, value: String): InfoCollector =
    if doDatagen then
      lang(key) = value
    this

  def addBlockLootTable(block: Block, table: FabricBlockLootTableProvider => LootTable.Builder): InfoCollector =
    if doDatagen then
      blockLootTables(block) = table
    this

  def block(name: ResourceLocation, block: Block): BlockBuilder =
    BlockBuilder(this, block, name)

  def block(name: String, block: Block): BlockBuilder =
    this.block(Lumomancy.locate(name), block)

  def item(name: ResourceLocation, item: Item): ItemBuilder =
    ItemBuilder(this, item, name)

  def item(name: String, item: Item): ItemBuilder =
    this.item(Lumomancy.locate(name), item)

  def registerDataGenerators(pack: FabricDataGenerator#Pack): Unit =
    pack.addProvider { (output, lookup) =>
      val langProvider = new FabricLanguageProvider(output, "en_us", lookup) {
        override def generateTranslations(provider: HolderLookup.Provider, translationBuilder: FabricLanguageProvider.TranslationBuilder): Unit =
          lang.foreach { (k, v) =>
            translationBuilder.add(k, v)
          }
      }
      val blockLootProvider =
        new FabricBlockLootTableProvider(output, lookup):
          override def generate(): Unit =
            blockLootTables.foreach { (block, gen) =>
              this.add(block, gen(this))
            }

      new DataProvider {
        override def run(output: CachedOutput): CompletableFuture[?] =
          CompletableFuture.allOf(langProvider.run(output), blockLootProvider.run(output))

        override def getName: String = "Block Loot + Lang provider for lumomancy"
      }
    }



object InfoCollector:
  val instance: InfoCollector = InfoCollector().addRawLang("itemGroup.lumomancy", "Lumomancy")

