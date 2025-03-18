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

package gay.menkissing.lumomancy.util

import net.minecraft.data.models.blockstates.{Variant, VariantProperties}
import net.minecraft.resources.ResourceLocation

object VariantHelpers:
  class Builder(model: ResourceLocation):
    val variant = Variant.variant().`with`(VariantProperties.MODEL, model)

    def build(): Variant = variant

    def xRot(rot: Int): this.type =
      variant.`with`(VariantProperties.X_ROT, VariantProperties.Rotation.values()((rot % 360) / 90))
      this

    def yRot(rot: Int): this.type =
      variant.`with`(VariantProperties.Y_ROT, VariantProperties.Rotation.values()((rot % 360) / 90))
      this

    def uvLock(lock: Boolean): this.type =
      variant.`with`(VariantProperties.UV_LOCK, lock)
      this

  def ofModel(model: ResourceLocation): Variant =
    Variant.variant().`with`(VariantProperties.MODEL, model)
