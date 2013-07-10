# changelog

## 0.3.0

Mostly a rewrite to use [sjacket](https://github.com/cgrand/sjacket), removing
all dependency on the Clojure reader and thereby eliminating all of the issues
that go along with it (e.g. lossy representation, line number changes when
emitting Clojure[Script], [issues like this](), etc).

The syntax for annotations has changed, as described in the readme.  tl;dr, all
you need to do is `s/\^:(cljs?)/#+$1/g`.  The only gotcha is that putting
annotations on var symbols no longer supported (which carried a bunch of
problems before anyway, but nevermind that); so, you have to change e.g. this:

```clojure
(defn ^:cljs foo [x] (whatever x))
```

into:

```clojure
#+cljs (defn foo [x] (whatever x))
```

Again, tl;dr: cljx annotations apply to _expressions_, and nothing else.

Otherwise, everything works as it did; even your existing `project.clj`
configurations can stay as they are (though anything other than the three
`:build` map keys described in the README are now superfluous).
