(ns poker.core-test
  (:use midje.sweet)
  (:require
   [clojure.test :refer :all]
   [poker.core :refer :all]))

(facts "about high cards"
  (fact "highest value card is returned"
    (high-card ["2H" "3D" "9H" "QD" "7S"]) => 12))

(facts "about pairs"
  (fact "two pairs of same value return highest value of tha card"
    (pairs ["2H" "2A" "3D"]) => 2
    (pairs ["2H" "2A"]) => 2
    (pairs ["2H" "2D" "AH" "AS"]) => 14
    (pairs ["2H" "2A" "2S"]) => falsey
    (pairs ["3H" "2A"]) => falsey))

(facts "about two pairs"
  (fact "hand contains two sets of pairs returning the highest value"
    (two-pairs ["8S" "8D" "AH" "AS" "2S"]) => 14
    (two-pairs ["2H" "2A"]) => falsey
    (two-pairs ["2H" "4A" "9D" "7S" "3H"]) => falsey))

(facts "about three of a kind"
  (fact "three of the same number returns matching value of card"
    (three-of-a-kind ["8H" "8D" "2H" "2A" "2S"]) => 2
    (three-of-a-kind ["8H" "8D" "3H" "2A" "2S"]) => falsey))

(facts "about four of a kind"
  (fact "four of the same number returns matching value of card"
    (four-of-a-kind ["8D" "8H" "8S" "8C" "2S"]) => 8
    (four-of-a-kind ["8H" "8H" "3H" "2A" "2S"]) => falsey))

(facts "about straight"
  (fact "hand contains 5 cards with consecutive values; hands which both contain a straight are ranked by their highest card"
    (straight ["3D" "4H" "5D" "6D" "7D"]) => 7
    (straight ["4H" "5D" "6D" "7D" "8D"]) => 8
    (straight ["8D" "4H" "5D" "6D" "7D"]) => 8
    (straight ["2H" "5D" "6D" "7D" "8D"]) => falsey))

(facts "about flush"
  (fact "hand contains 5 cards of the same suit; hands which are both flushes are ranked using the rules for High Card."
    (flush-hand ["2D" "5D" "6D" "7D" "8D"]) => 8
    (flush-hand ["2S" "5D" "6D" "7D" "8D"]) => falsey))

(facts "about full house"
  (fact "3 cards of the same value, with the remaining 2 cards forming a pair. Ranked by the value of the 3 cards."
    (full-house ["5D" "5S" "4D" "4S" "4H"]) => 4
    (full-house ["5D" "5S" "6D" "4S" "4H"]) => falsey))

(facts "about straight flush"
  (fact "5 cards of the same suit with consecutive values. Ranked by the highest card in the hand."
    (straight-flush ["7D" "6D" "5D" "4D" "3D"]) => 7))

(facts "about winning hands"
  (fact "full house beats flush"
    (winning-hand "5D 5S 4D 4S 4H"
                  "2D 5D 6D 7D 8D") => 1)

  (fact "straight flush beats high-card"
    (winning-hand "7D 6D 5D 4D 3D" "2H 3D 9H QD 7S") => 1
    (winning-hand "2H 3D 9H QD 7S" "7D 6D 5D 4D 3D") => 2)

  (fact "exact same hand will result in a draw"
    (winning-hand "7D 6D 5D 4D 3D" "7D 6D 5D 4D 3D") => 0)

  (fact "hand type being equal highest card wins"
    (winning-hand "5D 5S 4D 4S 4H" "5D 5S 6D 6S 6H") => 2))
