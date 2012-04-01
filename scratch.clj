(ns scratch
  (:use [cljx.core :only [generate]]
        [cljx.rules :only [cljs-rules clj-rules]]
        :reload-all))

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

  (use '[clojure.java.io :only [reader]])
  
  (let [r (reader (java.io.File. "test/cljx/testns/core.cljx"))]
    (kibit.check/check-reader r
                              :rules  cljs-rules
                              :guard identity
                              :resolution :toplevel))

  
  
  (use '[kibit.core :only [simplify simplify-one]])
  (use 'clojure.core.logic)



  (defn simplify-one [expr rules]
    (let [alt (run* [q]
                    (fresh [pat subst]
                           (membero [pat subst] rules)
                           (project [pat subst]
                                    (all (pat expr)
                                         (subst q)))))]
      (if (empty? alt) expr (first alt))))

  (def form '(defmacro nom [x] (some (delicious)) forms))
  
  ;;this works nicely
  (let [x    (lvar)
        rest (lvar)]
    (run* [q]  
          (== form (llist 'defmacro x rest))
          (== q x)))
  ;;=> (nom)

  ;;why not this?
  (let [x    (lvar)
        rest (lvar)]
    (run* [q]  
          (== form (defmacro ~x . ~rest))
          (== q x)))
  ;;=> ()

  ;;This doesn't work either
  (run* [q]
        (prep '(all
                (== form '(defmacro ?x . ?rest))
                (== q ?x))))
  ;;=> java.lang.ClassCastException: clojure.lang.PersistentList cannot be cast to clojure.lang.IFn
  
  
  ;;This also works
  (run* [q]
        (fresh [pat alt]
               (membero [pat alt]
                        [(prep '[(defmacro ?x . ?rest)
                                 ?x])])
               (== pat form)
               (== q alt)))
  ;;=> (nom) 



  (run* [q]
        (all
         (== form (prep '(defmacro ?x . ?rest)))
         (== q true)))

  (clojure.core/doall
   (clojure.core.logic/solve false [q]
                             (all
                              (== form (prep (quote (defmacro ?x . ?rest))))
                              (== q true))))


  (clojure.core/doall
   (clojure.core.logic/solve false [q]
                             (eval (prep (quote (all
                                                 (== form (defmacro ?x . ?rest))
                                                 (== q true)))))))
  
  (run* [q]
        (prep '(all
                 (== form (defmacro ?x . ?rest))
                 (== q true))))
;;why not this?
  (run* [q]
        (fresh [pat alt]
               (membero [pat alt]
                        [(prep '[#(== % (defmacro ?x . ?rest))
                                 #(== % ?x)])])
               (pat form)
               (alt q)))
        ;;=> clojure.core.logic.LVar cannot be cast to clojure.lang.IFn
        
  





  

  (simplify-one form
                [(fresh [pat alt]
                        (== [pat alt]
                            (prep '[(defmacro ?x . ?rest) #(== % ?x)]))
                        [#(== pat %)
                         alt])])













  (simplify-one form
                [[#(matche [%]
                           ([['defmacro . _]]))
                  #(== % :exclude)]])




  (def inc-rule
    (let [x (lvar)]
      [#(all (== % `(~'+ ~x 1))
             ;; It's easy to add pattern predicates
             (pred x number?))
       #(== % `(inc ~x))]))



  
  (let [x    (lvar)
        rest (lvar)]
    `(~'defmacro ~x . ~rest)
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

  
