package snma.game.grass_sim_2

import com.jme3.math.ColorRGBA
import com.jme3.math.FastMath
import com.jme3.renderer.RenderManager
import com.jme3.renderer.ViewPort
import com.jme3.scene.control.AbstractControl
import com.jme3.texture.Image
import com.jme3.texture.Texture2D
import com.jme3.texture.image.ColorSpace
import com.jme3.texture.image.ImageRaster
import com.jme3.ui.Picture
import com.jme3.util.BufferUtils

class Rain (private val dirtField: DirtField, width: Int, height: Int): Picture("Rain") {
    enum class Mode { RAINING, NOT_RAINING }

    private var mode: Mode = Mode.RAINING
        set(value) {
            if (value != field) {
                setCullHint(if (value == Mode.NOT_RAINING) CullHint.Always else CullHint.Dynamic)
            }
            field = value
        }

    init {
        val format = Image.Format.RGBA8
        val buffer = BufferUtils.createByteBuffer((format.bitsPerPixel / 8f * 1f * 1f).toInt())
        val image = Image(format, 1, 1, buffer, ColorSpace.Linear)
        val raster = ImageRaster.create(image)
        val color = ColorRGBA(0.3f, 0.5f, 1f, 0.3f)
        raster.setPixel(0, 0, color)

        val texture = Texture2D(image)
        setTexture(AssetStorage.INSTANCE.assetManager, texture, true)
        setWidth(width.toFloat())
        setHeight(height.toFloat())

        mode = Mode.NOT_RAINING

        addControl(RainControl())
    }

    fun update(tpf: Float) {
        when (mode) {
            Mode.NOT_RAINING -> {
                if (FastMath.nextRandomFloat() < Constants.Rain.START_PROB * tpf) {
                    mode = Mode.RAINING
                }
            }
            Mode.RAINING -> {
                dirtField.waterLevelAccessors.forEach { it.waterLevel = (it.waterLevel + Constants.Rain.WATER_INC * tpf)
                    .coerceAtMost(1f) }

                if (FastMath.nextRandomFloat() < Constants.Rain.STOP_PROB * tpf) {
                    mode = Mode.NOT_RAINING
                }
            }
        }
    }

    fun toggle() {
        mode = if (mode == Mode.NOT_RAINING) Mode.RAINING else Mode.NOT_RAINING
    }
}

private class RainControl: AbstractControl() {
    override fun controlUpdate(tpf: Float) {
        (spatial as Rain).update(tpf)
    }

    override fun controlRender(rm: RenderManager?, vp: ViewPort?) {
    }
}