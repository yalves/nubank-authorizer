(ns nubank-authorizer.processors.transaction
  (:gen-class))

  (defn transact
    "Effetuates the transaction operation."
    [validated-transaction]
  
    (if
     (validated-transaction :success)

      (do (def transaction-amount ((validated-transaction :transaction) :amount))
       (def current-amount ((validated-transaction :account) :availableLimit))
       (def final-amount (- current-amount transaction-amount))
       (def user-account (atom (validated-transaction :account)))
       (swap! user-account update-in [:availableLimit] (constantly final-amount))
       { :success true :violations (validated-transaction :violations) :transaction (validated-transaction :transaction) :account @user-account})
      
      (do { :success false :violations (validated-transaction :violations) :transaction (validated-transaction :transaction) :account (validated-transaction :account)})))