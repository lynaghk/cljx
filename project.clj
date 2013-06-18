(defproject com.keminglabs/cljx "0.3.0-SNAPSHOT"

  :description "Static Clojure code rewriting"
  :url "http://github.com/lynaghk/cljx"
  :license {:name "BSD"
            :url "http://www.opensource.org/licenses/BSD-3-Clause"}

  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/core.match "0.2.0-beta2"]
                 [org.clojars.trptcolin/sjacket "0.1.0.3"]
                 [com.cemerick/piggieback "0.0.4"]
                 [watchtower "0.1.1"]]

  :cljx {:builds [{:source-paths ["test"]
                   :output-path "target/test-output"
                   :rules cljx.rules/clj-rules
                   :extension "clj"}
                  {:source-paths ["test"]
                   :output-path "target/test-output"
                   :rules cljx.rules/cljs-rules
                   :extension "cljs"}]}

  :profiles {:dev {:dependencies [[org.clojure/clojurescript "0.0-1820"]]
                   :repl-options {:nrepl-middleware [cemerick.piggieback/wrap-cljs-repl
                                                    cljx.repl-middleware/wrap-cljx]}
                   :aliases {"cleantest" ["do" "clean," "cljx" "once," "test"]}}}

  :eval-in :leiningen)
