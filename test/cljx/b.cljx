(ns cljx.b
  (#+cljs :require-macros #+clj :require
          [foo.bar]))

#+clj
(defn clj-only [x] "kthx")

#+cljs
(defn cljs-only [x] "Internets time")

#+self-plugin
(defn both [x] "yay")

(defn x
  [y]
  (#+clj + #+cljs - y y))
