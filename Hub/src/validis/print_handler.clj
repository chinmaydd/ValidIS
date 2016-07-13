(ns validis.print-handler
  "Pretty printing middleware function.")

(defn print-handler
  "Handy middleware for (pretty) printing incoming requests. Used the clojure library `pprint` for all incoming requests."
  [handler]
  (fn [request]
      ;; Uses the clojure pretty print library for printing out ALL incoming
      ;; requests. This middleware can be used for specific routes for printing
      ;; details information about the requests and logging them.
    (clojure.pprint/pprint (:params request))))
