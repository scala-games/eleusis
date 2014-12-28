package eleusis.game

import scala.util.Random

case class Card(suit: Suit.Value, rank: Rank.Value) {
  override def toString = s"$rank$suit"
}

object Cards {
  lazy val deck: Set[Card] = for {
    s <- Suit.values
    r <- Rank.values
  } yield Card(s, r)

  def shuffledDecks(count: Int): List[Card] =
    Random.shuffle(List.fill(count)(deck.toList).flatten)
}

object Suit extends Enumeration {
  val HEARTS = Value("♥")
  val SPADES = Value("♤")
  val DIAMONDS = Value("♦")
  val CLUBS = Value("♧")
}

object Rank extends Enumeration {
  val ACE = Value("A")
  val TWO = Value("2")
  val THREE = Value("3")
  val FOUR = Value("4")
  val FIVE = Value("5")
  val SIX = Value("6")
  val SEVEN = Value("7")
  val EIGHT = Value("8")
  val NINE = Value("9")
  val TEN = Value("T")
  val JACK = Value("J")
  val QUEEN = Value("Q")
  val KING = Value("K")
}