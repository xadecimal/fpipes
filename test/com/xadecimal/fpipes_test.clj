(ns com.xadecimal.fpipes-test
  (:require
   [clojure.test :refer [deftest testing is]]
   [clojure.string :as str]
   [com.xadecimal.fpipes :refer [_ |< |> |_]]))

(deftest fpipes
  (testing "You want to use |> inside of thread-first"
    (is (= (-> [1 2 3]
               (|> map (fn [x] (+ x 1)))
               (|> reduce (fn [x y] (+ x y)) 0))
           9)))
  (testing "They work like partial does."
    (is (= (-> [1 2 3]
               (|> map (fn [x] (+ x 1)))
               (|> reduce (fn [x y] (+ x y)) 0))
           9)))
  (testing "If a function takes the data/coll first, you can use |< instead"
    (is (= (-> "a,b,c"
               (|< str/split #","))
           ["a" "b" "c"])))
  (testing "If a function calls for taking the data/coll in the middle, you
can use |_ and then position a _ inside where you want the data/coll piped."
    (is (= (-> "a,b,c"
               (|_ str "The string " _ " is here."))
           "The string a,b,c is here.")))
  (testing "You can pipe it into multiple places at once."
    (is (= (-> "a,b,c"
               (|_ str "The string " _ " is here and " _ " here too, and at the end as well " _))
           "The string a,b,c is here and a,b,c here too, and at the end as well a,b,c")))
  (testing "And you can mix and match them to create a complete threading pipeline."
    (is (= (-> "a,b,c"
               (|< str/split #",")
               (|> mapv str/upper-case)
               (|> str/join ";")
               (|_ str _ " The string is [" _ "," _ "," _ "] " _))
           "A;B;C The string is [A;B;C,A;B;C,A;B;C] A;B;C")))
  (testing "Another pipeline example"
    (is (= (-> [1 2 3]
               (|> map (fn [x] (+ x 1)))
               (|> reduce (fn [x y] (+ x y)) 0)
               (|< str " is the result")
               (|_ str "Here " _ " too")
               (|> str))
           "Here 9 is the result too")))
  (testing "A more realistic example pipeline"
    (is (= (let [users
                 [{:id 1 :name "Alice" :age 30 :city "New York"}
                  {:id 2 :name "Bob" :age 40 :city "San Francisco"}
                  {:id 3 :name "Charlie" :age 35 :city "New York"}
                  {:id 4 :name "Diana" :age 25 :city "Seattle"}
                  {:id 5 :name "Eve" :age 30 :city "San Francisco"}]]
             (-> users
                 (|> filter #(>= (:age %) 30))
                 (|> map #(assoc % :group (:city %)))
                 (|> group-by :group)
                 (|> map (fn [[group users-in-group]]
                           [group (/ (reduce + (map :age users-in-group))
                                     (count users-in-group))]))
                 (|> into {})))
           {"New York" (/ 65 2) "San Francisco" 35})))
  (testing "You can also sprinkle them inside a normal thread-first more selectively."
    (is (= (-> "a,b,c"
               (str/split #",")
               (|> mapv str/upper-case)
               (|> str/join ";")
               str)
           "A;B;C")))
  (testing "You decide if it's nicer than the good old Clojure standard threading macros..."
    (is (= (-> "a,b,c"
               (|< str/split #",")
               (|> mapv str/upper-case)
               (|> str/join ";")
               (|> str))
           (-> "a,b,c"
               (str/split #",")
               (->> (mapv str/upper-case)
                    (str/join ";"))
               str)))))
