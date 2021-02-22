package snma.game.grass_sim_2

import com.jme3.math.FastMath
import com.jme3.math.Vector3f
import com.jme3.renderer.RenderManager
import com.jme3.renderer.ViewPort
import com.jme3.renderer.queue.RenderQueue
import com.jme3.scene.Geometry
import com.jme3.scene.control.AbstractControl
import com.jme3.scene.shape.Box

class Seed(
    private val params: PlantParams,
    private val velocity: Vector3f,
    private val dirtField: DirtField,
) : Geometry("Seed", Box(0.02f, 0.02f, 0.02f)) {
    init {
        material = params.seedMaterial
        shadowMode = RenderQueue.ShadowMode.Off

        addControl(object : AbstractControl() {
            override fun controlUpdate(tpf: Float) {
                this@Seed.update(tpf)
            }

            override fun controlRender(rm: RenderManager?, vp: ViewPort?) {
            }
        })
    }

    private var waterLevelAccessor: WaterLevelAccessor? = null

    private fun update(tpf: Float) {
        if (waterLevelAccessor == null) {
            val pos = localTranslation
            pos.addLocal(velocity.mult(tpf))
            velocity.addLocal(0f, -Constants.GRAVITY * tpf, 0f)
            if (pos.y <= 0f) {
                pos.y = 0f
                waterLevelAccessor = dirtField.findByPos(pos)
                if (waterLevelAccessor == null) {
                    destroy()
                }
            }
            localTranslation = pos
        } else {
            val dirtBox = waterLevelAccessor!!
            if (dirtBox.waterLevel > params.waterLevelToGrow && FastMath.nextRandomFloat() < params.seedGrowProb * tpf) {
                dirtBox.waterLevel -= params.waterLevelToGrow
                grow()
            } else if (FastMath.nextRandomFloat() < params.seedDeathProb * tpf) {
                destroy()
            }
        }
    }

    private fun destroy() {
        removeFromParent()
    }

    private fun grow() {
        val plantModel = params.plantModel.clone()
        plantModel.localTranslation = localTranslation
        plantModel.rotate(0f, FastMath.nextRandomFloat() * FastMath.TWO_PI, 0f)
        plantModel.addControl(PlantControl(params, dirtField, waterLevelAccessor!!))
        parent.attachChild(plantModel)

        destroy()
    }
}