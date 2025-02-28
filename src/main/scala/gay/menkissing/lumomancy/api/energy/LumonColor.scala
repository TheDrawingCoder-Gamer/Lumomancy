package gay.menkissing.lumomancy.api.energy

import com.mojang.serialization.Codec
import gay.menkissing.lumomancy.registries.LumomancyRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.TagKey
import org.joml.Vector3f

import scala.jdk.OptionConverters.*

case class LumonColor(
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