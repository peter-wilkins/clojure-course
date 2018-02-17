(ns specter-gadget
  (:require [com.rpl.specter :refer :all]))

; Nested maps like this is very common these days.
(def m {:username "sally"
        :profile  {:name    "Sally Clojurian"
                   :likes   3
                   :address {:city "Austin" :state "TX"}}})

; get-in allows us to pull data out by a list of keys and provide a default if not found
(get-in m [:profile :name])
(get-in m [:profile :address :city])
(get-in m [:profile :address :zip-code])
(get-in m [:profile :address :zip-code] "no zip code!")

; with assoc-in we can change or add values
; Note that data is immutable in clojure - the original will not be updated in this case
(assoc-in m [:profile :address :city] "Norwich")
(assoc-in m [:profile :address :zip-code] "1234")

; we can also update a value with a function:
(update-in m [:profile :likes] inc)
; the function inc adds 1 to the :likes value in the returned structure

; We can also use these functions on nested vectors using their indexes as keys (0 indexed)
(def v [[1 2 3] [4 5 6] [7 8 9]])
(get-in v [2 1])
(assoc-in v [2 2] 100)
(update-in v [2 2] dec)

; practise - use get-in to return 7 from my-map
(def my-map {:a [1 2 :b {:c [4 5 {:d "e" :f [6 7 8]}]}]})
(get-in my-map [:a 3 :c 2 :f 1])

; Navigation with Specter
; A problem in the real world is we don't know index of the data we want.
; We might also want to get or change more than one piece of data at a time.
; And it would be nice if we could use functions to find data by certain criteria
(def users [{:username "sally" :profile {:name "Sal" :likes 3 :address {:city "Austin" :state "TX"}}}
            {:username "jeff" :profile {:name "Big J" :likes 33 :address {:city "London" :state "NR"}}}
            {:username "pete" :profile {:name "Pete" :likes 29 :address {:city "Norwich" :state "PJ"}}}
            {:username "bob" :profile {:name "Rob" :likes 0 :address {:city "Paris" :state "XX"}}}])

; You can navigate to an element or to a sequence
(select [FIRST :profile :name] users)                       ; select the first users name
(select [ALL :profile :name] users)                         ; select all user names
(select [ALL :profile (collect :name) :likes (pred> 10)] users) ; only selects the users with more than 10 likes
(select [ALL :profile :address MAP-VALS] users)             ; only the address values (no keys)

; practise - use select to return [7] from mv
(select [:a 3 :c 2 :f 1] my-map)

; select the profile of users who live in London. pred= will select values that match the argument
(select [ALL (collect :profile) :profile :address :state (pred= "TX")] users)

