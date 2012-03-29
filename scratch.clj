(ns scratch
  (:use [cljx.core :only [generate]]
        [cljx.rules :only [cljs-rules clj-rules]]
        :reload))

(do
  (def cljx-path "test/cljx")
  (def clj-output-path "test/generated/clj")
  (def cljs-output-path "test/generated/cljs"))

(do
  (def cljx-path "../c2/src/cljx")
  (def clj-output-path "../c2/.generated/clj")
  (def cljs-output-path "../c2/.generated/cljs"))

(comment
  (set! *print-meta* false)
  (use 'clojure.stacktrace)
  
  (do
    (generate cljx-path cljs-output-path "cljs" cljs-rules)
    (generate cljx-path clj-output-path "clj" clj-rules))






  
  (use '[kibit.core :only [simplify simplify-one]]
       'clojure.core.logic)
  
  (simplify-one '(defmacro nom [x] nom nom nom)
                [[#(matche [%]
                           ([['defmacro . _]]))
                   #(== % :exclude)]])
  (run* [q]
        (firsto '(defmacro nom [x] nom nom nom) 'defmacro)
)
  
  

  (defn ns->cljs-ns
    "Creates a cljs-compatible namespace form by splitting vars marked with ^:macro into appropriate use-macros or require-macros forms"
    [ns-form]

    (let [use-forms]

      )
    )

  (def ns-form '(ns
                    (:use [my-ns.thing :only [a b c ^:macro a-macro]])
                  ))

  (require '[clojure.core.logic :as logic])

  )
