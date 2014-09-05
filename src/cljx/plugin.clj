(ns cljx.plugin
  (:require [robert.hooke :as hooke]
            leiningen.repl
            [clojure.java.io :as io]))

(def cljx-coordinates
  (-> (or (io/resource "META-INF/leiningen/com.keminglabs/cljx/project.clj")
        (io/resource  "META-INF/leiningen/org.clojars.cemerick/cljx/project.clj"))
    slurp
    read-string
    ((fn [[_ artifact version]] [artifact version]))))

(assert (and (symbol? (first cljx-coordinates))
          (string? (second cljx-coordinates)))
  (str "Something went wrong, cljx coordinates are invalid: "
    cljx-coordinates))

(defn middleware
  [project]
  (if (or (-> project :cljx :disable-repl-integration)
        (not (-> project meta :included-profiles set (contains? :repl))))
    project
    (-> project
      (update-in [:repl-options :nrepl-middleware]
        (fnil into [])
        '[cljx.repl-middleware/wrap-cljx cemerick.piggieback/wrap-cljs-repl])
      (update-in [:dependencies]
        (fnil conj [])
        cljx-coordinates))))
