(ns cljx.repl-middleware
  (:require [cljx.core :as cljx]
            [cljx.rules :as rules]
            [cemerick.piggieback :as piggieback]
            [clojure.tools.nrepl.middleware :refer (set-descriptor!)]))

(defn wrap-cljx
  ([h] (wrap-cljx h {:clj rules/clj-rules :cljs rules/cljs-rules}))
  ([h {:keys [clj cljs] :as rules}]
    (fn [{:keys [op code file file-name session] :as msg}]
      (let [rules (if (@session #'piggieback/*cljs-repl-env*)
                    cljs
                    clj)]
        (cond
          (and (= op "eval") code)
          (h (assoc msg :code (cljx/transform code rules)))
          
          (and (= op "load-file") file (re-matches #".+\.cljx$" file-name))
          (h (assoc msg :file (cljx/transform file rules)))
          
          :else (h msg))))))

(set-descriptor! #'wrap-cljx
  {:requires #{"clone"}
   :expects #{#'piggieback/wrap-cljs-repl}
   :handles {}})
