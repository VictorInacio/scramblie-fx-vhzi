(ns scramblie.subs
  (:require
    [re-frame.core :as re-frame]
    [clojure.string :as string]
    [day8.re-frame.tracing :refer-macros [fn-traced defn-traced]]))


(defn db-subscription
  "Define a subscription handler that just gets a top level value from the db."
  ([db-kw] (db-subscription db-kw db-kw))
  ([sub-kw db-kw]
   (re-frame/reg-sub
     sub-kw
     (fn [db] (get db db-kw)))))

(db-subscription :scramble-result)



;;;:FORMS VALIDATION SUBS
(re-frame/reg-sub
  :valid-required?
  (fn [db [_ value]]
    (if (string/blank? value)
      [false "Campo obrigatório."]
      [true])))

(defn get-validation-subs
  "Build a list of validation subs based on the :required and :validate values
  of each field spec."
  [form fields]
  (reduce
    (fn [acc {:keys [key required validate]}]
      (cond-> acc
              required (conj [:valid-required? (key form)])
              validate (conj [validate (key form) form])))
    [] fields))

(re-frame/reg-sub
  :valid-form?
  (fn [[_ form fields]]
    (->> fields
         (get-validation-subs form)
         (map re-frame/subscribe)
         (doall)))

  (fn [validations _]
    (every? true? (map first validations))))
