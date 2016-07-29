;; src/validis/core-functions/user/delete-user.clj
(ns validis.core-functions.user.delete-user
  "Contains functions to delete a user account."
  (:require [validis.queries.user :as query]
            [ring.util.http-response :as respond]))

(defn delete-user
  "Delete a user by Id"
  [id]
  (let [deleted-user (query/delete-user {:id id})]
      (respond/ok        {:message (format "User id %s successfully removed" id)})))

(defn delete-user-response
  "Generates response for user deletion. We check if the user is deleting himself. Identity details are taken from the request token."
  [request id]
  (let [deleting-self? (= (str id) (get-in request [:identity :id]))]
    (if deleting-self?
      (delete-user id)
      (respond/unauthorized {:error "Not authorized"}))))
