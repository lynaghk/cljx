(ns cljx.plugin
  (:require [clojure.java.io :as io]
            [leiningen.core.project :refer [add-profiles set-profiles]]))

(def ^:private cljx-version
  (-> (io/resource "META-INF/leiningen/com.keminglabs/cljx/project.clj")
       slurp
       read-string
       (nth 2)))

(assert (string? cljx-version)
        (str "Something went wrong, version of cljx is not a string: "
             cljx-version))

(def profiles
  {:cljx/inject-dependencies
   {:dependencies [['com.keminglabs/cljx cljx-version :scope "test"]]}
   :cljx/inject-nrepl-middleware
   {:repl-options
    {:nrepl-middleware
     '[cljx.repl-middleware/wrap-cljx cemerick.piggieback/wrap-cljs-repl]}}})

(defn add-to-default-profile
  [project profiles]
  (vary-meta project update-in [:profiles :default] (fnil into []) profiles))

(defn middleware
  [project]
  (if (-> project meta ::middleware-applied) ; guard recursion via set-profiles
    project
    (-> project
        (vary-meta assoc ::middleware-applied true)
        (add-profiles profiles)
        (add-to-default-profile [:cljx/inject-dependencies
                                 :cljx/inject-nrepl-middleware])
        (set-profiles
         (into (-> project meta :included-profiles distinct)
               [:cljx/inject-dependencies
                :cljx/inject-nrepl-middleware]))
        (vary-meta dissoc ::middleware-applied))))
