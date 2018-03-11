A simple cli tool to create a bibtex entry for RFCs using the rfc-editor.org
services. It follows recomendations from
https://tools.ietf.org/html/draft-carpenter-rfc-citation-recs-01#section-5.2

# Usage

First install clojure (https://clojure.org/guides/getting_started)

Then run the following command in the project directory:

```
clj -m rfc-cite <rfc-id>
```

This will print the bibtex entry to stdout. If something goes wrong, 
you will be shown an exception as there was no effort put into 
error handling.

