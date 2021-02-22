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

    val seedMaterial = toShadableTexture(ColorRGBA.Red)
//    val plantMaterial = toShadableTexture(ColorRGBA.Green)
//    val deadMaterial = toShadableTexture(ColorRGBA.Black)

    val dirtColors: List<ColorRGBA>
    init {
        dirtColors = mutableListOf()
        val minColor = ColorRGBA(1f, .95f, .53f, 1f) // yellow
        val maxColor = ColorRGBA(.17f, .11f, .04f, 1f) // dark brown
        for (i in 0 until Constants.Dirt.COLORS_COUNT) {
            val factor = i.toFloat() / Constants.Dirt.COLORS_COUNT
            dirtColors.add(minColor.mult(1f - factor).addLocal(maxColor.mult(factor)))
        }
    }

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

    private fun toShadableTexture(color: ColorRGBA) = Material(assetManager, "Common/MatDefs/Light/Lighting.j3md").also { material ->
        material.setColor("Diffuse", color)
        material.setColor("Ambient", color)
        material.setBoolean("UseMaterialColors", true)
    }

    fun dirtColorByWaterLevel(water: Float): ColorRGBA {
        assert(water in 0f..1f)
        val idx = (water * (dirtColors.size - 1)).roundToInt()
        return dirtColors[idx]
    }

    companion object {
        lateinit var INSTANCE: AssetStorage
    }
}