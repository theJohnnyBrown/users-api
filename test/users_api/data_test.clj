(ns users-api.data-test
  (:require [clojure.test :refer :all]
            [clojure.java.io :as io]
            [users-api.data :refer :all])
  (:import [java.time LocalDate]))

(def test-rows
  [{:last-name "aaa", :first-name "FirstName", :gender :female, :favorite-color "Red",
    :dob (LocalDate/parse "1992-02-02")}
   {:last-name "Brown", :first-name "Jonathan", :gender :male, :favorite-color "Green",
    :dob (LocalDate/parse "1988-01-18")}
   {:last-name "zAz", :first-name "FirstName", :gender :female, :favorite-color "Orange",
    :dob (LocalDate/parse"1985-11-10")}
   {:last-name "BBB", :first-name "FirstName", :gender :female, :favorite-color "Orange",
    :dob (LocalDate/parse"1985-11-11")}])

(deftest rowmap-test
  (let [test-data (data-seq [[#" *\| *" (io/resource "fixtures/pipe-separated.txt")]])]
   (testing "Rows are created correctly"
     (is (= test-data test-rows)))

   (testing "Rows are sorted correctly"
     (let [name-sort (:name sort-orders)
           dob-sort (:birthdate sort-orders)
           gender-sort (:gender sort-orders)]
      (is (= (map :last-name (gender-sort test-data))
             ["aaa" "BBB" "zAz" "Brown"]))
      (is (= (map :last-name (dob-sort test-data))
             ["zAz" "BBB" "Brown" "aaa"]))
      (is (= (map :last-name (name-sort test-data))
             ["zAz" "Brown" "BBB" "aaa"]))))))
