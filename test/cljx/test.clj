(ns cljx.test
  (:use clojure.test))

(deftest verify-cljx-output
  (let [proc (.. Runtime getRuntime
                 (exec (str "diff -r test-output target" java.io.File/separator "test-output")))]
    (println (slurp (.getInputStream proc)))
    (is (zero? (.waitFor proc)))))
