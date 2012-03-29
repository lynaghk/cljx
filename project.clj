(defproject cljx "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.3.0"]
                 ;;[org.clojure/core.logic "0.6.8"]
                 ]
  
  :source-paths  ["src/clj"
                  "../software/kibit/src"
                  "../software/core.logic/src/main/clojure"]

  :cljsbuild {:builds
              [{:source-path "test/generated/cljs",
                :jar false,
                :compiler
                {:output-to "public/main.js",
                 :optimizations :simple,
                 :pretty-print true}}]})
