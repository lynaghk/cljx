(ns leiningen.cljx
  (:use [cljx.core :only [generate]]
        [cljx.rules :only [cljs-rules clj-rules]]))


(def no-opts-warning "You need a :cljx entry in your project.clj! It should look something like:\n
  :cljx {:cljx-paths [\"src/cljx\"]
         :clj-output-path \".generated/clj\"
         :cljs-output-path \".generated/cljs\"}
")

(defn cljx
  "Statically transform .cljx files into Clojure and ClojureScript sources."
  [project & args]

  (if-let [opts (:cljx project)]
    (let [{:keys [cljx-paths clj-output-path cljs-output-path]} opts]

      (when clj-output-path
        (doseq [p cljx-paths]
          (generate p clj-output-path "clj" clj-rules)))

      (when cljs-output-path
        (doseq [p cljx-paths]
          (generate p cljs-output-path "cljs" cljs-rules))))

    (println no-opts-warning)))
