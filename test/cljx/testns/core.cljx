(ns test.core)

(defn ^:cljs p [x]
  (.log js/console x))

(defn ^:clj p [x]
  (println x))

(defn both [x]
  (+ x x))

(reify
    clojure.lang.IFn (invoke [_ x] (inc x)))
