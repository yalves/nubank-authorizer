(ns nubank-authorizer.validators.transaction
  (:gen-class))

  (defn validate
    "Validates a transaction against  an account"
    [transaction account]
    (def transaction-amount (transaction :amount))
    (def current-amount (account :availableLimit))
    (def violations (atom []))
    (def success (atom true))
    
    (if (> transaction-amount current-amount)
      (do (swap! violations conj "insufficient-limit")
          (reset! success false)))

    (if (= (account  :activeCard) false)
      (do (swap! violations conj "card-not-active")
          (reset! success false)))

    { :success @success :violations @violations :transaction transaction :account account })