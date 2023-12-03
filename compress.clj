(ns compress 
  (:require [clojure.java.io :as io])
  (:require [clojure.string :as str]))

(def fileObject (io/file "."))

(def fileNames (.list fileObject))

(defn PrintDirectoryContents
  []
  (println "File List:")
  (doseq [item fileNames]
    (println "*./" item)))

(defn ReadFileContents
  [fileName]
  (if (.exists (io/file fileName))
    (println (slurp fileName))
    (println "Oops: specified file does not exist")))

(def freqFileContent (slurp "frequency.txt"))

(def words (str/split freqFileContent #"\s"))

(defn FindFrequencyCount
  [word]
  (let [firstMatchFrequency (first (for [i (range (count words))
                                         :when (== (compare (nth words i) word) 0)]
                                     i))]
    (if (not= nil firstMatchFrequency)
      (str firstMatchFrequency)
      word)))

(defn TreatPunctuation
  [wordToCompress]
   (if (not= nil (re-matches #"^[\(\[\{.,!?@$][a-zA-Z]+[\)\]\}.,!?@$]$" wordToCompress))
     (str
      (subs wordToCompress 0 1) 
      " " 
      (FindFrequencyCount (subs wordToCompress 1 (dec (count wordToCompress)))) 
      " " 
      (subs wordToCompress (dec (count wordToCompress))))
     (if (not= nil (re-matches #"^[\(\[\{.,!?@$][a-zA-Z]+$" wordToCompress))
        (str 
         (subs wordToCompress 0 1)
         " "
         (FindFrequencyCount (subs wordToCompress 1)))
        (if (not= nil (re-matches #"^[a-zA-Z]+[\)\]\}.,!?@$]{1,2}$" wordToCompress))
          (if (not= nil (re-matches #"^[a-zA-Z]+[\)\]\},.!/@$]{2}$" wordToCompress))
            (str
             (FindFrequencyCount (subs wordToCompress 0 (dec (dec (count wordToCompress)))))
             " "
             (subs wordToCompress (dec (dec (count wordToCompress))) (dec (count wordToCompress)))
             " "
             (subs wordToCompress (dec (count wordToCompress))))
            (str
             (FindFrequencyCount (subs wordToCompress 0 (dec (count wordToCompress))))
             " "
             (subs wordToCompress (dec (count wordToCompress)))))
          (FindFrequencyCount wordToCompress)))))

(defn CompressFileContent
  [fileName]
  (if (.exists (io/file fileName))
    (do 
      (let [fileContent (slurp fileName)
            fileWords (str/split fileContent #"[\s\r\n]")
            compressedWords (map (fn [word]
                                   (let [firstMatchFrequency (first (for [i (range (count words))
                                                                          :when (== (compare (nth words i) (str/lower-case word)) 0)]
                                                                      i))]
                                     (if (not= nil firstMatchFrequency)
                                       (str firstMatchFrequency) 
                                       (if (not= nil (re-matches #"[0-9]+" word))
                                         (str "@" word "@")
                                         (TreatPunctuation (str/lower-case word))))))
                                 fileWords)]
        (spit (str fileName ".ct") (str/join " " compressedWords)))
      (println "Outputted the compressed contents to the file!"))
    (println "Oops: specified file does not exist")))

(defn FormatUncompressedString
  [uncompressedString]
  (let [appendSpaceFormattedString (str/replace uncompressedString #"([\(\[\{@])(\s)" "$1")
        prependSpaceFormattedString (str/replace appendSpaceFormattedString #"(\s)([\)\]\}.,!?$])" "$2")
        sentenceStartFormattedString (str/replace prependSpaceFormattedString #"(\.{1}\s{1})([a-z]{1})" #(str (% 1) (str/upper-case (% 2))))]
    (str/replace sentenceStartFormattedString #"(^[a-z]{1})" #(str/upper-case (%1 1)))))

(defn DecompressFileContent
  [fileName]
  (if (.exists (io/file fileName))
    (let [compressedContent (slurp fileName)
          compressedWords (str/split compressedContent #"\s")
          originalWords (map (fn [compressedWord]
                               (let [returnedWord (if (not= nil (re-matches #"^@[0-9]+@$" compressedWord))
                                                     (str/replace compressedWord #"@" "")
                                                     (if (not= nil (re-matches #"^[0-9][0-9]*$" compressedWord))
                                                       (get words (int (Integer/parseInt compressedWord)))
                                                       compressedWord))]
                                 returnedWord))
                             compressedWords)]
      (println (FormatUncompressedString (str (str/join " " originalWords)))))
    (println "Oops: specified file does not exist")))
