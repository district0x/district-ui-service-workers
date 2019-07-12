(ns district.ui.service-workers.events
  (:require
    [district.ui.service-workers.effects :as effects]
    [district.ui.service-workers.queries :as queries]
    [district0x.re-frame.spec-interceptors :refer [validate-first-arg]]
    [re-frame.core :refer [reg-event-fx trim-v]]))

(def interceptors [trim-v])

(reg-event-fx
  ::start
  interceptors
  (fn [{:keys [:db]} [{:keys [:workers]}]]
    {::effects/watch-service-worker-ready {:on-ready [::service-worker-ready]
                                           :on-error [::service-worker-ready-error]}
     ::effects/watch-controller-change {:on-controller-change [::controller-changed]}
     :db (queries/assoc-workers db workers)
     :dispatch-n (for [worker workers]
                   [::register worker])}))


(reg-event-fx
  ::register
  [interceptors (validate-first-arg :district.ui.service-workers/worker)]
  (fn [{:keys [:db]} [{:keys [:script-url :scope :on-success :on-error] :as worker}]]
    {::effects/register {:script-url script-url
                         :opts {:scope (or scope "/")}
                         :on-success [::worker-registered worker]
                         :on-error [::worker-register-error worker]}
     :db (queries/assoc-worker db worker)}))


(reg-event-fx
  ::unregister
  interceptors
  (fn [{:keys [:db]} [{:keys [:scope]}]]
    {::effects/unregister (queries/worker-registration db scope)}))


(reg-event-fx
  ::worker-registered
  interceptors
  (fn [{:keys [:db]} [worker registration]]
    (merge
     {:db (queries/assoc-worker-registration db worker registration)}
     (when (:on-success worker)
       {:dispatch (conj (:on-success worker) worker registration)}))))


(reg-event-fx
  ::worker-register-error
  interceptors
  (fn [{:keys [:db]} [worker]]
    (.error js/console "Service Worker couldn't be registered:" (:script-url worker))
    (when (:on-error worker)
      {:dispatch (conj (:on-error worker) worker)})))


(reg-event-fx
  ::post-message
  interceptors
  (fn [{:keys [:db]} [data ports]]
    {::effects/post-message [data ports]}))


(reg-event-fx
  ::service-worker-ready
  (constantly nil))


(reg-event-fx
  ::service-worker-ready-error
  interceptors
  (fn [_ [error]]
    (.error js/console "Service worker ready error" error)))


(reg-event-fx
  ::controller-changed
  (constantly nil))

(reg-event-fx
  ::stop
  interceptors
  (fn [{:keys [:db]}]
    {:db (queries/dissoc-service-workers db)}))