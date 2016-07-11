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
(def conn (mg/connect {:host (env :host)}))

;; Get a database connection to the "validis" database
;; The db variable can then be used from the db-handler namespace in other files.
(def db (mg/get-db conn "validis"))
