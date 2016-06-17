(ns validis.server
  (:require [org.httpkit.server :as httpkit]
            [validis.handler :refer [app]]))

; (defn create-tables
;   "Create database tables if they don't exist"
;   []
;   (query/create-registered-user-table-if-not-exists! query/db)
;   (query/create-permission-table-if-not-exists! query/db)
;   (query/create-basic-permission-if-not-exists! query/db)
;   (query/create-user-permission-table-if-not-exists! query/db)
;   (query/create-password-reset-key-table-if-not-exists! query/db))

(defn -main [port]
  ; (create-tables)
  (httpkit/run-server app {:port (Integer/parseInt port) :join false})
  (println "Server started on port:" port))
