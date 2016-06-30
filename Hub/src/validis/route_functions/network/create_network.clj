(ns validis.route-functions.network.create-network
  (:require [validis.queries.network    :as query]
            [buddy.hashers              :as hashers]
            [ring.util.http-response    :as respond])
  (:import org.bson.types.ObjectId))


(defn create-network-response
  "Create a new network with the provided information `name`, `location`. Owner ID is assigned based on the authentication of the ID provided."
  [name location id]
  (let [new-network (query/create-new-network {:name name
                                                :location location
                                                :owner-id (ObjectId. id)
                                                })]
    (respond/created {:name (str (:name new-network))
                      :id (str (:id new-network))
                      })))
