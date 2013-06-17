(ns scratch
  (:require [net.cgrand.sjacket :as sj]
            [net.cgrand.sjacket.parser :as p]
            [clojure.core.match :refer [match]]
            [clojure.zip :as z]))

(def grr
  "^:clj  (test 1 2)
 ^:cljs (test 2 3)")

(def parsed (p/parser grr))


(defn- node-language
  [node]
  (match [node]
    ;;[{:tag :meta :content ["^" {:tag :keyword :content [":" {:tag :name :content ["cljx"]}]}]}]

    [{:tag :meta :content [_ {:tag :keyword :content [_ {:content [lang]}]}]}] (keyword lang)
    :else nil))

(defn walk
  [language-target node]
  (if (z/branch? node)
    (z/node (map walk (z/children node)))
    (when (= language-target (node-language node))
      node)))





(defn elide
  [target code]
  (-> (p/parser code)

      
      sj/str-pt))


