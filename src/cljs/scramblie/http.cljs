(ns scramblie.http
  (:require
    [re-frame.core :as re-frame]
    [clojure.walk :as w]
    [clojure.string :as str]
    [ajax.core :as ajax]
    [day8.re-frame.http-fx :as http-fx]
    [day8.re-frame.tracing :refer-macros [fn-traced defn-traced]]))

(defn replace-underscore-dash
  [s]
  (str/replace s \_ \-))

(defn snake->kebab
  [snake]
  (if-some [n (namespace snake)]
    (keyword (replace-underscore-dash n) (replace-underscore-dash (name snake)))
    (keyword (replace-underscore-dash (name snake)))))

(defn parse-response
  [res]
  (w/prewalk (fn [x]
               (if (keyword? x)
                 (let [n (name x)]
                   (snake->kebab (if (str/starts-with? n "__")
                                   (keyword "meta" (subs n 2))
                                   (keyword n))))
                 x))
             res))

(re-frame/reg-fx :http
                 (fn
                   [{:keys [method uri params on-success on-failure format response-format]
                     :or   {format          (ajax/json-request-format)
                            response-format (ajax/json-response-format {:keywords? true})}
                     :as   request}]
                   (http-fx/http-effect
                     {:method          method
                      :uri             uri
                      :timeout         25000
                      :params          params
                      :format          format
                      :response-format response-format
                      :on-success      [::callback on-success]
                      :on-failure      on-failure})))

(re-frame/reg-event-fx ::callback
                       [re-frame/trim-v]
                       (fn [_ [cb payload]]
                         {:dispatch (conj cb (parse-response payload))}))