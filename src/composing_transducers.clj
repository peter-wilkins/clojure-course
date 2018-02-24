(ns composing-transducers
  (:require [clojure.xml :as xml]
            [com.rpl.specter :refer :all]))

; https://juxt.pro/blog/posts/xpath-in-transducers.html
(def doc (xml/parse "resources/some-xml.xml"))
(clojure.pprint/pprint doc)

; Challenge: get the string content from chapter elements with attr name = "Introduction"

; using thread last
(time (->> [doc]
           (mapcat :content)
           (filter #(= :chapter (:tag %)))
           (filter #(= "Introduction" (get-in % [:attrs :name])))
           (mapcat :content)
           (filter #(= :para (:tag %)))
           (mapcat :content)
           (filter string?)))

; transducers and compose
(time (eduction
        (comp
          (mapcat :content)
          (filter (comp (partial = :chapter) :tag))
          (filter (comp (partial = "Introduction") :attrs :name))
          (mapcat :content)
          (filter (comp (partial = :para) :tag))
          (mapcat :content)
          (filter string?))
        [doc]))
; common transducer contexts: transduce, sequence, into, eduction, async/chan, async/pipeline

; specter:
(time (select [:content
         ALL
         (selected? :tag (pred= :chapter))
         (selected? :attrs :name (pred= "Introduction"))
         :content
         ALL
         (selected? :tag (pred= :para))
         :content
         ALL
         string?]
        doc))

; Composing transducers
; here is a transducer that does the same as [:content ALL]
(def children (mapcat :content))
(eduction children [doc])

; predicate transducer for :tag key
(defn tagp [pred]
  (comp children (filter (comp pred :tag))))

; tag equality predicate
(defn tag= [tag]
  (tagp (partial = tag)))

; select all the chapter nodes
(eduction (tag= :chapter) [doc])

; compose an accessor to access attributes
(defn attr-accessor [a]
  (comp a :attrs))

; attribute predicate transducer
(defn attrp [a pred]
  (filter (comp pred (attr-accessor a))))

; attribute equality predicate
(defn attr= [a v]
  (attrp a (partial = v)))

; find chapters with name attribute = "Conclusion"
(eduction (comp (tag= :chapter)
                (attr= :name "Conclusion")) [doc])

(defn xpath-lite [doc path]
  "returns elements from doc that match all predicates in path"
  (eduction (apply comp path) [doc]))

(xpath-lite doc [(tag= :chapter) (attr= :name "Conclusion")])

; xpath's text() as a transducer
(def text
  (comp (mapcat :content) (filter string?)))

; Original challenge redux:
(xpath-lite doc [(tag= :chapter) (attr= :name "Introduction") (tag= :para) text])

; Creates a map of chapter names -> strings, sometimes called a 'Rollup'
(into {} (xpath-lite
                 doc
                 [(tag= :chapter)
                  (map (juxt (attr-accessor :name)
                             #(sequence (comp (tag= :para) text) [%])))]))

; transducer version of clojure.core/xml-seq
(def xml-seq*
  (fn [rf]
    (fn
      ([] (rf))
      ([result] (rf result))
      ([result input]
       (rf result (xml-seq input))))))

; Xpath's descendants()
(def descendants* (comp xml-seq* cat))

(eduction descendants* doc)

; TODO add exercises for JSON version