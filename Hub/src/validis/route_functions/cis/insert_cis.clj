;; src/route-functions/cis/insert_cis.clj
(ns validis.route-functions.cis.insert-cis
  "Contains function for insertion of a CIS into the db."
  (:require [validis.queries.cis :as query]
            [ring.util.http-response :as respond]
            [monger.util :refer [object-id]]))

(defn insert-new-cis
  "Inserts a CIS with `name`, `address` and `api-url`. Response is the CIS id and the name."
  [name address api-url]
  (let [new-cis (query/insert-cis {:name name
                                   :address address
                                   :api-url api-url})]
      (respond/ok          {:id (str (:_id new-cis)) :name name})))

;; Assumed that we can have the same name
(defn insert-cis-response
  "Generates a response on insertion of a new CIS into the database. Checks if we have the same CIS in the database. If that is the case, we then return a conflict response."
  [request name address api-url]
  (let [url-query       (query/get-cis-by-field {:api-url api-url})
        name-empty?     (empty? name)
        address-empty?  (empty? address)
        api-url-empty?  (empty? api-url)
        url-exists?     (not-empty url-query)]
    (cond
      address-empty?  (respond/bad-request {:error "Address is empty!"})
      api-url-empty?  (respond/bad-request {:error "API URL is empty!"})
      name-empty?     (respond/bad-request {:error "Name is empty!"})
      url-exists?     (respond/conflict    {:error "CIS with the same api-url already exists in the database. Use a different url for your own CIS"})
      :else           (insert-new-cis name address api-url))))
