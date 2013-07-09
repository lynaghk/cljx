(ns leiningen.cljx
  (:require [leiningen.core.eval :refer (eval-in-project)]
            [leiningen.core.project :as project]))

(def no-opts-warning "You need a :cljx entry in your project.clj! See the cljx docs for more info.")

(defn- cljx-eip
  "Evaluates the given [form] within the context of a [project].  A single
form that is to be run beforehand (for requires, etc) is specified by
[init].

This variant of eval-in-project implicitly adds the current :plugin dep on
cljx to the main :dependencies vector of the project, as well as specifying
that the eval should happen in-process in a new classloader (faster!)."
  [project init form]
  (let [cljx-plugin (filter (comp #{'com.keminglabs/cljx 'org.clojars.cemerick/cljx} first)
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
       (#'cljx.core/cljx-compile '~builds)
       ; if users have :injections that start any agents, *and* we're in our own
       ; process, we need to shut them down so that e.g. `lein cljx once`
       ; doesn't take 60s
       ~(when (-> project :eval-in name (= "subprocess"))
          '(shutdown-agents)))))

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
