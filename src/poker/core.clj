(ns poker.core
  (:require [clojure.string :as str])
  (:gen-class))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;; Helpers
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn value [card]
  (and card
       (condp = (first card) \T 10 \J 11 \Q 12 \K 13 \A 14 (Integer/parseInt (str (first card))))))
(defn suit [card] (second card))

(defn of-a-kind [num hand]
  (->> (group-by value hand)
       vals
       (filter #(= num (count %)))
       (sort-by (comp value first))
       reverse))
(defn same-suit? [hand] (apply = (map suit hand)))
(defn consecutive? [[a b]] (= a (dec b)))
(defn consecutive-values? [hand]
  (->> (sort-by value hand)
       (map value)
       (partition 2 1)
       (map consecutive?)
       (every? identity)))
(defn of-a-kind-value[of-a-kind-result] (value (first (first of-a-kind-result))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;; Hand types - each returns highest value
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn high-card [hand] (value (last (sort-by value hand))))
(defn pairs [hand] (of-a-kind-value (of-a-kind 2 hand)))
(defn two-pairs [hand]
  (let [pairs (of-a-kind 2 hand)]
    (and (= (count pairs) 2)
         (of-a-kind-value pairs))))
(defn three-of-a-kind [hand] (of-a-kind-value (of-a-kind 3 hand)))
(defn four-of-a-kind [hand] (of-a-kind-value (of-a-kind 4 hand)))
(defn straight [hand] (and (consecutive-values? hand) (high-card hand)))
(defn flush-hand [hand] (and (same-suit? hand) (high-card hand)))
(defn full-house [hand] (and (pairs hand) (three-of-a-kind hand)))
(defn straight-flush [hand]
  (and (same-suit? hand)
       (consecutive-values? hand)
       (high-card hand)))

(def types [high-card pairs two-pairs three-of-a-kind straight flush-hand full-house four-of-a-kind straight-flush])

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;; Calc winning hand
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn array-value-to-value[[hand-type-index value]] (+ (* 100 hand-type-index) value))
(defn hand-value [hand]
  (->> (map-indexed (fn[index hand-type] [index (hand-type hand)])
                    types)
       (filter last)
       last
       array-value-to-value))
(defn winning-hand [hand1-str hand2-str]
  (let [value1 (hand-value (str/split hand1-str #" "))
        value2 (hand-value (str/split hand2-str #" "))]
    (cond (> value1 value2) 1
          (< value1 value2) 2
          :else 0)))

(defn -main
  [& args]
  (println "Winning hand: "
           (winning-hand "2H 3D 5S 9C KD"
                         "2H 3D 5S 9C KD")))
