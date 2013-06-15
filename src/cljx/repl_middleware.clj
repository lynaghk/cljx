(ns cljx.repl-middleware
  (:require [cljx.core :as cljx]
            [cljx.rules :as rules]
            [cemerick.piggieback :as piggieback]
            [clojure.string :as str]
            [clojure.tools.nrepl.middleware :refer (set-descriptor!)]
            [kibit.rules.util :refer (defrules compile-rule)]
            [clojure.core.logic :as l])
  (:use clojure.test))

(defn- any-root-cause?
  "Returns true if any cause within [exception] is an instance of [class]."
  [class ^Throwable exception]
  (loop [exception exception]
    (when exception
      (if (instance? class exception)
        true
        (recur (.getCause exception))))))

(defn- munge-code
  [code rules]
  (binding [*print-meta* true]
    (try
      #_(.println System/out (with-out-str
        (reduce #'cljx/write-on-correct-lines 1
          (cljx/munge-forms (java.io.StringReader. code)
            {:rules rules :nested-exclusions true}))))
      (with-out-str
        (reduce #'cljx/write-on-correct-lines 1
          (cljx/munge-forms (java.io.StringReader. code)
            {:rules rules :nested-exclusions true})))
      (catch Throwable e
        ;; choked reading the form; go ahead and let interruptible-eval read the
        ;; original code, so that the exception is reported properly
        (if (any-root-cause? clojure.lang.LispReader$ReaderException e)
          code
          (throw e))))))

(defn wrap-cljx
  ([h] (wrap-cljx h {:clj rules/clj-rules :cljs rules/cljs-rules}))
  ([h {:keys [clj cljs] :as rules}]
    (fn [{:keys [op code file file-name session] :as msg}]
      (let [rules (if (@session #'piggieback/*cljs-repl-env*)
                    cljs
                    clj)]
        (cond
          (and (= op "eval") code)
          (h (assoc msg :code (munge-code code rules)))
          
          (and (= op "load-file") file (re-matches #".+\.cljx$" file-name))
          (h (assoc msg :file (munge-code file rules)))
          
          :else (h msg))))))

(set-descriptor! #'wrap-cljx
  {:requires #{"clone"}
   :expects #{#'piggieback/wrap-cljs-repl}
   :handles {}})
