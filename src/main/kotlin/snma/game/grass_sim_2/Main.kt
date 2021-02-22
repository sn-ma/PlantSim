package snma.game.grass_sim_2

import com.jme3.app.SimpleApplication
import com.jme3.light.AmbientLight
import com.jme3.light.DirectionalLight
import com.jme3.math.ColorRGBA
import com.jme3.math.Vector3f
import com.jme3.renderer.queue.RenderQueue
import com.jme3.shadow.DirectionalLightShadowRenderer

fun main() {
    App().start()
}

class App: SimpleApplication() {
    override fun simpleInitApp() {
        AssetStorage.INSTANCE = AssetStorage(assetManager)

        flyByCamera.moveSpeed = 15f
        cam.location = Vector3f(10f, 6f, -10f)
        cam.lookAt(Vector3f(0f, 0f, 0f), Vector3f.UNIT_Y)

        val dirtField = DirtField(30, 30, 0.5f)
        dirtField.shadowMode = RenderQueue.ShadowMode.Receive
        rootNode.attachChild(dirtField)

        val plantParams = PlantParams(
            seedMaterial = AssetStorage.INSTANCE.seedMaterial,
            waterLevelToGrow = 0.1f,
            seedGrowProb = 0.5f,
            seedDeathProb = 0.01f,
            plantModel = AssetStorage.INSTANCE.grass1,
            requiresWaterPerSecond = 0.03f,
            scaleIncrement = 0.9f,
            splitSize = 5f,
            childrenCount = 3,
            childrenVelocity = 2f..6f,
        )
        rootNode.attachChild(Seed(plantParams, Vector3f(), dirtField).also { it.setLocalTranslation(0f, 6f, 0f) })

        guiNode.attachChild(Rain(dirtField, cam.width, cam.height))

        setupLightAndShadows()
    }

    private fun setupLightAndShadows() {
        val sun = DirectionalLight().apply { // Sun
            color = ColorRGBA.White.mult(2f)
            direction = Vector3f(-.5f, -.5f, -.5f).normalizeLocal()
            rootNode.addLight(this)
        }

        rootNode.addLight(AmbientLight().apply {
            color = ColorRGBA.White.mult(0.2f)
        })

        val dlsr = DirectionalLightShadowRenderer(assetManager, 4096, 3)
        dlsr.light = sun
        viewPort.addProcessor(dlsr)

        rootNode.shadowMode = RenderQueue.ShadowMode.CastAndReceive

//        val fpp = FilterPostProcessor(assetManager)
//        val ssaoFilter = SSAOFilter(0.05f, 2f, 0.05f, 0.1f)
//        fpp.addFilter(ssaoFilter)
//        viewPort.addProcessor(fpp)
    }
}