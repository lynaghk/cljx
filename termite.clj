(ns termite.core
  (:refer-clojure :exclude [==])
  (:use [clojure.core.logic]
        :reload))


(set! *print-meta* true)

(defn simplify-one [expr rules]
  (let [alt (run* [q]
              (fresh [pat subst]
                (membero [pat subst] rules)
                (project [pat subst]
                  (all (pat expr)
                       (subst q)))))]
    (if (empty? alt) expr (first alt))))



(simplify-one ;;'(reify clojure.lang.IFn (invoke [x] x))
 'clojure.lang.IFn
 [[#(== % 'clojure.lang.IFn) #(== % 'IFn)]] 
              )
