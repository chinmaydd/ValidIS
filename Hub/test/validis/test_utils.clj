(ns validis.test-utils
  "Utility functions required for testing."
  (:require [cheshire.core :as ch]
            [ring.mock.request :as mock]
            [validis.handler :refer [app]]
            [buddy.core.codecs :as codecs]
            [validis.queries.cis :as c-query]
            [validis.queries.user :as u-query]
            [validis.queries.network :as n-query]
            [buddy.core.codecs.base64 :as b64]))

(def str->base64
  "Converts a string to base64"
  (comp codecs/bytes->str b64/encode))

(defn parse-body
  "Parse a body as string?"
  [body]
  (ch/parse-string (slurp body) true))

(defn basic-auth-header
  "Generates an auth-header for retrieving a user token"
  [request original]
  (mock/header request "Authorization" (str "Basic " (str->base64 original))))

(defn token-auth-header
  "Generates an auth-header for request authorization"
  [request token]
  (mock/header request "Authorization" (str "Token " token)))

(defn get-user-token
  "Get a token for a user"
  [username-and-password]
  (let [initial-response (app (-> (mock/request :get "/api/auth")
                                  (basic-auth-header username-and-password)))
        initial-body     (parse-body (:body initial-response))]
    (:token initial-body)))

(defn get-token-auth-header-for-user
  "Get a auth-header for a token"
  [request username-and-password]
  (token-auth-header request (get-user-token username-and-password)))

(defn add-users
  "Add test users to the database."
  []
  (let [user-1 {:email "user1@test.com" :username "user1" :password "password"}
        user-2 {:email "user2@test.com" :username "user2" :password "pass12345"}]
    (app (-> (mock/request :post "/api/user" (ch/generate-string user-1))
             (mock/content-type "application/json")))

    (app (-> (mock/request :post "/api/user" (ch/generate-string user-2))
             (mock/content-type "application/json")))))

(defn add-networks
  "Adds test networks to the database."
  []
  (let [network-1 {:name "test-network-1" :location "BC"}
        network-2 {:name "test-network-2" :location "Montreal"}]
    (app (-> (mock/request :post "/api/network" (ch/generate-string network-1))
             (mock/content-type "application/json")
             (get-token-auth-header-for-user "user1:password")))
    (app (-> (mock/request :post "/api/network" (ch/generate-string network-2))
             (mock/content-type "application/json")
             (get-token-auth-header-for-user "user2:pass12345")))))

(defn add-cis
  "Adds test cis to the database for testing"
  []
  (let [test-cis-1 {:name "Clinic1" :address "Vic" :api_url "https://google.com"}
        test-cis-2 {:name "Clinic2" :address "BC" :api_url "https://github.com"}]
        (app (-> (mock/request :post "/api/cis" (ch/generate-string test-cis-1))
                 (mock/content-type "application/json")))
        (app (-> (mock/request :post "/api/cis" (ch/generate-string test-cis-2))
                 (mock/content-type "application/json")))))

(defn add-cis-to-network
  "Adds test CIS to network for testing"
  []
  (let [test-cis        (c-query/get-cis-by-field {:name "Clinic1"})
        test-cis-id     (str (:_id test-cis))
        test-network    (n-query/get-network-by-name {:name "test-network-1"})
        test-network-id (str (:_id test-network))]
    (app (-> (mock/request :post (str "/api/network/" test-network-id "/cis/" test-cis-id))
             (mock/content-type "application/json")
             (get-token-auth-header-for-user "user1:password")))))

(defn add-user-to-network
  "Adds a test user to network"
  []
  (let [user-2        (u-query/get-user-by-field {:username "user2"})
        user-2-id     (str (:_id user-2))
        network-1     (n-query/get-network-by-name {:name "test-network-1"})
        network-1-id  (str (:_id network-1))]
    (app (-> (mock/request :post (str "/api/network/" network-1-id "/user/" user-2-id))
             (mock/content-type "application/json")
             (get-token-auth-header-for-user "user1:password")))))
