(ns validis.route-functions.network.modify-network
  (:require [validis.queries.network :as query]
            [ring.util.http-response :as respond]))

(defn modify-network-response
  "Update network info (`:name`/`:location`/`:id`)"
  [request name location id]
  0)
