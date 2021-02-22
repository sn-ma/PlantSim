package snma.game.grass_sim_2

import com.jme3.material.Material
import com.jme3.scene.Spatial

class PlantParams(
    val seedMaterial: Material,
    val waterLevelToGrow: Float,
    val seedGrowProb: Float,
    val seedDeathProb: Float,
    val plantModel: Spatial,
    val requiresWaterPerSecond: Float,
    val scaleIncrement: Float,
    val splitSize: Float,
    val childrenCount: Int,
    val childrenVelocity: ClosedFloatingPointRange<Float>,
)