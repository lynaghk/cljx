;;; Profiles for cljx project layouts
{:cljx-only-source
 {:cljx {:builds [{:source-paths ["src"]
                   :output-path "target/generated/src/clj"
                   :rules :clj}
                  {:source-paths ["src"]
                   :output-path "target/generated/src/cljs"
                   :rules :cljs}]}
  :source-paths ^:replace ["target/generated/src/clj"]
  :resource-paths ^:replace ["target/generated/src/cljs"]}

 :cljx-only-test
 {:cljx {:builds [{:source-paths ["test"]
                   :output-path "target/generated/test/clj"
                   :rules :clj}
                  {:source-paths ["test"]
                   :output-path "target/generated/test/cljs"
                   :rules :cljs}]}
  :source-paths ["target/generated/test/clj"]
  :resource-paths ["target/generated/test/cljs"]}

 :mixed-source
 {:cljx {:builds [{:source-paths ["src/cljx/"]
                   :output-path "target/generated/src/clj"
                   :rules :clj}
                  {:source-paths ["src/cljx"]
                   :output-path "target/generated/src/cljs"
                   :rules :cljs}]}
  :source-paths ^:replace ["src/clj" "target/generated/src/clj"]
  :resource-paths ^:replace ["src/cljs" "target/generated/src/cljs"]}

 :mixed-test
 {:cljx {:builds [{:source-paths ["test/cljx/"]
                   :output-path "target/generated/test/clj"
                   :rules :clj}
                  {:source-paths ["test/cljx"]
                   :output-path "target/generated/test/cljs"
                   :rules :cljs}]}
  :source-paths ["test/clj" "target/generated/test/clj"]
  :resource-paths ["test/cljs" "target/generated/test/cljs"]}}
