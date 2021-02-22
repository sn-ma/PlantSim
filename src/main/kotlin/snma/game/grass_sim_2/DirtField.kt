package snma.game.grass_sim_2

import com.jme3.material.Material
import com.jme3.math.ColorRGBA
import com.jme3.math.FastMath
import com.jme3.math.Vector3f
import com.jme3.scene.Geometry
import com.jme3.scene.shape.Quad
import com.jme3.texture.Image
import com.jme3.texture.Texture2D
import com.jme3.texture.image.ColorSpace
import com.jme3.texture.image.ImageRaster
import com.jme3.util.BufferUtils
import kotlin.math.abs
import kotlin.math.max

class DirtField(
    width: Int,
    height: Int,
    private val step: Float,
) : Geometry("DirtField") {
    val waterLevelAccessors: List<WaterLevelAccessor>

    init {
        waterLevelAccessors = mutableListOf()

        val format = Image.Format.RGBA8
        val buffer = BufferUtils.createByteBuffer((format.bitsPerPixel / 8f * width * height).toInt())
        val image = Image(format, width, height, buffer, ColorSpace.Linear)
        val raster = ImageRaster.create(image)

        for (i in 0 until width) {
            for (j in 0 until height) {
                waterLevelAccessors.add(WaterLevelAccessor(
                    {color -> raster.setPixel(j, i, color)},
                    Vector3f((i - (width - 1) / 2f) * step, 0f, (j - (height - 1) / 2f) * step)))
            }
        }
        waterLevelAccessors.forEach { it.waterLevel = 0.5f }

        val texture = Texture2D(image)

        val material = Material(AssetStorage.INSTANCE.assetManager, "Common/MatDefs/Light/Lighting.j3md").also { material ->
            material.setTexture("DiffuseMap", texture)
            material.setColor("Diffuse", ColorRGBA.White)
            material.setColor("Ambient", ColorRGBA.White)
            material.setBoolean("UseMaterialColors", true)
        }
        setMaterial(material)

        mesh = Quad(width * step, height * step)
        rotate(-FastMath.HALF_PI, -FastMath.HALF_PI, 0f)
        setLocalTranslation(width * step / -2f, 0f, height * step / -2f)
    }

    fun findByPos(pos: Vector3f): WaterLevelAccessor? {
        val accessor = waterLevelAccessors.minByOrNull { it.centerPos.distanceSquared(pos) } ?: return null
        val maxCoordinateDiff = max(abs(accessor.centerPos.x - pos.x), abs(accessor.centerPos.z - pos.z))
        return if (maxCoordinateDiff <= step / 1.999f) {
            accessor
        } else {
            null
        }
    }
}

class WaterLevelAccessor(
    private val pixelSetter: (ColorRGBA) -> Unit,
    val centerPos: Vector3f,
) {
    var waterLevel: Float = Float.NaN
        set(value) {
            assert(value in 0f..1f)
            field = value
            pixelSetter(AssetStorage.INSTANCE.dirtColorByWaterLevel(value))
        }
}