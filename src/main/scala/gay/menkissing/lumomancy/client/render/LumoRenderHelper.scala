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

package gay.menkissing.lumomancy.client.render

import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.{BufferUploader, DefaultVertexFormat, PoseStack, Tesselator, VertexFormat}
import net.fabricmc.api.{EnvType, Environment}
import net.minecraft.client.renderer.{GameRenderer, MultiBufferSource, RenderType}
import net.minecraft.client.renderer.texture.TextureAtlasSprite


@Environment(EnvType.CLIENT)
object LumoRenderHelper:
  // adapted from EMI <https://github.com/emilyploszaj/emi/blob/1.21/xplat/src/main/java/dev/emi/emi/EmiRenderHelper.java#L88>
  def drawTintedSprite(stack: PoseStack, sprite: TextureAtlasSprite, source: MultiBufferSource.BufferSource, color: Int, x: Int, y: Int, xOff: Int, yOff: Int, width: Int, height: Int): Unit =
    if sprite == null then
      return

    RenderSystem.setShader(() => GameRenderer.getPositionTexColorShader)
    RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f)
    RenderSystem.setShaderTexture(0, sprite.atlasLocation())
    RenderSystem.disableBlend()

    val r = ((color >> 16) & 255) / 256f
    val g = ((color >> 8) & 255) / 256f
    val b = (color & 255) / 256f
    val bufferBuilder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR)
    val xMin = x.toFloat
    val yMin = y.toFloat
    val xMax = xMin + width
    val yMax = yMin + height
    val uSpan = sprite.getU1 - sprite.getU0
    val vSpan = sprite.getV1 - sprite.getV0
    val uMin = sprite.getU0 + uSpan / 16 * xOff
    val vMin = sprite.getV0 + vSpan / 16 * xOff
    val uMax = sprite.getU1 - uSpan / 16 * (16 - (width + xOff))
    val vMax = sprite.getV1 - vSpan / 16 * (16 - (height + yOff))
    val model = stack.last().pose()
    bufferBuilder.addVertex(model, xMin, yMax, 1).setColor(r, g, b, 1f).setUv(uMin, vMax)
    bufferBuilder.addVertex(model, xMax, yMax, 1).setColor(r, g, b, 1f).setUv(uMax, vMax)
    bufferBuilder.addVertex(model, xMax, yMin, 1).setColor(r, g, b, 1f).setUv(uMax, vMin)
    bufferBuilder.addVertex(model, xMin, yMin, 1).setColor(r, g, b, 1f).setUv(uMin, vMin)
    BufferUploader.drawWithShader(bufferBuilder.buildOrThrow())


