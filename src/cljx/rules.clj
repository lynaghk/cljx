(ns cljx.rules
  (:refer-clojure :exclude [==])
  (:use [clojure.core.logic :only [matche conde pred lvar == firsto]]
        [kibit.rules.util :only [compile-rule]]))

(defn- meta-guard [key]
  #(-> % meta key (= true)))

(defn remove-marked [key]
  [#(matche [%]
            ([[_ var . _]]
               (pred var (meta-guard key)))
            ([x]
               (pred x (meta-guard key))))
   #(== % :cljx.core/exclude)])

(def cljs-protocols
  (let [x (lvar)]
    [#(conde ;; matche has some problems here; you need to match (symbol "clojure.lang.IFn"), so it doesn't really save space...
       ((== % 'clojure.lang.IFn)  (== x 'IFn))
       ;;other protocol renaming goes here
       )
     #(== % x)]))

(def cljs-types
  (let [x (lvar)]
    [#(conde 
       ((== % 'clojure.lang.Atom)  (== x 'cljs.core.Atom))
       
       ;;Is there a nicer way to handle the trailing dot?
       ((== % 'Error)  (== x 'js/Error))
       ((== % 'Error.)  (== x 'js/Error.)))
     #(== % x)]))

(def remove-defmacro
  (compile-rule '[(defmacro . ?_) :cljx.core/exclude]))

(def remove-comment
  (compile-rule '[(comment . ?_) :cljx.core/exclude]))



(def cljs-rules [cljs-protocols
                 cljs-types
                 (remove-marked :clj)
                 remove-defmacro
                 remove-comment])

(def clj-rules [(remove-marked :cljs)
                remove-comment])
