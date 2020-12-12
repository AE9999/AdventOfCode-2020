package com.ae.aoc2020.day12

import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.RuntimeException
import kotlin.math.abs

@SpringBootApplication
class Aoc2020ApplicationDay12 : CommandLineRunner {

	private val logger = LoggerFactory.getLogger(javaClass)

	data class NavigationState(val navigationInstractions: List<String>) {
		var direction = 'E'
		val steeringDirections = listOf('L', 'R')
		var myX = 0
		var myY = 0

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
			navigationInstractions.forEach {
				step(it)
			}
		}

		fun distance() : Int {
			return abs(myX) + abs(myY)
		}

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
