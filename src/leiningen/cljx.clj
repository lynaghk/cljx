(ns leiningen.cljx
  (:use [cljx.core :only [generate]]
        [watchtower.core :only [watcher* watch rate file-filter on-change extensions]]))


(def no-opts-warning "You need a :cljx entry in your project.clj! It should look something like:\n
  :cljx {:cljx-paths [\"src/cljx\"]
         :clj-output-path \".generated/clj\"
         :cljs-output-path \".generated/cljs\"}
")

(defn- cljx-compile [builds]
  "The actual static transform, separated out so it can be called repeatedly."
  (doseq [{:keys [source-paths output-path extension rules include-meta]
           :or {extension "clj" include-meta false}} builds]
    (let [rules (eval rules)]
      (doseq [p source-paths]
        (binding [*print-meta* include-meta]
          (generate p output-path extension rules))))))

(defn- once
  "Transform .cljx files once and then exit."
  [builds]
  (cljx-compile builds))

(defn- auto
  "Watch .cljx files and transform them after any changes."
  [builds]
  (let [dirs (set (flatten (map :source-paths builds)))]
    (println "Watching" (vec dirs) "for changes.")
    (-> (watcher* dirs)
        (file-filter (extensions :cljx))
        (rate 1000)
        (on-change (fn [_] (cljx-compile builds)))
        (watch))))

(defn cljx
  "Statically transform .cljx files into Clojure and ClojureScript sources."
  {:subtasks [#'once #'auto]}
  ([project] (cljx project "once"))
  ([project subtask]

      (if-let [opts (:cljx project)]
        (if-let [{builds :builds} opts]
          (case subtask
            "once" (once builds)
            "auto" (auto builds)))

        (println no-opts-warning))))
