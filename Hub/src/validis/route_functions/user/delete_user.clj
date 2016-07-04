;; src/route-functions/user/delete-user.clj
(ns validis.route-functions.user.delete-user
  (:require [validis.queries.user    :as query]
            [ring.util.http-response :as respond]))

(defn delete-user
  "Delete a user by Id"
  [id]
  (let [deleted-user (query/delete-user {:id id})]
    (if (not= 0 deleted-user)
      (respond/ok        {:message (format "User id %s successfully removed" id)})
      (respond/not-found {:error "Userid does not exist"}))))

(defn delete-user-response
  "Generates response for user deletion."
  [request id]
  (let [deleting-self? (= (str id) (get-in request [:identity :id]))]
    (if deleting-self?
      (delete-user id)
      (respond/unauthorized {:error "Not authorized"}))))
