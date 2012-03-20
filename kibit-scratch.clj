(ns kibit-scratch
  (:use [jonase.kibit.core :only [simplify]])
  (:require [clojure.core.logic :as logic]))

(defmacro defrules [name & rules]
  (let [rules (for [rule rules]
                (if (= (count rule) 2)
                  (let [[pat alt] rule]
                    `['~pat [] '~alt])
                  (let [[pat constraint alt] rule]
                    `['~pat ~constraint '~alt])))]
    (list 'def name (vec rules))))

(logic/defne fn-call? [expr]
  ([[_ [_ . _] [fun . _]]]
     (logic/project [fun]
       (logic/pred fun symbol?)
       (logic/pred fun not-method?))))

(logic/defne cljs? [expr]
  ([var]
     (logic/project [var]
                    (logic/pred var #(= true (:cljs (meta %)))))))

(defrules cljs-rules
  [clojure.lang.IFn IFn]
  [?x [cljs?] nil])



(simplify (defn ^{:cljs true} sqrt [x] (Math/sqrt x))
          (map logic/prep cljs-rules))



(defn ^{:cljs false} sqrt [x] (Math/sqrt x))
(set! *print-meta* true)

'[(reify
    clojure.lang.IFn
    (fn [_ x] (inc x)))]


(simplify '(if (not x) 1 2))



