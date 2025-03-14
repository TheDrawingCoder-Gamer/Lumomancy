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

package gay.menkissing.lumomancy.client

import dev.emi.trinkets.api.client.TrinketRendererRegistry
import gay.menkissing.lumomancy.client.gui.ToolContainerGui
import gay.menkissing.lumomancy.content.{LumomancyBlocks, LumomancyItems}
import gay.menkissing.lumomancy.content.item.{LumonLens, StasisBottle, StasisTube}
import gay.menkissing.lumomancy.registries.LumomancyScreens
import net.fabricmc.api.{ClientModInitializer, EnvType, Environment}
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry
import net.minecraft.client.gui.screens.MenuScreens

@Environment(EnvType.CLIENT)
object LumomancyClient extends ClientModInitializer:
  override def onInitializeClient(): Unit =
    BuiltinItemRendererRegistry.INSTANCE.register(LumomancyItems.stasisTube, new StasisTube.Renderer())
    ModelLoadingPlugin.register(new StasisTube.Renderer())
    ModelLoadingPlugin.register(new StasisBottle.ItemModelLoader())
    MenuScreens.register(LumomancyScreens.toolContainer, ToolContainerGui.apply)
    LumomancyBlocks.registerClient()

    TrinketRendererRegistry.registerRenderer(LumomancyItems.lumonLens, LumonLens.Renderer())

  