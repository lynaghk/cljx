(ns cljx.guards
  (:require [clojure.core.logic :as logic]))

(defn- meta-guard [key]
    #(-> % meta key (= true)))

(logic/defne clj? [expr]
  ([[_ var . _]]
     (logic/project [var]
                    (logic/pred var (meta-guard :clj))))
  ([var]
     (logic/project [var]
                    (logic/pred var (meta-guard :clj)))))

(logic/defne cljs? [expr]
  ([[_ var . _]]
     (logic/project [var]
                    (logic/pred var (meta-guard :cljs))))
  ([var]
     (logic/project [var]
                    (logic/pred var (meta-guard :cljs)))))
