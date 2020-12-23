package com.ae.aoc2020.day23

import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.RuntimeException
import java.util.*
import kotlin.collections.HashMap
import kotlin.collections.HashSet
import kotlin.math.log

@SpringBootApplication
class Aoc2020ApplicationDay23 : CommandLineRunner {

	private val logger = LoggerFactory.getLogger(javaClass)

	private fun solve(file: String) {
		val bufferedReader = BufferedReader(InputStreamReader(Aoc2020ApplicationDay23::class.java.getResourceAsStream("/$file")))
		bufferedReader.useLines { input ->
		}
	}

	override fun run(vararg args: String?) {
//		solve(args[0]!!)
	}
}


fun main(args: Array<String>) {
	runApplication<Aoc2020ApplicationDay23>(*args)
}
