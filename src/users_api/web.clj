(ns users-api.web
  (:require [compojure.api.sweet :refer [defapi GET POST]]
            [ring.util.http-response :refer [ok]]))

(defapi app
  (GET "/ping" []
       (ok "pong")))

(comment
  (require '[ring.adapter.jetty :refer [run-jetty]])
  (require '[ring.middleware.reload :refer [wrap-reload]])
  (run-jetty (wrap-reload #'app) {:port 3000 :join? false})

  )
