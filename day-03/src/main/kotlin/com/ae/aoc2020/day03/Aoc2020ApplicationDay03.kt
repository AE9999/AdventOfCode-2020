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


	override fun run(vararg args: String?) {
		solve1(args[0]!!)
//		solve2(args[0]!!)
	}
}

fun main(args: Array<String>) {
	runApplication<Aoc2020ApplicationDay03>(*args)
}
