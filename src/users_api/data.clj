(ns users-api.data
  (:require [clojure.string :as str]
            [clojure.java.io :as io])
  (:import [java.time LocalDate]))


(defn lazy-line-seq
  "Returns a lazy seq of lines from the given reader. Closes the reader
  once it has been consumed"
  [rdr]
  (lazy-seq (if-let [next (.readLine rdr)]
              (cons next (lazy-line-seq rdr))
              (.close rdr))))

(def user-keys [:last-name :first-name :gender :favorite-color :dob])

(defn row->rowmap
  "converts a string into a map of keywords and parsed values"
  [separator row]
  (let [keys user-keys
        vals (str/split row separator)]
    (-> (zipmap keys vals)
        (update :gender #(condp contains? (str/lower-case %)
                           #{"f" "female" "woman"} :female
                           #{"m" "male" "man"}     :male
                           :other))
        (update :dob #(LocalDate/parse (str/trim %))))))

(defn path->rows [separator filename]
  (->> filename io/reader lazy-line-seq
       (map (partial row->rowmap separator))))

(defn data-seq
  "`files` should be a seq of vectors that look like [separator, path]"
  [files]
  (mapcat #(apply path->rows %) files))

(def sort-orders {:gender (partial sort-by (juxt :gender (comp str/lower-case :last-name)))
                  :last-name   #(reverse (sort-by (comp str/lower-case str/trim :last-name) %))
                  :dob    (partial sort-by :dob)})
