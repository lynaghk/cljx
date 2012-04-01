(ns cljx.hooks
  (:require leiningen.cljx
            [robert.hooke :as hooke]
            [leiningen.jar :as ljar]))

(defn- jar-hook [task project]
  (leiningen.cljx/cljx project)
  (task project))

(defn activate []
  (hooke/add-hook #'ljar/jar #'jar-hook))
