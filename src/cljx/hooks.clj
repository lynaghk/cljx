(ns cljx.hooks
  (:require leiningen.cljx
            [robert.hooke :as hooke]
            [leiningen.compile :as lcompile]))

(defn- hook [task & args]
  (leiningen.cljx/cljx (first args))
  (apply task args))

(defn activate []
  (hooke/add-hook #'lcompile/compile #'hook))
