(ns test.core)

^:cljs (defn p [x]
         (.log js/console x))

^:clj (defn p [x]
        (println x))

(defn both [x]
  (+ x x))
