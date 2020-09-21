(ns nubank-authorizer.processors.account
  (:gen-class))

  (defn register
    "Registers an account."
    [validated-account]
    (if
     (validated-account :success)

      (do { :success true :violations (validated-account :violations) :account (validated-account :account)})
      
      (do { :success false :violations (validated-account :violations) :account (validated-account :account)})))