      (ns cljx.a
        (:use [clojure.pprint :only [pp]]))

                 
                                                   

      
           
                      

     
(defn p [x]
  (println x))

(defn both [x]
  (+ x x))

(reify
        clojure.lang.IFn
                      
  (invoke [_ x] (inc x)))

(defmacro increment [x] `(inc ~x))

(comment foo)

; make sure that profiles work
              [2 4 5]

; making sure other tagged literals are left untouched
#whatever [1 2 3]


;;;;;;;;;;;; This file autogenerated from test/cljx/a.cljx
