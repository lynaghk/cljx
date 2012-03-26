(ns termite.core
  (:refer-clojure :exclude [==])
  (:use [clojure.core.logic]))

(defn- meta-guard [key]
  #(do
     (println % ":\t" (meta %))
     (-> % meta key (= true))))

(def cljs-rule
  [#(matcha [%]
            ([[_ var . _]]
               (pred var (meta-guard :clj)))
            ([x]
               (pred x (meta-guard :clj))))
   #(== % :exclude)])

(defn simplify-one [expr rules]
  (let [alt (run* [q]
              (fresh [pat subst]
                (membero [pat subst] rules)
                (project [pat subst]
                  (all (pat expr)
                       (subst q)))))]
    (if (empty? alt) expr (first alt))))



(set! *print-meta* true)

(doall (map #(simplify-one % [interop-rule inc-rule cljs-rule])
            ['(defn ^:clj x [] 123)
             '(defn ^:cljs stays [] 123)
             (quote ^:clj a-var)
             (quote ^:clj (a-form))
             '(stays)
             ]))

;;Metadata is getting dropped somewhere in the matcha:

;; x                   :	 {:clj true}
;; stays               :	 {:cljs true}
;; (defn stays [] 123) :	 nil
;; a-var               :	 {:clj true}
;; (a-form)            :	 nil
;; (stays)             :	 nil
;;=>  (:exclude ^{:line 1} (defn ^{:cljs true} stays [] 123) :exclude ^{:clj true, :line 1} (a-form) ^{:line 1} (stays))
