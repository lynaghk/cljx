(ns cljx.hooks
  (:require leiningen.cljx
            [robert.hooke :as hooke]
            [leiningen.jar :as ljar]))

(defn- jar-hook [task & args]
  (apply task args)
  (let [project (first args)]
    (leiningen.cljx/cljx project)))

(defn activate []
  (hooke/add-hook #'ljar/write-jar #'jar-hook))
