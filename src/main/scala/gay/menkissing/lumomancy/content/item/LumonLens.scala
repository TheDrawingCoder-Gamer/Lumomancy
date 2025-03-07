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

package gay.menkissing.lumomancy.content.item

import com.mojang.blaze3d.vertex.PoseStack
import dev.emi.trinkets.api.SlotReference
import dev.emi.trinkets.api.client.TrinketRenderer
import net.fabricmc.api.{EnvType, Environment}
import net.minecraft.client.Minecraft
import net.minecraft.client.model.{EntityModel, HumanoidModel}
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.{ArmorItem, Item, ItemDisplayContext, ItemStack}
import net.minecraft.world.level.block.DispenserBlock

class LumonLens(props: Item.Properties) extends Item(props)

object LumonLens:
  @Environment(EnvType.CLIENT)
  class Renderer extends TrinketRenderer:
    override def render(itemStack: ItemStack, slotReference: SlotReference, entityModel: EntityModel[_ <: LivingEntity],
                        poseStack: PoseStack, multiBufferSource: MultiBufferSource,
                        light: Int, livingEntity: LivingEntity, limbAngle: Float, limbDistance: Float,
                        tickDelta: Float, animProgress: Float, headYaw: Float, headPitch: Float): Unit =
      entityModel match
        case humanoidModel: HumanoidModel[?] =>
          humanoidModel.head.translateAndRotate(poseStack)
          poseStack.translate(0.15, -0.2, -0.25)
          poseStack.scale(0.3f, -0.3f, -0.3f)
          Minecraft.getInstance().getItemRenderer.renderStatic(itemStack, ItemDisplayContext.NONE, light,
            OverlayTexture.NO_OVERLAY, poseStack, multiBufferSource, livingEntity.level(), livingEntity.getId)
        case _ => ()


