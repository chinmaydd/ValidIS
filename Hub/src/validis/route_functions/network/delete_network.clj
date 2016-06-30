(ns validis.route-functions.network.delete-network
  (:require [validis.queries.network :as query]
            [ring.util.http-response :as respond])
  (:import org.bson.types.ObjectId))

(defn delete-network
  "Delete a network by its id"
  [network-id]
  (let [deleted-network (query/delete-network {:id network-id})]
    (if (not= 0 deleted-network)
      (respond/ok {:message (format "Network with id %s removed successfully!" network-id)})
      ;; Consider the other cases as well.
      (respond/not-found {:error "Something went wrong."}))))

(defn delete-network-response
  "Deletes a network with the id provided"
  [request owner-id network-id]
  (let [deleting-own-network? (and
                                ;; Authorization creds and token verification 
                                (= owner-id (get-in request [:identity :id]))
                                (= 0 (query/check-if-owned-network? {:id network-id 
                                                                     :owner-id owner-id})))]
    (if deleting-own-network?
      (delete-network network-id)
      (respond/unauthorized {:error "Not authorized"}))))
