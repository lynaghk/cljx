(defproject cljx "0.1.0-SNAPSHOT"

  :description "Static Clojure code rewriting"
  :url "http://github.com/lynaghk/cljx"
  :license {:name "BSD"
            :url "http://www.opensource.org/licenses/BSD-3-Clause"}
  
  :dependencies [[org.clojure/tools.namespace "0.1.2"]
                 [org.clojure/core.logic "0.7.0"]]
  
  ;;Until new core.logic and kibit releases are cut...
  :source-paths  ["src"
                  "../software/kibit/src"]
  
  :eval-in-leiningen true)
