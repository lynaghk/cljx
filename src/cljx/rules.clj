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

(defn apply-features
  [zip-loc features]
  (match [(z/node zip-loc)]
         [{:tag :reader-literal
           :content ["#" {:tag :symbol
                          :content [{:tag :name :content [feature-string]}]}
                     & annotated-exprs]}]
         (let [inclusive? (= \+ (first feature-string))
               ;; TODO exclusive expressions, sets in any case
               feature (subs feature-string 1)]
           (if-not (and inclusive? (features feature))
             (z/edit zip-loc whitespace-node-for)
             (z/edit zip-loc
                     assoc :content
                     (vec (cons (Node. :whitespace [(whitespace-for (str "#" feature-string))])
                                annotated-exprs)))))
         :else zip-loc))

(defn elide-form
  [form-name zip-loc]
  (match [(z/node zip-loc)]
         [{:tag :list
           :content ["(" {:tag :symbol
                          :content [{:tag :name :content [sym-name]}]}
                     & _]}]
         (if (= sym-name (name form-name))
           (z/edit zip-loc whitespace-node-for)
           zip-loc)
         :else zip-loc))

(def cljs-rules {:features #{"cljs"}
                 :transforms [(partial elide-form 'comment)
                              (partial elide-form 'defmacro)]})

(def clj-rules {:features #{"clj"}
                :transforms []})

