(ns cljx.rules
  (:use [kibit.rules.util :only [defrules]]
        [cljx.guards :only [clj? cljs?]]))

(defrules cljs-rules
  
  [clojure.lang.IFn IFn]
  
  [?x :cljx.core/exclude :when [clj?]])

