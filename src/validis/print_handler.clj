(ns validis.print-handler)

(defn print-handler
    "Handy middleware for (pretty) printing incoming requests"
    [handler]
    (fn [request]
      (clojure.pprint/pprint request)))