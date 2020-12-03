package com.ae.aoc2019.day03

import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.io.BufferedReader
import java.io.InputStreamReader


@SpringBootApplication
class Aoc2020ApplicationDay03 : CommandLineRunner {

	private val logger = LoggerFactory.getLogger(javaClass)

	class LocalGeology(val map: Map<Int, String>) {
		private fun mySize(): Int = map[0]!!.length

		fun isTree(x: Int, y: Int) : Boolean =
			map[y]!![x % mySize()] == '#'

		fun height() = map.size
		
		fun slopeCostYinc(xInc: Int) = (0 until height()).map {
			y ->
			if (isTree(y * xInc, y))
				1
			else 0
		}.sum()

		fun sloperCostXinc(yInc: Int) = (0 until height() / yInc).map {
			x ->
			if (isTree(x, x * yInc))
				1
			else 0
		}.sum()
	}

	private fun solve1(totalFile: String) {
		val bufferedReader = BufferedReader(InputStreamReader(Aoc2020ApplicationDay03::class.java.getResourceAsStream("/$totalFile")))
		val map = HashMap<Int, String>()
		bufferedReader.useLines {
			it.withIndex().forEach {line ->
				map[line.index] = line.value
			}
			val localGeology = LocalGeology(map)

			val ntrees = (0 until  localGeology.height()).map {
				y ->
				    if (localGeology.isTree(y * 3, y))
							1
						 else 0
		  }.sum()

			logger.info("I went by $ntrees trees on my way to the airport")
		}
	}

	private fun solve2(totalFile: String) {
		val bufferedReader = BufferedReader(InputStreamReader(Aoc2020ApplicationDay03::class.java.getResourceAsStream("/$totalFile")))
		val map = HashMap<Int, String>()
		bufferedReader.useLines {
			it.withIndex().forEach {line ->
				map[line.index] = line.value
			}
			val localGeology = LocalGeology(map)
			val xslopes = listOf(1, 3, 5, 7)
			val value : Int = xslopes.map { localGeology.slopeCostYinc(it) }.reduce { acc, i ->  acc * i }
			logger.info("The final outcome = ${value * (localGeology.sloperCostXinc(2))}")
		}
	}


	override fun run(vararg args: String?) {
		solve1(args[0]!!)
		solve2(args[0]!!)
	}
}

fun main(args: Array<String>) {
	runApplication<Aoc2020ApplicationDay03>(*args)
}
