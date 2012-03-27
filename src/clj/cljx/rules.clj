(ns cljx.rules
  (:refer-clojure :exclude [==])
  (:use [clojure.core.logic :only [matche conde pred lvar ==]]))

(defn- meta-guard [key]
  #(-> % meta key (= true)))

(def remove-marked-clj
  [#(matche [%]
            ([[_ var . _]]
               (pred var (meta-guard :clj)))
            ([x]
               (pred x (meta-guard :clj))))
   #(== % :cljx.core/exclude)])

(def cljs-protocols
  (let [x (lvar)]
    [#(conde ;; matche has some problems here; you need to match (symbol "clojure.lang.IFn"), so it doesn't really save space...
       ((== % 'clojure.lang.IFn)  (== x 'IFn))
       ;;other protocol renaming goes here
       )
     #(== % x)]))



(def cljs-rules [cljs-protocols
                 remove-marked-clj])
