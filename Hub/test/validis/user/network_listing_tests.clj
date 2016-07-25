;; test/validis/user/network_listing_tests.clj
(ns validis.user.network-listing-tests
  (:require [clojure.test :refer :all]
            [validis.handler :refer :all]
            [validis.test-utils :as helper]
            [validis.queries.user :as u-query]
            [validis.queries.network :as n-query]
            [ring.mock.request :as mock]
            [cheshire.core :as ch]))

;;;;;;;;;;;;;;;;;;;;;;;
;; Teardown function ;;
;;;;;;;;;;;;;;;;;;;;;;;

(defn setup-teardown
  [f]
  (do
      (helper/add-users)
      (helper/add-networks)
      (f)
      (n-query/empty-network-database)
      (u-query/empty-user-database)))

(use-fixtures :each setup-teardown)

(deftest can-list-networks-for-user
  (testing "Can list networks"
    (let [user-1           (u-query/get-user-by-field {:username "user1"})
          user-id          (str (:_id user-1))
          response         (app (-> (mock/request :get "/api/user/networks")
                                    (helper/get-token-auth-header-for-user "user1:password")))
          body             (helper/parse-body (:body response))
          networks-db-list (n-query/list-networks-for-user {:owner-id user-id})
          updated-list     (map #(update-in % [:_id] str) networks-db-list)
          networks-list    (:list body)]
      (is (= 200            (:status response)))
      (is (= updated-list   networks-list)))))
