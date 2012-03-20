(ns cljx.guards
  (:require [clojure.core.logic :as logic]))

(logic/defne clj? [expr]
  ([var]
     (logic/project [var]
                    (logic/pred var #(= true (:clj (meta %)))))))

(logic/defne cljs? [expr]
  ([var]
     (logic/project [var]
                    (logic/pred var #(= true (:cljs (meta %)))))))
