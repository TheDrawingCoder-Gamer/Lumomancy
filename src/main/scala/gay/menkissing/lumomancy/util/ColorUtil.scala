package gay.menkissing.lumomancy.util

import org.joml.Vector3f

import java.awt.Color

object ColorUtil:
  def colorIntToVec(color: Int): Vector3f =
    // cursed
    val colorObj = new Color(color)
    val argb = Array.ofDim[Float](4)
    colorObj.getColorComponents(argb)
    Vector3f(argb(0), argb(1), argb(2))
