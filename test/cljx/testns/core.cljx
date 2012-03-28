^:clj (ns test.core
        (:use [clojure.pprint :only [pp]]))

^:cljs (ns test.core
         (:use-macros [clojure.pprint :only [pp]]))

(defn ^:cljs p [x]
  (.log js/console x))

(defn ^:clj p [x]
  (println x))

(defn both [x]
  (+ x x))

(reify
    clojure.lang.IFn (invoke [_ x] (inc x)))

(defmacro increment [x] `(inc ~x))
