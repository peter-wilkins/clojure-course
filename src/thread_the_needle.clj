(ns thread-the-needle)

; Note: The word "thread" in this context (meaning passing a value through a pipeline of functions) is unrelated to the concept of concurrent threads of execution.

; Instructions: start repl: RUN -> Run REPL for 'p-p-p-pokerface'
; Load file into repl: TOOLS -> REPL -> Load file in REPL. Tip: learn the shortcuts.

; A form is clojure code in balanced brackets that we can run in the repl.
; To evaluate a form put cursor on closing bracket of the following form on line 12 and send it to the repl TOOLS -> REPL -> Send ...

; How quickly can you solve the following sum in your head? Send this form to the repl.
(+ (* (- (/ 20 5) 2) 3) 12)

; same sum, with the thread first macro ->
(-> 20                                                      ; start with 20
    (/ 5)                                                   ; divide by 5
    (- 2)                                                   ; minus 2
    (* 3)                                                   ; multiply by 3
    (+ 12)
    )                                                 ; finally add 12

; here again with comma's marking where the values are threaded (commas are whitespace in clojure)
(-> 20
    (/ , 5)
    (- , 2)
    (* , 3)
    (+ , 12)
    )
; If you put the final bracket(s) on the next line you can quickly comment out parts of the pipeline (with a ;) to see the values at each step in the repl - try it now.

; the thread last macro ->> threads result is threaded as the last argument.
; Run theses to see if the following pairs of forms have the same results?
(-> 10
    (* , 3)
    (+ , 12)
    )
(->> 10
     (* 3 ,)
     (+ 12 ,)
     ) ; Yes because add and multiply are associative
       ; the order of the arguments don't matter

(-> 21
    (/ , 3)
    (- , 2)
    )
(->> 21
     (/ 3 ,)
     (- 2 ,)
     )   ; divide and minus are not associative and the argument order does matter

; clojure collection functions take the collection as the last argument
(apply + (filter odd? [1 2 3 4 5 6 7 8 9]))

(->> [1 2 3 4 5 6 7 8 9]
     (filter odd? ,)
     (apply + ,)
     )

; The power of pipelines comes from writing code with only one path.
; Avoiding branching makes code straightforward (literally) and easy to reason about.

; Poker example - is this hand a Straight flush with a low ace?
; uncomment each stage in the pipeline in turn and send it to the repl to see how the values transform from a list to a boolean (without using if/else)
(->> ["2H" "3S" "4C" "5D" "AD"]
     (map first)                                            ; get the first character from each item in the list
     ;(replace {\A \1})                                      ; replace the A with a 1
     ;(map (fn [c] (Integer/valueOf (str c))))               ; cast each character to its number value
     ;(sort)
     ;(partition 2 1)                                        ; turn the list of numbers into a list of pairs of sequential numbers
     ;(map (fn [[a b]] (= (inc a) b)))                       ; turn each pair into a boolean, true if the pair is contiguous
     ;(every? true?)                                         ; reduce the list of bools to a single bool using the true? function
     )

; exercise: write a pipeline that returns true is all cards are hearts,
(->> ["2H" "3H" "4H" "5H" "AH"]
     )

; hint: if you are stuck read clojure docs for the functions second and every
; https://clojuredocs.org/clojure.core/second

; extra credit: read about as->, some->, some->> and cond-> at https://clojure.org/guides/threading_macros


(-> {:counter "10"}
    :counter
    (Integer/parseInt)
    inc)

(comment
  (-> {}
      :counter
      (Integer/parseInt) ; null pointer exception!
      inc)
  )

(some-> {}
        :counter
        (Integer/parseInt) ; nil
        inc)