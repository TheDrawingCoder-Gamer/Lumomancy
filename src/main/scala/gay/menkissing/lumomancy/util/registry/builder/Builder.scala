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
import net.minecraft.resources.ResourceLocation

import scala.collection.mutable

abstract class Builder[R, S <: Builder[R, S]]:

  private val delayedLangEntries = mutable.ListBuffer[(R => String, String)]()
  
  def owner: InfoCollector

  def lang(key: String, value: String): this.type =
    owner.addRawLang(key, value)
    this
  
  def lang(key: R => String, value: String): this.type =
    delayedLangEntries.append((key, value))
    this
  

  protected def registered(): R
  
  def register(): R =
    val item = registered()
    delayedLangEntries.foreach { (k, v) =>
      owner.addRawLang(k(item), v)
    }
    item
