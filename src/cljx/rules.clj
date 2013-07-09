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
                          :content [{:tag :name
                                     :content [(feature-string
                                                 :guard #(re-find #"^[\-\+]" %))]}]}
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
                          :content [{:tag :name
                                     :content [(sym-name :guard #(= % form-name))]}]}
                     & _]}]
         (z/edit zip-loc whitespace-node-for)
         :else zip-loc))

(defn- pad1
  [replacement original]
  (apply str replacement (repeat (- (count original) (count replacement)) \space)))

(defn replace-symbols
  [symbols-map zip-loc]
  (match [(z/node zip-loc)]
         [{:tag :symbol
           :content [{:tag :name
                      :content [(symbol-name :guard #(and (string? %)
                                                          (-> % symbol symbols-map)))]}]}]
         (z/edit zip-loc update-in [:content 0 :content 0]
                 #(-> % symbol symbols-map name (pad1 %)))
         :else zip-loc))

; disabled until a full solution can be proffered
; see gh-11
#_(def ^:private clj->cljs-symbols
  (->> '[IFn]
       (map name)
       (map #(vector (str "clojure.lang." %) (str "cljs.core." %)))
       (map (partial mapv symbol))
       (into {})))

(def cljs-rules {:filetype "cljs"
                 :features #{"cljs"}
                 :transforms [(partial elide-form "defmacro")
                              ; disabled until a full solution can be proffered,
                              ; see gh-11
                              #_(partial replace-symbols clj->cljs-symbols)]})

(def clj-rules {:filetype "clj"
                :features #{"clj"}
                :transforms []})

