(ns users-api.web
  (:require [compojure.api.sweet :refer [defapi context GET POST undocumented]]
            [ring.util.http-response :refer [ok created bad-request! not-found]]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
            [compojure.route]
            [schema.core :as s]
            [users-api.data :as data]
            [clojure.string :as str]
            [clojure.java.io :as io]
            [clojure.edn :as edn]))

;; should probably use something like aero for this
(def config (edn/read-string (slurp (io/resource "config.edn"))))

(def records (atom []))

;; TODO validate input with schema/spec
(defn insert! [separator row]
  (swap! records conj (data/row->rowmap separator row)))

(def separators
  {"text/csv" data/comma-sep
   "text/pipe-separated" data/pipe-sep
   "text/space-separated" data/space-sep})

(defapi app
  (context "/" []
           (GET "/ping" []
                (ok "pong"))

           (GET "/records/:sort-order" [sort-order]
                (let [sort-fn (get data/sort-orders (keyword sort-order))]
                  (ok (sort-fn @records))))

           (POST "/records" req
                 (let [created-resp {:status 201 :headers {} :body "created"}
                       body (-> req :body slurp)]
                   (if-let [separator (get separators (:content-type req))]
                     (do body
                       (insert! separator body)
                       (created))
                     (bad-request! "Invalid content-type"))))
           (undocumented
            (compojure.route/not-found (ok {:not "found"})))))

(defn init-data []
  (let [{:keys [separator seed-file]} (:data config)
        initial-data (->> seed-file io/resource slurp str/split-lines
                          (map (partial data/row->rowmap (re-pattern separator))))]
    (reset! records (vec initial-data))))

;; launch a dev server in a repl
(comment
  (require '[ring.adapter.jetty :refer [run-jetty]])
  (require '[ring.middleware.reload :refer [wrap-reload]])
  (run-jetty (wrap-reload #'app) {:port 3000 :join? false})
  @records
  )
