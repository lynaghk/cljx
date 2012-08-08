                /$$                
               | $$                
      /$$$$$$$ | $$    /$$   /$$   /$$
     /$$_____/ | $$   |__/  |  $$ /$$/
    | $$       | $$    /$$   \  $$$$/ 
    | $$       | $$   | $$    >$$  $$ 
    |  $$$$$$$ | $$   | $$   /$$/\  $$
     \_______/ |__/   | $$  |__/  \__/
                 /$$  | $$          
                |  $$$$$$/  Your code is, like, data, bro.        
                 \______/           


Cljx is a Lein plugin that emits Clojure and ClojureScript code from a single metadata-annotated codebase.

To use it, add it to your `project.clj`:

```clojure
:plugins [com.keminglabs/cljx "0.1.0"]
:cljx {:builds [{:source-paths ["src/cljx"]
                 :output-path ".generated/clj"
                 :rules cljx.rules/clj-rules}
                  
                {:source-paths ["src/cljx"]
                 :output-path ".generated/cljs"
                 :extension "cljs"
                 :include-meta true
                 :rules cljx.rules/cljs-rules}]}
```

Can be run "once" or "auto", in which case it will watch all source-paths for changes to .cljx files.  Defaults to "once".

Add

```clojure
:hooks [cljx.hooks]
```

to automatically run cljx before cutting a JAR.

The included clj and cljs rule sets will remove forms marked with platform-specific metadata and rename protocols as appropriate.
E.g., the `.cljx` source containing

```clojure
^:clj (ns c2.maths
        (:use [c2.macros :only [combine-with]]))
^:cljs (ns c2.maths
         (:use-macros [c2.macros :only [combine-with]]))

(defn ^:clj sin [x] (Math/sin x))
(defn ^:cljs sin [x] (.sin js/Math x))

(reify
  clojure.lang.IFn
  (invoke [_ x] (inc x)))
```

will, when run through `cljx.rules/cljs-rules`, yield:

```clojure
(ns c2.maths
  (:use-macros [c2.macros :only [combine-with]]))

(defn sin [x] (.sin js/Math x))

(reify
  IFn
  (invoke [_ x] (inc x)))
```

The value associated with `:rules` is `eval`'d in the plugin namespace.
You can specify your own rules inline, or load from a file:

```clojure   
:rules (load-file "my-rules.clj")
```

with the file containing, for instance:

```clojure
(use '[kibit.rules.util :only [compile-rule]])

[(compile-rule '[(+ ?x 1) (inc ?x)])]
```

Forms that are converted into `:cljx.core/exclude` will be excluded from the output.
See [Kibit](http://github.com/jonase/kibit) for more info on writing rules, and [C2](https://github.com/lynaghk/c2) for a project that uses `.cljx` heavily.


Clojure is a hosted language
----------------------------
Cljx does *not* try to hide implementation differences between host platforms.
Clojure has ints, floats, longs, &c., ClojureScript has number; Clojure regular expressions act differently than ClojureScript regular expressions, because *they are different*.

Cljx only tries to unify Clojure/ClojureScript abstractions when it makes sense.
E.g., converting `clojure.lang.IFn` into `IFn` when generating ClojureScript.


Misc
----
Emacs users, want syntax highlighting?
Add to your emacs config: `(add-to-list 'auto-mode-alist '("\\.cljx\\'" . clojure-mode))`.

Todo
----

+ CLJS: Remove docstrings from namespaces.

Thanks
======
@jonase & @ohpauleez for kibit
@swannodette for core.logic


