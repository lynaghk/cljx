(ns scratch
  (:use [cljx.core :only [generate]]))
(def cljx-path "test/cljx")
(def clj-output-path "test/generated/clj")
(def cljs-output-path "test/generated/cljs")


(comment
  (set! *print-meta* false)
  (use 'clojure.stacktrace)
  (generate cljx-path cljs-output-path "cljs")


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
