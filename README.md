# fpipes
Functional pipe operators inspired by the F# and OCaml `|>` pipe operator for Clojure.

Also similar to Elixir's and R's pipe operator `|>`.

## Installation

### Leiningen

Add the following dependency to your `project.clj`:

```clojure
[com.xadecimal/fpipes "1.0.0"]
```

### Clojure CLI/deps.edn

Add the following dependency to your `deps.edn`:

```clojure
{:deps {com.xadecimal/fpipes {:mvn/version "1.0.0"}}}
```

## Usage

You can use `|>` to pipe into the last argument, `|<` to pipe into the first and `|_` to pipe arbitrarily everywhere you put a `_`.

They work like `clojure.core/partial`, as they are inspired by OCaml's and F#'s pipe operator.

You want to use them inside the thread-first `->` macro as follows:

```clojure
(-> [1 2 3]
    (|> map (fn [x] (+ x 1)))
    (|> reduce (fn [x y] (+ x y)) 0))
;; => 9
```

```clojure
(-> [1 2 3]
    (|> map (fn [x] (+ x 1))) ;; Pipe into last
    (|> reduce (fn [x y] (+ x y)) 0)
    (|< str " is the result") ;; Pipe into first
    (|_ str "Here " _ " too") ;; Pipe into the _
    (|> str))
;; => "Here 9 is the result too"
```

For |_ you can pick multiple places to pipe into:

```clojure
(-> [1 2 3]
    (|> map (fn [x] (+ x 1)))
    (|> reduce (fn [x y] (+ x y)) 0)
    (|_ str _ " is the result. I said, " _ " is the result.")) ;; Pipe into all the _
;; => "9 is the result. I said, 9 is the result."
```

## Comparison with others

**Standard Clojure Threading**
```clojure
(-> "a,b,c"
    (str/split #",")
    (->> (mapv str/upper-case)
         (str/join ";"))
    println)
```

**FPipes** <-- This library
```clojure
(-> "a,b,c"
    (|< str/split #",")
    (|> mapv str/upper-case)
    (|> str/join ";")
    (|> println))
```

**OCaml**
```ocaml
"a,b,c"
|> String.split_on_char ','
|> List.map String.uppercase_ascii
|> String.concat ";"
|> print_string
```

**Elixir**
```elixir
"a,b,c"
|> String.split(",")
|> Enum.map(&String.upcase/1)
|> Enum.join(";")
|> IO.puts
```

## Require

I suggest you refer to all three pipes, and you also need to refer to `_` in order to use `|_`. In theory, it'll work without when inline, but when used as a function you'll have to have required `_` for it to work.

```
(ns your-ns
  (:require [com.xadecimal.fpipes :refer [|> |< |_ _]]))
```

## How it works?

`|>`, `|<`, and `|_` are functions of the form: `(fn [x f & more] (f more... x))`. As you can see, they take a data/coll `x` as their first argument and a function as the second argument with possibly more argument, and will call that function with `x` as the last argument, or the first or where `_` shows up depending which one you use.

Finally, an `:inline` form is given to all of them, which does the same as a macro so that there is no additional overhead to using them.

I can't think why you'd use them outside of `->`, so they'd probably always get inlined, but they are functions so you can pass them as arguments to other functions.
