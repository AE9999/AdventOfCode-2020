package com.ae.aoc2020.day22

import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.RuntimeException
import java.util.*

@SpringBootApplication
class Aoc2020ApplicationDay22 : CommandLineRunner {

	private val logger = LoggerFactory.getLogger(javaClass)

	private fun solve(file: String) {
		val bufferedReader = BufferedReader(InputStreamReader(Aoc2020ApplicationDay22::class.java.getResourceAsStream("/$file")))
		bufferedReader.useLines { input ->
			val bufferedInput = input.toList().drop(1)
			val cutOff = bufferedInput.indexOf("Player 2:")
			val decks = listOf(ArrayDeque<Int>(), ArrayDeque<Int>())
			decks[0].addAll(bufferedInput.take(cutOff - 1).map { it.toInt() })
			decks[1].addAll(bufferedInput.drop(cutOff + 1).map { it.toInt() })
			while(decks.filter { deck -> deck.isEmpty() }.isEmpty()) {
				val cards = listOf(decks[0].pop(), decks[1].pop())
				if (cards[0] > cards[1]) {
					decks[0].add(cards[0])
					decks[0].add(cards[1])
				} else {
					decks[1].add(cards[1])
					decks[1].add(cards[0])
				}
			}
			val winningDeck = if (decks[0].isEmpty()) decks[1] else decks[0]
			val score = winningDeck.withIndex().map { card -> (winningDeck.size - card.index) * card.value }.sum()
			logger.info("$score is the winning player's score")
		}
	}

	override fun run(vararg args: String?) {
		solve(args[0]!!)
	}
}

fun main(args: Array<String>) {
	runApplication<Aoc2020ApplicationDay22>(*args)
}
