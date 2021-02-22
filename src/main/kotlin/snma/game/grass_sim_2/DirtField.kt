package snma.game.grass_sim_2

import com.jme3.math.FastMath
import com.jme3.math.Vector3f
import com.jme3.scene.Geometry
import com.jme3.scene.Mesh
import com.jme3.scene.Node
import com.jme3.scene.shape.Box
import kotlin.math.abs
import kotlin.math.max

class DirtField(
    width: Int,
    height: Int,
    private val step: Float,
) : Node("DirtField") {
    val boxes: List<DirtBox>

    init {
        boxes = mutableListOf()
        for (i in 0 until width) {
            for (j in 0 until height) {
                val box = DirtBox("Dirt$i.$j", Box(step / 2f, 0.1f, step / 2f)) // FIXME optimize: replace with simple square polygon
                box.setLocalTranslation((i - (width - 1) / 2f) * step, -0.1f, (j - (height - 1) / 2f) * step)
                box.waterLevel = 0.5f
//                box.waterLevel = FastMath.nextRandomFloat()
                attachChild(box)
                boxes.add(box)
            }
        }
    }

    fun findByPos(pos: Vector3f): DirtBox? {
        val box = boxes.minByOrNull { it.localTranslation.distanceSquared(pos) } ?: return null
        val boxPos = box.localTranslation
        val maxCoordinateDiff = max(abs(boxPos.x - pos.x), abs(boxPos.z - pos.z))
        return if (maxCoordinateDiff <= step / 1.999f) {
            box
        } else {
            null
        }
    }
}

class DirtBox(name: String, mesh: Mesh) : Geometry(name, mesh) {
    var waterLevel: Float = Float.NaN
        set(value) {
            assert(value in 0f..1f)
            field = value
            material = AssetStorage.INSTANCE.dirtMaterialByWaterLevel(value)
        }
}