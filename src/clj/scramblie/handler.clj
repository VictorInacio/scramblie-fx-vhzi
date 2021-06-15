(ns scramblie.handler
  (:require
    [compojure.core :refer [GET POST DELETE defroutes]]
    [compojure.route :refer [resources]]
    [ring.util.response :refer [resource-response response]]
    [ring.middleware.json :refer [wrap-json-response wrap-json-body wrap-json-params]]
    [ring.middleware.reload :refer [wrap-reload]]
    [clojure.walk :refer [keywordize-keys]]))

(def env-type (or (System/getenv "ENV_TYPE") "DEV"))

(defn scramble? [str1 str2]
  (let [f1 (frequencies str1)
        f2 (frequencies str2)]
    (every? (fn [[char2 times2]]
              (when-let [times1 (get f1 char2)]
                (<= times2 times1))) f2)))
;; Routes
(defroutes
  routes

  (POST "/check" [str1 str2]
    (response {:result (scramble? str1 str2)}))

   ;; ------------------------------ FRONTEND ENTRY POINT - INDEX HTML -------------------------
  (GET "/" []
    (resource-response "index.html" {:root "public"}))

  ;; MIME TYPES RESOURCE
  (resources "/"))

;; Add JSON wrappers
(def routes-json (-> routes
                     (wrap-json-body {:keywords? true})
                     (wrap-json-params)
                     (wrap-json-response :pretty)))

;; figwheel entry point
(def dev-handler (-> routes-json wrap-reload))

;; main handler
(def handler routes-json)

(comment
  (routes {:uri "/app/load-version"})
  )