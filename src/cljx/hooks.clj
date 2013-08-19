(ns cljx.hooks
  (:require leiningen.cljx
            [robert.hooke :as hooke]
            [leiningen.core.project :as project]
            [leiningen.compile :as lcompile]
            [leiningen.test :as ltest]))

(def ^:private hooked (promise))

(defn- hook [task & args]
  ; leiningen.jar/jar unmerges all default profiles before calling through to
  ; e.g. leiningen.compile/compile.  No idea why, but cljx needs to find its
  ; dependency vector, which may be in a profile...
  (when (not (realized? hooked))
    (deliver hooked true)
    (leiningen.cljx/cljx (project/merge-profiles (first args) [:default])))
  (apply task args))

(defn activate []
  (hooke/add-hook #'lcompile/compile #'hook)
  ;;Also hook test to workaround https://github.com/technomancy/leiningen/issues/1300
  ;;File generation will happen only once.
  (hooke/add-hook #'ltest/test #'hook))
