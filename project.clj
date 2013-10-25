(defproject com.keminglabs/cljx "0.3.1"
  :description "Static Clojure code rewriting"
  :url "http://github.com/lynaghk/cljx"
  :license {:name "BSD"
            :url "http://www.opensource.org/licenses/BSD-3-Clause"}

  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/core.match "0.2.0"]
                 [org.clojars.trptcolin/sjacket "0.1.0.3"]
                 [com.cemerick/piggieback "0.1.0"]
                 [watchtower "0.1.1"]]

  :cljx {:builds [{:source-paths ["test"]
                   :output-path "target/test-output"
                   :rules :clj}
                  {:source-paths ["test"]
                   :output-path "target/test-output"
                   :rules :cljs}]}

  :profiles {:dev {
                   ; self-reference and chained `lein install; lein test` invocation
                   ; needed to use the project as its own plugin. Leiningen :-(
                   :plugins [[com.keminglabs/cljx "0.3.1"]]}
             :self-plugin [:default {:plugins [[com.cemerick/clojurescript.test "0.2.0-SNAPSHOT"]]}]}
  
  :aliases {"cleantest" ["with-profile" "self-plugin" "do" "clean," "cljx" "once," "test"]}

  :eval-in :leiningen)
