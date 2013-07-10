(ns cljx.hooks
  (:require leiningen.cljx
            [robert.hooke :as hooke]
            [leiningen.core.project :as project]
            [leiningen.compile :as lcompile]))

(defn- hook [task & args]
  ; leiningen.jar/jar unmerges all default profiles before calling through to
  ; e.g. leiningen.compile/compile.  No idea why, but cljx needs to find its
  ; dependency vector, which may be in a profile...
  (leiningen.cljx/cljx (project/merge-profiles (first args) [:default]))
  (apply task args))

(defn activate []
  (hooke/add-hook #'lcompile/compile #'hook))
