(ns tea-tray-sync
  (:require [clojure.core.async :as async]
            [clojure.core.reducers :as r]))
; make a channel with a buffer size of 1
(def a-channel (async/chan 1))

; put a hello on the channel - notice the repl the doesn't reply, how rude!
(async/>!! a-channel "Hello, world!")

; remember the buffer. Now take from the channel...
(async/<!! a-channel)


(def s (range 0 9999999))
(def sv (vec s))
(time (reduce
        +
        0
        (map inc
             (filter even? sv))))
(time (r/fold
        +
        (r/map inc
               (r/filter even? sv))))

(time (reduce (fn [acc item] (if (even? item)
                               (+ acc (inc item))
                               acc))
              0 sv))

(time (reduce ((comp (filter even?) (map inc)) +) 0 sv))

(defn square [x] (* x x))

(def xform
  (comp
    (filter even?)
    (filter #(< % 10))
    (map square)
    (map inc)))

(def c (async/chan 1 xform))

(async/go
  (async/onto-chan c [5 6 8 12 15]))  ; like calling >!! on every item in sequence
(loop [n (async/<!! c)]
  (when n
    (println n)
    (recur (async/<!! c))))