package com.ae.aoc2020.day21

import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.RuntimeException
import kotlin.math.cos
import kotlin.math.log
import kotlin.math.sin

@SpringBootApplication
class Aoc2020ApplicationDay21 : CommandLineRunner {

	private val logger = LoggerFactory.getLogger(javaClass)


	private fun solve(file: String) {
		val bufferedReader = BufferedReader(InputStreamReader(Aoc2020ApplicationDay21::class.java.getResourceAsStream("/$file")))
		bufferedReader.useLines { input ->
		}
	}

	override fun run(vararg args: String?) {
		solve(args[0]!!)
	}
}

fun main(args: Array<String>) {
	runApplication<Aoc2020ApplicationDay21>(*args)
}
