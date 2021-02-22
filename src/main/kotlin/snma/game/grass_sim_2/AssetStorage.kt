package snma.game.grass_sim_2

import com.jme3.asset.AssetManager
import com.jme3.asset.ModelKey
import com.jme3.material.Material
import com.jme3.math.ColorRGBA
import com.jme3.scene.Node
import com.jme3.scene.Spatial
import kotlin.math.roundToInt

class AssetStorage(
    val assetManager: AssetManager
) {
    private val grassScene = loadScene("grass1.glb")
    val grass1: Spatial = prepareModel(grassScene, "Grass1", 0.1f)
    val grass2: Spatial = prepareModel(grassScene, "Grass2", 0.1f)

    val seedMaterial by lazy { toShadableTexture(ColorRGBA.Red) }
//    val plantMaterial by lazy { toShadableTexture(ColorRGBA.Green) }
//    val deadMaterial by lazy { toShadableTexture(ColorRGBA.Black) }

    private fun loadScene(filename: String): Node {
        val key = ModelKey(filename)
        return assetManager.loadModel(key) as Node
    }

    private fun prepareModel(scene: Node, name: String, scale: Float): Spatial {
        val spatial = scene.getChild(name)!!
        spatial.setLocalTranslation(0f, 0f, 0f)
        spatial.scale(scale)
        return Node().also { it.attachChild(spatial) }
    }

    private val dirtMaterials by lazy {
        listOf(
            ColorRGBA(1f, .86f, .31f, 1f), // yellow
            ColorRGBA(.82f, .69f, .19f, 1f),
            ColorRGBA(.58f, .42f, .07f, 1f),
            ColorRGBA(.36f, .20f, .02f, 1f),
            ColorRGBA(.23f, .11f, .00f, 1f), // dark brown
        ).map(::toShadableTexture)
    }

    private fun toShadableTexture(color: ColorRGBA) = Material(assetManager, "Common/MatDefs/Light/Lighting.j3md").also { material ->
        material.setColor("Diffuse", color)
        material.setColor("Ambient", color)
        material.setBoolean("UseMaterialColors", true)
    }

    fun dirtMaterialByWaterLevel(water: Float): Material {
        assert(water in 0f..1f)
        val idx = (water * (dirtMaterials.size - 1)).roundToInt()
        return dirtMaterials[idx]
    }

    companion object {
        lateinit var INSTANCE: AssetStorage
    }
}