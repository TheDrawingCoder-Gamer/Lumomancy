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

import gay.menkissing.lumomancy.util.registry.InfoCollector
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item

class ItemBuilder(val owner: InfoCollector, val item: Item, val rl: ResourceLocation) extends Builder[Item, ItemBuilder]:
  override protected def registered(): Item =
    Registry.register(BuiltInRegistries.ITEM, rl, item)

  def lang(value: String): this.type =
    lang(_.getDescriptionId, value)

  def tooltip(sub: String, value: String): this.type =
    lang(it =>
      it.getDescriptionId + ".tooltip." + sub,
      value
    )
