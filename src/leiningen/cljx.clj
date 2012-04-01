(ns leiningen.cljx
  (:use [cljx.core :only [generate]]))


(def no-opts-warning "You need a :cljx entry in your project.clj! It should look something like:\n
  :cljx {:cljx-paths [\"src/cljx\"]
         :clj-output-path \".generated/clj\"
         :cljs-output-path \".generated/cljs\"}
")

(defn cljx
  "Statically transform .cljx files into Clojure and ClojureScript sources."
  [project]

  (if-let [opts (:cljx project)]
    (if-let [{builds :builds} opts]
      (doseq [{:keys [source-paths output-path extension rules]
               :or {extension "clj"}} builds]
        
        (let [rules (eval rules)]
          (doseq [p source-paths]
            (generate p output-path extension rules)))))

    (println no-opts-warning)))
