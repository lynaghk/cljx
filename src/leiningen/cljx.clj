(ns leiningen.cljx
  (:require cljx.plugin
            cljx.core
            [watchtower.core :as wt]))

(def no-opts-warning "You need a :cljx entry in your project.clj! See the cljx docs for more info.")

(defn- once
  "Transform .cljx files once and then exit."
  [project builds]
  (cljx.core/cljx-compile builds))

(defn- auto
  "Watch .cljx files and transform them after any changes."
  [project builds]
  (let [dirs (set (flatten (map :source-paths builds)))]
    (println "Watching" (vec dirs) "for changes.")
    (-> (wt/watcher* dirs)
        (wt/file-filter (wt/extensions :cljx))
        (wt/rate 1000)
        (wt/on-change (fn [files] 
                        (cljx.core/cljx-compile builds :files files)))
        (wt/watch))))

(defn cljx
  "Statically transform .cljx files into Clojure and ClojureScript sources."
  {:subtasks [#'once #'auto]}
  ([project] (cljx project "once"))
  ([project subtask]
   (if-let [opts (:cljx project)]
     (if-let [{builds :builds} opts]
       (case subtask
         "once" (once project builds)
         "auto" (auto project builds)))
     (println no-opts-warning))))
