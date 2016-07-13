;; src/db_handler.clj
(ns validis.db-handler
  (:require [monger.core       :as mg]
            [monger.collection :as mc]
            [environ.core      :refer [env]]))

;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Connection variables ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;

;; Connects to (default) local MongoDB instance.
;; The choice of MongoDB is based on the dynamic nature of requests and access
;; control of the database items.
;; Port is 12027.
;; We load up the server URL from the environment variable set in profiles.clj.
;; TODO: Add authentication while connecting to server on remote instance. This will be needed since all the user data needs to be secured.
(def conn
  "Specifies the connection between the application and the MongoDB backend. Currently, we are connecting to localhost but the variable can be changed in the `profiles.clj` file. We need to also add authentication to remote server since we will be handling public data and such a database has to be behind a secure storage."
  (mg/connect {:host (env :host)}))

;; Get a database connection to the "validis" database
;; The db variable can then be used from the db-handler namespace in other files.
(def db
  "Defines a database variable to be used while querying the database. We get a connection to the `validis` database in particular. This is then passed to update/insert/delete queries for modification to the document store."
  (mg/get-db conn "validis"))
