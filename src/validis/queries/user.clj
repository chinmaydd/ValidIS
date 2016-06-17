(ns validis.queries.user
  (:require [monger.collection :as mc]))

(defn insert-registered-user!
  "Inserts a registered user data(document) into the database"
  [db user_data]
  (mc/insert-and-return db "users" user_data))