(ns com.xadecimal.fpipes)

(def _ ::_)

(defn |>
  {:inline (fn [x f & more] `(~f ~@more ~x))}
  [x f & more]
  (apply f (concat more (list x))))

(defn |<
  {:inline (fn [x f & more] `(~f ~x ~@more))}
  [x f & more]
  (apply f x more))

(defn |_
  {:inline (fn [x f & more] `(~f ~@(map #(if (= '_ %) x %) more)))}
  [x f & more]
  (apply f (map #(if (= _ %) x %) more)))
