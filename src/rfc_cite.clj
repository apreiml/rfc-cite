(ns rfc-cite
  (:require [clojure.xml :as xml]
            [clojure.zip :as zip]
            [clojure.data.zip.xml :as zip-xml]
            [clj-http.client :as client]))

(def default-entry { 
    "howpublished" "Internet Requests for Comments",
    "type" "RFC",
    "publisher" "RFC Editor",
    "institution" "RFC Editor"
})

(defn bibxml-url
  [rfc]
  (str "https://www.rfc-editor.org/refs/bibxml/reference.RFC." rfc ".xml"))

(defn rfc-url
  [rfc]
  (str "https://www.rfc-editor.org/rfc/rfc" rfc ".txt"))

(defn entry-urls
  [rfc]
  (let [url (rfc-url rfc)]
    {"number" rfc, 
     "url" url}))

(defn get-issn
  [rfc]
  (let [text (:body (client/get (rfc-url rfc)))]
    {"issn" (nth (re-find #"ISSN: (\S+)" text) 1)}))

(defn parse-xml!
  [rfc]
  (let [url (bibxml-url rfc)]
    (xml/parse url)))

(defn row-str
  [row]
  (let [[k v] row]
    (str k " = {" v "}")))

(defn entry-str
  [rfc, entry]
  (let [entry (conj entry (entry-urls rfc) (get-issn rfc))]
    (str "@techreport{rfc" rfc ",\n\t"
      (clojure.string/join ",\n\t" (map row-str (seq entry)))
      "\n}")))

(defn get-zip
  [xml]
  (zip/xml-zip xml))

; clearly I really don't know how to work with zippers properly yet. :)
(defn extract-values
  [zipper]
  (let [ptr (zip-xml/xml-> zipper zip/down zip/down)]
      { "title" (first (zip-xml/xml-> zipper :reference :front :title zip-xml/text))
        "author" (clojure.string/join " and " (zip-xml/xml-> zipper :reference :front :author (zip-xml/attr :fullname)))
        "year" (first (zip-xml/xml-> zipper :reference :front :date (zip-xml/attr :year)))
        "month" (first (zip-xml/xml-> zipper :reference :front :date (zip-xml/attr :month)))
        }))

(defn -main
  [rfc & args]
  (->> rfc
       (parse-xml!)
       (get-zip)
       (extract-values)
       (into default-entry)
       (entry-str rfc)
       (println)))
