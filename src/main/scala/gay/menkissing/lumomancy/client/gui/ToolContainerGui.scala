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

package gay.menkissing.lumomancy.client.gui

import gay.menkissing.lumomancy.screen.ToolContainerMenu
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Inventory

class ToolContainerGui(menu: ToolContainerMenu, inventory: Inventory, component: Component)
  extends AbstractContainerScreen[ToolContainerMenu](menu, inventory, component):
  this.imageHeight = 114 + ToolContainerMenu.rows * 18
  this.inventoryLabelY = this.imageHeight - 94

  override def render(gui: GuiGraphics, mouseX: Int, mouseY: Int, partialTicks: Float): Unit =
    this.renderBackground(gui, mouseX, mouseY, partialTicks)
    super.render(gui, mouseX, mouseY, partialTicks)
    this.renderTooltip(gui, mouseX, mouseY)

  override protected def renderBg(guiGraphics: GuiGraphics, f: Float, i: Int, j: Int): Unit =
    val k: Int = (this.width - this.imageWidth) / 2
    val l: Int = (this.height - this.imageHeight) / 2
    guiGraphics.blit(ToolContainerGui.texture, k, l, 0, 0, this.imageWidth, ToolContainerMenu.rows * 18 + 17)
    guiGraphics.blit(ToolContainerGui.texture, k, l + ToolContainerMenu.rows * 18 + 17, 0, 126, this.imageWidth, 96)

object ToolContainerGui:
  val texture = ResourceLocation.withDefaultNamespace("textures/gui/container/generic_54.png")


