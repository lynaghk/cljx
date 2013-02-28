(ns leiningen.cljx
  (:require [leiningen.core.eval :refer (eval-in-project)]
            [leiningen.core.project :as project]))

(def no-opts-warning "You need a :cljx entry in your project.clj! It should look something like:\n
  :cljx {:cljx-paths [\"src/cljx\"]
         :clj-output-path \".generated/clj\"
         :cljs-output-path \".generated/cljs\"}
")

(defn- cljx-eip
  "Evaluates the given [form] within the context of a [project].  A single
form that is to be run beforehand (for requires, etc) is specified by
[init].

This variant of eval-in-project implicitly adds the current :plugin dep on
cljx to the main :dependencies vector of the project, as well as specifying
that the eval should happen in-process in a new classloader (faster!)."
  [project init form]
  (let [cljx-plugin (filter (comp #(= % 'com.keminglabs/cljx) first)
                            (:plugins project))]
    (eval-in-project
      (-> project
        (project/merge-profiles [{:dependencies cljx-plugin}])
        ; don't AOT any Clojure, as cljx will likely generate some .clj files
        ; that will be needed to make sure that succeeds
        (assoc :prep-tasks ["javac"]))
      form
      init)))

(defn- once
  "Transform .cljx files once and then exit."
  [project builds]
  (cljx-eip project
    '(require 'cljx.core)
    `(do
       ; ensure namespaces containing vars named in data_readers.clj files are loaded
       ; once Leiningen uses Clojure 1.5.0, we can :eval-in :classloader
       ; and bind *default-data-reader-fn* to a dummy type that prints just like
       ; the tagged literals that we read
       (doseq [v# (vals *data-readers*)] (require (-> v# .ns str symbol)))
       (#'cljx.core/cljx-compile '~builds))))

(defn- auto
  "Watch .cljx files and transform them after any changes."
  [project builds]
  (cljx-eip project
       '(require 'cljx.core '[watchtower.core :as wt])
       (let [dirs (set (flatten (map :source-paths builds)))]
          `(do
             (println "Watching" (vec ~dirs) "for changes.")
             (-> (wt/watcher* ~dirs)
               (wt/file-filter (wt/extensions :cljx))
               (wt/rate 1000)
               (wt/on-change (fn [_#] (#'cljx.core/cljx-compile '~builds)))
               (wt/watch))))))

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
