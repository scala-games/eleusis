package eleusis.game

import scala.util.Random

case class Card(suit: Suit.Value, rank: Rank.Value) {
  override def toString = s"$rank$suit"

  def color: Color.Value = suit.color
}

object Cards {
  def apply(cardsString: String): List[Card] =
    for {
      cs <- cardsString.split(" ").toList
      Seq(r, s) = cs.toList
      rank = Rank.withName(r)
      suit = Suit.withName(s)
    } yield Card(suit, rank)

  lazy val deck: Set[Card] = for {
    s <- Suit.values
    r <- Rank.values
  } yield Card(s, r)

  def shuffledDecks(count: Int): List[Card] =
    Random.shuffle(List.fill(count)(deck.toList).flatten)
}

object Suit extends Enumeration {
  import Color._
  val HEARTS = Value("♥")
  val SPADES = Value("♤")
  val DIAMONDS = Value("♦")
  val CLUBS = Value("♧")

  def withName(s: Char): Suit.Value = s match {
    case 'h' | 'H' => withName(HEARTS.toString)
    case 's' | 'S' => withName(SPADES.toString)
    case 'd' | 'D' => withName(DIAMONDS.toString)
    case 'c' | 'C' => withName(CLUBS.toString)
    case _ => withName(s.toString)
  }

  implicit class SuitValue(suit: Value) {
    def color: Color.Value = suit match {
      case CLUBS | SPADES => BLACK
      case _ => RED
    }
  }
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
    case _ => withName(s.toString)
  }
}