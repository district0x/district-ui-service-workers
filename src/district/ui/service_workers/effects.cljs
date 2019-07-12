(ns district.ui.service-workers.effects
  (:require
    [re-frame.core :refer [reg-fx dispatch]]))

(defn service-worker-supported? []
  (boolean (aget js/navigator "serviceWorker")))

(reg-fx
  ::watch-service-worker-ready
  (fn [{:keys [:on-ready :on-error]}]
    (when (service-worker-supported?)
      (-> js/navigator.serviceWorker.ready
        (.then #(dispatch on-ready %))
        (.catch #(dispatch on-error %))))))


(reg-fx
  ::watch-controller-change
  (fn [{:keys [:on-controller-change]}]
    (when (service-worker-supported?)
      (js/navigator.serviceWorker.addEventListener
        "controllerchange"
        #(dispatch (conj on-controller-change %))))))


(reg-fx
  ::register
  (fn [{:keys [:script-url :opts :on-success :on-error]}]
    (if (service-worker-supported?)
      (cond-> (js-invoke js/navigator.serviceWorker "register" script-url (clj->js opts))
        on-success (.then #(dispatch (conj on-success %)))
        on-error (.catch #(dispatch (conj on-error %))))
      (.warn js/console (str "Cannot register service worker " script-url " because it's not supported by your browser")))))


(reg-fx
  ::unregister
  (fn [registration]
    (when registration
     (js-invoke registration "unregister"))))


(reg-fx
  ::post-message
  (fn [[data & [ports]]]
    (when (service-worker-supported?)
      (js-invoke js/navigator.serviceWorker.controller "postMessage" (clj->js data) (clj->js ports)))))