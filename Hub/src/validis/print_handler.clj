(ns validis.print-handler)

(defn print-handler
    "Handy middleware for (pretty) printing incoming requests"
    [handler]
    (fn [request]
      ;; Uses the clojure pretty print library for printing out ALL incoming
      ;; requests. This middleware can be used for specific routes for printing
      ;; details information about the requests and logging them.
      (clojure.pprint/pprint request)))