(ns cljx.rules
  (:require [net.cgrand.sjacket :as sj]
            [clojure.core.match :refer (match)]
            [clojure.zip :as z]
            [clojure.string :as str]))

(defn- replacement-whitespace-for
  [node]
  (let [code (sj/str-pt node)
        ws (str/replace code #"[^\n\r]" " ")]
    (net.cgrand.parsley.Node. :whitespace [ws])))

(defmacro elide-marked
  [kw]
  `(fn [zip-loc#]
     (match [(z/node zip-loc#)]
            [{:tag :meta :content [~'_ {:tag :keyword :content [~'_ {:content [~(name kw)]}]} & ~'_]}]
            (z/edit zip-loc# replacement-whitespace-for)

            :else zip-loc#)))

(def cljs-rules [(elide-marked :clj)])

(def clj-rules [(elide-marked :cljs)])

