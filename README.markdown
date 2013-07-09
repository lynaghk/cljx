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


Cljx is a [Leiningen](https://github.com/technomancy/leiningen) plugin that
emits Clojure and ClojureScript code from a single annotated codebase.
Effectively, it is an s-expression preprocessor:

```
             +-----------+
             |           |
             | .cljx     |
             | sources   |
             |           |
             +-----+-----+
                   |
                   |
                   |
             +-----v-----+       +----------------+
             |           |       |                |
             | cljx      <-------+  configuration |
             | Leiningen |       |       +        |
             | plugin    |       |     rules      |
             +--+--+-----+       +----------------+
                |  |
                |  |
 +------------+ |  | +------------+
 |            | |  | |            |
 |   .clj     <-+  +->   .cljs    |
 |   sources  |      |   sources  |
 |            |      |            |
 +------------+      +------------+
```

When using cljx, you put APIs and implementations that are meant to be
fundamentally portable between Clojure and ClojureScript into one annotated
`.cljx` codebase, and leave things that are necessarily tied to a single
compilation target in their "native" language (e.g. macros should always be in
Clojure sources, DOM manipulation stuffs always in ClojureScript sources, etc).

## "Installation"

To use it, add to your `project.clj`:

```clojure
:plugins [[org.clojars.cemerick/cljx "0.3.0-SNAPSHOT"]]
:cljx {:builds [{:source-paths ["src/cljx"]
                 :output-path "target/generated/clj"
                 :rules :clj}
                  
                {:source-paths ["src/cljx"]
                 :output-path "target/generated/cljs"
                 :rules :cljs}]}
```
To automatically run cljx before starting a REPL, cutting a jar, etc., add its

hook:

```clojure
:hooks [cljx.hooks]
```

## Changelog

See `CHANGES.md` at the root of this repo.

(You'll especially want to look at the entry for `0.3.0` if you've been using
previous versions of cljx, as things have changed [of course, we think
significantly for the better :-P].)

## Usage

Cljx can be run `once` or `auto`; if the latter (e.g. `lein cljx auto`), it will
watch all `source-paths` for changes to `.cljx` files.  `once` is the default.

Each build (i.e. maps in the `:builds` vector in the `:cljx` configuration) can
be configured with the following options:

* `:source-paths`, a sequence of the source roots that contain your `.cljx`
  files.  Note that putting your `.cljx` files in your "regular" Leiningen
  project's `:source-paths` (by default, `"src"`) is not recommended; doing so
  will likely lead to them being included in e.g. jar files created by
  Leiningen.  Better to keep them separate, and use cljx to direct Clojure and
  CLojureScript sources whereever they will be picked up by other tooling.
* `:output-path`, the root directory where cljx's output will land.  Common
  options are `"target/classes"` for both Clojure and ClojureScript files you
  plan on distributing as a library; or, in an application project using
  [lein-cljsbuild](https://github.com/emezeske/lein-cljsbuild) to produce
  deployable JavaScript, sending cljx-produced Clojure output to
  `"target/classes"` (so it's on the classpath and available to be added to a
  jar/war) and ClojureScript output to a dummy directory (e.g.
  `"target/generated/cljs"`) that can be a source path in your lein-cljsbuild
  configuration(s).
* `:rules` can be one of:
 * `:clj` or `:cljs` to use cljx's default Clojure or ClojureScript ruleset
   (`cljx.rules/clj-rules` and `cljx.rules/cljs-rules`, respectively)
 * a map that specifies the three slots that make up a cljx ruleset:
  * `:filetype`, a string that defines what the extension of output filenames
    will be, e.g. `"cljs"`
  * `:features`, a _set_ of strings, each naming an enabled "feature"; code in
    `.cljx` files that is annotated with a feature that is not included in this
    set will be pruned in the output
  * `:transforms`, a sequence of functions that are applied to each expression
    in each input file, and can modify that expression without constraint
 * a fully-qualified symbol that names a var containing a map as described above

In general, you'll never need to go beyond the named cljx-provided rules.

E.g., the `.cljx` source containing

```clojure
(ns example
  (#+clj :use #+cljs :use-macros [c2.macros :only (combine-with)]))

(defn x-to-string
  [x]
  (let [buf #+clj (StringBuilder.) #+cljs (gstring/StringBuffer.)]
    (.append buf "x is: ")
    (.append buf (str x))))

(reify
  clojure.lang.IFn
  (invoke [_ x] (inc x)))
```

…will, when transformed using the `:cljs` ruleset, yield:

```clojure
(ns example
  (                  :use-macros [c2.macros :only (combine-with)]))

(defn x-to-string
  [x]
  (let [buf                               (gstring/StringBuffer.)]
    (.append buf "x is: ")
    (.append buf (str x))))

(reify
  clojure.lang.IFn
  (invoke [_ x] (inc x)))
```

Notice that only the `#+cljs`-annotated expressions remain, and that everything
is still in the same position as it was in the `.cljx` file; this last
fact means that line and column numbers produced by the resulting
Clojure/ClojureScript code will remain true to the original sources.

The `#+feature-name` "annotation" syntax is shamelessly stolen from [Common
Lisp](http://www.lispworks.com/documentation/lw50/CLHS/Body/02_dhq.htm) (and is
perhaps being considered for inclusion in Clojure[Script] itself?...see [feature
expressions](http://dev.clojure.org/display/design/Feature+Expressions)).  Cljx
only supports the simplest form of the syntax; other forms can be considered
valid TODOs:

* Exclusionary annotations, e.g. `#-cljs`
* "Union" annotations, e.g. `#+(or clj clr)`

<!-- TODO wait if/when C2 moves to new annotation approach
[C2](https://github.com/lynaghk/c2) for a project that uses `.cljx` heavily.
-->


### Clojure is a hosted language, in all flavours

Cljx does *not* try to hide implementation differences between host platforms.
Clojure has ints, floats, longs, &c., ClojureScript has number; Clojure regular
expressions act differently than ClojureScript regular expressions, because
*they are different*.

Cljx only tries to unify Clojure/ClojureScript abstractions when it makes sense.
E.g., converting `clojure.lang.IFn` into `IFn` when generating ClojureScript.
The rest is up to you, in annotating your code to include or exclude what's
needed by each runtime.

Also, note that *cljx has no effect on code produced by macros*.
Macroexpansion occurs long after cljx touches your code.


### REPL Integration

Cljx provides an nREPL middleware that allows you to work with `.cljx` files in
the same way you work with regular `.clj` files from any toolchain with good
nREPL support, like [nrepl.el](https://github.com/kingtim/nrepl.el),
[Counterclockwise](http://code.google.com/p/counterclockwise/), etc.

In your project, _in addition_ to adding cljx as a plugin, just add its
middleware in your `:dev` profile (along with
[Piggieback](https://github.com/cemerick/piggieback)'s, assuming you're going to
be interacting with ClojureScript REPLs as well):

```clojure
:profiles {:dev {:dependencies [[org.clojars.cemerick/cljx "0.3.0-SNAPSHOT"]]
                 :repl-options {:nrepl-middleware [cemerick.piggieback/wrap-cljs-repl
                                                   cljx.repl-middleware/wrap-cljx]}}}
```

Now all REPL evaluations and `load-file` operations will be processed by cljx
appropriately before they reach the Clojure or ClojureScript compiler.  Whether
cljx code is processed for Clojure or ClojureScript is determined by the
existence [or not] of a Piggieback ClojureScript environment in your current
nREPL session's environment; this is entirely automatic.

Currently, only cljx's default rulesets are used in this case (though you can
work around this by making your own higher-order cljx nREPL middleware that uses
whatever rulesets you want).

### Misc

Emacs users, want syntax highlighting?
Add to your emacs config: `(add-to-list 'auto-mode-alist '("\\.cljx\\'" . clojure-mode))`.

## Todo

+ CLJS: Remove docstrings from namespaces.

## Thanks

* @jonase and @ohpauleez for enabling cljx in the first place
* @cgrand and @trptcolin for [sjacket](https://github.com/cgrand/sjacket)
* @swannodette for [core.match](https://github.com/clojure/core.match)


