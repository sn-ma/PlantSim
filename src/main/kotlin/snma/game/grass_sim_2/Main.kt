package snma.game.grass_sim_2

import com.jme3.app.SimpleApplication
import com.jme3.input.KeyInput
import com.jme3.input.controls.ActionListener
import com.jme3.input.controls.KeyTrigger
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
    lateinit var rain: Rain

    override fun simpleInitApp() {
        AssetStorage.INSTANCE = AssetStorage(assetManager)

        flyByCamera.moveSpeed = 15f
        cam.location = Vector3f(-10f, 6f, 10f)
        cam.lookAt(Vector3f(0f, 0f, 0f), Vector3f.UNIT_Y)

        createAllTheStuff()

        setupLightAndShadows()

        inputManager.addMapping("ToggleRain", KeyTrigger(KeyInput.KEY_SPACE))
        inputManager.addListener(object : ActionListener {
            override fun onAction(name: String, isPressed: Boolean, tpf: Float) {
                when (name) {
                    "ToggleRain" -> {
                        if (isPressed) {
                            rain.toggle()
                        }
                    }
                }
            }
        }, "ToggleRain")
    }

    private fun createAllTheStuff() {
        val dirtField = DirtField(30, 30, 0.5f)
        dirtField.shadowMode = RenderQueue.ShadowMode.Receive
        rootNode.attachChild(dirtField)

        val plantParams1 = PlantParams(
            seedMaterial = AssetStorage.INSTANCE.seedMaterial1,
            waterLevelToGrow = 0.1f,
            seedGrowProb = 0.5f,
            seedDeathProb = 0.05f,
            plantModel = AssetStorage.INSTANCE.grass1,
            requiresWaterPerSecond = 0.02f,
            scaleIncrement = 1.2f,
            reserveIncrement = 0.05f,
            maxReservePerSize = 1f,
            splitSize = 5f,
            childrenCount = 3,
            childrenVelocity = 1f..5f,
        )
        rootNode.attachChild(Seed(plantParams1, Vector3f(), dirtField).also { it.setLocalTranslation(-5f, 6f, -5f) })
        rootNode.attachChild(Seed(plantParams1, Vector3f(), dirtField).also { it.setLocalTranslation(-4f, 6f, -4f) })

        val plantParams2 = PlantParams(
            seedMaterial = AssetStorage.INSTANCE.seedMaterial2,
            waterLevelToGrow = 0.2f,
            seedGrowProb = 0.5f,
            seedDeathProb = 0.03f,
            plantModel = AssetStorage.INSTANCE.grass2,
            requiresWaterPerSecond = 0.002f,
            scaleIncrement = 0.15f,
            reserveIncrement = 0.1f,
            maxReservePerSize = 10f,
            splitSize = 10f,
            childrenCount = 8,
            childrenVelocity = 3f..9f,
        )
        rootNode.attachChild(Seed(plantParams2, Vector3f(), dirtField).also { it.setLocalTranslation(5f, 6f, 5f) })
        rootNode.attachChild(Seed(plantParams2, Vector3f(), dirtField).also { it.setLocalTranslation(4f, 6f, 4f) })

        rain = Rain(dirtField, cam.width, cam.height)
        guiNode.attachChild(rain)
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