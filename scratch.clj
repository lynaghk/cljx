(ns cljx
  (:require [clojure.java.io :as io])
  (:import [clojure.lang LineNumberingPushbackReader]))


(set! *print-meta* true)
(set! *print-meta* false)



;;Some code taken from Kibit.

(defn read-ns
  "Generate a lazy sequence of top level forms from a
  LineNumberingPushbackReader"
  [^LineNumberingPushbackReader r]
  (lazy-seq
   (let [form (read r false ::eof)]
     (when-not (= form ::eof)
       (cons form (read-ns r))))))



(defn toplevel-forms-in [filename]
  (let [reader (io/reader (java.io.File. filename))]
    (read-ns (LineNumberingPushbackReader. reader))))




(let [forms (toplevel-forms-in "test-file.cljx")]
  {:cljs (filter #(or (= (-> % meta :cljs) true)
                      (not= (-> % meta :clj) true))
                 forms)

   :clj (filter #(or (= (-> % meta :clj) true)
                     (not= (-> % meta :cljs) true))
                forms)})







;; `tree-seq` returns a lazy-seq of nodes for a tree.
;; Given an expression, we can then match rules against its pieces.
;; This is like using `clojure.walk` with `identity`:
;;
;;     user=> (expr-seq '(if (pred? x) (inc x) x))
;;     ((if (pred? x) (inc x) x)
;;      if
;;      (pred? x)
;;      pred?
;;      x
;;      (inc x)
;;      inc
;;      x
;;      x)`
;;
(defn expr-seq
  "Given an expression (any piece of Clojure data), return a lazy (depth-first)
  sequence of the expr and all its sub-expressions"
  [expr]
  (tree-seq sequential?
            seq
            expr))
