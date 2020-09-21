(ns nubank-authorizer.core
  (:gen-class)
  (:require [clojure.data.json :as json]
            [nubank-authorizer.processors.transaction :as transaction-processor]
            [nubank-authorizer.validators.transaction :as transaction-validator]
            [nubank-authorizer.managers.operation :as operation-manager]))

  (def account-data "{ \"account\": { \"activeCard\": true, \"availableLimit\": 100 } }")
  (def transaction-data "{ \"transaction\": { \"merchant\": \"Habbib's\", \"amount\": 110, \"time\": \"2019-02-13T11:00:00.000Z\" } }")

  (def user-account (atom {}))
  (def user-transactions (atom []))
  (def user-violations (atom [])) 
    
  (defn update-state
    "Updates the application state."
    [state]
    (reset! user-violations (state :violations))
    (if (state :success)
        (do
          ((reset! user-account (state :account))           
           (if (not (= (state :transaction) nil))
             (do (swap! user-transactions conj (state :transaction))))))))
        
  (defn main-workflow
    "Orchestrates the operation workflow."
    []
    (println "Insert your operations:")
    (def transaction-operation (json/read-str (read-line) :key-fn keyword))

    (->>
      (operation-manager/execute-operation transaction-operation @user-account @user-transactions)
      (update-state))

    (println (json/write-str { :account @user-account :violations @user-violations })))
      
  (defn -main
    "Triggers the authorization flow."
    []
    (while true (main-workflow)))