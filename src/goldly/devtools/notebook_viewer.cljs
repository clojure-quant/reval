;; NOTEBOOK UI

(def show-viewer-debug-ui false) ; true for debugging

(defn segment-debug [segment]
  [:div.bg-gray-500
   [:p.font-bold "segment debug ui"]
   (pr-str segment)])

(defn segment [{:keys [src out hiccup] :as segment}]
  (let [scode (:code segment)]
    [:div
     (when scode
       [code scode])
     (when (not (str/blank? out))
       [text2 out])
     (when hiccup
       [:div.mt-1.mb-1
        (->hiccup hiccup)])
     (when show-viewer-debug-ui
       [segment-debug segment])]))

(defn notebook-debug [nb]
  [:div.bg-gray-500.mt-5
   [:p.font-bold "notebook debug ui"]
   (pr-str nb)])

(defn notebook [{:keys [meta content] :as nb}]
  (let [{:keys [ns created]} meta]
    [:div.bg-indigo-100.p-2
     [:h1.text-xl.text-blue-800.text-xl.pb-2 ns]
     [:p.pb-2 "evaluated: " created]
     [:hr]
     (into [:div]
           (map segment content))
     (when show-viewer-debug-ui
       [notebook-debug nb])]))

(pinkie/register-tag :p/notebook notebook)

;; COLLECTION UI

(defn nb-item [ns]
  [:p
   [link-dispatch [:bidi/goto :viewer :query-params {:ns ns}] ns]])


(defn nb-list [[name list]]
  (into
   [:div
    [:p.bg-red-300 name]
    (when show-viewer-debug-ui
      [:p (pr-str list)])]
   (map nb-item list)))

(defn notebook-collection [d]
  (into
   [:div.flex.flex-col.items-stretch.h-full.bg-gray-50.w-full]
   (map #(nb-list %) d)))


(pinkie/register-tag :p/notebookcollection notebook-collection)

;; APP

(def nb-welcome
  {:meta {:ns "goldly.welcome"}
   :content
   [{:code "(println \"Welcome to Goldly Notebook Viewer \")"
     :hiccup [:h1.text-blue-800 "Welcome to Notebook Viewer!"]
     :out "Welcome to Goldly Notebook Viewer"}]})


(defn viewer-debug [query-params]
  [:div.bg-gray-500.pt-10.hoover-bg-blue-300
   [:p.font-bold "viewer debug ui"]
   [:p "query params:"]
   [:p (pr-str query-params)]
   [:p "ns: " (:ns query-params)]])

(defn viewer [query-params]
  (fn [query-params]
    [site/main-with-header
     [devtools-menu] 30
     [site/sidebar-layout
      [url-loader {:fmt :clj
                   :url :nb/collections}
       notebook-collection []]
      [:div
       (if-let [ns (:ns query-params)]
         [url-loader {:fmt :edn
                      :url  (rdoc-link ns "notebook.edn") ;  (str "/api/rdocument/file/" ns "/notebook.edn")
                      }
          notebook []]
         [notebook nb-welcome])
       (when show-viewer-debug-ui
         [viewer-debug query-params])]]]))

(defn viewer-page [{:keys [route-params query-params handler] :as route}]
  [:div.bg-green-300.w-screen.h-screen
   [viewer query-params]])

(add-page viewer-page :viewer)