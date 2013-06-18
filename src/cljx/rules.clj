(ns cljx.rules
  (:require [net.cgrand.sjacket :as sj]
            [clojure.core.match :refer (match)]
            [clojure.zip :as z]
            [clojure.string :as str])
  (:import net.cgrand.parsley.Node))

(defn- whitespace-for
  [string]
  (str/replace string #"[^\n\r]" " "))

(defn- whitespace-node-for
  [node]
  (Node. :whitespace [(whitespace-for (sj/str-pt node))]))

(defmacro elide-marked
  [kw]
  `(fn [zip-loc#]
     (match [(z/node zip-loc#)]
            [{:tag :meta :content [~'_ {:tag :keyword :content [~'_ {:content [~(name kw)]}]} & ~'_]}]
            (z/edit zip-loc# whitespace-node-for)

            :else zip-loc#)))

; this is necessary because these marks are really not intended for runtime, and
; can cause read/load errors when e.g. on strings
(defmacro elide-mark
  [kw]
  `(fn [zip-loc#]
     (match [(z/node zip-loc#)]
            [{:tag :meta :content [~'_ {:tag :keyword :content [~'_ {:content [~(name kw)]}]} & contents#]}]
            (z/edit zip-loc#
                    assoc :content
                    (vec (cons (Node. :whitespace [(whitespace-for (str "^" ~kw))])
                               contents#)))

            :else zip-loc#)))

(def cljs-rules [(elide-marked :clj)
                 (elide-mark :cljs)])

(def clj-rules [(elide-marked :cljs)
                (elide-mark :clj)])

