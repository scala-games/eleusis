package eleusis.game

import scala.util.Random

case class Card(suit: Suit, rank: Rank.Value) {
  override def toString = s"$rank$suit"

  def color: Color.Value = suit.color

  def toCardString = CardString(suit.name, rank.toString)
}

case class CardString(suit: String, rank: String)

object Cards {
  def apply(cardsString: String): List[Card] =
    for {
      cs <- cardsString.split(" ").toList
      Seq(r, s) = cs.toList
      rank = Rank.withName(r)
      suit <- Suit.withName(s.toString)
    } yield Card(suit, rank)

  lazy val deck: Set[Card] = (for {
    s <- Suit.all
    r <- Rank.values
  } yield Card(s, r)).toSet

  def shuffledDecks(count: Int): List[Card] =
    Random.shuffle(List.fill(count)(deck.toList).flatten)
}

case class Suit(name: String, symbol: String, color: Color.Value)

object Suit {
  val HEARTS = Suit("h", "♥", Color.RED)
  val SPADES = Suit("s", "♤", Color.BLACK)
  val DIAMONDS = Suit("d", "♦", Color.RED)
  val CLUBS = Suit("c", "♧", Color.BLACK)
  val all = Seq(HEARTS, SPADES, DIAMONDS, CLUBS)
  def withName(n: String) = all.find(_.name == n)
}

object Color extends Enumeration {
  val RED, BLACK = Value
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

  def withName(s: Char): Rank.Value = s match {
    case '1' => withName("A")
    case _   => withName(s.toString)
  }
}