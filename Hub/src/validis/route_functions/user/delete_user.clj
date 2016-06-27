(ns validis.route-functions.user.delete-user
  (:require [validis.queries.user    :as query]
            [ring.util.http-response :as respond]))

(defn delete-user
  "Delete a user by ID"
  [id]
  (let [deleted-user (query/delete-registered-user! {:id id})]
    (if (not= 0 deleted-user)
      (respond/ok        {:message (format "User id %s successfully removed" id)})
      (respond/not-found {:error "Userid does not exist"}))))

(defn delete-user-response
  "Deletes a user with the id provided"
  [request id]
  (let [auth  (get-in request [:identity])
        deleting-self? (= (str id) (get-in request [:identity :id]))]
    (if (or (.contains auth "admin") deleting-self?)
      (delete-user id)
      (respond/unauthorized {:error "Not authorized"}))))