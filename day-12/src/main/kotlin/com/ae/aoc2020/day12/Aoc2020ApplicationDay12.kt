package com.ae.aoc2020.day12

import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.RuntimeException
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

@SpringBootApplication
class Aoc2020ApplicationDay12 : CommandLineRunner {

	private val logger = LoggerFactory.getLogger(javaClass)

	val steeringDirections = listOf('L', 'R')

	inner class NavigationState(val navigationInstractions: List<String>) {
		var lDirections = mapOf(
				'N' to listOf('W', 'S', 'E', 'N'),
				'W' to listOf('S', 'E', 'N', 'W'),
				'S' to listOf('E', 'N', 'W', 'S'),
				'E' to listOf('N', 'W', 'S', 'E')
		)
		var rDirections =  mapOf(
				'N' to listOf('E', 'S', 'W', 'N'),
				'E' to listOf('S', 'W', 'N', 'E'),
				'S' to listOf('W', 'N', 'E', 'S'),
				'W' to listOf('N', 'E', 'S', 'W')
		)

		var direction = 'E'
		var myX = 0
		var myY = 0

		fun updateStuff(direction: Char, distance: Int) {
			when (direction) {
				'N' -> myY += distance
				'S' -> myY -= distance
				'E' -> myX += distance
				'W' -> myX -= distance
				else -> throw RuntimeException("Illegal Input")
			}
		}

		fun step(instruction: String) {
			val distance = instruction.drop(1).toInt()
			val directionInstruction = instruction[0]

			if (steeringDirections.contains(directionInstruction)) {
				val distanceOffSet = (distance / 90) - 1
				direction = if (directionInstruction == 'L') lDirections[direction]!![distanceOffSet]
				            else  rDirections[direction]!![distanceOffSet]
			} else {
				val d = if (directionInstruction == 'F') direction else directionInstruction
				updateStuff(d, distance)
			}
		}

		fun run() {
			navigationInstractions.forEach { step(it) }
		}

		fun distance() = abs(myX) + abs(myY)
	}

	inner class WaypointNavigationState(val navigationInstractions: List<String>) {

		var location = Pair(0, 0)

		var wayPointLocation = Pair(10, 1)

		private operator fun Pair<Int, Int>.plus(pair: Pair<Int, Int>) : Pair<Int, Int> {
			return Pair(first + pair.first, second + pair.second)
		}

		private operator fun Pair<Int, Int>.times(amount: Int): Pair<Int, Int> {
			return Pair(first * amount, second * amount)
		}

		private fun Pair<Int, Int>.abs(): Int {
			return abs(first) + abs(second)
		}

		// I Fucking hate rotation matrices, I hate radials. I really, really do ..
		fun rotationStuff(degree: Int, x: Int, y: Int) : Pair<Int, Int> {
			val degree_ = Math.toRadians(degree.toDouble())
			val x_ =  (x * cos(degree_).toInt())  - (sin(degree_).toInt() * y)
			val y_ =  (x * sin(degree_).toInt())  + (cos(degree_).toInt() * y)
			return Pair(x_, y_)
		}

		fun step(instruction: String) {
			val distance = instruction.drop(1).toInt()
			val directionInstruction = instruction[0]

			if (steeringDirections.contains(directionInstruction)) {
				val degree = distance * (if (directionInstruction == 'R') -1 else 1)
				wayPointLocation  = rotationStuff(degree, wayPointLocation.first, wayPointLocation.second)
			} else {
				when (directionInstruction) {
					'N' -> wayPointLocation += Pair(0, distance)
					'S' -> wayPointLocation += Pair(0, -distance)
					'E' -> wayPointLocation += Pair(distance, 0)
					'W' -> wayPointLocation += Pair(-distance, 0)
					'F' -> location += (wayPointLocation * distance)
				}
			}
		}

		fun run() {
			navigationInstractions.forEach { step(it) }
		}

		fun distance() = location.abs()
	}

	private fun solve1(totalFile: String) {
		val bufferedReader = BufferedReader(InputStreamReader(Aoc2020ApplicationDay12::class.java.getResourceAsStream("/$totalFile")))
		bufferedReader.useLines {
				val naviationState = NavigationState(it.toMutableList())
				naviationState.run()
				logger.info("${naviationState.distance()} is the Manhattan distance between that location and the ship's starting position")
		}
	}

	private fun solve2(totalFile: String) {
		val bufferedReader = BufferedReader(InputStreamReader(Aoc2020ApplicationDay12::class.java.getResourceAsStream("/$totalFile")))
		bufferedReader.useLines {
			val waypointNavigationState = WaypointNavigationState(it.toMutableList())
			waypointNavigationState.run()
			logger.info("${waypointNavigationState.distance()} is the Manhattan distance between that location and the ship's starting position")
		}
	}

	override fun run(vararg args: String?) {
		solve1(args[0]!!)
		solve2(args[0]!!)
	}
}

fun main(args: Array<String>) {
	runApplication<Aoc2020ApplicationDay12>(*args)
}
