#+clj (ns cljx.a
        (:use [clojure.pprint :only [pp]]))

#+cljs (ns cljx.a
         (:use-macros [clojure.pprint :only [pp]]))

#+cljs
(defn p [x]
  (.log js/console x))

#+clj
(defn p [x]
  (println x))

(defn both [x]
  (+ x x))

(reify
  #+clj clojure.lang.IFn
  #+cljs cljs.core.IFn
  (invoke [_ x] (inc x)))

(defmacro increment [x] `(inc ~x))

(comment foo)

; making sure other tagged literals are left untouched
#whatever [1 2 3]

