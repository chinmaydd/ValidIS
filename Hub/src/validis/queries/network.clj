(ns validis.queries.network
  (:require [monger.collection  :as mc]
            [validis.db-handler :refer [db]]
            [monger.conversion  :refer [from-db-object]]
            [monger.operators   :refer :all])
  (:import org.bson.types.ObjectId))

(defn create-new-network!
  "Creates a new empty network (document) in the database"
  [network_data]
  (mc/insert-and-return db "networks" network_data))
