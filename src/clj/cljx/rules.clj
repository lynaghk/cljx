(ns cljx.rules
  (:use [jonase.kibit.rules.util :only [defrules]]
        [cljx.guards :only [clj? cljs?]]))

(defrules cljs-rules
  
  [clojure.lang.IFn IFn]
  
  [?x [clj?] nil])

