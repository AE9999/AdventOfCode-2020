package com.ae.aoc2020.day22

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
class Aoc2020ApplicationDay22 : CommandLineRunner {

	private val logger = LoggerFactory.getLogger(javaClass)

	private fun parseDecks(input: Sequence<String>): List<ArrayDeque<Int>> {
		val bufferedInput = input.toList().drop(1)
		val cutOff = bufferedInput.indexOf("Player 2:")
		val decks : List<ArrayDeque<Int>> = listOf(ArrayDeque(), ArrayDeque())
		decks[0].addAll(bufferedInput.take(cutOff - 1).map { it.toInt() })
		decks[1].addAll(bufferedInput.drop(cutOff + 1).map { it.toInt() })
		return decks
	}

	private fun solve(file: String) {
		val bufferedReader = BufferedReader(InputStreamReader(Aoc2020ApplicationDay22::class.java.getResourceAsStream("/$file")))
		bufferedReader.useLines { input ->
			val decks = parseDecks(input)
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

	private fun deckToScore(deck: ArrayDeque<Int>) : Int =
		deck.withIndex().map { card -> (deck.size - card.index) * card.value }.sum()

	data class DeckState(val left: List<Int>,
	                     val right: List<Int>)

	private fun solve2H(decks: List<ArrayDeque<Int>>, depth: Int) : Pair<Boolean, Int> {
//		logger.info("Starting game #($depth) Working with ${DeckState(decks[0].toList(), decks[1].toList())}")
		val seenStates = HashSet<DeckState>()

		while (decks.filter { deck -> deck.isEmpty() }.isEmpty()) {
			val state = DeckState(decks[0].toList(), decks[1].toList())

			if (seenStates.contains(state)) {
//				logger.info("Found a stalmate at game #($depth) after ${seenStates.size} steps Player 1 wins..")
				return Pair(true, deckToScore(decks[0]))
			}
			seenStates.add(state)
			val cards = listOf(decks[0].pop(), decks[1].pop())
			val player1wins = if (cards[0] <= decks[0].size && cards[1] <= decks[1].size) {
				val decks_ : List<ArrayDeque<Int>> = listOf(ArrayDeque(), ArrayDeque())
				decks_[0].addAll(decks[0].take(cards[0]))
				decks_[1].addAll(decks[1].take(cards[1]))

				solve2H(decks_, depth + 1).first
			} else {
				(cards[0] > cards[1])
			}

			if (player1wins) {
				decks[0].add(cards[0])
				decks[0].add(cards[1])
			} else {
				decks[1].add(cards[1])
				decks[1].add(cards[0])
			}
		}
		val winningDeck = if (decks[0].isEmpty()) decks[1] else decks[0]
		val score = deckToScore(winningDeck)
//		logger.info("Game $depth ended with Payer ${winningDeck + 1} winning with a score of $score .. ")
		return Pair(decks[0].isNotEmpty(), score)
	}

	private fun solve2(file: String) {
		val bufferedReader = BufferedReader(InputStreamReader(Aoc2020ApplicationDay22::class.java.getResourceAsStream("/$file")))
		bufferedReader.useLines { input ->
			val decks = parseDecks(input)
			val score = solve2H(decks, 0)
			logger.info("${score.second} is the winning player's score")
		}
	}


	override fun run(vararg args: String?) {
//		solve(args[0]!!)
		solve2(args[0]!!)
	}
}


fun main(args: Array<String>) {
	runApplication<Aoc2020ApplicationDay22>(*args)
}
