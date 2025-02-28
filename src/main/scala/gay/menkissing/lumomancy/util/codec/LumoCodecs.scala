package gay.menkissing.lumomancy.util.codec

import com.mojang.serialization.Codec
import scala.jdk.CollectionConverters.*

object LumoCodecs:
  def scalaListCodec[A](elementCodec: Codec[A]): Codec[List[A]] =
    Codec.list(elementCodec).xmap(_.asScala.toList, _.asJava)

  val scalaLongCodec: Codec[Long] =
    Codec.LONG.xmap(identity, identity)