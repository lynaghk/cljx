(ns kibit-scratch
  (:use [jonase.kibit.core :only [simplify]])
  (:require [clojure.core.logic :as logic]))


(simplify (defn ^{:cljs true} sqrt [x] (Math/sqrt x))
          (map logic/prep cljs-rules))



(defn ^{:cljs false} sqrt [x] (Math/sqrt x))
(set! *print-meta* true)

'[(reify
    clojure.lang.IFn
    (fn [_ x] (inc x)))]


(simplify '(if (not x) 1 2))



