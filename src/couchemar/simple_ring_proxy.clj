(ns couchemar.simple-ring-proxy
  (:require clojure.string
            clj-http.client
            [ring.middleware.cookies :refer [wrap-cookies]]))

(defn- prepare-request
  [uri request]
  {:method (:request-method request)
   :url (str uri "?" (:query-string request))
   :headers (dissoc (:headers request) "host" "content-length")
   :body (:body request)
   :throw-exceptions false
   :as :stream})

(def ^:private valid-cookie-attrs
  [:domain :max-age :path :secure
   :expires :http-only :same-site
   :value])

(defn- prepare-cookie
  [cookie]
  (let [valid-attrs (-> cookie
                        (get-in [1])
                        (select-keys valid-cookie-attrs)
                        (update-in [:expires] str))]
    (assoc-in cookie [1] valid-attrs)))

(defn- process-response
  [response]
  (let [cookies (:cookies response)]
    (assoc response :cookies (into {} (map prepare-cookie cookies)))))

(defn wrap-proxy
  [handler ^String req-path-re proxy-replace-re & [http-opts]]
  (wrap-cookies
   (fn [request]

     (if (re-matches req-path-re (:uri request))
       (-> (clojure.string/replace (:uri request) req-path-re proxy-replace-re)
           (prepare-request request)
           (merge http-opts)
           clj-http.client/request
           process-response)
       (handler request)))))
