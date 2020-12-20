package com.ae.aoc2020.day20

import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.io.BufferedReader
import java.io.InputStreamReader
import kotlin.math.cos
import kotlin.math.sin

@SpringBootApplication
class Aoc2020ApplicationDay20 : CommandLineRunner {

	private val logger = LoggerFactory.getLogger(javaClass)

	val degrees = listOf(0, 90, 180, 270)

	enum class Flip  {  NONE, HORIZONTAL, VERTICAL }

	enum class Edge { LEFT, RIGHT, TOP, BOTTOM }

	data class Configuration(val degree: Int, val flip: Flip)

	data class Placement(val x: Int, val y: Int) {
		fun top()  = Placement(x, y - 1)
		fun left() = Placement(x -1, y)
		fun right() = Placement(x + 1, y)
		fun bottom() = Placement(x, y + 1)
	}

	data class Tile(val id: Long, val image: List<CharArray>) {

		fun width() : Int = image[0].size

		fun rotationStuff(degree: Int, x: Double, y: Double) : Pair<Double, Double> {
			val degree_ = Math.toRadians(degree.toDouble())
			val x_ =  (x * cos(degree_))  - (sin(degree_) * y)
			val y_ =  (x * sin(degree_))  + (cos(degree_) * y)
			return Pair(x_, y_)
		}

		private fun rotate(degree: Int) : Tile {
			val target = (0..9).map { CharArray(10) }.toList()
			image.withIndex().forEach { y ->
				y.value.withIndex().forEach { x ->
					val offset = width() / 2
					var x_t  = (x.index + 0.5)  - offset
					var y_t = (y.index + 0.5) - offset

					val r = rotationStuff(degree, x_t, y_t)

					val x_ =  (r.first + offset).toInt()
					val y_ = (r.second + offset).toInt()

					target[y_][x_] = image[y.index][x.index]
				}
			}
			return Tile(id, target)
		}

		private fun flipped(flip: Flip) : Tile {
			val target = (0..9).map {  CharArray(10) }.toList()
			image.withIndex().forEach { y ->
				y.value.withIndex().forEach { x ->
					val offset = width() / 2
					var x_t  = (x.index + 0.5)  - offset
					var y_t = (y.index + 0.5) - offset

					when (flip) {
						Flip.HORIZONTAL -> {
							y_t *= -1
						}
						Flip.VERTICAL -> {
							x_t *= -1
						}
						Flip.NONE -> {

						}
					}
					val x_ = (x_t + offset).toInt()
					val y_ = (y_t + offset).toInt()

					target[y_][x_] = image[y.index][x.index]
				}
			}
			return Tile(id, target)
		}

		fun fromConfig(configuration: Configuration) : Tile {
			return rotate(configuration.degree).flipped(configuration.flip)
		}

		fun edge(edge: Edge) : CharArray {
			return when(edge) {
				Edge.BOTTOM -> image.last()
				Edge.LEFT -> image.map { it[0] }.toCharArray()
				Edge.RIGHT -> image.map { it.last() }.toCharArray()
				Edge.TOP -> image.first()
			}
		}
	}

	private fun parseTile(it: List<String>): Tile {
		val (id) = Regex("(\\d+):").find(it[0].split(" ")[1])!!.destructured
		val image = it.drop(1).take(10).map { it.toCharArray() }
		return Tile(id.toLong(), image)
	}

	private fun edgesMatch(placment: Placement,
						   tile: Tile,
						   currentPlacements: Map<Placement, Pair<Tile, Configuration>>) : Boolean {
		if (currentPlacements.containsKey(placment.left())
			&& !tile.edge(Edge.LEFT).contentEquals(currentPlacements[placment.left()]!!.first.edge(Edge.RIGHT))) {
			return false
		}
		if (currentPlacements.containsKey(placment.right())
			&& !tile.edge(Edge.RIGHT).contentEquals(currentPlacements[placment.left()]!!.first.edge(Edge.LEFT))) {
			return false
		}
		if (currentPlacements.containsKey(placment.top())
			&& !tile.edge(Edge.TOP).contentEquals(currentPlacements[placment.left()]!!.first.edge(Edge.BOTTOM)))  {
			return false
		}
		if (currentPlacements.containsKey(placment.bottom())
			&& !tile.edge(Edge.BOTTOM).contentEquals(currentPlacements[placment.left()]!!.first.edge(Edge.TOP)))  {
			return false
		}
		return true
	}

	private fun solveH(tiles: Set<Tile>,
					   possibleConfigurations: Set<Configuration>,
					   currentPlacements: Map<Placement, Pair<Tile, Configuration>>,
					   placementsToDo: List<Placement>) : Map<Placement, Pair<Tile, Configuration>>? {
		logger.info("Trying with ${tiles.size} ..")
		val placement = placementsToDo.first()!!
		tiles.forEach { tile ->
			possibleConfigurations.forEach lit@{ configuration ->
				val tile_ = tile.fromConfig(configuration)
				if (!edgesMatch(placement, tile_, currentPlacements)) {
					return@lit
				}

				val currentPlacments_ = currentPlacements.toMutableMap()
				currentPlacments_[placement] = Pair(tile_, configuration)
				val tiles_ = tiles.minus(tile)
				val placementsToDo_ = placementsToDo.drop(1)
				if (placementsToDo_.isEmpty()) {
					return currentPlacments_
				}

				val ok = solveH(tiles_, possibleConfigurations, currentPlacments_, placementsToDo_)
				if (ok != null) {
					return ok
				}
			}
		}
		return null
	}

	private fun solve1(file: String) {
		val bufferedReader = BufferedReader(InputStreamReader(Aoc2020ApplicationDay20::class.java.getResourceAsStream("/$file")))
		bufferedReader.useLines { input ->
			val tiles = input.chunked(12).map { parseTile(it) }.toSet()
			val placementsToDo = sequence {
				(0..2).forEach { y ->
					(0..2).forEach { x->
						yield(Placement(x,y))
					}
				}
			}.toList()
			val possibleConfigurations = sequence {
				degrees.forEach { degree ->
					Flip.values().forEach { flip ->
						yield(Configuration(degree, flip))
					}
				}
			}.toSet()
			val currentPlacements = solveH(tiles,
					                       possibleConfigurations,
					                       HashMap(),
					                       placementsToDo)!!
			val anwer = listOf(currentPlacements[Placement(0,0)]!!,
			                   currentPlacements[Placement(2,0)]!!,
			                   currentPlacements[Placement(0,2)]!!,
			                   currentPlacements[Placement(2,2)]!!).map { it.first.id }.reduce(Long::times)
			logger.info("$anwer do you get if you multiply together the IDs of the four corner tiles")

		}
	}

	private fun solve2(file: String) {
		val bufferedReader = BufferedReader(InputStreamReader(Aoc2020ApplicationDay20::class.java.getResourceAsStream("/$file")))
		bufferedReader.useLines { input ->

		}
	}

	override fun run(vararg args: String?) {
		solve1(args[0]!!)
		solve2(args[0]!!)
	}
}

fun main(args: Array<String>) {
	runApplication<Aoc2020ApplicationDay20>(*args)
}
