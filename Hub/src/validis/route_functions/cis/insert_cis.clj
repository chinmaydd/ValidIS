;; src/route-functions/cis/insert_cis.clj
(ns validis.route-functions.cis.insert-cis
  (:require [validis.queries.cis     :as query]
            [ring.util.http-response :as respond]))

(defn insert-new-cis
  "Inserts a CIS with `name`, `address`, `api-url` and `inserter-id`"
  [inserter-id name address api-url]
  (let [new-cis (query/insert-cis {:inserter-id inserter-id
                                   :name name
                                   :address address
                                   :api-url api-url})]
  (if (not-empty new-cis)
    (respond/created {:id (str (:_id new-cis)) :name name})
    (respond/bad-request {:error "CIS could not be added"}))))

;; Assumed that we can have the same name
(defn insert-cis-response
  "Generates a response on insertion of a new CIS into the database."
  [request name address api-url]
  (let [address-query   (query/get-cis-by-field {:address address})
        url-query       (query/get-cis-by-field {:api-url api-url})
        address-exists? (not-empty address-query)
        url-exists?     (not-empty url-query)
        inserter-id     (get-in request [:identity :id])]
    (cond
      address-exists? (respond/conflict {:error "CIS with the same address already exists in the database. Add it to your own network using it's id"})
      url-exists?     (respond/conflict {:error "CIS with the same api-url already exists in the database. Use a different url for your own CIS"})
      :else           (insert-new-cis inserter-id name address api-url))))