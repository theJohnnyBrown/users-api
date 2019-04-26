(ns users-api.cli
  (:require [clojure.tools.cli :refer [parse-opts]]
            [clojure.string :as str]
            [users-api.data :as data])
  (:import [java.time.format DateTimeFormatter]))

(def cli-options
  [["-c" "--commas-file PATH" "Path to a CSV data file"
    :default []
    :assoc-fn (fn [m k v]
                (update-in m [k] conj v))
    :id :commas-file]

   ["-s" "--spaces-file PATH" "Path to a space-separated data file"
    :default []
    :assoc-fn (fn [m k v]
                (update-in m [k] conj v))
    :id :spaces-file]

   ["-p" "--pipes-file PATH" "Path to a pipe-separated data file"
    :default []
    :assoc-fn (fn [m k v]
                (update-in m [k] conj v))
    :id :pipes-file]

   ["-z" "--sort ORDER" "Key by which to sort the results"
    :parse-fn keyword
    :default :last-name
    :validate [#{:dob :gender :last-name}
               "Please choose one of \"dob\", \"gender\" \"last-name\" "]
    :id :sort-order]])

(defn user-str [user]
  (let [u (-> user
              (update :dob #(.format % (DateTimeFormatter/ofPattern "M/d/uuuu")))
              (update :gender name))]
   (str/join ", " (for [k data/user-keys] (get u k)))))

(defn -main [& args]
  (let [opts (parse-opts args cli-options)]
    (if-let [e (:errors opts)]
      (prn e)
      (let [rows (data/data-seq (concat
                                 (map #(vector #" *\| *" %)
                                      (get-in opts [:options :pipes-file]))
                                 (map #(vector #" +" %)
                                      (get-in opts [:options :spaces-file]))
                                 (map #(vector #" *, *" %)
                                      (get-in opts [:options :commas-file]))))

            sort-fn (get data/sort-orders (get-in opts [:options :sort-order]))]
        (doseq [r (sort-fn rows)]
          (prn (user-str r)))))))
