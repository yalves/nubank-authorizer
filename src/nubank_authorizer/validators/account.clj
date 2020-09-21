(ns nubank-authorizer.validators.account
  (:gen-class))

  (defn validate
    "Validates an account operation"
    [user-account]

    (def violations (atom []))
    (def success (atom true))

    (if
      (not (= user-account {}))
      (do (swap! violations conj "account-already-initialized")
          (reset! success false)))

    { :success @success :violations @violations :account user-account})