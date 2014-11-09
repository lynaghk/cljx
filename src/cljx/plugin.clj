(ns cljx.plugin
  (:require [clojure.java.io :as io]))

(def ^:private cljx-version
  (-> (io/resource "META-INF/leiningen/com.keminglabs/cljx/project.clj")
       slurp
       read-string
       (nth 2)))

(assert (string? cljx-version)
        (str "Something went wrong, version of cljx is not a string: "
             cljx-version))

; stores a copy of the project, updated whenever lein calls (middleware). Calling
; wrap-cljx will make a copy of it so it should be safe to change while running
; another repl. This is only used for nrepl, the rest of the plugin can
; propogate the project variable and doesn't need it.
(def active-project (atom nil))

(defn middleware
  [project]

  (let [updated-project
        (-> project
            (update-in [:dependencies]
                       (fnil into [])
                       [['com.keminglabs/cljx cljx-version]])
            (update-in [:repl-options :nrepl-middleware]
                       (fnil into [])
                       '[cljx.repl-middleware/wrap-cljx cemerick.piggieback/wrap-cljs-repl]))]
    (reset! active-project updated-project)))
