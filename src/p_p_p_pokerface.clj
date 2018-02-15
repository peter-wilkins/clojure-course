(ns p-p-p-pokerface) ; credit to http://iloveponies.github.io/120-hour-epic-sax-marathon/p-p-p-pokerface.html

; Instructions: start repl: RUN -> Run REPL for 'p-p-p-pokerface'
; Load file into repl: TOOLS -> REPL -> Load file in REPL. Tip: learn the shortcuts.

; A form is clojure code in balanced brackets that we can run in the repl.
; You just loaded all the forms in the file into the repl so it knows about them all now.

; Sample data - this is how we are representing a poker hand. We define data with def.
(def high-seven ["2H" "3S" "4C" "5C" "7D"])
(def pair-hand ["2H" "2S" "4C" "5C" "7D"])
(def two-pairs-hand ["2H" "2S" "4C" "4D" "7D"])
(def three-of-a-kind-hand ["2H" "2S" "2C" "4D" "7D"])
(def four-of-a-kind-hand ["2H" "2S" "2C" "2D" "7D"])
(def straight-hand ["2H" "3S" "6C" "5D" "4D"])
(def low-ace-straight-hand ["2H" "3S" "4C" "5D" "AD"])
(def high-ace-straight-hand ["TH" "AS" "QC" "KD" "JD"])
(def flush-hand ["2H" "4H" "5H" "9H" "7H"])
(def full-house-hand ["2H" "5D" "2D" "2C" "5S"])
(def straight-flush-hand ["2H" "3H" "6H" "5H" "4H"])
(def low-ace-straight-flush-hand ["2D" "3D" "4D" "5D" "AD"])
(def high-ace-straight-flush-hand ["TS" "AS" "QS" "KS" "JS"])

; Put cursor on closing bracket of the form on line 34 and send it to the repl TOOLS -> REPL -> Send ...
; This form calls the function defined on line 30 with the list defined on line 10 as the argument.
; You should see '=> true' printed in the repl window. The function behaves as expected.
; Change the value from true to false and send it again. Notice the output is still true.
; Send the high-card? function to the repl in the same way and try again. Better. Change it back to true and run it again.

(defn high-card? [hand]
  "Returns true if the hand contains a card"
  true)

(high-card? high-seven) ;=> true

; Send the form on line 44. We get nil. Replace nil on line 42 with a form that works. Go read the docs:
; https://clojuredocs.org/clojure.core/second
; https://clojuredocs.org/clojure.core/str

(defn suit [hand]
  "Returns the suit of a card as a string. One of H, D, S or C"
  nil)

(suit "2H") ;=> "H"
(suit "2D") ;=> "D"
(suit "2C") ;=> "C"
(suit "3S") ;=> "S"

(defn rank [hand]
  "Returns the value of a card as a number between 2 and 14. Ace scores high (14)"
  nil)

(rank "2H") ;=> 2
(rank "4S") ;=> 4
(rank "TS") ;=> 10
(rank "JS") ;=> 11
(rank "QS") ;=> 12
(rank "KS") ;=> 13
(rank "AS") ;=> 14

(def shapes [:zeros :singles :pairs :threes :fours :fives])

(defn shape [hand]
  (->> hand
       (map rank)
       (frequencies)
       (vals)
       (frequencies)
       (map (fn [[k v]][(get shapes k) v ]))
       (flatten)
       (apply hash-map)
       ))

(shape pair-hand)

(defn pair? [hand]
  (= {:pairs 1, :singles 3} (shape hand)))

(pair? pair-hand)  ;=> true
(pair? high-seven) ;=> false

(defn three-of-a-kind? [hand]
  nil)

(three-of-a-kind? two-pairs-hand)       ;=> false
(three-of-a-kind? three-of-a-kind-hand) ;=> true

(defn four-of-a-kind? [hand]
  nil)

(four-of-a-kind? two-pairs-hand)      ;=> false
(four-of-a-kind? four-of-a-kind-hand) ;=> true

(defn full-house? [hand]
  nil)

(full-house? three-of-a-kind-hand) ;=> false
(full-house? full-house-hand)      ;=> true

(defn two-pairs? [hand]
  nil)

(two-pairs? two-pairs-hand)      ;=> true
(two-pairs? pair-hand)           ;=> false
(two-pairs? four-of-a-kind-hand) ;=> true

(defn straight? [hand]
  nil)

(straight? two-pairs-hand)             ;=> false
(straight? straight-hand)              ;=> true
(straight? low-ace-straight-hand)      ;=> true
(straight? ["2H" "2D" "3H" "4H" "5H"]) ;=> false
(straight? high-ace-straight-hand)     ;=> true

(defn flush? [hand]
  nil)

(flush? pair-hand)  ;=> false
(flush? flush-hand) ;=> true

(defn straight-flush? [hand]
  nil)

(straight-flush? straight-hand)                ;=> false
(straight-flush? flush-hand)                   ;=> false
(straight-flush? straight-flush-hand)          ;=> true
(straight-flush? low-ace-straight-flush-hand)  ;=> true
(straight-flush? high-ace-straight-flush-hand) ;=> true

(defn value [hand]
  (let [checkers #{[high-card? 0] [pair? 1]
                   [two-pairs? 2] [three-of-a-kind? 3]
                   [straight? 4] [flush? 5]
                   [full-house? 6] [four-of-a-kind? 7]
                   [straight-flush? 8]}]
    nil))

(value high-seven)                                          ;=> 0
(value pair-hand)                                           ;=> 1
(value two-pairs-hand)                                      ;=> 2
(value three-of-a-kind-hand)                                ;=> 3
(value straight-hand)                                       ;=> 4
(value flush-hand)                                          ;=> 5
(value full-house-hand)                                     ;=> 6
(value four-of-a-kind-hand)                                 ;=> 7
(value straight-flush-hand)                                 ;=> 8

; Congrats! You made it here. Now run the tests to check your work.
; To Run all tests paste these forms in REPL console (the window below the repl):
;    (use 'midje.repl) => nil
;    (load-facts 'p-p-p-pokerface-test)