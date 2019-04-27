(ns users-api.web-test
  (:require [clojure.test :refer :all]
            [clojure.java.io :as io]
            [users-api.web :refer :all]
            [cheshire.core :refer [parse-string]])
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

(deftest init-data-test
  (testing "data is loaded"
    (do
      (reset! records [])
      (init-data)
      (is (= (map :favorite-color @records)
             ["Red" "Green" "Orange" "Orange"]))
      (reset! records []))))

;; Should really use ring-mock for these, and possibly clojure.test/use-fixtures
(deftest routes-test
  (testing "ping"
    (is (= (:body (app {:uri "/ping"
                        :request-method :get}))
           "pong")))

  (testing "get records"
    (do
      (reset! records test-rows)
      (is (= (map :last-name
              (-> (app {:uri "/records/gender" :request-method :get})
                  :body slurp (parse-string true)))
             ["aaa" "BBB" "zAz" "Brown"]))

      (is (= (map :dob
              (-> (app {:uri "/records/birthdate" :request-method :get})
                  :body slurp (parse-string true)))
             ["1985-11-10" "1985-11-11" "1988-01-18" "1992-02-02"]))

      (is (= (map :last-name
              (-> (app {:uri "/records/name" :request-method :get})
                  :body slurp (parse-string true)))
             ["zAz" "Brown" "BBB" "aaa"]))))

  (testing "add a record"
    (do
      (reset! records [])
      (is (= (:status (app {:uri "/records" :request-method :post
                            :content-type "text/space-separated"
                            :headers {"content-type" "text/space-separated"}
                            :body (-> "Hawk Tony M Red 1968-05-12" (.getBytes) io/input-stream)}))
             201))
      (is (= (-> @records first :dob)
             (LocalDate/parse "1968-05-12"))))))
