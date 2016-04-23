package eleusis.game

import scala.util.Random

case class Card(suit: Suit, rank: Rank) {
  override def toString = s"$rank$suit"

  def color: Color.Value = suit.color

  def toCardString = CardString(suit.name, rank.name)
}

case class CardString(suit: String, rank: String)

object Cards {
  def apply(cardsString: String): List[Card] =
    for {
      cs <- cardsString.split(" ").toList
      Seq(r, s) = cs.toList
      rank <- Rank.withSymbol(r)
      suit <- Suit.withName(s.toString)
    } yield Card(suit, rank)

  lazy val deck: Set[Card] = (for {
    s <- Suit.all
    r <- Rank.all
  } yield Card(s, r)).toSet

  def shuffledDecks(count: Int): List[Card] =
    Random.shuffle(List.fill(count)(deck.toList).flatten)
}

case class Suit(name: String, symbol: String, color: Color.Value)

object Suit {
  val HEARTS = Suit("Hearts", "♥", Color.RED)
  val SPADES = Suit("Spades", "♤", Color.BLACK)
  val DIAMONDS = Suit("Diamonds", "♦", Color.RED)
  val CLUBS = Suit("Clubs", "♧", Color.BLACK)
  val all = Seq(HEARTS, SPADES, DIAMONDS, CLUBS)
  def withName(n: String) = all.find(_.name == n)
}

object Color extends Enumeration {
  val RED, BLACK = Value
}

case class Rank(name: String, symbol: Char)

object Rank {
  val ACE = Rank("Ace", 'A')
  val TWO = Rank("Two", '2')
  val THREE = Rank("Three", '3')
  val FOUR = Rank("Four", '4')
  val FIVE = Rank("Five", '5')
  val SIX = Rank("Six", '6')
  val SEVEN = Rank("Seven", '7')
  val EIGHT = Rank("Eight", '8')
  val NINE = Rank("Nine", '9')
  val TEN = Rank("Ten", 'T')
  val JACK = Rank("Jack", 'J')
  val QUEEN = Rank("Queen", 'Q')
  val KING = Rank("King", 'K')

  val all = Seq(ACE, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, TEN, JACK, QUEEN, KING)
  def withName(n: String) = all.find(_.name == n)
  def withSymbol(s: Char) = all.find(_.symbol == s)
}