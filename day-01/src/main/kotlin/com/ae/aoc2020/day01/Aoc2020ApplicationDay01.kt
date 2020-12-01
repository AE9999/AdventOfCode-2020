package com.ae.aoc2019.day01

import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.io.BufferedReader
import java.io.InputStreamReader


@SpringBootApplication
class Aoc2020ApplicationDay01 : CommandLineRunner {

	private val logger = LoggerFactory.getLogger(javaClass)

	override fun run(vararg args: String?) {
		logger.info("Hello World")
	}
}

fun main(args: Array<String>) {
	runApplication<Aoc2020ApplicationDay01>(*args)
}