; get jeff's state with specter:
(select [ALL #(= (:username %) "jeff") :profile :address :state] users)

; compared to using a pipeline:
(as-> users u
      (filter #(= (:username %) "jeff") u)
      (first u)
      (get-in u [:profile :address :state]))

; this is useful: https://github.com/nathanmarz/specter/wiki/Cheat-Sheet

; increment every users likes: (transform [PATH] function data)
(transform [ALL :profile :likes] inc users)

; Transform each key of users map to a string
(transform [ALL MAP-KEYS] name users)

; Transform each value of users addresses map to a keyword
(transform [ALL :profile :address MAP-VALS] keyword users)

; Add :popular true to a profile if a users likes are over 10.
; Combine (setval [PATH] value data) and  (selected? :likes #(> % 10))
(setval [ALL :profile (selected? :likes #(> % 10)) :popular] true users)

; credit https://www.youtube.com/watch?v=mXZxkpX5nt8&t=106s
(def world
  {:people [{:money 129827 :name "Alice Brown"}
            {:money 100 :name "John Smith"}
            {:money 6821212339 :name "Donald Trump"}
            {:money 2870 :name "Charlie Johnson"}
            {:money 8273821 :name "Charlie Rose"}
            ]
   :bank   {:funds 4782328748273}}
  )
; this function prints the world data structure and then the new value
(defn print-results [val]
  (println " ")
  (pprint world)
  (println "->")
  (pprint val)
  (println " "))

; this function demonstrates clojure without specter
; it can only send money from a user to the bank
(defn user->bank [world name amt]
  "send amt money from a user to the bank"
  (let [curr-funds (->> world
                        :people
                        (filter (fn [user] (= (:name user) name)))
                        first
                        :money
                        )]
    (if (< curr-funds amt)
      (throw (IllegalArgumentException. "Not enough funds!"))
      (-> world
          (update
            :people
            (fn [user-list]
              (mapv (fn [user]
                      (if (= (:name user) name)
                        (update user :money #(- % amt))
                        user
                        ))
                    user-list)))
          (update-in
            [:bank :funds]
            #(+ % amt))
          ))))
; the contents of the comment macro are not evaluated when loading file
; you can still send forms in comment to the repl
(comment
  (print-results
    (user->bank world "John Smith" 25))
  )

; this function uses specter for generic many to many transfers
; notice the 2nd and 3rd arguments to the function are PATHS
(defn transfer
  "generic many to many transfers, from-path and to-path are specter paths"
  [world from-path to-path amt]
  (let [givers (select from-path world)
        receivers (select to-path world)
        total-receive (* amt (count givers))
        total-give (* amt (count receivers))]
    (if (every? #(>= % total-give) givers)
      (->> world
           (transform from-path #(- % total-give))
           (transform to-path #(+ % total-receive))
           )
      (throw (IllegalArgumentException. "Not enough funds!"))
      )))

; example using the transfer function
(defn pay-fee [world]
  "transfer $1 from all users to the bank"
  (transfer world
            [:people ALL :money]  ; path to all user's balances
            [:bank :funds]        ; path to the banks balance
            1))

(comment
  (print-results
    (pay-fee world))
  )
; practise: complete the functions
(defn bank-give-dollar [world]
  "transfer $1 to all users from the bank"
  (transfer world
            [:bank :funds]
            [:people ALL :money]
            1)
  )

(comment
  (print-results
    (bank-give-dollar world))
  )

(defn pay-poor-fee [world]
  "transfer $50 from any user with balance below $3,000 to the bank"
  (transfer world
            [:people ALL :money #(< % 3000)]
            [:bank :funds]
            50)
  )

(comment
  (print-results
    (pay-poor-fee world))
  )

; hint: selected?
(defn rich-people [world]
  "return the names of all the people in world with more than $1,000,000,000"
  (select [:people
           ALL
           (selected? :money #(>= % 1000000000))
           :name]
          world))

(comment
  (print-results
    (rich-people world))
  )

(defn user [name]
  "returns the specter path required to get the map for a user by name"
  [:people
   ALL
   #(= (:name %) name)])

; use the user function and the transfer function
(defn transfer-users [world giver receiver amt]
  "transfers amt from giver to receiver"
  (transfer world
            [(user giver) :money]
            [(user receiver) :money]
            amt))

(comment
  (print-results
    (transfer-users world "Alice Brown" "John Smith" 10))
  )

; use the transfer and srange functions
(defn bank-loyal-bonus
  "Bank gives $5000 to earliest three users"
  [world]
  (transfer world
            [:bank :funds]
            [:people (srange 0 3) ALL :money]
            5000))

(comment
  (print-results
    (bank-loyal-bonus world))
  )

; hint setval and END
(defn add-person [world name]
  "adds a user to the world with zero balance"
  (setval [:people END]
          [{:money 0 :name name}]
          world)
  )

(comment
  (print-results
    (add-person world "Barry White"))
  )

(defn remove-person [world name]
  "removes map entry for users with provided name"
  (setval [:people ALL #(= name (:name %))]
          NONE
          world))

(comment
  (print-results
    (remove-person world "Charlie Rose"))
  )
; hint filterer and LAST
(defn bank-recent-charity-bonus
  "Bank gives $1000 to most recent person with less than 5000 dollars"
  [world]
  (transfer world
            [:bank :funds]
            [:people
             (filterer [:money #(< % 5000)])
             LAST
             :money]
            1000))

(comment
  (print-results
    (bank-recent-charity-bonus world))
  )

; hint setval and if-path
(defn mark-wealth-status [world]
  "adds :rich to user records with >= $100,000 otherwise :not-so-rich"
  (setval [:people
           ALL
           (if-path [:money #(>= % 100000)]
                    :rich
                    :not-so-rich)]
          true
          world))

(comment
  (print-results
    (mark-wealth-status world))
  )


; hint: transform and collect. Note that collected values are in a vector
(defn mark-wealth-status-bool [world]
      "adds ':rich true' to user records with >= $100,000 otherwise ':rich false'"
      (transform [:people ALL (collect :money) :rich]
                 (fn [[money] _] (if (>= money 100000) true false))
                 world))

(comment
  (print-results
    (mark-wealth-status-bool world))
  )

; a clojure function
(defn update-multi [m keys f]
      "updates values for given keys in map m with function f"
      (reduce (fn [m k] (update m k f)) m keys))

; use specter. hint: transform, submap and MAP-VALS
(defn update-multi* [m keys f]
      nil)

(update-multi {:a 500 :b 600 :c 700} [:a :b] inc)
(update-multi* {:a 500 :b 600 :c 700} [:a :b] inc)
