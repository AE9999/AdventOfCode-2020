package com.ae.aoc2020.day25

import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.RuntimeException

@SpringBootApplication
class Aoc2020ApplicationDay25 : CommandLineRunner {

	private val logger = LoggerFactory.getLogger(javaClass)


	fun transform(subjectNumber_: Long, loopSize: Long) : Long {
		var rvalue = 1L
		(0 until loopSize).forEach {
			rvalue *= subjectNumber_
			rvalue %= 20201227
		}
		return rvalue
	}

	private fun reverseKey(key: Long, subjectNumber: Long) : Long {
		//20201227
		(1..20201227).map { it.toLong() }.forEach { loopSize ->
			val transformed = transform(subjectNumber, loopSize)
			logger.info("Attempting loopsize: $loopSize => $transformed")
			if (transformed == key)  {
				logger.info("Found: $loopSize")
				return loopSize
			}
		}
		throw RuntimeException("Could not break doorKey ..")
	}

	private fun solve(file: String) {
		val bufferedReader = BufferedReader(InputStreamReader(Aoc2020ApplicationDay25::class.java.getResourceAsStream("/$file")))

		bufferedReader.useLines { input ->
			val it = input.iterator()
			val cardPubKey = it.next().toLong()
			val doorPubKey = it.next().toLong()
			val cardLoopSize = reverseKey(cardPubKey, 7)
			val doorLoopSize = reverseKey(doorPubKey, 7)
			val cardAnswer = transform(doorPubKey, cardLoopSize)
			val doorAnswer = transform(cardPubKey, doorLoopSize)
			if (cardAnswer == doorAnswer) {
				logger.info("We calculated correctly encryption key $cardAnswer")
			} else {
				logger.info("We calculated something wrong $cardAnswer vs $doorAnswer")
			}
		}
	}

	override fun run(vararg args: String?) {
		solve(args[0]!!)
	}
}


fun main(args: Array<String>) {
	runApplication<Aoc2020ApplicationDay25>(*args)
}
