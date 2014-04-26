(defproject com.keminglabs/cljx "0.4.0-SNAPSHOT"
  :description "Static Clojure code rewriting"
  :url "http://github.com/lynaghk/cljx"
  :license {:name "BSD"
            :url "http://www.opensource.org/licenses/BSD-3-Clause"}

  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/core.match "0.2.0"]
                 [org.clojars.trptcolin/sjacket "0.1.0.3"
                  :exclusions [org.clojure/clojure]]
                 [com.cemerick/piggieback "0.1.0"]
                 [watchtower "0.1.1"]]

  :cljx {:builds [{:source-paths ["test"]
                   :output-path "target/test-output"
                   :rules :clj}
                  {:source-paths ["test"]
                   :output-path "target/test-output"
                   :rules :cljs}]}

  :profiles {
             ; self-reference and chained `lein install; lein test` invocation
             ; needed to use the project as its own plugin. Leiningen :-(
             :self-plugin [:default {:plugins [[com.keminglabs/cljx "0.4.0-SNAPSHOT"]
                                               [com.cemerick/clojurescript.test "0.2.1"]]}]}

  :aliases {"cleantest" ["with-profile" "self-plugin" "do" "clean," "cljx" "once," "test"]}
  ;; This fails with the self-plugin, but can be uncommented to check
  ;; for version ranges with `lein deps :tree'.  Fix incoming.  See
  ;; https://github.com/technomancy/leiningen/issues/1498
  ;; :pedantic? :abort

  :eval-in :leiningen)
