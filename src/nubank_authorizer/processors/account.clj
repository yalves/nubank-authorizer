(ns nubank-authorizer.processors.account
  (:gen-class))

  (defn update-account
    "Updates the user account status"
    [account current-account]

    (def violations (atom []))
    (def success (atom true))

    (if
      (not (= current-account {}))
      (do (swap! violations conj "account-already-initialized")
          (reset! success false)
          { :success @success :violations @violations :account current-account})
      (do { :success @success :violations @violations :account account})))