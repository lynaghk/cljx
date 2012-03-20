(ns cljx.guards
  (:require [clojure.core.logic :as logic]))

(logic/defne clj? [expr]
  ([[_ var . _]]
     (logic/project [var]
                    (logic/pred var #(-> % meta :clj (= true))))))

(logic/defne cljs? [expr]
  ([[_ var . _]]
     (logic/project [var]
                    (logic/pred var #(-> % meta :cljs (= true))))))
