package com.ae.aoc2020.day24

import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.RuntimeException
import kotlin.math.log

@SpringBootApplication
class Aoc2020ApplicationDay24 : CommandLineRunner {

	private val logger = LoggerFactory.getLogger(javaClass)

	enum class OffSet  {
		E, W, SE, SW, NW, NE
	}

	// Stolen from https://math.stackexchange.com/questions/2254655/hexagon-grid-coordinate-system
	data class HexCoordinate(val x: Int, val y: Int, val z: Int) {
		fun from(offSet: OffSet) : HexCoordinate {
			return when (offSet) {
				OffSet.E -> HexCoordinate(x+1, y - 1, z)
				OffSet.W -> HexCoordinate(x-1, y + 1, z)
				OffSet.SE -> HexCoordinate(x, y-1, z +1)
				OffSet.SW -> HexCoordinate(x-1, y , z + 1)
				OffSet.NE -> HexCoordinate(x + 1, y, z - 1)
				OffSet.NW -> HexCoordinate(x, y + 1, z -1)
			}
		}

		fun neighbours() : List<HexCoordinate> = OffSet.values().map { from(it) }.toList()

	}

	private fun solve(file: String) {
		val bufferedReader = BufferedReader(InputStreamReader(Aoc2020ApplicationDay24::class.java.getResourceAsStream("/$file")))

		bufferedReader.useLines { input ->
			val hexCoordinate2timesFlipped = HashMap<HexCoordinate, Int>()
			input.forEach { line ->
				var hexCoordinate = HexCoordinate(0,0,0)
				var p = 0
				while (p < line.length) {
					if (line.substring(p, p + 1).toUpperCase() in OffSet.values().map { it.toString() }) {
						hexCoordinate = hexCoordinate.from(OffSet.valueOf(line.substring(p, p + 1).toUpperCase()))
						p += 1
					} else if (line.substring(p, p + 2).toUpperCase() in OffSet.values().map { it.toString() }) {
						hexCoordinate = hexCoordinate.from(OffSet.valueOf(line.substring(p, p + 2).toUpperCase()))
						p += 2
					} else {
						throw RuntimeException("Parsing Exception ..")
					}
				}
				hexCoordinate2timesFlipped[hexCoordinate] = hexCoordinate2timesFlipped.getOrDefault(hexCoordinate, 0) + 1
			}
			val nFlippedBlack = hexCoordinate2timesFlipped.map { (it.value % 2) }.sum()
			logger.info("${nFlippedBlack} are left with the black side up ..")

			var blackTiles = hexCoordinate2timesFlipped.filter { (it.value % 2 == 1) }.map { it.key }.toSet()
			(0..99).forEach {
				val blackToWhite = blackTiles.filter {
					tile ->
					val blackNeighbors = tile.neighbours().filter { neighbour -> blackTiles.contains(neighbour) }
					blackNeighbors.isEmpty() || blackNeighbors.size >= 2
				}

				val whiteToBlack = blackTiles.map { it.neighbours() }.flatten().toSet()
						                     .filter { whiteNeighbour ->
												 val blackNeighbors = whiteNeighbour.neighbours().filter { neighbour -> blackTiles.contains(neighbour) }
												 blackNeighbors.size == 2
											 }
				blackTiles = blackTiles.minus(blackToWhite).plus(whiteToBlack)
				logger.info("Day ${it + 1}: ${blackTiles.size}")
			}
		}
	}

	override fun run(vararg args: String?) {
		solve(args[0]!!)
	}
}


fun main(args: Array<String>) {
	runApplication<Aoc2020ApplicationDay24>(*args)
}
