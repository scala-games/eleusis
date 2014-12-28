package eleusis.game

import GameState._
import Suit._

case class GameState(
  players: List[Player],
  rule: EleusisRule,
  turns: List[Turn], // last turn is head
  deck: List[Card],
  prophet: Option[Player] = None) {

  lazy val acceptedCards: List[Card] = turns.map(_.accepted)

  def accepts(cards: Card*): Boolean =
    rule(cards.toList ::: acceptedCards)

  def play(cards: Card*): GameState =
    if (accepts(cards: _*))
      copy(turns = cards.toList.map(Turn(_)) ::: turns)
    else
      copy(turns = turns.head.reject(cards: _*) :: turns.tail)

  def dealCards(player: Player, count: Int): GameState = {
    val cards = deck.take(count)
    updatePlayer(player, p => p.copy(hand = p.hand ::: cards)).
      copy(deck = deck.drop(count))
  }

  def updatePlayer(id: Player, update: Player => Player): GameState = {
    val (List(player), others) = players.partition(_.name == id.name)
    copy(players = update(player) :: others)
  }
}

object GameState {
  //expects last card as head
  type EleusisRule = List[Card] => Boolean

  val decksCount = 4
  val initialHand = 14

  val rule: EleusisRule = {
    case a :: b :: tail => a.color != b.color
    case _ => true
  }

  def apply(names: List[String]): GameState = {
    val deck = Cards.shuffledDecks(decksCount)
    val players = names.map(Player(_))
    val Some(firstCard) = deck.find(c => rule(List(c))).orElse(throw new RuntimeException("Could not find initial card for rule"))
    val turn0 = Turn(firstCard)
    var state = GameState(players, rule, List(turn0), deck) // TODO: remove initial card from deck
    for (p <- players) {
      state = state.dealCards(p, initialHand)
    }
    state
  }
}

case class Turn(accepted: Card, rejectedAfter: List[List[Card]] = Nil) {
  def reject(cards: Card*): Turn =
    copy(rejectedAfter = cards.toList :: rejectedAfter)
}

case class Player(
  name: String,
  hand: List[Card] = Nil,
  prophetStartTurn: Option[Int] = None)
