package snma.game.grass_sim_2

import com.jme3.bounding.BoundingBox
import com.jme3.math.FastMath
import com.jme3.math.Vector3f
import com.jme3.renderer.RenderManager
import com.jme3.renderer.ViewPort
import com.jme3.scene.control.AbstractControl
import java.lang.Float.min

class PlantControl(
    private val params: PlantParams,
    private val dirtField: DirtField,
    private val dirtBox: WaterLevelAccessor,
): AbstractControl() {
    private var scale = 1f
    private var reserve = params.waterLevelToGrow

    override fun controlUpdate(tpf: Float) {
        val toGrowth = params.requiresWaterPerSecond * tpf
        if (dirtBox.waterLevel < toGrowth) {
            reserve -= toGrowth
            if (reserve < 0f) {
                destroy()
                return
            }
        }
        scale += params.scaleIncrement * tpf
        var toReserve = min(dirtBox.waterLevel, params.reserveIncrement * tpf)
        if (toReserve + reserve > params.maxReservePerSize * scale) {
            toReserve = params.maxReservePerSize * scale - reserve
        }
        reserve += toReserve

        dirtBox.waterLevel -= toGrowth + toReserve

        spatial.setLocalScale(scale)
        if (scale > params.splitSize) {
            split()
        }
    }

    private fun destroy() {
        spatial.removeFromParent()
    }

    private fun split() {
        val bb = spatial.worldBound as BoundingBox
        val topPos = spatial.localTranslation.addLocal(0f, bb.yExtent, 0f)
        for (i in 0 until params.childrenCount) {
            val angle = FastMath.nextRandomFloat() * FastMath.TWO_PI
            val direction = Vector3f(FastMath.cos(angle), 0f, FastMath.sin(angle))
            val velocity =
                FastMath.nextRandomFloat() *
                        (params.childrenVelocity.endInclusive - params.childrenVelocity.start) +
                        params.childrenVelocity.start
            val seed = Seed(params, direction.multLocal(velocity), dirtField)
            seed.localTranslation = topPos
            spatial.parent.attachChild(seed)
        }
        destroy()
    }

    override fun controlRender(rm: RenderManager?, vp: ViewPort?) {
    }
}