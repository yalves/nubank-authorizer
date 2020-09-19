(ns nubank-authorizer.validators.transaction
  (:gen-class))

(defn validate
  [transaction account]
  (def transaction-amount (transaction :amount))
  (def current-amount ((account :account) :availableLimit))
  (def violations (atom []))
  (def success (atom true))

  (if (> transaction-amount current-amount)
    (do (swap! violations conj "insufficient-limit")
        (reset! success false)))

  (if (= ((account :account) :activeCard) false)
    (do (swap! violations conj "card-not-active")
        (reset! success false)))
  
  { :success @success :violations @violations })