package gay.menkissing.lumomancy.api.energy

import com.mojang.serialization.Codec
import gay.menkissing.lumomancy.registries.LumomancyRegistries
import gay.menkissing.lumomancy.util.ColorUtil
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.TagKey
import net.minecraft.world.item.DyeColor
import org.joml.Vector3f

import scala.jdk.OptionConverters.*

case class LumonColor(
                     dyeColor: Option[DyeColor],
                     colorInt: Int,
                     colorVec: Vector3f,
                     textColor: Int,
                     textColorVec: Vector3f
                     ):
  def id: ResourceLocation =
    LumomancyRegistries.lumonColors.getKey(this)

  def isIn(tagKey: TagKey[LumonColor]): Boolean =
    LumomancyRegistries.lumonColors.wrapAsHolder(this).is(tagKey)

object LumonColor:
  def ofID(loc: ResourceLocation): Option[LumonColor] =
    LumomancyRegistries.lumonColors.getOptional(loc).toScala

  val CODEC: Codec[LumonColor] =
    ResourceLocation.CODEC.xmap(it => LumonColor.ofID(it).get, _.id)

  def fromInts(dyeColor: Option[DyeColor], color: Int, textColor: Int): LumonColor =
    LumonColor(dyeColor, color, ColorUtil.colorIntToVec(color), textColor, ColorUtil.colorIntToVec(textColor))