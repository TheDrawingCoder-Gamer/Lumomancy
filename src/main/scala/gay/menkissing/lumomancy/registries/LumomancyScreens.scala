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

import com.mojang.serialization.Codec
import gay.menkissing.lumomancy.Lumomancy
import gay.menkissing.lumomancy.screen.ToolContainerMenu
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.{FriendlyByteBuf, RegistryFriendlyByteBuf}
import net.minecraft.network.codec.{StreamCodec, StreamDecoder, StreamEncoder}
import net.minecraft.util.ExtraCodecs
import net.minecraft.world.inventory.MenuType

object LumomancyScreens:
  val toolContainer: ExtendedScreenHandlerType[ToolContainerMenu, Boolean] = new ExtendedScreenHandlerType[ToolContainerMenu, Boolean](ToolContainerMenu.fromNetwork, StreamCodec.of(
    new StreamEncoder[RegistryFriendlyByteBuf, Boolean] {
      override def encode(a: RegistryFriendlyByteBuf, b: Boolean): Unit =
        a.writeBoolean(b)  
    },
    new StreamDecoder[RegistryFriendlyByteBuf, Boolean] {
      override def decode(buf: RegistryFriendlyByteBuf): Boolean = buf.readBoolean()
    }
  ))
  Registry.register(BuiltInRegistries.MENU, Lumomancy.locate("tool_container"), toolContainer)

  def init(): Unit = ()