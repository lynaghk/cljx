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


Cljx is a Lein plugin that emits Clojure and ClojureScript code from a single
metadata-annotated codebase.

To use it, add it to your `project.clj`:

```clojure
:plugins [[com.keminglabs/cljx "0.2.2"]]
:cljx {:builds [{:source-paths ["src/cljx"]
                 :output-path ".generated/clj"
                 :rules cljx.rules/clj-rules}
                  
                {:source-paths ["src/cljx"]
                 :output-path ".generated/cljs"
                 :extension "cljs"
                 :include-meta true
                 :rules cljx.rules/cljs-rules}]}
```

Can be run "once" or "auto", in which case it will watch all source-paths for
changes to .cljx files.  Defaults to "once".

Add

```clojure
:hooks [cljx.hooks]
```

to automatically run cljx before starting a REPL, cutting a JAR, etc.

Available options include:

* `:nested-exclusions` — When true, `^:clj` and `^:cljs` metadata (used to
  indicate target-specific inclusions/exclusions) may be used on "nested"
  (non-top-level) forms (defaults `false`)
* `:maintain-form-position` – When true, the line positions of transformed cljx
  forms are maintained, which aligns error and debug info (e.g. line numbers in
  stack traces, ClojureScript source maps, etc) in the generated files with
  those in the source cljx files (defaults `false`)
* `:include-meta` — pass code-level metadata along to generated Clojure and
  ClojureScript (defaults `false`)
* `:extension` — a string indicating the target of a given "build" (defaults
  `"clj"`)
* `:rules` — a fully-qualified symbol that names a var containing the rules to
  be used

The included clj and cljs rule sets will remove forms marked with
platform-specific metadata and rename protocols as appropriate.

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

The value associated with `:rules` should be a symbol naming a var containing
the rules to use for that build.  `cljx.rules/cljs-rules` and `cljx.rules/clj-rules`
are provided as a convenience, but you can extend those (or replace them entirely).
For example, a namespace on your classpath like this defines some rules:

```clojure
(ns my.rules
  (:require [kibit.rules.util :refer (compile-rule defrules)]))

(defrules rules
  [(+ ?x 1) (inc ?x)]
  [(- ?x 1) (dec ?x)])
```

Now you can use those rules in a cljx build like so:

```clojure   
:rules my.rules/rules
```

The var's namespace will be automatically loaded by cljx (i.e. no need to do so
manually via the `:injections` key in your `project.clj`).

Forms that are converted into `:cljx.core/exclude` will be excluded from the output.
See [Kibit](http://github.com/jonase/kibit) for more info on writing rules, and
[C2](https://github.com/lynaghk/c2) for a project that uses `.cljx` heavily.


Clojure is a hosted language
----------------------------
Cljx does *not* try to hide implementation differences between host platforms.
Clojure has ints, floats, longs, &c., ClojureScript has number; Clojure regular
expressions act differently than ClojureScript regular expressions, because
*they are different*.

Cljx only tries to unify Clojure/ClojureScript abstractions when it makes sense.
E.g., converting `clojure.lang.IFn` into `IFn` when generating ClojureScript.

Also, note that *cljx has no effect on code produced by macros*.
Macroexpansion occurs long after cljx touches your code.


Misc
----
Emacs users, want syntax highlighting?
Add to your emacs config: `(add-to-list 'auto-mode-alist '("\\.cljx\\'" . clojure-mode))`.

Todo
----

+ CLJS: Remove docstrings from namespaces.
+ Explore providing an API that macros can easily use to transform their results

Thanks
======
@jonase & @ohpauleez for kibit
@swannodette for core.logic


