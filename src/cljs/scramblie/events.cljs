(ns scramblie.events
  (:require
    [re-frame.core :as re-frame]
    [scramblie.http]
    ))

;; Scramblie Check
(re-frame/reg-event-fx
  :api/check
  (fn [_ [_ form]]
    (let [str1 (:str1 form)
          str2 (:str2 form)]
      {:http {:method     :post
              :uri        "/check"
              :params     {:str1 str1
                           :str2 str2}
              :on-success [:api/check-success]
              :on-failure [:failure]}})))

(re-frame/reg-event-fx
  :api/check-success
  [re-frame/trim-v]
  (fn [{:keys [db]} [{:keys [result] :as payload}]]
    {:db (assoc db :scramble-result result)}))