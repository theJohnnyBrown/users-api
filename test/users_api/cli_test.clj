(ns users-api.cli-test
  (:require [clojure.test :refer :all]
            [clojure.java.io :as io]
            [users-api.cli :refer :all]
            [clojure.tools.cli :refer [parse-opts]])
  (:import [java.time LocalDate]))

(deftest options-test
  (testing "options are parsed correctly"
    (let [args ["--pipes-file" "foo.txt"
                "--pipes-file" "bar.txt"
                "--spaces-file" "baz.txt"
                "--sort" "gender"]]
      (is (= (:options (parse-opts args cli-options))
             {:pipes-file ["foo.txt" "bar.txt"]
              :spaces-file ["baz.txt"]
              :commas-file []
              :sort-order :gender}))))

  (testing "Error is present for invalid options"
    (let [args ["--pipes-file" "foo.txt"
                "--sort" "height"]]
      (is (= (:errors (parse-opts args cli-options))
             ["Failed to validate \"--sort height\": Please choose one of \"dob\", \"gender\" \"last-name\" "])))))

(deftest str-test
  (testing "Record is serialized correctly"
    (let [record {:last-name "BBB", :first-name "FirstName",
                  :gender :female, :favorite-color "Orange",
                  :dob (LocalDate/parse"1985-11-20")}]
      (is (= (user-str record)
             "BBB, FirstName, female, Orange, 11/20/1985")))))
