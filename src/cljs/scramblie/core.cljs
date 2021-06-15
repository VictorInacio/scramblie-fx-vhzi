(ns scramblie.core
  (:require
    [reagent.core :as reagent]
    [re-frame.core :as re-frame]
    [scramblie.events :as events]
    [scramblie.subs :as subs]
    [scramblie.config :as config]
    [scramblie.http]
    [scramblie.forms :refer [form-view]]
    [day8.re-frame.http-fx]))

(defn dev-setup
  []
  (when config/debug?
    (enable-console-print!)
    (println "dev mode")))

(defn header
  []
  [:nav.navbar.is-fixed-top.has-text-light.has-background-dark.has-text-weight-bold
   {:role "navigation" :aria-label "main navigation"}
   [:div.navbar-brand
    [:a.navbar-item.has-text-white
     [:span "Scramblie Demo"]]]] )
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;Main View;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn main-view
  "Build the ui based on the current-view in the app-db."
  []
  (let [result @(re-frame/subscribe [:scramble-result])]
    [:section.section {:style {:padding-top 10}}
   [header]
   [:div.columns
    [:div.column.is-half.is-offset-one-quarter
     [form-view {:submit-text  "Check Scramblie"
                 :submit-class "is-fullwidth"
                 :on-submit    [:api/check]
                 :fields       [{:key      :str1
                                 :type :default
                                 :label    "First word"
                                 :required true}
                                {:key      :str2
                                 :type :default
                                 :label    "Second word"
                                 :required true}]}]
     (when (not (nil? result)) [:h1 (str "Check result: " result)])]]]))

(defn mount-root
  []
  (re-frame/clear-subscription-cache!)
  (reagent/render [main-view]
                  (.getElementById js/document "app")))

(defn ^:export init []
  (dev-setup)
  (mount-root))
