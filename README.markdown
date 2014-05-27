                /$$
               | $$
      /$$$$$$$ | $$    /$$   /$$   /$$
     /$$_____/ | $$   |__/  |  $$ /$$/
    | $$       | $$    /$$   \  $$$$/
    | $$       | $$   | $$    >$$  $$
    |  $$$$$$$ | $$   | $$   /$$/\  $$
     \_______/ |__/   | $$  |__/  \__/
                 /$$  | $$
                |  $$$$$$/
                 \______/


cljx is a [Leiningen](https://github.com/technomancy/leiningen) plugin and nREPL
middleware that produces Clojure and ClojureScript code from a single annotated
codebase.  Effectively, it is an s-expression preprocessor that yields either
Clojure and ClojureScript sources on disk (e.g. for inclusion in jars or for
input to compilation tools like Clojure AOT-compilation and lein-cljsbuild):

```
             +---------+
             | .cljx   |
             | sources |
             +-----+---+
                   |
                   |
                   |
             +-----v-----+       +----------------+
             | cljx      <-------+  configuration |
             | Leiningen |       |       +        |
             | plugin    |       |     rules      |
             +--+--+-----+       +----------------+
                |  |
                |  |
 +------------+ |  | +------------+
 |   .clj     <-+  +->   .cljs    |
 |   sources  |      |   sources  |
 +------------+      +------------+
```

…or, when used in a REPL, cljx automatically applies the same transformation to
any namespaces to be loaded (e.g. as a result of `:require` declarations) from
`.cljx` files before they are consumed by the Clojure or ClojureScript compiler:

```
                          +------------+       +---------------+
     +---------+          | cljx       |       | configuration |
     | .cljx   |          | nREPL      |       |      +        |
     | sources +----------> middleware <-------+    rules      |
     +---------+          +-----+------+       +---------------+
                                |
                                |
                                |
                        +-------v---------+
                        | `require`       |
                        | `:require`      |
      +---------+       | `load-namespace`|        +---------+
      | .cljs   +-------> ...etc...       <--------+ .cljs   |
      | sources |       +------+--+-------+        | sources |
      +---------+              |  |                +---------+
                               |  |
                +------------+ |  | +---------------+
                |  Clojure   <-+  +-> ClojureScript |
                |  compiler  |      | compiler      |
                +------------+      +---------------+
```

When using cljx, you put APIs and implementations that are meant to be
fundamentally portable between Clojure and ClojureScript into one annotated
`.cljx` codebase, and leave things that are necessarily tied to a single
compilation target in their "native" language (e.g. macros should always be in
Clojure sources, DOM manipulation stuffs always in ClojureScript sources, etc).

Does this seem crazy?  Crazy awesome, maybe.

### Projects that use cljx

Here's some real-world examples
of projects that use cljx (when you feel in trouble, refer to these for usage
and configuration examples):

* [schema](https://github.com/Prismatic/schema)
* [formative](https://github.com/jkk/formative)
* [pprng](https://github.com/cemerick/pprng/)
* [sablano](https://github.com/r0man/sablono)
* [double-check](https://github.com/cemerick/double-check)
* [garden](https://github.com/noprompt/garden)
* [hickory](https://github.com/davidsantiago/hickory)
* [validateur](https://github.com/michaelklishin/validateur)
* [frak](https://github.com/noprompt/frak)
* [cellular](https://github.com/nodename/cellular)
* [enoki](https://github.com/harto/enoki)
* [inflections-clj](https://github.com/r0man/inflections-clj)
* [pathetic](https://github.com/davidsantiago/pathetic)

## "Installation"

To use it, add to your `project.clj`:

```clojure
:plugins [[com.keminglabs/cljx "0.4.0"]]
:cljx {:builds [{:source-paths ["src/cljx"]
                 :output-path "target/classes"
                 :rules :clj}

                {:source-paths ["src/cljx"]
                 :output-path "target/classes"
                 :rules :cljs}]}
```

To automatically run cljx before starting a REPL, cutting a jar, etc., add its hook:

```clojure
:hooks [cljx.hooks]
```

A more comprehensive configuration example can be found
[here](https://github.com/lynaghk/cljx/blob/master/sample.project.clj).

## Changelog

See `CHANGES.md` at the root of this repo.

(You'll especially want to look at the entry for `0.3.0` if you've been using
older versions of cljx, as things have changed [of course, we think
significantly for the better :-P].)

## Usage

There are two ways in which the cljx transformation can be made: via a Leiningen
task (necessary when you need the transformation result on disk for
e.g. packaging into a jar for distribution), and/or via an nREPL middleware that
makes using the Leiningen task unnecessary in REPL sessions.

cljx's Leiningen task can be run `once` or `auto`; if the latter (e.g. `lein
cljx auto`), it will watch all `source-paths` for changes to `.cljx` files.
`once` is the default.

Each build (i.e. maps in the `:builds` vector in the `:cljx` configuration) can
be configured with the following options:

* `:source-paths`, a sequence of the source roots that contain your `.cljx`
  files.  Note that putting your `.cljx` files in your "regular" Leiningen
  project's `:source-paths` (by default, `"src"`) is not recommended; doing so
  will likely lead to them being included in e.g. jar files created by
  Leiningen.  Better to keep them separate, and use cljx to direct Clojure and
  ClojureScript sources whereever they will be picked up by other tooling.
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
  #+clj clojure.lang.IFn
  #+cljs cljs.core.IFn
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

         cljs.core.IFn
  (invoke [_ x] (inc x)))
```

Notice that only the `#+cljs`-annotated expressions remain, and that everything
is still in the same position as it was in the `.cljx` file; this last
fact means that line and column numbers produced by the resulting
Clojure/ClojureScript code (e.g. in error messages, stack traces/frames,
debuggers, source maps, etc) will remain true to the original sources.

The `#+feature-name` "annotation" syntax is shamelessly stolen from [Common
Lisp](http://www.lispworks.com/documentation/lw50/CLHS/Body/02_dhq.htm) (and is
perhaps being considered for inclusion in Clojure[Script] itself?...see [feature
expressions](http://dev.clojure.org/display/design/Feature+Expressions)).  cljx
only supports the simplest form of the syntax; other forms can be considered
valid TODOs:

* Exclusionary annotations, e.g. `#-cljs`
* "Union" annotations, e.g. `#+(or clj clr)`

### Clojure is a hosted language, in all flavours

cljx does *not* try to hide implementation differences between host platforms.
Clojure has ints, floats, longs, &c., ClojureScript has number; Clojure regular
expressions act differently than ClojureScript regular expressions, because
*they are different*, and so on.

cljx only tries to unify Clojure/ClojureScript abstractions when it makes sense.
E.g., converting `clojure.lang.IFn` into `IFn` when generating ClojureScript.
The rest is up to you, in annotating your code to include or exclude what's
needed by each runtime.

Also, note that *cljx has no effect on code produced by macros*.
Macroexpansion occurs long after cljx touches your code.


### REPL Integration

cljx provides an nREPL middleware that allows you to work with `.cljx` files in
the same way you work with regular `.clj` files from any toolchain with good
nREPL support, like [cider](https://github.com/clojure-emacs/cider),
[Counterclockwise](http://code.google.com/p/counterclockwise/), etc.

When you add cljx as a `:plugin` to your Leiningen project:

1. The cljx and [Piggieback](https://github.com/cemerick/piggieback) nREPL
  middlewares will automatically be added to your `:repl-options`
2. cljx itself will be added as a project dependency (this will only affect REPL
  processes, and won't leak out into your project's `pom.xml`, influencing
  downstream users of your library, if you're writing one)

(Note that this does not conflict with using the
[Austin](http://github.com/cemerick/austin) plugin to automate the configuration
of your project to use Piggieback.  In fact, the pairing is highly recommended
for making the ClojureScript REPL side of your cljx project easy-peasy.)

With cljx installed as a plugin, all nREPL evaluations and `load-file`
operations will be processed by cljx appropriately before they reach the Clojure
or ClojureScript compiler.  Whether cljx code is processed for Clojure or
ClojureScript is determined by the existence [or not] of a Piggieback
ClojureScript environment in your current nREPL session's environment; this is
entirely automatic.

Currently, only cljx's default rulesets are used in this case (though you can
work around this by making your own higher-order cljx nREPL middleware that uses
whatever rulesets you want).

### Misc

#### Syntax highlighting

Get the same syntax highlighting of `.cljx` files as you currently do for `.clj` files!

##### Emacs

`(add-to-list 'auto-mode-alist '("\\.cljx\\'" . clojure-mode))`

##### Vim

`autocmd BufNewFile,BufReadPost *.cljx setfiletype clojure`

##### Eclipse + CounterClockwise

1. In Preferences, go to General > Editors > File Associations.
2. Add a `*.cljx` file type in the upper list.
3. Add an editor association for that `*.cljx` file type to Counterclockwise's `Clojure Editor`.

## Thanks

* @jonase and @ohpauleez for enabling the first [kibit](https://github.com/jonase/kibit)-based cljx
* @cemerick for design chats, maintaining and extending cljx, and rewriting the core to use sjacket
* @cgrand and @trptcolin for [sjacket](https://github.com/cgrand/sjacket)
* @swannodette for [core.match](https://github.com/clojure/core.match)
